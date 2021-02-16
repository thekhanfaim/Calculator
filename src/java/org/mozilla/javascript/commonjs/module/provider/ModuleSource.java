/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Reader
 *  java.io.Serializable
 *  java.lang.Object
 *  java.net.URI
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.Reader;
import java.io.Serializable;
import java.net.URI;

public class ModuleSource
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final URI base;
    private final Reader reader;
    private final Object securityDomain;
    private final URI uri;
    private final Object validator;

    public ModuleSource(Reader reader, Object object, URI uRI, URI uRI2, Object object2) {
        this.reader = reader;
        this.securityDomain = object;
        this.uri = uRI;
        this.base = uRI2;
        this.validator = object2;
    }

    public URI getBase() {
        return this.base;
    }

    public Reader getReader() {
        return this.reader;
    }

    public Object getSecurityDomain() {
        return this.securityDomain;
    }

    public URI getUri() {
        return this.uri;
    }

    public Object getValidator() {
        return this.validator;
    }
}

