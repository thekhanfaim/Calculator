/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.concurrent.ConcurrentHashMap
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

public class StrongCachingModuleScriptProvider
extends CachingModuleScriptProviderBase {
    private static final long serialVersionUID = 1L;
    private final Map<String, CachingModuleScriptProviderBase.CachedModuleScript> modules = new ConcurrentHashMap(16, 0.75f, StrongCachingModuleScriptProvider.getConcurrencyLevel());

    public StrongCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
        super(moduleSourceProvider);
    }

    @Override
    protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String string2) {
        return (CachingModuleScriptProviderBase.CachedModuleScript)this.modules.get((Object)string2);
    }

    @Override
    protected void putLoadedModule(String string2, ModuleScript moduleScript, Object object) {
        this.modules.put((Object)string2, (Object)new CachingModuleScriptProviderBase.CachedModuleScript(moduleScript, object));
    }
}

