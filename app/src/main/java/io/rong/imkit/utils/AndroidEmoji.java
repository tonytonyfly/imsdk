/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.TypedArray;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.Paint.FontMetricsInt;
/*     */ import android.graphics.Rect;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.text.style.ReplacementSpan;
/*     */ import android.util.DisplayMetrics;
/*     */ import android.util.Log;
/*     */ import io.rong.imkit.R.array;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.model.Emoji;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AndroidEmoji
/*     */ {
/*     */   private static float density;
/*     */   private static Map<Integer, Emoji> sEmojiMap;
/*     */   private static List<Emoji> sEmojiList;
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/*  32 */     sEmojiMap = new HashMap();
/*  33 */     sEmojiList = new ArrayList();
/*     */ 
/*  35 */     int[] codes = context.getResources().getIntArray(R.array.rc_emoji_code);
/*  36 */     TypedArray array = context.getResources().obtainTypedArray(R.array.rc_emoji_res);
/*     */ 
/*  38 */     if (codes.length != array.length()) {
/*  39 */       throw new RuntimeException("Emoji resource init fail.");
/*     */     }
/*     */ 
/*  42 */     int i = -1;
/*     */     while (true) { i++; if (i >= codes.length) break;
/*  44 */       Emoji emoji = new Emoji(codes[i], array.getResourceId(i, -1));
/*     */ 
/*  46 */       sEmojiMap.put(Integer.valueOf(codes[i]), emoji);
/*  47 */       sEmojiList.add(emoji);
/*     */     }
/*     */ 
/*  50 */     DisplayMetrics dm = context.getResources().getDisplayMetrics();
/*  51 */     density = dm.density;
/*     */ 
/*  53 */     Log.d("SystemUtils", "density:" + density);
/*     */   }
/*     */ 
/*     */   public static List<Emoji> getEmojiList()
/*     */   {
/*  58 */     return sEmojiList;
/*     */   }
/*     */ 
/*     */   public static int getEmojiCount(String input)
/*     */   {
/* 150 */     if (input == null) {
/* 151 */       return 0;
/*     */     }
/*     */ 
/* 154 */     int count = 0;
/*     */ 
/* 157 */     char[] chars = input.toCharArray();
/*     */ 
/* 159 */     SpannableStringBuilder ssb = new SpannableStringBuilder(input);
/*     */ 
/* 163 */     for (int i = 0; i < chars.length; i++) {
/* 164 */       if (Character.isHighSurrogate(chars[i]))
/*     */         continue;
/*     */       boolean isSurrogatePair;
/*     */       int codePoint;
/*     */       boolean isSurrogatePair;
/* 166 */       if (Character.isLowSurrogate(chars[i])) {
/* 167 */         if ((i <= 0) || (!Character.isSurrogatePair(chars[(i - 1)], chars[i]))) continue;
/* 168 */         int codePoint = Character.toCodePoint(chars[(i - 1)], chars[i]);
/* 169 */         isSurrogatePair = true;
/*     */       }
/*     */       else
/*     */       {
/* 174 */         codePoint = chars[i];
/* 175 */         isSurrogatePair = false;
/*     */       }
/*     */ 
/* 178 */       if (sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
/* 179 */         count++;
/*     */       }
/*     */     }
/* 182 */     return count;
/*     */   }
/*     */ 
/*     */   public static CharSequence ensure(String input)
/*     */   {
/* 187 */     if (input == null) {
/* 188 */       return input;
/*     */     }
/*     */ 
/* 192 */     char[] chars = input.toCharArray();
/*     */ 
/* 194 */     SpannableStringBuilder ssb = new SpannableStringBuilder(input);
/*     */ 
/* 198 */     for (int i = 0; i < chars.length; i++) {
/* 199 */       if (Character.isHighSurrogate(chars[i]))
/*     */         continue;
/*     */       boolean isSurrogatePair;
/*     */       int codePoint;
/*     */       boolean isSurrogatePair;
/* 201 */       if (Character.isLowSurrogate(chars[i])) {
/* 202 */         if ((i <= 0) || (!Character.isSurrogatePair(chars[(i - 1)], chars[i]))) continue;
/* 203 */         int codePoint = Character.toCodePoint(chars[(i - 1)], chars[i]);
/* 204 */         isSurrogatePair = true;
/*     */       }
/*     */       else
/*     */       {
/* 209 */         codePoint = chars[i];
/* 210 */         isSurrogatePair = false;
/*     */       }
/*     */ 
/* 213 */       if (sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
/* 214 */         ssb.setSpan(new EmojiImageSpan(codePoint, null), isSurrogatePair ? i - 1 : i, i + 1, 34);
/*     */       }
/*     */     }
/*     */ 
/* 218 */     return ssb;
/*     */   }
/*     */ 
/*     */   public static boolean isEmoji(String input)
/*     */   {
/* 224 */     if (input == null) {
/* 225 */       return false;
/*     */     }
/*     */ 
/* 228 */     char[] chars = input.toCharArray();
/*     */ 
/* 230 */     int codePoint = 0;
/* 231 */     int length = chars.length;
/*     */ 
/* 233 */     for (int i = 0; i < length; i++) {
/* 234 */       if (Character.isHighSurrogate(chars[i]))
/*     */         continue;
/* 236 */       if (Character.isLowSurrogate(chars[i])) {
/* 237 */         if ((i <= 0) || (!Character.isSurrogatePair(chars[(i - 1)], chars[i]))) continue;
/* 238 */         codePoint = Character.toCodePoint(chars[(i - 1)], chars[i]);
/*     */       }
/*     */       else
/*     */       {
/* 243 */         codePoint = chars[i];
/*     */       }
/*     */ 
/* 246 */       if (sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
/* 247 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 267 */     return false;
/*     */   }
/*     */ 
/*     */   public static void ensure(Spannable spannable)
/*     */   {
/* 273 */     char[] chars = spannable.toString().toCharArray();
/*     */ 
/* 278 */     for (int i = 0; i < chars.length; i++) {
/* 279 */       if (Character.isHighSurrogate(chars[i]))
/*     */         continue;
/*     */       boolean isSurrogatePair;
/*     */       int codePoint;
/*     */       boolean isSurrogatePair;
/* 281 */       if (Character.isLowSurrogate(chars[i])) {
/* 282 */         if ((i <= 0) || (!Character.isSurrogatePair(chars[(i - 1)], chars[i]))) continue;
/* 283 */         int codePoint = Character.toCodePoint(chars[(i - 1)], chars[i]);
/* 284 */         isSurrogatePair = true;
/*     */       }
/*     */       else
/*     */       {
/* 289 */         codePoint = chars[i];
/* 290 */         isSurrogatePair = false;
/*     */       }
/*     */ 
/* 293 */       if (sEmojiMap.containsKey(Integer.valueOf(codePoint)))
/* 294 */         spannable.setSpan(new EmojiImageSpan(codePoint, null), isSurrogatePair ? i - 1 : i, i + 1, 34);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class EmojiImageSpan extends ReplacementSpan
/*     */   {
/*     */     Drawable mDrawable;
/*     */     private static final String TAG = "DynamicDrawableSpan";
/*     */     public static final int ALIGN_BOTTOM = 0;
/*     */     private WeakReference<Drawable> mDrawableRef;
/*     */ 
/*     */     private EmojiImageSpan(int codePoint)
/*     */     {
/*  68 */       if (AndroidEmoji.sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
/*  69 */         this.mDrawable = RongContext.getInstance().getResources().getDrawable(((Emoji)AndroidEmoji.sEmojiMap.get(Integer.valueOf(codePoint))).getRes());
/*  70 */         int width = this.mDrawable.getIntrinsicWidth() - (int)(4.0F * AndroidEmoji.density);
/*  71 */         int height = this.mDrawable.getIntrinsicHeight() - (int)(4.0F * AndroidEmoji.density);
/*  72 */         this.mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Drawable getDrawable()
/*     */     {
/*  93 */       return this.mDrawable;
/*     */     }
/*     */ 
/*     */     public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm)
/*     */     {
/* 100 */       Drawable d = getCachedDrawable();
/* 101 */       Rect rect = d.getBounds();
/*     */ 
/* 103 */       if (fm != null) {
/* 104 */         fm.ascent = (-rect.bottom);
/* 105 */         fm.descent = 0;
/*     */ 
/* 107 */         fm.top = fm.ascent;
/* 108 */         fm.bottom = 0;
/*     */       }
/*     */ 
/* 111 */       return rect.right;
/*     */     }
/*     */ 
/*     */     public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
/*     */     {
/* 118 */       Drawable b = getCachedDrawable();
/* 119 */       canvas.save();
/*     */ 
/* 121 */       int transY = bottom - b.getBounds().bottom;
/*     */ 
/* 123 */       transY = (int)(transY - AndroidEmoji.density);
/*     */ 
/* 126 */       canvas.translate(x, transY);
/* 127 */       b.draw(canvas);
/* 128 */       canvas.restore();
/*     */     }
/*     */ 
/*     */     private Drawable getCachedDrawable() {
/* 132 */       WeakReference wr = this.mDrawableRef;
/* 133 */       Drawable d = null;
/*     */ 
/* 135 */       if (wr != null) {
/* 136 */         d = (Drawable)wr.get();
/*     */       }
/* 138 */       if (d == null) {
/* 139 */         d = getDrawable();
/* 140 */         this.mDrawableRef = new WeakReference(d);
/*     */       }
/*     */ 
/* 143 */       return d;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.AndroidEmoji
 * JD-Core Version:    0.6.0
 */