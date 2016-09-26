/*    */ package io.rong.imkit.cache;
/*    */ 
/*    */ import io.rong.imkit.RongContext;
/*    */ 
/*    */ public abstract class RongCacheWrap<K, V> extends RongCache<K, V>
/*    */ {
/*    */   RongContext mContext;
/* 11 */   boolean mIsSync = false;
/*    */ 
/*    */   public RongCacheWrap(RongContext context, int maxSize)
/*    */   {
/* 19 */     super(maxSize);
/* 20 */     this.mContext = context;
/*    */   }
/*    */ 
/*    */   public boolean isIsSync() {
/* 24 */     return this.mIsSync;
/*    */   }
/*    */ 
/*    */   public void setIsSync(boolean isSync) {
/* 28 */     this.mIsSync = isSync;
/*    */   }
/*    */ 
/*    */   protected V create(K key)
/*    */   {
/* 33 */     if (key == null)
/* 34 */       return null;
/* 35 */     if (!this.mIsSync)
/* 36 */       executeCacheProvider(key);
/*    */     else {
/* 38 */       return obtainValue(key);
/*    */     }
/* 40 */     return super.create(key);
/*    */   }
/*    */ 
/*    */   protected RongContext getContext() {
/* 44 */     return this.mContext;
/*    */   }
/*    */ 
/*    */   public void executeCacheProvider(K key) {
/* 48 */     this.mContext.executorBackground(new Runnable(key)
/*    */     {
/*    */       public void run() {
/* 51 */         RongCacheWrap.this.obtainValue(this.val$key);
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public abstract V obtainValue(K paramK);
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.cache.RongCacheWrap
 * JD-Core Version:    0.6.0
 */