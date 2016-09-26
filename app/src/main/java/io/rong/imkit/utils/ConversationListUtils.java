/*    */ package io.rong.imkit.utils;
/*    */ 
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imkit.widget.adapter.BaseAdapter;
/*    */ 
/*    */ public class ConversationListUtils
/*    */ {
/*    */   public static int findPositionForNewConversation(UIConversation uiconversation, BaseAdapter<UIConversation> mAdapter)
/*    */   {
/*  8 */     int count = mAdapter.getCount();
/*  9 */     int position = 0;
/*    */ 
/* 11 */     for (int i = 0; i < count; i++) {
/* 12 */       if (uiconversation.isTop()) {
/* 13 */         if ((!((UIConversation)mAdapter.getItem(i)).isTop()) || (((UIConversation)mAdapter.getItem(i)).getUIConversationTime() <= uiconversation.getUIConversationTime())) break;
/* 14 */         position++;
/*    */       }
/*    */       else
/*    */       {
/* 18 */         if ((!((UIConversation)mAdapter.getItem(i)).isTop()) && (((UIConversation)mAdapter.getItem(i)).getUIConversationTime() <= uiconversation.getUIConversationTime())) break;
/* 19 */         position++;
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 25 */     return position;
/*    */   }
/*    */ 
/*    */   public static int findPositionForSetTop(UIConversation uiconversation, BaseAdapter<UIConversation> mAdapter) {
/* 29 */     int count = mAdapter.getCount();
/* 30 */     int position = 0;
/*    */ 
/* 32 */     for (int i = 0; (i < count) && 
/* 33 */       (((UIConversation)mAdapter.getItem(i)).isTop()) && (((UIConversation)mAdapter.getItem(i)).getUIConversationTime() > uiconversation.getUIConversationTime()); i++)
/*    */     {
/* 34 */       position++;
/*    */     }
/*    */ 
/* 39 */     return position;
/*    */   }
/*    */ 
/*    */   public static int findPositionForCancleTop(int index, BaseAdapter<UIConversation> mAdapter)
/*    */   {
/* 46 */     int count = mAdapter.getCount();
/* 47 */     int tap = 0;
/*    */ 
/* 49 */     if (index > count) {
/* 50 */       throw new IllegalArgumentException("the index for the position is error!");
/*    */     }
/*    */ 
/* 53 */     for (int i = index + 1; (i < count) && (
/* 54 */       (((UIConversation)mAdapter.getItem(i)).isTop()) || (((UIConversation)mAdapter.getItem(index)).getUIConversationTime() < ((UIConversation)mAdapter.getItem(i)).getUIConversationTime())); i++)
/*    */     {
/* 56 */       tap++;
/*    */     }
/*    */ 
/* 61 */     return index + tap;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.ConversationListUtils
 * JD-Core Version:    0.6.0
 */