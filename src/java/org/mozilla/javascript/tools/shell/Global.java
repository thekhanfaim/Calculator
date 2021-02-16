/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.ByteArrayInputStream
 *  java.io.ByteArrayOutputStream
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileNotFoundException
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStream
 *  java.io.PrintStream
 *  java.io.Reader
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.ClassNotFoundException
 *  java.lang.IllegalAccessException
 *  java.lang.IllegalArgumentException
 *  java.lang.InstantiationException
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Iterable
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Process
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.lang.VirtualMachineError
 *  java.lang.reflect.InvocationTargetException
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.net.URL
 *  java.net.URLConnection
 *  java.nio.charset.Charset
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 */
package org.mozilla.javascript.tools.shell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Synchronizer;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;
import org.mozilla.javascript.serialize.ScriptableInputStream;
import org.mozilla.javascript.serialize.ScriptableOutputStream;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.-$$Lambda$Global$QRMIKKwBvzpmLL1LluAGrzZ4Kjw;
import org.mozilla.javascript.tools.shell.Environment;
import org.mozilla.javascript.tools.shell.Main;
import org.mozilla.javascript.tools.shell.PipeThread;
import org.mozilla.javascript.tools.shell.QuitAction;
import org.mozilla.javascript.tools.shell.Runner;
import org.mozilla.javascript.tools.shell.ShellConsole;

