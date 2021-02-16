/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.Object
 *  java.lang.String
 *  java.net.URI
 *  java.net.URISyntaxException
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;

public interface ModuleSourceProvider {
    public static final ModuleSource NOT_MODIFIED;

    static {
        ModuleSource moduleSource;
        NOT_MODIFIED = moduleSource = new ModuleSource(null, null, null, null, null);
    }

    public ModuleSource loadSource(String var1, Scriptable var2, Object var3) throws IOException, URISyntaxException;

    public ModuleSource loadSource(URI var1, URI var2, Object var3) throws IOException, URISyntaxException;
}

