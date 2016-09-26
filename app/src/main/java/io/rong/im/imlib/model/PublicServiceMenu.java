/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import java.util.ArrayList;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class PublicServiceMenu
/*     */   implements Parcelable
/*     */ {
/*     */   private ArrayList<PublicServiceMenuItem> menuItems;
/* 143 */   public static final Parcelable.Creator<PublicServiceMenu> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public PublicServiceMenu createFromParcel(Parcel source)
/*     */     {
/* 147 */       return new PublicServiceMenu(source);
/*     */     }
/*     */ 
/*     */     public PublicServiceMenu[] newArray(int size)
/*     */     {
/* 152 */       return new PublicServiceMenu[size];
/*     */     }
/* 143 */   };
/*     */ 
/*     */   public ArrayList<PublicServiceMenuItem> getMenuItems()
/*     */   {
/*  87 */     return this.menuItems;
/*     */   }
/*     */ 
/*     */   public void setMenuItems(ArrayList<PublicServiceMenuItem> menuItems) {
/*  91 */     this.menuItems = menuItems;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenu(JSONArray jsonArray) {
/*  95 */     this.menuItems = new ArrayList();
/*  96 */     for (int i = 0; i < jsonArray.length(); i++)
/*     */       try {
/*  98 */         JSONObject jsonItem = jsonArray.optJSONObject(i);
/*  99 */         PublicServiceMenuItem item = new PublicServiceMenuItem(jsonItem);
/* 100 */         this.menuItems.add(item);
/*     */       } catch (Exception e) {
/* 102 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private PublicServiceMenu()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 117 */     return 0;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenu(Parcel in)
/*     */   {
/* 126 */     this.menuItems = ParcelUtils.readListFromParcel(in, PublicServiceMenuItem.class);
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 137 */     ParcelUtils.writeListToParcel(dest, this.menuItems);
/*     */   }
/*     */ 
/*     */   public static enum PublicServiceMenuItemType
/*     */   {
/*  21 */     Group(0, "GROUP"), 
/*     */ 
/*  26 */     View(1, "VIEW"), 
/*     */ 
/*  31 */     Click(2, "CLICK"), 
/*     */ 
/*  36 */     Entry(3, "ENTRY");
/*     */ 
/*  38 */     private int value = 1;
/*     */     private String command;
/*     */ 
/*     */     private PublicServiceMenuItemType(int value, String command)
/*     */     {
/*  47 */       this.value = value;
/*  48 */       this.command = command;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/*  57 */       return this.value;
/*     */     }
/*     */ 
/*     */     public String getMessage()
/*     */     {
/*  66 */       return this.command;
/*     */     }
/*     */ 
/*     */     public static PublicServiceMenuItemType setValue(int code)
/*     */     {
/*  76 */       for (PublicServiceMenuItemType c : values()) {
/*  77 */         if (code == c.getValue()) {
/*  78 */           return c;
/*     */         }
/*     */       }
/*  81 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.PublicServiceMenu
 * JD-Core Version:    0.6.0
 */