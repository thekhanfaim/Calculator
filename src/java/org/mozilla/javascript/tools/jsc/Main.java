/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.PrintStream
 *  java.lang.Character
 *  java.lang.Class
 *  java.lang.ClassNotFoundException
 *  java.lang.Error
 *  java.lang.Integer
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.StringTokenizer
 */
package org.mozilla.javascript.tools.jsc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.optimizer.ClassCompiler;
import org.mozilla.javascript.tools.SourceReader;
import org.mozilla.javascript.tools.ToolErrorReporter;

public class Main {
    private String characterEncoding;
    private ClassCompiler compiler;
    private CompilerEnvirons compilerEnv;
    private String destinationDir;
    private boolean printHelp;
    private ToolErrorReporter reporter = new ToolErrorReporter(true);
    private String targetName;
    private String targetPackage;

    public Main() {
        CompilerEnvirons compilerEnvirons;
        this.compilerEnv = compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.setErrorReporter(this.reporter);
        this.compiler = new ClassCompiler(this.compilerEnv);
    }

    private void addError(String string2, String string3) {
        String string4 = string3 == null ? ToolErrorReporter.getMessage(string2) : ToolErrorReporter.getMessage(string2, string3);
        this.addFormatedError(string4);
    }

    private void addFormatedError(String string2) {
        this.reporter.error(string2, null, -1, null, -1);
    }

    private static void badUsage(String string2) {
        System.err.println(ToolErrorReporter.getMessage("msg.jsc.bad.usage", Main.class.getName(), string2));
    }

    private File getOutputFile(File file, String string2) {
        File file2;
        File file3 = new File(file, string2.replace('.', File.separatorChar).concat(".class"));
        String string3 = file3.getParent();
        if (string3 != null && !(file2 = new File(string3)).exists()) {
            file2.mkdirs();
        }
        return file3;
    }

    public static void main(String[] arrstring) {
        Main main = new Main();
        String[] arrstring2 = main.processOptions(arrstring);
        if (arrstring2 == null) {
            if (main.printHelp) {
                System.out.println(ToolErrorReporter.getMessage("msg.jsc.usage", Main.class.getName()));
                System.exit((int)0);
            }
            System.exit((int)1);
        }
        if (!main.reporter.hasReportedError()) {
            main.processSource(arrstring2);
        }
    }

    private static void p(String string2) {
        System.out.println(string2);
    }

    private String readSource(File file) {
        String string2 = file.getAbsolutePath();
        if (!file.isFile()) {
            this.addError("msg.jsfile.not.found", string2);
            return null;
        }
        try {
            String string3 = (String)SourceReader.readFileOrUrl(string2, true, this.characterEncoding);
            return string3;
        }
        catch (IOException iOException) {
            this.addFormatedError(iOException.toString());
            return null;
        }
        catch (FileNotFoundException fileNotFoundException) {
            this.addError("msg.couldnt.open", string2);
            return null;
        }
    }

