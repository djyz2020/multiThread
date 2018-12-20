package com.multiThreadProduceData;

import java.util.Map;

/**
 * 数据生产任务接口
 * @author haibozhang
 * 2018.3.17
 */
public interface IDataTask {
	
	/**
	 * 往指定目的地生产数据
	 * @param outputPath 数据输出路径
	 * @param taskInfo	 任务信息
	 */
	public void produceData(String outputPath, Map<String, Object> taskInfo);

}
