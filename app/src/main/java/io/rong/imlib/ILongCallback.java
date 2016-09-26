/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface ILongCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(long paramLong)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements ILongCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.ILongCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.ILongCallback");
/*     */     }
/*     */ 
/*     */     public static ILongCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.ILongCallback");
/*  29 */       if ((iin != null) && ((iin instanceof ILongCallback))) {
/*  30 */         return (ILongCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.ILongCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.ILongCallback");
/*     */ 
/*  51 */         long _arg0 = data.readLong();
/*  52 */         onComplete(_arg0);
/*  53 */         reply.writeNoException();
/*  54 */         return true;
/*     */       case 2:
/*  58 */         data.enforceInterface("io.rong.imlib.ILongCallback");
/*     */ 
/*  60 */         int _arg0 = data.readInt();
/*  61 */         onFailure(_arg0);
/*  62 */         reply.writeNoException();
/*  63 */         return true;
/*     */       }
/*     */ 
/*  66 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements ILongCallback {
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
/*  81 */         return "io.rong.imlib.ILongCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(long result) throws RemoteException {
/*  85 */         Parcel _data = Parcel.obtain();
/*  86 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  88 */           _data.writeInterfaceToken("io.rong.imlib.ILongCallback");
/*  89 */           _data.writeLong(result);
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
/* 103 */           _data.writeInterfaceToken("io.rong.imlib.ILongCallback");
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
 * Qualified Name:     io.rong.imlib.ILongCallback
 * JD-Core Version:    0.6.0
 */