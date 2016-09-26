/*    */ package io.rong.message;
/*    */ 
/*    */ import android.content.Context;
/*    */ import io.rong.imlib.model.Message;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ public abstract class MessageHandler<T extends MessageContent>
/*    */ {
/*    */   private Context context;
/*    */   protected IHandleMessageListener mHandleMessageListener;
/*    */ 
/*    */   public MessageHandler(Context context)
/*    */   {
/* 13 */     this.context = context;
/*    */   }
/*    */ 
/*    */   public abstract void decodeMessage(Message paramMessage, T paramT);
/*    */ 
/*    */   public abstract void encodeMessage(Message paramMessage);
/*    */ 
/*    */   public Context getContext()
/*    */   {
/* 32 */     return this.context;
/*    */   }
/*    */ 
/*    */   public void setHandleMessageListener(IHandleMessageListener mHandleMessageListener) {
/* 36 */     this.mHandleMessageListener = mHandleMessageListener;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.MessageHandler
 * JD-Core Version:    0.6.0
 */