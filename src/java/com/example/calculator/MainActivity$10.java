package com.example.calculator;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

class MainActivity$10 implements OnClickListener {
   // $FF: synthetic field
   final MainActivity this$0;

   MainActivity$10(MainActivity var1) {
      this.this$0 = var1;
   }

   public void onClick(View var1) {
      MainActivity var2 = this.this$0;
      var2.input = var2.txt_Output.getText().toString();
      TextView var3 = this.this$0.txt_Output;
      StringBuilder var4 = new StringBuilder();
      var4.append(this.this$0.input);
      var4.append("9");
      var3.setText(var4.toString());
   }
}
