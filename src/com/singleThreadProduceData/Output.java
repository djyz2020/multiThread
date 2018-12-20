package com.singleThreadProduceData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String filePath = "C:/Users/haibozhang/Desktop/test/test8.txt";
		FileWriter fw = new FileWriter(new File(filePath));
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++){
			long startTime1 = System.currentTimeMillis();
			StringBuffer s = new StringBuffer();
			for(int j = 0; j < 10000; j++){
				  s.append("20190109|2001|up1002|test2|0|15155526261|a002|疯狂英语2|20190109100100|20190109001000|cp1001|认证渠道|online_view|测试|cp3001|m"+ (i*10000 + j)+"|0|0|20190109"+ "\r\n");
			}
			System.out.println("耗时: " + (System.currentTimeMillis() - startTime1) + "ms");
			fw.write(s.toString());
		}
		System.out.println("总耗时: " + (System.currentTimeMillis() - startTime)/1000 + "s");
		fw.flush();
		fw.close();
		
	}

}
