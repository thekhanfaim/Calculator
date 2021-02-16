/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.lang.Character
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  javax.swing.JOptionPane
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import javax.swing.JOptionPane;

class MessageDialogWrapper {
    MessageDialogWrapper() {
    }

    public static void showMessageDialog(Component component, String string2, String string3, int n) {
        if (string2.length() > 60) {
            StringBuilder stringBuilder = new StringBuilder();
            int n2 = string2.length();
            int n3 = 0;
            int n4 = 0;
            while (n4 < n2) {
                char c = string2.charAt(n4);
                stringBuilder.append(c);
                if (Character.isWhitespace((char)c)) {
                    int n5;
                    for (n5 = n4 + 1; n5 < n2 && !Character.isWhitespace((char)string2.charAt(n5)); ++n5) {
                    }
                    if (n5 < n2 && n3 + (n5 - n4) > 60) {
                        stringBuilder.append('\n');
                        n3 = 0;
                    }
                }
                ++n4;
                ++n3;
            }
            string2 = stringBuilder.toString();
        }
        JOptionPane.showMessageDialog((Component)component, (Object)string2, (String)string3, (int)n);
    }
}