    String getClassName(String string2) {
        char[] arrc = new char[1 + string2.length()];
        boolean bl = Character.isJavaIdentifierStart((char)string2.charAt(0));
        int n = 0;
        if (!bl) {
            int n2 = 0 + 1;
            arrc[0] = 95;
            n = n2;
        }
        int n3 = 0;
        while (n3 < string2.length()) {
            int n4 = string2.charAt(n3);
            arrc[n] = Character.isJavaIdentifierPart((char)n4) ? n4 : 95;
            ++n3;
            ++n;
        }
        return new String(arrc).trim();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public String[] processOptions(String[] var1_1) {
        this.targetPackage = "";
        this.compilerEnv.setGenerateDebugInfo(false);
        var2_2 = 0;
        do {
            block24 : {
                block37 : {
                    block34 : {
                        block32 : {
                            block26 : {
                                block36 : {
                                    block35 : {
                                        block33 : {
                                            block30 : {
                                                block31 : {
                                                    block29 : {
                                                        block28 : {
                                                            block27 : {
                                                                block25 : {
                                                                    if (var2_2 >= var1_1.length) {
                                                                        Main.p(ToolErrorReporter.getMessage("msg.no.file"));
                                                                        return null;
                                                                    }
                                                                    var3_3 = var1_1[var2_2];
                                                                    if (!var3_3.startsWith("-")) {
                                                                        var24_20 = var1_1.length - var2_2;
                                                                        var25_21 = this.targetName;
                                                                        if (var25_21 != null && var24_20 > 1) {
                                                                            this.addError("msg.multiple.js.to.file", var25_21);
                                                                            return null;
                                                                        }
                                                                        var26_22 = new String[var24_20];
                                                                        var27_23 = 0;
                                                                        while (var27_23 != var24_20) {
                                                                            var26_22[var27_23] = var1_1[var2_2 + var27_23];
                                                                            ++var27_23;
                                                                        }
                                                                        return var26_22;
                                                                    }
                                                                    if (var3_3.equals((Object)"-help") || var3_3.equals((Object)"-h") || var3_3.equals((Object)"--help")) break block26;
                                                                    try {
                                                                        if (var3_3.equals((Object)"-version") && ++var2_2 < var1_1.length) {
                                                                            var23_19 = Integer.parseInt((String)var1_1[var2_2]);
                                                                            this.compilerEnv.setLanguageVersion(var23_19);
                                                                            break block24;
                                                                        }
                                                                        if (!var3_3.equals((Object)"-opt") && !var3_3.equals((Object)"-O")) break block25;
                                                                        ++var2_2;
                                                                    }
                                                                    catch (NumberFormatException var4_26) {
                                                                        Main.badUsage(var1_1[var2_2]);
                                                                        return null;
                                                                    }
                                                                    if (var2_2 >= var1_1.length) break block25;
                                                                    var22_18 = Integer.parseInt((String)var1_1[var2_2]);
                                                                    this.compilerEnv.setOptimizationLevel(var22_18);
                                                                }
                                                                if (!var3_3.equals((Object)"-nosource")) break block27;
                                                                this.compilerEnv.setGeneratingSource(false);
                                                                break block24;
                                                            }
                                                            if (var3_3.equals((Object)"-debug") || var3_3.equals((Object)"-g")) ** GOTO lbl87
                                                            if (!var3_3.equals((Object)"-main-method-class") || ++var2_2 >= var1_1.length) break block28;
                                                            this.compiler.setMainMethodClass(var1_1[var2_2]);
                                                            break block24;
                                                        }
                                                        if (!var3_3.equals((Object)"-encoding") || ++var2_2 >= var1_1.length) break block29;
                                                        this.characterEncoding = var1_1[var2_2];
                                                        break block24;
                                                    }
                                                    if (!var3_3.equals((Object)"-o") || ++var2_2 >= var1_1.length) break block30;
                                                    var18_14 = var1_1[var2_2];
                                                    var19_15 = var18_14.length();
                                                    if (var19_15 == 0 || !Character.isJavaIdentifierStart((char)var18_14.charAt(0))) break block31;
                                                    break block32;
                                                }
                                                this.addError("msg.invalid.classfile.name", var18_14);
                                                break block24;
                                            }
                                            if (var3_3.equals((Object)"-observe-instruction-count")) {
                                                this.compilerEnv.setGenerateObserverCount(true);
                                            }
                                            if (!var3_3.equals((Object)"-package") || ++var2_2 >= var1_1.length) break block33;
                                            var14_10 = var1_1[var2_2];
                                            var15_11 = var14_10.length();
                                            break block34;
                                        }
                                        if (!var3_3.equals((Object)"-extends") || ++var2_2 >= var1_1.length) break block35;
                                        var11_8 = var1_1[var2_2];
                                        try {
                                            var13_9 = Class.forName((String)var11_8);
                                        }
                                        catch (ClassNotFoundException var12_24) {
                                            throw new Error(var12_24.toString());
                                        }
                                        this.compiler.setTargetExtends(var13_9);
                                        break block24;
                                    }
                                    if (!var3_3.equals((Object)"-implements") || ++var2_2 >= var1_1.length) break block36;
                                    var5_4 = new StringTokenizer(var1_1[var2_2], ",");
                                    var6_5 = new ArrayList();
                                    break block37;
                                }
                                if (var3_3.equals((Object)"-d") && ++var2_2 < var1_1.length) {
                                    this.destinationDir = var1_1[var2_2];
                                } else {
                                    Main.badUsage(var3_3);
                                    return null;
lbl87: // 1 sources:
                                    this.compilerEnv.setGenerateDebugInfo(true);
                                }
                                break block24;
                            }
                            this.printHelp = true;
                            return null;
                        }
                        for (var20_16 = 1; var20_16 < var19_15; ++var20_16) {
                            var21_17 = var18_14.charAt(var20_16);
                            if (Character.isJavaIdentifierPart((char)var21_17)) continue;
                            if (var21_17 == '.' && var20_16 == var19_15 - 6 && var18_14.endsWith(".class")) {
                                var18_14 = var18_14.substring(0, var20_16);
                                break;
                            }
                            this.addError("msg.invalid.classfile.name", var18_14);
                            break;
                        }
                        this.targetName = var18_14;
                        break block24;
                    }
                    for (var16_12 = 0; var16_12 != var15_11; ++var16_12) {
                        var17_13 = var14_10.charAt(var16_12);
                        if (Character.isJavaIdentifierStart((char)var17_13)) {
                            while (++var16_12 != var15_11 && Character.isJavaIdentifierPart((char)(var17_13 = var14_10.charAt(var16_12)))) {
                            }
                            if (var16_12 == var15_11) break;
                            if (var17_13 == '.' && var16_12 != var15_11 - 1) {
                                continue;
                            }
                        }
                        this.addError("msg.package.name", this.targetPackage);
                        return null;
                    }
                    this.targetPackage = var14_10;
                    break block24;
                }
                while (var5_4.hasMoreTokens()) {
                    var8_7 = var5_4.nextToken();
                    try {
                        var6_5.add((Object)Class.forName((String)var8_7));
                    }
                    catch (ClassNotFoundException var9_25) {
                        throw new Error(var9_25.toString());
                    }
                }
                var7_6 = (Class[])var6_5.toArray((Object[])new Class[var6_5.size()]);
                this.compiler.setTargetImplements(var7_6);
            }
            ++var2_2;
        } while (true);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public void processSource(String[] arrstring) {
        int i = 0;
        while (i != arrstring.length) {
            File file2;
            Object[] arrobject;
            String string2 = arrstring[i];
            if (!string2.endsWith(".js")) {
                this.addError("msg.extension.not.js", string2);
                return;
            }
            File file = new File(string2);
            String string3 = this.readSource(file);
            if (string3 == null) {
                return;
            }
            String string4 = this.targetName;
            if (string4 == null) {
                String string5 = file.getName();
                string4 = this.getClassName(string5.substring(0, -3 + string5.length()));
            }
            if (this.targetPackage.length() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.targetPackage);
                stringBuilder.append(".");
                stringBuilder.append(string4);
                string4 = stringBuilder.toString();
            }
            if ((arrobject = this.compiler.compileToClassFiles(string3, string2, 1, string4)) == null) return;
            if (arrobject.length == 0) {
                return;
            }
            if (this.destinationDir != null) {
                file2 = new File(this.destinationDir);
            } else {
                String string6 = file.getParent();
                file2 = null;
                if (string6 != null) {
                    file2 = new File(string6);
                }
            }
            for (int j = 0; j != arrobject.length; j += 2) {
                String string7 = (String)arrobject[j];
                byte[] arrby = (byte[])arrobject[j + 1];
                File file3 = this.getOutputFile(file2, string7);
                FileOutputStream fileOutputStream = new FileOutputStream(file3);
                fileOutputStream.write(arrby);
                {
                    catch (Throwable throwable) {
                        fileOutputStream.close();
                        throw throwable;
                    }
                }
                try {
                    fileOutputStream.close();
                    continue;
                }
                catch (IOException iOException) {
                    this.addFormatedError(iOException.toString());
                }
            }
            ++i;
        }
    }
}

