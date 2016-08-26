package tianlinz_CS201L_assignment2;

import javax.swing.UIManager;

public class Assignment2 {

	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch(Exception e){
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		
		MainWindow main = new MainWindow();
		main.setVisible(true);
	}
}