/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Font
 *  java.awt.event.KeyEvent
 *  java.awt.event.KeyListener
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.io.PipedInputStream
 *  java.io.PipedOutputStream
 *  java.io.PrintStream
 *  java.io.PrintWriter
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  javax.swing.JTextArea
 *  javax.swing.event.DocumentEvent
 *  javax.swing.event.DocumentListener
 *  javax.swing.text.BadLocationException
 *  javax.swing.text.Caret
 *  javax.swing.text.Document
 *  javax.swing.text.Segment
 */
package org.mozilla.javascript.tools.shell;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.mozilla.javascript.tools.shell.ConsoleWriter;

public class ConsoleTextArea
extends JTextArea
implements KeyListener,
DocumentListener {
    static final long serialVersionUID = 8557083244830872961L;
    private ConsoleWriter console1 = new ConsoleWriter(this);
    private ConsoleWriter console2 = new ConsoleWriter(this);
    private PrintStream err = new PrintStream((OutputStream)this.console2, true);
    private List<String> history = new ArrayList();
    private int historyIndex = -1;
    private PipedInputStream in;
    private PrintWriter inPipe;
    private PrintStream out = new PrintStream((OutputStream)this.console1, true);
    private int outputMark = 0;

    public ConsoleTextArea(String[] arrstring) {
        PipedInputStream pipedInputStream;
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        this.inPipe = new PrintWriter((OutputStream)pipedOutputStream);
        this.in = pipedInputStream = new PipedInputStream();
        try {
            pipedOutputStream.connect(pipedInputStream);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        this.getDocument().addDocumentListener((DocumentListener)this);
        this.addKeyListener((KeyListener)this);
        this.setLineWrap(true);
        this.setFont(new Font("Monospaced", 0, 12));
    }

    /*
     * Enabled aggressive block sorting
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void changedUpdate(DocumentEvent documentEvent) {
        ConsoleTextArea consoleTextArea = this;
        // MONITORENTER : consoleTextArea
        // MONITOREXIT : consoleTextArea
    }

    public void eval(String string2) {
        this.inPipe.write(string2);
        this.inPipe.write("\n");
        this.inPipe.flush();
        this.console1.flush();
    }

    public PrintStream getErr() {
        return this.err;
    }

    public InputStream getIn() {
        return this.in;
    }

    public PrintStream getOut() {
        return this.out;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void insertUpdate(DocumentEvent documentEvent) {
        ConsoleTextArea consoleTextArea = this;
        synchronized (consoleTextArea) {
            int n = documentEvent.getLength();
            int n2 = documentEvent.getOffset();
            int n3 = this.outputMark;
            if (n3 > n2) {
                this.outputMark = n3 + n;
            }
            return;
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        int n = keyEvent.getKeyCode();
        if (n != 8 && n != 37) {
            if (n == 36) {
                int n2;
                int n3 = this.getCaretPosition();
                if (n3 == (n2 = this.outputMark)) {
                    keyEvent.consume();
                } else if (n3 > n2 && !keyEvent.isControlDown()) {
                    if (keyEvent.isShiftDown()) {
                        this.moveCaretPosition(this.outputMark);
                    } else {
                        this.setCaretPosition(this.outputMark);
                    }
                    keyEvent.consume();
                }
                return;
            }
            if (n == 10) {
                this.returnPressed();
                keyEvent.consume();
                return;
            }
            if (n == 38) {
                int n4;
                this.historyIndex = n4 = -1 + this.historyIndex;
                if (n4 >= 0) {
                    int n5;
                    if (n4 >= this.history.size()) {
                        this.historyIndex = -1 + this.history.size();
                    }
                    if ((n5 = this.historyIndex) >= 0) {
                        String string2 = (String)this.history.get(n5);
                        int n6 = this.getDocument().getLength();
                        this.replaceRange(string2, this.outputMark, n6);
                        int n7 = this.outputMark + string2.length();
                        this.select(n7, n7);
                    } else {
                        this.historyIndex = n5 + 1;
                    }
                } else {
                    this.historyIndex = n4 + 1;
                }
                keyEvent.consume();
                return;
            }
            if (n == 40) {
                int n8 = this.outputMark;
                if (this.history.size() > 0) {
                    int n9;
                    this.historyIndex = n9 = 1 + this.historyIndex;
                    if (n9 < 0) {
                        this.historyIndex = 0;
                    }
                    int n10 = this.getDocument().getLength();
                    if (this.historyIndex < this.history.size()) {
                        String string3 = (String)this.history.get(this.historyIndex);
                        this.replaceRange(string3, this.outputMark, n10);
                        n8 = this.outputMark + string3.length();
                    } else {
                        this.historyIndex = this.history.size();
                        this.replaceRange("", this.outputMark, n10);
                    }
                }
                this.select(n8, n8);
                keyEvent.consume();
                return;
            }
        } else if (this.outputMark == this.getCaretPosition()) {
            keyEvent.consume();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void keyReleased(KeyEvent keyEvent) {
        ConsoleTextArea consoleTextArea = this;
        // MONITORENTER : consoleTextArea
        // MONITOREXIT : consoleTextArea
    }

    public void keyTyped(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == '\b') {
            if (this.outputMark == this.getCaretPosition()) {
                keyEvent.consume();
                return;
            }
        } else {
            int n;
            int n2 = this.getCaretPosition();
            if (n2 < (n = this.outputMark)) {
                this.setCaretPosition(n);
            }
        }
    }

    public void postUpdateUI() {
        ConsoleTextArea consoleTextArea = this;
        synchronized (consoleTextArea) {
            this.requestFocus();
            this.setCaret(this.getCaret());
            int n = this.outputMark;
            this.select(n, n);
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void removeUpdate(DocumentEvent documentEvent) {
        ConsoleTextArea consoleTextArea = this;
        synchronized (consoleTextArea) {
            int n = documentEvent.getLength();
            int n2 = documentEvent.getOffset();
            int n3 = this.outputMark;
            if (n3 > n2) {
                this.outputMark = n3 >= n2 + n ? n3 - n : n2;
            }
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void returnPressed() {
        ConsoleTextArea consoleTextArea = this;
        synchronized (consoleTextArea) {
            Document document = this.getDocument();
            int n = document.getLength();
            Segment segment = new Segment();
            try {
                int n2 = this.outputMark;
                document.getText(n2, n - n2, segment);
            }
            catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            if (segment.count > 0) {
                this.history.add((Object)segment.toString());
            }
            this.historyIndex = this.history.size();
            this.inPipe.write(segment.array, segment.offset, segment.count);
            this.append("\n");
            this.outputMark = document.getLength();
            this.inPipe.write("\n");
            this.inPipe.flush();
            this.console1.flush();
            return;
        }
    }

    public void select(int n, int n2) {
        this.requestFocus();
        super.select(n, n2);
    }

    public void write(String string2) {
        ConsoleTextArea consoleTextArea = this;
        synchronized (consoleTextArea) {
            int n;
            this.insert(string2, this.outputMark);
            this.outputMark = n = string2.length() + this.outputMark;
            this.select(n, n);
            return;
        }
    }
}

