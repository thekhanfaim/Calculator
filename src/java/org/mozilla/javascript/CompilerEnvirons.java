package org.mozilla.javascript;

import java.util.Set;
import org.mozilla.javascript.ast.ErrorCollector;

public class CompilerEnvirons {
   Set activationNames;
   private boolean allowMemberExprAsFunctionName;
   private boolean allowSharpComments;
   private ErrorReporter errorReporter;
   private boolean generateDebugInfo;
   private boolean generateObserverCount;
   private boolean generatingSource;
   private boolean ideMode;
   private int languageVersion;
   private int optimizationLevel;
   private boolean recordingComments;
   private boolean recordingLocalJsDocComments;
   private boolean recoverFromErrors;
   private boolean reservedKeywordAsIdentifier;
   private boolean strictMode;
   private boolean warnTrailingComma;
   private boolean warningAsError;
   private boolean xmlAvailable;

   public CompilerEnvirons() {
      this.errorReporter = DefaultErrorReporter.instance;
      this.languageVersion = 0;
      this.generateDebugInfo = true;
      this.reservedKeywordAsIdentifier = true;
      this.allowMemberExprAsFunctionName = false;
      this.xmlAvailable = true;
      this.optimizationLevel = 0;
      this.generatingSource = true;
      this.strictMode = false;
      this.warningAsError = false;
      this.generateObserverCount = false;
      this.allowSharpComments = false;
   }

   public static CompilerEnvirons ideEnvirons() {
      CompilerEnvirons var0 = new CompilerEnvirons();
      var0.setRecoverFromErrors(true);
      var0.setRecordingComments(true);
      var0.setStrictMode(true);
      var0.setWarnTrailingComma(true);
      var0.setLanguageVersion(170);
      var0.setReservedKeywordAsIdentifier(true);
      var0.setIdeMode(true);
      var0.setErrorReporter(new ErrorCollector());
      return var0;
   }

   public Set getActivationNames() {
      return this.activationNames;
   }

   public boolean getAllowSharpComments() {
      return this.allowSharpComments;
   }

   public final ErrorReporter getErrorReporter() {
      return this.errorReporter;
   }

   public final int getLanguageVersion() {
      return this.languageVersion;
   }

   public final int getOptimizationLevel() {
      return this.optimizationLevel;
   }

   public boolean getWarnTrailingComma() {
      return this.warnTrailingComma;
   }

   public void initFromContext(Context var1) {
      this.setErrorReporter(var1.getErrorReporter());
      this.languageVersion = var1.getLanguageVersion();
      boolean var2;
      if (var1.isGeneratingDebugChanged() && !var1.isGeneratingDebug()) {
         var2 = false;
      } else {
         var2 = true;
      }

      this.generateDebugInfo = var2;
      this.reservedKeywordAsIdentifier = var1.hasFeature(3);
      this.allowMemberExprAsFunctionName = var1.hasFeature(2);
      this.strictMode = var1.hasFeature(11);
      this.warningAsError = var1.hasFeature(12);
      this.xmlAvailable = var1.hasFeature(6);
      this.optimizationLevel = var1.getOptimizationLevel();
      this.generatingSource = var1.isGeneratingSource();
      this.activationNames = var1.activationNames;
      this.generateObserverCount = var1.generateObserverCount;
   }

   public final boolean isAllowMemberExprAsFunctionName() {
      return this.allowMemberExprAsFunctionName;
   }

   public final boolean isGenerateDebugInfo() {
      return this.generateDebugInfo;
   }

   public boolean isGenerateObserverCount() {
      return this.generateObserverCount;
   }

   public final boolean isGeneratingSource() {
      return this.generatingSource;
   }

   public boolean isIdeMode() {
      return this.ideMode;
   }

   public boolean isRecordingComments() {
      return this.recordingComments;
   }

   public boolean isRecordingLocalJsDocComments() {
      return this.recordingLocalJsDocComments;
   }

   public final boolean isReservedKeywordAsIdentifier() {
      return this.reservedKeywordAsIdentifier;
   }

   public final boolean isStrictMode() {
      return this.strictMode;
   }

   public final boolean isXmlAvailable() {
      return this.xmlAvailable;
   }

   public boolean recoverFromErrors() {
      return this.recoverFromErrors;
   }

   public final boolean reportWarningAsError() {
      return this.warningAsError;
   }

   public void setActivationNames(Set var1) {
      this.activationNames = var1;
   }

   public void setAllowMemberExprAsFunctionName(boolean var1) {
      this.allowMemberExprAsFunctionName = var1;
   }

   public void setAllowSharpComments(boolean var1) {
      this.allowSharpComments = var1;
   }

   public void setErrorReporter(ErrorReporter var1) {
      if (var1 != null) {
         this.errorReporter = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setGenerateDebugInfo(boolean var1) {
      this.generateDebugInfo = var1;
   }

   public void setGenerateObserverCount(boolean var1) {
      this.generateObserverCount = var1;
   }

   public void setGeneratingSource(boolean var1) {
      this.generatingSource = var1;
   }

   public void setIdeMode(boolean var1) {
      this.ideMode = var1;
   }

   public void setLanguageVersion(int var1) {
      Context.checkLanguageVersion(var1);
      this.languageVersion = var1;
   }

   public void setOptimizationLevel(int var1) {
      Context.checkOptimizationLevel(var1);
      this.optimizationLevel = var1;
   }

   public void setRecordingComments(boolean var1) {
      this.recordingComments = var1;
   }

   public void setRecordingLocalJsDocComments(boolean var1) {
      this.recordingLocalJsDocComments = var1;
   }

   public void setRecoverFromErrors(boolean var1) {
      this.recoverFromErrors = var1;
   }

   public void setReservedKeywordAsIdentifier(boolean var1) {
      this.reservedKeywordAsIdentifier = var1;
   }

   public void setStrictMode(boolean var1) {
      this.strictMode = var1;
   }

   public void setWarnTrailingComma(boolean var1) {
      this.warnTrailingComma = var1;
   }

   public void setXmlAvailable(boolean var1) {
      this.xmlAvailable = var1;
   }
}
