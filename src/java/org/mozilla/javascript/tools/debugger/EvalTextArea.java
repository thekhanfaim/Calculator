/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Font
 *  java.awt.event.KeyEvent
 *  java.awt.event.KeyListener
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  javax.swing.JTextArea
 *  javax.swing.event.DocumentEvent
 *  javax.swing.event.DocumentListener
 *  javax.swing.text.BadLocationException
 *  javax.swing.text.Caret
 *  javax.swing.text.Document
 *  javax.swing.text.Segment
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.SwingGui;

class EvalTextArea
extends JTextArea
implements KeyListener,
DocumentListener {
    private static final long serialVersionUID = -3918033649601064194L;
    private SwingGui debugGui;
    private List<String> history;
    private int historyIndex = -1;
    private int outputMark;

    public EvalTextArea(SwingGui swingGui) {
        this.debugGui = swingGui;
        this.history = Collections.synchronizedList((List)new ArrayList());
        Document document = this.getDocument();
        document.addDocumentListener((DocumentListener)this);
        this.addKeyListener((KeyListener)this);
        this.setLineWrap(true);
        this.setFont(new Font("Monospaced", 0, 12));
        this.append("% ");
        this.outputMark = document.getLength();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void returnPressed() {
        EvalTextArea evalTextArea = this;
        synchronized (evalTextArea) {
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
            String string2 = segment.toString();
            if (this.debugGui.dim.stringIsCompilableUnit(string2)) {
                if (string2.trim().length() > 0) {
                    this.history.add((Object)string2);
                    this.historyIndex = this.history.size();
                }
                this.append("\n");
                String string3 = this.debugGui.dim.eval(string2);
                if (string3.length() > 0) {
                    this.append(string3);
                    this.append("\n");
                }
                this.append("% ");
                this.outputMark = document.getLength();
            } else {
                this.append("\n");
            }
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public void changedUpdate(DocumentEvent documentEvent) {
        EvalTextArea evalTextArea = this;
        // MONITORENTER : evalTextArea
        // MONITOREXIT : evalTextArea
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void insertUpdate(DocumentEvent documentEvent) {
        EvalTextArea evalTextArea = this;
        synchronized (evalTextArea) {
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
        EvalTextArea evalTextArea = this;
        // MONITORENTER : evalTextArea
        // MONITOREXIT : evalTextArea
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
        EvalTextArea evalTextArea = this;
        synchronized (evalTextArea) {
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
        EvalTextArea evalTextArea = this;
        synchronized (evalTextArea) {
            int n = documentEvent.getLength();
            int n2 = documentEvent.getOffset();
            int n3 = this.outputMark;
            if (n3 > n2) {
                this.outputMark = n3 >= n2 + n ? n3 - n : n2;
            }
            return;
        }
    }

    public void select(int n, int n2) {
        super.select(n, n2);
    }

    public void write(String string2) {
        EvalTextArea evalTextArea = this;
        synchronized (evalTextArea) {
            int n;
            this.insert(string2, this.outputMark);
            this.outputMark = n = string2.length() + this.outputMark;
            this.select(n, n);
            return;
        }
    }
}

