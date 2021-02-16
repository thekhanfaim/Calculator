/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.PrintStream
 *  java.io.Reader
 *  java.io.Writer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.text.SimpleDateFormat
 *  java.util.ArrayList
 *  java.util.Date
 *  java.util.List
 */
package org.mozilla.javascript.tools.idswitch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.idswitch.CodePrinter;
import org.mozilla.javascript.tools.idswitch.FileBody;
import org.mozilla.javascript.tools.idswitch.IdValuePair;
import org.mozilla.javascript.tools.idswitch.SwitchGenerator;

public class Main {
    private static final int GENERATED_TAG = 2;
    private static final String GENERATED_TAG_STR = "generated";
    private static final int NORMAL_LINE = 0;
    private static final int STRING_TAG = 3;
    private static final String STRING_TAG_STR = "string";
    private static final int SWITCH_TAG = 1;
    private static final String SWITCH_TAG_STR = "string_id_map";
    private CodePrinter P;
    private ToolErrorReporter R;
    private final List<IdValuePair> all_pairs = new ArrayList();
    private FileBody body;
    private String source_file;
    private int tag_definition_end;
    private int tag_value_end;
    private int tag_value_start;

    private void add_id(char[] arrc, int n, int n2, int n3, int n4) {
        IdValuePair idValuePair = new IdValuePair(new String(arrc, n3, n4 - n3), new String(arrc, n, n2 - n));
        idValuePair.setLineNumber(this.body.getLineNumber());
        this.all_pairs.add((Object)idValuePair);
    }

    private static boolean equals(String string2, char[] arrc, int n, int n2) {
        if (string2.length() == n2 - n) {
            int n3 = n;
            int n4 = 0;
            while (n3 != n2) {
                if (arrc[n3] != string2.charAt(n4)) {
                    return false;
                }
                ++n3;
                ++n4;
            }
            return true;
        }
        return false;
    }

    private int exec(String[] arrstring) {
        CodePrinter codePrinter;
        this.R = new ToolErrorReporter(true, System.err);
        int n = this.process_options(arrstring);
        if (n == 0) {
            this.option_error(ToolErrorReporter.getMessage("msg.idswitch.no_file_argument"));
            return -1;
        }
        if (n > 1) {
            this.option_error(ToolErrorReporter.getMessage("msg.idswitch.too_many_arguments"));
            return -1;
        }
        this.P = codePrinter = new CodePrinter();
        codePrinter.setIndentStep(4);
        this.P.setIndentTabSize(0);
        try {
            this.process_file(arrstring[0]);
            return 0;
        }
        catch (EvaluatorException evaluatorException) {
            return -1;
        }
        catch (IOException iOException) {
            this.print_error(ToolErrorReporter.getMessage("msg.idswitch.io_error", iOException.toString()));
            return -1;
        }
    }

