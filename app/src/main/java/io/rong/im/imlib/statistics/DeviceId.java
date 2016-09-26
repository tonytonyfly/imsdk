/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.util.Log;
/*     */ 
/*     */ public class DeviceId
/*     */ {
/*     */   private static final String TAG = "DeviceId";
/*     */   private static final String PREFERENCE_KEY_ID_TYPE = "ly.count.android.api.DeviceId.type";
/*     */   private String id;
/*     */   private Type type;
/*     */ 
/*     */   public DeviceId(Type type)
/*     */   {
/*  31 */     if (type == null)
/*  32 */       throw new IllegalStateException("Please specify DeviceId.Type, that is which type of device ID generation you want to use");
/*  33 */     if (type == Type.DEVELOPER_SUPPLIED) {
/*  34 */       throw new IllegalStateException("Please use another DeviceId constructor for device IDs supplied by developer");
/*     */     }
/*  36 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public DeviceId(String developerSuppliedId)
/*     */   {
/*  44 */     if ((developerSuppliedId == null) || ("".equals(developerSuppliedId))) {
/*  45 */       throw new IllegalStateException("Please make sure that device ID is not null or empty");
/*     */     }
/*  47 */     this.type = Type.DEVELOPER_SUPPLIED;
/*  48 */     this.id = developerSuppliedId;
/*     */   }
/*     */ 
/*     */   public void init(Context context, StatisticsStore store, boolean raiseExceptions)
/*     */   {
/*  62 */     Type overriddenType = retrieveOverriddenType(store);
/*     */ 
/*  66 */     if ((overriddenType != null) && (overriddenType != this.type)) {
/*  67 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  68 */         Log.i("DeviceId", "Overridden device ID generation strategy detected: " + overriddenType + ", using it instead of " + this.type);
/*     */       }
/*  70 */       this.type = overriddenType;
/*     */     }
/*     */ 
/*  73 */     switch (1.$SwitchMap$io$rong$imlib$statistics$DeviceId$Type[this.type.ordinal()])
/*     */     {
/*     */     case 1:
/*  76 */       break;
/*     */     case 2:
/*  78 */       if (OpenUDIDAdapter.isOpenUDIDAvailable()) {
/*  79 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  80 */           Log.i("DeviceId", "Using OpenUDID");
/*     */         }
/*  82 */         if (OpenUDIDAdapter.isInitialized()) break;
/*  83 */         OpenUDIDAdapter.sync(context);
/*     */       }
/*     */       else {
/*  86 */         if (!raiseExceptions) break; throw new IllegalStateException("OpenUDID is not available, please make sure that you have it in your classpath");
/*     */       }
/*     */ 
/*     */     case 3:
/*  90 */       if (AdvertisingIdAdapter.isAdvertisingIdAvailable()) {
/*  91 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  92 */           Log.i("DeviceId", "Using Advertising ID");
/*     */         }
/*  94 */         AdvertisingIdAdapter.setAdvertisingId(context, store, this);
/*  95 */       } else if (OpenUDIDAdapter.isOpenUDIDAvailable())
/*     */       {
/*  97 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  98 */           Log.i("DeviceId", "Advertising ID is not available, falling back to OpenUDID");
/*     */         }
/* 100 */         if (OpenUDIDAdapter.isInitialized()) break;
/* 101 */         OpenUDIDAdapter.sync(context);
/*     */       }
/*     */       else
/*     */       {
/* 105 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 106 */           Log.w("DeviceId", "Advertising ID is not available, neither OpenUDID is");
/*     */         }
/* 108 */         if (!raiseExceptions) break; throw new IllegalStateException("OpenUDID is not available, please make sure that you have it in your classpath");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void storeOverriddenType(StatisticsStore store, Type type)
/*     */   {
/* 116 */     store.setPreference("ly.count.android.api.DeviceId.type", type == null ? null : type.toString());
/*     */   }
/*     */ 
/*     */   private Type retrieveOverriddenType(StatisticsStore store)
/*     */   {
/* 121 */     String oldTypeString = store.getPreference("ly.count.android.api.DeviceId.type");
/*     */     Type oldType;
/*     */     Type oldType;
/* 123 */     if (oldTypeString == null) {
/* 124 */       oldType = null;
/*     */     }
/*     */     else
/*     */     {
/*     */       Type oldType;
/* 125 */       if (oldTypeString.equals(Type.DEVELOPER_SUPPLIED.toString())) {
/* 126 */         oldType = Type.DEVELOPER_SUPPLIED;
/*     */       }
/*     */       else
/*     */       {
/*     */         Type oldType;
/* 127 */         if (oldTypeString.equals(Type.OPEN_UDID.toString())) {
/* 128 */           oldType = Type.OPEN_UDID;
/*     */         }
/*     */         else
/*     */         {
/*     */           Type oldType;
/* 129 */           if (oldTypeString.equals(Type.ADVERTISING_ID.toString()))
/* 130 */             oldType = Type.ADVERTISING_ID;
/*     */           else
/* 132 */             oldType = null; 
/*     */         }
/*     */       }
/*     */     }
/* 134 */     return oldType;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 138 */     if ((this.id == null) && (this.type == Type.OPEN_UDID)) {
/* 139 */       this.id = OpenUDIDAdapter.getOpenUDID();
/*     */     }
/* 141 */     return this.id;
/*     */   }
/*     */ 
/*     */   protected void setId(Type type, String id) {
/* 145 */     if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 146 */       Log.w("DeviceId", "Device ID is " + id + " (type " + type + ")");
/*     */     }
/* 148 */     this.type = type;
/* 149 */     this.id = id;
/*     */   }
/*     */ 
/*     */   protected void switchToIdType(Type type, Context context, StatisticsStore store) {
/* 153 */     if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 154 */       Log.w("DeviceId", "Switching to device ID generation strategy " + type + " from " + this.type);
/*     */     }
/* 156 */     this.type = type;
/* 157 */     storeOverriddenType(store, type);
/* 158 */     init(context, store, false);
/*     */   }
/*     */ 
/*     */   public Type getType() {
/* 162 */     return this.type;
/*     */   }
/*     */ 
/*     */   static boolean deviceIDEqualsNullSafe(String id, Type type, DeviceId deviceId)
/*     */   {
/* 170 */     if ((type == null) || (type == Type.DEVELOPER_SUPPLIED)) {
/* 171 */       String deviceIdId = deviceId == null ? null : deviceId.getId();
/* 172 */       return ((deviceIdId == null) && (id == null)) || ((deviceIdId != null) && (deviceIdId.equals(id)));
/*     */     }
/* 174 */     return true;
/*     */   }
/*     */ 
/*     */   public static enum Type
/*     */   {
/*  15 */     DEVELOPER_SUPPLIED, 
/*  16 */     OPEN_UDID, 
/*  17 */     ADVERTISING_ID;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.DeviceId
 * JD-Core Version:    0.6.0
 */