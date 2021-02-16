package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjArray implements Serializable {
   private static final int FIELDS_STORE_SIZE = 5;
   private static final long serialVersionUID = 4174889037736658296L;
   private transient Object[] data;
   private transient Object f0;
   private transient Object f1;
   private transient Object f2;
   private transient Object f3;
   private transient Object f4;
   private boolean sealed;
   private int size;

   private void ensureCapacity(int var1) {
      int var2 = var1 - 5;
      if (var2 > 0) {
         Object[] var3 = this.data;
         if (var3 == null) {
            int var8 = 10;
            if (var8 < var2) {
               var8 = var2;
            }

            this.data = new Object[var8];
         } else {
            int var4 = var3.length;
            if (var4 < var2) {
               int var5;
               if (var4 <= 5) {
                  var5 = 10;
               } else {
                  var5 = var4 * 2;
               }

               if (var5 < var2) {
                  var5 = var2;
               }

               Object[] var6 = new Object[var5];
               int var7 = this.size;
               if (var7 > 5) {
                  System.arraycopy(var3, 0, var6, 0, var7 - 5);
               }

               this.data = var6;
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private Object getImpl(int var1) {
      if (var1 != 0) {
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  return var1 != 4 ? this.data[var1 - 5] : this.f4;
               } else {
                  return this.f3;
               }
            } else {
               return this.f2;
            }
         } else {
            return this.f1;
         }
      } else {
         return this.f0;
      }
   }

   private static RuntimeException onEmptyStackTopRead() {
      throw new RuntimeException("Empty stack");
   }

   private static RuntimeException onInvalidIndex(int var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(var0);
      var2.append(" ∉ [0, ");
      var2.append(var1);
      var2.append(')');
      throw new IndexOutOfBoundsException(var2.toString());
   }

   private static RuntimeException onSeledMutation() {
      throw new IllegalStateException("Attempt to modify sealed array");
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = this.size;
      if (var2 > 5) {
         this.data = new Object[var2 - 5];
      }

      for(int var3 = 0; var3 != var2; ++var3) {
         this.setImpl(var3, var1.readObject());
      }

   }

   private void setImpl(int var1, Object var2) {
      if (var1 != 0) {
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  if (var1 != 4) {
                     this.data[var1 - 5] = var2;
                  } else {
                     this.f4 = var2;
                  }
               } else {
                  this.f3 = var2;
               }
            } else {
               this.f2 = var2;
            }
         } else {
            this.f1 = var2;
         }
      } else {
         this.f0 = var2;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      int var2 = this.size;

      for(int var3 = 0; var3 != var2; ++var3) {
         var1.writeObject(this.getImpl(var3));
      }

   }

   public final void add(int var1, Object var2) {
      int var3 = this.size;
      if (var1 >= 0 && var1 <= var3) {
         if (this.sealed) {
            throw onSeledMutation();
         } else {
            label68: {
               label69: {
                  label70: {
                     label71: {
                        label72: {
                           if (var1 != 0) {
                              if (var1 != 1) {
                                 if (var1 != 2) {
                                    if (var1 != 3) {
                                       if (var1 != 4) {
                                          break label69;
                                       }
                                       break label70;
                                    }
                                    break label71;
                                 }
                                 break label72;
                              }
                           } else {
                              if (var3 == 0) {
                                 this.f0 = var2;
                                 break label68;
                              }

                              Object var4 = this.f0;
                              this.f0 = var2;
                              var2 = var4;
                           }

                           if (var3 == 1) {
                              this.f1 = var2;
                              break label68;
                           }

                           Object var5 = this.f1;
                           this.f1 = var2;
                           var2 = var5;
                        }

                        if (var3 == 2) {
                           this.f2 = var2;
                           break label68;
                        }

                        Object var6 = this.f2;
                        this.f2 = var2;
                        var2 = var6;
                     }

                     if (var3 == 3) {
                        this.f3 = var2;
                        break label68;
                     }

                     Object var7 = this.f3;
                     this.f3 = var2;
                     var2 = var7;
                  }

                  if (var3 == 4) {
                     this.f4 = var2;
                     break label68;
                  }

                  Object var8 = this.f4;
                  this.f4 = var2;
                  var2 = var8;
                  var1 = 5;
               }

               this.ensureCapacity(var3 + 1);
               if (var1 != var3) {
                  Object[] var9 = this.data;
                  System.arraycopy(var9, var1 - 5, var9, 1 + (var1 - 5), var3 - var1);
               }

               this.data[var1 - 5] = var2;
            }

            this.size = var3 + 1;
         }
      } else {
         throw onInvalidIndex(var1, var3 + 1);
      }
   }

   public final void add(Object var1) {
      if (!this.sealed) {
         int var2 = this.size;
         if (var2 >= 5) {
            this.ensureCapacity(var2 + 1);
         }

         this.size = var2 + 1;
         this.setImpl(var2, var1);
      } else {
         throw onSeledMutation();
      }
   }

   public final void clear() {
      if (this.sealed) {
         RuntimeException var1 = onSeledMutation();
         throw var1;
      } else {
         int var2 = this.size;

         for(int var3 = 0; var3 != var2; ++var3) {
            this.setImpl(var3, (Object)null);
         }

         this.size = 0;
      }
   }

   public final Object get(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         return this.getImpl(var1);
      } else {
         throw onInvalidIndex(var1, this.size);
      }
   }

   public int indexOf(Object var1) {
      int var2 = this.size;

      for(int var3 = 0; var3 != var2; ++var3) {
         Object var4 = this.getImpl(var3);
         if (var4 == var1) {
            return var3;
         }

         if (var4 != null && var4.equals(var1)) {
            return var3;
         }
      }

      return -1;
   }

   public final boolean isEmpty() {
      return this.size == 0;
   }

   public final boolean isSealed() {
      return this.sealed;
   }

   public int lastIndexOf(Object var1) {
      int var2 = this.size;

      Object var3;
      do {
         if (var2 == 0) {
            return -1;
         }

         --var2;
         var3 = this.getImpl(var2);
         if (var3 == var1) {
            return var2;
         }
      } while(var3 == null || !var3.equals(var1));

      return var2;
   }

   public final Object peek() {
      int var1 = this.size;
      if (var1 != 0) {
         return this.getImpl(var1 - 1);
      } else {
         throw onEmptyStackTopRead();
      }
   }

   public final Object pop() {
      if (!this.sealed) {
         int var1 = -1 + this.size;
         if (var1 != -1) {
            Object var2;
            if (var1 != 0) {
               if (var1 != 1) {
                  if (var1 != 2) {
                     if (var1 != 3) {
                        if (var1 != 4) {
                           Object[] var3 = this.data;
                           var2 = var3[var1 - 5];
                           var3[var1 - 5] = null;
                        } else {
                           var2 = this.f4;
                           this.f4 = null;
                        }
                     } else {
                        var2 = this.f3;
                        this.f3 = null;
                     }
                  } else {
                     var2 = this.f2;
                     this.f2 = null;
                  }
               } else {
                  var2 = this.f1;
                  this.f1 = null;
               }
            } else {
               var2 = this.f0;
               this.f0 = null;
            }

            this.size = var1;
            return var2;
         } else {
            throw onEmptyStackTopRead();
         }
      } else {
         throw onSeledMutation();
      }
   }

   public final void push(Object var1) {
      this.add(var1);
   }

   public final void remove(int var1) {
      int var2 = this.size;
      if (var1 >= 0 && var1 < var2) {
         if (this.sealed) {
            throw onSeledMutation();
         } else {
            int var3;
            label68: {
               label69: {
                  label70: {
                     label71: {
                        label72: {
                           var3 = var2 - 1;
                           if (var1 != 0) {
                              if (var1 != 1) {
                                 if (var1 != 2) {
                                    if (var1 != 3) {
                                       if (var1 != 4) {
                                          break label69;
                                       }
                                       break label70;
                                    }
                                    break label71;
                                 }
                                 break label72;
                              }
                           } else {
                              if (var3 == 0) {
                                 this.f0 = null;
                                 break label68;
                              }

                              this.f0 = this.f1;
                           }

                           if (var3 == 1) {
                              this.f1 = null;
                              break label68;
                           }

                           this.f1 = this.f2;
                        }

                        if (var3 == 2) {
                           this.f2 = null;
                           break label68;
                        }

                        this.f2 = this.f3;
                     }

                     if (var3 == 3) {
                        this.f3 = null;
                        break label68;
                     }

                     this.f3 = this.f4;
                  }

                  if (var3 == 4) {
                     this.f4 = null;
                     break label68;
                  }

                  this.f4 = this.data[0];
                  var1 = 5;
               }

               if (var1 != var3) {
                  Object[] var4 = this.data;
                  System.arraycopy(var4, 1 + (var1 - 5), var4, var1 - 5, var3 - var1);
               }

               this.data[var3 - 5] = null;
            }

            this.size = var3;
         }
      } else {
         throw onInvalidIndex(var1, var2);
      }
   }

   public final void seal() {
      this.sealed = true;
   }

   public final void set(int var1, Object var2) {
      if (var1 >= 0 && var1 < this.size) {
         if (!this.sealed) {
            this.setImpl(var1, var2);
         } else {
            throw onSeledMutation();
         }
      } else {
         throw onInvalidIndex(var1, this.size);
      }
   }

   public final void setSize(int var1) {
      if (var1 >= 0) {
         if (this.sealed) {
            throw onSeledMutation();
         } else {
            int var3 = this.size;
            if (var1 < var3) {
               for(int var4 = var1; var4 != var3; ++var4) {
                  this.setImpl(var4, (Object)null);
               }
            } else if (var1 > var3 && var1 > 5) {
               this.ensureCapacity(var1);
            }

            this.size = var1;
         }
      } else {
         IllegalArgumentException var2 = new IllegalArgumentException();
         throw var2;
      }
   }

   public final int size() {
      return this.size;
   }

   public final void toArray(Object[] var1) {
      this.toArray(var1, 0);
   }

   public final void toArray(Object[] var1, int var2) {
      int var3 = this.size;
      if (var3 != 0) {
         if (var3 != 1) {
            if (var3 != 2) {
               if (var3 != 3) {
                  if (var3 != 4) {
                     if (var3 != 5) {
                        System.arraycopy(this.data, 0, var1, var2 + 5, var3 - 5);
                     }

                     var1[var2 + 4] = this.f4;
                  }

                  var1[var2 + 3] = this.f3;
               }

               var1[var2 + 2] = this.f2;
            }

            var1[var2 + 1] = this.f1;
         }

         var1[var2 + 0] = this.f0;
      }

   }

   public final Object[] toArray() {
      Object[] var1 = new Object[this.size];
      this.toArray(var1, 0);
      return var1;
   }
}
