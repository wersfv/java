package Client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ChatClient {
	private static final String SERVER_IP = "192.168.0.6";
	private static final int SERVER_PORT = 5000;
	static String name = null;

	public static void main(String[] args) {
		
		NickName();
	}

	private static void NickName() {
		JFrame frame;
		JTextField textField;

		frame = new JFrame("대화명 입력");
		frame.setBounds(100, 100, 300, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		textField.setBounds(25, 40, 123, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton comfirmButton = new JButton("확인");
		comfirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.getText().equals("")) {
					name = textField.getText();
					join();
					frame.dispose();
				} else {
					JOptionPane a=new JOptionPane(); 
					a.showMessageDialog(null, "대화명을 한 글자 이상 입력해 주세요");
				}
			}
		});
		comfirmButton.setBounds(160, 40, 97, 23);
		frame.getContentPane().add(comfirmButton);
		
		textField.addKeyListener( new KeyAdapter() {
	            public void keyReleased(KeyEvent e) {
	                char keyCode = e.getKeyChar();
	                if (keyCode == KeyEvent.VK_ENTER) {
	                	if(!textField.getText().equals("")) {
	    					name = textField.getText();
	    					join();
	    					frame.dispose();
	    				} else {
	    					JOptionPane a=new JOptionPane(); 
	    					a.showMessageDialog(null, "대화명을 한 글자 이상 입력해 주세요");
	    				}
	                }
	            }
	        });
		frame.setVisible(true);
	}
	
	private static void join() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			consoleLog("채팅방목록에 입장하였습니다.");
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			String request = "join:" + name + "\r\n";
			pw.println(request);
			pw.flush();
			
			new cho_room(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void consoleLog(String log) {
		System.out.println(log);
	}


}