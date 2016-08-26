package tianlinz_CS201L_assignment4;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MyClient extends javax.swing.JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private HelperClasses.MyBackgroundPanel mainPanel;
	
	private JPanel introComponents;
	private HelperClasses.MyButton loginOptionButton;
	private HelperClasses.MyButton signUpOptionButton;
	private HelperClasses.MyButton offlineButton;
	
	private JPanel signUpComponents;
	private JLabel usernameLabel;
	private JTextField signUp_UsernameField;
	private JLabel passwordLabel;
	private JPasswordField signUp_PasswordField;
	private JLabel repeatLabel;
	private JPasswordField signUp_RepeatField;
	private HelperClasses.MyButton signUp_BackButton;
	private HelperClasses.MyButton signUpButton;
	
	private JPanel loginComponents;
	private JLabel login_UsernameLabel;
	private JTextField login_UsernameField;
	private JLabel login_PasswordLabel;
	private JPasswordField login_PasswordField;
	private HelperClasses.MyButton login_BackButton;
	private HelperClasses.MyButton loginButton;
	
	private MainWindow mainWindow;
		
	private int port;
	private String host;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private String username;
	private String password;
		
	
	public MyClient(){
		super("TextEdit Login");
		instantiateComponents();
		createGUI();
		addActions();
		readConfigFile();
	}

	private void instantiateComponents(){
		mainPanel = new HelperClasses.MyBackgroundPanel();
		introComponents = new JPanel();
		loginOptionButton = new HelperClasses.MyButton("Login", "resources/img/buttons/button.png");
		signUpOptionButton = new HelperClasses.MyButton("Sign up", "resources/img/buttons/button.png");
		offlineButton = new HelperClasses.MyButton("Offline", "resources/img/buttons/button.png");
		
		signUpComponents= new JPanel();
		usernameLabel = new JLabel("Username: ");
		signUp_UsernameField = new JTextField(20);
		passwordLabel = new JLabel("Password: ");
		signUp_PasswordField = new JPasswordField(20);
		repeatLabel = new JLabel("Repeat: ");
		signUp_RepeatField = new JPasswordField(20);
		signUpButton = new HelperClasses.MyButton("Sign up", "resources/img/buttons/button.png");
		signUp_BackButton = new HelperClasses.MyButton("Back", "resources/img/buttons/button.png");

		loginComponents = new JPanel();
		login_UsernameLabel = new JLabel("Username: ");
		login_UsernameField = new JTextField(20);
		login_PasswordLabel = new JLabel("Password: ");
		login_PasswordField = new JPasswordField(20);		
		loginButton = new HelperClasses.MyButton("Login", "resources/img/buttons/button.png");
		login_BackButton = new HelperClasses.MyButton("Back", "resources/img/buttons/button.png");
		mainWindow = new MainWindow(this);		
	}
	
	private void createGUI(){
		this.setResizable(false);
		this.setSize(900, 500);
		this.setLocation(700, 100);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.add(mainPanel);
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(introComponents);
		introComponents.setLayout(new GridBagLayout());
		introComponents.add(loginOptionButton);
		introComponents.add(signUpOptionButton);
		introComponents.add(offlineButton);	
		introComponents.setVisible(true);
		
		mainPanel.add(signUpComponents);
		signUpComponents.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 0;
		usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		usernameLabel.setPreferredSize(new Dimension(80, 30));
		signUpComponents.add(usernameLabel, gbc);
		gbc.gridx = 1;
		signUp_UsernameField.setHorizontalAlignment(SwingConstants.LEFT);
		signUp_UsernameField.setPreferredSize(new Dimension(150, 30));
		signUpComponents.add(signUp_UsernameField, gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		passwordLabel.setPreferredSize(new Dimension(80, 30));
		signUpComponents.add(passwordLabel,gbc);
		gbc.gridx = 1;
		signUp_PasswordField.setHorizontalAlignment(SwingConstants.LEFT);
		signUp_PasswordField.setPreferredSize(new Dimension(150, 30));
		signUpComponents.add(signUp_PasswordField,gbc);		
		gbc.gridy = 2;
		gbc.gridx = 0;
		repeatLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		repeatLabel.setPreferredSize(new Dimension(80, 30));
		signUpComponents.add(repeatLabel,gbc);
		gbc.gridx = 1;
		signUp_RepeatField.setHorizontalAlignment(SwingConstants.LEFT);
		signUp_RepeatField.setPreferredSize(new Dimension(150, 30));
		signUpComponents.add(signUp_RepeatField,gbc);
		gbc.gridy = 3;
		gbc.gridx = 0;
		signUpComponents.add(signUp_BackButton, gbc);
		gbc.gridx = 1;
		signUpComponents.add(signUpButton,gbc);
		signUpComponents.setVisible(false);
		
		
		mainPanel.add(loginComponents);
		loginComponents.setLayout(new GridBagLayout());
		gbc.gridy = 0;
		gbc.gridx = 0;
		login_UsernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		login_UsernameLabel.setPreferredSize(new Dimension(80, 30));
		loginComponents.add(login_UsernameLabel, gbc);
		gbc.gridx = 1;
		login_UsernameField.setHorizontalAlignment(SwingConstants.LEFT);
		login_UsernameField.setPreferredSize(new Dimension(150, 30));
		loginComponents.add(login_UsernameField, gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		login_PasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		login_PasswordLabel.setPreferredSize(new Dimension(80, 30));
		loginComponents.add(login_PasswordLabel,gbc);
		gbc.gridx = 1;
		login_PasswordField.setHorizontalAlignment(SwingConstants.LEFT);
		login_PasswordField.setPreferredSize(new Dimension(150, 30));
		loginComponents.add(login_PasswordField,gbc);		
		gbc.gridy = 2;
		gbc.gridx = 0;
		loginComponents.add(login_BackButton, gbc);
		gbc.gridx = 1;
		loginComponents.add(loginButton, gbc);
		loginComponents.setVisible(false);	
	}
	
	private void addActions(){
		loginOptionButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		signUpOptionButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		offlineButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		signUp_BackButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		signUpButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		login_BackButton.addMouseListener(new HelperClasses.MyMouseAdapter());
		loginButton.addMouseListener(new HelperClasses.MyMouseAdapter());

		
		loginOptionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToLogin();
			}
		});
		signUpOptionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToSignUp();
			}
		});
		offlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goToOfflineMode();
			}
		});
		signUp_BackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleIntroButtons();
			}
		});
		signUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signUp();
			}
		});
		login_BackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleIntroButtons();
			}
		});
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
	}
	
	private void toggleIntroButtons(){
		if(introComponents.isVisible()){
			introComponents.setVisible(false);
		}
		else{
			introComponents.setVisible(true);
		}
		loginComponents.setVisible(false);
		signUpComponents.setVisible(false);
		clearAllFields();
	}
	
	private void goToLogin(){
		connectToServer();
		toggleIntroButtons();
		loginComponents.setVisible(true);
	}
	
	private void goToSignUp(){
		connectToServer();
		toggleIntroButtons();
		signUpComponents.setVisible(true);
	}
	
	private void goToOfflineMode(){
		mainWindow.setUsername("");
		mainWindow.setVisible(true);
		this.setVisible(false);
		clearAllFields();
	}
	
	private void readConfigFile(){
		Scanner scnr;
		try {
			scnr = new Scanner(new File("resources/clientConfig.txt"));
			if(scnr.hasNext()){
				port = scnr.nextInt();
			}
			if(scnr.hasNext()){
				host = scnr.next();
			}
			scnr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void connectToServer(){
		Socket s = null;
		try {
			s = new Socket(host, port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			new Thread(this).start();	
		}
		catch (ConnectException ce){
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Client Connection failed", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
		}
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Client Connection failed", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
		} 
	}
	
	private void signUp(){
		//Get data
		username = signUp_UsernameField.getText();
		password = HelperMethods.encryptPassword(new String(signUp_PasswordField.getPassword()));
		String repeat = HelperMethods.encryptPassword(new String(signUp_RepeatField.getPassword()));		

		//Check validity of data
		if(!password.equals(repeat)){
			JOptionPane.showMessageDialog(this, Constants.passwordRepeatMismatch, "Sign up failed", JOptionPane.WARNING_MESSAGE);
			return;
		}	
		boolean hasUpperCase = false; boolean hasNum = false;
		for(char entry: signUp_PasswordField.getPassword()){
			if(Character.isDigit(entry)){
				hasNum = true;
			}
			else if(Character.isUpperCase(entry)){
				hasUpperCase = true;
			}
		}
		if((!hasUpperCase)||(!hasNum)){
			JOptionPane.showMessageDialog(this, Constants.passwordRequirements, "Sign up failed", JOptionPane.WARNING_MESSAGE);
			return;
		}
		clearAllFields();
		
		//Query server to check for validity
		try {
			Parcel parcel = new Parcel(true, "AVAILABLE", "SELECT * FROM Users", username, password);
			oos.writeObject(parcel);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Sign up failed", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
			return;
		}
	}
	
	private void finishSignUp(ReturnParcel result){
		
		//Result being true = username IS taken
		if(result.getResult()){
			JOptionPane.showMessageDialog(this, Constants.usernameTaken, "Sign up failed", JOptionPane.WARNING_MESSAGE);
			return;
		}
		else{
			//Username and password are valid and not taken, Insert combination into server
			try {
				Parcel parcel = new Parcel(false,"INSERT","INSERT INTO Users (username, password) VALUES ('"+username+"','"+password+"')",  null, null);
				oos.writeObject(parcel);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Sign up failed", JOptionPane.WARNING_MESSAGE);
				goToOfflineMode();
			}
			
			//Pass the user through
			this.setVisible(false);
			mainWindow.setUsername(username);
			mainWindow.setVisible(true);
		}
	}
	
	private void login(){
		//Get inputs
		username = login_UsernameField.getText();
		password = HelperMethods.encryptPassword(new String(login_PasswordField.getPassword()));
		clearAllFields();

		//Query server for authentication
		try {
			Parcel parcel = new Parcel(true, "AUTHENTICATION", "SELECT * from Users WHERE username='"+username+"'", username, password);
			oos.writeObject(parcel);				
		} 
		catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Login failed", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
			return;
		}
	}
	
	private void finishLogin(ReturnParcel result){
		//Authenticated, login
		if(result.getResult()){
			this.setVisible(false);
			mainWindow.setUsername(username);
			mainWindow.setVisible(true);
		}
		else{
			JOptionPane.showMessageDialog(this, Constants.invalidCombination, "Login failed", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	private void clearAllFields(){
		login_UsernameField.setText("");
		login_PasswordField.setText("");
		signUp_UsernameField.setText("");
		signUp_PasswordField.setText("");
		signUp_RepeatField.setText("");
	}

	public void queryUserFilenames(String purpose){
		//Query the server to check for all files under the given username
		try {
			Parcel parcel = new Parcel(true, "GETFILES"+purpose, "SELECT filename from Files WHERE username='"+username+"'", username, null);
			oos.writeObject(parcel);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Could not get files", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
			return;
		}
	}
	
	public void saveFile(String filename, String fileContent){
		//Save a file to server
		try {
			Parcel parcel = new Parcel(false, "SAVEFILE", "INSERT INTO Files(username,filename,fileContent)"
					+ "VALUES('" +username+ "','"+filename+"','"+fileContent+"')", username, filename);
			oos.writeObject(parcel);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Save file online failed", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
			return;
		}
	}
	
	public void openFile(String filename){
		//Notify server to log file "downloading"
		try {
			Parcel parcel = new Parcel(true, "OPENFILE", "SELECT fileContent FROM Files WHERE username='"
					+username+"' AND filename = '"+filename+"'", username, filename);
			oos.writeObject(parcel);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Could not open file", JOptionPane.WARNING_MESSAGE);
			goToOfflineMode();
			return;
		}
	}
	
	public void run() {
		while(true) {
			//Read from server
			try {
				Object received = ois.readObject();
				if(received == null){
					//Server cannot be reached
				}
				else{
					ReturnParcel result = (ReturnParcel)received;
					String operation = result.getOperation();
					switch (operation) {
					case "AVAILABLE":
						finishSignUp(result);
						break;
					case "AUTHENTICATION":
						finishLogin(result);
						break;
					case "GETFILES_SAVE":
						mainWindow.saveFileOnline(result.getFilenames());
						break;
					case "GETFILES_OPEN":
						mainWindow.openFileOnline(result.getFilenames());
						break;
					case "OPENFILE":
						mainWindow.finishOpeningOnline(result.getFilename(), result.getContent());
						break;
					case "ERROR":
						JOptionPane.showMessageDialog(this, Constants.serverUnreachable, "Connection error", JOptionPane.WARNING_MESSAGE);
						goToOfflineMode();
						break;
					}
				}
			} 
			catch (ClassNotFoundException | IOException e) {
				JOptionPane.showMessageDialog(this, Constants.cannotReadFromServer, "Connection error", JOptionPane.WARNING_MESSAGE);
				mainWindow.setUsername("");
				break;
			}
		}
	}
}
