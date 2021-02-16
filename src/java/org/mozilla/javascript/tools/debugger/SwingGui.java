/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.AWTEvent
 *  java.awt.ActiveEvent
 *  java.awt.BorderLayout
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.Dimension
 *  java.awt.EventQueue
 *  java.awt.LayoutManager
 *  java.awt.MenuComponent
 *  java.awt.Toolkit
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.awt.event.WindowAdapter
 *  java.awt.event.WindowEvent
 *  java.awt.event.WindowListener
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.IllegalAccessException
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Math
 *  java.lang.NoSuchMethodException
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.SecurityException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.reflect.InvocationTargetException
 *  java.lang.reflect.Method
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Properties
 *  java.util.TreeMap
 *  javax.swing.DesktopManager
 *  javax.swing.JButton
 *  javax.swing.JComboBox
 *  javax.swing.JComponent
 *  javax.swing.JDesktopPane
 *  javax.swing.JFileChooser
 *  javax.swing.JFrame
 *  javax.swing.JInternalFrame
 *  javax.swing.JLabel
 *  javax.swing.JMenu
 *  javax.swing.JMenuBar
 *  javax.swing.JMenuItem
 *  javax.swing.JOptionPane
 *  javax.swing.JPanel
 *  javax.swing.JSplitPane
 *  javax.swing.JToolBar
 *  javax.swing.SwingUtilities
 *  javax.swing.filechooser.FileFilter
 *  javax.swing.text.BadLocationException
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.DesktopManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.tools.debugger.ContextWindow;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.FileTextArea;
import org.mozilla.javascript.tools.debugger.FileWindow;
import org.mozilla.javascript.tools.debugger.FindFunction;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.JSInternalConsole;
import org.mozilla.javascript.tools.debugger.Menubar;
import org.mozilla.javascript.tools.debugger.MessageDialogWrapper;
import org.mozilla.javascript.tools.debugger.MoreWindows;
import org.mozilla.javascript.tools.debugger.RunProxy;
import org.mozilla.javascript.tools.shell.ConsoleTextArea;

