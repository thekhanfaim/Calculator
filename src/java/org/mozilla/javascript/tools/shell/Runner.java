/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Runnable
 */
package org.mozilla.javascript.tools.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

class Runner
implements Runnable,
ContextAction<Object> {
    private Object[] args;
    private Function f;
    ContextFactory factory;
    private Script s;
    private Scriptable scope;

    Runner(Scriptable scriptable, Function function, Object[] arrobject) {
        this.scope = scriptable;
        this.f = function;
        this.args = arrobject;
    }

    Runner(Scriptable scriptable, Script script) {
        this.scope = scriptable;
        this.s = script;
    }

    @Override
    public Object run(Context context) {
        Function function = this.f;
        if (function != null) {
            Scriptable scriptable = this.scope;
            return function.call(context, scriptable, scriptable, this.args);
        }
        return this.s.exec(context, this.scope);
    }

    public void run() {
        this.factory.call(this);
    }
}

