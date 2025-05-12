package me.earth.headlessmc.mc;

import com.google.gson.JsonElement;
import me.earth.headlessmc.mc.adventure.AdventureHelper;
import me.earth.headlessmc.mc.adventure.AdventureWrapper;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComponentHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean adventureHelperInitialized = false;
    private static AdventureHelper adventureHelper;

    public static String toAnsiStringOrContent(Component component) {
        String string = toAnsiString(component);
        return string == null ? component.getString() : string;
    }

    public static String toAnsiString(Component component) {
        if (AdventureWrapper.ENABLED) {
            String ansiString = null;
            try {
                if (!adventureHelperInitialized) {
                    adventureHelper = AdventureWrapper.getAdventureHelper(k -> Language.getInstance().has(k), (s, def) -> Language.getInstance().getOrDefault(s));
                    adventureHelperInitialized = true;
                }

                if (adventureHelper != null) {
                    JsonElement json = Component.Serializer.toJsonTree(component);
                    ansiString = adventureHelper.toAnsiString(json);
                }
            } catch (Exception e) {
                if (AdventureWrapper.OUTPUT_THROWABLES) {
                    if (e.getMessage().contains("Action not allowed: ")
                            || e.getMessage().contains("type not allowed: ")
                            || e.getMessage().contains("Can't access registry ")) {
                        ansiString = component.getString();
                    } else {
                        LOGGER.error("Failed to serialize {}", component.getString(), e);
                    }
                }
            }

            return ansiString;
        }

        return component.getString();
    }

}