public class SwingGui
extends JFrame
implements GuiCallback {
    private static final long serialVersionUID = -8217029773456711621L;
    private EventQueue awtEventQueue;
    private JSInternalConsole console;
    private ContextWindow context;
    private FileWindow currentWindow;
    private JDesktopPane desk;
    Dim dim;
    JFileChooser dlg;
    private Runnable exitAction;
    private final Map<String, FileWindow> fileWindows = Collections.synchronizedMap((Map)new TreeMap());
    private Menubar menubar;
    private JSplitPane split1;
    private JLabel statusBar;
    private JToolBar toolBar;
    private final Map<String, JFrame> toplevels = Collections.synchronizedMap((Map)new HashMap());

    public SwingGui(Dim dim, String string2) {
        super(string2);
        this.dim = dim;
        this.init();
        dim.setGuiCallback(this);
    }

    private String chooseFile(String string2) {
        this.dlg.setDialogTitle(string2);
        String string3 = SecurityUtilities.getSystemProperty("user.dir");
        File file = null;
        if (string3 != null) {
            file = new File(string3);
        }
        if (file != null) {
            this.dlg.setCurrentDirectory(file);
        }
        if (this.dlg.showOpenDialog((Component)this) == 0) {
            try {
                String string4 = this.dlg.getSelectedFile().getCanonicalPath();
                File file2 = this.dlg.getSelectedFile().getParentFile();
                Properties properties = System.getProperties();
                properties.put((Object)"user.dir", (Object)file2.getPath());
                System.setProperties((Properties)properties);
                return string4;
            }
            catch (SecurityException securityException) {
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return null;
    }

    private void exit() {
        Runnable runnable = this.exitAction;
        if (runnable != null) {
            SwingUtilities.invokeLater((Runnable)runnable);
        }
        this.dim.setReturnValue(5);
    }

    private JInternalFrame getSelectedFrame() {
        JInternalFrame[] arrjInternalFrame = this.desk.getAllFrames();
        for (int i = 0; i < arrjInternalFrame.length; ++i) {
            if (!arrjInternalFrame[i].isShowing()) continue;
            return arrjInternalFrame[i];
        }
        return arrjInternalFrame[-1 + arrjInternalFrame.length];
    }

    static String getShortName(String string2) {
        int n = string2.lastIndexOf(47);
        if (n < 0) {
            n = string2.lastIndexOf(92);
        }
        String string3 = string2;
        if (n >= 0 && n + 1 < string2.length()) {
            string3 = string2.substring(n + 1);
        }
        return string3;
    }

    private JMenu getWindowMenu() {
        return this.menubar.getMenu(3);
    }

    private void init() {
        JDesktopPane jDesktopPane;
        JSInternalConsole jSInternalConsole;
        ContextWindow contextWindow;
        JSplitPane jSplitPane;
        Menubar menubar;
        JLabel jLabel;
        this.menubar = menubar = new Menubar(this);
        this.setJMenuBar((JMenuBar)menubar);
        this.toolBar = new JToolBar();
        String[] arrstring = new String[]{"Break (Pause)", "Go (F5)", "Step Into (F11)", "Step Over (F7)", "Step Out (F8)"};
        JButton jButton = new JButton("Break");
        jButton.setToolTipText("Break");
        jButton.setActionCommand("Break");
        jButton.addActionListener((ActionListener)this.menubar);
        jButton.setEnabled(true);
        int n = 0 + 1;
        jButton.setToolTipText(arrstring[0]);
        JButton jButton2 = new JButton("Go");
        jButton2.setToolTipText("Go");
        jButton2.setActionCommand("Go");
        jButton2.addActionListener((ActionListener)this.menubar);
        jButton2.setEnabled(false);
        int n2 = n + 1;
        jButton2.setToolTipText(arrstring[n]);
        JButton jButton3 = new JButton("Step Into");
        jButton3.setToolTipText("Step Into");
        jButton3.setActionCommand("Step Into");
        jButton3.addActionListener((ActionListener)this.menubar);
        jButton3.setEnabled(false);
        int n3 = n2 + 1;
        jButton3.setToolTipText(arrstring[n2]);
        JButton jButton4 = new JButton("Step Over");
        jButton4.setToolTipText("Step Over");
        jButton4.setActionCommand("Step Over");
        jButton4.setEnabled(false);
        jButton4.addActionListener((ActionListener)this.menubar);
        int n4 = n3 + 1;
        jButton4.setToolTipText(arrstring[n3]);
        JButton jButton5 = new JButton("Step Out");
        jButton5.setToolTipText("Step Out");
        jButton5.setActionCommand("Step Out");
        jButton5.setEnabled(false);
        jButton5.addActionListener((ActionListener)this.menubar);
        n4 + 1;
        jButton5.setToolTipText(arrstring[n4]);
        Dimension dimension = jButton4.getPreferredSize();
        jButton.setPreferredSize(dimension);
        jButton.setMinimumSize(dimension);
        jButton.setMaximumSize(dimension);
        jButton.setSize(dimension);
        jButton2.setPreferredSize(dimension);
        jButton2.setMinimumSize(dimension);
        jButton2.setMaximumSize(dimension);
        jButton3.setPreferredSize(dimension);
        jButton3.setMinimumSize(dimension);
        jButton3.setMaximumSize(dimension);
        jButton4.setPreferredSize(dimension);
        jButton4.setMinimumSize(dimension);
        jButton4.setMaximumSize(dimension);
        jButton5.setPreferredSize(dimension);
        jButton5.setMinimumSize(dimension);
        jButton5.setMaximumSize(dimension);
        this.toolBar.add((Component)jButton);
        this.toolBar.add((Component)jButton2);
        this.toolBar.add((Component)jButton3);
        this.toolBar.add((Component)jButton4);
        this.toolBar.add((Component)jButton5);
        JPanel jPanel = new JPanel();
        jPanel.setLayout((LayoutManager)new BorderLayout());
        this.getContentPane().add((Component)this.toolBar, (Object)"North");
        this.getContentPane().add((Component)jPanel, (Object)"Center");
        this.desk = jDesktopPane = new JDesktopPane();
        jDesktopPane.setPreferredSize(new Dimension(600, 300));
        this.desk.setMinimumSize(new Dimension(150, 50));
        JDesktopPane jDesktopPane2 = this.desk;
        this.console = jSInternalConsole = new JSInternalConsole("JavaScript Console");
        jDesktopPane2.add((Component)jSInternalConsole);
        this.context = contextWindow = new ContextWindow(this);
        contextWindow.setPreferredSize(new Dimension(600, 120));
        this.context.setMinimumSize(new Dimension(50, 50));
        this.split1 = jSplitPane = new JSplitPane(0, (Component)this.desk, (Component)this.context);
        jSplitPane.setOneTouchExpandable(true);
        SwingGui.setResizeWeight(this.split1, 0.66);
        jPanel.add((Component)this.split1, (Object)"Center");
        this.statusBar = jLabel = new JLabel();
        jLabel.setText("Thread: ");
        jPanel.add((Component)this.statusBar, (Object)"South");
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
        this.addWindowListener((WindowListener)new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                SwingGui.this.exit();
            }
        });
    }

    /*
     * Exception decompiling
     */
    private String readFile(String var1_1) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.b.a.a.j.b(Op04StructuredStatement.java:409)
        // org.benf.cfr.reader.b.a.a.j.d(Op04StructuredStatement.java:487)
        // org.benf.cfr.reader.b.a.a.i.a(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:692)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
        // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
        // org.benf.cfr.reader.b.a(Driver.java:128)
        // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
        // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
        // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
        // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        // java.lang.Thread.run(Thread.java:919)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private void setFilePosition(FileWindow var1_1, int var2_2) {
        block8 : {
            var3_3 = var1_1.textArea;
            if (var2_2 != -1) ** GOTO lbl8
            try {
                var1_1.setPosition(-1);
                if (this.currentWindow == var1_1) {
                    this.currentWindow = null;
                }
                break block8;
lbl8: // 1 sources:
                var6_4 = var3_3.getLineStartOffset(var2_2 - 1);
                var7_5 = this.currentWindow;
                if (var7_5 != null && var7_5 != var1_1) {
                    var7_5.setPosition(-1);
                }
                var1_1.setPosition(var6_4);
                this.currentWindow = var1_1;
            }
            catch (BadLocationException var4_6) {
                // empty catch block
            }
        }
        if (true == false) return;
        if (var1_1.isIcon()) {
            this.desk.getDesktopManager().deiconifyFrame((JInternalFrame)var1_1);
        }
        this.desk.getDesktopManager().activateFrame((JInternalFrame)var1_1);
        try {
            var1_1.show();
            var1_1.toFront();
            var1_1.setSelected(true);
            return;
        }
        catch (Exception var5_7) {
            // empty catch block
        }
    }

    static void setResizeWeight(JSplitPane jSplitPane, double d) {
        try {
            Class[] arrclass = new Class[]{Double.TYPE};
            Method method = JSplitPane.class.getMethod("setResizeWeight", arrclass);
            Object[] arrobject = new Object[]{new Double(d)};
            method.invoke((Object)jSplitPane, arrobject);
            return;
        }
        catch (InvocationTargetException invocationTargetException) {
            return;
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    private void updateEnabled(boolean bl) {
        ((Menubar)this.getJMenuBar()).updateEnabled(bl);
        int n = this.toolBar.getComponentCount();
        for (int i = 0; i < n; ++i) {
            boolean bl2 = i == 0 ? bl ^ true : bl;
            this.toolBar.getComponent(i).setEnabled(bl2);
        }
        if (bl) {
            this.toolBar.setEnabled(true);
            if (this.getExtendedState() == 1) {
                this.setExtendedState(0);
            }
            this.toFront();
            this.context.setEnabled(true);
            return;
        }
        FileWindow fileWindow = this.currentWindow;
        if (fileWindow != null) {
            fileWindow.setPosition(-1);
        }
        this.context.setEnabled(false);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void actionPerformed(ActionEvent actionEvent) {
        String string2 = actionEvent.getActionCommand();
        int n = -1;
        if (!(string2.equals((Object)"Cut") || string2.equals((Object)"Copy") || string2.equals((Object)"Paste"))) {
            if (string2.equals((Object)"Step Over")) {
                n = 0;
            } else if (string2.equals((Object)"Step Into")) {
                n = 1;
            } else if (string2.equals((Object)"Step Out")) {
                n = 2;
            } else if (string2.equals((Object)"Go")) {
                n = 3;
            } else if (string2.equals((Object)"Break")) {
                this.dim.setBreak();
            } else if (string2.equals((Object)"Exit")) {
                this.exit();
            } else if (string2.equals((Object)"Open")) {
                String string3;
                String string4 = this.chooseFile("Select a file to compile");
                if (string4 != null && (string3 = this.readFile(string4)) != null) {
                    RunProxy runProxy = new RunProxy(this, 1);
                    runProxy.fileName = string4;
                    runProxy.text = string3;
                    new Thread((Runnable)runProxy).start();
                }
            } else if (string2.equals((Object)"Load")) {
                String string5;
                String string6 = this.chooseFile("Select a file to execute");
                if (string6 != null && (string5 = this.readFile(string6)) != null) {
                    RunProxy runProxy = new RunProxy(this, 2);
                    runProxy.fileName = string6;
                    runProxy.text = string5;
                    new Thread((Runnable)runProxy).start();
                }
            } else if (string2.equals((Object)"More Windows...")) {
                new MoreWindows(this, this.fileWindows, "Window", "Files").showDialog((Component)this);
            } else if (string2.equals((Object)"Console")) {
                if (this.console.isIcon()) {
                    this.desk.getDesktopManager().deiconifyFrame((JInternalFrame)this.console);
                }
                this.console.show();
                this.desk.getDesktopManager().activateFrame((JInternalFrame)this.console);
                this.console.consoleTextArea.requestFocus();
            } else if (!(string2.equals((Object)"Cut") || string2.equals((Object)"Copy") || string2.equals((Object)"Paste"))) {
                if (string2.equals((Object)"Go to function...")) {
                    new FindFunction(this, "Go to function", "Function").showDialog((Component)this);
                } else if (string2.equals((Object)"Go to line...")) {
                    String string7 = (String)JOptionPane.showInputDialog((Component)this, (Object)"Line number", (String)"Go to line...", (int)3, null, null, null);
                    if (string7 == null) return;
                    if (string7.trim().length() == 0) {
                        return;
                    }
                    try {
                        this.showFileWindow(null, Integer.parseInt((String)string7));
                    }
                    catch (NumberFormatException numberFormatException) {}
                } else if (string2.equals((Object)"Tile")) {
                    int n2;
                    int n3;
                    JInternalFrame[] arrjInternalFrame = this.desk.getAllFrames();
                    int n4 = arrjInternalFrame.length;
                    int n5 = (int)Math.sqrt((double)n4);
                    if (n5 * n5 < n4) {
                        int n6 = n5 + 1;
                        if (n5 * n6 < n4) {
                            int n7 = n5 + 1;
                            n3 = n6;
                            n2 = n7;
                        } else {
                            n3 = n6;
                            n2 = n5;
                        }
                    } else {
                        n3 = n5;
                        n2 = n5;
                    }
                    Dimension dimension = this.desk.getSize();
                    int n8 = dimension.width / n3;
                    int n9 = dimension.height / n2;
                    int n10 = 0;
                    int n11 = 0;
                    while (n11 < n2) {
                        int n12;
                        block55 : {
                            int n13 = 0;
                            int n14 = 0;
                            while (n14 < n3) {
                                int n15 = n14 + n11 * n3;
                                if (n15 >= arrjInternalFrame.length) {
                                    n12 = n11;
                                    break block55;
                                }
                                JInternalFrame jInternalFrame = arrjInternalFrame[n15];
                                try {
                                    jInternalFrame.setIcon(false);
                                    jInternalFrame.setMaximum(false);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                                DesktopManager desktopManager = this.desk.getDesktopManager();
                                int n16 = n14;
                                int n17 = n13;
                                int n18 = n11;
                                desktopManager.setBoundsForFrame((JComponent)jInternalFrame, n17, n10, n8, n9);
                                n13 += n8;
                                n14 = n16 + 1;
                                n11 = n18;
                            }
                            n12 = n11;
                        }
                        n10 += n9;
                        n11 = n12 + 1;
                    }
                } else if (string2.equals((Object)"Cascade")) {
                    JInternalFrame[] arrjInternalFrame = this.desk.getAllFrames();
                    int n19 = arrjInternalFrame.length;
                    int n20 = this.desk.getHeight() / n19;
                    if (n20 > 30) {
                        n20 = 30;
                    }
                    int n21 = n19 - 1;
                    int n22 = 0;
                    int n23 = 0;
                    while (n21 >= 0) {
                        JInternalFrame jInternalFrame = arrjInternalFrame[n21];
                        try {
                            jInternalFrame.setIcon(false);
                            jInternalFrame.setMaximum(false);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        Dimension dimension = jInternalFrame.getPreferredSize();
                        int n24 = dimension.width;
                        int n25 = dimension.height;
                        this.desk.getDesktopManager().setBoundsForFrame((JComponent)jInternalFrame, n22, n23, n24, n25);
                        --n21;
                        n22 += n20;
                        n23 += n20;
                    }
                } else {
                    FileWindow fileWindow = this.getFileWindow(string2);
                    if (fileWindow != null) {
                        FileWindow fileWindow2 = fileWindow;
                        try {
                            if (fileWindow2.isIcon()) {
                                fileWindow2.setIcon(false);
                            }
                            fileWindow2.setVisible(true);
                            fileWindow2.moveToFront();
                            fileWindow2.setSelected(true);
                        }
                        catch (Exception exception) {}
                    }
                }
            }
        } else {
            JInternalFrame jInternalFrame = this.getSelectedFrame();
            if (jInternalFrame != null && jInternalFrame instanceof ActionListener) {
                ((ActionListener)jInternalFrame).actionPerformed(actionEvent);
            }
        }
        if (n == -1) return;
        this.updateEnabled(false);
        this.dim.setReturnValue(n);
    }

    void addTopLevel(String string2, JFrame jFrame) {
        if (jFrame != this) {
            this.toplevels.put((Object)string2, (Object)jFrame);
        }
    }

    protected void createFileWindow(Dim.SourceInfo sourceInfo, int n) {
        String string2 = sourceInfo.url();
        FileWindow fileWindow = new FileWindow(this, sourceInfo);
        this.fileWindows.put((Object)string2, (Object)fileWindow);
        if (n != -1) {
            FileWindow fileWindow2 = this.currentWindow;
            if (fileWindow2 != null) {
                fileWindow2.setPosition(-1);
            }
            try {
                fileWindow.setPosition(fileWindow.textArea.getLineStartOffset(n - 1));
            }
            catch (BadLocationException badLocationException) {
                try {
                    fileWindow.setPosition(fileWindow.textArea.getLineStartOffset(0));
                }
                catch (BadLocationException badLocationException2) {
                    fileWindow.setPosition(-1);
                }
            }
        }
        this.desk.add((Component)fileWindow);
        if (n != -1) {
            this.currentWindow = fileWindow;
        }
        this.menubar.addFile(string2);
        fileWindow.setVisible(true);
        if (true) {
            try {
                fileWindow.setMaximum(true);
                fileWindow.setSelected(true);
                fileWindow.moveToFront();
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    public void dispatchNextGuiEvent() throws InterruptedException {
        AWTEvent aWTEvent;
        EventQueue eventQueue = this.awtEventQueue;
        if (eventQueue == null) {
            this.awtEventQueue = eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        }
        if ((aWTEvent = eventQueue.getNextEvent()) instanceof ActiveEvent) {
            ((ActiveEvent)aWTEvent).dispatch();
            return;
        }
        Object object = aWTEvent.getSource();
        if (object instanceof Component) {
            ((Component)object).dispatchEvent(aWTEvent);
        } else if (object instanceof MenuComponent) {
            ((MenuComponent)object).dispatchEvent(aWTEvent);
            return;
        }
    }

    @Override
    public void enterInterrupt(Dim.StackFrame stackFrame, String string2, String string3) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.enterInterruptImpl(stackFrame, string2, string3);
            return;
        }
        RunProxy runProxy = new RunProxy(this, 4);
        runProxy.lastFrame = stackFrame;
        runProxy.threadTitle = string2;
        runProxy.alertMessage = string3;
        SwingUtilities.invokeLater((Runnable)runProxy);
    }

    void enterInterruptImpl(Dim.StackFrame stackFrame, String string2, String string3) {
        JLabel jLabel = this.statusBar;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Thread: ");
        stringBuilder.append(string2);
        jLabel.setText(stringBuilder.toString());
        this.showStopLine(stackFrame);
        if (string3 != null) {
            MessageDialogWrapper.showMessageDialog((Component)this, string3, "Exception in Script", 0);
        }
        this.updateEnabled(true);
        Dim.ContextData contextData = stackFrame.contextData();
        JComboBox<String> jComboBox = this.context.context;
        List<String> list = this.context.toolTips;
        this.context.disableUpdate();
        int n = contextData.frameCount();
        jComboBox.removeAllItems();
        jComboBox.setSelectedItem(null);
        list.clear();
        for (int i = 0; i < n; ++i) {
            Dim.StackFrame stackFrame2 = contextData.getFrame(i);
            String string4 = stackFrame2.getUrl();
            int n2 = stackFrame2.getLineNumber();
            String string5 = string4;
            if (string4.length() > 20) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("...");
                stringBuilder2.append(string4.substring(-17 + string4.length()));
                string5 = stringBuilder2.toString();
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("\"");
            stringBuilder3.append(string5);
            stringBuilder3.append("\", line ");
            stringBuilder3.append(n2);
            jComboBox.insertItemAt((Object)stringBuilder3.toString(), i);
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append("\"");
            stringBuilder4.append(string4);
            stringBuilder4.append("\", line ");
            stringBuilder4.append(n2);
            list.add((Object)stringBuilder4.toString());
        }
        this.context.enableUpdate();
        jComboBox.setSelectedIndex(0);
        jComboBox.setMinimumSize(new Dimension(50, jComboBox.getMinimumSize().height));
    }

    public JSInternalConsole getConsole() {
        return this.console;
    }

    FileWindow getFileWindow(String string2) {
        if (string2 != null && !string2.equals((Object)"<stdin>")) {
            return (FileWindow)((Object)this.fileWindows.get((Object)string2));
        }
        return null;
    }

    public Menubar getMenubar() {
        return this.menubar;
    }

    @Override
    public boolean isGuiEventThread() {
        return SwingUtilities.isEventDispatchThread();
    }

    void removeWindow(FileWindow fileWindow) {
        this.fileWindows.remove((Object)fileWindow.getUrl());
        JMenu jMenu = this.getWindowMenu();
        int n = jMenu.getItemCount();
        JMenuItem jMenuItem = jMenu.getItem(n - 1);
        String string2 = SwingGui.getShortName(fileWindow.getUrl());
        for (int i = 5; i < n; ++i) {
            String string3;
            JMenuItem jMenuItem2 = jMenu.getItem(i);
            if (jMenuItem2 == null || !(string3 = jMenuItem2.getText()).substring(1 + string3.indexOf(32)).equals((Object)string2)) continue;
            jMenu.remove(jMenuItem2);
            if (n == 6) {
                jMenu.remove(4);
                break;
            }
            int n2 = i - 4;
            while (i < n - 1) {
                JMenuItem jMenuItem3 = jMenu.getItem(i);
                if (jMenuItem3 != null) {
                    String string4 = jMenuItem3.getText();
                    if (string4.equals((Object)"More Windows...")) break;
                    int n3 = string4.indexOf(32);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append((char)(n2 + 48));
                    stringBuilder.append(" ");
                    stringBuilder.append(string4.substring(n3 + 1));
                    jMenuItem3.setText(stringBuilder.toString());
                    jMenuItem3.setMnemonic(n2 + 48);
                    ++n2;
                }
                ++i;
            }
            if (n - 6 != 0 || jMenuItem == jMenuItem2 || !jMenuItem.getText().equals((Object)"More Windows...")) break;
            jMenu.remove(jMenuItem);
            break;
        }
        jMenu.revalidate();
    }

    public void setExitAction(Runnable runnable) {
        this.exitAction = runnable;
    }

    public void setVisible(boolean bl) {
        super.setVisible(bl);
        if (bl) {
            this.console.consoleTextArea.requestFocus();
            this.context.split.setDividerLocation(0.5);
            try {
                this.console.setMaximum(true);
                this.console.setSelected(true);
                this.console.show();
                this.console.consoleTextArea.requestFocus();
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void showFileWindow(String string2, int n) {
        JInternalFrame jInternalFrame;
        FileWindow fileWindow = string2 != null ? this.getFileWindow(string2) : ((jInternalFrame = this.getSelectedFrame()) != null && jInternalFrame instanceof FileWindow ? (FileWindow)jInternalFrame : this.currentWindow);
        if (fileWindow == null && string2 != null) {
            this.createFileWindow(this.dim.sourceInfo(string2), -1);
            fileWindow = this.getFileWindow(string2);
        }
        if (fileWindow == null) {
            return;
        }
        if (n > -1) {
            int n2 = fileWindow.getPosition(n - 1);
            int n3 = fileWindow.getPosition(n) - 1;
            if (n2 <= 0) {
                return;
            }
            fileWindow.textArea.select(n2);
            fileWindow.textArea.setCaretPosition(n2);
            fileWindow.textArea.moveCaretPosition(n3);
        }
        try {
            if (fileWindow.isIcon()) {
                fileWindow.setIcon(false);
            }
            fileWindow.setVisible(true);
            fileWindow.moveToFront();
            fileWindow.setSelected(true);
            this.requestFocus();
            fileWindow.requestFocus();
            fileWindow.textArea.requestFocus();
            return;
        }
        catch (Exception exception) {
            return;
        }
    }

    void showStopLine(Dim.StackFrame stackFrame) {
        String string2 = stackFrame.getUrl();
        if (string2 != null && !string2.equals((Object)"<stdin>")) {
            this.showFileWindow(string2, -1);
            int n = stackFrame.getLineNumber();
            FileWindow fileWindow = this.getFileWindow(string2);
            if (fileWindow != null) {
                this.setFilePosition(fileWindow, n);
                return;
            }
        } else if (this.console.isVisible()) {
            this.console.show();
        }
    }

    protected boolean updateFileWindow(Dim.SourceInfo sourceInfo) {
        FileWindow fileWindow = this.getFileWindow(sourceInfo.url());
        if (fileWindow != null) {
            fileWindow.updateText(sourceInfo);
            fileWindow.show();
            return true;
        }
        return false;
    }

    @Override
    public void updateSourceText(Dim.SourceInfo sourceInfo) {
        RunProxy runProxy = new RunProxy(this, 3);
        runProxy.sourceInfo = sourceInfo;
        SwingUtilities.invokeLater((Runnable)runProxy);
    }

}

