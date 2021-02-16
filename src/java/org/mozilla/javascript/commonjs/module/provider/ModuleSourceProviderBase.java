/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.io.IOException
 *  java.io.Serializable
 *  java.lang.Object
 *  java.lang.String
 *  java.net.MalformedURLException
 *  java.net.URI
 *  java.net.URISyntaxException
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

public abstract class ModuleSourceProviderBase
implements ModuleSourceProvider,
Serializable {
    private static final long serialVersionUID = 1L;

    private static String ensureTrailingSlash(String string2) {
        if (string2.endsWith("/")) {
            return string2;
        }
        return string2.concat("/");
    }

    private ModuleSource loadFromPathArray(String string2, Scriptable scriptable, Object object) throws IOException {
        long l = ScriptRuntime.toUint32(ScriptableObject.getProperty(scriptable, "length"));
        int n = l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
        for (int i = 0; i < n; ++i) {
            String string3 = ModuleSourceProviderBase.ensureTrailingSlash(ScriptableObject.getTypedProperty(scriptable, i, String.class));
            try {
                ModuleSource moduleSource;
                URI uRI = new URI(string3);
                if (!uRI.isAbsolute()) {
                    uRI = new File(string3).toURI().resolve("");
                }
                if ((moduleSource = this.loadFromUri(uRI.resolve(string2), uRI, object)) == null) continue;
                return moduleSource;
            }
            catch (URISyntaxException uRISyntaxException) {
                throw new MalformedURLException(uRISyntaxException.getMessage());
            }
        }
        return null;
    }

    protected boolean entityNeedsRevalidation(Object object) {
        return true;
    }

    protected ModuleSource loadFromFallbackLocations(String string2, Object object) throws IOException, URISyntaxException {
        return null;
    }

    protected ModuleSource loadFromPrivilegedLocations(String string2, Object object) throws IOException, URISyntaxException {
        return null;
    }

    protected abstract ModuleSource loadFromUri(URI var1, URI var2, Object var3) throws IOException, URISyntaxException;

    @Override
    public ModuleSource loadSource(String string2, Scriptable scriptable, Object object) throws IOException, URISyntaxException {
        ModuleSource moduleSource;
        if (!this.entityNeedsRevalidation(object)) {
            return NOT_MODIFIED;
        }
        ModuleSource moduleSource2 = this.loadFromPrivilegedLocations(string2, object);
        if (moduleSource2 != null) {
            return moduleSource2;
        }
        if (scriptable != null && (moduleSource = this.loadFromPathArray(string2, scriptable, object)) != null) {
            return moduleSource;
        }
        return this.loadFromFallbackLocations(string2, object);
    }

    @Override
    public ModuleSource loadSource(URI uRI, URI uRI2, Object object) throws IOException, URISyntaxException {
        return this.loadFromUri(uRI, uRI2, object);
    }
}

