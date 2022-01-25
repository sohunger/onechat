package com.huang;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientChat extends JFrame {
    private JTextArea ta =  new JTextArea(10,20); //设置一个10行，20列的文本域
    private JTextField tf = new JTextField(20); //设置一个20列文本框
    private Socket socket = null;
    private DataOutputStream dos= null;
    private static boolean isConn =false;

    public ClientChat() throws HeadlessException{}

    public void init(){
        this.setTitle("客户端窗口"); //设置窗口标题
        this.add(ta, BorderLayout.CENTER); //文本域
        this.add(tf,BorderLayout.SOUTH); //文本框
        this.setBounds(300,300,300,400); // 设置窗口大小
        tf.requestFocus(); // 光标聚焦
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ta.setEditable(false); //设置显示框不能输入
        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sendMsg = tf.getText(); //获取输入框内容
                if(sendMsg.trim().length() == 0){
                    return;
                }
                send(sendMsg);
                ta.append("我："+sendMsg+"\n"); //将输入框的内容打印在显示款
                tf.setText(""); //清空输入框
            }
        });
        try {
            socket = new Socket("127.0.0.1",8888);
                isConn = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
        new Thread(new receiveMsg()).start();
    }

    //输出消息
    public void send(String msg){
        try {
            dos = new java.io.DataOutputStream(socket.getOutputStream());
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //接收消息
    class receiveMsg implements Runnable{

        public void run() {
                try {
                    while(isConn){
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    ta.append(dis.readUTF());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }
    public static void main(String[] args) {
        ClientChat clientChat = new ClientChat();
        clientChat.init();
    }
}
