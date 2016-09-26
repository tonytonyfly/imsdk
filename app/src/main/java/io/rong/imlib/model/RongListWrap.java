/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class RongListWrap
/*    */   implements Parcelable
/*    */ {
/* 17 */   private List mList = new ArrayList();
/*    */   public static Class mClass;
/* 37 */   public static final Parcelable.Creator<RongListWrap> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RongListWrap createFromParcel(Parcel source) {
/* 40 */       return new RongListWrap(source);
/*    */     }
/*    */ 
/*    */     public RongListWrap[] newArray(int size)
/*    */     {
/* 45 */       return new RongListWrap[size];
/*    */     }
/* 37 */   };
/*    */ 
/*    */   public RongListWrap()
/*    */   {
/*    */   }
/*    */ 
/*    */   public static RongListWrap obtain(List list, Class cls)
/*    */   {
/* 25 */     return new RongListWrap(list, cls);
/*    */   }
/*    */ 
/*    */   public RongListWrap(List list, Class cls) {
/* 29 */     this.mList = list;
/* 30 */     mClass = cls;
/*    */   }
/*    */ 
/*    */   public RongListWrap(Parcel in) {
/* 34 */     this.mList = ParcelUtils.readListFromParcel(in, Message.class);
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 51 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 56 */     ParcelUtils.writeListToParcel(dest, this.mList);
/*    */   }
/*    */ 
/*    */   public List getList() {
/* 60 */     return this.mList;
/*    */   }
/*    */ 
/*    */   public void setList(List list) {
/* 64 */     this.mList = list;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.RongListWrap
 * JD-Core Version:    0.6.0
 */