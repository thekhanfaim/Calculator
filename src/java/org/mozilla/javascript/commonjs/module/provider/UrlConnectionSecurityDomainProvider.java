/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.net.URLConnection
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.net.URLConnection;

public interface UrlConnectionSecurityDomainProvider {
    public Object getSecurityDomain(URLConnection var1);
}

