package com.example.calculator;

import android.view.View;
import android.view.View.OnClickListener;

class MainActivity$16 implements OnClickListener {
   // $FF: synthetic field
   final MainActivity this$0;

   MainActivity$16(MainActivity var1) {
      this.this$0 = var1;
   }

   public void onClick(View var1) {
      MainActivity var2 = this.this$0;
      var2.input = var2.txt_Output.getText().toString();
      this.this$0.txt_Output.setText("");
   }
}
