/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.io.InputStream
 *  java.io.PrintStream
 *  java.lang.Object
 *  java.lang.String
 *  javax.swing.JInternalFrame
 *  javax.swing.JScrollPane
 *  javax.swing.event.InternalFrameAdapter
 *  javax.swing.event.InternalFrameEvent
 *  javax.swing.event.InternalFrameListener
 *  javax.swing.text.Caret
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.Caret;
import org.mozilla.javascript.tools.shell.ConsoleTextArea;

class JSInternalConsole
extends JInternalFrame
implements ActionListener {
    private static final long serialVersionUID = -5523468828771087292L;
    ConsoleTextArea consoleTextArea;

    public JSInternalConsole(String string2) {
        ConsoleTextArea consoleTextArea;
        super(string2, true, false, true, true);
        this.consoleTextArea = consoleTextArea = new ConsoleTextArea(null);
        consoleTextArea.setRows(24);
        this.consoleTextArea.setColumns(80);
        this.setContentPane((Container)new JScrollPane((Component)this.consoleTextArea));
        this.pack();
        this.addInternalFrameListener((InternalFrameListener)new InternalFrameAdapter(){

            public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
                if (JSInternalConsole.this.consoleTextArea.hasFocus()) {
                    JSInternalConsole.this.consoleTextArea.getCaret().setVisible(false);
                    JSInternalConsole.this.consoleTextArea.getCaret().setVisible(true);
                }
            }
        });
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string2 = actionEvent.getActionCommand();
        if (string2.equals((Object)"Cut")) {
            this.consoleTextArea.cut();
            return;
        }
        if (string2.equals((Object)"Copy")) {
            this.consoleTextArea.copy();
            return;
        }
        if (string2.equals((Object)"Paste")) {
            this.consoleTextArea.paste();
        }
    }

    public PrintStream getErr() {
        return this.consoleTextArea.getErr();
    }

    public InputStream getIn() {
        return this.consoleTextArea.getIn();
    }

    public PrintStream getOut() {
        return this.consoleTextArea.getOut();
    }

}

