package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.Socket;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

//@SuppressWarnings("serial")
//class Id extends JFrame implements ActionListener{
//  // ID Frame
//  
//  static JTextField tf = new JTextField(15);
//  JButton btn = new JButton("로그인");
//
//
//
//  WriteThread wt;
//  ClientFrame cf;
//  
//  public Id(){
//      
//  }
//  
//  public Id(WriteThread wt, ClientFrame cf){    ////// Frame 생성
//      super("name?");                    // 제목
//      this.wt = wt;
//      this.cf = cf;
//      
//      Color background=new Color(254,240,27);
//      setLayout(new FlowLayout());    // 레이아웃 변경
//      add(new JLabel("아이디"));            // 텍스트 추가
//      add(tf);                        // 입력창 (input)
//      add(btn);                        // 입력 버튼 
//      tf.registerKeyboardAction(this, "login", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), JComponent.WHEN_FOCUSED);
//      btn.setActionCommand("login");
//      btn.addActionListener(this);    // 익명 클래스로 버튼 이벤트를 추가(actionPerformed)
//      setBounds(300,300,250,120);        // 프레임 크기 조절
//      setBackground(background);
//      setVisible(true);                // 투명화 여부
//  }
//  
//  public void actionPerformed(ActionEvent e) {////// 익명 클래스 부분(id 입력에 대한 action)
//      if(e.getActionCommand() == "login"){
//          if(tf.getText().equals("")){        // 입력된 텍스트가  공백이면 값을 반환하지 않음
//              return;
//          }
//           
//          wt.sendMsg();                // 메세지 전송(ID)
//          cf.isFirst = false;            // 채팅방 입장 여부
//          cf.setVisible(true);        // 투명화 여부
//          this.dispose();                // 현재의 frame 종료
//      }
//  }
//  
//  public static String getId(){
//      return tf.getText();            // 입력한 ID 반환 
//  }
//}



@SuppressWarnings("serial")
public class ClientFrame extends JFrame implements ActionListener{    // Chatting Frame

	JTextArea txtA = new JTextArea();
	JTextField txtF = new JTextField(22);

	JPanel p1 = new JPanel();
	JPanel p2 = new JPanel();
	static JPanel p3 = new JPanel();
  
	RoundedYButton btnTransfer = new RoundedYButton("►");
	RoundedButton btnExit = new RoundedButton("Exit");
	RoundedButton btnList = new RoundedButton("≡");
  
	boolean isFirst = true;                        // 채팅방 입장 여부
	boolean ListOpen = false;
  
	WriteThread wt;
	Socket socket;
	EchoThread et;
	  
	public String sendName = "";
	public static String UserList[] = {};

	public ClientFrame(Socket socket, String name){	////// Frame 생성
		super("Work Talk");							// 제목
		sendName = name;
		this.socket = socket;
		wt = new WriteThread(this);
//      new Id(wt, this);
		JScrollPane js = new JScrollPane(txtA);
		Color background=new Color(155,187,212);
  
		add("Center",js);							// 전체 화면
      
		p1.add(txtF);								// 채팅 입력 화면
		p1.add(btnTransfer);						// 전송 버튼
		add("South", p1);							// p1에 대한 컨포넌트를 남쪽에 배치


		p2.setBackground(background);
		p2.setLayout(new BorderLayout());
		p2.add(btnList, BorderLayout.WEST);
		p2.add(btnExit, BorderLayout.EAST);
		add("North", p2);
      
//		String[] List = et.userlist.toArray(new String[et.userlist.size()]);
//		JList userList = new JList(List); 
//		p3.add(userList);
		JList userList = new JList(UserList); 
		p3.add(userList);
		p3.setVisible(false);
		p3.setPreferredSize(new Dimension(70,100));
		add("West",p3);
      
		txtA.setLineWrap(true);						// 스크롤
		txtA.setEditable(false);					// readonly
		txtF.setFocusable(true);					// 포커스 대기 상태
		txtA.setBackground(background);				// 배경 색상
		txtF.registerKeyboardAction(this, "talk", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), JComponent.WHEN_FOCUSED);
		btnTransfer.setActionCommand("talk");		// 전송 버튼에 talk 이라는 커맨드 행동을 지정
		btnTransfer.addActionListener(this);		// 익명 클래스로 전송 버튼 이벤트를 추가(actionPerformed)
		btnExit.addActionListener(this);			// 익명 클래스로 종료 버튼 이벤트를 추가(actionPerformed)
		btnList.addActionListener(this);			// 익명 클래스로 목록 버튼 이벤트를 추가(actionPerformed)
      
