/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.os.RemoteException;
/*     */ import io.rong.imlib.model.RemoteModelWrap;
/*     */ 
/*     */ public abstract interface IResultCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(RemoteModelWrap paramRemoteModelWrap)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IResultCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IResultCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  15 */       attachInterface(this, "io.rong.imlib.IResultCallback");
/*     */     }
/*     */ 
/*     */     public static IResultCallback asInterface(IBinder obj)
/*     */     {
/*  23 */       if (obj == null) {
/*  24 */         return null;
/*     */       }
/*  26 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IResultCallback");
/*  27 */       if ((iin != null) && ((iin instanceof IResultCallback))) {
/*  28 */         return (IResultCallback)iin;
/*     */       }
/*  30 */       return new Proxy(obj);
/*     */     }
/*     */ 
/*     */     public IBinder asBinder() {
/*  34 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/*  38 */       switch (code)
/*     */       {
/*     */       case 1598968902:
/*  42 */         reply.writeString("io.rong.imlib.IResultCallback");
/*  43 */         return true;
/*     */       case 1:
/*  47 */         data.enforceInterface("io.rong.imlib.IResultCallback");
/*     */         RemoteModelWrap _arg0;
/*     */         RemoteModelWrap _arg0;
/*  49 */         if (0 != data.readInt()) {
/*  50 */           _arg0 = (RemoteModelWrap)RemoteModelWrap.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  53 */           _arg0 = null;
/*     */         }
/*  55 */         onComplete(_arg0);
/*  56 */         reply.writeNoException();
/*  57 */         return true;
/*     */       case 2:
/*  61 */         data.enforceInterface("io.rong.imlib.IResultCallback");
/*     */ 
/*  63 */         int _arg0 = data.readInt();
/*  64 */         onFailure(_arg0);
/*  65 */         reply.writeNoException();
/*  66 */         return true;
/*     */       }
/*     */ 
/*  69 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IResultCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  76 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  80 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  84 */         return "io.rong.imlib.IResultCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(RemoteModelWrap model) throws RemoteException {
/*  88 */         Parcel _data = Parcel.obtain();
/*  89 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  91 */           _data.writeInterfaceToken("io.rong.imlib.IResultCallback");
/*  92 */           if (model != null) {
/*  93 */             _data.writeInt(1);
/*  94 */             model.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/*  97 */             _data.writeInt(0);
/*     */           }
/*  99 */           this.mRemote.transact(1, _data, _reply, 0);
/* 100 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 103 */           _reply.recycle();
/* 104 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(int errorCode) throws RemoteException {
/* 109 */         Parcel _data = Parcel.obtain();
/* 110 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 112 */           _data.writeInterfaceToken("io.rong.imlib.IResultCallback");
/* 113 */           _data.writeInt(errorCode);
/* 114 */           this.mRemote.transact(2, _data, _reply, 0);
/* 115 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 118 */           _reply.recycle();
/* 119 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IResultCallback
 * JD-Core Version:    0.6.0
 */