package com.city.esdemo2.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * io机制
 */
public class IoTest {
    ExecutorService poolExecutor;
    IoTest(){
        initPool();
    }
    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(8001)){
            
        }catch (IOException e){

        }
    }
    /**
     * 同步阻塞
     */
    public void bio(ServerSocket server) throws IOException {
        Socket client = server.accept();
        InputStream inputStream = client.getInputStream();
        byte[] bytes=new byte[1024];
        int len=0;
        StringBuilder sb = new StringBuilder();
        while((len=inputStream.read(bytes))!=-1){
            sb.append(new String(bytes,0,len,"utf-8"));
        }
        client.close();
    }
    /**
     * 多线程的bio
     */
    public void concurrentBIO(ServerSocket server) throws IOException{
        Socket client = server.accept();
        //开启线程处理io
        poolExecutor.submit(()->{
            try {
                bio(server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 创建线程池
     */
    public void initPool(){
        poolExecutor= Executors.newSingleThreadExecutor();
    }

}
