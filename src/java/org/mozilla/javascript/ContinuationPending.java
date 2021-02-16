package org.mozilla.javascript;

public class ContinuationPending extends RuntimeException {
   private static final long serialVersionUID = 4956008116771118856L;
   private Object applicationState;
   private NativeContinuation continuationState;

   protected ContinuationPending(NativeContinuation var1) {
      this.continuationState = var1;
   }

   public Object getApplicationState() {
      return this.applicationState;
   }

   public Object getContinuation() {
      return this.continuationState;
   }

   NativeContinuation getContinuationState() {
      return this.continuationState;
   }

   public void setApplicationState(Object var1) {
      this.applicationState = var1;
   }

   public void setContinuation(NativeContinuation var1) {
      this.continuationState = var1;
   }
}
