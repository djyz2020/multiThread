package com.multiThreadProduceData_raf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 通用工具类
 * @author haibozhang
 * 2018.3.17
 */
public class CommonUtils {
	
	//文件复制操作
	public static void copyFile(File sourceFile, String outputFilePath, InputStream in, OutputStream out){
		try {
			out = new FileOutputStream(new File(outputFilePath), true); //true表示追加操作
			in = new FileInputStream(sourceFile);
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len = in.read(buffer)) > -1){
				out.write(buffer, 0, len);
			}
			out.flush();
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
	
	//判断您是否为空
	public static boolean isEmpty(String str){
		if(str != null && !"".equals(str.trim())){
			return false;
		}else{
			return true;
		}
	}
	
	
}
