package org.mozilla.javascript.ast;

public class ParseProblem {
   private int length;
   private String message;
   private int offset;
   private String sourceName;
   private ParseProblem.Type type;

   public ParseProblem(ParseProblem.Type var1, String var2, String var3, int var4, int var5) {
      this.setType(var1);
      this.setMessage(var2);
      this.setSourceName(var3);
      this.setFileOffset(var4);
      this.setLength(var5);
   }

   public int getFileOffset() {
      return this.offset;
   }

   public int getLength() {
      return this.length;
   }

   public String getMessage() {
      return this.message;
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public ParseProblem.Type getType() {
      return this.type;
   }

   public void setFileOffset(int var1) {
      this.offset = var1;
   }

   public void setLength(int var1) {
      this.length = var1;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public void setSourceName(String var1) {
      this.sourceName = var1;
   }

   public void setType(ParseProblem.Type var1) {
      this.type = var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(200);
      var1.append(this.sourceName);
      var1.append(":");
      var1.append("offset=");
      var1.append(this.offset);
      var1.append(",");
      var1.append("length=");
      var1.append(this.length);
      var1.append(",");
      String var10;
      if (this.type == ParseProblem.Type.Error) {
         var10 = "error: ";
      } else {
         var10 = "warning: ";
      }

      var1.append(var10);
      var1.append(this.message);
      return var1.toString();
   }

   public static enum Type {
      Error,
      Warning;

      static {
         ParseProblem.Type var0 = new ParseProblem.Type("Error", 0);
         Error = var0;
         ParseProblem.Type var1 = new ParseProblem.Type("Warning", 1);
         Warning = var1;
      }
   }
}
