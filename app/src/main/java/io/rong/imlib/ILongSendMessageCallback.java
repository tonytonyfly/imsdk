/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface ILongSendMessageCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(long paramLong)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(long paramLong, int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements ILongSendMessageCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.ILongSendMessageCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.ILongSendMessageCallback");
/*     */     }
/*     */ 
/*     */     public static ILongSendMessageCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.ILongSendMessageCallback");
/*  29 */       if ((iin != null) && ((iin instanceof ILongSendMessageCallback))) {
/*  30 */         return (ILongSendMessageCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.ILongSendMessageCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.ILongSendMessageCallback");
/*     */ 
/*  51 */         long _arg0 = data.readLong();
/*  52 */         onComplete(_arg0);
/*  53 */         reply.writeNoException();
/*  54 */         return true;
/*     */       case 2:
/*  58 */         data.enforceInterface("io.rong.imlib.ILongSendMessageCallback");
/*     */ 
/*  60 */         long _arg0 = data.readLong();
/*     */ 
/*  62 */         int _arg1 = data.readInt();
/*  63 */         onFailure(_arg0, _arg1);
/*  64 */         reply.writeNoException();
/*  65 */         return true;
/*     */       }
/*     */ 
/*  68 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements ILongSendMessageCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  75 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  79 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  83 */         return "io.rong.imlib.ILongSendMessageCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(long result) throws RemoteException {
/*  87 */         Parcel _data = Parcel.obtain();
/*  88 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  90 */           _data.writeInterfaceToken("io.rong.imlib.ILongSendMessageCallback");
/*  91 */           _data.writeLong(result);
/*  92 */           this.mRemote.transact(1, _data, _reply, 0);
/*  93 */           _reply.readException();
/*     */         }
/*     */         finally {
/*  96 */           _reply.recycle();
/*  97 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(long result, int errorCode) throws RemoteException {
/* 102 */         Parcel _data = Parcel.obtain();
/* 103 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 105 */           _data.writeInterfaceToken("io.rong.imlib.ILongSendMessageCallback");
/* 106 */           _data.writeLong(result);
/* 107 */           _data.writeInt(errorCode);
/* 108 */           this.mRemote.transact(2, _data, _reply, 0);
/* 109 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 112 */           _reply.recycle();
/* 113 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ILongSendMessageCallback
 * JD-Core Version:    0.6.0
 */