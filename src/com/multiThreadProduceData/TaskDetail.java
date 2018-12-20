package com.multiThreadProduceData;

/**
 * 任务具体信息（跟业务相关）
 * @author haibozhang
 *
 */
public class TaskDetail implements ITaskDetail{
	
	@Override
	public String produceStr(long num){
		 String str = "20190109|2001|up1002|test2|0|15155526261|a002|疯狂英语2|20190109100100|20190109001000|cp1001|认证渠道|online_view|测试|cp3001|m"+ (num)+"|0|0|20190109"+ "\r\n";
		 return str;
	}

}