public class Global
extends ImporterTopLevel {
    static final long serialVersionUID = 4029130780977538005L;
    boolean attemptedJLineLoad;
    private ShellConsole console;
    private HashMap<String, String> doctestCanonicalizations;
    private PrintStream errStream;
    NativeArray history;
    private InputStream inStream;
    boolean initialized;
    private PrintStream outStream;
    private String[] prompts = new String[]{"js> ", "  > "};
    private QuitAction quitAction;
    private boolean sealedStdLib = false;

    public Global() {
    }

    public Global(Context context) {
        this.init(context);
    }

    public static void defineClass(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> class_ = Global.getClass(arrobject);
        if (Scriptable.class.isAssignableFrom(class_)) {
            ScriptableObject.defineClass(scriptable, class_);
            return;
        }
        throw Global.reportRuntimeError("msg.must.implement.Scriptable");
    }

    public static Object deserialize(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException, ClassNotFoundException {
        if (arrobject.length >= 1) {
            FileInputStream fileInputStream = new FileInputStream(Context.toString(arrobject[0]));
            Scriptable scriptable2 = ScriptableObject.getTopLevelScope(scriptable);
            ScriptableInputStream scriptableInputStream = new ScriptableInputStream((InputStream)fileInputStream, scriptable2);
            Object object = scriptableInputStream.readObject();
            scriptableInputStream.close();
            return Context.toObject(object, scriptable2);
        }
        throw Context.reportRuntimeError("Expected a filename to read the serialization from");
    }

    private static Object doPrint(Object[] arrobject, Function function, boolean bl) {
        PrintStream printStream = Global.getInstance(function).getOut();
        for (int i = 0; i < arrobject.length; ++i) {
            if (i > 0) {
                printStream.print(" ");
            }
            printStream.print(Context.toString(arrobject[i]));
        }
        if (bl) {
            printStream.println();
        }
        return Context.getUndefinedValue();
    }

    public static Object doctest(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        if (arrobject.length == 0) {
            return Boolean.FALSE;
        }
        String string = Context.toString(arrobject[0]);
        Global global = Global.getInstance(function);
        return new Integer(global.runDoctest(context, global, string, null, 0));
    }

    private boolean doctestOutputMatches(String string, String string2) {
        String string3;
        String string4 = string.trim();
        if (string4.equals((Object)(string3 = string2.trim().replace((CharSequence)"\r\n", (CharSequence)"\n")))) {
            return true;
        }
        for (Map.Entry entry : this.doctestCanonicalizations.entrySet()) {
            string4 = string4.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
        }
        if (string4.equals((Object)string3)) {
            return true;
        }
        Pattern pattern = Pattern.compile((String)"@[0-9a-fA-F]+");
        Matcher matcher = pattern.matcher((CharSequence)string4);
        Matcher matcher2 = pattern.matcher((CharSequence)string3);
        do {
            if (!matcher.find()) {
                return false;
            }
            if (!matcher2.find()) {
                return false;
            }
            if (matcher2.start() != matcher.start()) {
                return false;
            }
            int n = matcher.start();
            if (!string4.substring(0, n).equals((Object)string3.substring(0, n))) {
                return false;
            }
            String string5 = matcher.group();
            String string6 = matcher2.group();
            String string7 = (String)this.doctestCanonicalizations.get((Object)string5);
            if (string7 == null) {
                this.doctestCanonicalizations.put((Object)string5, (Object)string6);
                string4 = string4.replace((CharSequence)string5, (CharSequence)string6);
                continue;
            }
            if (string6.equals((Object)string7)) continue;
            return false;
        } while (!string4.equals((Object)string3));
        return true;
    }

    public static void gc(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        System.gc();
    }

    private static String getCharCodingFromType(String string) {
        int n = string.indexOf(59);
        if (n >= 0) {
            int n2;
            int n3 = string.length();
            for (n2 = n + 1; n2 != n3 && string.charAt(n2) <= ' '; ++n2) {
            }
            int n4 = "charset".length();
            if ("charset".regionMatches(true, 0, string, n2, n4)) {
                int n5;
                for (n5 = n2 + "charset".length(); n5 != n3 && string.charAt(n5) <= ' '; ++n5) {
                }
                if (n5 != n3 && string.charAt(n5) == '=') {
                    int n6;
                    for (n6 = n5 + 1; n6 != n3 && string.charAt(n6) <= ' '; ++n6) {
                    }
                    if (n6 != n3) {
                        while (string.charAt(n3 - 1) <= ' ') {
                            --n3;
                        }
                        return string.substring(n6, n3);
                    }
                }
            }
        }
        return null;
    }

    private static Class<?> getClass(Object[] arrobject) {
        if (arrobject.length != 0) {
            Object object;
            Object object2 = arrobject[0];
            if (object2 instanceof Wrapper && (object = ((Wrapper)object2).unwrap()) instanceof Class) {
                return (Class)object;
            }
            String string = Context.toString(arrobject[0]);
            try {
                Class class_ = Class.forName((String)string);
                return class_;
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw Global.reportRuntimeError("msg.class.not.found", string);
            }
        }
        throw Global.reportRuntimeError("msg.expected.string.arg");
    }

    private static Global getInstance(Function function) {
        Scriptable scriptable = function.getParentScope();
        if (scriptable instanceof Global) {
            return (Global)scriptable;
        }
        throw Global.reportRuntimeError("msg.bad.shell.function.scope", String.valueOf((Object)scriptable));
    }

    public static void help(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        Global.getInstance(function).getOut().println(ToolErrorReporter.getMessage("msg.help"));
    }

    public static void load(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        int n = arrobject.length;
        for (int i = 0; i < n; ++i) {
            String string = Context.toString(arrobject[i]);
            try {
                Main.processFile(context, scriptable, string);
            }
            catch (VirtualMachineError virtualMachineError) {
                virtualMachineError.printStackTrace();
                throw Context.reportRuntimeError(ToolErrorReporter.getMessage("msg.uncaughtJSException", virtualMachineError.toString()));
            }
            catch (IOException iOException) {
                throw Context.reportRuntimeError(ToolErrorReporter.getMessage("msg.couldnt.read.source", string, iOException.getMessage()));
            }
            continue;
        }
    }

    public static void loadClass(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IllegalAccessException, InstantiationException {
        Class<?> class_ = Global.getClass(arrobject);
        if (Script.class.isAssignableFrom(class_)) {
            ((Script)class_.newInstance()).exec(context, scriptable);
            return;
        }
        throw Global.reportRuntimeError("msg.must.implement.Script");
    }

    private boolean loadJLine(Charset charset) {
        if (!this.attemptedJLineLoad) {
            this.attemptedJLineLoad = true;
            this.console = ShellConsole.getConsole(this, charset);
        }
        return this.console != null;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    static void pipe(boolean var0, InputStream var1_1, OutputStream var2_2) throws IOException {
        try {
            block14 : {
                var5_3 = new byte[4096];
                do {
                    if (!var0) {
                        var6_4 = var1_1.read(var5_3, 0, 4096);
                    } else {
                        var6_4 = var1_1.read(var5_3, 0, 4096);
                    }
                    if (var6_4 < 0) break block14;
                    if (var0) {
                        var2_2.write(var5_3, 0, var6_4);
                        var2_2.flush();
                        continue;
                    }
                    try {
                        var2_2.write(var5_3, 0, var6_4);
                        var2_2.flush();
                    }
                    catch (IOException var7_5) {
                        break block14;
                    }
                } while (true);
                catch (IOException var9_6) {
                    // empty catch block
                }
            }
            if (!var0) ** GOTO lbl37
        }
        catch (Throwable var3_8) {
            if (!var0) ** GOTO lbl30
            try {
                var1_1.close();
                throw var3_8;
lbl30: // 1 sources:
                var2_2.close();
                throw var3_8;
            }
            catch (IOException var4_9) {
                throw var3_8;
            }
        }
        try {
            var1_1.close();
            return;
lbl37: // 1 sources:
            var2_2.close();
            return;
        }
        catch (IOException var8_7) {
            return;
        }
    }

    public static Object print(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        return Global.doPrint(arrobject, function, true);
    }

    public static void quit(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        Global global = Global.getInstance(function);
        if (global.quitAction != null) {
            int n = arrobject.length == 0 ? 0 : ScriptRuntime.toInt32(arrobject[0]);
            int n2 = n;
            global.quitAction.quit(context, n2);
        }
    }

    public static Object readFile(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException {
        if (arrobject.length != 0) {
            String string = ScriptRuntime.toString(arrobject[0]);
            int n = arrobject.length;
            String string2 = null;
            if (n >= 2) {
                string2 = ScriptRuntime.toString(arrobject[1]);
            }
            return Global.readUrl(string, string2, true);
        }
        throw Global.reportRuntimeError("msg.shell.readFile.bad.args");
    }

    private static String readReader(Reader reader) throws IOException {
        return Global.readReader(reader, 4096);
    }

    private static String readReader(Reader reader, int n) throws IOException {
        char[] arrc = new char[n];
        int n2 = 0;
        int n3;
        while ((n3 = reader.read(arrc, n2, arrc.length - n2)) >= 0) {
            if ((n2 += n3) != arrc.length) continue;
            char[] arrc2 = new char[2 * arrc.length];
            System.arraycopy((Object)arrc, (int)0, (Object)arrc2, (int)0, (int)n2);
            arrc = arrc2;
        }
        return new String(arrc, 0, n2);
    }

    public static Object readUrl(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException {
        if (arrobject.length != 0) {
            String string = ScriptRuntime.toString(arrobject[0]);
            int n = arrobject.length;
            String string2 = null;
            if (n >= 2) {
                string2 = ScriptRuntime.toString(arrobject[1]);
            }
            return Global.readUrl(string, string2, false);
        }
        throw Global.reportRuntimeError("msg.shell.readUrl.bad.args");
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private static String readUrl(String var0, String var1_1, boolean var2_2) throws IOException {
        block13 : {
            var3_3 = null;
            if (var2_2) ** GOTO lbl12
            var23_4 = new URL(var0).openConnection();
            var3_3 = var23_4.getInputStream();
            var14_5 = var23_4.getContentLength();
            if (var14_5 <= 0) {
                var14_5 = 1024;
            }
            if (var1_1 == null && (var24_6 = var23_4.getContentType()) != null) {
                var1_1 = Global.getCharCodingFromType(var24_6);
            }
            ** GOTO lbl29
lbl12: // 1 sources:
            var4_7 = new File(var0);
            var6_8 = var4_7.exists();
            var3_3 = null;
            if (!var6_8) ** GOTO lbl42
            var7_9 = var4_7.canRead();
            var3_3 = null;
            if (!var7_9) ** GOTO lbl38
            var8_10 = var4_7.length();
            var10_11 = (int)var8_10;
            if ((long)var10_11 != var8_10) break block13;
            if (var10_11 == 0) {
                if (false == false) return "";
                null.close();
                return "";
            }
            var3_3 = new FileInputStream(var4_7);
            var14_5 = var10_11;
lbl29: // 2 sources:
            var15_12 = var1_1 == null ? new InputStreamReader(var3_3) : new InputStreamReader(var3_3, var1_1);
            var16_13 = Global.readReader((Reader)var15_12, var14_5);
            return var16_13;
        }
        try {
            var11_14 = new StringBuilder();
            var11_14.append("Too big file size: ");
            var11_14.append(var8_10);
            throw new IOException(var11_14.toString());
lbl38: // 1 sources:
            var17_15 = new StringBuilder();
            var17_15.append("Cannot read file: ");
            var17_15.append(var0);
            throw new IOException(var17_15.toString());
lbl42: // 1 sources:
            var20_16 = new StringBuilder();
            var20_16.append("File not found: ");
            var20_16.append(var0);
            throw new FileNotFoundException(var20_16.toString());
        }
        catch (Throwable var5_17) {
            throw var5_17;
        }
        finally {
            if (var3_3 != null) {
                var3_3.close();
            }
        }
    }

    public static Object readline(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException {
        Global global = Global.getInstance(function);
        if (arrobject.length > 0) {
            return global.console.readLine(Context.toString(arrobject[0]));
        }
        return global.console.readLine();
    }

    static RuntimeException reportRuntimeError(String string) {
        return Context.reportRuntimeError(ToolErrorReporter.getMessage(string));
    }

    static RuntimeException reportRuntimeError(String string, String string2) {
        return Context.reportRuntimeError(ToolErrorReporter.getMessage(string, string2));
    }

    public static Object runCommand(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException {
        RuntimeException runtimeException;
        int n = arrobject.length;
        if (!(n == 0 || n == 1 && arrobject[0] instanceof Scriptable)) {
            PrintStream printStream;
            String[] arrstring;
            Object object;
            Object[] arrobject2;
            int n2;
            Scriptable scriptable2;
            ByteArrayOutputStream byteArrayOutputStream;
            ByteArrayOutputStream byteArrayOutputStream2;
            PrintStream printStream2;
            InputStream inputStream;
            File file;
            Object object2;
            PrintStream printStream3 = null;
            PrintStream printStream4 = null;
            ByteArrayOutputStream byteArrayOutputStream3 = null;
            String[] arrstring2 = null;
            if (arrobject[n - 1] instanceof Scriptable) {
                Object object3;
                Object object4;
                Scriptable scriptable3 = (Scriptable)arrobject[n - 1];
                int n3 = n - 1;
                Object object5 = ScriptableObject.getProperty(scriptable3, "env");
                if (object5 != Scriptable.NOT_FOUND) {
                    if (object5 == null) {
                        arrstring2 = new String[]{};
                    } else if (object5 instanceof Scriptable) {
                        Scriptable scriptable4 = (Scriptable)object5;
                        Object[] arrobject3 = ScriptableObject.getPropertyIds(scriptable4);
                        arrstring2 = new String[arrobject3.length];
                        for (int i = 0; i != arrobject3.length; ++i) {
                            String string;
                            Scriptable scriptable5;
                            Object object6;
                            Object object7 = arrobject3[i];
                            Object[] arrobject4 = arrobject3;
                            if (object7 instanceof String) {
                                string = (String)object7;
                                Object object8 = ScriptableObject.getProperty(scriptable4, string);
                                scriptable5 = scriptable4;
                                object6 = object8;
                            } else {
                                int n4 = ((Number)object7).intValue();
                                String string2 = Integer.toString((int)n4);
                                Object object9 = ScriptableObject.getProperty(scriptable4, n4);
                                string = string2;
                                scriptable5 = scriptable4;
                                object6 = object9;
                            }
                            if (object6 == ScriptableObject.NOT_FOUND) {
                                object6 = Undefined.instance;
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(string);
                            stringBuilder.append('=');
                            stringBuilder.append(ScriptRuntime.toString(object6));
                            arrstring2[i] = stringBuilder.toString();
                            arrobject3 = arrobject4;
                            scriptable4 = scriptable5;
                        }
                    } else {
                        throw Global.reportRuntimeError("msg.runCommand.bad.env");
                    }
                }
                File file2 = (object3 = ScriptableObject.getProperty(scriptable3, "dir")) != Scriptable.NOT_FOUND ? new File(ScriptRuntime.toString(object3)) : null;
                Object object10 = ScriptableObject.getProperty(scriptable3, "input");
                Object object11 = Scriptable.NOT_FOUND;
                inputStream = null;
                if (object10 != object11) {
                    inputStream = Global.toInputStream(object10);
                }
                if ((object4 = ScriptableObject.getProperty(scriptable3, "output")) != Scriptable.NOT_FOUND) {
                    printStream3 = Global.toOutputStream(object4);
                    byteArrayOutputStream3 = null;
                    if (printStream3 == null) {
                        byteArrayOutputStream3 = new ByteArrayOutputStream();
                        printStream3 = byteArrayOutputStream3;
                    }
                } else {
                    printStream3 = null;
                }
                Object object12 = ScriptableObject.getProperty(scriptable3, "err");
                Object object13 = Scriptable.NOT_FOUND;
                printStream4 = null;
                ByteArrayOutputStream byteArrayOutputStream4 = null;
                if (object12 != object13) {
                    OutputStream outputStream = Global.toOutputStream(object12);
                    if (outputStream == null) {
                        byteArrayOutputStream4 = new ByteArrayOutputStream();
                        printStream4 = byteArrayOutputStream4;
                    } else {
                        printStream4 = outputStream;
                    }
                }
                Object object14 = ScriptableObject.getProperty(scriptable3, "args");
                file = file2;
                if (object14 != Scriptable.NOT_FOUND) {
                    Object[] arrobject5 = context.getElements(Context.toObject(object14, Global.getTopLevelScope(scriptable)));
                    arrstring = arrstring2;
                    n2 = n3;
                    byteArrayOutputStream2 = byteArrayOutputStream4;
                    arrobject2 = arrobject5;
                    object = object12;
                    byteArrayOutputStream = byteArrayOutputStream3;
                    object2 = object4;
                    scriptable2 = scriptable3;
                } else {
                    arrstring = arrstring2;
                    n2 = n3;
                    byteArrayOutputStream2 = byteArrayOutputStream4;
                    object = object12;
                    byteArrayOutputStream = byteArrayOutputStream3;
                    object2 = object4;
                    scriptable2 = scriptable3;
                    arrobject2 = null;
                }
            } else {
                file = null;
                inputStream = null;
                n2 = n;
                arrstring = null;
                byteArrayOutputStream2 = null;
                arrobject2 = null;
                object = null;
                byteArrayOutputStream = null;
                object2 = null;
                scriptable2 = null;
            }
            Global global = Global.getInstance(function);
            if (printStream3 == null) {
                PrintStream printStream5 = global != null ? global.getOut() : System.out;
                printStream2 = printStream5;
            } else {
                printStream2 = printStream3;
            }
            if (printStream4 == null) {
                PrintStream printStream6 = global != null ? global.getErr() : System.err;
                printStream = printStream6;
            } else {
                printStream = printStream4;
            }
            int n5 = arrobject2 == null ? n2 : n2 + arrobject2.length;
            String[] arrstring3 = new String[n5];
            for (int i = 0; i != n2; ++i) {
                arrstring3[i] = ScriptRuntime.toString(arrobject[i]);
            }
            if (arrobject2 != null) {
                for (int i = 0; i != arrobject2.length; ++i) {
                    arrstring3[n2 + i] = ScriptRuntime.toString(arrobject2[i]);
                }
            }
            String[] arrstring4 = arrstring;
            File file3 = file;
            InputStream inputStream2 = inputStream;
            PrintStream printStream7 = printStream2;
            Scriptable scriptable6 = scriptable2;
            int n6 = Global.runProcess(arrstring3, arrstring4, file3, inputStream2, (OutputStream)printStream7, (OutputStream)printStream);
            if (byteArrayOutputStream != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(ScriptRuntime.toString(object2));
                stringBuilder.append(byteArrayOutputStream.toString());
                ScriptableObject.putProperty(scriptable6, "output", (Object)stringBuilder.toString());
            }
            if (byteArrayOutputStream2 != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(ScriptRuntime.toString(object));
                stringBuilder.append(byteArrayOutputStream2.toString());
                ScriptableObject.putProperty(scriptable6, "err", (Object)stringBuilder.toString());
            }
            return new Integer(n6);
        }
        runtimeException = Global.reportRuntimeError("msg.runCommand.bad.args");
        throw runtimeException;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private static int runProcess(String[] var0, String[] var1_1, File var2_2, InputStream var3_3, OutputStream var4_4, OutputStream var5_5) throws IOException {
        var6_6 = var1_1 == null ? Runtime.getRuntime().exec(var0, null, var2_2) : Runtime.getRuntime().exec(var0, var1_1, var2_2);
        var7_7 = null;
        if (var3_3 == null) ** GOTO lbl8
        try {
            block12 : {
                var7_7 = new PipeThread(false, var3_3, var6_6.getOutputStream());
                var7_7.start();
                break block12;
lbl8: // 1 sources:
                var6_6.getOutputStream().close();
            }
            var9_8 = null;
            if (var4_4 != null) {
                var9_8 = new PipeThread(true, var6_6.getInputStream(), var4_4);
                var9_8.start();
            } else {
                var6_6.getInputStream().close();
            }
            var10_9 = null;
            if (var5_5 != null) {
                var10_9 = new PipeThread(true, var6_6.getErrorStream(), var5_5);
                var10_9.start();
            } else {
                var6_6.getErrorStream().close();
            }
            do {
                block11 : {
                    try {
                        var6_6.waitFor();
                        if (var9_8 != null) {
                            var9_8.join();
                        }
                        if (var7_7 != null) {
                            var7_7.join();
                        }
                        if (var10_9 == null) break block11;
                        var10_9.join();
                    }
                    catch (InterruptedException var11_11) {
                        continue;
                    }
                }
                var13_10 = var6_6.exitValue();
                var6_6.destroy();
                return var13_10;
                break;
            } while (true);
        }
        catch (Throwable var8_12) {
            var6_6.destroy();
            throw var8_12;
        }
    }

    public static void seal(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof ScriptableObject && object != Undefined.instance) {
                continue;
            }
            if (object instanceof Scriptable && object != Undefined.instance) {
                throw Global.reportRuntimeError("msg.shell.seal.not.scriptable");
            }
            throw Global.reportRuntimeError("msg.shell.seal.not.object");
        }
        for (int i = 0; i != arrobject.length; ++i) {
            ((ScriptableObject)arrobject[i]).sealObject();
        }
    }

    public static void serialize(Context context, Scriptable scriptable, Object[] arrobject, Function function) throws IOException {
        if (arrobject.length >= 2) {
            Object object = arrobject[0];
            ScriptableOutputStream scriptableOutputStream = new ScriptableOutputStream((OutputStream)new FileOutputStream(Context.toString(arrobject[1])), ScriptableObject.getTopLevelScope(scriptable));
            scriptableOutputStream.writeObject(object);
            scriptableOutputStream.close();
            return;
        }
        throw Context.reportRuntimeError("Expected an object to serialize and a filename to write the serialization to");
    }

    public static Object spawn(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        block8 : {
            Runner runner;
            block7 : {
                Scriptable scriptable2;
                block6 : {
                    scriptable2 = function.getParentScope();
                    if (arrobject.length == 0 || !(arrobject[0] instanceof Function)) break block6;
                    int n = arrobject.length;
                    Object[] arrobject2 = null;
                    if (n > 1) {
                        boolean bl = arrobject[1] instanceof Scriptable;
                        arrobject2 = null;
                        if (bl) {
                            arrobject2 = context.getElements((Scriptable)arrobject[1]);
                        }
                    }
                    if (arrobject2 == null) {
                        arrobject2 = ScriptRuntime.emptyArgs;
                    }
                    runner = new Runner(scriptable2, (Function)arrobject[0], arrobject2);
                    break block7;
                }
                if (arrobject.length == 0 || !(arrobject[0] instanceof Script)) break block8;
                runner = new Runner(scriptable2, (Script)arrobject[0]);
            }
            runner.factory = context.getFactory();
            Thread thread = new Thread((Runnable)runner);
            thread.start();
            return thread;
        }
        throw Global.reportRuntimeError("msg.spawn.args");
    }

    public static Object sync(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        if (arrobject.length >= 1 && arrobject.length <= 2 && arrobject[0] instanceof Function) {
            int n = arrobject.length;
            Object object = null;
            if (n == 2) {
                Object object2 = arrobject[1];
                Object object3 = Undefined.instance;
                object = null;
                if (object2 != object3) {
                    object = arrobject[1];
                }
            }
            return new Synchronizer((Function)arrobject[0], object);
        }
        throw Global.reportRuntimeError("msg.sync.args");
    }

    private static InputStream toInputStream(Object object) throws IOException {
        boolean bl = object instanceof Wrapper;
        InputStream inputStream = null;
        String string = null;
        if (bl) {
            Object object2 = ((Wrapper)object).unwrap();
            if (object2 instanceof InputStream) {
                inputStream = (InputStream)object2;
                string = null;
            } else if (object2 instanceof byte[]) {
                inputStream = new ByteArrayInputStream((byte[])object2);
                string = null;
            } else if (object2 instanceof Reader) {
                string = Global.readReader((Reader)object2);
                inputStream = null;
            } else {
                boolean bl2 = object2 instanceof char[];
                inputStream = null;
                string = null;
                if (bl2) {
                    string = new String((char[])object2);
                }
            }
        }
        if (inputStream == null) {
            if (string == null) {
                string = ScriptRuntime.toString(object);
            }
            inputStream = new ByteArrayInputStream(string.getBytes());
        }
        return inputStream;
    }

    private static OutputStream toOutputStream(Object object) {
        boolean bl = object instanceof Wrapper;
        OutputStream outputStream = null;
        if (bl) {
            Object object2 = ((Wrapper)object).unwrap();
            boolean bl2 = object2 instanceof OutputStream;
            outputStream = null;
            if (bl2) {
                outputStream = (OutputStream)object2;
            }
        }
        return outputStream;
    }

    public static Object toint32(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        Object object = arrobject.length != 0 ? arrobject[0] : Undefined.instance;
        if (object instanceof Integer) {
            return object;
        }
        return ScriptRuntime.wrapInt(ScriptRuntime.toInt32(object));
    }

    public static double version(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        if (arrobject.length > 0) {
            context.setLanguageVersion((int)Context.toNumber(arrobject[0]));
        }
        return context.getLanguageVersion();
    }

    public static Object write(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        return Global.doPrint(arrobject, function, false);
    }

    public ShellConsole getConsole(Charset charset) {
        if (!this.loadJLine(charset)) {
            this.console = ShellConsole.getConsole(this.getIn(), this.getErr(), charset);
        }
        return this.console;
    }

    public PrintStream getErr() {
        PrintStream printStream = this.errStream;
        if (printStream == null) {
            printStream = System.err;
        }
        return printStream;
    }

    public InputStream getIn() {
        InputStream inputStream;
        if (this.inStream == null && !this.attemptedJLineLoad && this.loadJLine(Charset.defaultCharset())) {
            this.inStream = this.console.getIn();
        }
        if ((inputStream = this.inStream) == null) {
            inputStream = System.in;
        }
        return inputStream;
    }

    public PrintStream getOut() {
        PrintStream printStream = this.outStream;
        if (printStream == null) {
            printStream = System.out;
        }
        return printStream;
    }

    public String[] getPrompts(Context context) {
        Scriptable scriptable;
        Object object;
        if (ScriptableObject.hasProperty((Scriptable)this, "prompts") && (object = ScriptableObject.getProperty((Scriptable)this, "prompts")) instanceof Scriptable && ScriptableObject.hasProperty(scriptable = (Scriptable)object, 0) && ScriptableObject.hasProperty(scriptable, 1)) {
            Object object2 = ScriptableObject.getProperty(scriptable, 0);
            if (object2 instanceof Function) {
                object2 = ((Function)object2).call(context, this, scriptable, new Object[0]);
            }
            this.prompts[0] = Context.toString(object2);
            Object object3 = ScriptableObject.getProperty(scriptable, 1);
            if (object3 instanceof Function) {
                object3 = ((Function)object3).call(context, this, scriptable, new Object[0]);
            }
            this.prompts[1] = Context.toString(object3);
        }
        return this.prompts;
    }

    public void init(Context context) {
        NativeArray nativeArray;
        this.initStandardObjects(context, this.sealedStdLib);
        this.defineFunctionProperties(new String[]{"defineClass", "deserialize", "doctest", "gc", "help", "load", "loadClass", "print", "quit", "readline", "readFile", "readUrl", "runCommand", "seal", "serialize", "spawn", "sync", "toint32", "version", "write"}, Global.class, 2);
        Environment.defineClass(this);
        this.defineProperty("environment", (Object)new Environment(this), 2);
        this.history = nativeArray = (NativeArray)context.newArray((Scriptable)this, 0);
        this.defineProperty("history", (Object)nativeArray, 2);
        this.initialized = true;
    }

    public void init(ContextFactory contextFactory) {
        contextFactory.call(new -$$Lambda$Global$QRMIKKwBvzpmLL1LluAGrzZ4Kjw(this));
    }

    public void initQuitAction(QuitAction quitAction) {
        if (quitAction != null) {
            if (this.quitAction == null) {
                this.quitAction = quitAction;
                return;
            }
            throw new IllegalArgumentException("The method is once-call.");
        }
        throw new IllegalArgumentException("quitAction is null");
    }

    public Require installRequire(Context context, List<String> list, boolean bl) {
        RequireBuilder requireBuilder = new RequireBuilder();
        requireBuilder.setSandboxed(bl);
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            for (String string : list) {
                try {
                    URI uRI = new URI(string);
                    if (!uRI.isAbsolute()) {
                        uRI = new File(string).toURI().resolve("");
                    }
                    if (!uRI.toString().endsWith("/")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append((Object)uRI);
                        stringBuilder.append("/");
                        uRI = new URI(stringBuilder.toString());
                    }
                    arrayList.add((Object)uRI);
                }
                catch (URISyntaxException uRISyntaxException) {
                    throw new RuntimeException((Throwable)uRISyntaxException);
                }
            }
        }
        requireBuilder.setModuleScriptProvider(new SoftCachingModuleScriptProvider(new UrlModuleSourceProvider((Iterable<URI>)arrayList, null)));
        Require require = requireBuilder.createRequire(context, this);
        require.install(this);
        return require;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public /* synthetic */ Object lambda$init$0$Global(Context context) {
        this.init(context);
        return null;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public int runDoctest(Context context, Scriptable scriptable, String string, String string2, int n) {
        PrintStream printStream;
        String string5;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2;
        PrintStream printStream2;
        void var36_48;
        ErrorReporter errorReporter;
        block14 : {
            int n2;
            this.doctestCanonicalizations = new HashMap();
            String[] arrstring = string.split("\r\n?|\n");
            String string3 = this.prompts[0].trim();
            String string4 = this.prompts[1].trim();
            int n3 = 0;
            do {
                int n4 = arrstring.length;
                n2 = 0;
                if (n3 >= n4) break;
                boolean bl = arrstring[n3].trim().startsWith(string3);
                n2 = 0;
                if (bl) break;
                ++n3;
            } while (true);
            while (n3 < arrstring.length) {
                int n5;
                StringBuilder stringBuilder;
                int n6;
                String string6;
                String string7;
                String[] arrstring2;
                block16 : {
                    void var34_35;
                    block15 : {
                        PrintStream printStream3;
                        block13 : {
                            int n7;
                            String string8 = arrstring[n3].trim().substring(string3.length());
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(string8);
                            stringBuilder2.append("\n");
                            String string9 = stringBuilder2.toString();
                            String string10 = string9;
                            for (int i = n3 + 1; i < arrstring.length && arrstring[i].trim().startsWith(string4); ++i) {
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(string10);
                                stringBuilder3.append(arrstring[i].trim().substring(string4.length()));
                                String string11 = stringBuilder3.toString();
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(string11);
                                stringBuilder4.append("\n");
                                string10 = stringBuilder4.toString();
                            }
                            String string12 = "";
                            for (n7 = i; n7 < arrstring.length && !arrstring[n7].trim().startsWith(string3); ++n7) {
                                StringBuilder stringBuilder5 = new StringBuilder();
                                stringBuilder5.append(string12);
                                stringBuilder5.append(arrstring[n7]);
                                stringBuilder5.append("\n");
                                string12 = stringBuilder5.toString();
                            }
                            printStream2 = this.getOut();
                            printStream3 = this.getErr();
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            byteArrayOutputStream2 = new ByteArrayOutputStream();
                            this.setOut(new PrintStream((OutputStream)byteArrayOutputStream));
                            this.setErr(new PrintStream((OutputStream)byteArrayOutputStream2));
                            string5 = "";
                            errorReporter = context.getErrorReporter();
                            context.setErrorReporter(new ToolErrorReporter(false, this.getErr()));
                            n6 = n2 + 1;
                            string6 = string12;
                            String string13 = string10;
                            n5 = n7;
                            string7 = string10;
                            arrstring2 = arrstring;
                            Object object = context.evaluateString(scriptable, string13, "doctest input", 1, null);
                            Object object2 = Context.getUndefinedValue();
                            if (object == object2) break block13;
                            try {
                                String string14;
                                if (object instanceof Function && string7.trim().startsWith("function")) break block13;
                                string5 = string14 = Context.toString(object);
                            }
                            catch (Throwable throwable) {
                                printStream = printStream3;
                                break block14;
                            }
                            catch (RhinoException rhinoException) {
                                printStream = printStream3;
                                break block15;
                            }
                        }
                        this.setOut(printStream2);
                        this.setErr(printStream3);
                        context.setErrorReporter(errorReporter);
                        stringBuilder = new StringBuilder();
                        break block16;
                        catch (Throwable throwable) {
                            printStream = printStream3;
                            break block14;
                        }
                        catch (RhinoException rhinoException) {
                            printStream = printStream3;
                        }
                    }
                    ToolErrorReporter.reportException(context.getErrorReporter(), (RhinoException)var34_35);
                    this.setOut(printStream2);
                    this.setErr(printStream);
                    context.setErrorReporter(errorReporter);
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(string5);
                stringBuilder.append(byteArrayOutputStream2.toString());
                stringBuilder.append(byteArrayOutputStream.toString());
                String string15 = stringBuilder.toString();
                if (!this.doctestOutputMatches(string6, string15)) {
                    StringBuilder stringBuilder6 = new StringBuilder();
                    stringBuilder6.append("doctest failure running:\n");
                    stringBuilder6.append(string7);
                    stringBuilder6.append("expected: ");
                    stringBuilder6.append(string6);
                    stringBuilder6.append("actual: ");
                    stringBuilder6.append(string15);
                    stringBuilder6.append("\n");
                    String string16 = stringBuilder6.toString();
                    if (string2 == null) throw Context.reportRuntimeError(string16);
                    throw Context.reportRuntimeError(string16, string2, n + n5 - 1, null, 0);
                }
                arrstring = arrstring2;
                n3 = n5;
                n2 = n6;
            }
            return n2;
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.setOut(printStream2);
        this.setErr(printStream);
        context.setErrorReporter(errorReporter);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string5);
        stringBuilder.append(byteArrayOutputStream2.toString());
        stringBuilder.append(byteArrayOutputStream.toString());
        stringBuilder.toString();
        throw var36_48;
    }

    public void setErr(PrintStream printStream) {
        this.errStream = printStream;
    }

    public void setIn(InputStream inputStream) {
        this.inStream = inputStream;
    }

    public void setOut(PrintStream printStream) {
        this.outStream = printStream;
    }

    public void setSealedStdLib(boolean bl) {
        this.sealedStdLib = bl;
    }
}

