/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.io.Serializable
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.net.HttpURLConnection
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.net.URL
 *  java.net.URLConnection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.commonjs.module.provider.DefaultUrlConnectionExpiryCalculator;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;
import org.mozilla.javascript.commonjs.module.provider.ParsedContentType;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionExpiryCalculator;
import org.mozilla.javascript.commonjs.module.provider.UrlConnectionSecurityDomainProvider;

public class UrlModuleSourceProvider
extends ModuleSourceProviderBase {
    private static final long serialVersionUID = 1L;
    private final Iterable<URI> fallbackUris;
    private final Iterable<URI> privilegedUris;
    private final UrlConnectionExpiryCalculator urlConnectionExpiryCalculator;
    private final UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider;

    public UrlModuleSourceProvider(Iterable<URI> iterable, Iterable<URI> iterable2) {
        this(iterable, iterable2, new DefaultUrlConnectionExpiryCalculator(), null);
    }

    public UrlModuleSourceProvider(Iterable<URI> iterable, Iterable<URI> iterable2, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator, UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider) {
        this.privilegedUris = iterable;
        this.fallbackUris = iterable2;
        this.urlConnectionExpiryCalculator = urlConnectionExpiryCalculator;
        this.urlConnectionSecurityDomainProvider = urlConnectionSecurityDomainProvider;
    }

    private void close(URLConnection uRLConnection) {
        try {
            uRLConnection.getInputStream().close();
            return;
        }
        catch (IOException iOException) {
            this.onFailedClosingUrlConnection(uRLConnection, iOException);
            return;
        }
    }

    private static String getCharacterEncoding(URLConnection uRLConnection) {
        ParsedContentType parsedContentType = new ParsedContentType(uRLConnection.getContentType());
        String string2 = parsedContentType.getEncoding();
        if (string2 != null) {
            return string2;
        }
        String string3 = parsedContentType.getContentType();
        if (string3 != null && string3.startsWith("text/")) {
            return "8859_1";
        }
        return "utf-8";
    }

    private static Reader getReader(URLConnection uRLConnection) throws IOException {
        return new InputStreamReader(uRLConnection.getInputStream(), UrlModuleSourceProvider.getCharacterEncoding(uRLConnection));
    }

    private Object getSecurityDomain(URLConnection uRLConnection) {
        UrlConnectionSecurityDomainProvider urlConnectionSecurityDomainProvider = this.urlConnectionSecurityDomainProvider;
        if (urlConnectionSecurityDomainProvider == null) {
            return null;
        }
        return urlConnectionSecurityDomainProvider.getSecurityDomain(uRLConnection);
    }

    private ModuleSource loadFromPathList(String string2, Object object, Iterable<URI> iterable) throws IOException, URISyntaxException {
        if (iterable == null) {
            return null;
        }
        for (URI uRI : iterable) {
            ModuleSource moduleSource = this.loadFromUri(uRI.resolve(string2), uRI, object);
            if (moduleSource == null) continue;
            return moduleSource;
        }
        return null;
    }

    @Override
    protected boolean entityNeedsRevalidation(Object object) {
        return !(object instanceof URLValidator) || ((URLValidator)object).entityNeedsRevalidation();
        {
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected ModuleSource loadFromActualUri(URI uRI, URI uRI2, Object object) throws IOException {
        URLValidator uRLValidator;
        URL uRL = uRI2 == null ? null : uRI2.toURL();
        URL uRL2 = new URL(uRL, uRI.toString());
        long l = System.currentTimeMillis();
        URLConnection uRLConnection = this.openUrlConnection(uRL2);
        if (object instanceof URLValidator) {
            URLValidator uRLValidator2 = (URLValidator)object;
            URLValidator uRLValidator3 = uRLValidator2.appliesTo(uRI) ? uRLValidator2 : null;
            uRLValidator = uRLValidator3;
        } else {
            uRLValidator = null;
        }
        if (uRLValidator != null) {
            uRLValidator.applyConditionals(uRLConnection);
        }
        try {
            uRLConnection.connect();
            if (uRLValidator != null && !uRLValidator.updateValidator(uRLConnection, l, this.urlConnectionExpiryCalculator)) {
                this.close(uRLConnection);
                return NOT_MODIFIED;
            }
            Reader reader = UrlModuleSourceProvider.getReader(uRLConnection);
            Object object2 = this.getSecurityDomain(uRLConnection);
            URLValidator uRLValidator4 = new URLValidator(uRI, uRLConnection, l, this.urlConnectionExpiryCalculator);
            return new ModuleSource(reader, object2, uRI, uRI2, uRLValidator4);
        }
        catch (IOException iOException) {
            this.close(uRLConnection);
            throw iOException;
        }
        catch (RuntimeException runtimeException) {
            this.close(uRLConnection);
            throw runtimeException;
        }
        catch (FileNotFoundException fileNotFoundException) {
            return null;
        }
    }

    @Override
    protected ModuleSource loadFromFallbackLocations(String string2, Object object) throws IOException, URISyntaxException {
        return this.loadFromPathList(string2, object, this.fallbackUris);
    }

    @Override
    protected ModuleSource loadFromPrivilegedLocations(String string2, Object object) throws IOException, URISyntaxException {
        return this.loadFromPathList(string2, object, this.privilegedUris);
    }

    @Override
    protected ModuleSource loadFromUri(URI uRI, URI uRI2, Object object) throws IOException, URISyntaxException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((Object)uRI);
        stringBuilder.append(".js");
        ModuleSource moduleSource = this.loadFromActualUri(new URI(stringBuilder.toString()), uRI2, object);
        if (moduleSource != null) {
            return moduleSource;
        }
        return this.loadFromActualUri(uRI, uRI2, object);
    }

    protected void onFailedClosingUrlConnection(URLConnection uRLConnection, IOException iOException) {
    }

    protected URLConnection openUrlConnection(URL uRL) throws IOException {
        return uRL.openConnection();
    }

    private static class URLValidator
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String entityTags;
        private long expiry;
        private final long lastModified;
        private final URI uri;

        public URLValidator(URI uRI, URLConnection uRLConnection, long l, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) {
            this.uri = uRI;
            this.lastModified = uRLConnection.getLastModified();
            this.entityTags = this.getEntityTags(uRLConnection);
            this.expiry = this.calculateExpiry(uRLConnection, l, urlConnectionExpiryCalculator);
        }

        private long calculateExpiry(URLConnection uRLConnection, long l, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) {
            long l2;
            if ("no-cache".equals((Object)uRLConnection.getHeaderField("Pragma"))) {
                return 0L;
            }
            String string2 = uRLConnection.getHeaderField("Cache-Control");
            if (string2 != null) {
                if (string2.indexOf("no-cache") != -1) {
                    return 0L;
                }
                int n = this.getMaxAge(string2);
                if (-1 != n) {
                    long l3 = System.currentTimeMillis();
                    return l3 - (Math.max((long)Math.max((long)0L, (long)(l3 - uRLConnection.getDate())), (long)(1000L * (long)uRLConnection.getHeaderFieldInt("Age", 0))) + (l3 - l)) + 1000L * (long)n;
                }
            }
            if ((l2 = uRLConnection.getHeaderFieldDate("Expires", -1L)) != -1L) {
                return l2;
            }
            if (urlConnectionExpiryCalculator == null) {
                return 0L;
            }
            return urlConnectionExpiryCalculator.calculateExpiry(uRLConnection);
        }

        private String getEntityTags(URLConnection uRLConnection) {
            List list = (List)uRLConnection.getHeaderFields().get((Object)"ETag");
            if (list != null && !list.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                Iterator iterator = list.iterator();
                stringBuilder.append((String)iterator.next());
                while (iterator.hasNext()) {
                    stringBuilder.append(", ");
                    stringBuilder.append((String)iterator.next());
                }
                return stringBuilder.toString();
            }
            return null;
        }

        private int getMaxAge(String string2) {
            int n = string2.indexOf("max-age");
            if (n == -1) {
                return -1;
            }
            int n2 = string2.indexOf(61, n + 7);
            if (n2 == -1) {
                return -1;
            }
            int n3 = string2.indexOf(44, n2 + 1);
            String string3 = n3 == -1 ? string2.substring(n2 + 1) : string2.substring(n2 + 1, n3);
            try {
                int n4 = Integer.parseInt((String)string3);
                return n4;
            }
            catch (NumberFormatException numberFormatException) {
                return -1;
            }
        }

        private boolean isResourceChanged(URLConnection uRLConnection) throws IOException {
            if (uRLConnection instanceof HttpURLConnection) {
                return ((HttpURLConnection)uRLConnection).getResponseCode() == 304;
            }
            return this.lastModified != uRLConnection.getLastModified();
        }

        boolean appliesTo(URI uRI) {
            return this.uri.equals((Object)uRI);
        }

        void applyConditionals(URLConnection uRLConnection) {
            String string2;
            long l = this.lastModified;
            if (l != 0L) {
                uRLConnection.setIfModifiedSince(l);
            }
            if ((string2 = this.entityTags) != null && string2.length() > 0) {
                uRLConnection.addRequestProperty("If-None-Match", this.entityTags);
            }
        }

        boolean entityNeedsRevalidation() {
            return System.currentTimeMillis() > this.expiry;
        }

        boolean updateValidator(URLConnection uRLConnection, long l, UrlConnectionExpiryCalculator urlConnectionExpiryCalculator) throws IOException {
            boolean bl = this.isResourceChanged(uRLConnection);
            if (!bl) {
                this.expiry = this.calculateExpiry(uRLConnection, l, urlConnectionExpiryCalculator);
            }
            return bl;
        }
    }

}

