package com.multiThreadProduceData;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据生产工厂
 * @author haibozhang
 * 2018.3.17
 */
public class DataProduceFactory {
	
	public AtomicInteger count = new AtomicInteger(0); 				//线程任务完成计数器
	public String outputPath = "C:/test/"; 			  				//默认输出路径
	public ITaskDetail taskDetail = new TaskDetail();  				//任务详情（需要编写的业务）
	public long totalNum = 10 * 10000;				 	 			//需要的数据量
	public String outputFileName = UUID.randomUUID().toString(); 	//输出文件名称
	public String sourcePath = "";									//源文件路径
	
	public void DataProduce(String outputPath01, String outputFileName01, String sourcePath01,
			ITaskDetail taskDetail01, long totalNum01){
		if(!CommonUtils.isEmpty(outputPath01)){
			outputPath = outputPath01;
		}
		if(!CommonUtils.isEmpty(outputFileName01 + "")){
			outputFileName = outputFileName01;
		}
		if(!CommonUtils.isEmpty(sourcePath01)){
			sourcePath = sourcePath01;
		}
		if(taskDetail01 != null){
			taskDetail = taskDetail01;
		}
		if(!CommonUtils.isEmpty(totalNum01 + "") && totalNum01 != 0){
			totalNum = totalNum01;
		}
		
		//创建任务队列
		List<Map<String, Object>> taskInfoList = new ArrayList<Map<String, Object>>(); 
		//创建线程池
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		//根据CPU内核数量分配线程
		int threadNum = Runtime.getRuntime().availableProcessors(); 
		long taskExecuteTime = System.currentTimeMillis();
		
		//1.为线程分配任务
		AllocateTasks(taskInfoList, threadNum, sourcePath);
		
		//2.启动线程执行任务
		ExecuteTasks(taskInfoList, threadNum, cachedThreadPool);
		
		System.out.println("@任务执行耗时： " + (System.currentTimeMillis() - taskExecuteTime) /1000 + "s");
		
		//3.合并执行结果
		long combineResultTime = System.currentTimeMillis();
		CombineAndReduce();
		
		System.out.println("@合并结果耗时： " + (System.currentTimeMillis() - combineResultTime) /1000 + "s");
		System.out.println("@任务执行结果文件： " + outputPath + File.separator + outputFileName);
		
		//关闭线程池
		if(!cachedThreadPool.isShutdown()){
			cachedThreadPool.shutdown();
		}
	}
	
	/**
	 * 分配任务
	 * @param taskInfoList	任务列表
	 * @param threadNum		线程数量
	 * @param sourcePath 	源文件路径
	 */
	private void AllocateTasks(List<Map<String, Object>> taskInfoList, int threadNum, String sourcePath) {
		for (int i = 0; i < threadNum; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			if(i != threadNum - 1){
				map.put(Const.START, totalNum/threadNum * i);
				map.put(Const.END, totalNum/threadNum * (i + 1) - 1);
				map.put(Const.THREADNAME, "thread" + i);
			}else{
				map.put(Const.START, totalNum/threadNum * i);
				map.put(Const.END, totalNum);
				map.put(Const.THREADNAME, "thread" + i);
			}
			map.put(Const.TASKDETAIL, taskDetail.getClass());
			map.put(Const.SOURCEPATH, sourcePath);
			map.put(Const.COUNT, count);
			//System.out.println("任务信息： " + map);
			taskInfoList.add(map);
		}
	}

	/**
	 * 启动线程执行任务
	 * @param taskInfoList		 任务列表
	 * @param threadNum    		 执行线程
	 * @param cachedThreadPool	 线程池
	 */
	private void ExecuteTasks(List<Map<String, Object>> taskInfoList, int threadNum,
			ExecutorService cachedThreadPool) {
		File file = new File(outputPath);
		if(!file.exists() || !file.isDirectory()){
			file.mkdirs();
		}
		for (int i = 0; i < threadNum; i++) {
		    cachedThreadPool.execute(new DataThread(taskInfoList.get(i),
		    		DataTask.class, outputPath + File.separator + i + "_" +outputFileName));
		}
		while(count.get() < threadNum){
			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 合并任务结果
	 */
	private void CombineAndReduce() {
		File dir = new File(outputPath);
		if(!dir.exists() && !dir.isDirectory()){
			dir.mkdirs();
		}
		File outFile = new File(outputPath + File.separator + outputFileName);
		if(outFile.exists()){ //如果输出文件存在，先删除
			outFile.delete();
		}
		File[] files = null;
		if(dir.isDirectory()){ //合并任务结果
			files = dir.listFiles();
			InputStream in = null;
			OutputStream out = null;
			try {
				for(File file : files){
					CommonUtils.copyFile(file, outFile.getAbsolutePath() , in, out);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(in != null){
						in.close();
					}
					if(out != null){
						out.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		if(files != null && files.length > 0){ //删除缓存文件
			for(File file : files){
				if(file.exists() && file.getName().indexOf("_" +outputFileName) > -1){
					file.deleteOnExit();
				}
			}
		}
	}

}
