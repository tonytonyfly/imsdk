/*    */ package io.rong.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class CSHumanEvaluateItem
/*    */   implements Parcelable
/*    */ {
/*    */   private int value;
/* 14 */   private String description = "";
/*    */ 
/* 65 */   public static final Parcelable.Creator<CSHumanEvaluateItem> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public CSHumanEvaluateItem createFromParcel(Parcel source)
/*    */     {
/* 69 */       return new CSHumanEvaluateItem(source);
/*    */     }
/*    */ 
/*    */     public CSHumanEvaluateItem[] newArray(int size)
/*    */     {
/* 74 */       return new CSHumanEvaluateItem[size];
/*    */     }
/* 65 */   };
/*    */ 
/*    */   public CSHumanEvaluateItem(int value, String description)
/*    */   {
/* 18 */     this.value = value;
/* 19 */     this.description = description;
/*    */   }
/*    */ 
/*    */   public CSHumanEvaluateItem(Parcel in) {
/* 23 */     this.value = ParcelUtils.readIntFromParcel(in).intValue();
/* 24 */     this.description = ParcelUtils.readFromParcel(in);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 32 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 40 */     return this.description;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 50 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 61 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.value));
/* 62 */     ParcelUtils.writeToParcel(dest, this.description);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSHumanEvaluateItem
 * JD-Core Version:    0.6.0
 */