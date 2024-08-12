package me.earth.headlessmc.mc.commands;

import me.earth.headlessmc.api.MockedHeadlessMc;
import me.earth.headlessmc.api.command.CommandContext;
import me.earth.headlessmc.mc.MockMc;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DisAndConnectCommandTests {
    @Test
    public void connectCommandTest() {
        CommandContext ctx = new MinecraftContext(MockedHeadlessMc.INSTANCE, MockMc.INSTANCE);
        MockMc.INSTANCE.disconnect();
        assertNull(MockMc.INSTANCE.ip);
        assertEquals(-1, MockMc.INSTANCE.port);

        ctx.execute("connect 2b2t.org");
        assertEquals("2b2t.org", MockMc.INSTANCE.ip);
        assertEquals(25565, MockMc.INSTANCE.port);

        ctx.execute("disconnect");
        assertNull(MockMc.INSTANCE.ip);
        assertEquals(-1, MockMc.INSTANCE.port);

        ctx.execute("connect 2b2t.org 500");
        assertEquals("2b2t.org", MockMc.INSTANCE.ip);
        assertEquals(500, MockMc.INSTANCE.port);
    }

}
