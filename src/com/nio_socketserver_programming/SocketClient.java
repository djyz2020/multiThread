package com.nio_socketserver_programming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClient implements Runnable{
	int port; // 服务器端口号
	String addr; // 服务器地址
	Socket socket; // 服务器套接
	
	public SocketClient(String addr, int port) throws IOException{
		this.addr = addr;
		this.port = port;
	}
	
	public BufferedReader getReader(Socket socket) throws IOException{ // 输入流包装
		InputStreamReader socketIn = new InputStreamReader(socket.getInputStream());
		return new BufferedReader(socketIn);	
	}
	public PrintWriter getPrinter(Socket socket) throws IOException{ // 输出流包装
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(this.addr, this.port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("服务器连接成功！");
		BufferedReader in = null; // 输入流
		PrintWriter out = null; // 输出流
		String outMsg = null; // 输出信息
		String inMsg = null; // 输入信息
		Scanner mySc = new Scanner(System.in); // 创建输入对象
		try {
//			in = getReader(socket);
			out = getPrinter(socket);
			while(true){
//				if((outMsg = in.readLine()) != null){ // 输出服务器发来的消息
//					 System.out.println(outMsg);
//				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
			  	}
				if(!(inMsg = mySc.nextLine()).isEmpty()){ // 如果输出流不为空，输出
					out.println(inMsg);	
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();	
		}finally{
			try {
				if(in != null){
					in.close(); // 关闭输入流
				}
				if(out != null){
					out.close(); // 关闭输出流
				}
				if(mySc != null){
					mySc.close();
				}
				if(socket != null){
					socket.close(); // 关闭套接
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
	
    public static void main(String[] args) throws IOException {
    	int port = 8889;
    	String host = "localhost";
    	new Thread(new SocketClient(host, port)).start();
    }
}






