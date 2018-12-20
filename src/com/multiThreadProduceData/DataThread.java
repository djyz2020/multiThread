package com.multiThreadProduceData;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据生产线程
 * @author haibozhang
 * 2018.3.17
 */
public class DataThread implements Runnable{
	
	private Map<String, Object> taskInfo; 		//任务名字
	private Class<?> clazz;		 				//任务执行对象
	private String outputPath; 					//生产目的地
	
	public DataThread() {}
	
	public DataThread(Map<String, Object> taskInfo, Class<?> clazz, String outputPath) {
		this.taskInfo = taskInfo;
		this.clazz = clazz;
		this.outputPath = outputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Map<String, Object> getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(Map<String, Object> taskInfo) {
		this.taskInfo = taskInfo;
	}

	@Override
	public void run() { //线程启动执行的方法
		try {
			IDataTask dataTask = (IDataTask) clazz.newInstance();
			dataTask.produceData(outputPath, taskInfo);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			((AtomicInteger)taskInfo.get(Const.COUNT)).getAndIncrement();
		}
	}

}
