/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface IOperationCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete()
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IOperationCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IOperationCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.IOperationCallback");
/*     */     }
/*     */ 
/*     */     public static IOperationCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IOperationCallback");
/*  29 */       if ((iin != null) && ((iin instanceof IOperationCallback))) {
/*  30 */         return (IOperationCallback)iin;
/*     */       }
/*  32 */       return new Proxy(obj);
/*     */     }
/*     */ 
/*     */     public IBinder asBinder() {
/*  36 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/*  40 */       switch (code)
/*     */       {
/*     */       case 1598968902:
/*  44 */         reply.writeString("io.rong.imlib.IOperationCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.IOperationCallback");
/*  50 */         onComplete();
/*  51 */         reply.writeNoException();
/*  52 */         return true;
/*     */       case 2:
/*  56 */         data.enforceInterface("io.rong.imlib.IOperationCallback");
/*     */ 
/*  58 */         int _arg0 = data.readInt();
/*  59 */         onFailure(_arg0);
/*  60 */         reply.writeNoException();
/*  61 */         return true;
/*     */       }
/*     */ 
/*  64 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IOperationCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  71 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  75 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  79 */         return "io.rong.imlib.IOperationCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete() throws RemoteException {
/*  83 */         Parcel _data = Parcel.obtain();
/*  84 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  86 */           _data.writeInterfaceToken("io.rong.imlib.IOperationCallback");
/*  87 */           this.mRemote.transact(1, _data, _reply, 0);
/*  88 */           _reply.readException();
/*     */         }
/*     */         finally {
/*  91 */           _reply.recycle();
/*  92 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(int errorCode) throws RemoteException {
/*  97 */         Parcel _data = Parcel.obtain();
/*  98 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 100 */           _data.writeInterfaceToken("io.rong.imlib.IOperationCallback");
/* 101 */           _data.writeInt(errorCode);
/* 102 */           this.mRemote.transact(2, _data, _reply, 0);
/* 103 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 106 */           _reply.recycle();
/* 107 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IOperationCallback
 * JD-Core Version:    0.6.0
 */