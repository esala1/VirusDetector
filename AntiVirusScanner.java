import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JTextField;

import java.util.*;
import java.io.*;
import java.security.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Color;

public class AntiVirusScanner {

	//accessible anywhere: file object array 
	static File[] files_present;
	//creating this list to store md5 for files 
	static ArrayList<String> list_MD5Files = new ArrayList<String>();
	//store file names
	static ArrayList<String> file_names = new ArrayList<String>();
	static String directory_name;
	private static JFrame frame;
	private JTextArea textArea;
	private JTextField textField;
	static Connection DatabaseConnection = null;
	static String ValueFromDB;
	static String dirPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					AntiVirusScanner window = new AntiVirusScanner();
					window.frame.setVisible(true);
					//choose your directory path here
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int option = fileChooser.showOpenDialog(frame);
					if(option == JFileChooser.APPROVE_OPTION){
		               File file = fileChooser.getSelectedFile();
		               dirPath = file.getAbsolutePath();
					}
					File directoryPath = new File (dirPath);										
					filesReader(directoryPath);			
					//hashing algorithm implementation
					MD5_Hashing(); //this method will call checksum method	        
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static File[] filesReader(File directoryPath) {
		//handling user-visible error: if the path is incorrect, it will not save to array and will give error when attempting to access
		try {
			//I created an array of file objects to return files using default method, "listFiles()".
			files_present = directoryPath.listFiles();
			System.out.println("Total files detected: "+ files_present.length);
			System.out.println();
			System.out.println("The following are the list of files present in this directory: ");
			System.out.println();
			//printing some properties of those files
			for(int i=0; i<files_present.length; i++) {
				System.out.println("File name: "+files_present[i].getName());
				String file_n = (String)files_present[i].getName();
				file_names.add(file_n);
				
		        System.out.println("File path: "+files_present[i].getAbsolutePath());
		        directory_name = files_present[i].getPath();
		        System.out.println("Size :"+files_present[i].getTotalSpace());
		        System.out.println();
			}
		}
		catch (NullPointerException e){
			System.out.println("Please enter a correct path");
		}
		return files_present;
	}
	
	
	//method2_hashing
	public static void MD5_Hashing() {
		try {
			for(int i=0; i<files_present.length; i++) {
				//System.out.println(files_present[i]);
				MessageDigest mdigest = MessageDigest.getInstance("MD5"); //MD5 hashing algorithm
				String checksum = checksum(mdigest, files_present[i]); //performing checksum on the files 
			
				checksum(mdigest,files_present[i]); //calling checksum method for each file here
				System.out.println(checksum);
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println("error");
		} catch (IOException e) {
			System.out.println("error");
		}
		}
		
	//checksum is part of the MD5 hashing implementation
	//here we are throwing an exception. Alternatively could do try_catch block but I tried and this is better
	public static String checksum(MessageDigest mdigest, File file) throws IOException {
		
		FileInputStream file_input_stream = new FileInputStream(file);
		 
        // Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCounter = 0;
 
        // read and update file data using mdigest
        while ((bytesCounter = file_input_stream.read(byteArray)) != -1){
            mdigest.update(byteArray, 0, bytesCounter);
        };
        file_input_stream.close();
 
        // store the bytes returned
        byte[] bytes = mdigest.digest();
      
        //creating a string builder to update string
        StringBuilder sb1 = new StringBuilder();
       
        // loop through the bytes array
        for (int i = 0; i < bytes.length; i++) {
        	//System.out.println(bytes[i]);
        	//I converted the decimal representation into hexadecimal and appended to my string builder object
            sb1.append(Integer
                    .toString((bytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        list_MD5Files.add(sb1.toString());
        return sb1.toString();
	}			 
	 public static boolean IsInfected() {
		 try {
			 //import connection from Keys.java
		 	DatabaseConnection  = Keys.DatabaseConnection();
			if (DatabaseConnection == null) {
				JOptionPane.showMessageDialog(frame, "No Internet Connection");
				System.exit(0);
			}
			Statement StatementSelect = DatabaseConnection.createStatement();
			ResultSet DataResult; //create a return data object
			DataResult = StatementSelect.executeQuery("SELECT * from VirusMD5");
			while (DataResult.next() ) {
				ValueFromDB = DataResult.getString("MD5");	
				if(list_MD5Files.contains(ValueFromDB)) {
					 return true;
				 }
			 }		 
	    } catch (Exception e) {
	        System.out.println("please try again! ");
	    }
		 return false;
	    }
	/**
	 * Create the application.
	 */
	public AntiVirusScanner() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.CYAN);
		frame.setBounds(100, 100, 1206, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Scan");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				textArea.append("Files Scanned: " + "\n");
				textArea.append("...");
				textArea.append("\n");
				for(int i=0; i<file_names.size(); i++) {					
					textArea.append(file_names.get(i) + "\n"); //append all values from arraylist
				}			
				if(IsInfected()) {
					textField.setText("Virus detected");
					textField.setBackground(Color.red);
			    	JFrame frame = new JFrame("error MessageDialog ");
			    	for(int j=0; j<3; j++) {
						JOptionPane.showMessageDialog(frame, "Your system is infected, please contact our specialist: Eliyas");
						//System.exit(0);
				    }
				}
				else {
			    	textField.setText("Clean");
			    	textField.setBackground(Color.green);
				}
			}	
		});
		
		btnNewButton.setBounds(49, 134, 222, 61);
		frame.getContentPane().add(btnNewButton);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
		textArea.setBounds(326, 86, 611, 292);
		frame.getContentPane().add(textArea);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textField.setBounds(133, 489, 408, 45);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		statusLabel.setBounds(23, 487, 81, 45);
		frame.getContentPane().add(statusLabel);
		
		JLabel titleLabel = new JLabel("Virus Detector");
		titleLabel.setBackground(Color.WHITE);
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		titleLabel.setBounds(537, 10, 236, 71);
		frame.getContentPane().add(titleLabel);
		
		JLabel noteLabel = new JLabel("*NOTE: restart app for every new scan");
		noteLabel.setForeground(Color.RED);
		noteLabel.setBackground(Color.BLACK);
		noteLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		noteLabel.setBounds(649, 491, 346, 45);
		frame.getContentPane().add(noteLabel);
	}
}
