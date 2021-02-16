/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.event.ActionListener
 *  java.lang.String
 *  javax.swing.JComponent
 *  javax.swing.JMenuItem
 *  javax.swing.JPopupMenu
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.mozilla.javascript.tools.debugger.FileTextArea;

class FilePopupMenu
extends JPopupMenu {
    private static final long serialVersionUID = 3589525009546013565L;
    int x;
    int y;

    public FilePopupMenu(FileTextArea fileTextArea) {
        JMenuItem jMenuItem = new JMenuItem("Set Breakpoint");
        this.add(jMenuItem);
        jMenuItem.addActionListener((ActionListener)fileTextArea);
        JMenuItem jMenuItem2 = new JMenuItem("Clear Breakpoint");
        this.add(jMenuItem2);
        jMenuItem2.addActionListener((ActionListener)fileTextArea);
        JMenuItem jMenuItem3 = new JMenuItem("Run");
        this.add(jMenuItem3);
        jMenuItem3.addActionListener((ActionListener)fileTextArea);
    }

    public void show(JComponent jComponent, int n, int n2) {
        this.x = n;
        this.y = n2;
        super.show((Component)jComponent, n, n2);
    }
}

