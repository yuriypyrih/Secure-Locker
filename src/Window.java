import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


public class Window extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = -8255319694373975038L;
	private LockerManager manager;
	private String[] array_str_files;
	DefaultListModel model;
	//private SearchTable search_table;

	private JFrame frame;
	private JPanel mainPanel, mainPanel_center, mainPanel_right;
	private JLabel lb_description, lb_testing;
	private JButton btn_open, btn_add, btn_delete, btn_encrypt , btn_decrypt;
	private JList<String> list_files;
	private JScrollPane listScroller;
	
	
	private int HEIGHT;
	private int WIDTH;
	
	GridBagConstraints gbc = new GridBagConstraints();
	
	
	
	//Constructor of Window
	public Window(int width, int height, String title, LockerManager manager) {
		
				this.HEIGHT = height;
				this.WIDTH = width;
		
				this.manager = manager;
				frame = new JFrame(title);
				frame.setSize(width,height);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);	
				frame.setVisible(true);
			
				
				
				
				// Creating the components
				
				mainPanel = new JPanel(new BorderLayout());
				mainPanel_center = new JPanel(new GridBagLayout());
				mainPanel_right = new JPanel(new BorderLayout());
				
				
				
				
				//Creating mainPanel_center
				lb_description = new JLabel("Greetings, " + manager.getUserName());
				btn_open = new JButton("Open");
				btn_open.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	
		            	String fileName = list_files.getSelectedValue();
		            	if(fileName != null) {
		            		manager.openFile(fileName);
		            	}else {
		            		System.out.println("Warning : You must select a file first!");
		            	}
		            	
		            }
		        });
				btn_add = new JButton("Add");
				btn_add.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	manager.addFile();
		            	System.out.println("File added");
		            	updateList();
		            }
		        });
				btn_delete = new JButton("Delete");
				btn_delete.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	
		            	String fileName = list_files.getSelectedValue();
		            	if(fileName != null) {
		            		manager.deleteFile(fileName);
		            		System.out.println("File deleted");
		            		updateList();
		            	}else {
		            		System.out.println("Warning : You must select a file first!");
		            	}
		            	
		            }
		        });
				btn_encrypt = new JButton("Encrypt");
				btn_encrypt.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	
		            	String fileName = list_files.getSelectedValue();
		            	if(fileName != null) {
		            		try {
								manager.encryptFile(fileName);
								System.out.println("File Encrypted");
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		            		
		            		updateList();
		            	}else {
		            		System.out.println("Warning : You must select a file first!");
		            	}
		            	
		            }
		        });
				btn_decrypt = new JButton("Decrypt");
				btn_decrypt.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		            	
		            	String fileName = list_files.getSelectedValue();
		            	if(fileName != null) {
		            		try {
								manager.decryptFile(fileName);
								System.out.println("File Decrpted");
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		            		
		            		updateList();
		            	}else {
		            		System.out.println("Warning : You must select a file first!");
		            	}
		            	
		            }
		        });
				
				
				gbc.insets = new Insets(6,6,6,6);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridy = 0;
				
				mainPanel_center.add(lb_description, gbc );
				gbc.gridy = 1;
				mainPanel_center.add(btn_open, gbc );
				gbc.gridy = 2;
				mainPanel_center.add(btn_add, gbc );
				gbc.gridy = 3;
				mainPanel_center.add(btn_delete, gbc );
				gbc.gridy = 4;
				mainPanel_center.add(btn_encrypt, gbc );
				gbc.gridy = 5;
				mainPanel_center.add(btn_decrypt, gbc );
				
				
				
				//Creating mainPanel_right
				updateList();
				
				

			
				
				//Adding everything together
				
				mainPanel.add(mainPanel_center, BorderLayout.CENTER);
				mainPanel.add(mainPanel_right, BorderLayout.LINE_END);
				frame.add(mainPanel);
				frame.getContentPane().validate();
				frame.getContentPane().repaint();
				
	}//end of Constructor








	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateList() {
		array_str_files = manager.getStringFile();
		
		model = new DefaultListModel();
		model.clear();
		
		for(int i = 0; i < array_str_files.length ; i++) {
			model.addElement(array_str_files[i]);
		}
		
		list_files = new JList<String>(model);
		list_files.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                      boolean isSelected, boolean cellHasFocus) {
                 Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 if(manager.getConfigMap().get(value) == TYPE.ENCRYPTED) {
                	 setBackground(new Color(90, 181, 242));
                 }
                 else if(manager.getConfigMap().get(value) == TYPE.DECRYPTED) {
                	 setBackground(new Color(204, 204, 204)); 
                 }
                
                 return c;
            }

       });
  
	
		list_files.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list_files.setLayoutOrientation(JList.VERTICAL);
		

		listScroller = new JScrollPane(list_files);
		listScroller.setPreferredSize(new Dimension(250, HEIGHT - 70));
		
		mainPanel_right.removeAll();
		mainPanel_right.add(listScroller, BorderLayout.CENTER);
		mainPanel_right.add(new JLabel("     Blue = Encrypted  |  Grey = Decrypted"),BorderLayout.PAGE_END);
		
		frame.getContentPane().validate();
		frame.getContentPane().repaint();
		
		System.out.println("List updated, total " + array_str_files.length + " files");
	
	}
	



	
}//end of class Window