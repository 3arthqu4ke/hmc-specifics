package me.earth.headlessmc.mc.adventure;

import com.google.gson.JsonElement;

/**
 * @see AdventureHelperImpl
 */
public interface AdventureHelper {
    String toAnsiString(String json);

    String toAnsiString(JsonElement json);

    String toAnsiStringLegacy(String legacyString);

}
