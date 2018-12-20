package com.nio_socketserver_programming;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NioSocketServer {
	public static ThreadPoolExecutor executor = new ThreadPoolExecutor(6, 10, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	public static SelectorProvider provider = SelectorProvider.provider();
	public static ServerSocketChannel serverSocketChannel = null;
	public static Selector selector;

	public static void main(String[] args) throws IOException {
		//服务器端开端口
		serverSocketChannel = ServerSocketChannel.open();
		//非阻塞
		serverSocketChannel.configureBlocking(false);
		//绑定端口
		serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8889));

		//Selector 选择器，根据指定的条件选择东西
		selector = Selector.open();
		//selector = provider.openSelector();
		
		//选取操作系统底层 保存那些socket连接
		//在serverSocketChannel注册一个刚刚获取的选择器6
		//增加一个条件： OP_ACCEPT 新连接
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		while(true){
			//根据条件去查询数量： 有数据，没数据
			//查询超时时间：如果100毫秒内还没有查询结果，继续执行下面的代码
			selector.select();
			Set<SelectionKey> selected = selector.selectedKeys();
			Iterator<SelectionKey> iter = selected.iterator();
			while(iter.hasNext()){
				//如果Accept
				SelectionKey key = iter.next();
				//如果Acceptable，符合我们上面的条件，需要我们处理。新连接
				if(key.isAcceptable()){
					SocketChannel channel = serverSocketChannel.accept();
					channel.configureBlocking(false);
					channel.write(ByteBuffer.wrap("欢迎连接NIO服务器登录".getBytes()));
					//暂时不应该丢给线程池处理
					//channel注册selector
					//条件OP_READ，有数据传输
					channel.register(selector, SelectionKey.OP_READ);
				}else if(key.isValid() && key.isReadable()){ //代表有数据连接了
					//取出有数据传输的socket连接
					//取出有数据传输的socket连接
					SocketChannel channel = (SocketChannel) key.channel();
					channel.configureBlocking(false);
					//请求读取内容
					ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
					channel.read(requestBuffer);
					//转化为读数据的模式
					requestBuffer.flip();
					String requestData = new String(requestBuffer.array());
					System.out.println("收到信息数据："+ requestData +", 当前线程数：" + NioSocketServer.executor.getActiveCount());
					requestBuffer.clear();

					String reponseData = "hello, nioclient" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					channel.register(NioSocketServer.selector, SelectionKey.OP_WRITE, reponseData);
				}else if(key.isWritable()){
					SocketChannel channel = (SocketChannel) key.channel();
					channel.configureBlocking(false);
					channel.write(ByteBuffer.wrap(key.attachment().toString().getBytes()));
					channel.register(NioSocketServer.selector, SelectionKey.OP_READ, null);
					if(channel != null){
						channel.close();
					}
				}
			}
			//清空查询内容，防止重复处理
			selected.clear();
			//选择可以IO操作的keys，清除正在处理的连接，不再查询正在处理的连接
			selector.selectNow();
		}
	}
	
}
