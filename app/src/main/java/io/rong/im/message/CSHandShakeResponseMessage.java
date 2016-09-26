/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.CSGroupItem;
/*     */ import io.rong.imlib.model.CustomServiceMode;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CsHsR", flag=0)
/*     */ public class CSHandShakeResponseMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSHandShakeResponseMessage";
/*     */   private int status;
/*     */   private String msg;
/*     */   private String uid;
/*     */   private String sid;
/*     */   private String pid;
/*     */   private String companyName;
/*     */   private String companyIcon;
/*     */   private boolean isBlack;
/*     */   private boolean requiredChangMode;
/*     */   private int mode;
/*     */   private String robotName;
/*     */   private String robotLogo;
/*     */   private String robotHelloWord;
/*     */   private String robotSessionNoEva;
/*  45 */   private ArrayList<CSHumanEvaluateItem> humanEvaluateList = new ArrayList();
/*  46 */   private ArrayList<CSGroupItem> groupList = new ArrayList();
/*     */ 
/* 332 */   public static final Parcelable.Creator<CSHandShakeResponseMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSHandShakeResponseMessage createFromParcel(Parcel source)
/*     */     {
/* 336 */       return new CSHandShakeResponseMessage(source);
/*     */     }
/*     */ 
/*     */     public CSHandShakeResponseMessage[] newArray(int size)
/*     */     {
/* 341 */       return new CSHandShakeResponseMessage[size];
/*     */     }
/* 332 */   };
/*     */ 
/*     */   public CSHandShakeResponseMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSHandShakeResponseMessage(byte[] content)
/*     */   {
/*  53 */     String jsonStr = null;
/*     */     try {
/*  55 */       jsonStr = new String(content, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  57 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  61 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  62 */       JSONObject jsonData = jsonObj.getJSONObject("data");
/*  63 */       this.status = jsonObj.optInt("status");
/*  64 */       this.msg = jsonObj.optString("msg");
/*     */ 
/*  66 */       if (this.status != 0) {
/*  67 */         this.uid = jsonData.optString("uid");
/*  68 */         this.sid = jsonData.optString("sid");
/*  69 */         this.pid = jsonData.optString("pid");
/*  70 */         String serviceType = (String)jsonData.opt("serviceType");
/*  71 */         if (!TextUtils.isEmpty(serviceType))
/*  72 */           this.mode = Integer.parseInt(serviceType);
/*  73 */         String isblack = jsonData.optString("isblack");
/*  74 */         if (!TextUtils.isEmpty(isblack))
/*  75 */           this.isBlack = (Integer.parseInt(isblack) == 1);
/*  76 */         String changeMode = (String)jsonData.opt("notAutoCha");
/*  77 */         if (!TextUtils.isEmpty(changeMode))
/*  78 */           this.requiredChangMode = (Integer.parseInt(changeMode) == 1);
/*  79 */         this.robotName = jsonData.optString("robotName");
/*  80 */         this.robotLogo = jsonData.optString("robotIcon");
/*  81 */         this.robotHelloWord = jsonData.optString("robotWelcome");
/*  82 */         this.companyIcon = jsonData.optString("companyIcon");
/*  83 */         this.companyName = jsonData.optString("companyName");
/*  84 */         this.robotSessionNoEva = jsonData.optString("robotSessionNoEva");
/*  85 */         JSONArray jsonArray = jsonData.optJSONArray("evaluateList");
/*  86 */         this.humanEvaluateList.clear();
/*  87 */         if ((jsonArray != null) && (jsonArray.length() > 0)) {
/*  88 */           for (int i = 0; i < jsonArray.length(); i++) {
/*  89 */             String param1 = jsonArray.optJSONObject(i).optString("value");
/*  90 */             String param2 = jsonArray.optJSONObject(i).optString("description");
/*     */             int value;
/*     */             int value;
/*  92 */             if (TextUtils.isEmpty(param1))
/*  93 */               value = 0;
/*     */             else
/*  95 */               value = Integer.parseInt(param1);
/*  96 */             this.humanEvaluateList.add(new CSHumanEvaluateItem(value, param2));
/*     */           }
/*     */         }
/*     */ 
/* 100 */         JSONArray jsonArrayGroup = jsonData.optJSONArray("groups");
/* 101 */         this.groupList.clear();
/* 102 */         if ((jsonArrayGroup != null) && (jsonArrayGroup.length() > 0))
/* 103 */           for (int i = 0; i < jsonArrayGroup.length(); i++) {
/* 104 */             String param1 = jsonArrayGroup.optJSONObject(i).optString("id");
/* 105 */             String param2 = jsonArrayGroup.optJSONObject(i).optString("name");
/* 106 */             boolean param3 = jsonArrayGroup.optJSONObject(i).optBoolean("online");
/* 107 */             this.groupList.add(new CSGroupItem(param1, param2, param3));
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (JSONException e) {
/* 112 */       RLog.e("CSHandShakeResponseMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CSHandShakeResponseMessage obtain()
/*     */   {
/* 121 */     return new CSHandShakeResponseMessage();
/*     */   }
/*     */ 
/*     */   public CSHandShakeResponseMessage(Parcel in) {
/* 125 */     this.status = ParcelUtils.readIntFromParcel(in).intValue();
/* 126 */     this.msg = ParcelUtils.readFromParcel(in);
/* 127 */     this.sid = ParcelUtils.readFromParcel(in);
/* 128 */     this.uid = ParcelUtils.readFromParcel(in);
/* 129 */     this.pid = ParcelUtils.readFromParcel(in);
/* 130 */     this.mode = ParcelUtils.readIntFromParcel(in).intValue();
/* 131 */     this.companyName = ParcelUtils.readFromParcel(in);
/* 132 */     this.isBlack = (ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 133 */     this.requiredChangMode = (ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 134 */     this.robotName = ParcelUtils.readFromParcel(in);
/* 135 */     this.robotLogo = ParcelUtils.readFromParcel(in);
/* 136 */     this.robotHelloWord = ParcelUtils.readFromParcel(in);
/* 137 */     this.companyIcon = ParcelUtils.readFromParcel(in);
/* 138 */     this.robotSessionNoEva = ParcelUtils.readFromParcel(in);
/* 139 */     this.humanEvaluateList = ParcelUtils.readListFromParcel(in, CSHumanEvaluateItem.class);
/* 140 */     this.groupList = ParcelUtils.readListFromParcel(in, CSGroupItem.class);
/*     */   }
/*     */ 
/*     */   public boolean isRequiredChangMode() {
/* 144 */     return this.requiredChangMode;
/*     */   }
/*     */ 
/*     */   public String getUid()
/*     */   {
/* 157 */     return this.uid;
/*     */   }
/*     */ 
/*     */   public String getSid()
/*     */   {
/* 171 */     return this.sid;
/*     */   }
/*     */ 
/*     */   public String getPid()
/*     */   {
/* 184 */     return this.pid;
/*     */   }
/*     */ 
/*     */   public int getCode()
/*     */   {
/* 192 */     return this.status;
/*     */   }
/*     */ 
/*     */   public String getMsg()
/*     */   {
/* 200 */     return this.msg;
/*     */   }
/*     */ 
/*     */   public boolean isBlack()
/*     */   {
/* 208 */     return this.isBlack;
/*     */   }
/*     */ 
/*     */   public CustomServiceMode getMode()
/*     */   {
/* 217 */     return CustomServiceMode.valueOf(this.mode);
/*     */   }
/*     */ 
/*     */   public String getRobotName()
/*     */   {
/* 225 */     return this.robotName;
/*     */   }
/*     */ 
/*     */   public String getRobotLogo()
/*     */   {
/* 233 */     return this.robotLogo;
/*     */   }
/*     */ 
/*     */   public String getRobotHelloWord()
/*     */   {
/* 241 */     return this.robotHelloWord;
/*     */   }
/*     */ 
/*     */   public String getCompanyName()
/*     */   {
/* 249 */     return this.companyName;
/*     */   }
/*     */ 
/*     */   public String getCompanyIcon()
/*     */   {
/* 257 */     return this.companyIcon;
/*     */   }
/*     */ 
/*     */   public String getRobotSessionNoEva()
/*     */   {
/* 265 */     return this.robotSessionNoEva;
/*     */   }
/*     */ 
/*     */   public ArrayList<CSHumanEvaluateItem> getHumanEvaluateList()
/*     */   {
/* 274 */     return this.humanEvaluateList;
/*     */   }
/*     */ 
/*     */   public ArrayList<CSGroupItem> getGroupList()
/*     */   {
/* 283 */     return this.groupList;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 293 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 304 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.status));
/* 305 */     ParcelUtils.writeToParcel(dest, this.msg);
/* 306 */     ParcelUtils.writeToParcel(dest, this.sid);
/* 307 */     ParcelUtils.writeToParcel(dest, this.uid);
/* 308 */     ParcelUtils.writeToParcel(dest, this.pid);
/* 309 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mode));
/* 310 */     ParcelUtils.writeToParcel(dest, this.companyName);
/* 311 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.isBlack ? 1 : 0));
/* 312 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.requiredChangMode ? 1 : 0));
/* 313 */     ParcelUtils.writeToParcel(dest, this.robotName);
/* 314 */     ParcelUtils.writeToParcel(dest, this.robotLogo);
/* 315 */     ParcelUtils.writeToParcel(dest, this.robotHelloWord);
/* 316 */     ParcelUtils.writeToParcel(dest, this.companyIcon);
/* 317 */     ParcelUtils.writeToParcel(dest, this.robotSessionNoEva);
/* 318 */     ParcelUtils.writeToParcel(dest, this.humanEvaluateList);
/* 319 */     ParcelUtils.writeToParcel(dest, this.groupList);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 329 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSHandShakeResponseMessage
 * JD-Core Version:    0.6.0
 */