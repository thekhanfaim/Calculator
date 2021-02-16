/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.lang.Object
 *  java.lang.String
 *  javax.swing.JInternalFrame
 *  javax.swing.JScrollPane
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import org.mozilla.javascript.tools.debugger.EvalTextArea;
import org.mozilla.javascript.tools.debugger.SwingGui;

class EvalWindow
extends JInternalFrame
implements ActionListener {
    private static final long serialVersionUID = -2860585845212160176L;
    private EvalTextArea evalTextArea;

    public EvalWindow(String string2, SwingGui swingGui) {
        EvalTextArea evalTextArea;
        super(string2, true, false, true, true);
        this.evalTextArea = evalTextArea = new EvalTextArea(swingGui);
        evalTextArea.setRows(24);
        this.evalTextArea.setColumns(80);
        this.setContentPane((Container)new JScrollPane((Component)this.evalTextArea));
        this.pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string2 = actionEvent.getActionCommand();
        if (string2.equals((Object)"Cut")) {
            this.evalTextArea.cut();
            return;
        }
        if (string2.equals((Object)"Copy")) {
            this.evalTextArea.copy();
            return;
        }
        if (string2.equals((Object)"Paste")) {
            this.evalTextArea.paste();
        }
    }

    public void setEnabled(boolean bl) {
        super.setEnabled(bl);
        this.evalTextArea.setEnabled(bl);
    }
}

