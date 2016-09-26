/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface IDownloadMediaCallback extends IInterface
/*     */ {
/*     */   public abstract void onComplete(String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onFailure(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onProgress(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IDownloadMediaCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IDownloadMediaCallback";
/*     */     static final int TRANSACTION_onComplete = 1;
/*     */     static final int TRANSACTION_onFailure = 2;
/*     */     static final int TRANSACTION_onProgress = 3;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.IDownloadMediaCallback");
/*     */     }
/*     */ 
/*     */     public static IDownloadMediaCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IDownloadMediaCallback");
/*  29 */       if ((iin != null) && ((iin instanceof IDownloadMediaCallback))) {
/*  30 */         return (IDownloadMediaCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.IDownloadMediaCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.IDownloadMediaCallback");
/*     */ 
/*  51 */         String _arg0 = data.readString();
/*  52 */         onComplete(_arg0);
/*  53 */         reply.writeNoException();
/*  54 */         return true;
/*     */       case 2:
/*  58 */         data.enforceInterface("io.rong.imlib.IDownloadMediaCallback");
/*     */ 
/*  60 */         int _arg0 = data.readInt();
/*  61 */         onFailure(_arg0);
/*  62 */         reply.writeNoException();
/*  63 */         return true;
/*     */       case 3:
/*  67 */         data.enforceInterface("io.rong.imlib.IDownloadMediaCallback");
/*     */ 
/*  69 */         int _arg0 = data.readInt();
/*  70 */         onProgress(_arg0);
/*  71 */         reply.writeNoException();
/*  72 */         return true;
/*     */       }
/*     */ 
/*  75 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IDownloadMediaCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  82 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  86 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  90 */         return "io.rong.imlib.IDownloadMediaCallback";
/*     */       }
/*     */ 
/*     */       public void onComplete(String url) throws RemoteException {
/*  94 */         Parcel _data = Parcel.obtain();
/*  95 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  97 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaCallback");
/*  98 */           _data.writeString(url);
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
/* 112 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaCallback");
/* 113 */           _data.writeInt(errorCode);
/* 114 */           this.mRemote.transact(2, _data, _reply, 0);
/* 115 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 118 */           _reply.recycle();
/* 119 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onProgress(int progress) throws RemoteException {
/* 124 */         Parcel _data = Parcel.obtain();
/* 125 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 127 */           _data.writeInterfaceToken("io.rong.imlib.IDownloadMediaCallback");
/* 128 */           _data.writeInt(progress);
/* 129 */           this.mRemote.transact(3, _data, _reply, 0);
/* 130 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 133 */           _reply.recycle();
/* 134 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IDownloadMediaCallback
 * JD-Core Version:    0.6.0
 */