package me.earth.headlessmc.mc.keyboard;

import me.earth.headlessmc.api.HasId;
import me.earth.headlessmc.api.HasName;
import org.jetbrains.annotations.NotNull;

public class Key implements HasName, HasId, Comparable<Key> {
    private final String name;
    private final int id;
    private final int scanCode;

    public Key(String name, int id, int scanCode) {
        this.name = name;
        this.id = id;
        this.scanCode = scanCode;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getScanCode() {
        return scanCode;
    }

    @Override
    public int compareTo(@NotNull Key o) {
        return Integer.compare(id, o.id);
    }

    public static Key createFromMinecraftName(String name, int id, int scanCode) {
        String actualName = name;
        if (name.startsWith("key.keyboard.")) {
            actualName = name.substring("key.keyboard.".length());
        } else if (name.startsWith("key.")) {
            actualName = name.substring("key.".length());
        }

        return new Key(actualName, id, scanCode);
    }

}
