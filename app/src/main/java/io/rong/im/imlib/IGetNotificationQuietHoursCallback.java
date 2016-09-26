/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface IGetNotificationQuietHoursCallback extends IInterface
/*     */ {
/*     */   public abstract void onSuccess(String paramString, int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onError(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IGetNotificationQuietHoursCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IGetNotificationQuietHoursCallback";
/*     */     static final int TRANSACTION_onSuccess = 1;
/*     */     static final int TRANSACTION_onError = 2;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  17 */       attachInterface(this, "io.rong.imlib.IGetNotificationQuietHoursCallback");
/*     */     }
/*     */ 
/*     */     public static IGetNotificationQuietHoursCallback asInterface(IBinder obj)
/*     */     {
/*  25 */       if (obj == null) {
/*  26 */         return null;
/*     */       }
/*  28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IGetNotificationQuietHoursCallback");
/*  29 */       if ((iin != null) && ((iin instanceof IGetNotificationQuietHoursCallback))) {
/*  30 */         return (IGetNotificationQuietHoursCallback)iin;
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
/*  44 */         reply.writeString("io.rong.imlib.IGetNotificationQuietHoursCallback");
/*  45 */         return true;
/*     */       case 1:
/*  49 */         data.enforceInterface("io.rong.imlib.IGetNotificationQuietHoursCallback");
/*     */ 
/*  51 */         String _arg0 = data.readString();
/*     */ 
/*  53 */         int _arg1 = data.readInt();
/*  54 */         onSuccess(_arg0, _arg1);
/*  55 */         reply.writeNoException();
/*  56 */         return true;
/*     */       case 2:
/*  60 */         data.enforceInterface("io.rong.imlib.IGetNotificationQuietHoursCallback");
/*     */ 
/*  62 */         int _arg0 = data.readInt();
/*  63 */         onError(_arg0);
/*  64 */         reply.writeNoException();
/*  65 */         return true;
/*     */       }
/*     */ 
/*  68 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IGetNotificationQuietHoursCallback {
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
/*  83 */         return "io.rong.imlib.IGetNotificationQuietHoursCallback";
/*     */       }
/*     */ 
/*     */       public void onSuccess(String startTime, int minutes)
/*     */         throws RemoteException
/*     */       {
/*  91 */         Parcel _data = Parcel.obtain();
/*  92 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/*  94 */           _data.writeInterfaceToken("io.rong.imlib.IGetNotificationQuietHoursCallback");
/*  95 */           _data.writeString(startTime);
/*  96 */           _data.writeInt(minutes);
/*  97 */           this.mRemote.transact(1, _data, _reply, 0);
/*  98 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 101 */           _reply.recycle();
/* 102 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(int errorcode) throws RemoteException {
/* 107 */         Parcel _data = Parcel.obtain();
/* 108 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 110 */           _data.writeInterfaceToken("io.rong.imlib.IGetNotificationQuietHoursCallback");
/* 111 */           _data.writeInt(errorcode);
/* 112 */           this.mRemote.transact(2, _data, _reply, 0);
/* 113 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 116 */           _reply.recycle();
/* 117 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IGetNotificationQuietHoursCallback
 * JD-Core Version:    0.6.0
 */