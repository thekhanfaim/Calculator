package org.mozilla.javascript;

import java.io.Closeable;
import java.util.Iterator;

public class IteratorLikeIterable implements Iterable, Closeable {
   private boolean closed;
   private final Context cx;
   private final Scriptable iterator;
   private final Callable next;
   private final Callable returnFunc;
   private final Scriptable scope;

   public IteratorLikeIterable(Context param1, Scriptable param2, Object param3) {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   static Context access$000(IteratorLikeIterable param0) {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   static Scriptable access$100(IteratorLikeIterable param0) {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   static Scriptable access$200(IteratorLikeIterable param0) {
      // $FF: Couldn't be decompiled
   }

   // $FF: synthetic method
   static Callable access$300(IteratorLikeIterable param0) {
      // $FF: Couldn't be decompiled
   }

   public void close() {
      // $FF: Couldn't be decompiled
   }

   public IteratorLikeIterable.Itr iterator() {
      // $FF: Couldn't be decompiled
   }

   public final class Itr implements Iterator {
      private Object nextVal;
      // $FF: synthetic field
      final IteratorLikeIterable this$0;

      public Itr(IteratorLikeIterable param1) {
         // $FF: Couldn't be decompiled
      }

      public boolean hasNext() {
         // $FF: Couldn't be decompiled
      }

      public Object next() {
         // $FF: Couldn't be decompiled
      }
   }
}
