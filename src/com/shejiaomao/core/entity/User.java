package com.shejiaomao.core.entity;

import java.util.Date;

import com.shejiaomao.core.util.HashCodeHelper;


/**
 * 微博用户类
 *
 * @version
 * @author 
 */
public class User extends BaseSocialEntity {
	private static final long serialVersionUID = -6345893237975349030L;

	/** 用户ID */
	protected String userId;
	/** 用户名 */
	protected String name;
	/** 显示名称 */
	protected String screenName;
	/** 头像图片地址 */
	protected String profileImageUrl;
	/** 头像图片地址 */
	protected String bigProfileImageUrl;
	
	/** 性别 */
	protected Gender gender;
	
	/** 好友数或关注数 */
	private int friendsCount;
	/** 被关注数量或粉丝数 */
	private int followersCount;
	/** 签名或微博数 */
	private int statusesCount;
	
	/** 简单描述 */
	protected String description;
	/** 当前所在地 */
	protected String location;
	/** 是否经过认证 */
	protected boolean isVerified;
	private String verifyInfo;
	
	protected Date createdAt;
	
	/** 收藏数量 */
	private int favouritesCount;

	public String getDisplayName() {
		String displayName = null;
		switch(serviceProvider) {
		case Sina:
			displayName = screenName;
			break;
		case Tencent:
			displayName = name;
			break;
		}
		return displayName;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getBigProfileImageUrl() {
		return bigProfileImageUrl;
	}

	public void setBigProfileImageUrl(String bigProfileImageUrl) {
		this.bigProfileImageUrl = bigProfileImageUrl;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getFriendsCount() {
		return friendsCount;
	}
	
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	
	public int getFollowersCount() {
		return followersCount;
	}
	
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	
	public int getStatusesCount() {
		return statusesCount;
	}
	
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public String getVerifyInfo() {
		return verifyInfo;
	}

	public void setVerifyInfo(String verifyInfo) {
		this.verifyInfo = verifyInfo;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	@Override
	public int hashCode() {
		HashCodeHelper helper = HashCodeHelper.getInstance();
		helper.appendObj(userId).appendObj(serviceProvider)
		.appendObj(name).appendObj(screenName).appendBoolean(isVerified);
		return helper.getHashCode();
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
			return false;
        }
        
        final User obj = (User) o;
        if (userId == null 
        	|| serviceProvider == null) {
        	return false;
        }
        
        if (userId.equals(obj.getUserId())
        	&& serviceProvider == obj.getServiceProvider()) {
        	return true;
        }
        
        return false;
	}

	@Override
	public String toString() {
		return "User{"
				+ " sp=" + serviceProvider
				+ ", userId=" + userId
				+ ", name='" + name + '\''
				+ ", screenName='" + screenName + '\''
				+ ", location='" + location + '\''
				+ ", description='" + description + '\''
				+ ", profileImageUrl='" + profileImageUrl + '\''
				+ ", followersCount=" + this.getFollowersCount()
				
				+ ", friendsCount=" + this.getFriendsCount()
				+ ", createdAt=" + createdAt
				+ ", favouritesCount=" + favouritesCount
				+ ", statusesCount=" + this.getStatusesCount()
				+ ", verified=" + isVerified
				+ '}';
	}

}
