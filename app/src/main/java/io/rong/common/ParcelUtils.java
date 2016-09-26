/*     */ package io.rong.common;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ParcelUtils
/*     */ {
/*     */   public static final int EXIST_SEPARATOR = 1;
/*     */   public static final int NON_SEPARATOR = 0;
/*     */ 
/*     */   public static void writeToParcel(Parcel out, String obj)
/*     */   {
/*  19 */     if (obj != null) {
/*  20 */       out.writeInt(1);
/*  21 */       out.writeString(obj);
/*     */     } else {
/*  23 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Long obj)
/*     */   {
/*  29 */     if (obj != null) {
/*  30 */       out.writeInt(1);
/*  31 */       out.writeLong(obj.longValue());
/*     */     } else {
/*  33 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Integer obj)
/*     */   {
/*  39 */     if (obj != null) {
/*  40 */       out.writeInt(1);
/*  41 */       out.writeInt(obj.intValue());
/*     */     } else {
/*  43 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Float obj)
/*     */   {
/*  49 */     if (obj != null) {
/*  50 */       out.writeInt(1);
/*  51 */       out.writeFloat(obj.floatValue());
/*     */     } else {
/*  53 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Double obj)
/*     */   {
/*  59 */     if (obj != null) {
/*  60 */       out.writeInt(1);
/*  61 */       out.writeDouble(obj.doubleValue());
/*     */     } else {
/*  63 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Map obj)
/*     */   {
/*  69 */     if (obj != null) {
/*  70 */       out.writeInt(1);
/*  71 */       out.writeMap(obj);
/*     */     } else {
/*  73 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Date obj)
/*     */   {
/*  79 */     if (obj != null) {
/*  80 */       out.writeInt(1);
/*  81 */       out.writeLong(obj.getTime());
/*     */     } else {
/*  83 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Float readFloatFromParcel(Parcel in)
/*     */   {
/*  89 */     int flag = in.readInt();
/*  90 */     return flag == 1 ? Float.valueOf(in.readFloat()) : null;
/*     */   }
/*     */ 
/*     */   public static Double readDoubleFromParcel(Parcel in) {
/*  94 */     int flag = in.readInt();
/*  95 */     return flag == 1 ? Double.valueOf(in.readDouble()) : null;
/*     */   }
/*     */ 
/*     */   public static Date readDateFromParcel(Parcel in) {
/*  99 */     int flag = in.readInt();
/* 100 */     return flag == 1 ? new Date(in.readLong()) : null;
/*     */   }
/*     */ 
/*     */   public static Integer readIntFromParcel(Parcel in) {
/* 104 */     int flag = in.readInt();
/* 105 */     return flag == 1 ? Integer.valueOf(in.readInt()) : null;
/*     */   }
/*     */ 
/*     */   public static Long readLongFromParcel(Parcel in) {
/* 109 */     int flag = in.readInt();
/* 110 */     return flag == 1 ? Long.valueOf(in.readLong()) : null;
/*     */   }
/*     */ 
/*     */   public static String readFromParcel(Parcel in) {
/* 114 */     int flag = in.readInt();
/* 115 */     return flag == 1 ? in.readString() : null;
/*     */   }
/*     */ 
/*     */   public static Map readMapFromParcel(Parcel in) {
/* 119 */     int flag = in.readInt();
/* 120 */     return flag == 1 ? in.readHashMap(HashMap.class.getClassLoader()) : null;
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> T readFromParcel(Parcel in, Class<T> cls) {
/* 124 */     int flag = in.readInt();
/* 125 */     Parcelable t = null;
/* 126 */     if (flag == 1)
/* 127 */       t = in.readParcelable(cls.getClassLoader());
/* 128 */     return t;
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> void writeToParcel(Parcel out, T model) {
/* 132 */     if (model != null) {
/* 133 */       out.writeInt(1);
/* 134 */       out.writeParcelable(model, 0);
/*     */     } else {
/* 136 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T extends List<?>> void writeToParcel(Parcel out, T model)
/*     */   {
/* 142 */     if (model != null) {
/* 143 */       out.writeInt(1);
/* 144 */       out.writeList(model);
/*     */     } else {
/* 146 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T> ArrayList<T> readListFromParcel(Parcel in, Class<T> cls)
/*     */   {
/* 152 */     int flag = in.readInt();
/* 153 */     return flag == 1 ? in.readArrayList(cls.getClassLoader()) : null;
/*     */   }
/*     */ 
/*     */   public static void writeListToParcel(Parcel out, List<?> collection) {
/* 157 */     if (collection != null) {
/* 158 */       out.writeInt(1);
/* 159 */       out.writeList(collection);
/*     */     } else {
/* 161 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> T bytesToParcelable(byte[] data, Class<T> cls)
/*     */   {
/* 167 */     if ((data != null) && (data.length != 0)) {
/* 168 */       Parcel in = Parcel.obtain();
/* 169 */       in.unmarshall(data, 0, data.length);
/* 170 */       in.setDataPosition(0);
/* 171 */       Parcelable t = readFromParcel(in, cls);
/* 172 */       in.recycle();
/* 173 */       return t;
/*     */     }
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] parcelableToByte(Parcelable model)
/*     */   {
/* 180 */     if (model == null) {
/* 181 */       return null;
/*     */     }
/* 183 */     Parcel parcel = Parcel.obtain();
/* 184 */     writeToParcel(parcel, model);
/* 185 */     return parcel.marshall();
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> List<T> bytesToParcelableList(byte[] data, Class<T> cls)
/*     */   {
/* 190 */     if ((data != null) && (data.length != 0)) {
/* 191 */       Parcel in = Parcel.obtain();
/* 192 */       in.unmarshall(data, 0, data.length);
/* 193 */       in.setDataPosition(0);
/* 194 */       ArrayList t = readListFromParcel(in, cls);
/* 195 */       in.recycle();
/* 196 */       return t;
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] parcelableListToByte(List<? extends Parcelable> list)
/*     */   {
/* 203 */     if (list == null) {
/* 204 */       return null;
/*     */     }
/* 206 */     Parcel parcel = Parcel.obtain();
/* 207 */     writeListToParcel(parcel, list);
/* 208 */     return parcel.marshall();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.ParcelUtils
 * JD-Core Version:    0.6.0
 */