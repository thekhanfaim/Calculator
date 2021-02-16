/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.Reader
 *  java.io.Writer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 */
package org.mozilla.javascript.tools.idswitch;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class FileBody {
    private char[] buffer = new char[16384];
    private int bufferEnd;
    ReplaceItem firstReplace;
    ReplaceItem lastReplace;
    private int lineBegin;
    private int lineEnd;
    private int lineNumber;
    private int nextLineStart;

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

    public char[] getBuffer() {
        return this.buffer;
    }

    public int getLineBegin() {
        return this.lineBegin;
    }

    public int getLineEnd() {
        return this.lineEnd;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public boolean nextLine() {
        int n;
        int n2;
        if (this.nextLineStart == this.bufferEnd) {
            this.lineNumber = 0;
            return false;
        }
        char c = '\u0000';
        for (n2 = this.nextLineStart; n2 != (n = this.bufferEnd) && (c = this.buffer[n2]) != '\n' && c != '\r'; ++n2) {
        }
        this.lineBegin = this.nextLineStart;
        this.lineEnd = n2;
        this.nextLineStart = n2 == n ? n2 : (c == '\r' && n2 + 1 != n && this.buffer[n2 + 1] == '\n' ? n2 + 2 : n2 + 1);
        this.lineNumber = 1 + this.lineNumber;
        return true;
    }

    public void readData(Reader reader) throws IOException {
        int n = this.buffer.length;
        int n2 = 0;
        do {
            int n3;
            if ((n3 = reader.read(this.buffer, n2, n - n2)) < 0) {
                this.bufferEnd = n2;
                return;
            }
            if (n != (n2 += n3)) continue;
            char[] arrc = new char[n *= 2];
            System.arraycopy((Object)this.buffer, (int)0, (Object)arrc, (int)0, (int)n2);
            this.buffer = arrc;
        } while (true);
    }

    public boolean setReplacement(int n, int n2, String string2) {
        if (FileBody.equals(string2, this.buffer, n, n2)) {
            return false;
        }
        ReplaceItem replaceItem = new ReplaceItem(n, n2, string2);
        ReplaceItem replaceItem2 = this.firstReplace;
        if (replaceItem2 == null) {
            this.lastReplace = replaceItem;
            this.firstReplace = replaceItem;
        } else if (n < replaceItem2.begin) {
            replaceItem.next = this.firstReplace;
            this.firstReplace = replaceItem;
        } else {
            ReplaceItem replaceItem3 = this.firstReplace;
            ReplaceItem replaceItem4 = replaceItem3.next;
            while (replaceItem4 != null) {
                if (n < replaceItem4.begin) {
                    replaceItem.next = replaceItem4;
                    replaceItem3.next = replaceItem;
                    break;
                }
                replaceItem3 = replaceItem4;
                replaceItem4 = replaceItem4.next;
            }
            if (replaceItem4 == null) {
                this.lastReplace.next = replaceItem;
            }
        }
        return true;
    }

    public void startLineLoop() {
        this.lineNumber = 0;
        this.nextLineStart = 0;
        this.lineEnd = 0;
        this.lineBegin = 0;
    }

    public boolean wasModified() {
        return this.firstReplace != null;
    }

    public void writeData(Writer writer) throws IOException {
        int n = 0;
        ReplaceItem replaceItem = this.firstReplace;
        while (replaceItem != null) {
            int n2 = replaceItem.begin - n;
            if (n2 > 0) {
                writer.write(this.buffer, n, n2);
            }
            writer.write(replaceItem.replacement);
            n = replaceItem.end;
            replaceItem = replaceItem.next;
        }
        int n3 = this.bufferEnd - n;
        if (n3 != 0) {
            writer.write(this.buffer, n, n3);
        }
    }

    public void writeInitialData(Writer writer) throws IOException {
        writer.write(this.buffer, 0, this.bufferEnd);
    }

    private static class ReplaceItem {
        int begin;
        int end;
        ReplaceItem next;
        String replacement;

        ReplaceItem(int n, int n2, String string2) {
            this.begin = n;
            this.end = n2;
            this.replacement = string2;
        }
    }

}

