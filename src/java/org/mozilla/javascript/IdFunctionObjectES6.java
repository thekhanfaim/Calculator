/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

public class IdFunctionObjectES6
extends IdFunctionObject {
    private static final int Id_length = 1;
    private static final int Id_name = 3;
    private boolean myLength = true;
    private boolean myName = true;

    public IdFunctionObjectES6(IdFunctionCall idFunctionCall, Object object, int n, String string, int n2, Scriptable scriptable) {
        super(idFunctionCall, object, n, string, n2, scriptable);
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        if (string.equals((Object)"length")) {
            return IdFunctionObjectES6.instanceIdInfo(3, 1);
        }
        if (string.equals((Object)"name")) {
            return IdFunctionObjectES6.instanceIdInfo(3, 3);
        }
        return super.findInstanceIdInfo(string);
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        if (n == 1 && !this.myLength) {
            return NOT_FOUND;
        }
        if (n == 3 && !this.myName) {
            return NOT_FOUND;
        }
        return super.getInstanceIdValue(n);
    }

    @Override
    protected void setInstanceIdValue(int n, Object object) {
        if (n == 1 && object == NOT_FOUND) {
            this.myLength = false;
            return;
        }
        if (n == 3 && object == NOT_FOUND) {
            this.myName = false;
            return;
        }
        super.setInstanceIdValue(n, object);
    }
}

