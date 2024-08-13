package me.earth.headlessmc.mc;

import me.earth.headlessmc.api.config.Property;

import static me.earth.headlessmc.api.config.PropertyTypes.bool;

public interface SpecificProperties {
    Property<Boolean> KEEP_RUNTIME_COMMANDS = bool("hmc.keepruntimecommands");
    Property<Boolean> DEBUG_JLINE = bool("hmc.debug.jline");

}
