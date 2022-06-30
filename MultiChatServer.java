package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

class EchoThread extends Thread{
    Socket socket;
    Vector<Socket> vec;
    String id = "";
    public static ArrayList<String> userlist = new ArrayList<String>();
    public EchoThread(Socket socket, Vector<Socket> vec){
        this.socket = socket;
        this.vec = vec;
    }
    public void run(){
        BufferedReader br = null;
        PrintWriter pw = null;
        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw=new PrintWriter(socket.getOutputStream(),true);
            String str = null;
 
            id = br.readLine();
            sendMsg(" ← ["+id+"] 님 로그인  →");

            userlist.add(id);
            System.out.println("현재 접속자 " + userlist);
            pw.println("guest"+userlist);
            sendMsg("guest"+userlist);
 

            while(true){
                str = br.readLine();
                if(str.indexOf("로그아웃")!= -1){
                	vec.remove(socket);
                    userlist.remove(id);
                    System.out.println(id+" 접속 종료");
                    System.out.println("현재 접속자 " + userlist);
                    sendMsg("guest"+userlist);
                    sendMsg(str);            
                    break;
                }
                sendMsg(str);
            }
        }catch(IOException ie){
            System.out.println(ie.getMessage());
        }finally{
            try{ 
                if(br != null) br.close();
                if(socket != null) socket.close();
            }catch(IOException ie){
                System.out.println(ie.getMessage());
            }
        }
    }
    
    public void sendMsg(String str){ 
        try{
            for(Socket socket:vec){  
                if(socket != this.socket){
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
                    pw.println(str);
                    pw.flush();           
                }
            }
        }catch(IOException ie){
            System.out.println(ie.getMessage());
        }
    }
}

public class MultiChatServer{
    public static void main(String[] args){
        ServerSocket server = null;
        Socket socket=null;
        Vector<Socket> vec = new Vector<Socket>(); 
        try{
            server=new ServerSocket(3000);
            while(true){
                System.out.println("wait...");
                socket = server.accept();
                vec.add(socket);                       
                new EchoThread(socket, vec).start();
                
            }
        }catch(IOException ie){
            System.out.println(ie.getMessage());
        }
    }
}