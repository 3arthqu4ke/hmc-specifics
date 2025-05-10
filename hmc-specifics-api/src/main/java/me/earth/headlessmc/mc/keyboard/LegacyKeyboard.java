package me.earth.headlessmc.mc.keyboard;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LegacyKeyboard implements Keyboard {
    private final List<Key> keys;
    private final Set<Integer> pressed = new HashSet<>();

    private int eventKey = 0;
    private boolean eventKeyState = false;

    public LegacyKeyboard(List<Key> keys) {
        this.keys = keys;
    }

    public boolean getEventKeyState() {
        return eventKeyState;
    }

    public int getEventKey() {
        return eventKey;
    }

    public boolean isKeyDown(int key) {
        return pressed.contains(key);
    }

    @Override
    public void press(Key key) {
        eventKey = key.getId();
        eventKeyState = true;
        pressed.add(key.getId());
    }

    @Override
    public void release(Key key) {
        eventKey = key.getId();
        eventKeyState = false;
        pressed.remove(key.getId());
    }

    @Override
    public @NotNull Iterator<Key> iterator() {
        return keys.iterator();
    }

}
