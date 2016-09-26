/*     */ package io.rong.imkit.mention;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.text.Editable;
/*     */ import android.text.TextUtils;
/*     */ import android.widget.EditText;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM.IGroupMembersProvider;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.adapter.BaseAdapter;
/*     */ import io.rong.imkit.widget.provider.TextInputProvider;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class RongMentionManager
/*     */   implements IMemberMentionedListener, ITextInputListener
/*     */ {
/*  23 */   private static String TAG = "RongMentionManager";
/*  24 */   private Stack<MentionInstance> stack = new Stack();
/*     */   private RongIM.IGroupMembersProvider mGroupMembersProvider;
/*     */   private IMentionedInputListener mMentionedInputListener;
/*     */ 
/*     */   public static RongMentionManager getInstance()
/*     */   {
/*  37 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   public void createInstance(Conversation.ConversationType conversationType, String targetId, BaseAdapter adapter, TextInputProvider inputProvider) {
/*  41 */     RLog.i(TAG, "createInstance");
/*  42 */     String key = conversationType.getName() + targetId;
/*     */ 
/*  44 */     if (this.stack.size() > 0) {
/*  45 */       MentionInstance mentionInstance = (MentionInstance)this.stack.peek();
/*  46 */       if (mentionInstance.key.equals(key)) {
/*  47 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  51 */     MentionInstance mentionInstance = new MentionInstance();
/*  52 */     mentionInstance.key = key;
/*  53 */     mentionInstance.mentionBlocks = new ArrayList();
/*  54 */     mentionInstance.mentionInputProvider = inputProvider;
/*  55 */     this.stack.add(mentionInstance);
/*  56 */     adapter.setOnItemClickListener(this);
/*  57 */     inputProvider.setTextInputListener(this);
/*     */   }
/*     */ 
/*     */   public void destroyInstance() {
/*  61 */     RLog.i(TAG, "destroyInstance");
/*  62 */     if (this.stack.size() > 0)
/*  63 */       this.stack.pop();
/*     */     else
/*  65 */       RLog.e(TAG, "destroyInstance error.");
/*     */   }
/*     */ 
/*     */   public void onMemberMentioned(String userId)
/*     */   {
/*  71 */     RLog.d(TAG, "onMemberMentioned " + userId);
/*  72 */     if (TextUtils.isEmpty(userId)) {
/*  73 */       RLog.e(TAG, "onMemberMentioned userId is null");
/*  74 */       return;
/*     */     }
/*  76 */     UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
/*  77 */     addMentionedMember(userInfo, 0);
/*     */   }
/*     */ 
/*     */   public void mentionMember(UserInfo userInfo) {
/*  81 */     if ((userInfo == null) || (TextUtils.isEmpty(userInfo.getUserId()))) {
/*  82 */       RLog.e(TAG, "onMemberMentioned userId is null");
/*  83 */       return;
/*     */     }
/*  85 */     addMentionedMember(userInfo, 1);
/*     */   }
/*     */ 
/*     */   private void addMentionedMember(UserInfo userInfo, int from)
/*     */   {
/*  93 */     if (this.stack.size() > 0) {
/*  94 */       MentionInstance mentionInstance = (MentionInstance)this.stack.peek();
/*  95 */       EditText editText = mentionInstance.mentionInputProvider.getEditText();
/*  96 */       if ((userInfo != null) && (editText != null))
/*     */       {
/*  98 */         String mentionContent = userInfo.getName() + " ";
/*  99 */         int len = mentionContent.length();
/* 100 */         int cursorPos = editText.getSelectionStart();
/*     */ 
/* 102 */         MentionBlock brokenBlock = getBrokenMentionedBlock(cursorPos, mentionInstance.mentionBlocks);
/* 103 */         if (brokenBlock != null) {
/* 104 */           mentionInstance.mentionBlocks.remove(brokenBlock);
/*     */         }
/*     */ 
/* 107 */         MentionBlock mentionBlock = new MentionBlock();
/* 108 */         mentionBlock.userId = userInfo.getUserId();
/* 109 */         mentionBlock.offset = false;
/* 110 */         mentionBlock.name = userInfo.getName();
/* 111 */         if (from == 1)
/* 112 */           mentionBlock.start = (cursorPos - 1);
/*     */         else {
/* 114 */           mentionBlock.start = cursorPos;
/*     */         }
/* 116 */         mentionBlock.end = (cursorPos + len);
/* 117 */         mentionInstance.mentionBlocks.add(mentionBlock);
/*     */ 
/* 119 */         editText.getEditableText().insert(cursorPos, mentionContent);
/* 120 */         editText.setSelection(cursorPos + len);
/* 121 */         mentionBlock.offset = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private MentionBlock getBrokenMentionedBlock(int cursorPos, List<MentionBlock> blocks) {
/* 127 */     MentionBlock brokenBlock = null;
/* 128 */     for (MentionBlock block : blocks) {
/* 129 */       if ((block.offset) && (cursorPos < block.end) && (cursorPos > block.start)) {
/* 130 */         brokenBlock = block;
/* 131 */         break;
/*     */       }
/*     */     }
/* 134 */     return brokenBlock;
/*     */   }
/*     */ 
/*     */   private void offsetMentionedBlocks(int cursorPos, int offset, List<MentionBlock> blocks) {
/* 138 */     for (MentionBlock block : blocks) {
/* 139 */       if ((cursorPos <= block.start) && (block.offset)) {
/* 140 */         block.start += offset;
/* 141 */         block.end += offset;
/*     */       }
/* 143 */       block.offset = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private MentionBlock getDeleteMentionedBlock(int cursorPos, List<MentionBlock> blocks) {
/* 148 */     MentionBlock deleteBlock = null;
/* 149 */     for (MentionBlock block : blocks) {
/* 150 */       if (cursorPos == block.end) {
/* 151 */         deleteBlock = block;
/* 152 */         break;
/*     */       }
/*     */     }
/* 155 */     return deleteBlock;
/*     */   }
/*     */ 
/*     */   public void onTextEdit(Conversation.ConversationType conversationType, String targetId, int cursorPos, int offset, String text)
/*     */   {
/* 169 */     RLog.d(TAG, "onTextEdit " + cursorPos + ", " + text);
/*     */ 
/* 171 */     if ((this.stack == null) || (this.stack.size() == 0)) {
/* 172 */       RLog.w(TAG, "onTextEdit ignore.");
/* 173 */       return;
/*     */     }
/* 175 */     MentionInstance mentionInstance = (MentionInstance)this.stack.peek();
/* 176 */     if (!mentionInstance.key.equals(conversationType.getName() + targetId)) {
/* 177 */       RLog.w(TAG, "onTextEdit ignore conversation.");
/* 178 */       return;
/*     */     }
/*     */ 
/* 181 */     if ((offset == 1) && 
/* 182 */       (!TextUtils.isEmpty(text))) {
/* 183 */       boolean showMention = false;
/*     */ 
/* 185 */       if (cursorPos == 0) {
/* 186 */         String str = text.substring(0, 1);
/* 187 */         showMention = str.equals("@");
/*     */       } else {
/* 189 */         String preChar = text.substring(cursorPos - 1, cursorPos);
/* 190 */         String str = text.substring(cursorPos, cursorPos + 1);
/* 191 */         if ((str.equals("@")) && (!preChar.matches("^[a-zA-Z]*")) && (!preChar.matches("^\\d+$"))) {
/* 192 */           showMention = true;
/*     */         }
/*     */       }
/* 195 */       if ((showMention) && ((this.mMentionedInputListener == null) || (!this.mMentionedInputListener.onMentionedInput(conversationType, targetId))))
/*     */       {
/* 197 */         Intent intent = new Intent(RongContext.getInstance(), MemberMentionedActivity.class);
/* 198 */         intent.putExtra("conversationType", conversationType.getValue());
/* 199 */         intent.putExtra("targetId", targetId);
/* 200 */         intent.setFlags(268435456);
/* 201 */         RongContext.getInstance().startActivity(intent);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 207 */     MentionBlock brokenBlock = getBrokenMentionedBlock(cursorPos, mentionInstance.mentionBlocks);
/* 208 */     if (brokenBlock != null) {
/* 209 */       mentionInstance.mentionBlocks.remove(brokenBlock);
/*     */     }
/*     */ 
/* 212 */     offsetMentionedBlocks(cursorPos, offset, mentionInstance.mentionBlocks);
/*     */   }
/*     */ 
/*     */   public MentionedInfo onSendButtonClick()
/*     */   {
/* 217 */     if (this.stack.size() > 0) {
/* 218 */       List userIds = new ArrayList();
/* 219 */       MentionInstance curInstance = (MentionInstance)this.stack.peek();
/* 220 */       for (MentionBlock block : curInstance.mentionBlocks) {
/* 221 */         if (!userIds.contains(block.userId)) {
/* 222 */           userIds.add(block.userId);
/*     */         }
/*     */       }
/* 225 */       if (userIds.size() > 0) {
/* 226 */         curInstance.mentionBlocks.clear();
/* 227 */         return new MentionedInfo(MentionedInfo.MentionedType.PART, userIds, null);
/*     */       }
/*     */     }
/* 230 */     return null;
/*     */   }
/*     */ 
/*     */   public void onDeleteClick(Conversation.ConversationType type, String targetId, EditText editText, int cursorPos)
/*     */   {
/* 235 */     RLog.d(TAG, "onTextEdit " + cursorPos);
/*     */ 
/* 237 */     if ((this.stack.size() > 0) && (cursorPos > 0)) {
/* 238 */       MentionInstance mentionInstance = (MentionInstance)this.stack.peek();
/* 239 */       if (mentionInstance.key.equals(type.getName() + targetId)) {
/* 240 */         MentionBlock deleteBlock = getDeleteMentionedBlock(cursorPos, mentionInstance.mentionBlocks);
/* 241 */         if (deleteBlock != null) {
/* 242 */           mentionInstance.mentionBlocks.remove(deleteBlock);
/* 243 */           String delText = deleteBlock.name;
/* 244 */           int start = cursorPos - delText.length() - 1;
/* 245 */           editText.getEditableText().delete(start, cursorPos);
/* 246 */           editText.setSelection(start);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setGroupMembersProvider(RongIM.IGroupMembersProvider groupMembersProvider) {
/* 253 */     this.mGroupMembersProvider = groupMembersProvider;
/*     */   }
/*     */ 
/*     */   public RongIM.IGroupMembersProvider getGroupMembersProvider() {
/* 257 */     return this.mGroupMembersProvider;
/*     */   }
/*     */ 
/*     */   public void setMentionedInputListener(IMentionedInputListener listener) {
/* 261 */     this.mMentionedInputListener = listener;
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  29 */     static RongMentionManager sInstance = new RongMentionManager();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.mention.RongMentionManager
 * JD-Core Version:    0.6.0
 */