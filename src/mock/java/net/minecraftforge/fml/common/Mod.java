package net.minecraftforge.fml.common;

/**
 * Mock of the Mod interface. The mock sourceSet containing this class will
 * only be added to the classpath if we are compiling without forge.
 */
public @interface Mod {
    String value();
}
