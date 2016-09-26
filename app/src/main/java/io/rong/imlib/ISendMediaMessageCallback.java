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
/*     */ public abstract interface ISendMediaMessageCallback extends IInterface
/*     */ {
/*     */   public abstract void onAttached(Message paramMessage)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onProgress(Message paramMessage, int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onSuccess(Message paramMessage)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onError(Message paramMessage, int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements ISendMediaMessageCallback
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.ISendMediaMessageCallback";
/*     */     static final int TRANSACTION_onAttached = 1;
/*     */     static final int TRANSACTION_onProgress = 2;
/*     */     static final int TRANSACTION_onSuccess = 3;
/*     */     static final int TRANSACTION_onError = 4;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  15 */       attachInterface(this, "io.rong.imlib.ISendMediaMessageCallback");
/*     */     }
/*     */ 
/*     */     public static ISendMediaMessageCallback asInterface(IBinder obj)
/*     */     {
/*  23 */       if (obj == null) {
/*  24 */         return null;
/*     */       }
/*  26 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.ISendMediaMessageCallback");
/*  27 */       if ((iin != null) && ((iin instanceof ISendMediaMessageCallback))) {
/*  28 */         return (ISendMediaMessageCallback)iin;
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
/*  42 */         reply.writeString("io.rong.imlib.ISendMediaMessageCallback");
/*  43 */         return true;
/*     */       case 1:
/*  47 */         data.enforceInterface("io.rong.imlib.ISendMediaMessageCallback");
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
/*  61 */         data.enforceInterface("io.rong.imlib.ISendMediaMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  63 */         if (0 != data.readInt()) {
/*  64 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  67 */           _arg0 = null;
/*     */         }
/*     */ 
/*  70 */         int _arg1 = data.readInt();
/*  71 */         onProgress(_arg0, _arg1);
/*  72 */         reply.writeNoException();
/*  73 */         return true;
/*     */       case 3:
/*  77 */         data.enforceInterface("io.rong.imlib.ISendMediaMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  79 */         if (0 != data.readInt()) {
/*  80 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  83 */           _arg0 = null;
/*     */         }
/*  85 */         onSuccess(_arg0);
/*  86 */         reply.writeNoException();
/*  87 */         return true;
/*     */       case 4:
/*  91 */         data.enforceInterface("io.rong.imlib.ISendMediaMessageCallback");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  93 */         if (0 != data.readInt()) {
/*  94 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  97 */           _arg0 = null;
/*     */         }
/*     */ 
/* 100 */         int _arg1 = data.readInt();
/* 101 */         onError(_arg0, _arg1);
/* 102 */         reply.writeNoException();
/* 103 */         return true;
/*     */       }
/*     */ 
/* 106 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements ISendMediaMessageCallback {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/* 113 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/* 117 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/* 121 */         return "io.rong.imlib.ISendMediaMessageCallback";
/*     */       }
/*     */ 
/*     */       public void onAttached(Message message) throws RemoteException {
/* 125 */         Parcel _data = Parcel.obtain();
/* 126 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 128 */           _data.writeInterfaceToken("io.rong.imlib.ISendMediaMessageCallback");
/* 129 */           if (message != null) {
/* 130 */             _data.writeInt(1);
/* 131 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 134 */             _data.writeInt(0);
/*     */           }
/* 136 */           this.mRemote.transact(1, _data, _reply, 0);
/* 137 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 140 */           _reply.recycle();
/* 141 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onProgress(Message message, int progress) throws RemoteException {
/* 146 */         Parcel _data = Parcel.obtain();
/* 147 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 149 */           _data.writeInterfaceToken("io.rong.imlib.ISendMediaMessageCallback");
/* 150 */           if (message != null) {
/* 151 */             _data.writeInt(1);
/* 152 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 155 */             _data.writeInt(0);
/*     */           }
/* 157 */           _data.writeInt(progress);
/* 158 */           this.mRemote.transact(2, _data, _reply, 0);
/* 159 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 162 */           _reply.recycle();
/* 163 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onSuccess(Message message) throws RemoteException {
/* 168 */         Parcel _data = Parcel.obtain();
/* 169 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 171 */           _data.writeInterfaceToken("io.rong.imlib.ISendMediaMessageCallback");
/* 172 */           if (message != null) {
/* 173 */             _data.writeInt(1);
/* 174 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 177 */             _data.writeInt(0);
/*     */           }
/* 179 */           this.mRemote.transact(3, _data, _reply, 0);
/* 180 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 183 */           _reply.recycle();
/* 184 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(Message message, int errorCode) throws RemoteException {
/* 189 */         Parcel _data = Parcel.obtain();
/* 190 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 192 */           _data.writeInterfaceToken("io.rong.imlib.ISendMediaMessageCallback");
/* 193 */           if (message != null) {
/* 194 */             _data.writeInt(1);
/* 195 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/* 198 */             _data.writeInt(0);
/*     */           }
/* 200 */           _data.writeInt(errorCode);
/* 201 */           this.mRemote.transact(4, _data, _reply, 0);
/* 202 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 205 */           _reply.recycle();
/* 206 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ISendMediaMessageCallback
 * JD-Core Version:    0.6.0
 */