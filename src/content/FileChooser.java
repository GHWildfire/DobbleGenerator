package content;

import java.io.File;

import javax.swing.JFileChooser;

import panels.Panel;

/**
 * Runnable FileChooser to select a directory or a file
 * @author Etienne Hüsler
 * @version 2.0
 */
public class FileChooser implements Runnable
{
	private Panel panel;
	
	public FileChooser(Panel panel)
	{
		this.panel = panel;
	}
	
	/**
	 * Open a dialog based on the desktop to search a directory
	 */
	@Override
	public void run() 
	{
		// Instantiate a JFileChooser to search for directories
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Source des symboles");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
		
		// Deactivate the loading screen
		panel.setIsLoading(false);
		
		// Handle the response of the JFileChooser
		int code = fc.showOpenDialog(panel);
		if (code == 0)
		{
			panel.changeSymbolsFolder(fc.getSelectedFile());
		}
	}
}
