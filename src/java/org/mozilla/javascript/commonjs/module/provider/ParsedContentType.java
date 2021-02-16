/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.StringTokenizer
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.Serializable;
import java.util.StringTokenizer;

public final class ParsedContentType
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String contentType;
    private final String encoding;

    public ParsedContentType(String string2) {
        String string3;
        String string4;
        block6 : {
            string4 = null;
            string3 = null;
            if (string2 != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(string2, ";");
                boolean bl = stringTokenizer.hasMoreTokens();
                string4 = null;
                string3 = null;
                if (bl) {
                    String string5;
                    string4 = stringTokenizer.nextToken().trim();
                    do {
                        boolean bl2 = stringTokenizer.hasMoreTokens();
                        string3 = null;
                        if (!bl2) break block6;
                    } while (!(string5 = stringTokenizer.nextToken().trim()).startsWith("charset="));
                    string3 = string5.substring(8).trim();
                    int n = string3.length();
                    if (n > 0) {
                        if (string3.charAt(0) == '\"') {
                            string3 = string3.substring(1);
                        }
                        if (string3.charAt(n - 1) == '\"') {
                            string3 = string3.substring(0, n - 1);
                        }
                    }
                }
            }
        }
        this.contentType = string4;
        this.encoding = string3;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getEncoding() {
        return this.encoding;
    }
}

