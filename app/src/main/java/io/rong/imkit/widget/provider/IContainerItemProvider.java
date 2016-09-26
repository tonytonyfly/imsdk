/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.net.Uri;
/*    */ import android.os.Parcelable;
/*    */ import android.text.Spannable;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import io.rong.imkit.model.UIMessage;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ public abstract interface IContainerItemProvider<T extends Parcelable>
/*    */ {
/*    */   public abstract View newView(Context paramContext, ViewGroup paramViewGroup);
/*    */ 
/*    */   public abstract void bindView(View paramView, int paramInt, T paramT);
/*    */ 
/*    */   public static abstract interface ConversationProvider<T extends Parcelable> extends IContainerItemProvider<T>
/*    */   {
/*    */     public abstract String getTitle(String paramString);
/*    */ 
/*    */     public abstract Uri getPortraitUri(String paramString);
/*    */   }
/*    */ 
/*    */   public static abstract class MessageProvider<K extends MessageContent>
/*    */     implements IContainerItemProvider<UIMessage>
/*    */   {
/*    */     public final void bindView(View v, int position, UIMessage data)
/*    */     {
/* 49 */       bindView(v, position, data.getContent(), data);
/*    */     }
/*    */ 
/*    */     public abstract void bindView(View paramView, int paramInt, K paramK, UIMessage paramUIMessage);
/*    */ 
/*    */     public final Spannable getSummary(UIMessage data)
/*    */     {
/* 69 */       return getContentSummary(data.getContent());
/*    */     }
/*    */ 
/*    */     public abstract Spannable getContentSummary(K paramK);
/*    */ 
/*    */     public abstract void onItemClick(View paramView, int paramInt, K paramK, UIMessage paramUIMessage);
/*    */ 
/*    */     public abstract void onItemLongClick(View paramView, int paramInt, K paramK, UIMessage paramUIMessage);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.IContainerItemProvider
 * JD-Core Version:    0.6.0
 */