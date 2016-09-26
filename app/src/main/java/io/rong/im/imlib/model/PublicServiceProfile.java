/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class PublicServiceProfile
/*     */   implements Parcelable
/*     */ {
/*     */   private String name;
/*     */   private Uri portraitUri;
/*     */   private String publicServiceId;
/*     */   private Conversation.ConversationType publicServiceType;
/*     */   private boolean isFollowed;
/*     */   private String introduction;
/*     */   private boolean isGlobal;
/*     */   private PublicServiceMenu menu;
/*  94 */   public static final Parcelable.Creator<PublicServiceProfile> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public PublicServiceProfile createFromParcel(Parcel source)
/*     */     {
/*  98 */       return new PublicServiceProfile(source);
/*     */     }
/*     */ 
/*     */     public PublicServiceProfile[] newArray(int size)
/*     */     {
/* 103 */       return new PublicServiceProfile[size];
/*     */     }
/*  94 */   };
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  32 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  37 */     ParcelUtils.writeToParcel(dest, this.name);
/*  38 */     ParcelUtils.writeToParcel(dest, this.portraitUri);
/*  39 */     ParcelUtils.writeToParcel(dest, this.publicServiceId);
/*     */ 
/*  41 */     if (this.publicServiceType != null)
/*  42 */       ParcelUtils.writeToParcel(dest, Integer.valueOf(this.publicServiceType.getValue()));
/*     */     else {
/*  44 */       ParcelUtils.writeToParcel(dest, Integer.valueOf(0));
/*     */     }
/*     */ 
/*  47 */     ParcelUtils.writeToParcel(dest, this.introduction);
/*  48 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.isFollowed ? 1 : 0));
/*  49 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.isGlobal ? 1 : 0));
/*  50 */     ParcelUtils.writeToParcel(dest, this.menu);
/*     */   }
/*     */ 
/*     */   public PublicServiceProfile() {
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*     */     try {
/*  58 */       if (!TextUtils.isEmpty(extra)) {
/*  59 */         JSONObject jsonObj = new JSONObject(extra);
/*     */ 
/*  61 */         if (jsonObj.has("introduction")) {
/*  62 */           setIntroduction(jsonObj.optString("introduction"));
/*     */         }
/*  64 */         if (jsonObj.has("follow")) {
/*  65 */           setIsFollow(jsonObj.optBoolean("follow"));
/*     */         }
/*  67 */         if (jsonObj.has("isGlobal")) {
/*  68 */           setIsGlobal(jsonObj.optBoolean("isGlobal"));
/*     */         }
/*  70 */         if ((jsonObj.has("menu")) && (jsonObj.getJSONArray("menu") != null))
/*     */           try {
/*  72 */             this.menu = new PublicServiceMenu(jsonObj.getJSONArray("menu"));
/*     */           } catch (Exception e) {
/*  74 */             Log.e("DecodePSMenu", e.getMessage());
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (JSONException e) {
/*  79 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public PublicServiceProfile(Parcel source) {
/*  84 */     this.name = ParcelUtils.readFromParcel(source);
/*  85 */     this.portraitUri = ((Uri)ParcelUtils.readFromParcel(source, Uri.class));
/*  86 */     this.publicServiceId = ParcelUtils.readFromParcel(source);
/*  87 */     this.publicServiceType = Conversation.ConversationType.setValue(ParcelUtils.readIntFromParcel(source).intValue());
/*  88 */     this.introduction = ParcelUtils.readFromParcel(source);
/*  89 */     this.isFollowed = (ParcelUtils.readIntFromParcel(source).intValue() == 1);
/*  90 */     this.isGlobal = (ParcelUtils.readIntFromParcel(source).intValue() == 1);
/*  91 */     this.menu = ((PublicServiceMenu)ParcelUtils.readFromParcel(source, PublicServiceMenu.class));
/*     */   }
/*     */ 
/*     */   public void setIsGlobal(boolean global)
/*     */   {
/* 112 */     this.isGlobal = global;
/*     */   }
/*     */ 
/*     */   public Uri getPortraitUri()
/*     */   {
/* 120 */     return this.portraitUri;
/*     */   }
/*     */ 
/*     */   public void setPortraitUri(Uri portraitUri)
/*     */   {
/* 128 */     this.portraitUri = portraitUri;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 136 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 144 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getTargetId()
/*     */   {
/* 152 */     return this.publicServiceId;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String targetId)
/*     */   {
/* 160 */     this.publicServiceId = targetId;
/*     */   }
/*     */ 
/*     */   public void setIntroduction(String intro)
/*     */   {
/* 168 */     this.introduction = intro;
/*     */   }
/*     */ 
/*     */   public boolean isFollow()
/*     */   {
/* 176 */     return this.isFollowed;
/*     */   }
/*     */ 
/*     */   public void setIsFollow(boolean isFollow)
/*     */   {
/* 184 */     this.isFollowed = isFollow;
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationType getConversationType()
/*     */   {
/* 192 */     return this.publicServiceType;
/*     */   }
/*     */ 
/*     */   public void setPublicServiceType(Conversation.ConversationType publicServiceType)
/*     */   {
/* 200 */     this.publicServiceType = publicServiceType;
/*     */   }
/*     */ 
/*     */   public boolean isGlobal()
/*     */   {
/* 208 */     return this.isGlobal;
/*     */   }
/*     */ 
/*     */   public String getIntroduction()
/*     */   {
/* 216 */     return this.introduction;
/*     */   }
/*     */ 
/*     */   public PublicServiceMenu getMenu()
/*     */   {
/* 224 */     return this.menu;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.PublicServiceProfile
 * JD-Core Version:    0.6.0
 */