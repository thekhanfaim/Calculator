/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.net.URLConnection
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.Serializable;
import java.net.URLConnection;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionExpiryCalculator;

public class DefaultUrlConnectionExpiryCalculator
implements UrlConnectionExpiryCalculator,
Serializable {
    private static final long serialVersionUID = 1L;
    private final long relativeExpiry;

    public DefaultUrlConnectionExpiryCalculator() {
        this(60000L);
    }

    public DefaultUrlConnectionExpiryCalculator(long l) {
        if (l >= 0L) {
            this.relativeExpiry = l;
            return;
        }
        throw new IllegalArgumentException("relativeExpiry < 0");
    }

    @Override
    public long calculateExpiry(URLConnection uRLConnection) {
        return System.currentTimeMillis() + this.relativeExpiry;
    }
}

