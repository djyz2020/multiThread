package com.multiThreadProduceData_raf;

import java.io.RandomAccessFile;
import java.util.Map;

/**
 * 数据生产任务接口
 * @author haibozhang
 * 2018.3.17
 */
public interface IDataTask {
	
	/**
	 * 往指定目的地生产数据
	 * @param raf 		缓存文件
	 * @param taskInfo	 任务信息
	 */
	public void produceData(RandomAccessFile raf, Map<String, Object> taskInfo);

}
