/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag("RC:RRRspMsg")
/*     */ public class ReadReceiptResponseMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "ReadReceiptResponseMessage";
/*     */   private static final String RECEIPT_MAP = "receiptMessageDic";
/*     */   private HashMap<String, ArrayList<String>> mReceiptMap;
/* 141 */   public static final Parcelable.Creator<ReadReceiptResponseMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ReadReceiptResponseMessage createFromParcel(Parcel source) {
/* 144 */       return new ReadReceiptResponseMessage(source);
/*     */     }
/*     */ 
/*     */     public ReadReceiptResponseMessage[] newArray(int size)
/*     */     {
/* 149 */       return new ReadReceiptResponseMessage[size];
/*     */     }
/* 141 */   };
/*     */ 
/*     */   public ReadReceiptResponseMessage(HashMap<String, ArrayList<String>> receiptMap)
/*     */   {
/*  38 */     this.mReceiptMap = receiptMap;
/*     */   }
/*     */ 
/*     */   public ReadReceiptResponseMessage(List<Message> messageList) {
/*  42 */     this.mReceiptMap = new HashMap();
/*  43 */     for (Message message : messageList) {
/*  44 */       String userId = message.getSenderUserId();
/*  45 */       String messageUId = message.getUId();
/*  46 */       ArrayList messageUIdList = (ArrayList)this.mReceiptMap.get(userId);
/*  47 */       if (messageUIdList == null) {
/*  48 */         messageUIdList = new ArrayList();
/*     */       }
/*  50 */       if (!messageUIdList.contains(messageUId)) {
/*  51 */         messageUIdList.add(messageUId);
/*  52 */         this.mReceiptMap.put(userId, messageUIdList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<String> getSenderIdSet() {
/*  58 */     return this.mReceiptMap.keySet();
/*     */   }
/*     */ 
/*     */   public ArrayList<String> getMessageUIdListBySenderId(String senderId) {
/*  62 */     return (ArrayList)this.mReceiptMap.get(senderId);
/*     */   }
/*     */ 
/*     */   public ReadReceiptResponseMessage(Parcel in) {
/*  66 */     this.mReceiptMap = ((HashMap)ParcelUtils.readMapFromParcel(in));
/*     */   }
/*     */ 
/*     */   public ReadReceiptResponseMessage(byte[] data) {
/*  70 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  73 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  75 */       RLog.e("ReadReceiptResponseMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  79 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  80 */       if (jsonObj.has("receiptMessageDic")) {
/*  81 */         this.mReceiptMap = new HashMap();
/*  82 */         JSONObject valueObj = jsonObj.getJSONObject("receiptMessageDic");
/*  83 */         Iterator it = valueObj.keys();
/*  84 */         while (it.hasNext()) {
/*  85 */           String key = it.next().toString();
/*  86 */           JSONArray jsonArray = valueObj.getJSONArray(key);
/*  87 */           ArrayList messageUIdList = new ArrayList();
/*  88 */           for (int i = 0; i < jsonArray.length(); i++) {
/*  89 */             messageUIdList.add(jsonArray.getString(i));
/*     */           }
/*  91 */           this.mReceiptMap.put(key, messageUIdList);
/*     */         }
/*     */       }
/*     */     } catch (JSONException e) {
/*  95 */       RLog.e("ReadReceiptResponseMessage", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 101 */     JSONObject jsonObj = new JSONObject();
/* 102 */     JSONObject valueObj = new JSONObject();
/*     */     try {
/* 104 */       if (this.mReceiptMap != null) {
/* 105 */         for (String key : this.mReceiptMap.keySet()) {
/* 106 */           ArrayList messageUIdList = (ArrayList)this.mReceiptMap.get(key);
/* 107 */           JSONArray jsonArray = new JSONArray();
/* 108 */           for (String uId : messageUIdList) {
/* 109 */             jsonArray.put(uId);
/*     */           }
/* 111 */           valueObj.put(key, jsonArray);
/*     */         }
/* 113 */         jsonObj.put("receiptMessageDic", valueObj);
/*     */       }
/*     */     } catch (JSONException e) {
/* 116 */       RLog.e("ReadReceiptResponseMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 120 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 123 */       e.printStackTrace();
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 130 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 135 */     ParcelUtils.writeToParcel(dest, this.mReceiptMap);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ReadReceiptResponseMessage
 * JD-Core Version:    0.6.0
 */