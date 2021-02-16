/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.DefaultErrorReporter;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Evaluator;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

class NativeScript
extends BaseFunction {
    private static final int Id_compile = 3;
    private static final int Id_constructor = 1;
    private static final int Id_exec = 4;
    private static final int Id_toString = 2;
    private static final int MAX_PROTOTYPE_ID = 4;
    private static final Object SCRIPT_TAG = "Script";
    private static final long serialVersionUID = -6795101161980121700L;
    private Script script;

    private NativeScript(Script script) {
        this.script = script;
    }

    private static Script compile(Context context, String string) {
        int[] arrn = new int[]{0};
        String string2 = Context.getSourcePositionFromStack(arrn);
        if (string2 == null) {
            string2 = "<Script object>";
            arrn[0] = 1;
        }
        ErrorReporter errorReporter = DefaultErrorReporter.forEval(context.getErrorReporter());
        int n = arrn[0];
        return context.compileString(string, null, errorReporter, string2, n, null);
    }

    static void init(Scriptable scriptable, boolean bl) {
        new NativeScript(null).exportAsJSClass(4, scriptable, bl);
    }

    private static NativeScript realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeScript) {
            return (NativeScript)scriptable;
        }
        throw NativeScript.incompatibleCallError(idFunctionObject);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        Script script = this.script;
        if (script != null) {
            return script.exec(context, scriptable);
        }
        return Undefined.instance;
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        throw Context.reportRuntimeError0("msg.script.is.not.constructor");
    }

    @Override
    String decompile(int n, int n2) {
        Script script = this.script;
        if (script instanceof NativeFunction) {
            return ((NativeFunction)((Object)script)).decompile(n, n2);
        }
        return super.decompile(n, n2);
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(SCRIPT_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        String string = "";
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n != 4) {
                        throw new IllegalArgumentException(String.valueOf((int)n));
                    }
                    throw Context.reportRuntimeError1("msg.cant.call.indirect", "exec");
                }
                NativeScript nativeScript = NativeScript.realThis(scriptable2, idFunctionObject);
                nativeScript.script = NativeScript.compile(context, ScriptRuntime.toString(arrobject, 0));
                return nativeScript;
            }
            Script script = NativeScript.realThis((Scriptable)scriptable2, (IdFunctionObject)idFunctionObject).script;
            if (script == null) {
                return string;
            }
            return context.decompileScript(script, 0);
        }
        if (arrobject.length != 0) {
            string = ScriptRuntime.toString(arrobject[0]);
        }
        NativeScript nativeScript = new NativeScript(NativeScript.compile(context, string));
        ScriptRuntime.setObjectProtoAndParent(nativeScript, scriptable);
        return nativeScript;
    }

    @Override
    protected int findPrototypeId(String string) {
        String string2;
        int n;
        int n2 = string.length();
        if (n2 != 4) {
            if (n2 != 11) {
                if (n2 != 7) {
                    if (n2 != 8) {
                        n = 0;
                        string2 = null;
                    } else {
                        string2 = "toString";
                        n = 2;
                    }
                } else {
                    string2 = "compile";
                    n = 3;
                }
            } else {
                string2 = "constructor";
                n = 1;
            }
        } else {
            string2 = "exec";
            n = 4;
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public String getClassName() {
        return "Script";
    }

    @Override
    public int getLength() {
        return 0;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        int n2;
        String string;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n != 4) throw new IllegalArgumentException(String.valueOf((int)n));
                    string = "exec";
                    n2 = 0;
                } else {
                    n2 = 1;
                    string = "compile";
                }
            } else {
                string = "toString";
                n2 = 0;
            }
        } else {
            n2 = 1;
            string = "constructor";
        }
        this.initPrototypeMethod(SCRIPT_TAG, n, string, n2);
    }
}

