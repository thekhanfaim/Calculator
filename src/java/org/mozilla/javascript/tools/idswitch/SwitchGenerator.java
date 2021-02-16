/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.tools.idswitch;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.idswitch.CodePrinter;
import org.mozilla.javascript.tools.idswitch.IdValuePair;

public class SwitchGenerator {
    private CodePrinter P;
    private ToolErrorReporter R;
    private boolean c_was_defined;
    int char_tail_test_threshold = 2;
    private int[] columns;
    private String default_value;
    private IdValuePair[] pairs;
    private String source_file;
    int use_if_threshold = 3;
    String v_c = "c";
    String v_guess = "X";
    String v_id = "id";
    String v_label = "L";
    String v_length_suffix = "_length";
    String v_s = "s";
    String v_switch_label = "L0";

    private static boolean bigger(IdValuePair idValuePair, IdValuePair idValuePair2, int n) {
        if (n < 0) {
            int n2 = idValuePair.idLength - idValuePair2.idLength;
            if (n2 != 0) {
                return n2 > 0;
            }
            return idValuePair.id.compareTo(idValuePair2.id) > 0;
        }
        return idValuePair.id.charAt(n) > idValuePair2.id.charAt(n);
    }

    private void check_all_is_different(int n, int n2) {
        if (n != n2) {
            IdValuePair idValuePair = this.pairs[n];
            while (++n != n2) {
                IdValuePair idValuePair2 = this.pairs[n];
                if (!idValuePair.id.equals((Object)idValuePair2.id)) {
                    idValuePair = idValuePair2;
                    continue;
                }
                throw this.on_same_pair_fail(idValuePair, idValuePair2);
            }
        }
    }

    private int count_different_chars(int n, int n2, int n3) {
        int n4 = 0;
        char c = '\uffffffff';
        while (n != n2) {
            char c2 = this.pairs[n].id.charAt(n3);
            if (c2 != c) {
                ++n4;
                c = c2;
            }
            ++n;
        }
        return n4;
    }

    private int count_different_lengths(int n, int n2) {
        int n3 = 0;
        int n4 = -1;
        while (n != n2) {
            int n5 = this.pairs[n].idLength;
            if (n4 != n5) {
                ++n3;
                n4 = n5;
            }
            ++n;
        }
        return n3;
    }

