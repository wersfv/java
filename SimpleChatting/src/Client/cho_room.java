package Client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class cho_room {

	private JFrame frame;
	private DefaultListModel model = new DefaultListModel();
	private Socket socket;
	private String room_name;
	
	public cho_room(Socket socket) {
		this.socket = socket;
		initialize();
		frame.setVisible(true);
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.EAST);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JList list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getClickCount() == 2) {
					join_room(socket, room_num_search((String) list.getSelectedValue()));
					new ChattingWindow(socket, (String) list.getSelectedValue());
					frame.dispose();
				}
				
			}
		});
		scrollPane.setViewportView(list);
		set_room_list(list,model);
		JButton refresh = new JButton("\uC0C8\uB85C\uACE0\uCE68");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				set_room_list(list,model);
			}
		});
		JButton create_room = new JButton("\uBC29 \uC0DD\uC131");
		create_room.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RoomName(frame);	
			}
		});
		GridBagConstraints gbc_create_room = new GridBagConstraints();
		gbc_create_room.insets = new Insets(0, 0, 5, 0);
		gbc_create_room.gridx = 0;
		gbc_create_room.gridy = 0;
		panel_1.add(create_room, gbc_create_room);
		
		
		GridBagConstraints gbc_refresh = new GridBagConstraints();
		gbc_refresh.gridx = 0;
		gbc_refresh.gridy = 1;
		panel_1.add(refresh, gbc_refresh);
	}
	
	private void set_room_list(JList list,DefaultListModel model) {
		
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			pw.println("send_room_list\r\n");
			pw.flush();
			model.removeAllElements();
			String num = br.readLine();
			System.out.println("room_num = " + num);
			System.out.println(Integer.parseInt(num));
			for(int i = 0 ; i < Integer.parseInt(num) ; i++) {
				System.out.println(br.readLine());
				String room_name = br.readLine();
				System.out.println(room_name);
				model.addElement(room_name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.setModel(model);
		
	}
	
	public void join_room(Socket socket, int roomnum) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
			pw.println("join_room:" + roomnum + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int room_num_search(String name) {
		PrintWriter pw;
		int room_num = 0;
        try {
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String request = "room_num_search:" + name + "\r\n";
            pw.println(request);
            
            room_num =  Integer.parseInt(br.readLine());
           
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return room_num;
	}
	
	private void RoomName(JFrame fra) {
		JFrame frame;
		JTextField textField;
		

		frame = new JFrame("대화방 이름 입력");
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
					room_name = textField.getText();
					try {
						PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
						
						pw.println("create_room:" + room_name + "\r\n");
						pw.flush();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					frame.dispose();
					fra.dispose();
					join_room(socket, room_num_search(room_name));
					new ChattingWindow(socket, room_name);
					
					
				} else {
					JOptionPane a=new JOptionPane(); 
					a.showMessageDialog(null, "대화방 이름을 한 글자 이상 입력해 주세요");
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
    					room_name = textField.getText();
    					try {
    						PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    						
    						pw.println("create_room:"+room_name+"\r\n");
    						pw.flush();
    						
    					} catch (IOException e1) {
    						e1.printStackTrace();
    					}
    					frame.dispose();
    					fra.dispose();
    					join_room(socket, room_num_search(room_name));
    					new ChattingWindow(socket, room_name);
    					
    				} else {
    					JOptionPane a=new JOptionPane(); 
    					a.showMessageDialog(null, "대화방 이름을 한 글자 이상 입력해 주세요");
    				}
                }
            }
        });
		
		frame.setVisible(true);
	}

}
