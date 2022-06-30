package chat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

class WriteThread{
	Socket socket;
	ClientFrame cf;
	String str;
	String id;
    
	public WriteThread(ClientFrame cf) {
		this.cf = cf;
		this.socket = cf.socket;
	}
    
	public void sendMsg() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                                                               
		PrintWriter pw = null;
		try{
			pw=new PrintWriter(socket.getOutputStream(),true);
			id = cf.sendName;
			str=" ["+id+"] "+cf.txtF.getText();
			pw.println(str);                                    
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br!=null) br.close();
			}catch(IOException ie) {
				System.out.println(ie.getMessage());
			}
		}
	}

	public void sendExitMsg() {
		PrintWriter pw = null;
		try{
			id = cf.sendName;
			pw=new PrintWriter(socket.getOutputStream(),true);
			str=" ← ["+id+"] 님 로그아웃 →";
			pw.println(str);					
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

class ReadThread extends Thread{
	Socket socket;
	ClientFrame cf;

	public ReadThread(Socket socket, ClientFrame cf){
		this.cf = cf;
		this.socket = socket;
	}
	    
	public void run() {
		BufferedReader br = null;
		PrintWriter pw = null;
		String list = "";
		try{
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw=new PrintWriter(socket.getOutputStream(),true);
			pw.println(cf.sendName);
            
			while(true){
				String str=br.readLine();            
				if(str==null){
					System.out.println("Disconnected...");
					break;
				}
				else if(str.indexOf("guest")!= -1){
					list = str.substring(5);
					System.out.println(list);
					cf.txtA.append("접속자 : "+list+"\n");
				}else{
					cf.txtA.append(str+"\n");           
					cf.txtA.setCaretPosition(cf.txtA.getDocument().getLength());
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br!=null) br.close();
				if(socket!=null) socket.close();
			}catch(IOException ie){}
		}
	}
}

public class MultiChatClient {
	
	public static String clientNm="";
	
	public static void loginForm(){
		JFrame fram = new JFrame();
		JTextField tff = new JTextField(15);
	    JButton btn = new JButton("로그인");
	    Color background=new Color(254,240,27);
	    fram.setTitle("name?");
	    fram.setLayout(new FlowLayout());		// 레이아웃 변경
	    fram.add(new JLabel("아이디"));			// 텍스트 추가
	    fram.add(tff);							// 입력창 (input)
	    fram.add(btn);							// 입력 버튼 
        btn.setActionCommand("login");
        fram.setBounds(300,300,250,120);		// 프레임 크기 조절
        fram.setBackground(background);
        fram.setVisible(true);					// 투명화 여부
        
        btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientNm = tff.getText();
				fram.setVisible(false);
				fram.dispose();
				testobj();
			}
		});
		
	}
	
	public static void testobj(){
		Socket socket=null;
        ClientFrame cf = null;
        
        try{
            socket=new Socket("127.0.0.1",3000);
            System.out.println("connected..");
            cf = new ClientFrame(socket,clientNm);
            new ReadThread(socket,cf).start();
            System.out.println("접속한 이름 : "+clientNm);
        }catch(IOException ie){
            System.out.println(ie.getMessage());
        }
	}
	
    public static void main(String[] args) {
    	loginForm();
    }
}