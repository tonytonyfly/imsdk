package io.rong.imkit.model;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConversationProviderTag
{
  public abstract int portraitPosition();

  public abstract boolean centerInHorizontal();

  public abstract String conversationType();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.ConversationProviderTag
 * JD-Core Version:    0.6.0
 */