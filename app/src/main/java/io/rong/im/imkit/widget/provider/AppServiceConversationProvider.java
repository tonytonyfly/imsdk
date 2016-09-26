/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.model.ConversationKey;
/*    */ import io.rong.imkit.model.ConversationProviderTag;
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ import io.rong.imlib.model.PublicServiceProfile;
/*    */ 
/*    */ @ConversationProviderTag(conversationType="app_public_service", portraitPosition=1)
/*    */ public class AppServiceConversationProvider extends PrivateConversationProvider
/*    */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*    */ {
/*    */   public String getTitle(String id)
/*    */   {
/* 18 */     ConversationKey mKey = ConversationKey.obtain(id, Conversation.ConversationType.APP_PUBLIC_SERVICE);
/* 19 */     PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
/*    */     String name;
/*    */     String name;
/* 21 */     if (info != null)
/* 22 */       name = info.getName();
/*    */     else {
/* 24 */       name = "";
/*    */     }
/* 26 */     return name;
/*    */   }
/*    */ 
/*    */   public Uri getPortraitUri(String id)
/*    */   {
/* 32 */     ConversationKey mKey = ConversationKey.obtain(id, Conversation.ConversationType.APP_PUBLIC_SERVICE);
/* 33 */     PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
/*    */     Uri uri;
/*    */     Uri uri;
/* 35 */     if (info != null)
/* 36 */       uri = info.getPortraitUri();
/*    */     else {
/* 38 */       uri = null;
/*    */     }
/* 40 */     return uri;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.AppServiceConversationProvider
 * JD-Core Version:    0.6.0
 */