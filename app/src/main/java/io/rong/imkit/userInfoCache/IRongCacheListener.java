package io.rong.imkit.userInfoCache;

import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

public abstract interface IRongCacheListener
{
  public abstract void onUserInfoUpdated(UserInfo paramUserInfo);

  public abstract void onGroupUserInfoUpdated(GroupUserInfo paramGroupUserInfo);

  public abstract void onGroupUpdated(Group paramGroup);

  public abstract void onDiscussionUpdated(Discussion paramDiscussion);

  public abstract void onPublicServiceProfileUpdated(PublicServiceProfile paramPublicServiceProfile);

  public abstract UserInfo getUserInfo(String paramString);

  public abstract GroupUserInfo getGroupUserInfo(String paramString1, String paramString2);

  public abstract Group getGroupInfo(String paramString);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.IRongCacheListener
 * JD-Core Version:    0.6.0
 */