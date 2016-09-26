/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.util.Log;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class StatisticsStore
/*     */ {
/*     */   private static final String PREFERENCES = "COUNTLY_STORE";
/*     */   private static final String DELIMITER = ":::";
/*     */   private static final String CONNECTIONS_PREFERENCE = "CONNECTIONS";
/*     */   private static final String LATEST_UPLOAD = "LATEST";
/*     */   private static final String EVENTS_PREFERENCE = "EVENTS";
/*     */   private static final String LOCATION_PREFERENCE = "LOCATION";
/*     */   private static final int UPLOAD_DURATION = 86400;
/*     */   private final SharedPreferences preferences_;
/*     */ 
/*     */   StatisticsStore(Context context)
/*     */   {
/*  70 */     if (context == null) {
/*  71 */       throw new IllegalArgumentException("must provide valid context");
/*     */     }
/*  73 */     this.preferences_ = context.getSharedPreferences("COUNTLY_STORE", 0);
/*     */   }
/*     */ 
/*     */   public String[] connections()
/*     */   {
/*  80 */     String joinedConnStr = this.preferences_.getString("CONNECTIONS", "");
/*  81 */     return joinedConnStr.length() == 0 ? new String[0] : joinedConnStr.split(":::");
/*     */   }
/*     */ 
/*     */   public String[] events()
/*     */   {
/*  88 */     String joinedEventsStr = this.preferences_.getString("EVENTS", "");
/*  89 */     return joinedEventsStr.length() == 0 ? new String[0] : joinedEventsStr.split(":::");
/*     */   }
/*     */ 
/*     */   public List<Event> eventsList()
/*     */   {
/*  96 */     String[] array = events();
/*  97 */     List events = new ArrayList(array.length);
/*  98 */     for (String s : array) {
/*     */       try {
/* 100 */         Event event = Event.fromJSON(new JSONObject(s));
/* 101 */         if (event != null) {
/* 102 */           events.add(event);
/*     */         }
/*     */       }
/*     */       catch (JSONException ignored)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 110 */     Collections.sort(events, new Comparator()
/*     */     {
/*     */       public int compare(Event e1, Event e2) {
/* 113 */         return e1.timestamp - e2.timestamp;
/*     */       }
/*     */     });
/* 116 */     return events;
/*     */   }
/*     */ 
/*     */   public boolean isEmptyConnections()
/*     */   {
/* 123 */     return this.preferences_.getString("CONNECTIONS", "").length() == 0;
/*     */   }
/*     */ 
/*     */   public synchronized void addConnection(String str)
/*     */   {
/* 131 */     if ((str != null) && (str.length() > 0)) {
/* 132 */       List connections = new ArrayList(Arrays.asList(connections()));
/* 133 */       connections.add(str);
/* 134 */       this.preferences_.edit().putString("CONNECTIONS", join(connections, ":::")).commit();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean uploadIfNeed()
/*     */   {
/* 144 */     int current = Statistics.currentTimestamp();
/* 145 */     int time = this.preferences_.getInt("LATEST", 0);
/* 146 */     if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 147 */       Log.w("Statistics", new StringBuilder().append("uploadIfNeed : last = ").append(time).append(", current = ").append(current).toString());
/*     */     }
/* 149 */     if (time == 0) {
/* 150 */       updateLatestUploadTime();
/* 151 */       return true;
/*     */     }
/*     */ 
/* 154 */     time += 86400;
/* 155 */     if (time <= current) {
/* 156 */       updateLatestUploadTime();
/* 157 */       return true;
/*     */     }
/* 159 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateLatestUploadTime()
/*     */   {
/* 167 */     this.preferences_.edit().putInt("LATEST", Statistics.currentTimestamp()).commit();
/*     */   }
/*     */ 
/*     */   public synchronized void removeConnection(String str)
/*     */   {
/* 176 */     if ((str != null) && (str.length() > 0)) {
/* 177 */       List connections = new ArrayList(Arrays.asList(connections()));
/* 178 */       if (connections.remove(str))
/* 179 */         this.preferences_.edit().putString("CONNECTIONS", join(connections, ":::")).commit();
/*     */     }
/*     */   }
/*     */ 
/*     */   void addEvent(Event event)
/*     */   {
/* 189 */     List events = eventsList();
/* 190 */     events.add(event);
/* 191 */     this.preferences_.edit().putString("EVENTS", joinEvents(events, ":::")).commit();
/*     */   }
/*     */ 
/*     */   void setLocation(double lat, double lon)
/*     */   {
/* 198 */     this.preferences_.edit().putString("LOCATION", new StringBuilder().append(lat).append(",").append(lon).toString()).commit();
/*     */   }
/*     */ 
/*     */   String getAndRemoveLocation()
/*     */   {
/* 205 */     String location = this.preferences_.getString("LOCATION", "");
/* 206 */     if (!location.equals("")) {
/* 207 */       this.preferences_.edit().remove("LOCATION").commit();
/*     */     }
/* 209 */     return location;
/*     */   }
/*     */ 
/*     */   public synchronized void addEvent(String key, Map<String, String> segmentation, int timestamp, int count, double sum)
/*     */   {
/* 222 */     Event event = new Event();
/* 223 */     event.key = key;
/* 224 */     event.segmentation = segmentation;
/* 225 */     event.timestamp = timestamp;
/* 226 */     event.count = count;
/* 227 */     event.sum = sum;
/*     */ 
/* 229 */     addEvent(event);
/*     */   }
/*     */ 
/*     */   public synchronized void removeEvents(Collection<Event> eventsToRemove)
/*     */   {
/* 238 */     if ((eventsToRemove != null) && (eventsToRemove.size() > 0)) {
/* 239 */       List events = eventsList();
/* 240 */       if (events.removeAll(eventsToRemove))
/* 241 */         this.preferences_.edit().putString("EVENTS", joinEvents(events, ":::")).commit();
/*     */     }
/*     */   }
/*     */ 
/*     */   static String joinEvents(Collection<Event> collection, String delimiter)
/*     */   {
/* 253 */     List strings = new ArrayList();
/* 254 */     for (Event e : collection) {
/* 255 */       strings.add(e.toJSON().toString());
/*     */     }
/* 257 */     return join(strings, delimiter);
/*     */   }
/*     */ 
/*     */   static String join(Collection<String> collection, String delimiter)
/*     */   {
/* 264 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 266 */     int i = 0;
/* 267 */     for (String s : collection) {
/* 268 */       builder.append(s);
/* 269 */       i++; if (i < collection.size()) {
/* 270 */         builder.append(delimiter);
/*     */       }
/*     */     }
/*     */ 
/* 274 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public synchronized String getPreference(String key)
/*     */   {
/* 282 */     return this.preferences_.getString(key, null);
/*     */   }
/*     */ 
/*     */   public synchronized void setPreference(String key, String value)
/*     */   {
/* 291 */     if (value == null)
/* 292 */       this.preferences_.edit().remove(key).commit();
/*     */     else
/* 294 */       this.preferences_.edit().putString(key, value).commit();
/*     */   }
/*     */ 
/*     */   synchronized void clear()
/*     */   {
/* 300 */     SharedPreferences.Editor prefsEditor = this.preferences_.edit();
/* 301 */     prefsEditor.remove("EVENTS");
/* 302 */     prefsEditor.remove("CONNECTIONS");
/* 303 */     prefsEditor.commit();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.StatisticsStore
 * JD-Core Version:    0.6.0
 */