package com.nio_socketserver_programming;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by haibozhang on 2018/12/20.
 */
public class NioClient {
    static SelectorProvider provider = SelectorProvider.provider();
    static Selector selector = null;
    static SocketChannel client = null;
    static boolean close = false;

    private static void write(String msg) throws IOException {
        byte[] bytes = msg.getBytes(Charset.forName("UTF-8"));
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length); // 建立HeapByteBuffer（DirectByteBuffer以后有机会再讨论）
        buffer.put(bytes);
        buffer.flip(); // 切换为读模式
        client.write(buffer);
    }

    private static int read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024); // 分配HeapByteBuffer
        int len = client.read(buffer); // 直到没有数据 || buffer满
        if (len > 0)
            // buffer.array()：取HeapByteBuffer中的原始byte[]
            System.out.println(new String(buffer.array(), 0, len, Charset.forName("UTF-8")));
        return len;
    }

    public static void main(String[] args) throws IOException {
        try {
            selector = provider.openSelector();
            client = provider.openSocketChannel();
            client.configureBlocking(false); // 非阻塞模式
            SelectionKey key = client.register(selector, 0, null); // 注册
            if (client.connect(new InetSocketAddress("127.0.0.1", 8889))) { // 连接成功
                //System.out.println("connected...");
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE); // 监听读就绪和写就绪（准备写数据）
            } else {// 连接失败（正常情况下）
                key.interestOps(SelectionKey.OP_CONNECT); // 监听连接就绪
            }
            while (!close) {
                selector.select(); // 监听就绪事件
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove(); // 从已选择键集移除key
                    if (key.isConnectable()) { // 连接就绪
                        client.finishConnect(); // 完成连接
                        //System.out.println("connected...");
                        // 取消监听连接就绪（否则selector会不断提醒连接就绪）
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ | SelectionKey.OP_WRITE); // 监听读就绪和写就绪
                    } else {
                        if (key.isWritable()) { // 写就绪
                            //System.out.println("write...");
                            write("Hello NioServer! ");
                            // 取消写就绪，否则会一直触发写就绪（写就绪为代码触发）
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                        }
                        if (key.isValid() && key.isReadable()) { // key有效（避免在写就绪时关闭了channel或者取消了key） && 读就绪
                            //System.out.println("read...");
                            if (read() < 0) // 服务器已关闭socket
                                close = true; // 退出循环
                        }
                    }
                }
            }
        } finally {
            if (client != null)
                client.close();
            if (selector != null)
                selector.close();
        }
    }
}
