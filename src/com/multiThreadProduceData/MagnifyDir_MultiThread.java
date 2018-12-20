package com.multiThreadProduceData;

public class MagnifyDir_MultiThread {
	
	public static void main(String[] args) {
		
		//放大系数
		long totalNum = 5*10000;	
		//文件输出路径
		String outputPath = "C:/target"; 
		
		ITaskDetail taskDetail = new TaskDetail(); //任务细节
		new DataProduceFactory().DataProduce(outputPath, "test.txt", "", taskDetail, totalNum); //数据生产
	}

}
