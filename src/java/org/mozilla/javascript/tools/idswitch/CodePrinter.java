/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 */
package org.mozilla.javascript.tools.idswitch;

class CodePrinter {
    private static final int LITERAL_CHAR_MAX_SIZE = 6;
    private char[] buffer = new char[4096];
    private int indentStep = 4;
    private int indentTabSize = 8;
    private String lineTerminator = System.lineSeparator();
    private int offset;

    CodePrinter() {
    }

    private int add_area(int n) {
        int n2 = this.ensure_area(n);
        this.offset = n2 + n;
        return n2;
    }

    private static char digit_to_hex_letter(int n) {
        int n2 = n < 10 ? n + 48 : n + 55;
        return (char)n2;
    }

    private int ensure_area(int n) {
        int n2 = this.offset;
        int n3 = n2 + n;
        char[] arrc = this.buffer;
        if (n3 > arrc.length) {
            int n4 = 2 * arrc.length;
            if (n3 > n4) {
                n4 = n3;
            }
            char[] arrc2 = new char[n4];
            System.arraycopy((Object)arrc, (int)0, (Object)arrc2, (int)0, (int)n2);
            this.buffer = arrc2;
        }
        return n2;
    }

    private int put_string_literal_char(int n, int n2, boolean bl) {
        boolean bl2 = true;
        if (n2 != 12) {
            if (n2 != 13) {
                if (n2 != 34) {
                    if (n2 != 39) {
                        switch (n2) {
                            default: {
                                bl2 = false;
                                break;
                            }
                            case 10: {
                                n2 = 110;
                                break;
                            }
                            case 9: {
                                n2 = 116;
                                break;
                            }
                            case 8: {
                                n2 = 98;
                                break;
                            }
                        }
                    } else {
                        bl2 = bl ^ true;
                    }
                } else {
                    bl2 = bl;
                }
            } else {
                n2 = 114;
            }
        } else {
            n2 = 102;
        }
        if (bl2) {
            char[] arrc = this.buffer;
            arrc[n] = 92;
            arrc[n + 1] = (char)n2;
            return n + 2;
        }
        if (32 <= n2 && n2 <= 126) {
            this.buffer[n] = (char)n2;
            return n + 1;
        }
        char[] arrc = this.buffer;
        arrc[n] = 92;
        arrc[n + 1] = 117;
        arrc[n + 2] = CodePrinter.digit_to_hex_letter(15 & n2 >> 12);
        this.buffer[n + 3] = CodePrinter.digit_to_hex_letter(15 & n2 >> 8);
        this.buffer[n + 4] = CodePrinter.digit_to_hex_letter(15 & n2 >> 4);
        this.buffer[n + 5] = CodePrinter.digit_to_hex_letter(n2 & 15);
        return n + 6;
    }

    public void clear() {
        this.offset = 0;
    }

    public void erase(int n, int n2) {
        char[] arrc = this.buffer;
        System.arraycopy((Object)arrc, (int)n2, (Object)arrc, (int)n, (int)(this.offset - n2));
        this.offset -= n2 - n;
    }

    public int getIndentStep() {
        return this.indentStep;
    }

    public int getIndentTabSize() {
        return this.indentTabSize;
    }

    public int getLastChar() {
        int n = this.offset;
        if (n == 0) {
            return -1;
        }
        return this.buffer[n - 1];
    }

    public String getLineTerminator() {
        return this.lineTerminator;
    }

    public int getOffset() {
        return this.offset;
    }

    public void indent(int n) {
        int n2;
        int n3;
        int n4;
        int n5 = n * this.indentStep;
        int n6 = this.indentTabSize;
        if (n6 <= 0) {
            n3 = n5;
            n2 = 0;
        } else {
            int n7 = n5 / n6;
            n3 = n7 + n5 % n6;
            n2 = n7;
        }
        int n8 = n4 + n2;
        int n9 = n4 + n3;
        for (n4 = this.add_area((int)n3); n4 != n8; ++n4) {
            this.buffer[n4] = 9;
        }
        while (n4 != n9) {
            this.buffer[n4] = 32;
            ++n4;
        }
    }

    public void line(int n, String string2) {
        this.indent(n);
        this.p(string2);
        this.nl();
    }

    public void nl() {
        this.p(this.getLineTerminator());
    }

    public void p(char c) {
        int n = this.add_area(1);
        this.buffer[n] = c;
    }

    public void p(int n) {
        this.p(Integer.toString((int)n));
    }

    public void p(String string2) {
        int n = string2.length();
        int n2 = this.add_area(n);
        string2.getChars(0, n, this.buffer, n2);
    }

    public final void p(char[] arrc) {
        this.p(arrc, 0, arrc.length);
    }

    public void p(char[] arrc, int n, int n2) {
        int n3 = n2 - n;
        int n4 = this.add_area(n3);
        System.arraycopy((Object)arrc, (int)n, (Object)this.buffer, (int)n4, (int)n3);
    }

    public void qchar(int n) {
        int n2 = this.ensure_area(8);
        this.buffer[n2] = 39;
        int n3 = this.put_string_literal_char(n2 + 1, n, false);
        this.buffer[n3] = 39;
        this.offset = n3 + 1;
    }

    public void qstring(String string2) {
        int n = string2.length();
        int n2 = this.ensure_area(2 + n * 6);
        this.buffer[n2] = 34;
        int n3 = n2 + 1;
        for (int i = 0; i != n; ++i) {
            n3 = this.put_string_literal_char(n3, string2.charAt(i), true);
        }
        this.buffer[n3] = 34;
        this.offset = n3 + 1;
    }

    public void setIndentStep(int n) {
        this.indentStep = n;
    }

    public void setIndentTabSize(int n) {
        this.indentTabSize = n;
    }

    public void setLineTerminator(String string2) {
        this.lineTerminator = string2;
    }

    public String toString() {
        return new String(this.buffer, 0, this.offset);
    }
}

