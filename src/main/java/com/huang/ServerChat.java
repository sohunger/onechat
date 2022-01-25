package com.huang;

import sun.rmi.runtime.NewThreadAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerChat extends JFrame {
    private JTextArea serverTa = new JTextArea();
    private JPanel btnTool = new JPanel();
    private JButton stopBtn = new JButton("停止");
    private static ServerSocket serverSocket = null;
    private DataInputStream dis= null;
    private ArrayList<receiveThread> receiveThreads = new ArrayList<receiveThread>();
    private  boolean isStart = false;

    //窗口监听

    public void init(){
        this.setTitle("服务器窗口");
        this.add(serverTa, BorderLayout.CENTER);
        btnTool.add(stopBtn);
        this.add(btnTool,BorderLayout.SOUTH);
        this.setBounds(0,0,500,500);
        serverTa.setEditable(false);
        this.setDefaultCloseOperation(3);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isStart =false;

                    try {
                        if(serverSocket != null) {
                        serverSocket.close();
                        }
                        System.out.println("服务器断开"+"\n");
                        System.exit(0);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
        });

        //停止按钮监听
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isStart = false;
                System.out.println("停止按钮");
                stopServer();
            }
        });
        this.setVisible(true);
        //因为顺序执行，所以如果把这个放在启动按钮监听器中，就会一直阻塞阻塞在监听其中。
        startServer();
    }

    public void ServerChat(){

    }

    //服务器启动方法
    public void startServer()  {
        isStart = true;
        if(serverSocket == null){
            try {
                serverSocket = new ServerSocket(8888);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while(isStart){
            try {
                System.out.println("启动中。。。。。。");
                Socket socket = new Socket();
                System.out.println("创建出接收器");
                socket = serverSocket.accept();
                System.out.println("接收成功");
                receiveThreads.add(new receiveThread(socket));
                serverTa.append("一个客户端连接服务器"+socket.getInetAddress()+"/"+socket.getPort()+"\n");
            }catch (SocketException e){

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //停止方法
    public void stopServer(){
            try {
                if (serverSocket != null){
                    serverSocket.close();
                }
                serverTa.append("服务器已经关闭"+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //改为多线程接收
    class receiveThread implements Runnable{
        Socket socket =null;
        public receiveThread(Socket socket){
            this.socket = socket;
            new Thread(this).start();
        }
        public void run() {
            try {
                while(isStart){
                    dis = new DataInputStream(this.socket.getInputStream());
                    String s = dis.readUTF();
                    serverTa.append(socket.getPort()+"："+s+"\n");
                    String msg = socket.getPort()+"："+s+"\n";
                    for (receiveThread thread : receiveThreads) {
                        //如果这个线程连接是当前发过来的客户端，那么就忽略掉
                        if(this.socket == thread.socket){
                            continue;
                        }
                        thread.send(msg);
                    }
                }
            }catch (SocketException e){
                System.out.println(socket.getPort()+"下线了");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        //发送方法
        public void send(String msg){
            try {
                DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
                dos.writeUTF(msg);
            }catch (SocketException e){
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
}
    public static void main(String[] args) {
        ServerChat serverChat = new ServerChat();
        serverChat.init();
    }
}
