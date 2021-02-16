/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.optimizer.OptRuntime;

public final class -$$Lambda$OptRuntime$nkcl7ufBnCixh_eNHP8jiz3N5n8
implements ContextAction {
    public final /* synthetic */ String[] f$0;
    public final /* synthetic */ Script f$1;

    public /* synthetic */ -$$Lambda$OptRuntime$nkcl7ufBnCixh_eNHP8jiz3N5n8(String[] arrstring, Script script) {
        this.f$0 = arrstring;
        this.f$1 = script;
    }

    public final Object run(Context context) {
        return OptRuntime.lambda$main$0(this.f$0, this.f$1, context);
    }
}

