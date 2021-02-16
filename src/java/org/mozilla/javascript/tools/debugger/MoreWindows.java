/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.awt.Container
 *  java.awt.Dimension
 *  java.awt.Frame
 *  java.awt.LayoutManager
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.awt.event.KeyAdapter
 *  java.awt.event.KeyEvent
 *  java.awt.event.KeyListener
 *  java.awt.event.MouseAdapter
 *  java.awt.event.MouseEvent
 *  java.awt.event.MouseListener
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Set
 *  javax.swing.BorderFactory
 *  javax.swing.Box
 *  javax.swing.BoxLayout
 *  javax.swing.DefaultListModel
 *  javax.swing.JButton
 *  javax.swing.JDialog
 *  javax.swing.JLabel
 *  javax.swing.JList
 *  javax.swing.JPanel
 *  javax.swing.JRootPane
 *  javax.swing.JScrollPane
 *  javax.swing.ListModel
 *  javax.swing.border.Border
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.Border;
import org.mozilla.javascript.tools.debugger.FileWindow;
import org.mozilla.javascript.tools.debugger.SwingGui;

class MoreWindows
extends JDialog
implements ActionListener {
    private static final long serialVersionUID = 5177066296457377546L;
    private JButton cancelButton;
    private JList<String> list;
    private JButton setButton;
    private SwingGui swingGui;
    private String value;

    MoreWindows(SwingGui swingGui, Map<String, FileWindow> map, String string2, String string3) {
        JList jList;
        super((Frame)swingGui, string2, true);
        this.swingGui = swingGui;
        this.cancelButton = new JButton("Cancel");
        this.setButton = new JButton("Select");
        this.cancelButton.addActionListener((ActionListener)this);
        this.setButton.addActionListener((ActionListener)this);
        this.getRootPane().setDefaultButton(this.setButton);
        this.list = jList = new JList((ListModel)new DefaultListModel());
        DefaultListModel defaultListModel = (DefaultListModel)jList.getModel();
        defaultListModel.clear();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            defaultListModel.addElement((Object)((String)iterator.next()));
        }
        this.list.setSelectedIndex(0);
        this.setButton.setEnabled(true);
        this.list.setSelectionMode(1);
        this.list.addMouseListener((MouseListener)new MouseHandler());
        JScrollPane jScrollPane = new JScrollPane(this.list);
        jScrollPane.setPreferredSize(new Dimension(320, 240));
        jScrollPane.setMinimumSize(new Dimension(250, 80));
        jScrollPane.setAlignmentX(0.0f);
        JPanel jPanel = new JPanel();
        jPanel.setLayout((LayoutManager)new BoxLayout((Container)jPanel, 1));
        JLabel jLabel = new JLabel(string3);
        jLabel.setLabelFor(this.list);
        jPanel.add((Component)jLabel);
        jPanel.add(Box.createRigidArea((Dimension)new Dimension(0, 5)));
        jPanel.add((Component)jScrollPane);
        jPanel.setBorder(BorderFactory.createEmptyBorder((int)10, (int)10, (int)10, (int)10));
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout((LayoutManager)new BoxLayout((Container)jPanel2, 0));
        jPanel2.setBorder(BorderFactory.createEmptyBorder((int)0, (int)10, (int)10, (int)10));
        jPanel2.add(Box.createHorizontalGlue());
        jPanel2.add((Component)this.cancelButton);
        jPanel2.add(Box.createRigidArea((Dimension)new Dimension(10, 0)));
        jPanel2.add((Component)this.setButton);
        Container container = this.getContentPane();
        container.add((Component)jPanel, (Object)"Center");
        container.add((Component)jPanel2, (Object)"South");
        this.pack();
        this.addKeyListener((KeyListener)new KeyAdapter(){

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 27) {
                    keyEvent.consume();
                    MoreWindows.this.value = null;
                    MoreWindows.this.setVisible(false);
                }
            }
        });
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string2 = actionEvent.getActionCommand();
        if (string2.equals((Object)"Cancel")) {
            this.setVisible(false);
            this.value = null;
            return;
        }
        if (string2.equals((Object)"Select")) {
            this.value = (String)this.list.getSelectedValue();
            this.setVisible(false);
            this.swingGui.showFileWindow(this.value, -1);
        }
    }

    public String showDialog(Component component) {
        this.value = null;
        this.setLocationRelativeTo(component);
        this.setVisible(true);
        return this.value;
    }

    private class MouseHandler
    extends MouseAdapter {
        private MouseHandler() {
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2) {
                MoreWindows.this.setButton.doClick();
            }
        }
    }

}

