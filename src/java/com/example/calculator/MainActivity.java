/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.widget.Button
 *  android.widget.TextView
 *  androidx.appcompat.app.AppCompatActivity
 *  java.lang.String
 */
package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calculator.MainActivity;

public class MainActivity
extends AppCompatActivity {
    public Button btn_AC;
    public Button btn_Addition;
    public Button btn_Clear;
    public Button btn_Division;
    public Button btn_Dot;
    public Button btn_Eight;
    public Button btn_Equal;
    public Button btn_Five;
    public Button btn_Four;
    public Button btn_Multiply;
    public Button btn_Nine;
    public Button btn_One;
    public Button btn_Percentage;
    public Button btn_Seven;
    public Button btn_Six;
    public Button btn_Subtraction;
    public Button btn_Three;
    public Button btn_Two;
    public Button btn_Zero;
    String input;
    TextView txt_Output;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2131427356);
        this.btn_Dot = (Button)this.findViewById(2131230811);
        this.btn_AC = (Button)this.findViewById(2131230807);
        this.btn_Clear = (Button)this.findViewById(2131230809);
        this.btn_Zero = (Button)this.findViewById(2131230826);
        this.btn_One = (Button)this.findViewById(2131230819);
        this.btn_Two = (Button)this.findViewById(2131230825);
        this.btn_Three = (Button)this.findViewById(2131230824);
        this.btn_Four = (Button)this.findViewById(2131230815);
        this.btn_Five = (Button)this.findViewById(2131230814);
        this.btn_Seven = (Button)this.findViewById(2131230821);
        this.btn_Six = (Button)this.findViewById(2131230822);
        this.btn_Eight = (Button)this.findViewById(2131230812);
        this.btn_Nine = (Button)this.findViewById(2131230818);
        this.btn_Percentage = (Button)this.findViewById(2131230820);
        this.btn_Addition = (Button)this.findViewById(2131230808);
        this.btn_Subtraction = (Button)this.findViewById(2131230823);
        this.btn_Multiply = (Button)this.findViewById(2131230817);
        this.btn_Division = (Button)this.findViewById(2131230810);
        this.btn_Equal = (Button)this.findViewById(2131230813);
        this.txt_Output = (TextView)this.findViewById(2131231138);
        this.btn_Zero.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("0");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_One.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("1");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Two.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("2");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Three.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("3");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Four.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("4");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Five.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("5");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Six.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("6");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Seven.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("7");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Eight.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("8");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Nine.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("9");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Addition.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("+");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Subtraction.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("-");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Multiply.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("\u00d7");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Division.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("\u00f7");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Percentage.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append("%");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_AC.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                this.this$0.txt_Output.setText((java.lang.CharSequence)"");
            }
        });
        this.btn_Dot.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                TextView textView = this.this$0.txt_Output;
                java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();
                stringBuilder.append(this.this$0.input);
                stringBuilder.append(".");
                textView.setText((java.lang.CharSequence)stringBuilder.toString());
            }
        });
        this.btn_Equal.setOnClickListener(new View.OnClickListener(this){
            final /* synthetic */ MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onClick(View view) {
                String string;
                MainActivity mainActivity = this.this$0;
                mainActivity.input = mainActivity.txt_Output.getText().toString();
                MainActivity mainActivity2 = this.this$0;
                mainActivity2.input = mainActivity2.input.replaceAll("\u00d7", "*");
                MainActivity mainActivity3 = this.this$0;
                mainActivity3.input = mainActivity3.input.replaceAll("%", "/100");
                MainActivity mainActivity4 = this.this$0;
                mainActivity4.input = mainActivity4.input.replaceAll("\u00f7", "/");
                org.mozilla.javascript.Context context = org.mozilla.javascript.Context.enter();
                context.setOptimizationLevel(-1);
                try {
                    String string2;
                    string = string2 = context.evaluateString(context.initStandardObjects(), this.this$0.input, "javascript", 1, null).toString();
                }
                catch (java.lang.Exception exception) {
                    string = "0";
                }
                this.this$0.txt_Output.setText((java.lang.CharSequence)string);
            }
        });
    }
}

