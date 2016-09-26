/*      */ package io.rong.push.common.stateMachine;
/*      */ 
/*      */ import android.os.Handler;
/*      */ import android.os.HandlerThread;
/*      */ import android.os.Looper;
/*      */ import android.os.Message;
/*      */ import android.text.TextUtils;
/*      */ import android.util.Log;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class StateMachine
/*      */ {
/*      */   private String mName;
/*      */   private static final int SM_QUIT_CMD = -1;
/*      */   private static final int SM_INIT_CMD = -2;
/*      */   public static final boolean HANDLED = true;
/*      */   public static final boolean NOT_HANDLED = false;
/*      */   private SmHandler mSmHandler;
/*      */   private HandlerThread mSmThread;
/*      */ 
/*      */   private void initStateMachine(String name, Looper looper)
/*      */   {
/* 1239 */     this.mName = name;
/* 1240 */     this.mSmHandler = new SmHandler(looper, this, null);
/*      */   }
/*      */ 
/*      */   protected StateMachine(String name)
/*      */   {
/* 1249 */     this.mSmThread = new HandlerThread(name);
/* 1250 */     this.mSmThread.start();
/* 1251 */     Looper looper = this.mSmThread.getLooper();
/*      */ 
/* 1253 */     initStateMachine(name, looper);
/*      */   }
/*      */ 
/*      */   protected StateMachine(String name, Looper looper)
/*      */   {
/* 1262 */     initStateMachine(name, looper);
/*      */   }
/*      */ 
/*      */   protected StateMachine(String name, Handler handler)
/*      */   {
/* 1271 */     initStateMachine(name, handler.getLooper());
/*      */   }
/*      */ 
/*      */   protected final void addState(State state, State parent)
/*      */   {
/* 1280 */     this.mSmHandler.addState(state, parent);
/*      */   }
/*      */ 
/*      */   protected final void addState(State state)
/*      */   {
/* 1288 */     this.mSmHandler.addState(state, null);
/*      */   }
/*      */ 
/*      */   protected final void setInitialState(State initialState)
/*      */   {
/* 1298 */     this.mSmHandler.setInitialState(initialState);
/*      */   }
/*      */ 
/*      */   protected final Message getCurrentMessage()
/*      */   {
/* 1306 */     SmHandler smh = this.mSmHandler;
/* 1307 */     if (smh == null) return null;
/* 1308 */     return smh.getCurrentMessage();
/*      */   }
/*      */ 
/*      */   protected final IState getCurrentState()
/*      */   {
/* 1316 */     SmHandler smh = this.mSmHandler;
/* 1317 */     if (smh == null) return null;
/* 1318 */     return smh.getCurrentState();
/*      */   }
/*      */ 
/*      */   protected final void transitionTo(IState destState)
/*      */   {
/* 1336 */     this.mSmHandler.transitionTo(destState);
/*      */   }
/*      */ 
/*      */   protected final void transitionToHaltingState()
/*      */   {
/* 1347 */     this.mSmHandler.transitionTo(this.mSmHandler.mHaltingState);
/*      */   }
/*      */ 
/*      */   protected final void deferMessage(Message msg)
/*      */   {
/* 1360 */     this.mSmHandler.deferMessage(msg);
/*      */   }
/*      */ 
/*      */   protected void unhandledMessage(Message msg)
/*      */   {
/* 1369 */     if (this.mSmHandler.mDbg) loge(" - unhandledMessage: msg.what=" + msg.what);
/*      */   }
/*      */ 
/*      */   protected void haltedProcessMessage(Message msg)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void onHalting()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void onQuitting()
/*      */   {
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/* 1400 */     return this.mName;
/*      */   }
/*      */ 
/*      */   public final void setLogRecSize(int maxSize)
/*      */   {
/* 1409 */     this.mSmHandler.mLogRecords.setSize(maxSize);
/*      */   }
/*      */ 
/*      */   public final void setLogOnlyTransitions(boolean enable)
/*      */   {
/* 1418 */     this.mSmHandler.mLogRecords.setLogOnlyTransitions(enable);
/*      */   }
/*      */ 
/*      */   public final int getLogRecSize()
/*      */   {
/* 1426 */     SmHandler smh = this.mSmHandler;
/* 1427 */     if (smh == null) return 0;
/* 1428 */     return smh.mLogRecords.size();
/*      */   }
/*      */ 
/*      */   public final int getLogRecCount()
/*      */   {
/* 1436 */     SmHandler smh = this.mSmHandler;
/* 1437 */     if (smh == null) return 0;
/* 1438 */     return smh.mLogRecords.count();
/*      */   }
/*      */ 
/*      */   public final LogRec getLogRec(int index)
/*      */   {
/* 1446 */     SmHandler smh = this.mSmHandler;
/* 1447 */     if (smh == null) return null;
/* 1448 */     return smh.mLogRecords.get(index);
/*      */   }
/*      */ 
/*      */   public final Collection<LogRec> copyLogRecs()
/*      */   {
/* 1455 */     Vector vlr = new Vector();
/* 1456 */     SmHandler smh = this.mSmHandler;
/* 1457 */     if (smh != null) {
/* 1458 */       for (LogRec lr : SmHandler.access$1600(smh).mLogRecVector) {
/* 1459 */         vlr.add(lr);
/*      */       }
/*      */     }
/* 1462 */     return vlr;
/*      */   }
/*      */ 
/*      */   protected void addLogRec(String string)
/*      */   {
/* 1472 */     SmHandler smh = this.mSmHandler;
/* 1473 */     if (smh == null) return;
/* 1474 */     smh.mLogRecords.add(this, smh.getCurrentMessage(), string, smh.getCurrentState(), smh.mStateStack[smh.mStateStackTopIndex].state, smh.mDestState);
/*      */   }
/*      */ 
/*      */   protected boolean recordLogRec(Message msg)
/*      */   {
/* 1482 */     return true;
/*      */   }
/*      */ 
/*      */   protected String getLogRecString(Message msg)
/*      */   {
/* 1493 */     return "";
/*      */   }
/*      */ 
/*      */   protected String getWhatToString(int what)
/*      */   {
/* 1500 */     return null;
/*      */   }
/*      */ 
/*      */   public final Handler getHandler()
/*      */   {
/* 1507 */     return this.mSmHandler;
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage()
/*      */   {
/* 1521 */     return Message.obtain(this.mSmHandler);
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage(int what)
/*      */   {
/* 1536 */     return Message.obtain(this.mSmHandler, what);
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage(int what, Object obj)
/*      */   {
/* 1553 */     return Message.obtain(this.mSmHandler, what, obj);
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage(int what, int arg1)
/*      */   {
/* 1571 */     return Message.obtain(this.mSmHandler, what, arg1, 0);
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage(int what, int arg1, int arg2)
/*      */   {
/* 1589 */     return Message.obtain(this.mSmHandler, what, arg1, arg2);
/*      */   }
/*      */ 
/*      */   public final Message obtainMessage(int what, int arg1, int arg2, Object obj)
/*      */   {
/* 1608 */     return Message.obtain(this.mSmHandler, what, arg1, arg2, obj);
/*      */   }
/*      */ 
/*      */   public final void sendMessage(int what)
/*      */   {
/* 1618 */     SmHandler smh = this.mSmHandler;
/* 1619 */     if (smh == null) return;
/*      */ 
/* 1621 */     smh.sendMessage(obtainMessage(what));
/*      */   }
/*      */ 
/*      */   public final void sendMessage(int what, Object obj)
/*      */   {
/* 1631 */     SmHandler smh = this.mSmHandler;
/* 1632 */     if (smh == null) return;
/*      */ 
/* 1634 */     smh.sendMessage(obtainMessage(what, obj));
/*      */   }
/*      */ 
/*      */   public final void sendMessage(int what, int arg1)
/*      */   {
/* 1644 */     SmHandler smh = this.mSmHandler;
/* 1645 */     if (smh == null) return;
/*      */ 
/* 1647 */     smh.sendMessage(obtainMessage(what, arg1));
/*      */   }
/*      */ 
/*      */   public final void sendMessage(int what, int arg1, int arg2)
/*      */   {
/* 1657 */     SmHandler smh = this.mSmHandler;
/* 1658 */     if (smh == null) return;
/*      */ 
/* 1660 */     smh.sendMessage(obtainMessage(what, arg1, arg2));
/*      */   }
/*      */ 
/*      */   public final void sendMessage(int what, int arg1, int arg2, Object obj)
/*      */   {
/* 1670 */     SmHandler smh = this.mSmHandler;
/* 1671 */     if (smh == null) return;
/*      */ 
/* 1673 */     smh.sendMessage(obtainMessage(what, arg1, arg2, obj));
/*      */   }
/*      */ 
/*      */   public final void sendMessage(Message msg)
/*      */   {
/* 1683 */     SmHandler smh = this.mSmHandler;
/* 1684 */     if (smh == null) return;
/*      */ 
/* 1686 */     smh.sendMessage(msg);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(int what, long delayMillis)
/*      */   {
/* 1696 */     SmHandler smh = this.mSmHandler;
/* 1697 */     if (smh == null) return;
/*      */ 
/* 1699 */     smh.sendMessageDelayed(obtainMessage(what), delayMillis);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(int what, Object obj, long delayMillis)
/*      */   {
/* 1709 */     SmHandler smh = this.mSmHandler;
/* 1710 */     if (smh == null) return;
/*      */ 
/* 1712 */     smh.sendMessageDelayed(obtainMessage(what, obj), delayMillis);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(int what, int arg1, long delayMillis)
/*      */   {
/* 1722 */     SmHandler smh = this.mSmHandler;
/* 1723 */     if (smh == null) return;
/*      */ 
/* 1725 */     smh.sendMessageDelayed(obtainMessage(what, arg1), delayMillis);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(int what, int arg1, int arg2, long delayMillis)
/*      */   {
/* 1735 */     SmHandler smh = this.mSmHandler;
/* 1736 */     if (smh == null) return;
/*      */ 
/* 1738 */     smh.sendMessageDelayed(obtainMessage(what, arg1, arg2), delayMillis);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(int what, int arg1, int arg2, Object obj, long delayMillis)
/*      */   {
/* 1749 */     SmHandler smh = this.mSmHandler;
/* 1750 */     if (smh == null) return;
/*      */ 
/* 1752 */     smh.sendMessageDelayed(obtainMessage(what, arg1, arg2, obj), delayMillis);
/*      */   }
/*      */ 
/*      */   public final void sendMessageDelayed(Message msg, long delayMillis)
/*      */   {
/* 1762 */     SmHandler smh = this.mSmHandler;
/* 1763 */     if (smh == null) return;
/*      */ 
/* 1765 */     smh.sendMessageDelayed(msg, delayMillis);
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(int what)
/*      */   {
/* 1776 */     SmHandler smh = this.mSmHandler;
/* 1777 */     if (smh == null) return;
/*      */ 
/* 1779 */     smh.sendMessageAtFrontOfQueue(obtainMessage(what));
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(int what, Object obj)
/*      */   {
/* 1790 */     SmHandler smh = this.mSmHandler;
/* 1791 */     if (smh == null) return;
/*      */ 
/* 1793 */     smh.sendMessageAtFrontOfQueue(obtainMessage(what, obj));
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(int what, int arg1)
/*      */   {
/* 1804 */     SmHandler smh = this.mSmHandler;
/* 1805 */     if (smh == null) return;
/*      */ 
/* 1807 */     smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1));
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2)
/*      */   {
/* 1819 */     SmHandler smh = this.mSmHandler;
/* 1820 */     if (smh == null) return;
/*      */ 
/* 1822 */     smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2));
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2, Object obj)
/*      */   {
/* 1833 */     SmHandler smh = this.mSmHandler;
/* 1834 */     if (smh == null) return;
/*      */ 
/* 1836 */     smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2, obj));
/*      */   }
/*      */ 
/*      */   protected final void sendMessageAtFrontOfQueue(Message msg)
/*      */   {
/* 1847 */     SmHandler smh = this.mSmHandler;
/* 1848 */     if (smh == null) return;
/*      */ 
/* 1850 */     smh.sendMessageAtFrontOfQueue(msg);
/*      */   }
/*      */ 
/*      */   protected final void removeMessages(int what)
/*      */   {
/* 1859 */     SmHandler smh = this.mSmHandler;
/* 1860 */     if (smh == null) return;
/*      */ 
/* 1862 */     smh.removeMessages(what);
/*      */   }
/*      */ 
/*      */   protected final boolean isQuit(Message msg)
/*      */   {
/* 1871 */     SmHandler smh = this.mSmHandler;
/* 1872 */     if (smh == null) return msg.what == -1;
/*      */ 
/* 1874 */     return smh.isQuit(msg);
/*      */   }
/*      */ 
/*      */   protected final void quit()
/*      */   {
/* 1882 */     SmHandler smh = this.mSmHandler;
/* 1883 */     if (smh == null) return;
/*      */ 
/* 1885 */     smh.quit();
/*      */   }
/*      */ 
/*      */   protected final void quitNow()
/*      */   {
/* 1893 */     SmHandler smh = this.mSmHandler;
/* 1894 */     if (smh == null) return;
/*      */ 
/* 1896 */     smh.quitNow();
/*      */   }
/*      */ 
/*      */   public boolean isDbg()
/*      */   {
/* 1904 */     SmHandler smh = this.mSmHandler;
/* 1905 */     if (smh == null) return false;
/*      */ 
/* 1907 */     return smh.isDbg();
/*      */   }
/*      */ 
/*      */   public void setDbg(boolean dbg)
/*      */   {
/* 1917 */     SmHandler smh = this.mSmHandler;
/* 1918 */     if (smh == null) return;
/*      */ 
/* 1920 */     smh.setDbg(dbg);
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/* 1928 */     SmHandler smh = this.mSmHandler;
/* 1929 */     if (smh == null) return;
/*      */ 
/* 1932 */     smh.completeConstruction();
/*      */   }
/*      */ 
/*      */   public void dump(FileDescriptor fd, PrintWriter pw, String[] args)
/*      */   {
/* 1943 */     pw.println(getName() + ":");
/* 1944 */     pw.println(" total records=" + getLogRecCount());
/* 1945 */     for (int i = 0; i < getLogRecSize(); i++) {
/* 1946 */       pw.printf(" rec[%d]: %s\n", new Object[] { Integer.valueOf(i), getLogRec(i).toString() });
/* 1947 */       pw.flush();
/*      */     }
/* 1949 */     pw.println("curState=" + getCurrentState().getName());
/*      */   }
/*      */ 
/*      */   protected void logAndAddLogRec(String s)
/*      */   {
/* 1958 */     addLogRec(s);
/* 1959 */     log(s);
/*      */   }
/*      */ 
/*      */   protected void log(String s)
/*      */   {
/* 1968 */     Log.d(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void logd(String s)
/*      */   {
/* 1977 */     Log.d(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void logv(String s)
/*      */   {
/* 1986 */     Log.v(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void logi(String s)
/*      */   {
/* 1995 */     Log.i(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void logw(String s)
/*      */   {
/* 2004 */     Log.w(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void loge(String s)
/*      */   {
/* 2013 */     Log.e(this.mName, s);
/*      */   }
/*      */ 
/*      */   protected void loge(String s, Throwable e)
/*      */   {
/* 2023 */     Log.e(this.mName, s, e);
/*      */   }
/*      */ 
/*      */   private static class SmHandler extends Handler
/*      */   {
/*  676 */     private boolean mHasQuit = false;
/*      */ 
/*  679 */     private boolean mDbg = false;
/*      */ 
/*  682 */     private static final Object mSmHandlerObj = new Object();
/*      */     private Message mMsg;
/*  688 */     private StateMachine.LogRecords mLogRecords = new StateMachine.LogRecords(null);
/*      */     private boolean mIsConstructionCompleted;
/*      */     private StateInfo[] mStateStack;
/*  697 */     private int mStateStackTopIndex = -1;
/*      */     private StateInfo[] mTempStateStack;
/*      */     private int mTempStateStackCount;
/*  706 */     private HaltingState mHaltingState = new HaltingState(null);
/*      */ 
/*  709 */     private QuittingState mQuittingState = new QuittingState(null);
/*      */     private StateMachine mSm;
/*  739 */     private HashMap<State, StateInfo> mStateInfo = new HashMap();
/*      */     private State mInitialState;
/*      */     private State mDestState;
/*  748 */     private ArrayList<Message> mDeferredMessages = new ArrayList();
/*      */ 
/*      */     public final void handleMessage(Message msg)
/*      */     {
/*  779 */       if (!this.mHasQuit) {
/*  780 */         if (this.mDbg) this.mSm.log(new StringBuilder().append("handleMessage: E msg.what=").append(msg.what).toString());
/*      */ 
/*  783 */         this.mMsg = msg;
/*      */ 
/*  786 */         State msgProcessedState = null;
/*  787 */         if (this.mIsConstructionCompleted)
/*      */         {
/*  789 */           msgProcessedState = processMsg(msg);
/*  790 */         } else if ((!this.mIsConstructionCompleted) && (this.mMsg.what == -2) && (this.mMsg.obj == mSmHandlerObj))
/*      */         {
/*  793 */           this.mIsConstructionCompleted = true;
/*  794 */           invokeEnterMethods(0);
/*      */         } else {
/*  796 */           throw new RuntimeException(new StringBuilder().append("StateMachine.handleMessage: The start method not called, received msg: ").append(msg).toString());
/*      */         }
/*      */ 
/*  799 */         performTransitions(msgProcessedState, msg);
/*      */ 
/*  802 */         if ((this.mDbg) && (this.mSm != null)) this.mSm.log("handleMessage: X");
/*      */       }
/*      */     }
/*      */ 
/*      */     private void performTransitions(State msgProcessedState, Message msg)
/*      */     {
/*  816 */       State orgState = this.mStateStack[this.mStateStackTopIndex].state;
/*      */ 
/*  823 */       boolean recordLogMsg = (this.mSm.recordLogRec(this.mMsg)) && (msg.obj != mSmHandlerObj);
/*      */ 
/*  825 */       if (this.mLogRecords.logOnlyTransitions())
/*      */       {
/*  827 */         if (this.mDestState != null) {
/*  828 */           this.mLogRecords.add(this.mSm, this.mMsg, this.mSm.getLogRecString(this.mMsg), msgProcessedState, orgState, this.mDestState);
/*      */         }
/*      */       }
/*  831 */       else if (recordLogMsg)
/*      */       {
/*  833 */         this.mLogRecords.add(this.mSm, this.mMsg, this.mSm.getLogRecString(this.mMsg), msgProcessedState, orgState, this.mDestState);
/*      */       }
/*      */ 
/*  837 */       State destState = this.mDestState;
/*  838 */       if (destState != null)
/*      */       {
/*      */         while (true)
/*      */         {
/*  843 */           if (this.mDbg) this.mSm.log("handleMessage: new destination call exit/enter");
/*      */ 
/*  850 */           StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
/*  851 */           invokeExitMethods(commonStateInfo);
/*  852 */           int stateStackEnteringIndex = moveTempStateStackToStateStack();
/*  853 */           invokeEnterMethods(stateStackEnteringIndex);
/*      */ 
/*  861 */           moveDeferredMessageAtFrontOfQueue();
/*      */ 
/*  863 */           if (destState == this.mDestState)
/*      */             break;
/*  865 */           destState = this.mDestState;
/*      */         }
/*      */ 
/*  871 */         this.mDestState = null;
/*      */       }
/*      */ 
/*  878 */       if (destState != null)
/*  879 */         if (destState == this.mQuittingState)
/*      */         {
/*  883 */           this.mSm.onQuitting();
/*  884 */           cleanupAfterQuitting();
/*  885 */         } else if (destState == this.mHaltingState)
/*      */         {
/*  891 */           this.mSm.onHalting();
/*      */         }
/*      */     }
/*      */ 
/*      */     private final void cleanupAfterQuitting()
/*      */     {
/*  900 */       if (this.mSm.mSmThread != null)
/*      */       {
/*  902 */         getLooper().quit();
/*  903 */         StateMachine.access$402(this.mSm, null);
/*      */       }
/*      */ 
/*  906 */       StateMachine.access$502(this.mSm, null);
/*  907 */       this.mSm = null;
/*  908 */       this.mMsg = null;
/*  909 */       this.mLogRecords.cleanup();
/*  910 */       this.mStateStack = null;
/*  911 */       this.mTempStateStack = null;
/*  912 */       this.mStateInfo.clear();
/*  913 */       this.mInitialState = null;
/*  914 */       this.mDestState = null;
/*  915 */       this.mDeferredMessages.clear();
/*  916 */       this.mHasQuit = true;
/*      */     }
/*      */ 
/*      */     private final void completeConstruction()
/*      */     {
/*  923 */       if (this.mDbg) this.mSm.log("completeConstruction: E");
/*      */ 
/*  929 */       int maxDepth = 0;
/*  930 */       for (StateInfo si : this.mStateInfo.values()) {
/*  931 */         int depth = 0;
/*  932 */         for (StateInfo i = si; i != null; depth++) {
/*  933 */           i = i.parentStateInfo;
/*      */         }
/*  935 */         if (maxDepth < depth) {
/*  936 */           maxDepth = depth;
/*      */         }
/*      */       }
/*  939 */       if (this.mDbg) this.mSm.log(new StringBuilder().append("completeConstruction: maxDepth=").append(maxDepth).toString());
/*      */ 
/*  941 */       this.mStateStack = new StateInfo[maxDepth];
/*  942 */       this.mTempStateStack = new StateInfo[maxDepth];
/*  943 */       setupInitialStateStack();
/*      */ 
/*  946 */       sendMessageAtFrontOfQueue(obtainMessage(-2, mSmHandlerObj));
/*      */ 
/*  948 */       if (this.mDbg) this.mSm.log("completeConstruction: X");
/*      */     }
/*      */ 
/*      */     private final State processMsg(Message msg)
/*      */     {
/*  958 */       StateInfo curStateInfo = this.mStateStack[this.mStateStackTopIndex];
/*  959 */       if (this.mDbg) {
/*  960 */         this.mSm.log(new StringBuilder().append("processMsg: ").append(curStateInfo.state.getName()).toString());
/*      */       }
/*      */ 
/*  963 */       if (isQuit(msg))
/*  964 */         transitionTo(this.mQuittingState);
/*      */       else {
/*  966 */         while (!curStateInfo.state.processMessage(msg))
/*      */         {
/*  970 */           curStateInfo = curStateInfo.parentStateInfo;
/*  971 */           if (curStateInfo == null)
/*      */           {
/*  975 */             this.mSm.unhandledMessage(msg);
/*  976 */             break;
/*      */           }
/*  978 */           if (this.mDbg) {
/*  979 */             this.mSm.log(new StringBuilder().append("processMsg: ").append(curStateInfo.state.getName()).toString());
/*      */           }
/*      */         }
/*      */       }
/*  983 */       return curStateInfo != null ? curStateInfo.state : null;
/*      */     }
/*      */ 
/*      */     private final void invokeExitMethods(StateInfo commonStateInfo)
/*      */     {
/*  992 */       while ((this.mStateStackTopIndex >= 0) && (this.mStateStack[this.mStateStackTopIndex] != commonStateInfo)) {
/*  993 */         State curState = this.mStateStack[this.mStateStackTopIndex].state;
/*  994 */         if (this.mDbg) this.mSm.log(new StringBuilder().append("invokeExitMethods: ").append(curState.getName()).toString());
/*  995 */         curState.exit();
/*  996 */         this.mStateStack[this.mStateStackTopIndex].active = false;
/*  997 */         this.mStateStackTopIndex -= 1;
/*      */       }
/*      */     }
/*      */ 
/*      */     private final void invokeEnterMethods(int stateStackEnteringIndex)
/*      */     {
/* 1005 */       for (int i = stateStackEnteringIndex; i <= this.mStateStackTopIndex; i++) {
/* 1006 */         if (this.mDbg) this.mSm.log(new StringBuilder().append("invokeEnterMethods: ").append(this.mStateStack[i].state.getName()).toString());
/* 1007 */         this.mStateStack[i].state.enter();
/* 1008 */         this.mStateStack[i].active = true;
/*      */       }
/*      */     }
/*      */ 
/*      */     private final void moveDeferredMessageAtFrontOfQueue()
/*      */     {
/* 1022 */       for (int i = this.mDeferredMessages.size() - 1; i >= 0; i--) {
/* 1023 */         Message curMsg = (Message)this.mDeferredMessages.get(i);
/* 1024 */         if (this.mDbg) this.mSm.log(new StringBuilder().append("moveDeferredMessageAtFrontOfQueue; what=").append(curMsg.what).toString());
/* 1025 */         sendMessageAtFrontOfQueue(curMsg);
/*      */       }
/* 1027 */       this.mDeferredMessages.clear();
/*      */     }
/*      */ 
/*      */     private final int moveTempStateStackToStateStack()
/*      */     {
/* 1038 */       int startingIndex = this.mStateStackTopIndex + 1;
/* 1039 */       int i = this.mTempStateStackCount - 1;
/* 1040 */       int j = startingIndex;
/* 1041 */       while (i >= 0) {
/* 1042 */         if (this.mDbg) this.mSm.log(new StringBuilder().append("moveTempStackToStateStack: i=").append(i).append(",j=").append(j).toString());
/* 1043 */         this.mStateStack[j] = this.mTempStateStack[i];
/* 1044 */         j++;
/* 1045 */         i--;
/*      */       }
/*      */ 
/* 1048 */       this.mStateStackTopIndex = (j - 1);
/* 1049 */       if (this.mDbg) {
/* 1050 */         this.mSm.log(new StringBuilder().append("moveTempStackToStateStack: X mStateStackTop=").append(this.mStateStackTopIndex).append(",startingIndex=").append(startingIndex).append(",Top=").append(this.mStateStack[this.mStateStackTopIndex].state.getName()).toString());
/*      */       }
/*      */ 
/* 1054 */       return startingIndex;
/*      */     }
/*      */ 
/*      */     private final StateInfo setupTempStateStackWithStatesToEnter(State destState)
/*      */     {
/* 1075 */       this.mTempStateStackCount = 0;
/* 1076 */       StateInfo curStateInfo = (StateInfo)this.mStateInfo.get(destState);
/*      */       do {
/* 1078 */         this.mTempStateStack[(this.mTempStateStackCount++)] = curStateInfo;
/* 1079 */         curStateInfo = curStateInfo.parentStateInfo;
/* 1080 */       }while ((curStateInfo != null) && (!curStateInfo.active));
/*      */ 
/* 1082 */       if (this.mDbg) {
/* 1083 */         this.mSm.log(new StringBuilder().append("setupTempStateStackWithStatesToEnter: X mTempStateStackCount=").append(this.mTempStateStackCount).append(",curStateInfo: ").append(curStateInfo).toString());
/*      */       }
/*      */ 
/* 1086 */       return curStateInfo;
/*      */     }
/*      */ 
/*      */     private final void setupInitialStateStack()
/*      */     {
/* 1093 */       if (this.mDbg) {
/* 1094 */         this.mSm.log(new StringBuilder().append("setupInitialStateStack: E mInitialState=").append(this.mInitialState.getName()).toString());
/*      */       }
/*      */ 
/* 1097 */       StateInfo curStateInfo = (StateInfo)this.mStateInfo.get(this.mInitialState);
/* 1098 */       for (this.mTempStateStackCount = 0; curStateInfo != null; this.mTempStateStackCount += 1) {
/* 1099 */         this.mTempStateStack[this.mTempStateStackCount] = curStateInfo;
/* 1100 */         curStateInfo = curStateInfo.parentStateInfo;
/*      */       }
/*      */ 
/* 1104 */       this.mStateStackTopIndex = -1;
/*      */ 
/* 1106 */       moveTempStateStackToStateStack();
/*      */     }
/*      */ 
/*      */     private final Message getCurrentMessage()
/*      */     {
/* 1113 */       return this.mMsg;
/*      */     }
/*      */ 
/*      */     private final IState getCurrentState()
/*      */     {
/* 1120 */       return this.mStateStack[this.mStateStackTopIndex].state;
/*      */     }
/*      */ 
/*      */     private final StateInfo addState(State state, State parent)
/*      */     {
/* 1133 */       if (this.mDbg) {
/* 1134 */         this.mSm.log(new StringBuilder().append("addStateInternal: E state=").append(state.getName()).append(",parent=").append(parent == null ? "" : parent.getName()).toString());
/*      */       }
/*      */ 
/* 1137 */       StateInfo parentStateInfo = null;
/* 1138 */       if (parent != null) {
/* 1139 */         parentStateInfo = (StateInfo)this.mStateInfo.get(parent);
/* 1140 */         if (parentStateInfo == null)
/*      */         {
/* 1142 */           parentStateInfo = addState(parent, null);
/*      */         }
/*      */       }
/* 1145 */       StateInfo stateInfo = (StateInfo)this.mStateInfo.get(state);
/* 1146 */       if (stateInfo == null) {
/* 1147 */         stateInfo = new StateInfo(null);
/* 1148 */         this.mStateInfo.put(state, stateInfo);
/*      */       }
/*      */ 
/* 1152 */       if ((stateInfo.parentStateInfo != null) && (stateInfo.parentStateInfo != parentStateInfo))
/*      */       {
/* 1154 */         throw new RuntimeException("state already added");
/*      */       }
/* 1156 */       stateInfo.state = state;
/* 1157 */       stateInfo.parentStateInfo = parentStateInfo;
/* 1158 */       stateInfo.active = false;
/* 1159 */       if (this.mDbg) this.mSm.log(new StringBuilder().append("addStateInternal: X stateInfo: ").append(stateInfo).toString());
/* 1160 */       return stateInfo;
/*      */     }
/*      */ 
/*      */     private SmHandler(Looper looper, StateMachine sm)
/*      */     {
/* 1170 */       super();
/* 1171 */       this.mSm = sm;
/*      */ 
/* 1173 */       addState(this.mHaltingState, null);
/* 1174 */       addState(this.mQuittingState, null);
/*      */     }
/*      */ 
/*      */     private final void setInitialState(State initialState)
/*      */     {
/* 1179 */       if (this.mDbg) this.mSm.log(new StringBuilder().append("setInitialState: initialState=").append(initialState.getName()).toString());
/* 1180 */       this.mInitialState = initialState;
/*      */     }
/*      */ 
/*      */     private final void transitionTo(IState destState)
/*      */     {
/* 1185 */       this.mDestState = ((State)destState);
/* 1186 */       if (this.mDbg) this.mSm.log(new StringBuilder().append("transitionTo: destState=").append(this.mDestState.getName()).toString());
/*      */     }
/*      */ 
/*      */     private final void deferMessage(Message msg)
/*      */     {
/* 1191 */       if (this.mDbg) this.mSm.log(new StringBuilder().append("deferMessage: msg=").append(msg.what).toString());
/*      */ 
/* 1194 */       Message newMsg = obtainMessage();
/* 1195 */       newMsg.copyFrom(msg);
/*      */ 
/* 1197 */       this.mDeferredMessages.add(newMsg);
/*      */     }
/*      */ 
/*      */     private final void quit()
/*      */     {
/* 1202 */       if (this.mDbg) this.mSm.log("quit:");
/* 1203 */       sendMessage(obtainMessage(-1, mSmHandlerObj));
/*      */     }
/*      */ 
/*      */     private final void quitNow()
/*      */     {
/* 1208 */       if (this.mDbg) this.mSm.log("quitNow:");
/* 1209 */       sendMessageAtFrontOfQueue(obtainMessage(-1, mSmHandlerObj));
/*      */     }
/*      */ 
/*      */     private final boolean isQuit(Message msg)
/*      */     {
/* 1214 */       return (msg.what == -1) && (msg.obj == mSmHandlerObj);
/*      */     }
/*      */ 
/*      */     private final boolean isDbg()
/*      */     {
/* 1219 */       return this.mDbg;
/*      */     }
/*      */ 
/*      */     private final void setDbg(boolean dbg)
/*      */     {
/* 1224 */       this.mDbg = dbg;
/*      */     }
/*      */ 
/*      */     private class QuittingState extends State
/*      */     {
/*      */       private QuittingState()
/*      */       {
/*      */       }
/*      */ 
/*      */       public boolean processMessage(Message msg)
/*      */       {
/*  767 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class HaltingState extends State
/*      */     {
/*      */       private HaltingState()
/*      */       {
/*      */       }
/*      */ 
/*      */       public boolean processMessage(Message msg)
/*      */       {
/*  756 */         StateMachine.this.haltedProcessMessage(msg);
/*  757 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*      */     private class StateInfo
/*      */     {
/*      */       State state;
/*      */       StateInfo parentStateInfo;
/*      */       boolean active;
/*      */ 
/*      */       private StateInfo()
/*      */       {
/*      */       }
/*      */ 
/*      */       public String toString()
/*      */       {
/*  733 */         return new StringBuilder().append("state=").append(this.state.getName()).append(",active=").append(this.active).append(",parent=").append(this.parentStateInfo == null ? "null" : this.parentStateInfo.state.getName()).toString();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogRecords
/*      */   {
/*      */     private static final int DEFAULT_SIZE = 20;
/*  576 */     private Vector<StateMachine.LogRec> mLogRecVector = new Vector();
/*  577 */     private int mMaxSize = 20;
/*  578 */     private int mOldestIndex = 0;
/*  579 */     private int mCount = 0;
/*  580 */     private boolean mLogOnlyTransitions = false;
/*      */ 
/*      */     synchronized void setSize(int maxSize)
/*      */     {
/*  594 */       this.mMaxSize = maxSize;
/*  595 */       this.mCount = 0;
/*  596 */       this.mLogRecVector.clear();
/*      */     }
/*      */ 
/*      */     synchronized void setLogOnlyTransitions(boolean enable) {
/*  600 */       this.mLogOnlyTransitions = enable;
/*      */     }
/*      */ 
/*      */     synchronized boolean logOnlyTransitions() {
/*  604 */       return this.mLogOnlyTransitions;
/*      */     }
/*      */ 
/*      */     synchronized int size()
/*      */     {
/*  611 */       return this.mLogRecVector.size();
/*      */     }
/*      */ 
/*      */     synchronized int count()
/*      */     {
/*  618 */       return this.mCount;
/*      */     }
/*      */ 
/*      */     synchronized void cleanup()
/*      */     {
/*  625 */       this.mLogRecVector.clear();
/*      */     }
/*      */ 
/*      */     synchronized StateMachine.LogRec get(int index)
/*      */     {
/*  634 */       int nextIndex = this.mOldestIndex + index;
/*  635 */       if (nextIndex >= this.mMaxSize) {
/*  636 */         nextIndex -= this.mMaxSize;
/*      */       }
/*  638 */       if (nextIndex >= size()) {
/*  639 */         return null;
/*      */       }
/*  641 */       return (StateMachine.LogRec)this.mLogRecVector.get(nextIndex);
/*      */     }
/*      */ 
/*      */     synchronized void add(StateMachine sm, Message msg, String messageInfo, IState state, IState orgState, IState transToState)
/*      */     {
/*  659 */       this.mCount += 1;
/*  660 */       if (this.mLogRecVector.size() < this.mMaxSize) {
/*  661 */         this.mLogRecVector.add(new StateMachine.LogRec(sm, msg, messageInfo, state, orgState, transToState));
/*      */       } else {
/*  663 */         StateMachine.LogRec pmi = (StateMachine.LogRec)this.mLogRecVector.get(this.mOldestIndex);
/*  664 */         this.mOldestIndex += 1;
/*  665 */         if (this.mOldestIndex >= this.mMaxSize) {
/*  666 */           this.mOldestIndex = 0;
/*      */         }
/*  668 */         pmi.update(sm, msg, messageInfo, state, orgState, transToState);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class LogRec
/*      */   {
/*      */     private StateMachine mSm;
/*      */     private long mTime;
/*      */     private int mWhat;
/*      */     private String mInfo;
/*      */     private IState mState;
/*      */     private IState mOrgState;
/*      */     private IState mDstState;
/*      */ 
/*      */     LogRec(StateMachine sm, Message msg, String info, IState state, IState orgState, IState transToState)
/*      */     {
/*  468 */       update(sm, msg, info, state, orgState, transToState);
/*      */     }
/*      */ 
/*      */     public void update(StateMachine sm, Message msg, String info, IState state, IState orgState, IState dstState)
/*      */     {
/*  479 */       this.mSm = sm;
/*  480 */       this.mTime = System.currentTimeMillis();
/*  481 */       this.mWhat = (msg != null ? msg.what : 0);
/*  482 */       this.mInfo = info;
/*  483 */       this.mState = state;
/*  484 */       this.mOrgState = orgState;
/*  485 */       this.mDstState = dstState;
/*      */     }
/*      */ 
/*      */     public long getTime()
/*      */     {
/*  492 */       return this.mTime;
/*      */     }
/*      */ 
/*      */     public long getWhat()
/*      */     {
/*  499 */       return this.mWhat;
/*      */     }
/*      */ 
/*      */     public String getInfo()
/*      */     {
/*  506 */       return this.mInfo;
/*      */     }
/*      */ 
/*      */     public IState getState()
/*      */     {
/*  513 */       return this.mState;
/*      */     }
/*      */ 
/*      */     public IState getDestState()
/*      */     {
/*  520 */       return this.mDstState;
/*      */     }
/*      */ 
/*      */     public IState getOriginalState()
/*      */     {
/*  527 */       return this.mOrgState;
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  532 */       StringBuilder sb = new StringBuilder();
/*  533 */       sb.append("time=");
/*  534 */       Calendar c = Calendar.getInstance();
/*  535 */       c.setTimeInMillis(this.mTime);
/*  536 */       sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[] { c, c, c, c, c, c }));
/*  537 */       sb.append(" processed=");
/*  538 */       sb.append(this.mState == null ? "<null>" : this.mState.getName());
/*  539 */       sb.append(" org=");
/*  540 */       sb.append(this.mOrgState == null ? "<null>" : this.mOrgState.getName());
/*  541 */       sb.append(" dest=");
/*  542 */       sb.append(this.mDstState == null ? "<null>" : this.mDstState.getName());
/*  543 */       sb.append(" what=");
/*  544 */       String what = this.mSm != null ? this.mSm.getWhatToString(this.mWhat) : "";
/*  545 */       if (TextUtils.isEmpty(what)) {
/*  546 */         sb.append(this.mWhat);
/*  547 */         sb.append("(0x");
/*  548 */         sb.append(Integer.toHexString(this.mWhat));
/*  549 */         sb.append(")");
/*      */       } else {
/*  551 */         sb.append(what);
/*      */       }
/*  553 */       if (!TextUtils.isEmpty(this.mInfo)) {
/*  554 */         sb.append(" ");
/*  555 */         sb.append(this.mInfo);
/*      */       }
/*  557 */       return sb.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.common.stateMachine.StateMachine
 * JD-Core Version:    0.6.0
 */