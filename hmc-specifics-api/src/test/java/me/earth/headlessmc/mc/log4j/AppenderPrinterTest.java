package me.earth.headlessmc.mc.log4j;

import me.earth.headlessmc.api.HeadlessMc;
import me.earth.headlessmc.api.classloading.Deencapsulator;
import me.earth.headlessmc.api.command.line.CommandLine;
import me.earth.headlessmc.api.command.line.CommandLineReader;
import me.earth.headlessmc.jline.JLineCommandLineReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.utils.AttributedString;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class AppenderPrinterTest {
    private final Deencapsulator deencapsulator = new Deencapsulator();
    private MockJLineCommandLineReader mockCommandLineReader;
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private LineReader mockLineReader;
    private Terminal mockTerminal;

    @BeforeEach
    public void setup() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter, true);

        LineReader mockLineReader = new LineReader() {
            @Override
            public Map<String, KeyMap<Binding>> defaultKeyMaps() {
                return new HashMap<>();
            }

            @Override
            public String readLine() throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(Character mask) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(String prompt, Character mask) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(String prompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(String prompt, String rightPrompt, Character mask, String buffer) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public String readLine(String prompt, String rightPrompt, MaskingCallback maskingCallback, String buffer) throws UserInterruptException, EndOfFileException {
                return "";
            }

            @Override
            public void printAbove(String text) {
                printWriter.println(text);
            }

            @Override
            public void printAbove(AttributedString str) {

            }

            @Override
            public boolean isReading() {
                return false;
            }

            @Override
            public LineReader variable(String name, Object value) {
                return null;
            }

            @Override
            public LineReader option(Option option, boolean value) {
                return null;
            }

            @Override
            public void callWidget(String name) {

            }

            @Override
            public Map<String, Object> getVariables() {
                return new HashMap<>();
            }

            @Override
            public Object getVariable(String name) {
                return null;
            }

            @Override
            public void setVariable(String name, Object value) {

            }

            @Override
            public boolean isSet(Option option) {
                return false;
            }

            @Override
            public void setOpt(Option option) {

            }

            @Override
            public void unsetOpt(Option option) {

            }

            @Override
            public Terminal getTerminal() {
                return null;
            }

            @Override
            public Map<String, Widget> getWidgets() {
                return new HashMap<>();
            }

            @Override
            public Map<String, Widget> getBuiltinWidgets() {
                return new HashMap<>();
            }

            @Override
            public Buffer getBuffer() {
                return null;
            }

            @Override
            public String getAppName() {
                return "";
            }

            @Override
            public void runMacro(String macro) {

            }

            @Override
            public MouseEvent readMouseEvent() {
                return null;
            }

            @Override
            public History getHistory() {
                return null;
            }

            @Override
            public Parser getParser() {
                return null;
            }

            @Override
            public Highlighter getHighlighter() {
                return null;
            }

            @Override
            public Expander getExpander() {
                return null;
            }

            @Override
            public Map<String, KeyMap<Binding>> getKeyMaps() {
                return new HashMap<>();
            }

            @Override
            public String getKeyMap() {
                return "";
            }

            @Override
            public boolean setKeyMap(String name) {
                return false;
            }

            @Override
            public KeyMap<Binding> getKeys() {
                return null;
            }

            @Override
            public ParsedLine getParsedLine() {
                return null;
            }

            @Override
            public String getSearchTerm() {
                return "";
            }

            @Override
            public RegionType getRegionActive() {
                return null;
            }

            @Override
            public int getRegionMark() {
                return 0;
            }

            @Override
            public void addCommandsInBuffer(Collection<String> commands) {

            }

            @Override
            public void editAndAddInBuffer(File file) throws Exception {

            }

            @Override
            public String getLastBinding() {
                return "";
            }

            @Override
            public String getTailTip() {
                return "";
            }

            @Override
            public void setTailTip(String tailTip) {

            }

            @Override
            public void setAutosuggestion(SuggestionType type) {

            }

            @Override
            public SuggestionType getAutosuggestion() {
                return null;
            }

            @Override
            public void zeroOut() {

            }
        };

        Terminal mockTerminal = new Terminal() {
            @Override
            public void close() {

            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public SignalHandler handle(Signal signal, SignalHandler handler) {
                return null;
            }

            @Override
            public void raise(Signal signal) {

            }

            @Override
            public NonBlockingReader reader() {
                return null;
            }

            @Override
            public PrintWriter writer() {
                return printWriter;
            }

            @Override
            public Charset encoding() {
                return null;
            }

            @Override
            public InputStream input() {
                return null;
            }

            @Override
            public OutputStream output() {
                return null;
            }

            @Override
            public boolean canPauseResume() {
                return false;
            }

            @Override
            public void pause() {

            }

            @Override
            public void pause(boolean wait) throws InterruptedException {

            }

            @Override
            public void resume() {

            }

            @Override
            public boolean paused() {
                return false;
            }

            @Override
            public Attributes enterRawMode() {
                return null;
            }

            @Override
            public boolean echo() {
                return false;
            }

            @Override
            public boolean echo(boolean echo) {
                return false;
            }

            @Override
            public Attributes getAttributes() {
                return null;
            }

            @Override
            public void setAttributes(Attributes attr) {

            }

            @Override
            public Size getSize() {
                return null;
            }

            @Override
            public void setSize(Size size) {

            }

            @Override
            public void flush() {

            }

            @Override
            public String getType() {
                return "";
            }

            @Override
            public boolean puts(InfoCmp.Capability capability, Object... params) {
                return false;
            }

            @Override
            public boolean getBooleanCapability(InfoCmp.Capability capability) {
                return false;
            }

            @Override
            public Integer getNumericCapability(InfoCmp.Capability capability) {
                return 0;
            }

            @Override
            public String getStringCapability(InfoCmp.Capability capability) {
                return "";
            }

            @Override
            public Cursor getCursorPosition(IntConsumer discarded) {
                return null;
            }

            @Override
            public boolean hasMouseSupport() {
                return false;
            }

            @Override
            public boolean trackMouse(MouseTracking tracking) {
                return false;
            }

            @Override
            public MouseEvent readMouseEvent() {
                return null;
            }

            @Override
            public MouseEvent readMouseEvent(IntSupplier reader) {
                return null;
            }

            @Override
            public boolean hasFocusSupport() {
                return false;
            }

            @Override
            public boolean trackFocus(boolean tracking) {
                return false;
            }

            @Override
            public ColorPalette getPalette() {
                return null;
            }
        };

        mockCommandLineReader = new MockJLineCommandLineReader();
        mockCommandLineReader.mockTerminal = mockTerminal;
        mockCommandLineReader.mockLineReader = mockLineReader;
        this.mockTerminal = mockTerminal;
        this.mockLineReader = mockLineReader;
    }

    @Test
    public void testNullCommandLine() {
        assertThrows(IllegalStateException.class, () -> new AppenderPrinter.Factory().getAppenderPrinter(deencapsulator, new CommandLine()));
    }

    @Test
    public void testDirectPrintAboveLineReader() {
        CommandLine commandLine = new CommandLine();
        commandLine.setCommandLineReader(mockCommandLineReader);
        Optional<AppenderPrinter> appenderPrinter = new AppenderPrinter.Factory().getAppenderPrinter(deencapsulator, commandLine);
        assertTrue(appenderPrinter.isPresent());
        String testText = "Hello, World!";
        appenderPrinter.get().printAboveLineReader(testText);
        assertEquals(testText + System.lineSeparator(), stringWriter.toString());
    }

    @Test
    public void testReflectiveLineReader() {
        AppenderPrinter.Factory factory = new AppenderPrinter.Factory() {
            @Override
            protected Class<?> getLineReaderClass(Object commandLineReader) {
                return LineReaderWithoutPrintAboveAndNoWidgets.class;
            }
        };

        CommandLine commandLine = new CommandLine();
        commandLine.setCommandLineReader(mockCommandLineReader);
        Optional<AppenderPrinter> optionalAppenderPrinter = factory.getAppenderPrinter(deencapsulator, commandLine);
        assertFalse(optionalAppenderPrinter.isPresent());

        MockJLine2CommandLineReader mockJLine2CommandLineReader = new MockJLine2CommandLineReader();
        LineReaderWithoutPrintAbove lineReaderWithoutPrintAbove = new LineReaderWithoutPrintAbove();
        mockJLine2CommandLineReader.mockLineReader = lineReaderWithoutPrintAbove;
        mockJLine2CommandLineReader.mockTerminal = mockTerminal;
        commandLine.setCommandLineReader(mockJLine2CommandLineReader);
        factory = new AppenderPrinter.Factory() {
            @Override
            protected Class<?> getLineReaderClass(Object commandLineReader) {
                return lineReaderWithoutPrintAbove.getClass();
            }
        };

        optionalAppenderPrinter = factory.getAppenderPrinter(deencapsulator, commandLine);
        assertTrue(optionalAppenderPrinter.isPresent());
        assertInstanceOf(AppenderPrinter.Factory.ReflectivePrinter.class, optionalAppenderPrinter.get());

        String testText = "Hello, World!";
        optionalAppenderPrinter.get().printAboveLineReader(testText);
        assertEquals(testText, stringWriter.toString());
    }

    private static class LineReaderWithoutPrintAboveAndNoWidgets {

    }

    private class LineReaderWithoutPrintAbove {
        public String widget;

        public PrintWriter writer() {
            return mockTerminal.writer();
        }

        public void callWidget(String widget) {
            this.widget = widget;
        }
    }

    private static class MockJLineCommandLineReader extends JLineCommandLineReader {
        public LineReader mockLineReader;
        public Terminal mockTerminal;

        @Override
        public LineReader getLineReader() {
            return mockLineReader;
        }

        @Override
        public Terminal getTerminal() {
            return mockTerminal;
        }
    }

    private static class MockJLine2CommandLineReader implements CommandLineReader {
        public Object mockLineReader;
        public Terminal mockTerminal;

        public Object getLineReader() {
            return mockLineReader;
        }

        public Terminal getTerminal() {
            return mockTerminal;
        }

        @Override
        public void read(HeadlessMc headlessMc) throws IOError {

        }
    }

}
