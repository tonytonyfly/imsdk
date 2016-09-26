/*    */ package io.rong.message;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import android.util.Log;
/*    */ import io.rong.common.ParcelUtils;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ @MessageTag(value="RC:PSImgTxtMsg", flag=3)
/*    */ public class PublicServiceRichContentMessage extends MessageContent
/*    */   implements Parcelable
/*    */ {
/*    */   private RichContentItem mRichContentItem;
/*    */   private UserInfo mUser;
/* 75 */   public static final Parcelable.Creator<PublicServiceRichContentMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public PublicServiceRichContentMessage createFromParcel(Parcel source) {
/* 78 */       return new PublicServiceRichContentMessage(source);
/*    */     }
/*    */ 
/*    */     public PublicServiceRichContentMessage[] newArray(int size)
/*    */     {
/* 83 */       return new PublicServiceRichContentMessage[size];
/*    */     }
/* 75 */   };
/*    */ 
/*    */   public PublicServiceRichContentMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PublicServiceRichContentMessage(byte[] data)
/*    */   {
/* 27 */     String jsonStr = new String(data);
/*    */     try
/*    */     {
/* 30 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*    */ 
/* 32 */       JSONArray items = (JSONArray)jsonObj.get("articles");
/* 33 */       jsonObj = (JSONObject)items.get(0);
/* 34 */       this.mRichContentItem = new RichContentItem(jsonObj);
/*    */ 
/* 36 */       JSONObject user = (JSONObject)jsonObj.get("user");
/* 37 */       if (jsonObj.has("portrait")) {
/* 38 */         Uri uri = Uri.parse(jsonObj.optString("portrait"));
/* 39 */         this.mUser = new UserInfo(jsonObj.optString("id"), jsonObj.optString("name"), uri);
/*    */       }
/*    */     }
/*    */     catch (JSONException e) {
/* 43 */       Log.e("JSONException", e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public RichContentItem getMessage() {
/* 48 */     return this.mRichContentItem;
/*    */   }
/*    */ 
/*    */   public UserInfo getPublicServiceUserInfo() {
/* 52 */     return this.mUser;
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 57 */     return null;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 62 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 67 */     ParcelUtils.writeToParcel(dest, this.mRichContentItem);
/*    */   }
/*    */ 
/*    */   public PublicServiceRichContentMessage(Parcel in)
/*    */   {
/* 72 */     this.mRichContentItem = ((RichContentItem)ParcelUtils.readFromParcel(in, RichContentItem.class));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.PublicServiceRichContentMessage
 * JD-Core Version:    0.6.0
 */