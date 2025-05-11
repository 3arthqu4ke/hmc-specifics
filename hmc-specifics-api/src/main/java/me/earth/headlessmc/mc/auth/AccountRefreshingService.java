package me.earth.headlessmc.mc.auth;

import me.earth.headlessmc.api.config.Config;
import me.earth.headlessmc.auth.AccountJsonLoader;
import me.earth.headlessmc.auth.ValidatedAccount;
import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import me.earth.headlessmc.mc.Minecraft;
import me.earth.headlessmc.mc.SpecificProperties;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.util.logging.JavaConsoleLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class AccountRefreshingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRefreshingService.class);

    private final Supplier<HttpClient> httpClientFactory = MinecraftAuth::createHttpClient;

    private volatile ScheduledExecutorService executor;
    private volatile Future<?> future;
    private volatile ValidatedAccount validatedAccount;

    public boolean isRunning() {
        return executor != null;
    }

    public synchronized void setValidatedAccount(ValidatedAccount validatedAccount) {
        ValidatedAccount old = this.validatedAccount;
        if (old != null
                && old.getSession().getMcProfile().getId().equals(validatedAccount.getSession().getMcProfile().getId())) {
            this.validatedAccount = new ValidatedAccount(validatedAccount.getSession(), old.getXuid());
            saveAccounts(this.validatedAccount);
        } else {
            this.validatedAccount = validatedAccount;
        }
    }

    public synchronized boolean start(Config config, Minecraft mc) {
        if (executor == null) {
            LOGGER.info("Starting account refreshing service");
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("AccountRefreshingService");
                return thread;
            });

            long timeout = config.get(SpecificProperties.REFRESH_INTERVAL, TimeUnit.HOURS.toSeconds(23));
            future = executor.schedule(() -> refresh(mc, config), timeout, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    public synchronized boolean stop() {
        if (executor != null) {
            if (future != null) {
                future.cancel(true);
            }

            executor.shutdownNow();
            executor = null;
            future = null;
            return true;
        }

        return false;
    }

    public synchronized boolean refresh(Minecraft mc, Config config) {
        McAccount mcAccount = mc.getMcAccount();
        ValidatedAccount validatedAccount = this.validatedAccount;
        if (validatedAccount == null
            || !validatedAccount.getSession().getMcProfile().getId().equals(mcAccount.getUuid())) {
            List<ValidatedAccount> accounts = getAccounts();
            validatedAccount = accounts.stream()
                    .filter(a ->
                            a.getSession().getMcProfile().getId().equals(mcAccount.getUuid()))
                    .findFirst()
                    .orElse(null);
        }

        if (validatedAccount == null) {
            LOGGER.error("Failed to refresh account, failed to find account in HeadlessMc/auth.");
            return false;
        }

        HttpClient httpClient = httpClientFactory.get();
        StepFullJavaSession.FullJavaSession session = validatedAccount.getSession();
        StepFullJavaSession.FullJavaSession refreshed;
        try {
            refreshed = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(
                    new JavaConsoleLogger(java.util.logging.Logger.getLogger("AccountRefreshingService")),
                    httpClient,
                    session
            );
        } catch (Exception e) {
            LOGGER.error("Failed to refresh account", e);
            return false;
        }

        ValidatedAccount refreshedAccount = new ValidatedAccount(refreshed, validatedAccount.getXuid());
        McAccount refreshedMcAccount = new McAccount(
                refreshedAccount.getName(),
                refreshedAccount.getSession().getMcProfile().getId(),
                refreshedAccount.getSession().getMcProfile().getMcToken().getAccessToken()
        );

        this.validatedAccount = validatedAccount;
        mc.schedule(() -> mc.setMcAccount(refreshedMcAccount));
        LOGGER.info("Refreshed account successfully.");
        saveAccounts(refreshedAccount);
        ScheduledExecutorService executor = this.executor;
        if (executor != null) {
            long timeout = config.get(SpecificProperties.REFRESH_INTERVAL, TimeUnit.HOURS.toSeconds(23));
            future = executor.schedule(() -> refresh(mc, config), timeout, TimeUnit.SECONDS);
        }

        return true;
    }

    private void saveAccounts(ValidatedAccount account) {
        try {
            List<ValidatedAccount> accounts = getAccounts();
            if (accounts.removeIf(a ->
                    a.getSession().getMcProfile().getId().equals(account.getSession().getMcProfile().getId()))) {
                accounts.add(account);
                Path accountsJson = getPath();
                AccountJsonLoader accountJsonLoader = new AccountJsonLoader();
                accountJsonLoader.save(accountsJson, accounts);
                LOGGER.info("Saved account to HeadlessMc/auth.");
            }
        } catch (Exception e) { // getParent might be null
            LOGGER.error("Failed to save accounts json", e);
        }
    }

    private List<ValidatedAccount> getAccounts() {
        try {
            Path accountsJson = getPath();
            AccountJsonLoader accountJsonLoader = new AccountJsonLoader();
            return accountJsonLoader.load(accountsJson);
        } catch (Exception e) { // getParent might be null
            LOGGER.error("Failed to load accounts json", e);
        }

        return Collections.emptyList();
    }

    private Path getPath() throws NullPointerException {
        String javaLibraryPath = System.getProperty("java.library.path");
        if (javaLibraryPath == null) {
            throw new NullPointerException("java.library.path is null");
        }

        return Paths.get(javaLibraryPath) // extracted
                .getParent() // launch-uuid
                .getParent() // HeadlessMC
                .resolve("auth")
                .resolve(".accounts.json");
    }

}
