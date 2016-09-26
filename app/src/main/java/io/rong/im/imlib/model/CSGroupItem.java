/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class CSGroupItem
/*    */   implements Parcelable
/*    */ {
/* 16 */   private String id = "";
/* 17 */   private String name = "";
/* 18 */   private boolean online = false;
/*    */ 
/* 81 */   public static final Parcelable.Creator<CSGroupItem> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public CSGroupItem createFromParcel(Parcel source)
/*    */     {
/* 85 */       return new CSGroupItem(source);
/*    */     }
/*    */ 
/*    */     public CSGroupItem[] newArray(int size)
/*    */     {
/* 90 */       return new CSGroupItem[size];
/*    */     }
/* 81 */   };
/*    */ 
/*    */   public CSGroupItem(String id, String name, boolean online)
/*    */   {
/* 22 */     this.id = id;
/* 23 */     this.name = name;
/* 24 */     this.online = online;
/*    */   }
/*    */ 
/*    */   public CSGroupItem(Parcel in) {
/* 28 */     this.id = ParcelUtils.readFromParcel(in);
/* 29 */     this.name = ParcelUtils.readFromParcel(in);
/* 30 */     this.online = (ParcelUtils.readIntFromParcel(in).intValue() == 1);
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 38 */     return this.id;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 46 */     return this.name;
/*    */   }
/*    */ 
/*    */   public boolean getOnline()
/*    */   {
/* 55 */     return this.online;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 65 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 76 */     ParcelUtils.writeToParcel(dest, this.id);
/* 77 */     ParcelUtils.writeToParcel(dest, this.name);
/* 78 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.online ? 1 : 0));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.CSGroupItem
 * JD-Core Version:    0.6.0
 */