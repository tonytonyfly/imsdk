/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class PublicServiceProfileList
/*    */   implements Parcelable
/*    */ {
/*    */   private ArrayList<PublicServiceProfile> mList;
/* 26 */   public static final Parcelable.Creator<PublicServiceProfileList> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public PublicServiceProfileList createFromParcel(Parcel source) {
/* 29 */       return new PublicServiceProfileList(source);
/*    */     }
/*    */ 
/*    */     public PublicServiceProfileList[] newArray(int size)
/*    */     {
/* 34 */       return new PublicServiceProfileList[size];
/*    */     }
/* 26 */   };
/*    */ 
/*    */   public PublicServiceProfileList()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PublicServiceProfileList(ArrayList<PublicServiceProfile> list)
/*    */   {
/* 18 */     this.mList = list;
/*    */   }
/*    */ 
/*    */   public PublicServiceProfileList(Parcel in)
/*    */   {
/* 23 */     this.mList = ParcelUtils.readListFromParcel(in, PublicServiceProfile.class);
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 40 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 46 */     ParcelUtils.writeListToParcel(dest, this.mList);
/*    */   }
/*    */ 
/*    */   public ArrayList<PublicServiceProfile> getPublicServiceData()
/*    */   {
/* 55 */     return this.mList;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.PublicServiceProfileList
 * JD-Core Version:    0.6.0
 */