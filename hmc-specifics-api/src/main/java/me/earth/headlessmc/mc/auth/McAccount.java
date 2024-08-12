package me.earth.headlessmc.mc.auth;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class McAccount {
    private final String name;
    private final UUID uuid;
    private final String accessToken;
    private final String type;
    private final Optional<String> xuid;
    private final Optional<String> clientId;

    public McAccount(String name, UUID uuid, String accessToken) {
        this.name = name;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.type = "msa";
        this.xuid = Optional.empty();
        this.clientId = Optional.empty();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Optional<String> getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Optional<String> getXuid() {
        return xuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof McAccount)) return false;
        McAccount mcAccount = (McAccount) o;
        return Objects.equals(getName(), mcAccount.getName()) && Objects.equals(getUuid(), mcAccount.getUuid()) && Objects.equals(getAccessToken(), mcAccount.getAccessToken()) && Objects.equals(getType(),
                                                                                                                                                                                                  mcAccount.getType()) && Objects.equals(
            getXuid(), mcAccount.getXuid()) && Objects.equals(getClientId(), mcAccount.getClientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUuid(), getAccessToken(), getType(), getXuid(), getClientId());
    }

    /*
       On 1.21, User:

       private final String name;
       private final UUID uuid;
       private final String accessToken;
       private final Optional<String> xuid;
       private final Optional<String> clientId;
       private final User.Type type;

       On 1.7.10, Session:

       private final String username;
       private final String playerID;
       private final String token;
       private final Session$Type sessionType;
     */

}