    private int extract_line_tag_id(char[] arrc, int n, int n2) {
        int n3;
        block15 : {
            int n4;
            int n5;
            int n6;
            char c;
            boolean bl;
            boolean bl2;
            block16 : {
                char c2;
                int n7 = Main.skip_white_space(arrc, n, n2);
                int n8 = this.look_for_slash_slash(arrc, n7, n2);
                n3 = 0;
                if (n8 == n2) break block15;
                bl2 = n7 + 2 == n8;
                int n9 = Main.skip_white_space(arrc, n8, n2);
                n3 = 0;
                if (n9 == n2) break block15;
                char c3 = arrc[n9];
                n3 = 0;
                if (c3 != '#') break block15;
                int n10 = n9 + 1;
                bl = false;
                if (n10 != n2) {
                    char c4 = arrc[n10];
                    bl = false;
                    if (c4 == '/') {
                        ++n10;
                        bl = true;
                    }
                }
                n4 = n10;
                while (n10 != n2 && (c2 = arrc[n10]) != '#' && c2 != '=' && !Main.is_white_space(c2)) {
                    ++n10;
                }
                n3 = 0;
                if (n10 == n2) break block15;
                n5 = n10;
                n6 = Main.skip_white_space(arrc, n10, n2);
                n3 = 0;
                if (n6 == n2) break block15;
                c = arrc[n6];
                if (c == '=') break block16;
                n3 = 0;
                if (c != '#') break block15;
            }
            if ((n3 = this.get_tag_id(arrc, n4, n5, bl2)) != 0) {
                String string2;
                if (c == '#') {
                    string2 = null;
                    if (bl) {
                        n3 = -n3;
                        boolean bl3 = Main.is_value_type(n3);
                        string2 = null;
                        if (bl3) {
                            string2 = "msg.idswitch.no_end_usage";
                        }
                    }
                    this.tag_definition_end = n6 + 1;
                } else {
                    if (bl) {
                        string2 = "msg.idswitch.no_end_with_value";
                    } else {
                        boolean bl4 = Main.is_value_type(n3);
                        string2 = null;
                        if (!bl4) {
                            string2 = "msg.idswitch.no_value_allowed";
                        }
                    }
                    n3 = this.extract_tag_value(arrc, n6 + 1, n2, n3);
                }
                if (string2 == null) {
                    return n3;
                }
                String string3 = ToolErrorReporter.getMessage(string2, Main.tag_name(n3));
                throw this.R.runtimeError(string3, this.source_file, this.body.getLineNumber(), null, 0);
            }
        }
        return n3;
    }

    private int extract_tag_value(char[] arrc, int n, int n2, int n3) {
        int n4 = Main.skip_white_space(arrc, n, n2);
        boolean bl = false;
        if (n4 != n2) {
            int n5 = n4;
            int n6 = n4;
            while (n4 != n2) {
                char c = arrc[n4];
                if (Main.is_white_space(c)) {
                    int n7 = Main.skip_white_space(arrc, n4 + 1, n2);
                    if (n7 != n2 && arrc[n7] == '#') {
                        n6 = n4;
                        n4 = n7;
                        break;
                    }
                    n4 = n7 + 1;
                    continue;
                }
                if (c == '#') {
                    n6 = n4;
                    break;
                }
                ++n4;
            }
            bl = false;
            if (n4 != n2) {
                bl = true;
                this.tag_value_start = n5;
                this.tag_value_end = n6;
                this.tag_definition_end = n4 + 1;
            }
        }
        if (bl) {
            return n3;
        }
        return 0;
    }

    private void generate_java_code() {
        this.P.clear();
        Object[] arrobject = new IdValuePair[this.all_pairs.size()];
        this.all_pairs.toArray(arrobject);
        SwitchGenerator switchGenerator = new SwitchGenerator();
        switchGenerator.char_tail_test_threshold = 2;
        switchGenerator.setReporter(this.R);
        switchGenerator.setCodePrinter(this.P);
        switchGenerator.generateSwitch((IdValuePair[])arrobject, "0");
    }

    private int get_tag_id(char[] arrc, int n, int n2, boolean bl) {
        if (bl) {
            if (Main.equals(SWITCH_TAG_STR, arrc, n, n2)) {
                return 1;
            }
            if (Main.equals(GENERATED_TAG_STR, arrc, n, n2)) {
                return 2;
            }
        }
        if (Main.equals(STRING_TAG_STR, arrc, n, n2)) {
            return 3;
        }
        return 0;
    }

    private String get_time_stamp() {
        return new SimpleDateFormat(" 'Last update:' yyyy-MM-dd HH:mm:ss z").format(new Date());
    }

    private static boolean is_value_type(int n) {
        return n == 3;
    }

    private static boolean is_white_space(int n) {
        return n == 32 || n == 9;
        {
        }
    }

    private void look_for_id_definitions(char[] arrc, int n, int n2, boolean bl) {
        int n3;
        int n4;
        int n5 = Main.skip_white_space(arrc, n, n2);
        int n6 = Main.skip_matched_prefix("Id_", arrc, n5, n2);
        if (n6 >= 0 && n6 != (n4 = Main.skip_name_char(arrc, n6, n2)) && (n3 = Main.skip_white_space(arrc, n4, n2)) != n2 && arrc[n3] == '=') {
            int n7;
            int n8;
            if (bl) {
                int n9 = this.tag_value_start;
                int n10 = this.tag_value_end;
                n7 = n9;
                n8 = n10;
            } else {
                n7 = n6;
                n8 = n4;
            }
            this.add_id(arrc, n5, n4, n7, n8);
        }
    }

