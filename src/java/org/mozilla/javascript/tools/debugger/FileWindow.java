/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  javax.swing.JComponent
 *  javax.swing.JInternalFrame
 *  javax.swing.JScrollPane
 *  javax.swing.text.BadLocationException
 *  javax.swing.text.Document
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.FileHeader;
import org.mozilla.javascript.tools.debugger.FileTextArea;
import org.mozilla.javascript.tools.debugger.RunProxy;
import org.mozilla.javascript.tools.debugger.SwingGui;

class FileWindow
extends JInternalFrame
implements ActionListener {
    private static final long serialVersionUID = -6212382604952082370L;
    int currentPos;
    private SwingGui debugGui;
    private FileHeader fileHeader;
    private JScrollPane p;
    private Dim.SourceInfo sourceInfo;
    FileTextArea textArea;

    public FileWindow(SwingGui swingGui, Dim.SourceInfo sourceInfo) {
        FileTextArea fileTextArea;
        super(SwingGui.getShortName(sourceInfo.url()), true, true, true, true);
        this.debugGui = swingGui;
        this.sourceInfo = sourceInfo;
        this.updateToolTip();
        this.currentPos = -1;
        this.textArea = fileTextArea = new FileTextArea(this);
        fileTextArea.setRows(24);
        this.textArea.setColumns(80);
        this.p = new JScrollPane();
        this.fileHeader = new FileHeader(this);
        this.p.setViewportView((Component)this.textArea);
        this.p.setRowHeaderView((Component)this.fileHeader);
        this.setContentPane((Container)this.p);
        this.pack();
        this.updateText(sourceInfo);
        this.textArea.select(0);
    }

    private void updateToolTip() {
        int n = this.getComponentCount() - 1;
        if (n > 1) {
            n = 1;
        } else if (n < 0) {
            return;
        }
        Component component = this.getComponent(n);
        if (component != null && component instanceof JComponent) {
            ((JComponent)component).setToolTipText(this.getUrl());
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string2 = actionEvent.getActionCommand();
        if (string2.equals((Object)"Cut")) {
            return;
        }
        if (string2.equals((Object)"Copy")) {
            this.textArea.copy();
            return;
        }
        string2.equals((Object)"Paste");
    }

    public void clearBreakPoint(int n) {
        if (this.sourceInfo.breakableLine(n) && this.sourceInfo.breakpoint(n, false)) {
            this.fileHeader.repaint();
        }
    }

    public void dispose() {
        this.debugGui.removeWindow(this);
        super.dispose();
    }

    public int getPosition(int n) {
        try {
            int n2 = this.textArea.getLineStartOffset(n);
            return n2;
        }
        catch (BadLocationException badLocationException) {
            return -1;
        }
    }

    public String getUrl() {
        return this.sourceInfo.url();
    }

    public boolean isBreakPoint(int n) {
        return this.sourceInfo.breakableLine(n) && this.sourceInfo.breakpoint(n);
    }

    void load() {
        String string2 = this.getUrl();
        if (string2 != null) {
            RunProxy runProxy = new RunProxy(this.debugGui, 2);
            runProxy.fileName = string2;
            runProxy.text = this.sourceInfo.source();
            new Thread((Runnable)runProxy).start();
        }
    }

    public void select(int n, int n2) {
        int n3 = this.textArea.getDocument().getLength();
        this.textArea.select(n3, n3);
        this.textArea.select(n, n2);
    }

    public void setBreakPoint(int n) {
        if (this.sourceInfo.breakableLine(n) && this.sourceInfo.breakpoint(n, true)) {
            this.fileHeader.repaint();
        }
    }

    public void setPosition(int n) {
        this.textArea.select(n);
        this.currentPos = n;
        this.fileHeader.repaint();
    }

    public void toggleBreakPoint(int n) {
        if (!this.isBreakPoint(n)) {
            this.setBreakPoint(n);
            return;
        }
        this.clearBreakPoint(n);
    }

    public void updateText(Dim.SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
        String string2 = sourceInfo.source();
        if (!this.textArea.getText().equals((Object)string2)) {
            this.textArea.setText(string2);
            int n = this.currentPos;
            int n2 = 0;
            if (n != -1) {
                n2 = this.currentPos;
            }
            this.textArea.select(n2);
        }
        this.fileHeader.update();
        this.fileHeader.repaint();
    }
}

