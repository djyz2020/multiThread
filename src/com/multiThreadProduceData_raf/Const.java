package com.multiThreadProduceData_raf;

/**
 * 常量库
 * @author haibozhang
 * 2018.3.17
 */
public class Const {
	
	public final static String START = "START";				 //任务起点标记
	public final static String END = "END";					 //任务终点标记
	public final static String THREADNAME = "THREADNAME";	 //线程名字标记
	
	public final static String TASKDETAIL = "TASKDETAIL"; 	 //任务接口标记
	public final static String SOURCEPATH = "SOURCEPATH"; 	 //map中文件名标记
	public final static String COUNT = "COUNT";				 //线程计数器标记
	public static long BLOCKSIZE = 1000;					 //写入文件数据块大小
	

}
