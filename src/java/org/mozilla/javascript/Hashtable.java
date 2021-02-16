package org.mozilla.javascript;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class Hashtable implements Serializable, Iterable {
   // $FF: synthetic field
   static final boolean $assertionsDisabled;
   private Hashtable.Entry first;
   private Hashtable.Entry last;
   private final HashMap map;

   static {
      // $FF: Couldn't be decompiled
   }

   public Hashtable() {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   static Hashtable.Entry access$000(Hashtable param0) {
      // $FF: Couldn't be decompiled
   }

   private Hashtable.Entry makeDummy() {
      // $FF: Couldn't be decompiled
   }

   public void clear() {
      // $FF: Couldn't be decompiled
   }

   public Object delete(Object param1) {
      // $FF: Couldn't be decompiled
   }

   public Object get(Object param1) {
      // $FF: Couldn't be decompiled
   }

   public boolean has(Object param1) {
      // $FF: Couldn't be decompiled
   }

   public Iterator iterator() {
      // $FF: Couldn't be decompiled
   }

   public void put(Object param1, Object param2) {
      // $FF: Couldn't be decompiled
   }

   public int size() {
      // $FF: Couldn't be decompiled
   }

   public static final class Entry implements Serializable {
      protected boolean deleted;
      private final int hashCode;
      protected Object key;
      protected Hashtable.Entry next;
      protected Hashtable.Entry prev;
      protected Object value;

      Entry() {
         // $FF: Couldn't be decompiled
      }

      Entry(Object param1, Object param2) {
         // $FF: Couldn't be decompiled
      }

      Object clear() {
         // $FF: Couldn't be decompiled
      }

      public boolean equals(Object param1) {
         // $FF: Couldn't be decompiled
      }

      public int hashCode() {
         // $FF: Couldn't be decompiled
      }

      public Object key() {
         // $FF: Couldn't be decompiled
      }

      public Object value() {
         // $FF: Couldn't be decompiled
      }
   }

   private final class Iter implements Iterator {
      private Hashtable.Entry pos;
      // $FF: synthetic field
      final Hashtable this$0;

      Iter(Hashtable param1, Hashtable.Entry param2) {
         // $FF: Couldn't be decompiled
      }

      private void skipDeleted() {
         // $FF: Couldn't be decompiled
      }

      public boolean hasNext() {
         // $FF: Couldn't be decompiled
      }

      public Hashtable.Entry next() {
         // $FF: Couldn't be decompiled
      }
   }
}
