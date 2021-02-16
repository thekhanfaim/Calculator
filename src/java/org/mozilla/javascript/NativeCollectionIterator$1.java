package org.mozilla.javascript;

// $FF: synthetic class
class NativeCollectionIterator$1 {
   // $FF: synthetic field
   static final int[] $SwitchMap$org$mozilla$javascript$NativeCollectionIterator$Type;

   static {
      int[] var0 = new int[NativeCollectionIterator.Type.values().length];
      $SwitchMap$org$mozilla$javascript$NativeCollectionIterator$Type = var0;

      try {
         var0[NativeCollectionIterator.Type.KEYS.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         $SwitchMap$org$mozilla$javascript$NativeCollectionIterator$Type[NativeCollectionIterator.Type.VALUES.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         $SwitchMap$org$mozilla$javascript$NativeCollectionIterator$Type[NativeCollectionIterator.Type.BOTH.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }
   }
}
