/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.ObjectInputStream
 *  java.io.ObjectOutputStream
 *  java.lang.ClassNotFoundException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.ref.Reference
 *  java.lang.ref.ReferenceQueue
 *  java.lang.ref.SoftReference
 *  java.net.URI
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.concurrent.ConcurrentHashMap
 *  java.util.concurrent.ConcurrentMap
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.provider.CachingModuleScriptProviderBase;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

public class SoftCachingModuleScriptProvider
extends CachingModuleScriptProviderBase {
    private static final long serialVersionUID = 1L;
    private transient ReferenceQueue<Script> scriptRefQueue = new ReferenceQueue();
    private transient ConcurrentMap<String, ScriptReference> scripts = new ConcurrentHashMap(16, 0.75f, SoftCachingModuleScriptProvider.getConcurrencyLevel());

    public SoftCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
        super(moduleSourceProvider);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.scriptRefQueue = new ReferenceQueue();
        this.scripts = new ConcurrentHashMap();
        for (Map.Entry entry : ((Map)objectInputStream.readObject()).entrySet()) {
            CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = (CachingModuleScriptProviderBase.CachedModuleScript)entry.getValue();
            this.putLoadedModule((String)entry.getKey(), cachedModuleScript.getModule(), cachedModuleScript.getValidator());
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        HashMap hashMap = new HashMap();
        for (Map.Entry entry : this.scripts.entrySet()) {
            CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = ((ScriptReference)((Object)entry.getValue())).getCachedModuleScript();
            if (cachedModuleScript == null) continue;
            hashMap.put(entry.getKey(), (Object)cachedModuleScript);
        }
        objectOutputStream.writeObject((Object)hashMap);
    }

    @Override
    protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String string2) {
        ScriptReference scriptReference = (ScriptReference)((Object)this.scripts.get((Object)string2));
        if (scriptReference != null) {
            return scriptReference.getCachedModuleScript();
        }
        return null;
    }

    @Override
    public ModuleScript getModuleScript(Context context, String string2, URI uRI, URI uRI2, Scriptable scriptable) throws Exception {
        ScriptReference scriptReference;
        while ((scriptReference = (ScriptReference)this.scriptRefQueue.poll()) != null) {
            this.scripts.remove((Object)scriptReference.getModuleId(), (Object)scriptReference);
        }
        return super.getModuleScript(context, string2, uRI, uRI2, scriptable);
    }

    @Override
    protected void putLoadedModule(String string2, ModuleScript moduleScript, Object object) {
        ConcurrentMap<String, ScriptReference> concurrentMap = this.scripts;
        ScriptReference scriptReference = new ScriptReference(moduleScript.getScript(), string2, moduleScript.getUri(), moduleScript.getBase(), object, this.scriptRefQueue);
        concurrentMap.put((Object)string2, (Object)scriptReference);
    }

    private static class ScriptReference
    extends SoftReference<Script> {
        private final URI base;
        private final String moduleId;
        private final URI uri;
        private final Object validator;

        ScriptReference(Script script, String string2, URI uRI, URI uRI2, Object object, ReferenceQueue<Script> referenceQueue) {
            super((Object)script, referenceQueue);
            this.moduleId = string2;
            this.uri = uRI;
            this.base = uRI2;
            this.validator = object;
        }

        CachingModuleScriptProviderBase.CachedModuleScript getCachedModuleScript() {
            Script script = (Script)this.get();
            if (script == null) {
                return null;
            }
            return new CachingModuleScriptProviderBase.CachedModuleScript(new ModuleScript(script, this.uri, this.base), this.validator);
        }

        String getModuleId() {
            return this.moduleId;
        }
    }

}

