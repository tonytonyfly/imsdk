/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ReadReceiptInfo
/*     */   implements Parcelable
/*     */ {
/*     */   private static final String TAG = "ReadReceiptInfo";
/* 117 */   public static final Parcelable.Creator<ReadReceiptInfo> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ReadReceiptInfo createFromParcel(Parcel source)
/*     */     {
/* 121 */       return new ReadReceiptInfo(source);
/*     */     }
/*     */ 
/*     */     public ReadReceiptInfo[] newArray(int size)
/*     */     {
/* 126 */       return new ReadReceiptInfo[size];
/*     */     }
/* 117 */   };
/*     */   private boolean isReadReceiptMessage;
/*     */   private boolean hasRespond;
/*     */   private HashMap<String, Long> respondUserIdList;
/*     */ 
/*     */   public JSONObject toJSON()
/*     */   {
/*  24 */     JSONObject jsonObject = new JSONObject();
/*     */     try {
/*  26 */       jsonObject.put("isReceiptRequestMessage", this.isReadReceiptMessage);
/*  27 */       jsonObject.put("hasRespond", this.hasRespond);
/*  28 */       if ((this.respondUserIdList != null) && 
/*  29 */         (this.respondUserIdList.size() > 0)) {
/*  30 */         JSONObject userObject = new JSONObject();
/*  31 */         for (Map.Entry entry : this.respondUserIdList.entrySet()) {
/*  32 */           userObject.put((String)entry.getKey(), ((Long)entry.getValue()).longValue());
/*     */         }
/*  34 */         jsonObject.put("userIdList", userObject);
/*     */       }
/*     */     }
/*     */     catch (JSONException e) {
/*  38 */       RLog.e("ReadReceiptInfo", e.getMessage());
/*     */     }
/*  40 */     return jsonObject;
/*     */   }
/*     */ 
/*     */   public ReadReceiptInfo() {
/*     */   }
/*     */ 
/*     */   public ReadReceiptInfo(String jsonString) {
/*  47 */     if (TextUtils.isEmpty(jsonString))
/*  48 */       return;
/*     */     try
/*     */     {
/*  51 */       JSONObject jsonObject = new JSONObject(jsonString);
/*  52 */       if (jsonObject.has("isReceiptRequestMessage")) {
/*  53 */         this.isReadReceiptMessage = jsonObject.optBoolean("isReceiptRequestMessage");
/*     */       }
/*  55 */       if (jsonObject.has("hasRespond")) {
/*  56 */         this.hasRespond = jsonObject.optBoolean("hasRespond");
/*     */       }
/*  58 */       if (jsonObject.has("userIdList")) {
/*  59 */         JSONObject userListObject = jsonObject.getJSONObject("userIdList");
/*  60 */         HashMap map = new HashMap();
/*  61 */         Iterator it = userListObject.keys();
/*  62 */         while (it.hasNext()) {
/*  63 */           String key = (String)it.next();
/*  64 */           long value = ((Long)userListObject.get(key)).longValue();
/*  65 */           map.put(key, Long.valueOf(value));
/*     */         }
/*  67 */         this.respondUserIdList = map;
/*     */       }
/*     */     } catch (JSONException e) {
/*  70 */       RLog.e("ReadReceiptInfo", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isReadReceiptMessage()
/*     */   {
/*  76 */     return this.isReadReceiptMessage;
/*     */   }
/*     */ 
/*     */   public void setIsReadReceiptMessage(boolean isReadReceiptMessage) {
/*  80 */     this.isReadReceiptMessage = isReadReceiptMessage;
/*     */   }
/*     */ 
/*     */   public boolean hasRespond() {
/*  84 */     return this.hasRespond;
/*     */   }
/*     */ 
/*     */   public void setHasRespond(boolean hasRespond) {
/*  88 */     this.hasRespond = hasRespond;
/*     */   }
/*     */ 
/*     */   public HashMap<String, Long> getRespondUserIdList() {
/*  92 */     return this.respondUserIdList;
/*     */   }
/*     */ 
/*     */   public void setRespondUserIdList(HashMap<String, Long> respondUserIdList) {
/*  96 */     this.respondUserIdList = respondUserIdList;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 101 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 106 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.isReadReceiptMessage ? 1 : 0));
/* 107 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.hasRespond ? 1 : 0));
/* 108 */     ParcelUtils.writeToParcel(dest, this.respondUserIdList);
/*     */   }
/*     */ 
/*     */   public ReadReceiptInfo(Parcel in) {
/* 112 */     setIsReadReceiptMessage(ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 113 */     setHasRespond(ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 114 */     setRespondUserIdList((HashMap)ParcelUtils.readMapFromParcel(in));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.ReadReceiptInfo
 * JD-Core Version:    0.6.0
 */