/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStream
 *  java.io.PrintStream
 *  java.io.PrintWriter
 *  java.io.Reader
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.IllegalAccessException
 *  java.lang.IllegalArgumentException
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.InstantiationException
 *  java.lang.Math
 *  java.lang.NoSuchMethodException
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Constructor
 *  java.lang.reflect.InvocationHandler
 *  java.lang.reflect.InvocationTargetException
 *  java.lang.reflect.Method
 *  java.lang.reflect.Proxy
 *  java.nio.charset.Charset
 */
package org.mozilla.javascript.tools.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.FlexibleCompletor;

public abstract class ShellConsole {
    private static final Class[] BOOLEAN_ARG;
    private static final Class[] CHARSEQ_ARG;
    private static final Class[] NO_ARG;
    private static final Class[] STRING_ARG;

    static {
        NO_ARG = new Class[0];
        Class[] arrclass = new Class[]{Boolean.TYPE};
        BOOLEAN_ARG = arrclass;
        STRING_ARG = new Class[]{String.class};
        CHARSEQ_ARG = new Class[]{CharSequence.class};
    }

    protected ShellConsole() {
    }

    public static ShellConsole getConsole(InputStream inputStream, PrintStream printStream, Charset charset) {
        return new SimpleShellConsole(inputStream, printStream, charset);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static ShellConsole getConsole(Scriptable scriptable, Charset charset) {
        ClassLoader classLoader = ShellConsole.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        if (classLoader == null) {
            return null;
        }
        try {
            Class<?> class_ = Kit.classOrNull(classLoader, "jline.console.ConsoleReader");
            if (class_ != null) {
                return ShellConsole.getJLineShellConsoleV2(classLoader, class_, scriptable, charset);
            }
            Class<?> class_2 = Kit.classOrNull(classLoader, "jline.ConsoleReader");
            if (class_2 == null) return null;
            return ShellConsole.getJLineShellConsoleV1(classLoader, class_2, scriptable, charset);
        }
        catch (InvocationTargetException invocationTargetException) {
            return null;
        }
        catch (InstantiationException instantiationException) {
            return null;
        }
        catch (IllegalAccessException illegalAccessException) {
            return null;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return null;
    }

    private static JLineShellConsoleV1 getJLineShellConsoleV1(ClassLoader classLoader, Class<?> class_, Scriptable scriptable, Charset charset) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object object = class_.getConstructor(new Class[0]).newInstance(new Object[0]);
        Class[] arrclass = BOOLEAN_ARG;
        Object[] arrobject = new Object[]{Boolean.FALSE};
        ShellConsole.tryInvoke(object, "setBellEnabled", arrclass, arrobject);
        Class<?> class_2 = Kit.classOrNull(classLoader, "jline.Completor");
        Object object2 = Proxy.newProxyInstance((ClassLoader)classLoader, (Class[])new Class[]{class_2}, (InvocationHandler)new FlexibleCompletor(class_2, scriptable));
        ShellConsole.tryInvoke(object, "addCompletor", new Class[]{class_2}, object2);
        return new JLineShellConsoleV1(object, charset);
    }

    private static JLineShellConsoleV2 getJLineShellConsoleV2(ClassLoader classLoader, Class<?> class_, Scriptable scriptable, Charset charset) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object object = class_.getConstructor(new Class[0]).newInstance(new Object[0]);
        Class[] arrclass = BOOLEAN_ARG;
        Object[] arrobject = new Object[]{Boolean.FALSE};
        ShellConsole.tryInvoke(object, "setBellEnabled", arrclass, arrobject);
        Class<?> class_2 = Kit.classOrNull(classLoader, "jline.console.completer.Completer");
        Object object2 = Proxy.newProxyInstance((ClassLoader)classLoader, (Class[])new Class[]{class_2}, (InvocationHandler)new FlexibleCompletor(class_2, scriptable));
        ShellConsole.tryInvoke(object, "addCompleter", new Class[]{class_2}, object2);
        return new JLineShellConsoleV2(object, charset);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static /* varargs */ Object tryInvoke(Object object, String string2, Class[] arrclass, Object ... arrobject) {
        Method method = object.getClass().getDeclaredMethod(string2, arrclass);
        if (method == null) return null;
        try {
            return method.invoke(object, arrobject);
        }
        catch (InvocationTargetException invocationTargetException) {
            return null;
        }
        catch (IllegalAccessException illegalAccessException) {
            return null;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return null;
    }

    public abstract void flush() throws IOException;

    public abstract InputStream getIn();

    public abstract void print(String var1) throws IOException;

    public abstract void println() throws IOException;

    public abstract void println(String var1) throws IOException;

    public abstract String readLine() throws IOException;

    public abstract String readLine(String var1) throws IOException;

    private static class ConsoleInputStream
    extends InputStream {
        private static final byte[] EMPTY = new byte[0];
        private boolean atEOF = false;
        private byte[] buffer = EMPTY;
        private final ShellConsole console;
        private final Charset cs;
        private int cursor = -1;

        public ConsoleInputStream(ShellConsole shellConsole, Charset charset) {
            this.console = shellConsole;
            this.cs = charset;
        }

        private boolean ensureInput() throws IOException {
            if (this.atEOF) {
                return false;
            }
            int n = this.cursor;
            if (n < 0 || n > this.buffer.length) {
                if (this.readNextLine() == -1) {
                    this.atEOF = true;
                    return false;
                }
                this.cursor = 0;
            }
            return true;
        }

        private int readNextLine() throws IOException {
            String string2 = this.console.readLine(null);
            if (string2 != null) {
                byte[] arrby = string2.getBytes(this.cs);
                this.buffer = arrby;
                return arrby.length;
            }
            this.buffer = EMPTY;
            return -1;
        }

        public int read() throws IOException {
            ConsoleInputStream consoleInputStream = this;
            synchronized (consoleInputStream) {
                int n;
                byte[] arrby;
                block6 : {
                    block5 : {
                        boolean bl = this.ensureInput();
                        if (bl) break block5;
                        return -1;
                    }
                    n = this.cursor;
                    arrby = this.buffer;
                    if (n != arrby.length) break block6;
                    this.cursor = n + 1;
                    return 10;
                }
                int n2 = n + 1;
                this.cursor = n2;
                byte by = arrby[n];
                return by;
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public int read(byte[] arrby, int n, int n2) throws IOException {
            ConsoleInputStream consoleInputStream = this;
            synchronized (consoleInputStream) {
                if (arrby == null) throw new NullPointerException();
                if (n >= 0 && n2 >= 0) {
                    int n3 = arrby.length;
                    if (n2 <= n3 - n) {
                        block14 : {
                            if (n2 == 0) {
                                return 0;
                            }
                            boolean bl = this.ensureInput();
                            if (bl) break block14;
                            return -1;
                        }
                        int n4 = Math.min((int)n2, (int)(this.buffer.length - this.cursor));
                        for (int i = 0; i < n4; ++i) {
                            arrby[n + i] = this.buffer[i + this.cursor];
                        }
                        if (n4 < n2) {
                            int n5 = n4 + 1;
                            arrby[n4 + n] = 10;
                            n4 = n5;
                        }
                        this.cursor = n4 + this.cursor;
                        return n4;
                    }
                }
                throw new IndexOutOfBoundsException();
            }
        }
    }

    private static class JLineShellConsoleV1
    extends ShellConsole {
        private final InputStream in;
        private final Object reader;

        JLineShellConsoleV1(Object object, Charset charset) {
            this.reader = object;
            this.in = new ConsoleInputStream(this, charset);
        }

        @Override
        public void flush() throws IOException {
            ShellConsole.tryInvoke(this.reader, "flushConsole", NO_ARG, new Object[0]);
        }

        @Override
        public InputStream getIn() {
            return this.in;
        }

        @Override
        public void print(String string2) throws IOException {
            ShellConsole.tryInvoke(this.reader, "printString", STRING_ARG, new Object[]{string2});
        }

        @Override
        public void println() throws IOException {
            ShellConsole.tryInvoke(this.reader, "printNewline", NO_ARG, new Object[0]);
        }

        @Override
        public void println(String string2) throws IOException {
            ShellConsole.tryInvoke(this.reader, "printString", STRING_ARG, new Object[]{string2});
            ShellConsole.tryInvoke(this.reader, "printNewline", NO_ARG, new Object[0]);
        }

        @Override
        public String readLine() throws IOException {
            return (String)ShellConsole.tryInvoke(this.reader, "readLine", NO_ARG, new Object[0]);
        }

        @Override
        public String readLine(String string2) throws IOException {
            return (String)ShellConsole.tryInvoke(this.reader, "readLine", STRING_ARG, new Object[]{string2});
        }
    }

    private static class JLineShellConsoleV2
    extends ShellConsole {
        private final InputStream in;
        private final Object reader;

        JLineShellConsoleV2(Object object, Charset charset) {
            this.reader = object;
            this.in = new ConsoleInputStream(this, charset);
        }

        @Override
        public void flush() throws IOException {
            ShellConsole.tryInvoke(this.reader, "flush", NO_ARG, new Object[0]);
        }

        @Override
        public InputStream getIn() {
            return this.in;
        }

        @Override
        public void print(String string2) throws IOException {
            ShellConsole.tryInvoke(this.reader, "print", CHARSEQ_ARG, new Object[]{string2});
        }

        @Override
        public void println() throws IOException {
            ShellConsole.tryInvoke(this.reader, "println", NO_ARG, new Object[0]);
        }

        @Override
        public void println(String string2) throws IOException {
            ShellConsole.tryInvoke(this.reader, "println", CHARSEQ_ARG, new Object[]{string2});
        }

        @Override
        public String readLine() throws IOException {
            return (String)ShellConsole.tryInvoke(this.reader, "readLine", NO_ARG, new Object[0]);
        }

        @Override
        public String readLine(String string2) throws IOException {
            return (String)ShellConsole.tryInvoke(this.reader, "readLine", STRING_ARG, new Object[]{string2});
        }
    }

    private static class SimpleShellConsole
    extends ShellConsole {
        private final InputStream in;
        private final PrintWriter out;
        private final BufferedReader reader;

        SimpleShellConsole(InputStream inputStream, PrintStream printStream, Charset charset) {
            this.in = inputStream;
            this.out = new PrintWriter((OutputStream)printStream);
            this.reader = new BufferedReader((Reader)new InputStreamReader(inputStream, charset));
        }

        @Override
        public void flush() throws IOException {
            this.out.flush();
        }

        @Override
        public InputStream getIn() {
            return this.in;
        }

        @Override
        public void print(String string2) throws IOException {
            this.out.print(string2);
        }

        @Override
        public void println() throws IOException {
            this.out.println();
        }

        @Override
        public void println(String string2) throws IOException {
            this.out.println(string2);
        }

        @Override
        public String readLine() throws IOException {
            return this.reader.readLine();
        }

        @Override
        public String readLine(String string2) throws IOException {
            if (string2 != null) {
                this.out.write(string2);
                this.out.flush();
            }
            return this.reader.readLine();
        }
    }

}

