/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.net.URI
 *  java.util.Iterator
 *  java.util.LinkedList
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

public class MultiModuleScriptProvider
implements ModuleScriptProvider {
    private final ModuleScriptProvider[] providers;

    public MultiModuleScriptProvider(Iterable<? extends ModuleScriptProvider> iterable) {
        LinkedList linkedList = new LinkedList();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            linkedList.add((Object)((ModuleScriptProvider)iterator.next()));
        }
        this.providers = (ModuleScriptProvider[])linkedList.toArray((Object[])new ModuleScriptProvider[linkedList.size()]);
    }

    @Override
    public ModuleScript getModuleScript(Context context, String string2, URI uRI, URI uRI2, Scriptable scriptable) throws Exception {
        ModuleScriptProvider[] arrmoduleScriptProvider = this.providers;
        int n = arrmoduleScriptProvider.length;
        for (int i = 0; i < n; ++i) {
            ModuleScript moduleScript = arrmoduleScriptProvider[i].getModuleScript(context, string2, uRI, uRI2, scriptable);
            if (moduleScript == null) continue;
            return moduleScript;
        }
        return null;
    }
}

