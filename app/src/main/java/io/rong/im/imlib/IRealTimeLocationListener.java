/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.os.Binder;
/*     */ import android.os.IBinder;
/*     */ import android.os.IInterface;
/*     */ import android.os.Parcel;
/*     */ import android.os.RemoteException;
/*     */ 
/*     */ public abstract interface IRealTimeLocationListener extends IInterface
/*     */ {
/*     */   public abstract void onStatusChange(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onReceiveLocation(double paramDouble1, double paramDouble2, String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onParticipantsJoin(String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onParticipantsQuit(String paramString)
/*     */     throws RemoteException;
/*     */ 
/*     */   public abstract void onError(int paramInt)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements IRealTimeLocationListener
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.IRealTimeLocationListener";
/*     */     static final int TRANSACTION_onStatusChange = 1;
/*     */     static final int TRANSACTION_onReceiveLocation = 2;
/*     */     static final int TRANSACTION_onParticipantsJoin = 3;
/*     */     static final int TRANSACTION_onParticipantsQuit = 4;
/*     */     static final int TRANSACTION_onError = 5;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  15 */       attachInterface(this, "io.rong.imlib.IRealTimeLocationListener");
/*     */     }
/*     */ 
/*     */     public static IRealTimeLocationListener asInterface(IBinder obj)
/*     */     {
/*  23 */       if (obj == null) {
/*  24 */         return null;
/*     */       }
/*  26 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IRealTimeLocationListener");
/*  27 */       if ((iin != null) && ((iin instanceof IRealTimeLocationListener))) {
/*  28 */         return (IRealTimeLocationListener)iin;
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
/*  42 */         reply.writeString("io.rong.imlib.IRealTimeLocationListener");
/*  43 */         return true;
/*     */       case 1:
/*  47 */         data.enforceInterface("io.rong.imlib.IRealTimeLocationListener");
/*     */ 
/*  49 */         int _arg0 = data.readInt();
/*  50 */         onStatusChange(_arg0);
/*  51 */         reply.writeNoException();
/*  52 */         return true;
/*     */       case 2:
/*  56 */         data.enforceInterface("io.rong.imlib.IRealTimeLocationListener");
/*     */ 
/*  58 */         double _arg0 = data.readDouble();
/*     */ 
/*  60 */         double _arg1 = data.readDouble();
/*     */ 
/*  62 */         String _arg2 = data.readString();
/*  63 */         onReceiveLocation(_arg0, _arg1, _arg2);
/*  64 */         reply.writeNoException();
/*  65 */         return true;
/*     */       case 3:
/*  69 */         data.enforceInterface("io.rong.imlib.IRealTimeLocationListener");
/*     */ 
/*  71 */         String _arg0 = data.readString();
/*  72 */         onParticipantsJoin(_arg0);
/*  73 */         reply.writeNoException();
/*  74 */         return true;
/*     */       case 4:
/*  78 */         data.enforceInterface("io.rong.imlib.IRealTimeLocationListener");
/*     */ 
/*  80 */         String _arg0 = data.readString();
/*  81 */         onParticipantsQuit(_arg0);
/*  82 */         reply.writeNoException();
/*  83 */         return true;
/*     */       case 5:
/*  87 */         data.enforceInterface("io.rong.imlib.IRealTimeLocationListener");
/*     */ 
/*  89 */         int _arg0 = data.readInt();
/*  90 */         onError(_arg0);
/*  91 */         reply.writeNoException();
/*  92 */         return true;
/*     */       }
/*     */ 
/*  95 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements IRealTimeLocationListener {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/* 102 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/* 106 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/* 110 */         return "io.rong.imlib.IRealTimeLocationListener";
/*     */       }
/*     */ 
/*     */       public void onStatusChange(int status) throws RemoteException {
/* 114 */         Parcel _data = Parcel.obtain();
/* 115 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 117 */           _data.writeInterfaceToken("io.rong.imlib.IRealTimeLocationListener");
/* 118 */           _data.writeInt(status);
/* 119 */           this.mRemote.transact(1, _data, _reply, 0);
/* 120 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 123 */           _reply.recycle();
/* 124 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onReceiveLocation(double latitude, double longitude, String userId) throws RemoteException {
/* 129 */         Parcel _data = Parcel.obtain();
/* 130 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 132 */           _data.writeInterfaceToken("io.rong.imlib.IRealTimeLocationListener");
/* 133 */           _data.writeDouble(latitude);
/* 134 */           _data.writeDouble(longitude);
/* 135 */           _data.writeString(userId);
/* 136 */           this.mRemote.transact(2, _data, _reply, 0);
/* 137 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 140 */           _reply.recycle();
/* 141 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onParticipantsJoin(String userId) throws RemoteException {
/* 146 */         Parcel _data = Parcel.obtain();
/* 147 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 149 */           _data.writeInterfaceToken("io.rong.imlib.IRealTimeLocationListener");
/* 150 */           _data.writeString(userId);
/* 151 */           this.mRemote.transact(3, _data, _reply, 0);
/* 152 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 155 */           _reply.recycle();
/* 156 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onParticipantsQuit(String userId) throws RemoteException {
/* 161 */         Parcel _data = Parcel.obtain();
/* 162 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 164 */           _data.writeInterfaceToken("io.rong.imlib.IRealTimeLocationListener");
/* 165 */           _data.writeString(userId);
/* 166 */           this.mRemote.transact(4, _data, _reply, 0);
/* 167 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 170 */           _reply.recycle();
/* 171 */           _data.recycle();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(int errorCode) throws RemoteException {
/* 176 */         Parcel _data = Parcel.obtain();
/* 177 */         Parcel _reply = Parcel.obtain();
/*     */         try {
/* 179 */           _data.writeInterfaceToken("io.rong.imlib.IRealTimeLocationListener");
/* 180 */           _data.writeInt(errorCode);
/* 181 */           this.mRemote.transact(5, _data, _reply, 0);
/* 182 */           _reply.readException();
/*     */         }
/*     */         finally {
/* 185 */           _reply.recycle();
/* 186 */           _data.recycle();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IRealTimeLocationListener
 * JD-Core Version:    0.6.0
 */