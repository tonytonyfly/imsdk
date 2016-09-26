/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.util.Log;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ class Event
/*     */ {
/*     */   private static final String SEGMENTATION_KEY = "segmentation";
/*     */   private static final String KEY_KEY = "key";
/*     */   private static final String COUNT_KEY = "count";
/*     */   private static final String SUM_KEY = "sum";
/*     */   private static final String TIMESTAMP_KEY = "timestamp";
/*     */   public String key;
/*     */   public Map<String, String> segmentation;
/*     */   public int count;
/*     */   public double sum;
/*     */   public int timestamp;
/*     */ 
/*     */   JSONObject toJSON()
/*     */   {
/*  57 */     JSONObject json = new JSONObject();
/*     */     try
/*     */     {
/*  60 */       json.put("key", this.key);
/*  61 */       json.put("count", this.count);
/*  62 */       json.put("timestamp", this.timestamp);
/*     */ 
/*  64 */       if (this.segmentation != null) {
/*  65 */         json.put("segmentation", new JSONObject(this.segmentation));
/*     */       }
/*     */ 
/*  71 */       json.put("sum", this.sum);
/*     */     }
/*     */     catch (JSONException e) {
/*  74 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  75 */         Log.w("Statistics", "Got exception converting an Event to JSON", e);
/*     */       }
/*     */     }
/*     */ 
/*  79 */     return json;
/*     */   }
/*     */ 
/*     */   static Event fromJSON(JSONObject json)
/*     */   {
/*  90 */     Event event = new Event();
/*     */     try
/*     */     {
/*  93 */       if (!json.isNull("key")) {
/*  94 */         event.key = json.getString("key");
/*     */       }
/*  96 */       event.count = json.optInt("count");
/*  97 */       event.sum = json.optDouble("sum", 0.0D);
/*  98 */       event.timestamp = json.optInt("timestamp");
/*     */ 
/* 100 */       if (!json.isNull("segmentation")) {
/* 101 */         JSONObject segm = json.getJSONObject("segmentation");
/* 102 */         HashMap segmentation = new HashMap(segm.length());
/* 103 */         Iterator nameItr = segm.keys();
/* 104 */         while (nameItr.hasNext()) {
/* 105 */           String key = (String)nameItr.next();
/* 106 */           if (!segm.isNull(key)) {
/* 107 */             segmentation.put(key, segm.getString(key));
/*     */           }
/*     */         }
/* 110 */         event.segmentation = segmentation;
/*     */       }
/*     */     }
/*     */     catch (JSONException e) {
/* 114 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 115 */         Log.w("Statistics", "Got exception converting JSON to an Event", e);
/*     */       }
/* 117 */       event = null;
/*     */     }
/*     */ 
/* 120 */     return (event != null) && (event.key != null) && (event.key.length() > 0) ? event : null;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 125 */     if ((o == null) || (!(o instanceof Event))) {
/* 126 */       return false;
/*     */     }
/*     */ 
/* 129 */     Event e = (Event)o;
/*     */ 
/* 131 */     return (this.key == null ? e.key == null : this.key.equals(e.key)) && (this.timestamp == e.timestamp) && (this.segmentation == null ? e.segmentation == null : this.segmentation.equals(e.segmentation));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 138 */     return (this.key != null ? this.key.hashCode() : 1) ^ (this.segmentation != null ? this.segmentation.hashCode() : 1) ^ (this.timestamp != 0 ? this.timestamp : 1);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.Event
 * JD-Core Version:    0.6.0
 */