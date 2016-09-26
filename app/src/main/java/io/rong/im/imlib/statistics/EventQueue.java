/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class EventQueue
/*     */ {
/*     */   private final StatisticsStore statisticsStore_;
/*     */ 
/*     */   EventQueue(StatisticsStore statisticsStore)
/*     */   {
/*  48 */     this.statisticsStore_ = statisticsStore;
/*     */   }
/*     */ 
/*     */   int size()
/*     */   {
/*  56 */     return this.statisticsStore_.events().length;
/*     */   }
/*     */ 
/*     */   String events()
/*     */   {
/*  67 */     List events = this.statisticsStore_.eventsList();
/*     */ 
/*  69 */     JSONArray eventArray = new JSONArray();
/*  70 */     for (Event e : events) {
/*  71 */       eventArray.put(e.toJSON());
/*     */     }
/*     */ 
/*  74 */     String result = eventArray.toString();
/*     */ 
/*  76 */     this.statisticsStore_.removeEvents(events);
/*     */     try
/*     */     {
/*  79 */       result = URLEncoder.encode(result, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*     */     }
/*  84 */     return result;
/*     */   }
/*     */ 
/*     */   void recordEvent(String key, Map<String, String> segmentation, int count, double sum)
/*     */   {
/*  97 */     int timestamp = Statistics.currentTimestamp();
/*  98 */     this.statisticsStore_.addEvent(key, segmentation, timestamp, count, sum);
/*     */   }
/*     */ 
/*     */   StatisticsStore getCountlyStore()
/*     */   {
/* 103 */     return this.statisticsStore_;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.EventQueue
 * JD-Core Version:    0.6.0
 */