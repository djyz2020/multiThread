package com.multiThreadDownFile;

import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

/**
 * @author haibozhang 多线程下载文件
 * @serial 1.0.0
 * 2018.3.19
 */
public class MultiThreadDownload {
    private String remotePath; 			// 远程资源路径
    private String localPath; 			// 本地存储路径
    private DownFileThread[] threads; 	// 线程list
    private int threadNum; 				// 线程数量
    private long length; 				// 下载的文件大小

    // 构造初始化
    public MultiThreadDownload(String remotePath, String localPath, int threadNum) {
        this.remotePath = remotePath;
        this.localPath = localPath;
        this.threads = new DownFileThread[threadNum];
        this.threadNum = threadNum;
    }

    // 多线程下载文件资源
    @SuppressWarnings("resource")
	public void download() {
        try {
        	URL url = new URL(remotePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
            conn.setRequestMethod("GET");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("accept", "*/*");

            // 获取远程文件的大小
            length = conn.getContentLength();
            conn.disconnect();

            //设置文件大小
            new RandomAccessFile(localPath, "rw").setLength(length);
            
            // 每个线程下载大小
            long avgPart = length / threadNum;
            // 下载文件
            for (int i = 0; i < threadNum; i++) {
                long startPos = avgPart * i;
                RandomAccessFile targetFile = new RandomAccessFile(localPath, "rw");
                targetFile.seek(startPos); // 分段下载
                threads[i] = new DownFileThread(remotePath, startPos, targetFile, avgPart);
                threads[i].start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 监控下载进度
    public double getDownRate() {
        int currentSize = 0;
        for (int i = 0; i < threadNum; i++) {
            currentSize += threads[i].getLength();
        }
        return currentSize * 1.0 / length;
    }

    // 测试
    public static void main(String[] args) {
    	//String remotePath = args[0]; //远程资源路径
    	//String localPath = args[1];  //本地存储路径
        String remotePath = "http://localhost:8080/Web01/spring-session-redis-data-demo(20180318).rar";
        String localPath = "C:/Users/haibozhang/Desktop/test/test.rar";
        final MultiThreadDownload download = new MultiThreadDownload(remotePath, localPath, 4);
        download.download();
        
        long startTime = System.currentTimeMillis();
        CountDownLatch cdl = new CountDownLatch(1);
        // 主线程负责下载文件，在启动一个线程负责监控下载的进度
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (download.getDownRate() < 1) {
                	Double downRate = download.getDownRate()*100;
                	DecimalFormat df = new DecimalFormat("#.00");
                    System.out.println("已下载：" + df.format(downRate) + "%");
                    try {
                        Thread.sleep(200); // 200毫秒扫描一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("已下载：100%，下载完成！");
                cdl.countDown();
            }

        }).start();
        
        try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        long time = System.currentTimeMillis() - startTime;
        System.out.println("下载消耗时间: " + time + "ms, 合计: " + time/1000 + "s");
    }
}


