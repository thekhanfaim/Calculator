import java.util.Arrays;

// $FF: synthetic class
public class $r8$backportedMethods$utility$Objects$2$deepEquals {
   // $FF: synthetic method
   public static boolean deepEquals(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 == null) {
         return false;
      } else if (var0 instanceof boolean[]) {
         return var1 instanceof boolean[] && Arrays.equals((boolean[])var0, (boolean[])var1);
      } else if (var0 instanceof byte[]) {
         return var1 instanceof byte[] && Arrays.equals((byte[])var0, (byte[])var1);
      } else if (var0 instanceof char[]) {
         return var1 instanceof char[] && Arrays.equals((char[])var0, (char[])var1);
      } else if (var0 instanceof double[]) {
         return var1 instanceof double[] && Arrays.equals((double[])var0, (double[])var1);
      } else if (var0 instanceof float[]) {
         return var1 instanceof float[] && Arrays.equals((float[])var0, (float[])var1);
      } else if (var0 instanceof int[]) {
         return var1 instanceof int[] && Arrays.equals((int[])var0, (int[])var1);
      } else if (var0 instanceof long[]) {
         return var1 instanceof long[] && Arrays.equals((long[])var0, (long[])var1);
      } else if (var0 instanceof short[]) {
         return var1 instanceof short[] && Arrays.equals((short[])var0, (short[])var1);
      } else if (var0 instanceof Object[]) {
         return var1 instanceof Object[] && Arrays.deepEquals((Object[])var0, (Object[])var1);
      } else {
         return var0.equals(var1);
      }
   }
}
