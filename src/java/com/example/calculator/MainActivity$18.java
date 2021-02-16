package com.example.calculator;

import android.view.View;
import android.view.View.OnClickListener;
import org.mozilla.javascript.Context;

class MainActivity$18 implements OnClickListener {
   // $FF: synthetic field
   final MainActivity this$0;

   MainActivity$18(MainActivity var1) {
      this.this$0 = var1;
   }

   public void onClick(View var1) {
      MainActivity var2 = this.this$0;
      var2.input = var2.txt_Output.getText().toString();
      MainActivity var3 = this.this$0;
      var3.input = var3.input.replaceAll("×", "*");
      MainActivity var4 = this.this$0;
      var4.input = var4.input.replaceAll("%", "/100");
      MainActivity var5 = this.this$0;
      var5.input = var5.input.replaceAll("÷", "/");
      Context var6 = Context.enter();
      var6.setOptimizationLevel(-1);

      String var8;
      label13: {
         String var9;
         try {
            var9 = var6.evaluateString(var6.initStandardObjects(), this.this$0.input, "javascript", 1, (Object)null).toString();
         } catch (Exception var10) {
            var8 = "0";
            break label13;
         }

         var8 = var9;
      }

      this.this$0.txt_Output.setText(var8);
   }
}
