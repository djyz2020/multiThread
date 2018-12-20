package com.multiThreadProduceDataTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import com.multiThreadProduceData.Const;
import com.multiThreadProduceData.ITaskDetail;

/**
 * 任务具体信息（跟业务相关）
 * @author haibozhang
 * 2018.3.17
 */
public class TaskDetail implements ITaskDetail{
	
	private String sourcePath; //放大文件夹路径
	
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	public TaskDetail() {
		super();
	}

	public TaskDetail(String sourcePath) {
		this.setSourcePath(sourcePath);
	}

	@Override
	public String produceStr(long num){
		StringBuffer sb = new StringBuffer();
		if(sb.length() == 0){ //只读取源文件一次，不需要重复读取
			sb = readFile(sourcePath);
		}
		if(this.sourcePath != null){
			Const.BLOCKSIZE = 100;
		}
		if(sb.toString().length() > 5000){ //数据块大小设置
			Const.BLOCKSIZE = 100;
		}else if(sb.toString().length() > 20000){
			Const.BLOCKSIZE = 20;
		}
		return sb.toString();
	}
	
	public static StringBuffer readFile(String sourcePath){
		File sourceFile = new File(sourcePath);
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(sourceFile));
			while(in.ready()){
				sb.append(in.readLine() + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null){
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return sb;
	}
}


