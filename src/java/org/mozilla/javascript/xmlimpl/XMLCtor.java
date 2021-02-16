/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XmlProcessor;

class XMLCtor
extends IdFunctionObject {
    private static final int Id_defaultSettings = 1;
    private static final int Id_ignoreComments = 1;
    private static final int Id_ignoreProcessingInstructions = 2;
    private static final int Id_ignoreWhitespace = 3;
    private static final int Id_prettyIndent = 4;
    private static final int Id_prettyPrinting = 5;
    private static final int Id_setSettings = 3;
    private static final int Id_settings = 2;
    private static final int MAX_FUNCTION_ID = 3;
    private static final int MAX_INSTANCE_ID = 5;
    private static final Object XMLCTOR_TAG = "XMLCtor";
    static final long serialVersionUID = -8708195078359817341L;
    private XmlProcessor options;

    XMLCtor(XML xML, Object object, int n, int n2) {
        super(xML, object, n, n2);
        this.options = xML.getProcessor();
        this.activatePrototypeMap(3);
    }

    private void readSettings(Scriptable scriptable) {
        for (int i = 1; i <= 5; ++i) {
            Object object;
            int n;
            block5 : {
                block3 : {
                    block4 : {
                        n = i + super.getMaxInstanceId();
                        object = ScriptableObject.getProperty(scriptable, this.getInstanceIdName(n));
                        if (object == Scriptable.NOT_FOUND) continue;
                        if (i == 1 || i == 2 || i == 3) break block3;
                        if (i == 4) break block4;
                        if (i != 5) {
                            throw new IllegalStateException();
                        }
                        break block3;
                    }
                    if (!(object instanceof Number)) {
                        continue;
                    }
                    break block5;
                }
                if (!(object instanceof Boolean)) continue;
            }
            this.setInstanceIdValue(n, object);
        }
    }

    private void writeSetting(Scriptable scriptable) {
        for (int i = 1; i <= 5; ++i) {
            int n = i + super.getMaxInstanceId();
            ScriptableObject.putProperty(scriptable, this.getInstanceIdName(n), this.getInstanceIdValue(n));
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(XMLCTOR_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        if (n != 1) {
            if (n != 2) {
                if (n == 3) {
                    if (arrobject.length != 0 && arrobject[0] != null && arrobject[0] != Undefined.instance) {
                        if (arrobject[0] instanceof Scriptable) {
                            this.readSettings((Scriptable)arrobject[0]);
                        }
                    } else {
                        this.options.setDefault();
                    }
                    return Undefined.instance;
                }
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            Scriptable scriptable3 = context.newObject(scriptable);
            this.writeSetting(scriptable3);
            return scriptable3;
        }
        this.options.setDefault();
        Scriptable scriptable4 = context.newObject(scriptable);
        this.writeSetting(scriptable4);
        return scriptable4;
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        String string2;
        int n;
        int n2 = string.length();
        if (n2 != 12) {
            if (n2 != 14) {
                if (n2 != 16) {
                    if (n2 != 28) {
                        n = 0;
                        string2 = null;
                    } else {
                        string2 = "ignoreProcessingInstructions";
                        n = 2;
                    }
                } else {
                    string2 = "ignoreWhitespace";
                    n = 3;
                }
            } else {
                char c = string.charAt(0);
                if (c == 'i') {
                    string2 = "ignoreComments";
                    n = 1;
                } else {
                    n = 0;
                    string2 = null;
                    if (c == 'p') {
                        string2 = "prettyPrinting";
                        n = 5;
                    }
                }
            }
        } else {
            string2 = "prettyIndent";
            n = 4;
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        if (n == 0) {
            return super.findInstanceIdInfo(string);
        }
        if (n != 1 && n != 2 && n != 3 && n != 4 && n != 5) {
            throw new IllegalStateException();
        }
        return XMLCtor.instanceIdInfo(6, n + super.getMaxInstanceId());
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 8) {
            string2 = "settings";
            n = 2;
        } else if (n2 == 11) {
            string2 = "setSettings";
            n = 3;
        } else {
            n = 0;
            string2 = null;
            if (n2 == 15) {
                string2 = "defaultSettings";
                n = 1;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    protected String getInstanceIdName(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                if (n2 != 3) {
                    if (n2 != 4) {
                        if (n2 != 5) {
                            return super.getInstanceIdName(n);
                        }
                        return "prettyPrinting";
                    }
                    return "prettyIndent";
                }
                return "ignoreWhitespace";
            }
            return "ignoreProcessingInstructions";
        }
        return "ignoreComments";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                if (n2 != 3) {
                    if (n2 != 4) {
                        if (n2 != 5) {
                            return super.getInstanceIdValue(n);
                        }
                        return ScriptRuntime.wrapBoolean(this.options.isPrettyPrinting());
                    }
                    return ScriptRuntime.wrapInt(this.options.getPrettyIndent());
                }
                return ScriptRuntime.wrapBoolean(this.options.isIgnoreWhitespace());
            }
            return ScriptRuntime.wrapBoolean(this.options.isIgnoreProcessingInstructions());
        }
        return ScriptRuntime.wrapBoolean(this.options.isIgnoreComments());
    }

    @Override
    protected int getMaxInstanceId() {
        return 5 + super.getMaxInstanceId();
    }

    @Override
    public boolean hasInstance(Scriptable scriptable) {
        return scriptable instanceof XML || scriptable instanceof XMLList;
        {
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) throw new IllegalArgumentException(String.valueOf((int)n));
                n2 = 1;
                string = "setSettings";
            } else {
                string = "settings";
                n2 = 0;
            }
        } else {
            n2 = 0;
            string = "defaultSettings";
        }
        this.initPrototypeMethod(XMLCTOR_TAG, n, string, n2);
    }

    @Override
    protected void setInstanceIdValue(int n, Object object) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                if (n2 != 3) {
                    if (n2 != 4) {
                        if (n2 != 5) {
                            super.setInstanceIdValue(n, object);
                            return;
                        }
                        this.options.setPrettyPrinting(ScriptRuntime.toBoolean(object));
                        return;
                    }
                    this.options.setPrettyIndent(ScriptRuntime.toInt32(object));
                    return;
                }
                this.options.setIgnoreWhitespace(ScriptRuntime.toBoolean(object));
                return;
            }
            this.options.setIgnoreProcessingInstructions(ScriptRuntime.toBoolean(object));
            return;
        }
        this.options.setIgnoreComments(ScriptRuntime.toBoolean(object));
    }
}

