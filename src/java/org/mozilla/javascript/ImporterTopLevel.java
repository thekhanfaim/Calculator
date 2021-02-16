/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Deprecated
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

public class ImporterTopLevel
extends TopLevel {
    private static final Object IMPORTER_TAG = "Importer";
    private static final int Id_constructor = 1;
    private static final int Id_importClass = 2;
    private static final int Id_importPackage = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    private static final long serialVersionUID = -9095380847465315412L;
    private ObjArray importedPackages = new ObjArray();
    private boolean topScopeFlag;

    public ImporterTopLevel() {
    }

    public ImporterTopLevel(Context context) {
        this(context, false);
    }

    public ImporterTopLevel(Context context, boolean bl) {
        this.initStandardObjects(context, bl);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private Object getPackageProperty(String string, Scriptable scriptable) {
        ObjArray objArray;
        Object object = NOT_FOUND;
        ObjArray objArray2 = objArray = this.importedPackages;
        // MONITORENTER : objArray2
        Object[] arrobject = this.importedPackages.toArray();
        // MONITOREXIT : objArray2
        int n = 0;
        while (n < arrobject.length) {
            Object object2 = ((NativeJavaPackage)arrobject[n]).getPkgProperty(string, scriptable, false);
            if (object2 != null && !(object2 instanceof NativeJavaPackage)) {
                if (object != NOT_FOUND) throw Context.reportRuntimeError2("msg.ambig.import", object.toString(), object2.toString());
                object = object2;
            }
            ++n;
        }
        return object;
        {
            catch (Throwable throwable) {}
            {
                // MONITOREXIT : objArray2
                throw throwable;
            }
        }
    }

    private void importClass(NativeJavaClass nativeJavaClass) {
        String string = nativeJavaClass.getClassObject().getName();
        String string2 = string.substring(1 + string.lastIndexOf(46));
        Object object = this.get(string2, this);
        if (object != NOT_FOUND && object != nativeJavaClass) {
            throw Context.reportRuntimeError1("msg.prop.defined", string2);
        }
        this.put(string2, (Scriptable)this, (Object)nativeJavaClass);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void importPackage(NativeJavaPackage nativeJavaPackage) {
        ObjArray objArray;
        if (nativeJavaPackage == null) {
            return;
        }
        ObjArray objArray2 = objArray = this.importedPackages;
        synchronized (objArray2) {
            int n = 0;
            do {
                if (n == this.importedPackages.size()) {
                    this.importedPackages.add(nativeJavaPackage);
                    return;
                }
                if (nativeJavaPackage.equals(this.importedPackages.get(n))) {
                    return;
                }
                ++n;
            } while (true);
        }
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new ImporterTopLevel().exportAsJSClass(3, scriptable, bl);
    }

    private Object js_construct(Scriptable scriptable, Object[] arrobject) {
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof NativeJavaClass) {
                importerTopLevel.importClass((NativeJavaClass)object);
                continue;
            }
            if (object instanceof NativeJavaPackage) {
                importerTopLevel.importPackage((NativeJavaPackage)object);
                continue;
            }
            throw Context.reportRuntimeError1("msg.not.class.not.pkg", Context.toString(object));
        }
        importerTopLevel.setParentScope(scriptable);
        importerTopLevel.setPrototype(this);
        return importerTopLevel;
    }

    private Object js_importClass(Object[] arrobject) {
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof NativeJavaClass) {
                this.importClass((NativeJavaClass)object);
                continue;
            }
            throw Context.reportRuntimeError1("msg.not.class", Context.toString(object));
        }
        return Undefined.instance;
    }

    private Object js_importPackage(Object[] arrobject) {
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof NativeJavaPackage) {
                this.importPackage((NativeJavaPackage)object);
                continue;
            }
            throw Context.reportRuntimeError1("msg.not.pkg", Context.toString(object));
        }
        return Undefined.instance;
    }

    private ImporterTopLevel realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (this.topScopeFlag) {
            return this;
        }
        if (scriptable instanceof ImporterTopLevel) {
            return (ImporterTopLevel)scriptable;
        }
        throw ImporterTopLevel.incompatibleCallError(idFunctionObject);
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(IMPORTER_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        if (n != 1) {
            if (n != 2) {
                if (n == 3) {
                    return this.realThis(scriptable2, idFunctionObject).js_importPackage(arrobject);
                }
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            return this.realThis(scriptable2, idFunctionObject).js_importClass(arrobject);
        }
        return this.js_construct(scriptable, arrobject);
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 11) {
            char c = string.charAt(0);
            if (c == 'c') {
                string2 = "constructor";
                n = 1;
            } else {
                n = 0;
                string2 = null;
                if (c == 'i') {
                    string2 = "importClass";
                    n = 2;
                }
            }
        } else {
            n = 0;
            string2 = null;
            if (n2 == 13) {
                string2 = "importPackage";
                n = 3;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public Object get(String string, Scriptable scriptable) {
        Object object = super.get(string, scriptable);
        if (object != NOT_FOUND) {
            return object;
        }
        return this.getPackageProperty(string, scriptable);
    }

    @Override
    public String getClassName() {
        if (this.topScopeFlag) {
            return "global";
        }
        return "JavaImporter";
    }

    @Override
    public boolean has(String string, Scriptable scriptable) {
        return super.has(string, scriptable) || this.getPackageProperty(string, scriptable) != NOT_FOUND;
        {
        }
    }

    @Deprecated
    public void importPackage(Context context, Scriptable scriptable, Object[] arrobject, Function function) {
        this.js_importPackage(arrobject);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) throw new IllegalArgumentException(String.valueOf((int)n));
                n2 = 1;
                string = "importPackage";
            } else {
                n2 = 1;
                string = "importClass";
            }
        } else {
            n2 = 0;
            string = "constructor";
        }
        this.initPrototypeMethod(IMPORTER_TAG, n, string, n2);
    }

    public void initStandardObjects(Context context, boolean bl) {
        context.initStandardObjects(this, bl);
        this.topScopeFlag = true;
        IdFunctionObject idFunctionObject = this.exportAsJSClass(3, this, false);
        if (bl) {
            idFunctionObject.sealObject();
        }
        this.delete("constructor");
    }
}

