/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.awt.event.WindowAdapter
 *  java.awt.event.WindowEvent
 *  java.awt.event.WindowListener
 *  java.io.File
 *  java.io.InputStream
 *  java.io.PrintStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  javax.swing.AbstractButton
 *  javax.swing.ButtonGroup
 *  javax.swing.JFileChooser
 *  javax.swing.JFrame
 *  javax.swing.JMenu
 *  javax.swing.JMenuBar
 *  javax.swing.JMenuItem
 *  javax.swing.JOptionPane
 *  javax.swing.JRadioButtonMenuItem
 *  javax.swing.JScrollPane
 *  javax.swing.SwingUtilities
 *  javax.swing.UIManager
 *  javax.swing.filechooser.FileFilter
 */
package org.mozilla.javascript.tools.shell;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.tools.shell.ConsoleTextArea;
import org.mozilla.javascript.tools.shell.Main;

public class JSConsole
extends JFrame
implements ActionListener {
    static final long serialVersionUID = 2551225560631876300L;
    private File CWD;
    private ConsoleTextArea consoleTextArea;
    private JFileChooser dlg;

    public JSConsole(String[] arrstring) {
        super("Rhino JavaScript Console");
        JMenuBar jMenuBar = new JMenuBar();
        this.createFileChooser();
        String[] arrstring2 = new String[]{"Load...", "Exit"};
        String[] arrstring3 = new String[]{"Load", "Exit"};
        char[] arrc = new char[]{'L', 'X'};
        String[] arrstring4 = new String[]{"Cut", "Copy", "Paste"};
        char[] arrc2 = new char[]{'T', 'C', 'P'};
        String[] arrstring5 = new String[]{"Metal", "Windows", "Motif"};
        boolean[] arrbl = new boolean[]{true, false, false};
        JMenu jMenu = new JMenu("File");
        jMenu.setMnemonic('F');
        JMenu jMenu2 = new JMenu("Edit");
        jMenu2.setMnemonic('E');
        JMenu jMenu3 = new JMenu("Platform");
        jMenu3.setMnemonic('P');
        for (int i = 0; i < arrstring2.length; ++i) {
            JMenuItem jMenuItem = new JMenuItem(arrstring2[i], (int)arrc[i]);
            jMenuItem.setActionCommand(arrstring3[i]);
            jMenuItem.addActionListener((ActionListener)this);
            jMenu.add(jMenuItem);
        }
        for (int i = 0; i < arrstring4.length; ++i) {
            JMenuItem jMenuItem = new JMenuItem(arrstring4[i], (int)arrc2[i]);
            jMenuItem.addActionListener((ActionListener)this);
            jMenu2.add(jMenuItem);
        }
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < arrstring5.length; ++i) {
            String string2 = arrstring5[i];
            String[] arrstring6 = arrstring5;
            JRadioButtonMenuItem jRadioButtonMenuItem = new JRadioButtonMenuItem(string2, arrbl[i]);
            buttonGroup.add((AbstractButton)jRadioButtonMenuItem);
            jRadioButtonMenuItem.addActionListener((ActionListener)this);
            jMenu3.add((JMenuItem)jRadioButtonMenuItem);
            arrstring5 = arrstring6;
        }
        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu2);
        jMenuBar.add(jMenu3);
        this.setJMenuBar(jMenuBar);
        this.consoleTextArea = new ConsoleTextArea(arrstring);
        this.setContentPane((Container)new JScrollPane((Component)this.consoleTextArea));
        this.consoleTextArea.setRows(24);
        this.consoleTextArea.setColumns(80);
        this.addWindowListener((WindowListener)new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                System.exit((int)0);
            }
        });
        this.pack();
        this.setVisible(true);
        Main.setIn(this.consoleTextArea.getIn());
        Main.setOut(this.consoleTextArea.getOut());
        Main.setErr(this.consoleTextArea.getErr());
        Main.main(arrstring);
    }

    public static void main(String[] arrstring) {
        new JSConsole(arrstring);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string2;
        String string3 = actionEvent.getActionCommand();
        if (string3.equals((Object)"Load")) {
            String string4 = this.chooseFile();
            if (string4 != null) {
                String string5 = string4.replace('\\', '/');
                ConsoleTextArea consoleTextArea = this.consoleTextArea;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("load(\"");
                stringBuilder.append(string5);
                stringBuilder.append("\");");
                consoleTextArea.eval(stringBuilder.toString());
            }
            return;
        }
        if (string3.equals((Object)"Exit")) {
            System.exit((int)0);
            return;
        }
        if (string3.equals((Object)"Cut")) {
            this.consoleTextArea.cut();
            return;
        }
        if (string3.equals((Object)"Copy")) {
            this.consoleTextArea.copy();
            return;
        }
        if (string3.equals((Object)"Paste")) {
            this.consoleTextArea.paste();
            return;
        }
        if (string3.equals((Object)"Metal")) {
            string2 = "javax.swing.plaf.metal.MetalLookAndFeel";
        } else if (string3.equals((Object)"Windows")) {
            string2 = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        } else {
            boolean bl = string3.equals((Object)"Motif");
            string2 = null;
            if (bl) {
                string2 = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            }
        }
        if (string2 != null) {
            try {
                UIManager.setLookAndFeel((String)string2);
                SwingUtilities.updateComponentTreeUI((Component)this);
                this.consoleTextArea.postUpdateUI();
                this.createFileChooser();
                return;
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog((Component)this, (Object)exception.getMessage(), (String)"Platform", (int)0);
            }
        }
    }

    public String chooseFile() {
        File file;
        String string2;
        if (this.CWD == null && (string2 = SecurityUtilities.getSystemProperty("user.dir")) != null) {
            this.CWD = new File(string2);
        }
        if ((file = this.CWD) != null) {
            this.dlg.setCurrentDirectory(file);
        }
        this.dlg.setDialogTitle("Select a file to load");
        if (this.dlg.showOpenDialog((Component)this) == 0) {
            String string3 = this.dlg.getSelectedFile().getPath();
            this.CWD = new File(this.dlg.getSelectedFile().getParent());
            return string3;
        }
        return null;
    }

    public void createFileChooser() {
        this.dlg = new JFileChooser();
        FileFilter fileFilter = new FileFilter(){

            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                String string2 = file.getName();
                int n = string2.lastIndexOf(46);
                return n > 0 && n < string2.length() - 1 && string2.substring(n + 1).toLowerCase().equals((Object)"js");
            }

            public String getDescription() {
                return "JavaScript Files (*.js)";
            }
        };
        this.dlg.addChoosableFileFilter(fileFilter);
    }

}

