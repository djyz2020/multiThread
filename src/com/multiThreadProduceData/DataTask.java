package com.multiThreadProduceData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 数据生产任务示例
 * @author haibozhang
 * 2018.3.17
 */
public class DataTask implements IDataTask{

	@Override
	public void produceData(String outputPath, Map<String, Object> map) {
		long startNum = Long.valueOf(map.get(Const.START) + "");
		long endNum = Long.valueOf(map.get(Const.END) + "");
		long recordsNum = (endNum - startNum) + 1;
		long blockSize = Const.BLOCKSIZE;
		Class<?> clazz = (Class<?>) map.get(Const.TASKDETAIL);
		ITaskDetail taskDetail = null;
		String sourcePath = (String) map.get(Const.SOURCEPATH);
		if(CommonUtils.isEmpty(sourcePath)){ //如果不指定源文件
			try {
				taskDetail = (ITaskDetail) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{ //如果指定源文件
			try {
				Constructor<?> cons = clazz.getConstructor(String.class);
				taskDetail = (ITaskDetail) cons.newInstance(sourcePath);
			} catch (NoSuchMethodException | SecurityException 
					| InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			if(taskDetail == null){
				System.out.println("实例化ITaskDetail实现类失败！");
				return;
			}
		}
		
		BufferedWriter bf = null;
		try {
			File file = new File(outputPath);
			if(!(file.exists() && file.isFile())){
				file.createNewFile();
			}
			bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			long outerStartTime = System.currentTimeMillis();
			
			if(recordsNum <= blockSize){
				StringBuffer s = new StringBuffer();
				for(long i = startNum; i < endNum + 1; i++){
					s.append(taskDetail.produceStr(i)); //被放大的文件内容
				}
				bf.write(s.toString());
			}else{
				//数据切分
				long startIndex = startNum/blockSize;
				long endIndex = endNum/blockSize;
				//a. 数据首段
				if(startNum % blockSize > 0){
					StringBuffer s = new StringBuffer();
					for(long i = startNum; i < (startIndex  + 1) * blockSize + 1; i++){
						s.append(taskDetail.produceStr(i));
					}
					bf.write(s.toString());
					startIndex += 1;
				}
				//b. 数据中间段
				for(long i = startIndex; i < endIndex; i++){
					StringBuffer s = new StringBuffer();
					for(long j = 0; j < blockSize; j++){
						s.append(taskDetail.produceStr(i*blockSize + j));
					}
					bf.write(s.toString());
				}
				//c. 数据尾段
				if(recordsNum % blockSize > 0){
					StringBuffer s = new StringBuffer();
					for(long i = endIndex * blockSize; i < endNum + 1; i++){
						s.append(taskDetail.produceStr(i));
					}
					bf.write(s.toString());
				}
			}
			System.out.println("线程 [" + map.get(Const.THREADNAME) + "] 耗时: "
							+ (System.currentTimeMillis() - outerStartTime) + "ms");
			bf.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bf != null){
					bf.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
}


