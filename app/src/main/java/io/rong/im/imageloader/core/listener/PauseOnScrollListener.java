/*    */ package io.rong.imageloader.core.listener;
/*    */ 
/*    */ import android.widget.AbsListView;
/*    */ import android.widget.AbsListView.OnScrollListener;
/*    */ import io.rong.imageloader.core.ImageLoader;
/*    */ 
/*    */ public class PauseOnScrollListener
/*    */   implements AbsListView.OnScrollListener
/*    */ {
/*    */   private ImageLoader imageLoader;
/*    */   private final boolean pauseOnScroll;
/*    */   private final boolean pauseOnFling;
/*    */   private final AbsListView.OnScrollListener externalListener;
/*    */ 
/*    */   public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling)
/*    */   {
/* 50 */     this(imageLoader, pauseOnScroll, pauseOnFling, null);
/*    */   }
/*    */ 
/*    */   public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, AbsListView.OnScrollListener customListener)
/*    */   {
/* 64 */     this.imageLoader = imageLoader;
/* 65 */     this.pauseOnScroll = pauseOnScroll;
/* 66 */     this.pauseOnFling = pauseOnFling;
/* 67 */     this.externalListener = customListener;
/*    */   }
/*    */ 
/*    */   public void onScrollStateChanged(AbsListView view, int scrollState)
/*    */   {
/* 72 */     switch (scrollState) {
/*    */     case 0:
/* 74 */       this.imageLoader.resume();
/* 75 */       break;
/*    */     case 1:
/* 77 */       if (!this.pauseOnScroll) break;
/* 78 */       this.imageLoader.pause(); break;
/*    */     case 2:
/* 82 */       if (!this.pauseOnFling) break;
/* 83 */       this.imageLoader.pause();
/*    */     }
/*    */ 
/* 87 */     if (this.externalListener != null)
/* 88 */       this.externalListener.onScrollStateChanged(view, scrollState);
/*    */   }
/*    */ 
/*    */   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
/*    */   {
/* 94 */     if (this.externalListener != null)
/* 95 */       this.externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.listener.PauseOnScrollListener
 * JD-Core Version:    0.6.0
 */