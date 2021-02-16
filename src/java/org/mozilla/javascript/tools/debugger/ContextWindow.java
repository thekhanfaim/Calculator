/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.BorderLayout
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.Dimension
 *  java.awt.GridBagConstraints
 *  java.awt.GridBagLayout
 *  java.awt.GridLayout
 *  java.awt.Insets
 *  java.awt.LayoutManager
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.awt.event.ComponentEvent
 *  java.awt.event.ComponentListener
 *  java.awt.event.ContainerEvent
 *  java.awt.event.ContainerListener
 *  java.awt.event.WindowAdapter
 *  java.awt.event.WindowEvent
 *  java.awt.event.WindowListener
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.EventListener
 *  java.util.List
 *  javax.swing.JComboBox
 *  javax.swing.JFrame
 *  javax.swing.JLabel
 *  javax.swing.JPanel
 *  javax.swing.JScrollPane
 *  javax.swing.JSplitPane
 *  javax.swing.JTabbedPane
 *  javax.swing.JToolBar
 *  javax.swing.JTree
 *  javax.swing.JViewport
 *  javax.swing.border.Border
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.border.Border;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.EvalTextArea;
import org.mozilla.javascript.tools.debugger.Evaluator;
import org.mozilla.javascript.tools.debugger.MyTableModel;
import org.mozilla.javascript.tools.debugger.MyTreeTable;
import org.mozilla.javascript.tools.debugger.SwingGui;
import org.mozilla.javascript.tools.debugger.VariableModel;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;

