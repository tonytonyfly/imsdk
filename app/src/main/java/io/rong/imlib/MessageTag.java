package io.rong.imlib;

import io.rong.message.MessageHandler;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageTag
{
  public static final int NONE = 0;
  public static final int ISPERSISTED = 1;
  public static final int ISCOUNTED = 3;
  public static final int STATUS = 16;

  public abstract String value();

  public abstract int flag();

  public abstract Class<? extends MessageHandler> messageHandler();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.MessageTag
 * JD-Core Version:    0.6.0
 */