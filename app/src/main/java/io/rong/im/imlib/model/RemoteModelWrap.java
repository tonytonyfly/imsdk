/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class RemoteModelWrap
/*    */   implements Parcelable
/*    */ {
/*    */   Parcelable model;
/* 44 */   public static final Parcelable.Creator<RemoteModelWrap> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RemoteModelWrap createFromParcel(Parcel source)
/*    */     {
/* 48 */       return new RemoteModelWrap(source);
/*    */     }
/*    */ 
/*    */     public RemoteModelWrap[] newArray(int size)
/*    */     {
/* 53 */       return new RemoteModelWrap[size];
/*    */     }
/* 44 */   };
/*    */ 
/*    */   public RemoteModelWrap()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RemoteModelWrap(Parcelable t)
/*    */   {
/* 15 */     this.model = t;
/*    */   }
/*    */ 
/*    */   public RemoteModelWrap(Parcel in) {
/* 19 */     String className = ParcelUtils.readFromParcel(in);
/*    */ 
/* 21 */     Class loader = null;
/*    */     try {
/* 23 */       loader = Class.forName(className);
/*    */     } catch (ClassNotFoundException e) {
/* 25 */       e.printStackTrace();
/*    */     }
/* 27 */     this.model = ParcelUtils.readFromParcel(in, loader);
/*    */   }
/*    */ 
/*    */   public <T extends Parcelable> T getContent() {
/* 31 */     return this.model;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 36 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 41 */     ParcelUtils.writeToParcel(dest, this.model.getClass().getName());
/* 42 */     ParcelUtils.writeToParcel(dest, this.model);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.RemoteModelWrap
 * JD-Core Version:    0.6.0
 */