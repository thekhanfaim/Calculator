/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  javax.swing.JCheckBoxMenuItem
 *  javax.swing.JFileChooser
 *  javax.swing.JMenu
 *  javax.swing.JMenuBar
 *  javax.swing.JMenuItem
 *  javax.swing.KeyStroke
 *  javax.swing.SwingUtilities
 *  javax.swing.UIManager
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.SwingGui;

class Menubar
extends JMenuBar
implements ActionListener {
    private static final long serialVersionUID = 3217170497245911461L;
    private JCheckBoxMenuItem breakOnEnter;
    private JCheckBoxMenuItem breakOnExceptions;
    private JCheckBoxMenuItem breakOnReturn;
    private SwingGui debugGui;
    private List<JMenuItem> interruptOnlyItems = Collections.synchronizedList((List)new ArrayList());
    private List<JMenuItem> runOnlyItems = Collections.synchronizedList((List)new ArrayList());
    private JMenu windowMenu;

    Menubar(SwingGui swingGui) {
        JCheckBoxMenuItem jCheckBoxMenuItem;
        JMenu jMenu;
        JCheckBoxMenuItem jCheckBoxMenuItem2;
        JCheckBoxMenuItem jCheckBoxMenuItem3;
        String[] arrstring;
        this.debugGui = swingGui;
        String[] arrstring2 = new String[]{"Open...", "Run...", "", "Exit"};
        String[] arrstring3 = new String[]{"Open", "Load", "", "Exit"};
        char[] arrc = new char[]{'0', 'N', '\u0000', 'X'};
        int[] arrn = new int[]{79, 78, 0, 81};
        String[] arrstring4 = new String[]{"Cut", "Copy", "Paste", "Go to function...", "Go to line..."};
        char[] arrc2 = new char[]{'T', 'C', 'P', 'F', 'L'};
        int[] arrn2 = new int[]{0, 0, 0, 0, 76};
        String[] arrstring5 = new String[]{"Break", "Go", "Step Into", "Step Over", "Step Out"};
        char[] arrc3 = new char[]{'B', 'G', 'I', 'O', 'T'};
        String[] arrstring6 = new String[]{"Metal", "Windows", "Motif"};
        char[] arrc4 = new char[]{'M', 'W', 'F'};
        int[] arrn3 = new int[]{19, 116, 122, 118, 119, 0, 0};
        JMenu jMenu2 = new JMenu("File");
        jMenu2.setMnemonic('F');
        JMenu jMenu3 = new JMenu("Edit");
        jMenu3.setMnemonic('E');
        JMenu jMenu4 = new JMenu("Platform");
        jMenu4.setMnemonic('P');
        JMenu jMenu5 = new JMenu("Debug");
        jMenu5.setMnemonic('D');
        this.windowMenu = jMenu = new JMenu("Window");
        jMenu.setMnemonic('W');
        for (int i = 0; i < arrstring2.length; ++i) {
            String[] arrstring7;
            String[] arrstring8;
            if (arrstring2[i].length() == 0) {
                jMenu2.addSeparator();
                arrstring8 = arrstring5;
                arrstring7 = arrstring2;
            } else {
                arrstring8 = arrstring5;
                String string2 = arrstring2[i];
                arrstring7 = arrstring2;
                JMenuItem jMenuItem = new JMenuItem(string2, (int)arrc[i]);
                jMenuItem.setActionCommand(arrstring3[i]);
                jMenuItem.addActionListener((ActionListener)this);
                jMenu2.add(jMenuItem);
                if (arrn[i] != 0) {
                    jMenuItem.setAccelerator(KeyStroke.getKeyStroke((int)arrn[i], (int)2));
                }
            }
            arrstring5 = arrstring8;
            arrstring2 = arrstring7;
        }
        String[] arrstring9 = arrstring5;
        for (int i = 0; i < arrstring4.length; ++i) {
            JMenuItem jMenuItem = new JMenuItem(arrstring4[i], (int)arrc2[i]);
            jMenuItem.addActionListener((ActionListener)this);
            jMenu3.add(jMenuItem);
            if (arrn2[i] == 0) continue;
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke((int)arrn2[i], (int)2));
        }
        for (int i = 0; i < arrstring6.length; ++i) {
            JMenuItem jMenuItem = new JMenuItem(arrstring6[i], (int)arrc4[i]);
            jMenuItem.addActionListener((ActionListener)this);
            jMenu4.add(jMenuItem);
        }
        for (int i = 0; i < (arrstring = arrstring9).length; ++i) {
            String string3 = arrstring[i];
            arrstring9 = arrstring;
            JMenuItem jMenuItem = new JMenuItem(string3, (int)arrc3[i]);
            jMenuItem.addActionListener((ActionListener)this);
            if (arrn3[i] != 0) {
                jMenuItem.setAccelerator(KeyStroke.getKeyStroke((int)arrn3[i], (int)0));
            }
            if (i != 0) {
                this.interruptOnlyItems.add((Object)jMenuItem);
            } else {
                this.runOnlyItems.add((Object)jMenuItem);
            }
            jMenu5.add(jMenuItem);
        }
        this.breakOnExceptions = jCheckBoxMenuItem3 = new JCheckBoxMenuItem("Break on Exceptions");
        jCheckBoxMenuItem3.setMnemonic('X');
        this.breakOnExceptions.addActionListener((ActionListener)this);
        this.breakOnExceptions.setSelected(false);
        jMenu5.add((JMenuItem)this.breakOnExceptions);
        this.breakOnEnter = jCheckBoxMenuItem = new JCheckBoxMenuItem("Break on Function Enter");
        jCheckBoxMenuItem.setMnemonic('E');
        this.breakOnEnter.addActionListener((ActionListener)this);
        this.breakOnEnter.setSelected(false);
        jMenu5.add((JMenuItem)this.breakOnEnter);
        this.breakOnReturn = jCheckBoxMenuItem2 = new JCheckBoxMenuItem("Break on Function Return");
        jCheckBoxMenuItem2.setMnemonic('R');
        this.breakOnReturn.addActionListener((ActionListener)this);
        this.breakOnReturn.setSelected(false);
        jMenu5.add((JMenuItem)this.breakOnReturn);
        this.add(jMenu2);
        this.add(jMenu3);
        this.add(jMenu5);
        JMenu jMenu6 = this.windowMenu;
        JMenuItem jMenuItem = new JMenuItem("Cascade", 65);
        jMenu6.add(jMenuItem);
        jMenuItem.addActionListener((ActionListener)this);
        JMenu jMenu7 = this.windowMenu;
        JMenuItem jMenuItem2 = new JMenuItem("Tile", 84);
        jMenu7.add(jMenuItem2);
        jMenuItem2.addActionListener((ActionListener)this);
        this.windowMenu.addSeparator();
        JMenu jMenu8 = this.windowMenu;
        JMenuItem jMenuItem3 = new JMenuItem("Console", 67);
        jMenu8.add(jMenuItem3);
        jMenuItem3.addActionListener((ActionListener)this);
        this.add(this.windowMenu);
        this.updateEnabled(false);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        block11 : {
            String string2;
            block9 : {
                String string3;
                block10 : {
                    block8 : {
                        string3 = actionEvent.getActionCommand();
                        if (!string3.equals((Object)"Metal")) break block8;
                        string2 = "javax.swing.plaf.metal.MetalLookAndFeel";
                        break block9;
                    }
                    if (!string3.equals((Object)"Windows")) break block10;
                    string2 = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
                    break block9;
                }
                if (!string3.equals((Object)"Motif")) break block11;
                string2 = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            }
            try {
                UIManager.setLookAndFeel((String)string2);
                SwingUtilities.updateComponentTreeUI((Component)this.debugGui);
                SwingUtilities.updateComponentTreeUI((Component)this.debugGui.dlg);
                return;
            }
            catch (Exception exception) {
                return;
            }
        }
        Object object = actionEvent.getSource();
        if (object == this.breakOnExceptions) {
            this.debugGui.dim.setBreakOnExceptions(this.breakOnExceptions.isSelected());
            return;
        }
        if (object == this.breakOnEnter) {
            this.debugGui.dim.setBreakOnEnter(this.breakOnEnter.isSelected());
            return;
        }
        if (object == this.breakOnReturn) {
            this.debugGui.dim.setBreakOnReturn(this.breakOnReturn.isSelected());
            return;
        }
        this.debugGui.actionPerformed(actionEvent);
    }

    public void addFile(String string2) {
        int n = this.windowMenu.getItemCount();
        if (n == 4) {
            this.windowMenu.addSeparator();
            ++n;
        }
        JMenuItem jMenuItem = this.windowMenu.getItem(n - 1);
        int n2 = 5;
        boolean bl = false;
        if (jMenuItem != null) {
            boolean bl2 = jMenuItem.getText().equals((Object)"More Windows...");
            bl = false;
            if (bl2) {
                bl = true;
                ++n2;
            }
        }
        if (!bl && n - 4 == 5) {
            JMenu jMenu = this.windowMenu;
            JMenuItem jMenuItem2 = new JMenuItem("More Windows...", 77);
            jMenu.add(jMenuItem2);
            jMenuItem2.setActionCommand("More Windows...");
            jMenuItem2.addActionListener((ActionListener)this);
            return;
        }
        if (n - 4 <= n2) {
            if (bl) {
                --n;
                this.windowMenu.remove(jMenuItem);
            }
            String string3 = SwingGui.getShortName(string2);
            JMenu jMenu = this.windowMenu;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((char)(48 + (n - 4)));
            stringBuilder.append(" ");
            stringBuilder.append(string3);
            JMenuItem jMenuItem3 = new JMenuItem(stringBuilder.toString(), 48 + (n - 4));
            jMenu.add(jMenuItem3);
            if (bl) {
                this.windowMenu.add(jMenuItem);
            }
            jMenuItem3.setActionCommand(string2);
            jMenuItem3.addActionListener((ActionListener)this);
            return;
        }
    }

    public JCheckBoxMenuItem getBreakOnEnter() {
        return this.breakOnEnter;
    }

    public JCheckBoxMenuItem getBreakOnExceptions() {
        return this.breakOnExceptions;
    }

    public JCheckBoxMenuItem getBreakOnReturn() {
        return this.breakOnReturn;
    }

    public JMenu getDebugMenu() {
        return this.getMenu(2);
    }

    public void updateEnabled(boolean bl) {
        for (int i = 0; i != this.interruptOnlyItems.size(); ++i) {
            ((JMenuItem)this.interruptOnlyItems.get(i)).setEnabled(bl);
        }
        for (int i = 0; i != this.runOnlyItems.size(); ++i) {
            ((JMenuItem)this.runOnlyItems.get(i)).setEnabled(bl ^ true);
        }
    }
}

