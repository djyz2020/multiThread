package com.nio_socketserver_programming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
	/**
	 * 网络版坦克大战服务器（支持多服务端）
	 * @param args
	 */
	private int port = 8889; // 游戏服务器端口
	private ServerSocket serverSocket; // 游戏服务器套接
	private ExecutorService executorService; // 线程池
	private final int POOL_SIZE = 100; // 单个CPU线程池大小
	public static int clientNum; // 在线客户端数量
	public static ArrayList<String> clientAddrList; // 在线客户端信息
	
	public SocketServer() throws IOException{
		serverSocket = new ServerSocket(port); // 创建服务器套接对象
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE); // 多并发线程池
		System.out.println("..............................【网络版坦克大战游戏服务器已启动】..............................");
		clientAddrList = new ArrayList<String>(); // 在线客户端信息	
		clientNum = 0; // 在线客户数量
		
	}
	public void service() throws IOException{
		Socket socket= null;
		while(true){
			socket = serverSocket.accept(); // (阻塞状态) 等待客户端连接
			clientAddrList.add(socket.getInetAddress()+":"+socket.getPort()); // 将客户端的信息放入集合
			clientNum++; // 客户端人数加1
			executorService.execute(new Handler(socket,clientAddrList)); // 为客户端开辟线程
			
		}
	}
	
	public static void main(String[] args) {
		try {
			new SocketServer().service();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Handler implements Runnable{
	
	private Socket socket;
	private ArrayList<String> clientAddr;
	
	public Handler(Socket socket,ArrayList<String> clientAddr) {
		this.socket = socket;
		this.clientAddr = clientAddr;
	}
	
	public BufferedReader getReader(Socket socket) throws IOException{ // 输入流
		InputStreamReader socketIn = new InputStreamReader(socket.getInputStream());
		return new BufferedReader(socketIn);	
	}
	
	public PrintWriter getPrinter(Socket socket) throws IOException{ // 输入流
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut,true);
	}
	
	public String echo(String msg){
		return "echo:" + msg;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			System.out.println("客户端[" + socket.getInetAddress()+":"+socket.getPort() + "]: 连接成功！");
			System.out.println("当前共有" + SocketServer.clientNum + "个客户端在线！");
			in = getReader(socket);
			out = getPrinter(socket);
			String client_message = null;
			out.println("当前共有" + SocketServer.clientNum + "个客户端在线！");
			while(true){
				out.println("服务器：请输入您想说的话....");
				if((client_message = in.readLine()) != null){
					System.out.println("客户端[" + socket.getInetAddress()+":"+socket.getPort() + "]消息: "
							+ client_message);
				}
				try {
					Thread.sleep(20);
	              } catch (InterruptedException e) {
					e.printStackTrace();
	              }
			}
		} catch (IOException e) {
			try {
				in.close();
				out.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}finally{
			if(socket != null){
				try {
					// 显示下线的客户端
					System.out.println("客户端：[" + socket.getInetAddress() + ":" + socket.getPort() + "]: 已下线！");
					// 显示剩余在线人数
					System.out.println("剩余在线人数：" + (SocketServer.clientAddrList.size()-1) + "人！");
					// 客户端在线人数减1
					SocketServer.clientNum--;
					// 关闭socket
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SocketServer.clientAddrList.remove(clientAddr.size()-1); // 移除下线客户信息
		}		
	}
}







