/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import java.util.ArrayList;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class PublicServiceMenuItem
/*     */   implements Parcelable
/*     */ {
/*     */   private String id;
/*     */   private String name;
/*     */   private String url;
/*     */   private PublicServiceMenu.PublicServiceMenuItemType type;
/*  21 */   private ArrayList<PublicServiceMenuItem> subMenuItems = new ArrayList();
/*     */ 
/* 145 */   public static final Parcelable.Creator<PublicServiceMenuItem> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public PublicServiceMenuItem createFromParcel(Parcel source)
/*     */     {
/* 149 */       return new PublicServiceMenuItem(source);
/*     */     }
/*     */ 
/*     */     public PublicServiceMenuItem[] newArray(int size)
/*     */     {
/* 154 */       return new PublicServiceMenuItem[size];
/*     */     }
/* 145 */   };
/*     */ 
/*     */   public String getName()
/*     */   {
/*  28 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/*  36 */     return this.url;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenu.PublicServiceMenuItemType getType()
/*     */   {
/*  44 */     return this.type;
/*     */   }
/*     */ 
/*     */   public ArrayList<PublicServiceMenuItem> getSubMenuItems()
/*     */   {
/*  52 */     return this.subMenuItems;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  60 */     return this.id;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenuItem(JSONObject jsonItem) throws Exception {
/*     */     try {
/*  65 */       if (jsonItem.has("id")) {
/*  66 */         this.id = jsonItem.optString("id");
/*     */       }
/*  68 */       if (jsonItem.has("name")) {
/*  69 */         this.name = jsonItem.optString("name");
/*     */       }
/*  71 */       if (jsonItem.has("url")) {
/*  72 */         this.url = jsonItem.optString("url");
/*     */       }
/*  74 */       if (jsonItem.has("type")) {
/*  75 */         this.type = PublicServiceMenu.PublicServiceMenuItemType.setValue(jsonItem.optInt("type"));
/*  76 */         if ((this.type != null) && (this.type == PublicServiceMenu.PublicServiceMenuItemType.Group) && 
/*  77 */           (jsonItem.has("children"))) {
/*  78 */           JSONArray jsonArray = jsonItem.getJSONArray("children");
/*  79 */           if (jsonArray != null) {
/*  80 */             for (int i = 0; i < jsonArray.length(); i++) {
/*  81 */               JSONObject jsonSubItem = jsonArray.optJSONObject(i);
/*  82 */               if (jsonSubItem == null) continue;
/*     */               try {
/*  84 */                 PublicServiceMenuItem item = new PublicServiceMenuItem(jsonSubItem);
/*  85 */                 this.subMenuItems.add(item);
/*     */               } catch (Exception e) {
/*  87 */                 e.printStackTrace();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/*  96 */       e.printStackTrace();
/*  97 */       throw new Exception("PublicServiceMenuItem parse error!");
/*     */     }
/*     */   }
/*     */ 
/*     */   private PublicServiceMenuItem()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 111 */     return 0;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenuItem(Parcel in)
/*     */   {
/* 120 */     this.id = ParcelUtils.readFromParcel(in);
/* 121 */     this.name = ParcelUtils.readFromParcel(in);
/* 122 */     this.url = ParcelUtils.readFromParcel(in);
/* 123 */     this.type = PublicServiceMenu.PublicServiceMenuItemType.setValue(ParcelUtils.readIntFromParcel(in).intValue());
/* 124 */     this.subMenuItems = ParcelUtils.readListFromParcel(in, PublicServiceMenuItem.class);
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 135 */     ParcelUtils.writeToParcel(dest, this.id);
/* 136 */     ParcelUtils.writeToParcel(dest, this.name);
/* 137 */     ParcelUtils.writeToParcel(dest, this.url);
/* 138 */     ParcelUtils.writeToParcel(dest, this.type != null ? Integer.valueOf(this.type.getValue()) : null);
/* 139 */     ParcelUtils.writeToParcel(dest, this.subMenuItems);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.PublicServiceMenuItem
 * JD-Core Version:    0.6.0
 */