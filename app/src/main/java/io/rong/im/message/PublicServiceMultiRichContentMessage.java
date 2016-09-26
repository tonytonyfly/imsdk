/*    */ package io.rong.message;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import android.util.Log;
/*    */ import io.rong.common.ParcelUtils;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ import java.util.ArrayList;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ @MessageTag(value="RC:PSMultiImgTxtMsg", flag=3)
/*    */ public class PublicServiceMultiRichContentMessage extends MessageContent
/*    */ {
/* 21 */   private ArrayList<RichContentItem> mRichContentItems = new ArrayList();
/*    */   private UserInfo mUser;
/* 79 */   public static final Parcelable.Creator<PublicServiceMultiRichContentMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public PublicServiceMultiRichContentMessage createFromParcel(Parcel source) {
/* 82 */       return new PublicServiceMultiRichContentMessage(source);
/*    */     }
/*    */ 
/*    */     public PublicServiceMultiRichContentMessage[] newArray(int size)
/*    */     {
/* 87 */       return new PublicServiceMultiRichContentMessage[size];
/*    */     }
/* 79 */   };
/*    */ 
/*    */   public PublicServiceMultiRichContentMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PublicServiceMultiRichContentMessage(byte[] data)
/*    */   {
/* 29 */     String jsonStr = new String(data);
/*    */     try
/*    */     {
/* 32 */       JSONObject jsonObj = new JSONObject(jsonStr);
/* 33 */       JSONArray items = (JSONArray)jsonObj.get("articles");
/*    */ 
/* 35 */       for (int i = 0; i < items.length(); i++) {
/* 36 */         jsonObj = (JSONObject)items.get(i);
/*    */ 
/* 38 */         RichContentItem richContentItem = new RichContentItem(jsonObj);
/* 39 */         this.mRichContentItems.add(richContentItem);
/*    */       }
/*    */ 
/* 42 */       JSONObject user = (JSONObject)jsonObj.get("user");
/* 43 */       if (jsonObj.has("portrait")) {
/* 44 */         Uri uri = Uri.parse(jsonObj.optString("portrait"));
/* 45 */         this.mUser = new UserInfo(jsonObj.optString("id"), jsonObj.optString("name"), uri);
/*    */       }
/*    */     } catch (JSONException e) {
/* 48 */       Log.e("JSONException", e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 54 */     return null;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 59 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 64 */     ParcelUtils.writeListToParcel(dest, this.mRichContentItems);
/*    */   }
/*    */ 
/*    */   public ArrayList<RichContentItem> getMessages() {
/* 68 */     return this.mRichContentItems;
/*    */   }
/*    */ 
/*    */   public UserInfo getPublicServiceUserInfo() {
/* 72 */     return this.mUser;
/*    */   }
/*    */ 
/*    */   public PublicServiceMultiRichContentMessage(Parcel in) {
/* 76 */     this.mRichContentItems = ParcelUtils.readListFromParcel(in, RichContentItem.class);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.PublicServiceMultiRichContentMessage
 * JD-Core Version:    0.6.0
 */