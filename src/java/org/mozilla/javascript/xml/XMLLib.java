/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 */
package org.mozilla.javascript.xml;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class XMLLib {
    private static final Object XML_LIB_KEY = new Object();

    public static XMLLib extractFromScope(Scriptable scriptable) {
        XMLLib xMLLib = XMLLib.extractFromScopeOrNull(scriptable);
        if (xMLLib != null) {
            return xMLLib;
        }
        throw Context.reportRuntimeError(ScriptRuntime.getMessage0("msg.XML.not.available"));
    }

    public static XMLLib extractFromScopeOrNull(Scriptable scriptable) {
        ScriptableObject scriptableObject = ScriptRuntime.getLibraryScopeOrNull(scriptable);
        if (scriptableObject == null) {
            return null;
        }
        ScriptableObject.getProperty((Scriptable)scriptableObject, "XML");
        return (XMLLib)scriptableObject.getAssociatedValue(XML_LIB_KEY);
    }

    protected final XMLLib bindToScope(Scriptable scriptable) {
        ScriptableObject scriptableObject = ScriptRuntime.getLibraryScopeOrNull(scriptable);
        if (scriptableObject != null) {
            return (XMLLib)scriptableObject.associateValue(XML_LIB_KEY, this);
        }
        throw new IllegalStateException();
    }

    public abstract String escapeAttributeValue(Object var1);

    public abstract String escapeTextValue(Object var1);

    public int getPrettyIndent() {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreComments() {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreProcessingInstructions() {
        throw new UnsupportedOperationException();
    }

    public boolean isIgnoreWhitespace() {
        throw new UnsupportedOperationException();
    }

    public boolean isPrettyPrinting() {
        throw new UnsupportedOperationException();
    }

    public abstract boolean isXMLName(Context var1, Object var2);

    public abstract Ref nameRef(Context var1, Object var2, Object var3, Scriptable var4, int var5);

    public abstract Ref nameRef(Context var1, Object var2, Scriptable var3, int var4);

    public void setIgnoreComments(boolean bl) {
        throw new UnsupportedOperationException();
    }

    public void setIgnoreProcessingInstructions(boolean bl) {
        throw new UnsupportedOperationException();
    }

    public void setIgnoreWhitespace(boolean bl) {
        throw new UnsupportedOperationException();
    }

    public void setPrettyIndent(int n) {
        throw new UnsupportedOperationException();
    }

    public void setPrettyPrinting(boolean bl) {
        throw new UnsupportedOperationException();
    }

    public abstract Object toDefaultXmlNamespace(Context var1, Object var2);

    public static abstract class Factory {
        public static Factory create(final String string2) {
            return new Factory(){

                @Override
                public String getImplementationClassName() {
                    return string2;
                }
            };
        }

        public abstract String getImplementationClassName();

    }

}

