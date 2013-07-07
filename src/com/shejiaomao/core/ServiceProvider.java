package com.shejiaomao.core;

public enum ServiceProvider {

	None(-1, null), // 空，仅用于非SP请求或无需指定SP的情况
	SelfService(ServiceProvider.SP_SELFSERVICE, "Self Service"),
	Sina(ServiceProvider.SP_SINA, "新浪微博"),
	Tencent(ServiceProvider.SP_TENCENT, "腾讯微博"),	
	Qzone(ServiceProvider.SP_QZONE, "QQ空间");

	private ServiceProvider(int spNo, String spName) {
		this.spNo = spNo;
		this.spName = spName;
	}

	/** 服务提供商编号 */
	private int spNo;
	private String spName;

	public int getSpNo() {
		return spNo;
	}

	public String getSpName() {
		return spName;
	}

	public static final int SP_SELFSERVICE       = 30;     //自身服务的SP编号
	
	//微博平台编号
	public static final int SP_SINA            = 1;     // 新浪的SP编号
	public static final int SP_TENCENT         = 2;     // 腾讯的SP编号
	public static final int SP_QZONE           = 3;     // QQ空间的SP编号

	public static ServiceProvider getServiceProvider(int spNo){
		ServiceProvider sp = null;
		switch(spNo){
		case SP_SELFSERVICE:
			sp = SelfService;
			break;
		case SP_SINA:
			sp = Sina;
			break;		
		case SP_TENCENT:
			sp = Tencent;
			break;	
		case SP_QZONE:
			sp = Qzone;
			break;		
		default:
			sp = None;
			break;
		}
		return sp;
	}
}
