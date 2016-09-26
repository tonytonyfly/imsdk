/*     */ package io.rong.imkit.mention;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.Color;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.Typeface;
/*     */ import android.graphics.drawable.ColorDrawable;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.MotionEvent;
/*     */ import android.view.View;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.drawable;
/*     */ 
/*     */ public class SideBar extends View
/*     */ {
/*     */   private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
/*  31 */   public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
/*     */ 
/*  36 */   private int choose = -1;
/*     */ 
/*  38 */   private Paint paint = new Paint();
/*     */   private TextView mTextDialog;
/*     */ 
/*     */   public void setTextView(TextView mTextDialog)
/*     */   {
/*  48 */     this.mTextDialog = mTextDialog;
/*     */   }
/*     */ 
/*     */   public SideBar(Context context, AttributeSet attrs, int defStyleAttr)
/*     */   {
/*  53 */     super(context, attrs, defStyleAttr);
/*     */   }
/*     */ 
/*     */   public SideBar(Context context, AttributeSet attrs) {
/*  57 */     super(context, attrs);
/*     */   }
/*     */ 
/*     */   public SideBar(Context context) {
/*  61 */     super(context);
/*     */   }
/*     */ 
/*     */   protected void onDraw(Canvas canvas)
/*     */   {
/*  70 */     super.onDraw(canvas);
/*  71 */     int height = getHeight();
/*  72 */     int width = getWidth();
/*  73 */     int singleHeight = height / b.length;
/*  74 */     for (int i = 0; i < b.length; i++) {
/*  75 */       this.paint.setColor(-7829368);
/*  76 */       this.paint.setTypeface(Typeface.DEFAULT);
/*  77 */       this.paint.setAntiAlias(true);
/*  78 */       this.paint.setTextSize(30.0F);
/*     */ 
/*  80 */       if (i == this.choose) {
/*  81 */         this.paint.setColor(Color.parseColor("#FFFFFF"));
/*  82 */         this.paint.setFakeBoldText(true);
/*     */       }
/*     */ 
/*  85 */       float xPos = width / 2 - this.paint.measureText(b[i]) / 2.0F;
/*  86 */       float yPos = singleHeight * i + singleHeight;
/*  87 */       canvas.drawText(b[i], xPos, yPos, this.paint);
/*  88 */       this.paint.reset();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean dispatchTouchEvent(MotionEvent event)
/*     */   {
/*  95 */     int action = event.getAction();
/*  96 */     float y = event.getY();
/*  97 */     int oldChoose = this.choose;
/*     */ 
/*  99 */     OnTouchingLetterChangedListener listener = this.onTouchingLetterChangedListener;
/*     */ 
/* 101 */     int c = (int)(y / getHeight() * b.length);
/*     */ 
/* 103 */     switch (action) {
/*     */     case 1:
/* 105 */       setBackgroundDrawable(new ColorDrawable(0));
/* 106 */       this.choose = -1;
/* 107 */       invalidate();
/* 108 */       if (this.mTextDialog == null) break;
/* 109 */       this.mTextDialog.setVisibility(4); break;
/*     */     default:
/* 114 */       setBackgroundResource(R.drawable.rc_bg_sidebar);
/* 115 */       if ((oldChoose == c) || 
/* 116 */         (c < 0) || (c >= b.length)) break;
/* 117 */       if (listener != null) {
/* 118 */         listener.onTouchingLetterChanged(b[c]);
/*     */       }
/* 120 */       if (this.mTextDialog != null) {
/* 121 */         this.mTextDialog.setText(b[c]);
/* 122 */         this.mTextDialog.setVisibility(0);
/*     */       }
/* 124 */       this.choose = c;
/* 125 */       invalidate();
/*     */     }
/*     */ 
/* 134 */     return true;
/*     */   }
/*     */ 
/*     */   public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
/*     */   {
/* 143 */     this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
/*     */   }
/*     */ 
/*     */   public static abstract interface OnTouchingLetterChangedListener
/*     */   {
/*     */     public abstract void onTouchingLetterChanged(String paramString);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.mention.SideBar
 * JD-Core Version:    0.6.0
 */