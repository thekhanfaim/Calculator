package org.mozilla.javascript;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

class JavaMembers {
   private Class cl;
   NativeJavaMethod ctors;
   private Map fieldAndMethods;
   private Map members;
   private Map staticFieldAndMethods;
   private Map staticMembers;

   JavaMembers(Scriptable param1, Class param2) {
      // $FF: Couldn't be decompiled
   }

   JavaMembers(Scriptable param1, Class param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   private static void discoverAccessibleMethods(Class param0, Map param1, boolean param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   private static Method[] discoverAccessibleMethods(Class param0, boolean param1, boolean param2) {
      // $FF: Couldn't be decompiled
   }

   private static MemberBox extractGetMethod(MemberBox[] param0, boolean param1) {
      // $FF: Couldn't be decompiled
   }

   private static MemberBox extractSetMethod(Class param0, MemberBox[] param1, boolean param2) {
      // $FF: Couldn't be decompiled
   }

   private static MemberBox extractSetMethod(MemberBox[] param0, boolean param1) {
      // $FF: Couldn't be decompiled
   }

   private MemberBox findExplicitFunction(String param1, boolean param2) {
      // $FF: Couldn't be decompiled
   }

   private MemberBox findGetter(boolean param1, Map param2, String param3, String param4) {
      // $FF: Couldn't be decompiled
   }

   private Constructor[] getAccessibleConstructors(boolean param1) {
      // $FF: Couldn't be decompiled
   }

   private Field[] getAccessibleFields(boolean param1, boolean param2) {
      // $FF: Couldn't be decompiled
   }

   private Object getExplicitFunction(Scriptable param1, String param2, Object param3, boolean param4) {
      // $FF: Couldn't be decompiled
   }

   static String javaSignature(Class param0) {
      // $FF: Couldn't be decompiled
   }

   static String liveConnectSignature(Class[] param0) {
      // $FF: Couldn't be decompiled
   }

   static JavaMembers lookupClass(Scriptable param0, Class param1, Class param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   private void reflect(Scriptable param1, boolean param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   Object get(Scriptable param1, String param2, Object param3, boolean param4) {
      // $FF: Couldn't be decompiled
   }

   Map getFieldAndMethodsObjects(Scriptable param1, Object param2, boolean param3) {
      // $FF: Couldn't be decompiled
   }

   Object[] getIds(boolean param1) {
      // $FF: Couldn't be decompiled
   }

   boolean has(String param1, boolean param2) {
      // $FF: Couldn't be decompiled
   }

   void put(Scriptable param1, String param2, Object param3, Object param4, boolean param5) {
      // $FF: Couldn't be decompiled
   }

   RuntimeException reportMemberNotFound(String param1) {
      // $FF: Couldn't be decompiled
   }

   private static final class MethodSignature {
      private final Class[] args;
      private final String name;

      private MethodSignature(String param1, Class[] param2) {
         // $FF: Couldn't be decompiled
      }

      MethodSignature(Method param1) {
         // $FF: Couldn't be decompiled
      }

      public boolean equals(Object param1) {
         // $FF: Couldn't be decompiled
      }

      public int hashCode() {
         // $FF: Couldn't be decompiled
      }
   }
}
