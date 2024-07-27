package me.earth.headlessmc.mc;

import me.earth.headlessmc.mc.gui.FontRenderer;
import me.earth.headlessmc.mc.gui.FontRendererListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public enum FontRendererImpl implements FontRenderer, FontRendererListener {
    INSTANCE;

    private final List<FontRendererListener> listeners =
        new CopyOnWriteArrayList<>();

    @Override
    public void register(FontRendererListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(FontRendererListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onRender(String text, float x, float y) {
        listeners.forEach(l -> l.onRender(text, x, y));
    }

}
