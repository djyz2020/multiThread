package com.nio_socketserver_programming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class NioSocketClient {
	
	public static void main(String[] args) throws Exception {
		final Socket socket = new Socket("localhost", 8889);
		CountDownLatch countDownLatch = new CountDownLatch(200);
		CountDownLatch countDownLatch1 = new CountDownLatch(200);
		try {
			for(int i = 0; i < 200; i++){
				final int threadName = i;
				new Thread(new Runnable() {
				    BufferedReader reader = null;
					PrintWriter writer = null;
					@Override
					public void run() {
						//向服务器端发送消
						String str = "服务器，你好! 我是客户端" + threadName + "号";
						try {
							writer = new PrintWriter(socket.getOutputStream(), true);
							countDownLatch.await();
							System.out.println(str);
							writer.println(str);
							//接收服务器端消息
//							String line = null;
//							StringBuffer sb = new StringBuffer();
//							reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//							while((line = reader.readLine()) != null) {
//								sb.append(line);
//							}
//							System.out.println("收到服务器端消息：" + sb.toString());
							Thread.currentThread();
							Thread.sleep(500);
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						} finally {
							try {
								if(reader != null) {
									reader.close();
								}
								if(writer != null){
									writer.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							countDownLatch1.countDown();
						}
					}
				}).start();
				countDownLatch.countDown();
			}
			countDownLatch1.await();
			while (true){
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*try {
				if(socket != null){
					socket.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}*/
		}
	}
}


