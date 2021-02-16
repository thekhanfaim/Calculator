/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Reader
 *  java.io.Serializable
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.URI
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

public abstract class CachingModuleScriptProviderBase
implements ModuleScriptProvider,
Serializable {
    private static final int loadConcurrencyLevel = 0;
    private static final int loadLockCount = 0;
    private static final int loadLockMask = 0;
    private static final int loadLockShift = 0;
    private static final long serialVersionUID = 1L;
    private final Object[] loadLocks = new Object[loadLockCount];
    private final ModuleSourceProvider moduleSourceProvider;

    static {
        int n;
        loadConcurrencyLevel = 8 * Runtime.getRuntime().availableProcessors();
        int n2 = 0;
        for (n = 1; n < loadConcurrencyLevel; n <<= 1) {
            ++n2;
        }
        loadLockShift = 32 - n2;
        loadLockMask = n - 1;
        loadLockCount = n;
    }

    protected CachingModuleScriptProviderBase(ModuleSourceProvider moduleSourceProvider) {
        Object[] arrobject;
        for (int i = 0; i < (arrobject = this.loadLocks).length; ++i) {
            arrobject[i] = new Object();
        }
        this.moduleSourceProvider = moduleSourceProvider;
    }

    private static boolean equal(Object object, Object object2) {
        if (object == null) {
            return object2 == null;
        }
        return object.equals(object2);
    }

    protected static int getConcurrencyLevel() {
        return loadLockCount;
    }

    private static Object getValidator(CachedModuleScript cachedModuleScript) {
        if (cachedModuleScript == null) {
            return null;
        }
        return cachedModuleScript.getValidator();
    }

    protected abstract CachedModuleScript getLoadedModule(String var1);

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public ModuleScript getModuleScript(Context context, String string2, URI uRI, URI uRI2, Scriptable scriptable) throws Exception {
        Reader reader;
        block17 : {
            Object object;
            ModuleScript moduleScript;
            CachedModuleScript cachedModuleScript = this.getLoadedModule(string2);
            Object object2 = CachingModuleScriptProviderBase.getValidator(cachedModuleScript);
            ModuleSource moduleSource = uRI == null ? this.moduleSourceProvider.loadSource(string2, scriptable, object2) : this.moduleSourceProvider.loadSource(uRI, uRI2, object2);
            ModuleSource moduleSource2 = moduleSource;
            if (moduleSource2 == ModuleSourceProvider.NOT_MODIFIED) {
                return cachedModuleScript.getModule();
            }
            if (moduleSource2 == null) {
                return null;
            }
            reader = moduleSource2.getReader();
            int n = string2.hashCode();
            Object object3 = object = this.loadLocks[n >>> loadLockShift & loadLockMask];
            // MONITORENTER : object3
            CachedModuleScript cachedModuleScript2 = this.getLoadedModule(string2);
            if (cachedModuleScript2 != null && !CachingModuleScriptProviderBase.equal(object2, CachingModuleScriptProviderBase.getValidator(cachedModuleScript2))) {
                ModuleScript moduleScript2 = cachedModuleScript2.getModule();
                // MONITOREXIT : object3
                if (reader == null) return moduleScript2;
                reader.close();
                return moduleScript2;
            }
            URI uRI3 = moduleSource2.getUri();
            String string3 = uRI3.toString();
            Object object4 = moduleSource2.getSecurityDomain();
            try {
                moduleScript = new ModuleScript(context.compileReader(reader, string3, 1, object4), uRI3, moduleSource2.getBase());
                this.putLoadedModule(string2, moduleScript, moduleSource2.getValidator());
                // MONITOREXIT : object3
                if (reader == null) return moduleScript;
            }
            catch (Throwable throwable) {}
            reader.close();
            return moduleScript;
            try {
                throw throwable;
            }
            catch (Throwable throwable) {
                break block17;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        try {
            void var11_23;
            throw var11_23;
        }
        catch (Throwable throwable) {
            if (reader == null) throw throwable;
            try {
                reader.close();
                throw throwable;
            }
            catch (Throwable throwable2) {
                throw throwable;
            }
        }
    }

    protected abstract void putLoadedModule(String var1, ModuleScript var2, Object var3);

    public static class CachedModuleScript {
        private final ModuleScript moduleScript;
        private final Object validator;

        public CachedModuleScript(ModuleScript moduleScript, Object object) {
            this.moduleScript = moduleScript;
            this.validator = object;
        }

        ModuleScript getModule() {
            return this.moduleScript;
        }

        Object getValidator() {
            return this.validator;
        }
    }

}