		setDefaultCloseOperation(EXIT_ON_CLOSE);	// 프로그램 정상 종료
		setBounds(300,300,350,600);					// 프레임 크기 조정
		setVisible(true);							// 프레임 투명화 여부
      
	}
  
	public void actionPerformed(ActionEvent e) {	////// 익명 클래스 부분 (채팅 입력에 대한 action)
		String id = sendName;						// 입력한 아이디 가져오기
		  
		if(e.getActionCommand() == "talk"){			// ActionCommand가 talk면 참
		if(txtF.getText().equals("")){				// 입력된 텍스트가  공백이면 값을 반환하지 않음
			return;
		}
		txtA.append(" ["+id+"] "+txtF.getText()+"\n");
		wt.sendMsg();								// 메세지 전송
		txtF.setText("");							// 텍스트 필드 초기화
		}
		else if(e.getSource()==btnExit){			// 이벤트를 발생시킨 객체의 위치값이 btnExit이면 참
		wt.sendExitMsg();
		this.dispose();								// 현재의 frame 종료
		}
		else if(e.getSource()==btnList){			// 이벤트를 발생시킨 객체의 위치값이 btnList이면 참
			if (ListOpen) {
				p3.setVisible(false);
				ListOpen=false;
			} else {
				p3.setVisible(true);
				ListOpen=true;
			}
		}
	}
	
	public class RoundedButton extends JButton {
	public RoundedButton() { super(); decorate(); } 
	public RoundedButton(String text) { super(text); decorate(); } 
	public RoundedButton(Action action) { super(action); decorate(); } 
	public RoundedButton(Icon icon) { super(icon); decorate(); } 
	public RoundedButton(String text, Icon icon) { super(text, icon); decorate(); } 
	protected void decorate() { setBorderPainted(false); setOpaque(false); }
	@Override 
		protected void paintComponent(Graphics g) {
			Color c=new Color(155,187,212);	//배경색 결정
			Color o=new Color(0,0,0);		//글자색 결정
			int width = getWidth(); 
			int height = getHeight(); 
			Graphics2D graphics = (Graphics2D) g; 
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
			if (getModel().isArmed()) { graphics.setColor(c.darker()); } 
			else if (getModel().isRollover()) { graphics.setColor(c); } 
			else { graphics.setColor(c); } 
			graphics.fillRoundRect(0, 0, width, height, 10, 10); 
			FontMetrics fontMetrics = graphics.getFontMetrics(); 
			Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds(); 
			int textX = (width - stringBounds.width) / 2; 
			int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent(); 
			graphics.setColor(o); 
			graphics.setFont(getFont()); 
			graphics.drawString(getText(), textX, textY); 
			graphics.dispose(); 
			super.paintComponent(g); 
		}
	}
	
	public class RoundedYButton extends JButton {
		public RoundedYButton() { super(); decorate(); } 
		public RoundedYButton(String text) { super(text); decorate(); } 
		public RoundedYButton(Action action) { super(action); decorate(); } 
		public RoundedYButton(Icon icon) { super(icon); decorate(); } 
		public RoundedYButton(String text, Icon icon) { super(text, icon); decorate(); } 
		protected void decorate() { setBorderPainted(false); setOpaque(false); }
		@Override 
			protected void paintComponent(Graphics g) {
				Color c=new Color(254,240,27);	//배경색 결정
				Color o=new Color(0,0,0);		//글자색 결정
				int width = getWidth(); 
				int height = getHeight(); 
				Graphics2D graphics = (Graphics2D) g; 
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
				if (getModel().isArmed()) { graphics.setColor(c.darker()); } 
				else if (getModel().isRollover()) { graphics.setColor(c); } 
				else { graphics.setColor(c); } 
				graphics.fillRoundRect(0, 0, width, height, 10, 10); 
				FontMetrics fontMetrics = graphics.getFontMetrics(); 
				Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds(); 
				int textX = (width - stringBounds.width) / 2; 
				int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent(); 
				graphics.setColor(o); 
				graphics.setFont(getFont()); 
				graphics.drawString(getText(), textX, textY); 
				graphics.dispose(); 
				super.paintComponent(g); 
			}
		}
}