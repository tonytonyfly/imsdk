/*    */ package io.rong.imkit.widget.adapter;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.view.LayoutInflater;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import android.widget.BaseAdapter;
/*    */ import android.widget.ImageView;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.id;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.model.Emoji;
/*    */ import io.rong.imkit.utils.AndroidEmoji;
/*    */ import java.util.List;
/*    */ 
/*    */ public class EmojiAdapter extends BaseAdapter
/*    */ {
/*    */   private static final int EMOJI_PER_PAGE = 20;
/*    */   private Context mContext;
/*    */   private int mStartIndex;
/*    */ 
/*    */   public EmojiAdapter(Context context, int startIndex)
/*    */   {
/* 19 */     this.mContext = context;
/* 20 */     this.mStartIndex = startIndex;
/*    */   }
/*    */ 
/*    */   public int getCount()
/*    */   {
/* 25 */     int count = AndroidEmoji.getEmojiList().size() - this.mStartIndex + 1;
/* 26 */     count = Math.min(count, 21);
/* 27 */     return count;
/*    */   }
/*    */ 
/*    */   public Object getItem(int position)
/*    */   {
/* 32 */     Emoji e = (Emoji)AndroidEmoji.getEmojiList().get(position + this.mStartIndex);
/* 33 */     return Integer.valueOf(e.getRes());
/*    */   }
/*    */ 
/*    */   public long getItemId(int position)
/*    */   {
/* 38 */     return this.mStartIndex + position;
/*    */   }
/*    */ 
/*    */   public View getView(int position, View convertView, ViewGroup parent)
/*    */   {
/* 43 */     if (convertView == null)
/* 44 */       convertView = LayoutInflater.from(this.mContext).inflate(R.layout.rc_emoji_item, null);
/* 45 */     ImageView emoji = (ImageView)convertView.findViewById(R.id.rc_emoji_item);
/* 46 */     int count = AndroidEmoji.getEmojiList().size();
/* 47 */     int index = position + this.mStartIndex;
/* 48 */     if ((position == 20) || (index == count)) {
/* 49 */       emoji.setImageResource(R.drawable.rc_ic_delete);
/* 50 */     } else if (index < count) {
/* 51 */       Emoji e = (Emoji)AndroidEmoji.getEmojiList().get(index);
/* 52 */       emoji.setImageResource(e.getRes());
/*    */     }
/* 54 */     return convertView;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.EmojiAdapter
 * JD-Core Version:    0.6.0
 */