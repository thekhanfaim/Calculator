/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Container
 *  java.awt.Font
 *  java.awt.Point
 *  java.awt.Rectangle
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.awt.event.KeyEvent
 *  java.awt.event.KeyListener
 *  java.awt.event.MouseEvent
 *  java.awt.event.MouseListener
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  javax.swing.JComponent
 *  javax.swing.JTextArea
 *  javax.swing.JViewport
 *  javax.swing.event.PopupMenuEvent
 *  javax.swing.event.PopupMenuListener
 *  javax.swing.text.BadLocationException
 *  javax.swing.text.Caret
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import org.mozilla.javascript.tools.debugger.FilePopupMenu;
import org.mozilla.javascript.tools.debugger.FileWindow;

class FileTextArea
extends JTextArea
implements ActionListener,
PopupMenuListener,
KeyListener,
MouseListener {
    private static final long serialVersionUID = -25032065448563720L;
    private FilePopupMenu popup;
    private FileWindow w;

    public FileTextArea(FileWindow fileWindow) {
        FilePopupMenu filePopupMenu;
        this.w = fileWindow;
        this.popup = filePopupMenu = new FilePopupMenu(this);
        filePopupMenu.addPopupMenuListener((PopupMenuListener)this);
        this.addMouseListener((MouseListener)this);
        this.addKeyListener((KeyListener)this);
        this.setFont(new Font("Monospaced", 0, 12));
    }

    private void checkPopup(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            this.popup.show((JComponent)this, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        int n = this.viewToModel(new Point(this.popup.x, this.popup.y));
        this.popup.setVisible(false);
        String string2 = actionEvent.getActionCommand();
        int n2 = -1;
        try {
            int n3;
            n2 = n3 = this.getLineOfOffset(n);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (string2.equals((Object)"Set Breakpoint")) {
            this.w.setBreakPoint(n2 + 1);
            return;
        }
        if (string2.equals((Object)"Clear Breakpoint")) {
            this.w.clearBreakPoint(n2 + 1);
            return;
        }
        if (string2.equals((Object)"Run")) {
            this.w.load();
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        int n = keyEvent.getKeyCode();
        if (n != 127) {
            switch (n) {
                default: {
                    return;
                }
                case 8: 
                case 9: 
                case 10: 
            }
        }
        keyEvent.consume();
    }

    public void keyReleased(KeyEvent keyEvent) {
        keyEvent.consume();
    }

    public void keyTyped(KeyEvent keyEvent) {
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.checkPopup(mouseEvent);
        this.requestFocus();
        this.getCaret().setVisible(true);
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        this.checkPopup(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        this.checkPopup(mouseEvent);
    }

    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void select(int n) {
        int n2;
        Rectangle rectangle;
        block9 : {
            if (n < 0) return;
            n2 = this.getLineOfOffset(n);
            rectangle = this.modelToView(n);
            if (rectangle != null) break block9;
            this.select(n, n);
            return;
        }
        int n3 = n2 + 1;
        try {
            Rectangle rectangle2 = this.modelToView(this.getLineStartOffset(n3));
            if (rectangle2 != null) {
                rectangle = rectangle2;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            Rectangle rectangle3 = ((JViewport)this.getParent()).getViewRect();
            if (rectangle3.y + rectangle3.height > rectangle.y) {
                this.select(n, n);
                return;
            } else {
                rectangle.y += (rectangle3.height - rectangle.height) / 2;
                this.scrollRectToVisible(rectangle);
                this.select(n, n);
            }
            return;
        }
        catch (BadLocationException badLocationException) {
            this.select(n, n);
        }
    }
}

