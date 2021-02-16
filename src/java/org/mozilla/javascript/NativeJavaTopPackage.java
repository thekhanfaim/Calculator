package org.mozilla.javascript;

public class NativeJavaTopPackage extends NativeJavaPackage implements Function, IdFunctionCall {
   private static final Object FTAG = "JavaTopPackage";
   private static final int Id_getClass = 1;
   private static final String[][] commonPackages = new String[][]{{"java", "lang", "reflect"}, {"java", "io"}, {"java", "math"}, {"java", "net"}, {"java", "util", "zip"}, {"java", "text", "resources"}, {"java", "applet"}, {"javax", "swing"}};
   private static final long serialVersionUID = -1455787259477709999L;

   NativeJavaTopPackage(ClassLoader var1) {
      super(true, "", var1);
   }

   public static void init(Context var0, Scriptable var1, boolean var2) {
      NativeJavaTopPackage var3 = new NativeJavaTopPackage(var0.getApplicationClassLoader());
      var3.setPrototype(getObjectPrototype(var1));
      var3.setParentScope(var1);

      for(int var4 = 0; var4 != commonPackages.length; ++var4) {
         Object var11 = var3;
         int var12 = 0;

         while(true) {
            String[][] var13 = commonPackages;
            if (var12 == var13[var4].length) {
               break;
            }

            var11 = ((NativeJavaPackage)var11).forcePackage(var13[var4][var12], var1);
            ++var12;
         }
      }

      IdFunctionObject var5 = new IdFunctionObject(var3, FTAG, 1, "getClass", 1, var1);
      String[] var6 = ScriptRuntime.getTopPackageNames();
      NativeJavaPackage[] var7 = new NativeJavaPackage[var6.length];

      for(int var8 = 0; var8 < var6.length; ++var8) {
         var7[var8] = (NativeJavaPackage)var3.get(var6[var8], var3);
      }

      ScriptableObject var9 = (ScriptableObject)var1;
      if (var2) {
         var5.sealObject();
      }

      var5.exportAsScopeProperty();
      var9.defineProperty((String)"Packages", (Object)var3, 2);

      for(int var10 = 0; var10 < var6.length; ++var10) {
         var9.defineProperty((String)var6[var10], (Object)var7[var10], 2);
      }

   }

   private Scriptable js_getClass(Context var1, Scriptable var2, Object[] var3) {
      if (var3.length > 0 && var3[0] instanceof Wrapper) {
         Object var5 = this;
         String var6 = ((Wrapper)var3[0]).unwrap().getClass().getName();
         int var7 = 0;

         while(true) {
            int var8 = var6.indexOf(46, var7);
            String var9;
            if (var8 == -1) {
               var9 = var6.substring(var7);
            } else {
               var9 = var6.substring(var7, var8);
            }

            Object var10 = ((Scriptable)var5).get(var9, (Scriptable)var5);
            if (!(var10 instanceof Scriptable)) {
               break;
            }

            var5 = (Scriptable)var10;
            if (var8 == -1) {
               return (Scriptable)var5;
            }

            var7 = var8 + 1;
         }
      }

      EvaluatorException var4 = Context.reportRuntimeError0("msg.not.java.obj");
      throw var4;
   }

   public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      return this.construct(var1, var2, var4);
   }

   public Scriptable construct(Context var1, Scriptable var2, Object[] var3) {
      int var4 = var3.length;
      ClassLoader var5 = null;
      if (var4 != 0) {
         Object var8 = var3[0];
         if (var8 instanceof Wrapper) {
            var8 = ((Wrapper)var8).unwrap();
         }

         boolean var9 = var8 instanceof ClassLoader;
         var5 = null;
         if (var9) {
            var5 = (ClassLoader)var8;
         }
      }

      if (var5 == null) {
         Context.reportRuntimeError0("msg.not.classloader");
         return null;
      } else {
         NativeJavaPackage var6 = new NativeJavaPackage(true, "", var5);
         ScriptRuntime.setObjectProtoAndParent(var6, var2);
         return var6;
      }
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (var1.hasTag(FTAG) && var1.methodId() == 1) {
         return this.js_getClass(var2, var3, var5);
      } else {
         throw var1.unknown();
      }
   }
}
