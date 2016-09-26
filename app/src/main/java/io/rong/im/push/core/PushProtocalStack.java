/*      */ package io.rong.push.core;
/*      */ 
/*      */ import io.rong.push.common.RLog;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ 
/*      */ class PushProtocalStack
/*      */ {
/*      */   public static class QueryAckMessage extends PushProtocalStack.RetryableMessage
/*      */   {
/*      */     private byte[] data;
/*      */     private int status;
/*      */     private static final int msgLen = 8;
/*      */     private int date;
/*      */ 
/*      */     public QueryAckMessage(int messageId, int status, byte[] data)
/*      */     {
/* 1035 */       this(messageId);
/* 1036 */       this.data = data;
/* 1037 */       this.date = (int)(System.currentTimeMillis() / 1000L);
/* 1038 */       this.status = status;
/*      */     }
/*      */ 
/*      */     public QueryAckMessage(int messageId) {
/* 1042 */       super();
/* 1043 */       setMessageId(messageId);
/*      */     }
/*      */ 
/*      */     public QueryAckMessage(PushProtocalStack.Message.Header header) throws IOException {
/* 1047 */       super();
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/* 1052 */       int length = 8;
/* 1053 */       if ((this.data != null) && (this.data.length > 0)) {
/* 1054 */         length += this.data.length;
/*      */       }
/* 1056 */       return length;
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/* 1061 */       super.writeMessage(out);
/* 1062 */       DataOutputStream dos = new DataOutputStream(out);
/* 1063 */       dos.writeInt(this.date);
/* 1064 */       dos.writeInt(this.status);
/* 1065 */       if ((this.data != null) && (this.data.length > 0)) {
/* 1066 */         dos.write(this.data);
/*      */       }
/* 1068 */       dos.flush();
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength)
/*      */       throws IOException
/*      */     {
/* 1074 */       super.readMessage(in, msgLength);
/*      */ 
/* 1076 */       DataInputStream dis = new DataInputStream(in);
/* 1077 */       this.date = dis.readInt();
/* 1078 */       this.status = dis.readInt();
/* 1079 */       if (msgLength > 8) {
/* 1080 */         this.data = new byte[msgLength - 8];
/* 1081 */         dis.read(this.data);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getStatus() {
/* 1086 */       return this.status;
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup)
/*      */     {
/* 1091 */       throw new UnsupportedOperationException("PubAck messages don't use the DUP flag.");
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos)
/*      */     {
/* 1097 */       throw new UnsupportedOperationException("PubAck messages don't use the QoS flags.");
/*      */     }
/*      */ 
/*      */     public String getDataAsString()
/*      */     {
/* 1102 */       if (this.data != null) {
/* 1103 */         return PushProtocalStack.FormatUtil.toString(this.data);
/*      */       }
/* 1105 */       return null;
/*      */     }
/*      */ 
/*      */     public static enum QueryStatus
/*      */     {
/* 1022 */       STATUS_ERROR(0), STATUS_OK(1), STATUS_NODBCONF(2), STATUS_PARAMERROR(3);
/*      */ 
/*      */       private int value;
/*      */ 
/* 1026 */       private QueryStatus(int val) { this.value = val; }
/*      */ 
/*      */       public int get()
/*      */       {
/* 1030 */         return this.value;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class QueryMessage extends PushProtocalStack.RetryableMessage
/*      */   {
/*      */     private String topic;
/*      */     private byte[] data;
/*      */     private String targetId;
/*      */     private long signature;
/*      */ 
/*      */     public QueryMessage(String topic, String msg, String targetId)
/*      */     {
/*  943 */       this(topic, PushProtocalStack.FormatUtil.toWMtpString(msg), targetId);
/*      */     }
/*      */ 
/*      */     public QueryMessage(String topic, byte[] data, String targetId) {
/*  947 */       super();
/*  948 */       this.topic = topic;
/*  949 */       this.targetId = targetId;
/*  950 */       this.data = data;
/*  951 */       this.signature = 255L;
/*      */     }
/*      */ 
/*      */     public QueryMessage(PushProtocalStack.Message.Header header) throws IOException {
/*  955 */       super();
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*  960 */       int length = 8;
/*  961 */       length += PushProtocalStack.FormatUtil.toWMtpString(this.topic).length;
/*  962 */       length += PushProtocalStack.FormatUtil.toWMtpString(this.targetId).length;
/*  963 */       length += 2;
/*  964 */       length += this.data.length;
/*  965 */       return length;
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/*  970 */       DataOutputStream dos = new DataOutputStream(out);
/*  971 */       dos.writeLong(this.signature);
/*  972 */       dos.writeUTF(this.topic);
/*  973 */       dos.writeUTF(this.targetId);
/*  974 */       dos.flush();
/*  975 */       super.writeMessage(out);
/*  976 */       dos.write(this.data);
/*  977 */       dos.flush();
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength)
/*      */       throws IOException
/*      */     {
/*  983 */       int pos = 0;
/*  984 */       DataInputStream dis = new DataInputStream(in);
/*  985 */       this.signature = dis.readLong();
/*  986 */       this.topic = dis.readUTF();
/*  987 */       this.targetId = dis.readUTF();
/*  988 */       pos += 8;
/*  989 */       pos += PushProtocalStack.FormatUtil.toWMtpString(this.topic).length;
/*  990 */       pos += PushProtocalStack.FormatUtil.toWMtpString(this.targetId).length;
/*  991 */       super.readMessage(in, msgLength);
/*  992 */       pos += 2;
/*  993 */       this.data = new byte[msgLength - pos];
/*  994 */       dis.read(this.data);
/*      */     }
/*      */ 
/*      */     public String getTopic() {
/*  998 */       return this.topic;
/*      */     }
/*      */ 
/*      */     public byte[] getData() {
/* 1002 */       return this.data;
/*      */     }
/*      */ 
/*      */     public String getTargetId() {
/* 1006 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public String getDataAsString() {
/* 1010 */       return new String(this.data);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class FormatUtil
/*      */   {
/*      */     public static String dumpByteArray(byte[] bytes)
/*      */     {
/*  898 */       StringBuilder sb = new StringBuilder();
/*  899 */       for (int i = 0; i < bytes.length; i++) {
/*  900 */         byte b = bytes[i];
/*  901 */         int iVal = b & 0xFF;
/*  902 */         int byteN = Integer.parseInt(Integer.toBinaryString(iVal));
/*  903 */         sb.append(String.format("%1$02d: %2$08d %3$1c %3$d\n", new Object[] { Integer.valueOf(i), Integer.valueOf(byteN), Integer.valueOf(iVal) }));
/*      */       }
/*  905 */       return sb.toString();
/*      */     }
/*      */ 
/*      */     public static byte[] toWMtpString(String s) {
/*  909 */       if (s == null) {
/*  910 */         return new byte[0];
/*      */       }
/*  912 */       ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
/*  913 */       DataOutputStream dos = new DataOutputStream(byteOut);
/*      */       try {
/*  915 */         dos.writeUTF(s);
/*  916 */         dos.flush();
/*      */       }
/*      */       catch (IOException e) {
/*  919 */         return new byte[0];
/*      */       }
/*  921 */       return byteOut.toByteArray();
/*      */     }
/*      */ 
/*      */     public static String toString(byte[] data) {
/*  925 */       ByteArrayInputStream bais = new ByteArrayInputStream(data);
/*  926 */       DataInputStream dis = new DataInputStream(bais);
/*      */       try {
/*  928 */         return dis.readUTF();
/*      */       } catch (IOException e) {
/*      */       }
/*  931 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class RetryableMessage extends PushProtocalStack.Message
/*      */   {
/*      */     private int messageId;
/*      */ 
/*      */     public RetryableMessage(PushProtocalStack.Message.Header header)
/*      */       throws IOException
/*      */     {
/*  858 */       super();
/*      */     }
/*      */ 
/*      */     public RetryableMessage(PushProtocalStack.Message.Type type) {
/*  862 */       super();
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*  867 */       return 2;
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/*  872 */       int id = getMessageId();
/*  873 */       int lsb = id & 0xFF;
/*  874 */       int msb = (id & 0xFF00) >> 8;
/*  875 */       out.write(msb);
/*  876 */       out.write(lsb);
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength) throws IOException
/*      */     {
/*  881 */       int msgId = in.read() * 255 + in.read();
/*  882 */       setMessageId(msgId);
/*      */     }
/*      */ 
/*      */     public void setMessageId(int messageId) {
/*  886 */       this.messageId = messageId;
/*      */     }
/*      */ 
/*      */     public int getMessageId() {
/*  890 */       return this.messageId;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum QoS
/*      */   {
/*  836 */     AT_MOST_ONCE(0), AT_LEAST_ONCE(1), EXACTLY_ONCE(2), DEFAULT(3);
/*      */ 
/*      */     public final int val;
/*      */ 
/*  841 */     private QoS(int val) { this.val = val; }
/*      */ 
/*      */     static QoS valueOf(int i)
/*      */     {
/*  845 */       for (QoS q : values()) {
/*  846 */         if (q.val == i)
/*  847 */           return q;
/*      */       }
/*  849 */       throw new IllegalArgumentException("Not a valid QoS number: " + i);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class PublishMessage extends PushProtocalStack.RetryableMessage
/*      */   {
/*      */     private String topic;
/*      */     private byte[] data;
/*      */     private String targetId;
/*      */     private int date;
/*      */ 
/*      */     public PublishMessage(PushProtocalStack.Message.Header header)
/*      */       throws IOException
/*      */     {
/*  779 */       super();
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*  784 */       return 0;
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/*  789 */       super.writeMessage(out);
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength) throws IOException
/*      */     {
/*  794 */       int pos = 14;
/*  795 */       DataInputStream dis = new DataInputStream(in);
/*  796 */       dis.readLong();
/*  797 */       this.date = dis.readInt();
/*  798 */       this.topic = dis.readUTF();
/*  799 */       this.targetId = dis.readUTF();
/*  800 */       pos += PushProtocalStack.FormatUtil.toWMtpString(this.topic).length;
/*  801 */       pos += PushProtocalStack.FormatUtil.toWMtpString(this.targetId).length;
/*  802 */       super.readMessage(in, msgLength);
/*  803 */       if (msgLength >= pos) {
/*  804 */         this.data = new byte[msgLength - pos];
/*  805 */         dis.read(this.data);
/*      */       } else {
/*  807 */         RLog.e("PushProtocal", "error msgLength. msgLength:" + msgLength + "pos:" + pos);
/*      */       }
/*      */     }
/*      */ 
/*      */     public String getTopic() {
/*  812 */       return this.topic;
/*      */     }
/*      */ 
/*      */     public byte[] getData() {
/*  816 */       return this.data;
/*      */     }
/*      */ 
/*      */     public int getServerTime() {
/*  820 */       return this.date;
/*      */     }
/*      */ 
/*      */     public String getTargetId() {
/*  824 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public String getDataAsString() {
/*  828 */       if (this.data == null)
/*  829 */         return null;
/*  830 */       return PushProtocalStack.FormatUtil.toString(this.data);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class PingRespMessage extends PushProtocalStack.Message
/*      */   {
/*      */     public PingRespMessage(PushProtocalStack.Message.Header header)
/*      */       throws IOException
/*      */     {
/*  763 */       super();
/*      */     }
/*      */ 
/*      */     public PingRespMessage() {
/*  767 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class PingReqMessage extends PushProtocalStack.Message
/*      */   {
/*      */     public PingReqMessage()
/*      */     {
/*  736 */       super();
/*      */     }
/*      */ 
/*      */     public PingReqMessage(PushProtocalStack.Message.Header header) throws IOException {
/*  740 */       super();
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup)
/*      */     {
/*  745 */       throw new UnsupportedOperationException("PINGREQ message does not support the DUP flag");
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos)
/*      */     {
/*  750 */       throw new UnsupportedOperationException("PINGREQ message does not support the QoS flag");
/*      */     }
/*      */ 
/*      */     public void setRetained(boolean retain)
/*      */     {
/*  755 */       throw new UnsupportedOperationException("PINGREQ message does not support the RETAIN flag");
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class MessageOutputStream
/*      */   {
/*      */     private final OutputStream out;
/*      */ 
/*      */     public MessageOutputStream(OutputStream out)
/*      */     {
/*  723 */       this.out = out;
/*      */     }
/*      */ 
/*      */     public void writeMessage(PushProtocalStack.Message msg) throws IOException {
/*  727 */       msg.write(this.out);
/*  728 */       this.out.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class MessageInputStream
/*      */     implements Closeable
/*      */   {
/*      */     private InputStream in;
/*      */ 
/*      */     public MessageInputStream(InputStream in)
/*      */     {
/*  666 */       this.in = in;
/*      */     }
/*      */ 
/*      */     public PushProtocalStack.Message readMessage() throws IOException {
/*  670 */       byte flags = (byte)this.in.read();
/*      */ 
/*  672 */       PushProtocalStack.Message.Header header = new PushProtocalStack.Message.Header(flags);
/*  673 */       PushProtocalStack.Message msg = null;
/*  674 */       if (header.getType() == null) {
/*  675 */         return null;
/*      */       }
/*  677 */       RLog.i("PushProtocalStack", "receive message type:" + header.getType());
/*  678 */       switch (PushProtocalStack.1.$SwitchMap$io$rong$push$core$PushProtocalStack$Message$Type[header.getType().ordinal()]) {
/*      */       case 1:
/*  680 */         msg = new PushProtocalStack.ConnAckMessage(header);
/*  681 */         break;
/*      */       case 2:
/*  683 */         msg = new PushProtocalStack.PublishMessage(header);
/*  684 */         break;
/*      */       case 3:
/*  687 */         msg = new PushProtocalStack.PingRespMessage(header);
/*  688 */         break;
/*      */       case 4:
/*  690 */         msg = new PushProtocalStack.ConnectMessage(header);
/*  691 */         break;
/*      */       case 5:
/*  693 */         msg = new PushProtocalStack.PingReqMessage(header);
/*  694 */         break;
/*      */       case 6:
/*  696 */         msg = new PushProtocalStack.DisconnectMessage(header);
/*  697 */         break;
/*      */       case 7:
/*  699 */         msg = new PushProtocalStack.QueryMessage(header);
/*  700 */         break;
/*      */       case 8:
/*  702 */         msg = new PushProtocalStack.QueryAckMessage(header);
/*  703 */         break;
/*      */       default:
/*  705 */         RLog.e("PushProtocalStack", "No support for deserializing" + header.getType() + "messages");
/*  706 */         return null;
/*      */       }
/*  708 */       this.in.read();
/*  709 */       msg.read(this.in);
/*  710 */       return msg;
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/*  714 */       this.in.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class Message
/*      */   {
/*      */     private final Header header;
/*      */     private byte headerCode;
/*      */ 
/*      */     public Message(Type type)
/*      */     {
/*  551 */       this.header = new Header(type, false, PushProtocalStack.QoS.AT_MOST_ONCE, false, null);
/*      */     }
/*      */ 
/*      */     public Message(Header header) throws IOException {
/*  555 */       this.header = header;
/*      */     }
/*      */ 
/*      */     final void read(InputStream in) throws IOException {
/*  559 */       int msgLength = readMsgLength(in);
/*  560 */       readMessage(in, msgLength);
/*      */     }
/*      */ 
/*      */     public final void write(OutputStream out) throws IOException {
/*  564 */       this.headerCode = this.header.encode();
/*  565 */       out.write(this.headerCode);
/*  566 */       writeMsgCode(out);
/*  567 */       writeMsgLength(out);
/*  568 */       writeMessage(out);
/*      */     }
/*  572 */     private int readMsgLength(InputStream in) throws IOException { int msgLength = 0;
/*  573 */       int multiplier = 1;
/*      */       int digit;
/*      */       do { digit = in.read();
/*  577 */         msgLength += (digit & 0x7F) * multiplier;
/*  578 */         multiplier *= 128; }
/*  579 */       while ((digit & 0x80) > 0);
/*  580 */       return msgLength; }
/*      */ 
/*      */     private void writeMsgLength(OutputStream out) throws IOException
/*      */     {
/*  584 */       int val = messageLength();
/*      */       do
/*      */       {
/*  587 */         byte b = (byte)(val & 0x7F);
/*  588 */         val >>= 7;
/*  589 */         if (val > 0) {
/*  590 */           b = (byte)(b | 0x80);
/*      */         }
/*  592 */         out.write(b);
/*  593 */       }while (val > 0);
/*      */     }
/*      */ 
/*      */     private void writeMsgCode(OutputStream out) throws IOException {
/*  597 */       int val = messageLength();
/*  598 */       int code = this.headerCode;
/*      */       do
/*      */       {
/*  601 */         byte b = (byte)(val & 0x7F);
/*  602 */         val >>= 7;
/*  603 */         if (val > 0) {
/*  604 */           b = (byte)(b | 0x80);
/*      */         }
/*  606 */         code ^= b;
/*  607 */       }while (val > 0);
/*      */ 
/*  609 */       out.write(code);
/*      */     }
/*      */ 
/*      */     public final byte[] toBytes() {
/*  613 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */       try {
/*  615 */         write(baos);
/*      */       } catch (IOException e) {
/*      */       }
/*  618 */       return baos.toByteArray();
/*      */     }
/*      */ 
/*      */     protected int messageLength() {
/*  622 */       return 0;
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength) throws IOException {
/*      */     }
/*      */ 
/*      */     public void setRetained(boolean retain) {
/*  633 */       Header.access$302(this.header, retain);
/*      */     }
/*      */ 
/*      */     public boolean isRetained() {
/*  637 */       return this.header.retain;
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos) {
/*  641 */       Header.access$402(this.header, qos);
/*      */     }
/*      */ 
/*      */     public PushProtocalStack.QoS getQos() {
/*  645 */       return this.header.qos;
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup) {
/*  649 */       Header.access$502(this.header, dup);
/*      */     }
/*      */ 
/*      */     public boolean isDup() {
/*  653 */       return this.header.dup;
/*      */     }
/*      */ 
/*      */     public Type getType() {
/*  657 */       return this.header.type;
/*      */     }
/*      */ 
/*      */     public static class Header
/*      */     {
/*      */       private PushProtocalStack.Message.Type type;
/*      */       private boolean retain;
/*  510 */       private PushProtocalStack.QoS qos = PushProtocalStack.QoS.AT_MOST_ONCE;
/*      */       private boolean dup;
/*      */ 
/*      */       private Header(PushProtocalStack.Message.Type type, boolean retain, PushProtocalStack.QoS qos, boolean dup)
/*      */       {
/*  514 */         this.type = type;
/*  515 */         this.retain = retain;
/*  516 */         this.qos = qos;
/*  517 */         this.dup = dup;
/*      */       }
/*      */ 
/*      */       public Header(byte flags) {
/*  521 */         this.retain = ((flags & 0x1) > 0);
/*  522 */         this.qos = PushProtocalStack.QoS.valueOf((flags & 0x6) >> 1);
/*  523 */         this.dup = ((flags & 0x8) > 0);
/*  524 */         this.type = PushProtocalStack.Message.Type.valueOf(flags >> 4 & 0xF);
/*      */       }
/*      */ 
/*      */       public PushProtocalStack.Message.Type getType() {
/*  528 */         return this.type;
/*      */       }
/*      */ 
/*      */       private byte encode() {
/*  532 */         byte b = 0;
/*  533 */         b = (byte)(PushProtocalStack.Message.Type.access$000(this.type) << 4);
/*  534 */         b = (byte)(b | (this.retain ? 1 : 0));
/*  535 */         b = (byte)(b | this.qos.val << 1);
/*  536 */         b = (byte)(b | (this.dup ? 8 : 0));
/*  537 */         return b;
/*      */       }
/*      */ 
/*      */       public String toString()
/*      */       {
/*  542 */         return "Header [type=" + this.type + ", retain=" + this.retain + ", qos=" + this.qos + ", dup=" + this.dup + "]";
/*      */       }
/*      */     }
/*      */ 
/*      */     public static enum Type
/*      */     {
/*  486 */       CONNECT(1), CONNACK(2), PUBLISH(3), PUBACK(4), QUERY(5), 
/*  487 */       QUERYACK(6), 
/*  488 */       QUERYCON(7), 
/*  489 */       SUBSCRIBE(8), SUBACK(9), UNSUBSCRIBE(10), UNSUBACK(11), PINGREQ(12), PINGRESP(13), DISCONNECT(14);
/*      */ 
/*      */       private final int val;
/*      */ 
/*  494 */       private Type(int val) { this.val = val; }
/*      */ 
/*      */       static Type valueOf(int i)
/*      */       {
/*  498 */         for (Type t : values()) {
/*  499 */           if (t.val == i)
/*  500 */             return t;
/*      */         }
/*  502 */         return null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class DisconnectMessage extends PushProtocalStack.Message
/*      */   {
/*      */     public static final int MESSAGE_LENGTH = 2;
/*      */     private DisconnectionStatus status;
/*      */ 
/*      */     public DisconnectMessage(PushProtocalStack.Message.Header header)
/*      */       throws IOException
/*      */     {
/*  402 */       super();
/*      */     }
/*      */ 
/*      */     public DisconnectMessage(DisconnectionStatus status) {
/*  406 */       super();
/*  407 */       if (status == null) {
/*  408 */         throw new IllegalArgumentException("The status of ConnAskMessage can't be null");
/*      */       }
/*  410 */       this.status = status;
/*      */     }
/*      */ 
/*      */     public DisconnectMessage() {
/*  414 */       super();
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*  419 */       return 2;
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength)
/*      */       throws IOException
/*      */     {
/*  425 */       in.read();
/*  426 */       int result = in.read();
/*  427 */       switch (result) {
/*      */       case 0:
/*  429 */         this.status = DisconnectionStatus.RECONNECT;
/*  430 */         break;
/*      */       case 1:
/*  432 */         this.status = DisconnectionStatus.OTHER_DEVICE_LOGIN;
/*  433 */         break;
/*      */       case 2:
/*  435 */         this.status = DisconnectionStatus.CLOSURE;
/*  436 */         break;
/*      */       default:
/*  438 */         RLog.e("PushProtocol", "Unsupported DisconnectMessage status: " + result);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out)
/*      */       throws IOException
/*      */     {
/*  445 */       out.write(0);
/*  446 */       switch (PushProtocalStack.1.$SwitchMap$io$rong$push$core$PushProtocalStack$DisconnectMessage$DisconnectionStatus[this.status.ordinal()]) {
/*      */       case 1:
/*  448 */         out.write(0);
/*  449 */         break;
/*      */       case 2:
/*  451 */         out.write(1);
/*  452 */         break;
/*      */       case 3:
/*  454 */         out.write(2);
/*  455 */         break;
/*      */       default:
/*  457 */         RLog.e("PushProtocol", "Unsupported DisconnectMessage code.");
/*      */       }
/*      */     }
/*      */ 
/*      */     public DisconnectionStatus getStatus()
/*      */     {
/*  463 */       return this.status;
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup)
/*      */     {
/*  468 */       throw new UnsupportedOperationException("DISCONNECT message does not support the DUP flag");
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos)
/*      */     {
/*  473 */       throw new UnsupportedOperationException("DISCONNECT message does not support the QoS flag");
/*      */     }
/*      */ 
/*      */     public void setRetained(boolean retain)
/*      */     {
/*  478 */       throw new UnsupportedOperationException("DISCONNECT message does not support the RETAIN flag");
/*      */     }
/*      */ 
/*      */     public static enum DisconnectionStatus
/*      */     {
/*  392 */       RECONNECT, 
/*      */ 
/*  394 */       OTHER_DEVICE_LOGIN, 
/*      */ 
/*  396 */       CLOSURE;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class ConnectMessage extends PushProtocalStack.Message
/*      */   {
/*  173 */     private static int CONNECT_HEADER_SIZE = 12;
/*      */ 
/*  175 */     private String protocolId = "MQIsdp";
/*  176 */     private byte protocolVersion = 3;
/*      */     private String clientId;
/*      */     private int keepAlive;
/*      */     private String username;
/*      */     private String password;
/*      */     private boolean cleanSession;
/*      */     private String willTopic;
/*      */     private String will;
/*      */     private PushProtocalStack.QoS willQoS;
/*      */     private boolean retainWill;
/*      */     private boolean hasUsername;
/*      */     private boolean hasPassword;
/*      */     private boolean hasWill;
/*      */ 
/*      */     public ConnectMessage()
/*      */     {
/*  191 */       super();
/*      */     }
/*      */ 
/*      */     public ConnectMessage(PushProtocalStack.Message.Header header) throws IOException {
/*  195 */       super();
/*      */     }
/*      */ 
/*      */     public ConnectMessage(String clientId, boolean cleanSession, int keepAlive) {
/*  199 */       super();
/*  200 */       if ((clientId == null) || (clientId.length() > 64)) {
/*  201 */         throw new IllegalArgumentException("Client id cannot be null and must be at most 64 characters long: " + clientId);
/*      */       }
/*  203 */       this.clientId = clientId;
/*  204 */       this.cleanSession = cleanSession;
/*  205 */       this.keepAlive = keepAlive;
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*  210 */       int payloadSize = PushProtocalStack.FormatUtil.toWMtpString(this.clientId).length;
/*  211 */       payloadSize += PushProtocalStack.FormatUtil.toWMtpString(this.willTopic).length;
/*  212 */       payloadSize += PushProtocalStack.FormatUtil.toWMtpString(this.will).length;
/*  213 */       payloadSize += PushProtocalStack.FormatUtil.toWMtpString(this.username).length;
/*  214 */       payloadSize += PushProtocalStack.FormatUtil.toWMtpString(this.password).length;
/*  215 */       return payloadSize + CONNECT_HEADER_SIZE;
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength) throws IOException
/*      */     {
/*  220 */       DataInputStream dis = new DataInputStream(in);
/*  221 */       this.protocolId = dis.readUTF();
/*  222 */       this.protocolVersion = dis.readByte();
/*  223 */       byte cFlags = dis.readByte();
/*  224 */       this.hasUsername = ((cFlags & 0x80) > 0);
/*  225 */       this.hasPassword = ((cFlags & 0x40) > 0);
/*  226 */       this.retainWill = ((cFlags & 0x20) > 0);
/*  227 */       this.willQoS = PushProtocalStack.QoS.valueOf(cFlags >> 3 & 0x3);
/*  228 */       this.hasWill = ((cFlags & 0x4) > 0);
/*  229 */       this.cleanSession = ((cFlags & 0x20) > 0);
/*  230 */       this.keepAlive = (dis.read() * 256 + dis.read());
/*  231 */       this.clientId = dis.readUTF();
/*  232 */       if (this.hasWill) {
/*  233 */         this.willTopic = dis.readUTF();
/*  234 */         this.will = dis.readUTF();
/*      */       }
/*  236 */       if (this.hasUsername)
/*      */         try {
/*  238 */           this.username = dis.readUTF();
/*      */         }
/*      */         catch (EOFException e)
/*      */         {
/*      */         }
/*  243 */       if (this.hasPassword)
/*      */         try {
/*  245 */           this.password = dis.readUTF();
/*      */         }
/*      */         catch (EOFException e)
/*      */         {
/*      */         }
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out)
/*      */       throws IOException
/*      */     {
/*  255 */       DataOutputStream dos = new DataOutputStream(out);
/*  256 */       dos.writeUTF(this.protocolId);
/*  257 */       dos.write(this.protocolVersion);
/*  258 */       int flags = this.cleanSession ? 2 : 0;
/*  259 */       flags |= (this.hasWill ? 4 : 0);
/*  260 */       flags |= (this.willQoS == null ? 0 : this.willQoS.val << 3);
/*  261 */       flags |= (this.retainWill ? 32 : 0);
/*  262 */       flags |= (this.hasPassword ? 64 : 0);
/*  263 */       flags |= (this.hasUsername ? 128 : 0);
/*  264 */       dos.write((byte)flags);
/*  265 */       dos.writeChar(this.keepAlive);
/*      */ 
/*  267 */       dos.writeUTF(this.clientId);
/*  268 */       if (this.hasWill) {
/*  269 */         dos.writeUTF(this.willTopic);
/*  270 */         dos.writeUTF(this.will);
/*      */       }
/*  272 */       if (this.hasUsername) {
/*  273 */         dos.writeUTF(this.username);
/*      */       }
/*  275 */       if (this.hasPassword) {
/*  276 */         dos.writeUTF(this.password);
/*      */       }
/*      */ 
/*  279 */       dos.flush();
/*      */     }
/*      */ 
/*      */     public void setCredentials(String username) {
/*  283 */       setCredentials(username, null);
/*      */     }
/*      */ 
/*      */     public void setCredentials(String username, String password)
/*      */     {
/*  288 */       if (((username == null) || (username.isEmpty())) && (password != null) && (!password.isEmpty())) {
/*  289 */         throw new IllegalArgumentException("It is not valid to supply a password without supplying a username.");
/*      */       }
/*      */ 
/*  292 */       this.username = username;
/*  293 */       this.password = password;
/*  294 */       this.hasUsername = (this.username != null);
/*  295 */       this.hasPassword = (this.password != null);
/*      */     }
/*      */ 
/*      */     public void setWill(String willTopic, String will)
/*      */     {
/*  300 */       setWill(willTopic, will, PushProtocalStack.QoS.AT_MOST_ONCE, false);
/*      */     }
/*      */ 
/*      */     public void setWill(String willTopic, String will, PushProtocalStack.QoS willQoS, boolean retainWill) {
/*  304 */       if (((willTopic == null ? 1 : 0) ^ (will == null ? 1 : 0)) == 0) { if (((will == null ? 1 : 0) ^ (willQoS == null ? 1 : 0)) == 0); } else throw new IllegalArgumentException("Can't set willTopic, will or willQoS value independently");
/*      */ 
/*  308 */       this.willTopic = willTopic;
/*  309 */       this.will = will;
/*  310 */       this.willQoS = willQoS;
/*  311 */       this.retainWill = retainWill;
/*  312 */       this.hasWill = (willTopic != null);
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup)
/*      */     {
/*  317 */       throw new UnsupportedOperationException("CONNECT messages don't use the DUP flag.");
/*      */     }
/*      */ 
/*      */     public void setRetained(boolean retain)
/*      */     {
/*  322 */       throw new UnsupportedOperationException("CONNECT messages don't use the RETAIN flag.");
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos)
/*      */     {
/*  327 */       throw new UnsupportedOperationException("CONNECT messages don't use the QoS flags.");
/*      */     }
/*      */ 
/*      */     public String getProtocolId() {
/*  331 */       return this.protocolId;
/*      */     }
/*      */ 
/*      */     public byte getProtocolVersion() {
/*  335 */       return this.protocolVersion;
/*      */     }
/*      */ 
/*      */     public String getClientId() {
/*  339 */       return this.clientId;
/*      */     }
/*      */ 
/*      */     public int getKeepAlive() {
/*  343 */       return this.keepAlive;
/*      */     }
/*      */ 
/*      */     public String getUsername() {
/*  347 */       return this.username;
/*      */     }
/*      */ 
/*      */     public String getPassword() {
/*  351 */       return this.password;
/*      */     }
/*      */ 
/*      */     public boolean isCleanSession() {
/*  355 */       return this.cleanSession;
/*      */     }
/*      */ 
/*      */     public String getWillTopic() {
/*  359 */       return this.willTopic;
/*      */     }
/*      */ 
/*      */     public String getWill() {
/*  363 */       return this.will;
/*      */     }
/*      */ 
/*      */     public PushProtocalStack.QoS getWillQoS() {
/*  367 */       return this.willQoS;
/*      */     }
/*      */ 
/*      */     public boolean isWillRetained() {
/*  371 */       return this.retainWill;
/*      */     }
/*      */ 
/*      */     public boolean hasUsername() {
/*  375 */       return this.hasUsername;
/*      */     }
/*      */ 
/*      */     public boolean hasPassword() {
/*  379 */       return this.hasPassword;
/*      */     }
/*      */ 
/*      */     public boolean hasWill() {
/*  383 */       return this.hasWill;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class ConnAckMessage extends PushProtocalStack.Message
/*      */   {
/*      */     public static final int MESSAGE_LENGTH = 2;
/*      */     private ConnectionStatus status;
/*      */     private String userId;
/*      */ 
/*      */     public ConnAckMessage()
/*      */     {
/*   40 */       super();
/*      */     }
/*      */ 
/*      */     public ConnAckMessage(PushProtocalStack.Message.Header header) throws IOException {
/*   44 */       super();
/*      */     }
/*      */ 
/*      */     public ConnAckMessage(ConnectionStatus status) {
/*   48 */       super();
/*   49 */       if (status == null) {
/*   50 */         throw new IllegalArgumentException("The status of ConnAskMessage can't be null");
/*      */       }
/*   52 */       this.status = status;
/*      */     }
/*      */ 
/*      */     protected int messageLength()
/*      */     {
/*   57 */       int length = 2;
/*      */ 
/*   59 */       if ((this.userId != null) && (!this.userId.isEmpty())) {
/*   60 */         length += PushProtocalStack.FormatUtil.toWMtpString(this.userId).length;
/*      */       }
/*      */ 
/*   63 */       return length;
/*      */     }
/*      */ 
/*      */     protected void readMessage(InputStream in, int msgLength)
/*      */       throws IOException
/*      */     {
/*   69 */       in.read();
/*   70 */       int result = in.read();
/*   71 */       switch (result) {
/*      */       case 0:
/*   73 */         this.status = ConnectionStatus.ACCEPTED;
/*   74 */         break;
/*      */       case 1:
/*   76 */         this.status = ConnectionStatus.UNACCEPTABLE_PROTOCOL_VERSION;
/*   77 */         break;
/*      */       case 2:
/*   79 */         this.status = ConnectionStatus.IDENTIFIER_REJECTED;
/*   80 */         break;
/*      */       case 3:
/*   82 */         this.status = ConnectionStatus.SERVER_UNAVAILABLE;
/*   83 */         break;
/*      */       case 4:
/*   85 */         this.status = ConnectionStatus.BAD_USERNAME_OR_PASSWORD;
/*   86 */         break;
/*      */       case 5:
/*   88 */         this.status = ConnectionStatus.NOT_AUTHORIZED;
/*   89 */         break;
/*      */       case 6:
/*   91 */         this.status = ConnectionStatus.REDIRECT;
/*   92 */         break;
/*      */       default:
/*   94 */         RLog.e("PushProtocol", "Unsupported CONNACK code");
/*   95 */         this.status = ConnectionStatus.REDIRECT;
/*      */       }
/*      */ 
/*   99 */       if (msgLength > 2) {
/*  100 */         DataInputStream dis = new DataInputStream(in);
/*  101 */         this.userId = dis.readUTF();
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void writeMessage(OutputStream out) throws IOException
/*      */     {
/*  107 */       out.write(0);
/*  108 */       switch (PushProtocalStack.1.$SwitchMap$io$rong$push$core$PushProtocalStack$ConnAckMessage$ConnectionStatus[this.status.ordinal()]) {
/*      */       case 1:
/*  110 */         out.write(0);
/*  111 */         break;
/*      */       case 2:
/*  113 */         out.write(1);
/*  114 */         break;
/*      */       case 3:
/*  116 */         out.write(2);
/*  117 */         break;
/*      */       case 4:
/*  119 */         out.write(3);
/*  120 */         break;
/*      */       case 5:
/*  122 */         out.write(4);
/*  123 */         break;
/*      */       case 6:
/*  125 */         out.write(5);
/*  126 */         break;
/*      */       case 7:
/*  128 */         out.write(6);
/*  129 */         break;
/*      */       default:
/*  131 */         RLog.e("PushProtocol", "Unsupported CONNACK message status: " + this.status);
/*      */       }
/*      */ 
/*  135 */       if ((this.userId != null) && (!this.userId.isEmpty())) {
/*  136 */         DataOutputStream dos = new DataOutputStream(out);
/*  137 */         dos.writeUTF(this.userId);
/*  138 */         dos.flush();
/*      */       }
/*      */     }
/*      */ 
/*      */     public ConnectionStatus getStatus() {
/*  143 */       return this.status;
/*      */     }
/*      */ 
/*      */     public void setUserId(String userId) {
/*  147 */       this.userId = userId;
/*      */     }
/*      */ 
/*      */     public String getUserId() {
/*  151 */       return this.userId;
/*      */     }
/*      */ 
/*      */     public void setDup(boolean dup)
/*      */     {
/*  156 */       throw new UnsupportedOperationException("CONNACK messages don't use the DUP flag.");
/*      */     }
/*      */ 
/*      */     public void setRetained(boolean retain)
/*      */     {
/*  161 */       throw new UnsupportedOperationException("CONNACK messages don't use the RETAIN flag.");
/*      */     }
/*      */ 
/*      */     public void setQos(PushProtocalStack.QoS qos)
/*      */     {
/*  166 */       throw new UnsupportedOperationException("CONNACK messages don't use the QoS flags.");
/*      */     }
/*      */ 
/*      */     public static enum ConnectionStatus
/*      */     {
/*   23 */       ACCEPTED, 
/*      */ 
/*   25 */       UNACCEPTABLE_PROTOCOL_VERSION, 
/*      */ 
/*   27 */       IDENTIFIER_REJECTED, 
/*      */ 
/*   29 */       SERVER_UNAVAILABLE, 
/*      */ 
/*   31 */       BAD_USERNAME_OR_PASSWORD, 
/*      */ 
/*   33 */       NOT_AUTHORIZED, REDIRECT;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.PushProtocalStack
 * JD-Core Version:    0.6.0
 */