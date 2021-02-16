/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.mozilla.javascript.xml.XMLObject
 *  org.mozilla.javascript.xmlimpl.XMLList
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.mozilla.javascript.xmlimpl.XMLList;

final class XMLWithScope
extends NativeWith {
    private static final long serialVersionUID = -696429282095170887L;
    private int _currIndex;
    private XMLObject _dqPrototype;
    private XMLList _xmlList;
    private XMLLibImpl lib;

    XMLWithScope(XMLLibImpl xMLLibImpl, Scriptable scriptable, XMLObject xMLObject) {
        super(scriptable, (Scriptable)xMLObject);
        this.lib = xMLLibImpl;
    }

    void initAsDotQuery() {
        XMLList xMLList;
        XMLObject xMLObject = (XMLObject)this.getPrototype();
        this._currIndex = 0;
        this._dqPrototype = xMLObject;
        if (xMLObject instanceof XMLList && (xMLList = (XMLList)xMLObject).length() > 0) {
            this.setPrototype((Scriptable)xMLList.get(0, null));
        }
        this._xmlList = this.lib.newXMLList();
    }

    @Override
    protected Object updateDotQuery(boolean bl) {
        XMLObject xMLObject = this._dqPrototype;
        XMLList xMLList = this._xmlList;
        if (xMLObject instanceof XMLList) {
            int n;
            XMLList xMLList2 = (XMLList)xMLObject;
            int n2 = this._currIndex;
            if (bl) {
                xMLList.addToList(xMLList2.get(n2, null));
            }
            if ((n = n2 + 1) < xMLList2.length()) {
                this._currIndex = n;
                this.setPrototype((Scriptable)xMLList2.get(n, null));
                return null;
            }
            return xMLList;
        }
        if (bl) {
            xMLList.addToList((Object)xMLObject);
        }
        return xMLList;
    }
}

