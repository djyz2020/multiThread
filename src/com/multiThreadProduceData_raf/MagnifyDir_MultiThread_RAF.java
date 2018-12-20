package com.multiThreadProduceData_raf;

import java.io.File;

public class MagnifyDir_MultiThread_RAF {
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		//需要放大的文件夹路径
		String sourcePath = "C:\\source";
		//放大系数
		long totalNum = 16;	
		//文件输出路径
		String outputPath = "C:\\target";  //61,440 59,160
		
		File sourceDir = new File(sourcePath);
		if(sourceDir.exists() && sourceDir.isDirectory()){
			File[] files = sourceDir.listFiles();
			for(File file : files){
				ITaskDetail taskDetail = new TaskDetail(); //任务细节
				new DataProduceFactory().DataProduce(outputPath, file.getName(), file.getAbsolutePath(),
						taskDetail, totalNum); //数据生产
			}
		}else{
			System.out.println("指定的路径不存在，请检查 [" + sourcePath + "]");
		}
		System.out.println("the program takes : [" + (System.currentTimeMillis() - startTime)/1000 + "] s");
		
	}

}
