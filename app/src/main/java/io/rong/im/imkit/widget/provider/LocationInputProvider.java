/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.res.Resources;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.view.View;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.RongIM.LocationProvider;
/*    */ import io.rong.imkit.RongIM.LocationProvider.LocationCallback;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ import io.rong.imlib.model.Message;
/*    */ import io.rong.message.LocationMessage;
/*    */ 
/*    */ public class LocationInputProvider extends InputProvider.ExtendProvider
/*    */ {
/*    */   private static final String TAG = "LocationInputProvider";
/*    */   Message mCurrentMessage;
/*    */   RongContext mContext;
/*    */ 
/*    */   public LocationInputProvider(RongContext context)
/*    */   {
/* 33 */     super(context);
/* 34 */     this.mContext = context;
/*    */   }
/*    */ 
/*    */   public Drawable obtainPluginDrawable(Context context)
/*    */   {
/* 39 */     return context.getResources().getDrawable(R.drawable.rc_ic_location);
/*    */   }
/*    */ 
/*    */   public CharSequence obtainPluginTitle(Context context)
/*    */   {
/* 44 */     return context.getString(R.string.rc_plugins_location);
/*    */   }
/*    */ 
/*    */   public void onPluginClick(View view)
/*    */   {
/* 49 */     if ((RongContext.getInstance() != null) && (RongContext.getInstance().getLocationProvider() != null))
/* 50 */       RongContext.getInstance().getLocationProvider().onStartLocation(getContext(), new RongIM.LocationProvider.LocationCallback()
/*    */       {
/*    */         public void onSuccess(LocationMessage locationMessage) {
/* 53 */           Message message = Message.obtain(LocationInputProvider.this.mCurrentConversation.getTargetId(), LocationInputProvider.this.mCurrentConversation.getConversationType(), locationMessage);
/* 54 */           RongIM.getInstance().sendLocationMessage(message, null, null, null);
/*    */         }
/*    */ 
/*    */         public void onFailure(String msg)
/*    */         {
/*    */         }
/*    */       });
/*    */   }
/*    */ 
/*    */   public void onDetached()
/*    */   {
/* 66 */     super.onDetached();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.LocationInputProvider
 * JD-Core Version:    0.6.0
 */