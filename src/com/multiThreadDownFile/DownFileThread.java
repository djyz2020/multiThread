package com.multiThreadDownFile;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

// 下载线程类
class DownFileThread extends Thread {
    private long startPos;
    private RandomAccessFile raf;
    private long size;
    private long length;
    private String remotePath;

    public DownFileThread(String remotePath, long startPos, RandomAccessFile raf, long size) {
        this.remotePath = remotePath;
        this.startPos = startPos;
        this.raf = raf;
        this.size = size;
    }

    public void run() {
    	InputStream in = null;
        try {
        	URL url = new URL(remotePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
            conn.setRequestMethod("GET");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("accept", "*/*");

            in = conn.getInputStream();
            in.skip(this.startPos);
            byte[] buf = new byte[1024];
            int hasRead = 0;
            while (length < size && (hasRead = in.read(buf)) != -1) {
                raf.write(buf, 0, hasRead);
                length += hasRead;
            }
           
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		 if(raf != null){
        			 raf.close();
        		 }
        		 if(in != null){
        			 in.close();
        		 }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    }

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public RandomAccessFile getRaf() {
		return raf;
	}

	public void setRaf(RandomAccessFile raf) {
		this.raf = raf;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
    
    
    
}

