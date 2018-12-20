package com.multiThreadProduceDataTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 放大文件工具
 * @author haibozhang
 * 2018.3.17
 */
public class MagnifyDir_SingleThread{
	
	public MagnifyDir_SingleThread(){}

	/**
	 * 放大文件方法
	 * @param sourceFile  	被放大的文件
	 * @param factor	          被放大的系数
	 * @@param outputPath 	输出文件夹路径
	 */
	public static void magnifyFile(File sourceFile, long factor, String outputPath) {
		OutputStream out = null;
		InputStream in = null;
		try {
			File targetFile = new File(outputPath + File.separator + sourceFile.getName());
			for(long i = 0; i < factor; i++){ //根据放大洗漱放大
				copyFile(sourceFile, targetFile, in, out);
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
	
	/**
	 * 文件复制操作
	 * @param sourceFile 源文件
	 * @param targetFile 目标文件
	 * @param in		   输入流
	 * @param out        输出流
	 */
	public static void copyFile(File sourceFile, File targetFile, InputStream in, OutputStream out){
		try {
			if(!targetFile.exists()){
				targetFile.createNewFile();
			}
			out = new FileOutputStream(targetFile, true); //true表示追加操作
			in = new FileInputStream(sourceFile);
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len = in.read(buffer)) > -1){
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 放大文件夹
	 * @param sourceDir 源文件夹
	 * @param targetDir 目标文件夹
	 * @param factor	放大系数
	 */
	public static void magnifyDirectory(String sourceDir, String targetDir, long factor){
		File dir = new File(sourceDir == null ? "" : sourceDir.trim());
		if(dir.exists() && dir.isDirectory()){ //放大文件夹存在
			//输出文件夹创建
			File outputDir = new File(targetDir);
			if(!outputDir.exists() || !outputDir.isDirectory()){
				outputDir.mkdir();
			}
			//放大文件
			File[] files = dir.listFiles();
			for(File file : files){
				magnifyFile(file, factor, outputDir.getAbsolutePath());
			}
			System.out.println("target directory：[" + targetDir + "]");
		}else{
			System.out.println("source directory is not exist，please check：[" + sourceDir + "]");
		}
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String sourcePath = "C:/source";
		String targetPath = "C:/target";
		magnifyDirectory(sourcePath, targetPath, 50000);
		System.out.println("spend time [" + (System.currentTimeMillis() - startTime)/1000 + "] s");
	}


}
