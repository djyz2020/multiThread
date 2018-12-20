package com.nio_socketserver_programming;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by haibozhang on 2018/12/20.
 */
public class NioServer {
    static SelectorProvider provider = SelectorProvider.provider();
    static Selector selector = null;
    static ServerSocketChannel server = null;

    private static void accept() throws IOException {
        SocketChannel channel = null;
        try {
            channel = server.accept(); // 接受连接
            channel.configureBlocking(false); // 非阻塞模式
            channel.register(selector, SelectionKey.OP_READ, null); // 监听读就绪
        } catch (IOException e) {
            if (channel != null)
                channel.close();
        }
    }

    private static int read(SocketChannel channel) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024); // 分配HeapByteBuffer
            int len = channel.read(buffer); // 直到没有数据 || buffer满
            if (len > 0)
                // buffer.array()：取HeapByteBuffer中的原始byte[]
                System.out.println(new String(buffer.array(), 0, len, Charset.forName("UTF-8")));
            return len;
        } catch (IOException e) {
            if (channel != null)
                channel.close();
            return -1;
        }
    }

    private static void write(SocketChannel channel, String msg) throws IOException {
        try {
            byte[] bytes = msg.getBytes(Charset.forName("UTF-8"));
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length); // 分配HeapByteBuffer
            buffer.put(bytes);
            buffer.flip(); // 切换为读模式
            channel.write(buffer);
        } catch (IOException e) {
            if (channel != null)
                channel.close();
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            selector = provider.openSelector();
            server = provider.openServerSocketChannel();
            server.configureBlocking(false); // 非阻塞模式
            SelectionKey key = server.register(selector, 0, null); // 注册
            if (server.bind(new InetSocketAddress(8888)).socket().isBound()) // 绑定成功
                key.interestOps(SelectionKey.OP_ACCEPT); // 监听连接请求
            while (true) {
                selector.select(); // 监听就绪事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove(); // 从已选择键集中移除key
                    if (key.isAcceptable()) { // 连接请求到来
                        //System.out.println("accept...");
                        accept();
                    } else {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (key.isWritable()) { // 写就绪
                            // System.out.println("write...");
                            write(channel, "Hello NioClient!");
                            // 取消写就绪，否则会一直触发写就绪（写就绪为代码触发）
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                            key.channel().close(); // 关闭channel（key将失效）
                        }
                        if (key.isValid() && key.isReadable()) { // key有效（避免在写就绪时关闭了channel或者取消了key） && 读就绪
                            // System.out.println("read...");
                            int len = read(channel);
                            if (len >= 0)
                                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE); // 写就绪，准备写数据
                            else if (len < 0) // 客户端已关闭socket
                                channel.close(); // 关闭channel（key将失效）
                        }
                    }
                }
            }
        } finally {
            if (server != null)
                server.close();
            if (selector != null)
                selector.close();
        }
    }
}
