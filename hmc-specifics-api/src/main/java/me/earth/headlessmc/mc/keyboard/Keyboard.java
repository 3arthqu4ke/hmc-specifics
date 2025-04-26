package me.earth.headlessmc.mc.keyboard;

public interface Keyboard extends Iterable<Key> {
    void press(Key key);

    void release(Key key);

}
