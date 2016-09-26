/*     */ package io.rong.push.common;
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
/*  20 */     if (obj != null) {
/*  21 */       out.writeInt(1);
/*  22 */       out.writeString(obj);
/*     */     } else {
/*  24 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Long obj)
/*     */   {
/*  30 */     if (obj != null) {
/*  31 */       out.writeInt(1);
/*  32 */       out.writeLong(obj.longValue());
/*     */     } else {
/*  34 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Integer obj)
/*     */   {
/*  40 */     if (obj != null) {
/*  41 */       out.writeInt(1);
/*  42 */       out.writeInt(obj.intValue());
/*     */     } else {
/*  44 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Float obj)
/*     */   {
/*  50 */     if (obj != null) {
/*  51 */       out.writeInt(1);
/*  52 */       out.writeFloat(obj.floatValue());
/*     */     } else {
/*  54 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Double obj)
/*     */   {
/*  60 */     if (obj != null) {
/*  61 */       out.writeInt(1);
/*  62 */       out.writeDouble(obj.doubleValue());
/*     */     } else {
/*  64 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Map obj)
/*     */   {
/*  70 */     if (obj != null) {
/*  71 */       out.writeInt(1);
/*  72 */       out.writeMap(obj);
/*     */     } else {
/*  74 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeToParcel(Parcel out, Date obj)
/*     */   {
/*  80 */     if (obj != null) {
/*  81 */       out.writeInt(1);
/*  82 */       out.writeLong(obj.getTime());
/*     */     } else {
/*  84 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Float readFloatFromParcel(Parcel in)
/*     */   {
/*  90 */     int flag = in.readInt();
/*  91 */     return flag == 1 ? Float.valueOf(in.readFloat()) : null;
/*     */   }
/*     */ 
/*     */   public static Double readDoubleFromParcel(Parcel in) {
/*  95 */     int flag = in.readInt();
/*  96 */     return flag == 1 ? Double.valueOf(in.readDouble()) : null;
/*     */   }
/*     */ 
/*     */   public static Date readDateFromParcel(Parcel in) {
/* 100 */     int flag = in.readInt();
/* 101 */     return flag == 1 ? new Date(in.readLong()) : null;
/*     */   }
/*     */ 
/*     */   public static Integer readIntFromParcel(Parcel in) {
/* 105 */     int flag = in.readInt();
/* 106 */     return flag == 1 ? Integer.valueOf(in.readInt()) : null;
/*     */   }
/*     */ 
/*     */   public static Long readLongFromParcel(Parcel in) {
/* 110 */     int flag = in.readInt();
/* 111 */     return flag == 1 ? Long.valueOf(in.readLong()) : null;
/*     */   }
/*     */ 
/*     */   public static String readFromParcel(Parcel in) {
/* 115 */     int flag = in.readInt();
/* 116 */     return flag == 1 ? in.readString() : null;
/*     */   }
/*     */ 
/*     */   public static Map readMapFromParcel(Parcel in) {
/* 120 */     int flag = in.readInt();
/* 121 */     return flag == 1 ? in.readHashMap(HashMap.class.getClassLoader()) : null;
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> T readFromParcel(Parcel in, Class<T> cls) {
/* 125 */     int flag = in.readInt();
/* 126 */     Parcelable t = null;
/* 127 */     if (flag == 1)
/* 128 */       t = in.readParcelable(cls.getClassLoader());
/* 129 */     return t;
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> void writeToParcel(Parcel out, T model) {
/* 133 */     if (model != null) {
/* 134 */       out.writeInt(1);
/* 135 */       out.writeParcelable(model, 0);
/*     */     } else {
/* 137 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T extends List<?>> void writeToParcel(Parcel out, T model)
/*     */   {
/* 143 */     if (model != null) {
/* 144 */       out.writeInt(1);
/* 145 */       out.writeList(model);
/*     */     } else {
/* 147 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T> ArrayList<T> readListFromParcel(Parcel in, Class<T> cls)
/*     */   {
/* 153 */     int flag = in.readInt();
/* 154 */     return flag == 1 ? in.readArrayList(cls.getClassLoader()) : null;
/*     */   }
/*     */ 
/*     */   public static void writeListToParcel(Parcel out, List<?> collection) {
/* 158 */     if (collection != null) {
/* 159 */       out.writeInt(1);
/* 160 */       out.writeList(collection);
/*     */     } else {
/* 162 */       out.writeInt(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> T bytesToParcelable(byte[] data, Class<T> cls)
/*     */   {
/* 168 */     if ((data != null) && (data.length != 0)) {
/* 169 */       Parcel in = Parcel.obtain();
/* 170 */       in.unmarshall(data, 0, data.length);
/* 171 */       in.setDataPosition(0);
/* 172 */       Parcelable t = readFromParcel(in, cls);
/* 173 */       in.recycle();
/* 174 */       return t;
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] parcelableToByte(Parcelable model)
/*     */   {
/* 181 */     if (model == null) {
/* 182 */       return null;
/*     */     }
/* 184 */     Parcel parcel = Parcel.obtain();
/* 185 */     writeToParcel(parcel, model);
/* 186 */     return parcel.marshall();
/*     */   }
/*     */ 
/*     */   public static <T extends Parcelable> List<T> bytesToParcelableList(byte[] data, Class<T> cls)
/*     */   {
/* 191 */     if ((data != null) && (data.length != 0)) {
/* 192 */       Parcel in = Parcel.obtain();
/* 193 */       in.unmarshall(data, 0, data.length);
/* 194 */       in.setDataPosition(0);
/* 195 */       ArrayList t = readListFromParcel(in, cls);
/* 196 */       in.recycle();
/* 197 */       return t;
/*     */     }
/* 199 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] parcelableListToByte(List<? extends Parcelable> list)
/*     */   {
/* 204 */     if (list == null) {
/* 205 */       return null;
/*     */     }
/* 207 */     Parcel parcel = Parcel.obtain();
/* 208 */     writeListToParcel(parcel, list);
/* 209 */     return parcel.marshall();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.common.ParcelUtils
 * JD-Core Version:    0.6.0
 */