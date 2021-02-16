/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.net.MalformedURLException
 *  java.net.URL
 *  java.security.AccessControlContext
 *  java.security.AccessControlException
 *  java.security.AccessController
 *  java.security.CodeSource
 *  java.security.Permission
 *  java.security.PermissionCollection
 *  java.security.Policy
 *  java.security.PrivilegedAction
 *  java.security.ProtectionDomain
 *  java.security.cert.Certificate
 *  java.util.Enumeration
 */
package org.mozilla.javascript.tools.shell;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Enumeration;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Main;
import org.mozilla.javascript.tools.shell.SecurityProxy;

public class JavaPolicySecurity
extends SecurityProxy {
    public JavaPolicySecurity() {
        new CodeSource(null, (Certificate[])null);
    }

    private ProtectionDomain getDynamicDomain(ProtectionDomain protectionDomain) {
        return new ProtectionDomain(null, (PermissionCollection)new ContextPermissions(protectionDomain));
    }

    private ProtectionDomain getUrlDomain(URL uRL) {
        CodeSource codeSource = new CodeSource(uRL, (Certificate[])null);
        return new ProtectionDomain(codeSource, Policy.getPolicy().getPermissions(codeSource));
    }

    private URL getUrlObj(String string2) {
        try {
            URL uRL = new URL(string2);
            return uRL;
        }
        catch (MalformedURLException malformedURLException) {
            String string3 = System.getProperty((String)"user.dir").replace('\\', '/');
            if (!string3.endsWith("/")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string3);
                stringBuilder.append('/');
                string3 = stringBuilder.toString();
            }
            try {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("file:");
                stringBuilder.append(string3);
                URL uRL = new URL(new URL(stringBuilder.toString()), string2);
                return uRL;
            }
            catch (MalformedURLException malformedURLException2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Can not construct file URL for '");
                stringBuilder.append(string2);
                stringBuilder.append("':");
                stringBuilder.append(malformedURLException2.getMessage());
                throw new RuntimeException(stringBuilder.toString());
            }
        }
    }

    @Override
    protected void callProcessFileSecure(final Context context, final Scriptable scriptable, final String string2) {
        AccessController.doPrivileged((PrivilegedAction)new PrivilegedAction<Object>(){

            public Object run() {
                URL uRL = JavaPolicySecurity.this.getUrlObj(string2);
                ProtectionDomain protectionDomain = JavaPolicySecurity.this.getUrlDomain(uRL);
                try {
                    Main.processFileSecure(context, scriptable, uRL.toExternalForm(), (Object)protectionDomain);
                    return null;
                }
                catch (IOException iOException) {
                    throw new RuntimeException((Throwable)iOException);
                }
            }
        });
    }

    @Override
    public Object callWithDomain(Object object, final Context context, final Callable callable, final Scriptable scriptable, final Scriptable scriptable2, final Object[] arrobject) {
        AccessControlContext accessControlContext = new AccessControlContext(new ProtectionDomain[]{this.getDynamicDomain((ProtectionDomain)object)});
        PrivilegedAction<Object> privilegedAction = new PrivilegedAction<Object>(){

            public Object run() {
                return callable.call(context, scriptable, scriptable2, arrobject);
            }
        };
        return AccessController.doPrivileged((PrivilegedAction)privilegedAction, (AccessControlContext)accessControlContext);
    }

    @Override
    public GeneratedClassLoader createClassLoader(final ClassLoader classLoader, Object object) {
        return (GeneratedClassLoader)AccessController.doPrivileged((PrivilegedAction)new PrivilegedAction<Loader>((ProtectionDomain)object){
            final /* synthetic */ ProtectionDomain val$domain;
            {
                this.val$domain = protectionDomain;
            }

            public Loader run() {
                return new Loader(classLoader, this.val$domain);
            }
        });
    }

    @Override
    public Object getDynamicSecurityDomain(Object object) {
        return this.getDynamicDomain((ProtectionDomain)object);
    }

    @Override
    public Class<?> getStaticSecurityDomainClassInternal() {
        return ProtectionDomain.class;
    }

    private static class ContextPermissions
    extends PermissionCollection {
        static final long serialVersionUID = -1721494496320750721L;
        AccessControlContext _context = AccessController.getContext();
        PermissionCollection _statisPermissions;

        ContextPermissions(ProtectionDomain protectionDomain) {
            if (protectionDomain != null) {
                this._statisPermissions = protectionDomain.getPermissions();
            }
            this.setReadOnly();
        }

        public void add(Permission permission) {
            throw new RuntimeException("NOT IMPLEMENTED");
        }

        public Enumeration<Permission> elements() {
            return new Enumeration<Permission>(){

                public boolean hasMoreElements() {
                    return false;
                }

                public Permission nextElement() {
                    return null;
                }
            };
        }

        public boolean implies(Permission permission) {
            PermissionCollection permissionCollection = this._statisPermissions;
            if (permissionCollection != null && !permissionCollection.implies(permission)) {
                return false;
            }
            try {
                this._context.checkPermission(permission);
                return true;
            }
            catch (AccessControlException accessControlException) {
                return false;
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.getClass().getName());
            stringBuilder.append('@');
            stringBuilder.append(Integer.toHexString((int)System.identityHashCode((Object)((Object)this))));
            stringBuilder.append(" (context=");
            stringBuilder.append((Object)this._context);
            stringBuilder.append(", static_permitions=");
            stringBuilder.append((Object)this._statisPermissions);
            stringBuilder.append(')');
            return stringBuilder.toString();
        }

    }

    private static class Loader
    extends ClassLoader
    implements GeneratedClassLoader {
        private ProtectionDomain domain;

        Loader(ClassLoader classLoader, ProtectionDomain protectionDomain) {
            ClassLoader classLoader2 = classLoader != null ? classLoader : Loader.getSystemClassLoader();
            super(classLoader2);
            this.domain = protectionDomain;
        }

        @Override
        public Class<?> defineClass(String string2, byte[] arrby) {
            return super.defineClass(string2, arrby, 0, arrby.length, this.domain);
        }

        @Override
        public void linkClass(Class<?> class_) {
            this.resolveClass(class_);
        }
    }

}

