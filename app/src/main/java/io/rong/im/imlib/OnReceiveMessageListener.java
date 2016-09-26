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
/*     */ public abstract interface OnReceiveMessageListener extends IInterface
/*     */ {
/*     */   public abstract boolean onReceived(Message paramMessage, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */     throws RemoteException;
/*     */ 
/*     */   public static abstract class Stub extends Binder
/*     */     implements OnReceiveMessageListener
/*     */   {
/*     */     private static final String DESCRIPTOR = "io.rong.imlib.OnReceiveMessageListener";
/*     */     static final int TRANSACTION_onReceived = 1;
/*     */ 
/*     */     public Stub()
/*     */     {
/*  15 */       attachInterface(this, "io.rong.imlib.OnReceiveMessageListener");
/*     */     }
/*     */ 
/*     */     public static OnReceiveMessageListener asInterface(IBinder obj)
/*     */     {
/*  23 */       if (obj == null) {
/*  24 */         return null;
/*     */       }
/*  26 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.OnReceiveMessageListener");
/*  27 */       if ((iin != null) && ((iin instanceof OnReceiveMessageListener))) {
/*  28 */         return (OnReceiveMessageListener)iin;
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
/*  42 */         reply.writeString("io.rong.imlib.OnReceiveMessageListener");
/*  43 */         return true;
/*     */       case 1:
/*  47 */         data.enforceInterface("io.rong.imlib.OnReceiveMessageListener");
/*     */         Message _arg0;
/*     */         Message _arg0;
/*  49 */         if (0 != data.readInt()) {
/*  50 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*     */         }
/*     */         else {
/*  53 */           _arg0 = null;
/*     */         }
/*     */ 
/*  56 */         int _arg1 = data.readInt();
/*     */ 
/*  58 */         boolean _arg2 = 0 != data.readInt();
/*     */ 
/*  60 */         int _arg3 = data.readInt();
/*  61 */         boolean _result = onReceived(_arg0, _arg1, _arg2, _arg3);
/*  62 */         reply.writeNoException();
/*  63 */         reply.writeInt(_result ? 1 : 0);
/*  64 */         return true;
/*     */       }
/*     */ 
/*  67 */       return super.onTransact(code, data, reply, flags);
/*     */     }
/*     */     private static class Proxy implements OnReceiveMessageListener {
/*     */       private IBinder mRemote;
/*     */ 
/*     */       Proxy(IBinder remote) {
/*  74 */         this.mRemote = remote;
/*     */       }
/*     */ 
/*     */       public IBinder asBinder() {
/*  78 */         return this.mRemote;
/*     */       }
/*     */ 
/*     */       public String getInterfaceDescriptor() {
/*  82 */         return "io.rong.imlib.OnReceiveMessageListener";
/*     */       }
/*  86 */       public boolean onReceived(Message message, int left, boolean offline, int cmdLeft) throws RemoteException { Parcel _data = Parcel.obtain();
/*  87 */         Parcel _reply = Parcel.obtain();
/*     */         boolean _result;
/*     */         try { _data.writeInterfaceToken("io.rong.imlib.OnReceiveMessageListener");
/*  91 */           if (message != null) {
/*  92 */             _data.writeInt(1);
/*  93 */             message.writeToParcel(_data, 0);
/*     */           }
/*     */           else {
/*  96 */             _data.writeInt(0);
/*     */           }
/*  98 */           _data.writeInt(left);
/*  99 */           _data.writeInt(offline ? 1 : 0);
/* 100 */           _data.writeInt(cmdLeft);
/* 101 */           this.mRemote.transact(1, _data, _reply, 0);
/* 102 */           _reply.readException();
/* 103 */           _result = 0 != _reply.readInt();
/*     */         } finally
/*     */         {
/* 106 */           _reply.recycle();
/* 107 */           _data.recycle();
/*     */         }
/* 109 */         return _result;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.OnReceiveMessageListener
 * JD-Core Version:    0.6.0
 */