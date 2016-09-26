/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import android.view.View;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.model.ConversationKey;
/*    */ import io.rong.imkit.model.ConversationProviderTag;
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ import io.rong.imlib.model.PublicServiceProfile;
/*    */ 
/*    */ @ConversationProviderTag(conversationType="public_service", portraitPosition=1)
/*    */ public class PublicServiceConversationProvider extends PrivateConversationProvider
/*    */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*    */ {
/*    */   private ConversationKey mKey;
/*    */ 
/*    */   public String getTitle(String id)
/*    */   {
/* 21 */     this.mKey = ConversationKey.obtain(id, Conversation.ConversationType.PUBLIC_SERVICE);
/* 22 */     PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(this.mKey.getKey());
/*    */     String name;
/*    */     String name;
/* 24 */     if (info != null)
/* 25 */       name = info.getName();
/*    */     else {
/* 27 */       name = "";
/*    */     }
/* 29 */     return name;
/*    */   }
/*    */ 
/*    */   public Uri getPortraitUri(String id)
/*    */   {
/* 35 */     this.mKey = ConversationKey.obtain(id, Conversation.ConversationType.PUBLIC_SERVICE);
/* 36 */     PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(this.mKey.getKey());
/*    */     Uri uri;
/*    */     Uri uri;
/* 38 */     if (info != null)
/* 39 */       uri = info.getPortraitUri();
/*    */     else {
/* 41 */       uri = null;
/*    */     }
/* 43 */     return uri;
/*    */   }
/*    */ 
/*    */   public void bindView(View view, int position, UIConversation data)
/*    */   {
/* 48 */     super.bindView(view, position, data);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.PublicServiceConversationProvider
 * JD-Core Version:    0.6.0
 */