    private int find_max_different_column(int n, int n2, int n3) {
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i != n3; ++i) {
            int n6 = this.columns[i];
            this.sort_pairs(n, n2, n6);
            int n7 = this.count_different_chars(n, n2, n6);
            if (n7 == n2 - n) {
                return i;
            }
            if (n4 >= n7) continue;
            n4 = n7;
            n5 = i;
        }
        if (n5 != n3 - 1) {
            this.sort_pairs(n, n2, this.columns[n5]);
        }
        return n5;
    }

    private void generate_body(int n, int n2, int n3) {
        this.P.indent(n3);
        this.P.p(this.v_switch_label);
        this.P.p(": { ");
        this.P.p(this.v_id);
        this.P.p(" = ");
        this.P.p(this.default_value);
        this.P.p("; String ");
        this.P.p(this.v_guess);
        this.P.p(" = null;");
        this.c_was_defined = false;
        int n4 = this.P.getOffset();
        this.P.p(" int ");
        this.P.p(this.v_c);
        this.P.p(';');
        int n5 = this.P.getOffset();
        this.P.nl();
        this.generate_length_switch(n, n2, n3 + 1);
        if (!this.c_was_defined) {
            this.P.erase(n4, n5);
        }
        this.P.indent(n3 + 1);
        this.P.p("if (");
        this.P.p(this.v_guess);
        this.P.p("!=null && ");
        this.P.p(this.v_guess);
        this.P.p("!=");
        this.P.p(this.v_s);
        this.P.p(" && !");
        this.P.p(this.v_guess);
        this.P.p(".equals(");
        this.P.p(this.v_s);
        this.P.p(")) ");
        this.P.p(this.v_id);
        this.P.p(" = ");
        this.P.p(this.default_value);
        this.P.p(";");
        this.P.nl();
        this.P.indent(n3 + 1);
        this.P.p("break ");
        this.P.p(this.v_switch_label);
        this.P.p(";");
        this.P.nl();
        this.P.line(n3, "}");
    }

    private void generate_length_switch(int n, int n2, int n3) {
        boolean bl;
        this.sort_pairs(n, n2, -1);
        this.check_all_is_different(n, n2);
        int n4 = this.count_different_lengths(n, n2);
        this.columns = new int[this.pairs[n2 - 1].idLength];
        if (n4 <= this.use_if_threshold) {
            if (n4 != 1) {
                this.P.indent(n3);
                this.P.p("int ");
                this.P.p(this.v_s);
                this.P.p(this.v_length_suffix);
                this.P.p(" = ");
                this.P.p(this.v_s);
                this.P.p(".length();");
                this.P.nl();
            }
            bl = true;
        } else {
            this.P.indent(n3);
            this.P.p(this.v_label);
            this.P.p(": switch (");
            this.P.p(this.v_s);
            this.P.p(".length()) {");
            this.P.nl();
            bl = false;
        }
        int n5 = this.pairs[n].idLength;
        int n6 = 0;
        int n7 = n;
        int n8 = n;
        int n9 = n5;
        do {
            int n10;
            int n11;
            if ((n11 = n7 + 1) != n2) {
                int n12;
                n6 = n12 = this.pairs[n11].idLength;
                if (n12 == n9) {
                    n7 = n11;
                    continue;
                }
            }
            int n13 = n6;
            if (bl) {
                this.P.indent(n3);
                if (n8 != n) {
                    this.P.p("else ");
                }
                this.P.p("if (");
                if (n4 == 1) {
                    this.P.p(this.v_s);
                    this.P.p(".length()==");
                } else {
                    this.P.p(this.v_s);
                    this.P.p(this.v_length_suffix);
                    this.P.p("==");
                }
                this.P.p(n9);
                this.P.p(") {");
                n10 = n3 + 1;
            } else {
                this.P.indent(n3);
                this.P.p("case ");
                this.P.p(n9);
                this.P.p(":");
                n10 = n3 + 1;
            }
            boolean bl2 = bl ^ true;
            this.generate_letter_switch(n8, n11, n10, bl2, bl);
            if (bl) {
                this.P.p("}");
                this.P.nl();
            } else {
                this.P.p("break ");
                this.P.p(this.v_label);
                this.P.p(";");
                this.P.nl();
            }
            if (n11 == n2) {
                if (!bl) {
                    this.P.indent(n3);
                    this.P.p("}");
                    this.P.nl();
                }
                return;
            }
            n8 = n11;
            n9 = n13;
            n7 = n11;
            n6 = n13;
        } while (true);
    }

    private void generate_letter_switch(int n, int n2, int n3, boolean bl, boolean bl2) {
        int n4 = this.pairs[n].idLength;
        int n5 = 0;
        while (n5 != n4) {
            this.columns[n5] = n5++;
        }
        this.generate_letter_switch_r(n, n2, n4, n3, bl, bl2);
    }

    private boolean generate_letter_switch_r(int n, int n2, int n3, int n4, boolean bl, boolean bl2) {
        boolean bl3;
        boolean bl4;
        int n5 = n;
        boolean bl5 = false;
        if (n5 + 1 == n2) {
            this.P.p(' ');
            IdValuePair idValuePair = this.pairs[n5];
            if (n3 > this.char_tail_test_threshold) {
                this.P.p(this.v_guess);
                this.P.p("=");
                this.P.qstring(idValuePair.id);
                this.P.p(";");
                this.P.p(this.v_id);
                this.P.p("=");
                this.P.p(idValuePair.value);
                this.P.p(";");
                bl5 = false;
            } else if (n3 == 0) {
                bl5 = true;
                this.P.p(this.v_id);
                this.P.p("=");
                this.P.p(idValuePair.value);
                this.P.p("; break ");
                this.P.p(this.v_switch_label);
                this.P.p(";");
            } else {
                this.P.p("if (");
                int n6 = this.columns[0];
                this.P.p(this.v_s);
                this.P.p(".charAt(");
                this.P.p(n6);
                this.P.p(")==");
                this.P.qchar(idValuePair.id.charAt(n6));
                for (int i = 1; i != n3; ++i) {
                    this.P.p(" && ");
                    int n7 = this.columns[i];
                    this.P.p(this.v_s);
                    this.P.p(".charAt(");
                    this.P.p(n7);
                    this.P.p(")==");
                    this.P.qchar(idValuePair.id.charAt(n7));
                }
                this.P.p(") {");
                this.P.p(this.v_id);
                this.P.p("=");
                this.P.p(idValuePair.value);
                this.P.p("; break ");
                this.P.p(this.v_switch_label);
                this.P.p(";}");
            }
            this.P.p(' ');
            return bl5;
        }
        int n8 = this.find_max_different_column(n, n2, n3);
        int n9 = this.columns[n8];
        int n10 = this.count_different_chars(n5, n2, n9);
        int[] arrn = this.columns;
        arrn[n8] = arrn[n3 - 1];
        if (bl2) {
            this.P.nl();
            this.P.indent(n4);
        } else {
            this.P.p(' ');
        }
        if (n10 <= this.use_if_threshold) {
            this.c_was_defined = true;
            this.P.p(this.v_c);
            this.P.p("=");
            this.P.p(this.v_s);
            this.P.p(".charAt(");
            this.P.p(n9);
            this.P.p(");");
            bl3 = bl;
            bl4 = true;
        } else {
            boolean bl6;
            if (!bl) {
                bl6 = true;
                this.P.p(this.v_label);
                this.P.p(": ");
            } else {
                bl6 = bl;
            }
            this.P.p("switch (");
            this.P.p(this.v_s);
            this.P.p(".charAt(");
            this.P.p(n9);
            this.P.p(")) {");
            bl4 = false;
            bl3 = bl6;
        }
        char c = this.pairs[n5].id.charAt(n9);
        char c2 = '\u0000';
        int n11 = n;
        int n12 = n;
        char c3 = c;
        do {
            int n13;
            int n14;
            if ((n14 = n12 + 1) != n2) {
                char c4;
                c2 = c4 = this.pairs[n14].id.charAt(n9);
                if (c4 == c3) {
                    n12 = n14;
                    continue;
                }
            }
            char c5 = c2;
            if (bl4) {
                this.P.nl();
                this.P.indent(n4);
                if (n11 != n5) {
                    this.P.p("else ");
                }
                this.P.p("if (");
                this.P.p(this.v_c);
                this.P.p("==");
                this.P.qchar(c3);
                this.P.p(") {");
                n13 = n4 + 1;
            } else {
                this.P.nl();
                this.P.indent(n4);
                this.P.p("case ");
                this.P.qchar(c3);
                this.P.p(":");
                n13 = n4 + 1;
            }
            int n15 = n3 - 1;
            int n16 = n11;
            int n17 = n13;
            int n18 = n10;
            boolean bl7 = bl3;
            int n19 = n9;
            boolean bl8 = this.generate_letter_switch_r(n16, n14, n15, n17, bl7, bl4);
            if (bl4) {
                this.P.p("}");
            } else if (!bl8) {
                this.P.p("break ");
                this.P.p(this.v_label);
                this.P.p(";");
            }
            if (n14 == n2) {
                if (bl4) {
                    this.P.nl();
                    if (bl2) {
                        this.P.indent(n4 - 1);
                    } else {
                        this.P.indent(n4);
                    }
                } else {
                    this.P.nl();
                    this.P.indent(n4);
                    this.P.p("}");
                    if (bl2) {
                        this.P.nl();
                        this.P.indent(n4 - 1);
                    } else {
                        this.P.p(' ');
                    }
                }
                this.columns[n8] = n19;
                return false;
            }
            n11 = n14;
            c3 = c5;
            n5 = n;
            n12 = n14;
            c2 = c5;
            n9 = n19;
            n10 = n18;
        } while (true);
    }

    private static void heap4Sort(IdValuePair[] arridValuePair, int n, int n2, int n3) {
        if (n2 <= 1) {
            return;
        }
        SwitchGenerator.makeHeap4(arridValuePair, n, n2, n3);
        while (n2 > 1) {
            IdValuePair idValuePair;
            IdValuePair idValuePair2 = arridValuePair[n + --n2];
            arridValuePair[n + n2] = idValuePair = arridValuePair[n + 0];
            arridValuePair[n + 0] = idValuePair2;
            SwitchGenerator.heapify4(arridValuePair, n, n2, 0, n3);
        }
    }

    private static void heapify4(IdValuePair[] arridValuePair, int n, int n2, int n3, int n4) {
        IdValuePair idValuePair = arridValuePair[n + n3];
        do {
            int n5 = n3 << 2;
            int n6 = n5 | 1;
            int n7 = n5 | 2;
            int n8 = n5 | 3;
            int n9 = n5 + 4;
            if (n9 >= n2) {
                if (n6 < n2) {
                    IdValuePair idValuePair2 = arridValuePair[n + n6];
                    if (n7 != n2) {
                        IdValuePair idValuePair3;
                        IdValuePair idValuePair4 = arridValuePair[n + n7];
                        if (SwitchGenerator.bigger(idValuePair4, idValuePair2, n4)) {
                            idValuePair2 = idValuePair4;
                            n6 = n7;
                        }
                        if (n8 != n2 && SwitchGenerator.bigger(idValuePair3 = arridValuePair[n + n8], idValuePair2, n4)) {
                            idValuePair2 = idValuePair3;
                            n6 = n8;
                        }
                    }
                    if (SwitchGenerator.bigger(idValuePair2, idValuePair, n4)) {
                        arridValuePair[n + n3] = idValuePair2;
                        arridValuePair[n + n6] = idValuePair;
                    }
                }
                return;
            }
            IdValuePair idValuePair5 = arridValuePair[n + n6];
            IdValuePair idValuePair6 = arridValuePair[n + n7];
            IdValuePair idValuePair7 = arridValuePair[n + n8];
            IdValuePair idValuePair8 = arridValuePair[n + n9];
            if (SwitchGenerator.bigger(idValuePair6, idValuePair5, n4)) {
                idValuePair5 = idValuePair6;
                n6 = n7;
            }
            if (SwitchGenerator.bigger(idValuePair8, idValuePair7, n4)) {
                idValuePair7 = idValuePair8;
                n8 = n9;
            }
            if (SwitchGenerator.bigger(idValuePair7, idValuePair5, n4)) {
                idValuePair5 = idValuePair7;
                n6 = n8;
            }
            if (SwitchGenerator.bigger(idValuePair, idValuePair5, n4)) {
                return;
            }
            arridValuePair[n + n3] = idValuePair5;
            arridValuePair[n + n6] = idValuePair;
            n3 = n6;
        } while (true);
    }

    private static void makeHeap4(IdValuePair[] arridValuePair, int n, int n2, int n3) {
        int n4 = n2 + 2 >> 2;
        while (n4 != 0) {
            SwitchGenerator.heapify4(arridValuePair, n, n2, --n4, n3);
        }
    }

    private EvaluatorException on_same_pair_fail(IdValuePair idValuePair, IdValuePair idValuePair2) {
        int n = idValuePair.getLineNumber();
        int n2 = idValuePair2.getLineNumber();
        if (n2 > n) {
            int n3 = n;
            n = n2;
            n2 = n3;
        }
        String string2 = ToolErrorReporter.getMessage("msg.idswitch.same_string", idValuePair.id, (Object)new Integer(n2));
        return this.R.runtimeError(string2, this.source_file, n, null, 0);
    }

    private void sort_pairs(int n, int n2, int n3) {
        SwitchGenerator.heap4Sort(this.pairs, n, n2 - n, n3);
    }

    public void generateSwitch(String[] arrstring, String string2) {
        int n = arrstring.length / 2;
        IdValuePair[] arridValuePair = new IdValuePair[n];
        for (int i = 0; i != n; ++i) {
            arridValuePair[i] = new IdValuePair(arrstring[i * 2], arrstring[1 + i * 2]);
        }
        this.generateSwitch(arridValuePair, string2);
    }

    public void generateSwitch(IdValuePair[] arridValuePair, String string2) {
        int n = arridValuePair.length;
        if (n == 0) {
            return;
        }
        this.pairs = arridValuePair;
        this.default_value = string2;
        this.generate_body(0, n, 2);
    }

    public CodePrinter getCodePrinter() {
        return this.P;
    }

    public ToolErrorReporter getReporter() {
        return this.R;
    }

    public String getSourceFileName() {
        return this.source_file;
    }

    public void setCodePrinter(CodePrinter codePrinter) {
        this.P = codePrinter;
    }

    public void setReporter(ToolErrorReporter toolErrorReporter) {
        this.R = toolErrorReporter;
    }

    public void setSourceFileName(String string2) {
        this.source_file = string2;
    }
}