    private int look_for_slash_slash(char[] arrc, int n, int n2) {
        while (n + 2 <= n2) {
            int n3 = n + 1;
            if (arrc[n] == '/') {
                int n4 = n3 + 1;
                if (arrc[n3] == '/') {
                    return n4;
                }
                n = n4;
                continue;
            }
            n = n3;
        }
        return n2;
    }

    public static void main(String[] arrstring) {
        System.exit((int)new Main().exec(arrstring));
    }

    private void option_error(String string2) {
        this.print_error(ToolErrorReporter.getMessage("msg.idswitch.bad_invocation", string2));
    }

    private void print_error(String string2) {
        System.err.println(string2);
    }

    private void process_file() {
        EvaluatorException evaluatorException;
        int n = 0;
        char[] arrc = this.body.getBuffer();
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        this.body.startLineLoop();
        while (this.body.nextLine()) {
            boolean bl;
            int n6 = this.body.getLineBegin();
            int n7 = this.body.getLineEnd();
            int n8 = this.extract_line_tag_id(arrc, n6, n7);
            if (n != 0) {
                if (n != 1) {
                    if (n != 2) {
                        bl = false;
                    } else if (n8 == 0) {
                        bl = false;
                        if (n2 < 0) {
                            n2 = n6;
                            bl = false;
                        }
                    } else if (n8 == -2) {
                        if (n2 < 0) {
                            n2 = n6;
                        }
                        n = 1;
                        n3 = n6;
                        bl = false;
                    } else {
                        bl = true;
                    }
                } else if (n8 == 0) {
                    this.look_for_id_definitions(arrc, n6, n7, false);
                    bl = false;
                } else if (n8 == 3) {
                    this.look_for_id_definitions(arrc, n6, n7, true);
                    bl = false;
                } else if (n8 == 2) {
                    if (n2 >= 0) {
                        bl = true;
                    } else {
                        n = 2;
                        n4 = this.tag_definition_end;
                        n5 = n7;
                        bl = false;
                    }
                } else if (n8 == -1) {
                    n = 0;
                    bl = false;
                    if (n2 >= 0) {
                        boolean bl2 = this.all_pairs.isEmpty();
                        n = 0;
                        bl = false;
                        if (!bl2) {
                            this.generate_java_code();
                            String string2 = this.P.toString();
                            if (this.body.setReplacement(n2, n3, string2)) {
                                String string3 = this.get_time_stamp();
                                this.body.setReplacement(n4, n5, string3);
                            }
                            n = 0;
                            bl = false;
                        }
                    }
                } else {
                    bl = true;
                }
            } else if (n8 == 1) {
                n = 1;
                this.all_pairs.clear();
                n2 = -1;
                bl = false;
            } else {
                bl = false;
                if (n8 == -1) {
                    bl = true;
                }
            }
            if (!bl) continue;
            String string4 = ToolErrorReporter.getMessage("msg.idswitch.bad_tag_order", Main.tag_name(n8));
            throw this.R.runtimeError(string4, this.source_file, this.body.getLineNumber(), null, 0);
        }
        if (n == 0) {
            return;
        }
        String string5 = ToolErrorReporter.getMessage("msg.idswitch.file_end_in_switch", Main.tag_name(n));
        evaluatorException = this.R.runtimeError(string5, this.source_file, this.body.getLineNumber(), null, 0);
        throw evaluatorException;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private int process_options(String[] var1_1) {
        var2_2 = 1;
        var3_3 = false;
        var4_4 = false;
        var5_5 = var1_1.length;
        block0 : for (var6_6 = 0; var6_6 != var5_5; ++var6_6) {
            var7_7 = var1_1[var6_6];
            var8_8 = var7_7.length();
            if (var8_8 < 2 || var7_7.charAt(0) != '-') continue;
            if (var7_7.charAt(1) != '-') ** GOTO lbl22
            if (var8_8 == 2) {
                var1_1[var6_6] = null;
                break;
            }
            if (var7_7.equals((Object)"--help")) {
                var3_3 = true;
            } else if (var7_7.equals((Object)"--version")) {
                var4_4 = true;
            } else {
                this.option_error(ToolErrorReporter.getMessage("msg.idswitch.bad_option", var7_7));
                var2_2 = -1;
                break;
lbl22: // 2 sources:
                for (var9_9 = 1; var9_9 != var8_8; ++var9_9) {
                    var10_10 = var7_7.charAt(var9_9);
                    if (var10_10 != 'h') {
                        this.option_error(ToolErrorReporter.getMessage("msg.idswitch.bad_option_char", String.valueOf((char)var10_10)));
                        var2_2 = -1;
                        break block0;
                    }
                    var3_3 = true;
                }
            }
            var1_1[var6_6] = null;
        }
        if (var2_2 == 1) {
            if (var3_3) {
                this.show_usage();
                var2_2 = 0;
            }
            if (var4_4) {
                this.show_version();
                var2_2 = 0;
            }
        }
        if (var2_2 == true) return this.remove_nulls(var1_1);
        System.exit((int)var2_2);
        return this.remove_nulls(var1_1);
    }

    private int remove_nulls(String[] arrstring) {
        int n;
        int n2 = arrstring.length;
        for (n = 0; n != n2 && arrstring[n] != null; ++n) {
        }
        int n3 = n;
        if (n != n2) {
            for (int i = n + 1; i != n2; ++i) {
                String string2 = arrstring[i];
                if (string2 == null) continue;
                arrstring[n3] = string2;
                ++n3;
            }
        }
        return n3;
    }

    private void show_usage() {
        System.out.println(ToolErrorReporter.getMessage("msg.idswitch.usage"));
        System.out.println();
    }

    private void show_version() {
        System.out.println(ToolErrorReporter.getMessage("msg.idswitch.version"));
    }

    private static int skip_matched_prefix(String string2, char[] arrc, int n, int n2) {
        int n3 = -1;
        int n4 = string2.length();
        if (n4 <= n2 - n) {
            n3 = n;
            int n5 = 0;
            while (n5 != n4) {
                if (string2.charAt(n5) != arrc[n3]) {
                    return -1;
                }
                ++n5;
                ++n3;
            }
        }
        return n3;
    }

    private static int skip_name_char(char[] arrc, int n, int n2) {
        int n3;
        for (n3 = n; n3 != n2; ++n3) {
            char c = arrc[n3];
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9' || c == '_') continue;
            return n3;
        }
        return n3;
    }

    private static int skip_white_space(char[] arrc, int n, int n2) {
        int n3;
        for (n3 = n; n3 != n2; ++n3) {
            if (Main.is_white_space(arrc[n3])) continue;
            return n3;
        }
        return n3;
    }

    private static String tag_name(int n) {
        if (n != -2) {
            if (n != -1) {
                if (n != 1) {
                    if (n != 2) {
                        return "";
                    }
                    return GENERATED_TAG_STR;
                }
                return SWITCH_TAG_STR;
            }
            return "/string_id_map";
        }
        return "/generated";
    }

    void process_file(String string2) throws IOException {
        this.source_file = string2;
        this.body = new FileBody();
        Object object = string2.equals((Object)"-") ? System.in : new FileInputStream(string2);
        InputStreamReader inputStreamReader = new InputStreamReader(object, "ASCII");
        this.body.readData((Reader)inputStreamReader);
        this.process_file();
        if (this.body.wasModified()) {
            Object object2 = string2.equals((Object)"-") ? System.out : new FileOutputStream(string2);
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)object2);
                this.body.writeData((Writer)outputStreamWriter);
                outputStreamWriter.flush();
                return;
            }
            finally {
                object2.close();
            }
        }
        return;
        finally {
            object.close();
        }
    }
}

