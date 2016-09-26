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
/*     */ public abstract interface ISendMessageCallback extends IInterface
/*     */ {
/*     */   public abstract void onAttached(Message paramMessage)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onSuccess(Message paramMessage)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onError(Message paramMessage, int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements ISendMessageCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.ISendMessageCallback";
/*     */     static final int TRANSACTION_onAttached = 1;
/*     */     static final int TRANSACTION_onSuccess = 2;
/*     */     static final int TRANSACTION_onError = 3;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  15 */       attachInterface(this, "io.rong.imlib.ISendMessageCallback");
/*     */     }
/*     */ 
/*     */     public static ISendMessageCallback asInterface(IBinder obj)
/*     */     {
/*  23 */       if (obj == null) {
/*  24 */         return null;
/*     */       }
/*  26 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.ISendMessageCallback");
/*  27 */       if ((iin != null) && ((iin instanceof ISendMessageCallback))) {
/*  28 */         return (ISendMessageCallback)iin;
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
/*  42 */         reply.writeString("io.rong.imlib.ISendMessageCallback");
/*  43 */         return true;
/*     */       case 1:
/*  47 */         data.enforceInterface("io.rong.imlib.ISendMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  49 */         if (0 != data.readInt()) {
/*  50 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  53 */           _arg0 = null;
/*     */         }
/*  55 */         onAttached(_arg0);
/*  56 */         reply.writeNoException();
/*  57 */         return true;
/*     */       case 2:
/*  61 */         data.enforceInterface("io.rong.imlib.ISendMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  63 */         if (0 != data.readInt()) {
/*  64 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  67 */           _arg0 = null;
/*     */         }
/*  69 */         onSuccess(_arg0);
/*  70 */         reply.writeNoException();
/*  71 */         return true;
/*     */       case 3:
/*  75 */         data.enforceInterface("io.rong.imlib.ISendMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  77 */         if (0 != data.readInt()) {
/*  78 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  81 */           _arg0 = null;
/*     */         }
/*     */ 
/*  84 */         int _arg1 = data.readInt();
/*  85 */         onError(_arg0, _arg1);
/*  86 */         reply.writeNoException();
/*  87 */         return true;
/*     */       }
/*     */ 
/*  90 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements ISendMessageCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  97 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/* 101 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/* 105 */         return "io.rong.imlib.ISendMessageCallback";
/*     */       }
/*     */ 
/*     */       public void onAttached(Message message) throws RemoteException {
/* 109 */         Parcel _data = Parcel.obtain();
/* 110 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 112 */           _data.writeInterfaceToken("io.rong.imlib.ISendMessageCallback");
/* 113 */           if (message != null) {
/* 114 */             _data.writeInt(1);
/* 115 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 118 */             _data.writeInt(0);
/*     */           }
/* 120 */           this.mRemote.transact(1, _data, _reply, 0);
/* 121 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 124 */           _reply.recycle();
/* 125 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onSuccess(Message message) throws RemoteException {
/* 130 */         Parcel _data = Parcel.obtain();
/* 131 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 133 */           _data.writeInterfaceToken("io.rong.imlib.ISendMessageCallback");
/* 134 */           if (message != null) {
/* 135 */             _data.writeInt(1);
/* 136 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 139 */             _data.writeInt(0);
/*     */           }
/* 141 */           this.mRemote.transact(2, _data, _reply, 0);
/* 142 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 145 */           _reply.recycle();
/* 146 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(Message message, int errorCode) throws RemoteException {
/* 151 */         Parcel _data = Parcel.obtain();
/* 152 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 154 */           _data.writeInterfaceToken("io.rong.imlib.ISendMessageCallback");
/* 155 */           if (message != null) {
/* 156 */             _data.writeInt(1);
/* 157 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 160 */             _data.writeInt(0);
/*     */           }
/* 162 */           _data.writeInt(errorCode);
/* 163 */           this.mRemote.transact(3, _data, _reply, 0);
/* 164 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 167 */           _reply.recycle();
/* 168 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ISendMessageCallback
 * JD-Core Version:    0.6.0
 */