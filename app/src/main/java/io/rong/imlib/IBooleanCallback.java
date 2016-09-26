/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface IBooleanCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(boolean paramBoolean)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IBooleanCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IBooleanCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.IBooleanCallback");
/*     */     }
/*     */ 
/*     */     public static IBooleanCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IBooleanCallback");
/*  29 */       if ((iin != null) && ((iin instanceof IBooleanCallback))) {
/*  30 */         return (IBooleanCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.IBooleanCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.IBooleanCallback");
/*     */ 
/*  51 */         boolean _arg0 = 0 != data.readInt();
/*  52 */         onComplete(_arg0);
/*  53 */         reply.writeNoException();
/*  54 */         return true;
/*     */       case 2:
/*  58 */         data.enforceInterface("io.rong.imlib.IBooleanCallback");
/*     */ 
/*  60 */         int _arg0 = data.readInt();
/*  61 */         onFailure(_arg0);
/*  62 */         reply.writeNoException();
/*  63 */         return true;
/*     */       }
/*     */ 
/*  66 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IBooleanCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  73 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  77 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  81 */         return "io.rong.imlib.IBooleanCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(boolean result) throws RemoteException {
/*  85 */         Parcel _data = Parcel.obtain();
/*  86 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  88 */           _data.writeInterfaceToken("io.rong.imlib.IBooleanCallback");
/*  89 */           _data.writeInt(result ? 1 : 0);
/*  90 */           this.mRemote.transact(1, _data, _reply, 0);
/*  91 */           _reply.readException();
/*     */         }
/*     */         finally {
/*  94 */           _reply.recycle();
/*  95 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(int errorCode) throws RemoteException {
/* 100 */         Parcel _data = Parcel.obtain();
/* 101 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 103 */           _data.writeInterfaceToken("io.rong.imlib.IBooleanCallback");
/* 104 */           _data.writeInt(errorCode);
/* 105 */           this.mRemote.transact(2, _data, _reply, 0);
/* 106 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 109 */           _reply.recycle();
/* 110 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IBooleanCallback
 * JD-Core Version:    0.6.0
 */