class ContextWindow
extends JPanel
implements ActionListener {
    private static final long serialVersionUID = 2306040975490228051L;
    private EvalTextArea cmdLine;
    JComboBox<String> context;
    private SwingGui debugGui;
    private boolean enabled;
    private Evaluator evaluator;
    private MyTreeTable localsTable;
    JSplitPane split;
    private MyTableModel tableModel;
    private JTabbedPane tabs;
    private JTabbedPane tabs2;
    private MyTreeTable thisTable;
    List<String> toolTips;

    public ContextWindow(final SwingGui swingGui) {
        JTabbedPane jTabbedPane;
        JTabbedPane jTabbedPane2;
        JSplitPane jSplitPane;
        MyTreeTable myTreeTable;
        JComboBox jComboBox;
        this.debugGui = swingGui;
        this.enabled = false;
        JPanel jPanel = new JPanel();
        final JToolBar jToolBar = new JToolBar();
        jToolBar.setName("Variables");
        jToolBar.setLayout((LayoutManager)new GridLayout());
        jToolBar.add((Component)jPanel);
        final JPanel jPanel2 = new JPanel();
        jPanel2.setLayout((LayoutManager)new GridLayout());
        final JPanel jPanel3 = new JPanel();
        jPanel3.setLayout((LayoutManager)new GridLayout());
        jPanel2.add((Component)jToolBar);
        JLabel jLabel = new JLabel("Context:");
        this.context = jComboBox = new JComboBox();
        jComboBox.setLightWeightPopupEnabled(false);
        this.toolTips = Collections.synchronizedList((List)new ArrayList());
        jLabel.setBorder(this.context.getBorder());
        this.context.addActionListener((ActionListener)this);
        this.context.setActionCommand("ContextSwitch");
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout((LayoutManager)gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets.left = 5;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.ipadx = 5;
        gridBagLayout.setConstraints((Component)jLabel, gridBagConstraints);
        jPanel.add((Component)jLabel);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.anchor = 17;
        gridBagLayout.setConstraints(this.context, gridBagConstraints2);
        jPanel.add(this.context);
        this.tabs = jTabbedPane2 = new JTabbedPane(3);
        jTabbedPane2.setPreferredSize(new Dimension(500, 300));
        this.thisTable = new MyTreeTable(new VariableModel());
        JScrollPane jScrollPane = new JScrollPane((Component)this.thisTable);
        jScrollPane.getViewport().setViewSize(new Dimension(5, 2));
        this.tabs.add("this", (Component)jScrollPane);
        this.localsTable = myTreeTable = new MyTreeTable(new VariableModel());
        myTreeTable.setAutoResizeMode(4);
        this.localsTable.setPreferredSize(null);
        JScrollPane jScrollPane2 = new JScrollPane((Component)this.localsTable);
        this.tabs.add("Locals", (Component)jScrollPane2);
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridheight = 0;
        gridBagConstraints2.fill = 1;
        gridBagConstraints2.anchor = 17;
        gridBagLayout.setConstraints((Component)this.tabs, gridBagConstraints2);
        jPanel.add((Component)this.tabs);
        this.evaluator = new Evaluator(swingGui);
        this.cmdLine = new EvalTextArea(swingGui);
        this.tableModel = this.evaluator.tableModel;
        JScrollPane jScrollPane3 = new JScrollPane((Component)this.evaluator);
        final JToolBar jToolBar2 = new JToolBar();
        jToolBar2.setName("Evaluate");
        this.tabs2 = jTabbedPane = new JTabbedPane(3);
        jTabbedPane.add("Watch", (Component)jScrollPane3);
        this.tabs2.add("Evaluate", (Component)new JScrollPane((Component)this.cmdLine));
        this.tabs2.setPreferredSize(new Dimension(500, 300));
        jToolBar2.setLayout((LayoutManager)new GridLayout());
        jToolBar2.add((Component)this.tabs2);
        jPanel3.add((Component)jToolBar2);
        this.evaluator.setAutoResizeMode(4);
        this.split = jSplitPane = new JSplitPane(1, (Component)jPanel2, (Component)jPanel3);
        jSplitPane.setOneTouchExpandable(true);
        SwingGui.setResizeWeight(this.split, 0.5);
        this.setLayout((LayoutManager)new BorderLayout());
        this.add((Component)this.split, (Object)"Center");
        final JSplitPane jSplitPane2 = this.split;
        ComponentListener componentListener = new ComponentListener(){
            boolean t2Docked = true;

            void check(Component component) {
                Container container;
                boolean bl;
                Container container2 = this.getParent();
                if (container2 == null) {
                    return;
                }
                Container container3 = jToolBar.getParent();
                boolean bl2 = true;
                boolean bl3 = true;
                if (container3 != null) {
                    if (container3 != jPanel2) {
                        while (!(container3 instanceof JFrame)) {
                            container3 = container3.getParent();
                        }
                        JFrame jFrame = (JFrame)container3;
                        swingGui.addTopLevel("Variables", jFrame);
                        if (!jFrame.isResizable()) {
                            jFrame.setResizable(true);
                            jFrame.setDefaultCloseOperation(0);
                            final WindowListener[] arrwindowListener = (WindowListener[])jFrame.getListeners(WindowListener.class);
                            jFrame.removeWindowListener(arrwindowListener[0]);
                            jFrame.addWindowListener((WindowListener)new WindowAdapter(){

                                public void windowClosing(WindowEvent windowEvent) {
                                    ContextWindow.this.context.hidePopup();
                                    arrwindowListener[0].windowClosing(windowEvent);
                                }
                            });
                        }
                        bl2 = false;
                    } else {
                        bl2 = true;
                    }
                }
                if ((container = jToolBar2.getParent()) != null) {
                    if (container != jPanel3) {
                        while (!(container instanceof JFrame)) {
                            container = container.getParent();
                        }
                        JFrame jFrame = (JFrame)container;
                        swingGui.addTopLevel("Evaluate", jFrame);
                        jFrame.setResizable(true);
                        bl3 = false;
                    } else {
                        bl3 = true;
                    }
                }
                if (bl2 && (bl = this.t2Docked) && bl3 && bl) {
                    return;
                }
                this.t2Docked = bl3;
                JSplitPane jSplitPane = (JSplitPane)container2;
                if (bl2) {
                    if (bl3) {
                        jSplitPane2.setDividerLocation(0.5);
                    } else {
                        jSplitPane2.setDividerLocation(1.0);
                    }
                    if (false) {
                        jSplitPane.setDividerLocation(0.66);
                        return;
                    }
                } else {
                    if (bl3) {
                        jSplitPane2.setDividerLocation(0.0);
                        jSplitPane.setDividerLocation(0.66);
                        return;
                    }
                    jSplitPane.setDividerLocation(1.0);
                }
            }

            public void componentHidden(ComponentEvent componentEvent) {
                this.check(componentEvent.getComponent());
            }

            public void componentMoved(ComponentEvent componentEvent) {
                this.check(componentEvent.getComponent());
            }

            public void componentResized(ComponentEvent componentEvent) {
                this.check(componentEvent.getComponent());
            }

            public void componentShown(ComponentEvent componentEvent) {
                this.check(componentEvent.getComponent());
            }

        };
        ContainerListener containerListener = new ContainerListener(){

            public void componentAdded(ContainerEvent containerEvent) {
                JSplitPane jSplitPane = (JSplitPane)this.getParent();
                if (containerEvent.getChild() == jToolBar) {
                    if (jToolBar2.getParent() == jPanel3) {
                        jSplitPane2.setDividerLocation(0.5);
                    } else {
                        jSplitPane2.setDividerLocation(1.0);
                    }
                    jSplitPane.setDividerLocation(0.66);
                }
            }

            public void componentRemoved(ContainerEvent containerEvent) {
                JSplitPane jSplitPane = (JSplitPane)this.getParent();
                if (containerEvent.getChild() == jToolBar) {
                    if (jToolBar2.getParent() == jPanel3) {
                        jSplitPane2.setDividerLocation(0.0);
                        jSplitPane.setDividerLocation(0.66);
                        return;
                    }
                    jSplitPane.setDividerLocation(1.0);
                }
            }
        };
        jPanel2.addContainerListener(containerListener);
        jToolBar.addComponentListener(componentListener);
        jToolBar2.addComponentListener(componentListener);
        this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (!this.enabled) {
            return;
        }
        if (actionEvent.getActionCommand().equals((Object)"ContextSwitch")) {
            Dim.ContextData contextData = this.debugGui.dim.currentContextData();
            if (contextData == null) {
                return;
            }
            int n = this.context.getSelectedIndex();
            this.context.setToolTipText((String)this.toolTips.get(n));
            if (n >= contextData.frameCount()) {
                return;
            }
            Dim.StackFrame stackFrame = contextData.getFrame(n);
            Object object = stackFrame.scope();
            Object object2 = stackFrame.thisObj();
            this.thisTable.resetTree(new VariableModel(this.debugGui.dim, object2));
            VariableModel variableModel = object != object2 ? new VariableModel(this.debugGui.dim, object) : new VariableModel();
            this.localsTable.resetTree(variableModel);
            this.debugGui.dim.contextSwitch(n);
            this.debugGui.showStopLine(stackFrame);
            this.tableModel.updateModel();
        }
    }

    public void disableUpdate() {
        this.enabled = false;
    }

    public void enableUpdate() {
        this.enabled = true;
    }

    public void setEnabled(boolean bl) {
        this.context.setEnabled(bl);
        this.thisTable.setEnabled(bl);
        this.localsTable.setEnabled(bl);
        this.evaluator.setEnabled(bl);
        this.cmdLine.setEnabled(bl);
    }

}

