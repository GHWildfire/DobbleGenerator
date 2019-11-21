package content;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import panels.Panel;

/**
 * Runnable FileChooser to select the directory where the PDF will be generated
 * @author Etienne Hüsler
 * @version 2.0
 */
public class SetSaver implements Runnable
{
	private Panel panel;
	
	public SetSaver(Panel panel)
	{
		this.panel = panel;
	}
	
	/**
	 * Open a dialog based on the desktop to search a directory
	 */
	@Override
	public void run() 
	{
		// Instantiate a JFileChooser to search for PDF files
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Génération des cartes");
		fc.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
		fc.setSelectedFile(new File("CardSet.pdf"));
		fc.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
		
		// Deactivate the loading screen
		panel.setIsLoading(false);
		
		// Handle the response of the JFileChooser
		int code = fc.showSaveDialog(panel);
		if (code == 0)
		{
			panel.saveSet(fc.getSelectedFile());
		}
	}
}
