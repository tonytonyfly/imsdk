/*    */ package io.rong.imlib;
/*    */ 
/*    */ import android.os.Binder;
/*    */ import android.os.IBinder;
/*    */ import android.os.IInterface;
/*    */ import android.os.Parcel;
/*    */ import android.os.RemoteException;
/*    */ 
/*    */ public abstract interface IConnectionStatusListener extends IInterface
/*    */ {
/*    */   public abstract void onChanged(int paramInt)
/*    */     throws RemoteException;
/*    */ 
/*    */   public static abstract class Stub extends Binder
/*    */     implements IConnectionStatusListener
/*    */   {
/*    */     private static final String DESCRIPTOR = "io.rong.imlib.IConnectionStatusListener";
/*    */     static final int TRANSACTION_onChanged = 1;
/*    */ 
/*    */     public Stub()
/*    */     {
/* 17 */       attachInterface(this, "io.rong.imlib.IConnectionStatusListener");
/*    */     }
/*    */ 
/*    */     public static IConnectionStatusListener asInterface(IBinder obj)
/*    */     {
/* 25 */       if (obj == null) {
/* 26 */         return null;
/*    */       }
/* 28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IConnectionStatusListener");
/* 29 */       if ((iin != null) && ((iin instanceof IConnectionStatusListener))) {
/* 30 */         return (IConnectionStatusListener)iin;
/*    */       }
/* 32 */       return new Proxy(obj);
/*    */     }
/*    */ 
/*    */     public IBinder asBinder() {
/* 36 */       return this;
/*    */     }
/*    */ 
/*    */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/* 40 */       switch (code)
/*    */       {
/*    */       case 1598968902:
/* 44 */         reply.writeString("io.rong.imlib.IConnectionStatusListener");
/* 45 */         return true;
/*    */       case 1:
/* 49 */         data.enforceInterface("io.rong.imlib.IConnectionStatusListener");
/*    */ 
/* 51 */         int _arg0 = data.readInt();
/* 52 */         onChanged(_arg0);
/* 53 */         reply.writeNoException();
/* 54 */         return true;
/*    */       }
/*    */ 
/* 57 */       return super.onTransact(code, data, reply, flags);
/*    */     }
/*    */     private static class Proxy implements IConnectionStatusListener {
/*    */       private IBinder mRemote;
/*    */ 
/*    */       Proxy(IBinder remote) {
/* 64 */         this.mRemote = remote;
/*    */       }
/*    */ 
/*    */       public IBinder asBinder() {
/* 68 */         return this.mRemote;
/*    */       }
/*    */ 
/*    */       public String getInterfaceDescriptor() {
/* 72 */         return "io.rong.imlib.IConnectionStatusListener";
/*    */       }
/*    */ 
/*    */       public void onChanged(int status)
/*    */         throws RemoteException
/*    */       {
/* 80 */         Parcel _data = Parcel.obtain();
/* 81 */         Parcel _reply = Parcel.obtain();
/*    */         try {
/* 83 */           _data.writeInterfaceToken("io.rong.imlib.IConnectionStatusListener");
/* 84 */           _data.writeInt(status);
/* 85 */           this.mRemote.transact(1, _data, _reply, 0);
/* 86 */           _reply.readException();
/*    */         }
/*    */         finally {
/* 89 */           _reply.recycle();
/* 90 */           _data.recycle();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IConnectionStatusListener
 * JD-Core Version:    0.6.0
 */