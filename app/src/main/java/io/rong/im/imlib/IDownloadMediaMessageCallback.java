/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.os.RemoteException;
/*     */ import io.rong.imlib.model.Message;
/*     */ 
/*     */ public abstract interface IDownloadMediaMessageCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(Message paramMessage)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onProgress(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onCanceled()
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IDownloadMediaMessageCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IDownloadMediaMessageCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */     static final int TRANSACTION_onProgress = 3;
/*     */     static final int TRANSACTION_onCanceled = 4;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.IDownloadMediaMessageCallback");
/*     */     }
/*     */ 
/*     */     public static IDownloadMediaMessageCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IDownloadMediaMessageCallback");
/*  29 */       if ((iin != null) && ((iin instanceof IDownloadMediaMessageCallback))) {
/*  30 */         return (IDownloadMediaMessageCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.IDownloadMediaMessageCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.IDownloadMediaMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  51 */         if (0 != data.readInt()) {
/*  52 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  55 */           _arg0 = null;
/*     */         }
/*  57 */         onComplete(_arg0);
/*  58 */         reply.writeNoException();
/*  59 */         return true;
/*     */       case 2:
/*  63 */         data.enforceInterface("io.rong.imlib.IDownloadMediaMessageCallback");
/*     */ 
/*  65 */         int _arg0 = data.readInt();
/*  66 */         onFailure(_arg0);
/*  67 */         reply.writeNoException();
/*  68 */         return true;
/*     */       case 3:
/*  72 */         data.enforceInterface("io.rong.imlib.IDownloadMediaMessageCallback");
/*     */ 
/*  74 */         int _arg0 = data.readInt();
/*  75 */         onProgress(_arg0);
/*  76 */         reply.writeNoException();
/*  77 */         return true;
/*     */       case 4:
/*  81 */         data.enforceInterface("io.rong.imlib.IDownloadMediaMessageCallback");
/*  82 */         onCanceled();
/*  83 */         reply.writeNoException();
/*  84 */         return true;
/*     */       }
/*     */ 
/*  87 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IDownloadMediaMessageCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  94 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  98 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/* 102 */         return "io.rong.imlib.IDownloadMediaMessageCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(Message messag) throws RemoteException {
/* 106 */         Parcel _data = Parcel.obtain();
/* 107 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 109 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaMessageCallback");
/* 110 */           if (messag != null) {
/* 111 */             _data.writeInt(1);
/* 112 */             messag.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 115 */             _data.writeInt(0);
/*     */           }
/* 117 */           this.mRemote.transact(1, _data, _reply, 0);
/* 118 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 121 */           _reply.recycle();
/* 122 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onFailure(int errorCode) throws RemoteException {
/* 127 */         Parcel _data = Parcel.obtain();
/* 128 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 130 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaMessageCallback");
/* 131 */           _data.writeInt(errorCode);
/* 132 */           this.mRemote.transact(2, _data, _reply, 0);
/* 133 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 136 */           _reply.recycle();
/* 137 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onProgress(int progress) throws RemoteException {
/* 142 */         Parcel _data = Parcel.obtain();
/* 143 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 145 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaMessageCallback");
/* 146 */           _data.writeInt(progress);
/* 147 */           this.mRemote.transact(3, _data, _reply, 0);
/* 148 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 151 */           _reply.recycle();
/* 152 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onCanceled() throws RemoteException {
/* 157 */         Parcel _data = Parcel.obtain();
/* 158 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 160 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaMessageCallback");
/* 161 */           this.mRemote.transact(4, _data, _reply, 0);
/* 162 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 165 */           _reply.recycle();
/* 166 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IDownloadMediaMessageCallback
 * JD-Core Version:    0.6.0
 */