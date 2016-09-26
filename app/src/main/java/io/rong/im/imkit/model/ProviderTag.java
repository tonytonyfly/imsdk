package io.rong.imkit.model;

import io.rong.imlib.model.MessageContent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderTag
{
  public abstract boolean showPortrait();

  public abstract boolean centerInHorizontal();

  public abstract boolean hide();

  public abstract boolean showWarning();

  public abstract boolean showProgress();

  public abstract boolean showSummaryWithName();

  public abstract Class<? extends MessageContent> messageContent();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.ProviderTag
 * JD-Core Version:    0.6.0
 */