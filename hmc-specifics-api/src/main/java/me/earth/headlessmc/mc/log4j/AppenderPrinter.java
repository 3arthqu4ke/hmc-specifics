package me.earth.headlessmc.mc.log4j;

import me.earth.headlessmc.api.classloading.Deencapsulator;
import me.earth.headlessmc.api.process.InAndOutProvider;
import me.earth.headlessmc.api.util.ReflectionUtil;
import me.earth.headlessmc.jline.JLineCommandLineReader;
import me.earth.headlessmc.logging.Logger;
import me.earth.headlessmc.logging.LoggerFactory;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Wrapper for {@link org.jline.reader.LineReader#printAbove(String)} or alternatives,
 * if the LineReader is on another classloader or if it does not have printAbove yet.
 */
public interface AppenderPrinter {
    void printAboveLineReader(String text);

    class Factory {
        private static final Logger LOGGER = LoggerFactory.getLogger(AppenderPrinter.class);

        public Optional<AppenderPrinter> getAppenderPrinter(Deencapsulator deencapsulator, Object commandLine) {
            LOGGER.info("Getting AppenderPrinter for " + commandLine);
            deencapsulator.deencapsulate(commandLine.getClass());
            // commandLineReader = commandLine.getCommandLineReader()
            Object commandLineReader = ReflectionUtil.invoke("getCommandLineReader", commandLine, null, new Class[0]);
            if (commandLineReader == null) {
                throw new IllegalStateException(commandLine + " reader was null!");
            }

            Class<?> lineReaderClass = getLineReaderClass(commandLineReader);
            deencapsulator.deencapsulate(lineReaderClass);
            Method printAbove = null;
            try {
                printAbove = lineReaderClass.getMethod("printAbove", String.class);
            } catch (NoSuchMethodException e) {
                LOGGER.warn("LineReader does not support printAbove!", e);
            }

            if (printAbove != null && commandLineReader instanceof JLineCommandLineReader) {
                return Optional.of(new DirectPrinter((JLineCommandLineReader) commandLineReader));
            } else {
                deencapsulator.deencapsulate(commandLineReader.getClass());
                Method getLineReader;
                Method getTerminal;
                try {
                    getLineReader = commandLineReader.getClass().getMethod("getLineReader");
                    getTerminal = commandLineReader.getClass().getMethod("getTerminal");
                } catch (NoSuchMethodException e) {
                    LOGGER.warn("CommandLineReader " + commandLineReader + " does not expose a getLineReader and/or getTerminal method!", e);
                    return Optional.empty();
                }

                try {
                    return Optional.of(new ReflectivePrinter(deencapsulator, commandLineReader, printAbove, getTerminal, getLineReader, lineReaderClass));
                } catch (ReflectiveOperationException e) {
                    LOGGER.warn("Failed to create Reflection AppenderPrinter", e);
                    return Optional.empty();
                }
            }
        }

        protected Class<?> getLineReaderClass(Object commandLineReader) {
            Class<?> lineReaderClass;
            try {
                lineReaderClass = commandLineReader.getClass().getClassLoader().loadClass(LineReader.class.getName());
                if (lineReaderClass == null) {
                    throw new ClassNotFoundException("null");
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                LOGGER.error("Failed to find LineReader class on classloader", e);
                lineReaderClass = LineReader.class;
            }

            return lineReaderClass;
        }

        public static class DirectPrinter implements AppenderPrinter {
            private final InAndOutProvider inAndOutProvider = new InAndOutProvider();
            private final JLineCommandLineReader commandLineReader;

            public DirectPrinter(JLineCommandLineReader commandLineReader) {
                this.commandLineReader = commandLineReader;
            }

            @Override
            public void printAboveLineReader(String text) {
                LineReader lineReader = commandLineReader.getLineReader();
                if (lineReader != null) {
                    lineReader.printAbove(text);
                } else {
                    Terminal terminal = commandLineReader.getTerminal();
                    if (terminal != null) {
                        terminal.writer().print(text);
                        terminal.writer().flush();
                    } else {
                        inAndOutProvider.getOut().get().print(text);
                    }
                }
            }
        }

        public static class ReflectivePrinter implements AppenderPrinter {
            private final InAndOutProvider inAndOutProvider = new InAndOutProvider();
            private final Object commandLineReader;
            private final Method printAbove;
            private final Method getTerminal;
            private final Method getLineReader;

            private final Method writer;
            private final Method callWidget;

            public ReflectivePrinter(Deencapsulator deencapsulator,
                                     Object commandLineReader,
                                     Method printAbove,
                                     Method getTerminal,
                                     Method getLineReader,
                                     Class<?> lineReaderClass) throws ReflectiveOperationException {
                this.commandLineReader = commandLineReader;
                this.printAbove = printAbove;
                this.getTerminal = getTerminal;
                this.getLineReader = getLineReader;
                if (printAbove == null) {
                    Class<?> terminalClass = getTerminal.getReturnType();
                    deencapsulator.deencapsulate(terminalClass);
                    writer = terminalClass.getMethod("writer");
                    callWidget = lineReaderClass.getMethod("callWidget", String.class);
                } else {
                    writer = null;
                    callWidget = null;
                }
            }

            @Override
            public void printAboveLineReader(String text) {
                try {
                    Object lineReader = getLineReader.invoke(commandLineReader);
                    if (lineReader != null) {
                        if (printAbove != null) {
                            printAbove.invoke(lineReader, text);
                        } else {
                            Object terminal = getTerminal.invoke(commandLineReader);
                            if (terminal != null) {
                                // https://github.com/jline/jline3/issues/292
                                // potential race condition but I do not care!!!!
                                PrintWriter terminalWriter = (PrintWriter) writer.invoke(terminal);
                                callWidget.invoke(lineReader, LineReader.CLEAR);
                                terminalWriter.print(text);
                                callWidget.invoke(lineReader, LineReader.REDRAW_LINE);
                                callWidget.invoke(lineReader, LineReader.REDISPLAY);
                                terminalWriter.flush();
                            } else {
                                inAndOutProvider.getOut().get().print(text);
                            }
                        }
                    } else {
                        Object terminal = getTerminal.invoke(commandLineReader);
                        if (terminal != null) {
                            PrintWriter terminalWriter = (PrintWriter) writer.invoke(terminal);
                            terminalWriter.print(text);
                            terminalWriter.flush();
                        } else {
                            inAndOutProvider.getOut().get().print(text);
                        }
                    }
                } catch (Exception e) {
                    inAndOutProvider.getErr().get().print("Failed to print text: " + text);
                    e.printStackTrace(inAndOutProvider.getErr().get());
                }
            }
        }
    }

}
