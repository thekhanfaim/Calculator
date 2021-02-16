/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Color
 *  java.awt.Component
 *  java.awt.Dimension
 *  java.awt.Font
 *  java.awt.FontMetrics
 *  java.awt.Graphics
 *  java.awt.Polygon
 *  java.awt.Rectangle
 *  java.awt.event.MouseEvent
 *  java.awt.event.MouseListener
 *  java.lang.Integer
 *  java.lang.String
 *  java.lang.StringBuilder
 *  javax.swing.JPanel
 *  javax.swing.text.BadLocationException
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import org.mozilla.javascript.tools.debugger.FileTextArea;
import org.mozilla.javascript.tools.debugger.FileWindow;

class FileHeader
extends JPanel
implements MouseListener {
    private static final long serialVersionUID = -2858905404778259127L;
    private FileWindow fileWindow;
    private int pressLine = -1;

    public FileHeader(FileWindow fileWindow) {
        this.fileWindow = fileWindow;
        this.addMouseListener((MouseListener)this);
        this.update();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        int n = this.getFontMetrics(this.fileWindow.textArea.getFont()).getHeight();
        this.pressLine = mouseEvent.getY() / n;
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getComponent() == this && (16 & mouseEvent.getModifiers()) != 0) {
            int n = mouseEvent.getY() / this.getFontMetrics(this.fileWindow.textArea.getFont()).getHeight();
            if (n == this.pressLine) {
                this.fileWindow.toggleBreakPoint(n + 1);
                return;
            }
            this.pressLine = -1;
        }
    }

    public void paint(Graphics graphics) {
        FileHeader fileHeader = this;
        super.paint(graphics);
        FileTextArea fileTextArea = fileHeader.fileWindow.textArea;
        Font font = fileTextArea.getFont();
        graphics.setFont(font);
        FontMetrics fontMetrics = fileHeader.getFontMetrics(font);
        Rectangle rectangle = graphics.getClipBounds();
        graphics.setColor(this.getBackground());
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        int n = fontMetrics.getMaxAscent();
        int n2 = fontMetrics.getHeight();
        int n3 = 1 + fileTextArea.getLineCount();
        if (Integer.toString((int)n3).length() < 2) {
            // empty if block
        }
        int n4 = rectangle.y / n2;
        int n5 = 1 + (rectangle.y + rectangle.height) / n2;
        int n6 = this.getWidth();
        if (n5 > n3) {
            n5 = n3;
        }
        int n7 = n5;
        for (int i = n4; i < n7; ++i) {
            int n8 = -2;
            try {
                int n9;
                n8 = n9 = fileTextArea.getLineStartOffset(i);
            }
            catch (BadLocationException badLocationException) {
                // empty catch block
            }
            FileWindow fileWindow = fileHeader.fileWindow;
            FileTextArea fileTextArea2 = fileTextArea;
            boolean bl = fileWindow.isBreakPoint(i + 1);
            StringBuilder stringBuilder = new StringBuilder();
            int n10 = i + 1;
            Font font2 = font;
            stringBuilder.append(Integer.toString((int)n10));
            stringBuilder.append(" ");
            String string2 = stringBuilder.toString();
            int n11 = i * n2;
            FontMetrics fontMetrics2 = fontMetrics;
            graphics.setColor(Color.blue);
            int n12 = n11 + n;
            Rectangle rectangle2 = rectangle;
            graphics.drawString(string2, 0, n12);
            int n13 = n6 - n;
            if (bl) {
                graphics.setColor(new Color(128, 0, 0));
                int n14 = n11 + n - 9;
                graphics.fillOval(n13, n14, 9, 9);
                graphics.drawOval(n13, n14, 8, 8);
                graphics.drawOval(n13, n14, 9, 9);
            }
            if (n8 == fileHeader.fileWindow.currentPos) {
                Polygon polygon = new Polygon();
                int n15 = n11 + (n - 10);
                polygon.addPoint(n13, n15 + 3);
                polygon.addPoint(n13 + 5, n15 + 3);
                int n16 = n13 + 5;
                int n17 = n15;
                while (n16 <= n13 + 10) {
                    polygon.addPoint(n16, n17);
                    ++n16;
                    ++n17;
                }
                int n18 = n13 + 9;
                while (n18 >= n13 + 5) {
                    polygon.addPoint(n18, n17);
                    --n18;
                    ++n17;
                }
                int n19 = n13 + 5;
                polygon.addPoint(n19, n15 + 7);
                polygon.addPoint(n13, n15 + 7);
                graphics.setColor(Color.yellow);
                graphics.fillPolygon(polygon);
                graphics.setColor(Color.black);
                graphics.drawPolygon(polygon);
            }
            fileHeader = this;
            fileTextArea = fileTextArea2;
            fontMetrics = fontMetrics2;
            font = font2;
            rectangle = rectangle2;
        }
    }

    public void update() {
        FileTextArea fileTextArea = this.fileWindow.textArea;
        Font font = fileTextArea.getFont();
        this.setFont(font);
        FontMetrics fontMetrics = this.getFontMetrics(font);
        int n = fontMetrics.getHeight();
        int n2 = 1 + fileTextArea.getLineCount();
        String string2 = Integer.toString((int)n2);
        if (string2.length() < 2) {
            string2 = "99";
        }
        Dimension dimension = new Dimension();
        dimension.width = 16 + fontMetrics.stringWidth(string2);
        dimension.height = 100 + n2 * n;
        this.setPreferredSize(dimension);
        this.setSize(dimension);
    }
}

