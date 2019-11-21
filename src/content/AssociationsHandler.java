package content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import models.AssociationsModel;

/**
 * Class to load and save all symbol-tag associations in a file
 * @author Etienne Hüsler
 * @version 2.0
 */
public class AssociationsHandler 
{
	private FilenameFilter filter;
	private File dir;
	
	private final String EXTENSION = "DGAF";  // Dobble Generator Associations File
	
	private static AssociationsHandler instance;
	
	private AssociationsHandler()
	{
		// Filter that only accept files with the associations file extension
		filter = new FilenameFilter() 
		{
	        @Override
	        public boolean accept(final File dir, final String name) 
	        {
                return (name.toLowerCase().endsWith("." + EXTENSION.toLowerCase()));
	        }
	    };
	}
	
	public static AssociationsHandler getInstance()
	{
		if (instance == null)
		{
			instance = new AssociationsHandler();
		}
		return instance;
	}
	

	/**
	 * Change the directory of the AssociationsHandler 
	 * @param file : File representing the directory
	 */
	public void setDirectory(File file)
	{
		dir = file;
	}
	
	/**
	 * Read the associations file and bind all associations to the given list of symbols
	 * @param symbols : List of symbols that will be associated with the content of the file
	 */
	public void read(ArrayList<Symbol> symbols)
	{
		// Verify that the directory is not a file
		if (dir.isDirectory())
		{
			// Create a list containing all files respecting the filter
			File[] files = dir.listFiles(filter);
			
			if (files.length > 0)
			{
				// Take the first file if many associations files are present
				File file = files[0];
				
				// Deserialization into the AssociationsModel
				ObjectInputStream ois = null;
				try (FileInputStream streamIn = new FileInputStream(file)) 
				{
					ois = new ObjectInputStream(streamIn);
					AssociationsModel associationsModel = (AssociationsModel) ois.readObject();
					associationsModel.assignAssociations(symbols);
				} catch (Exception e) 
				{
				    System.out.println("Error while reading " + dir.getName());
				} finally 
				{
					// Close the stream
				    if(ois != null){
				    	try 
				    	{
							ois.close();
						} catch (IOException e) 
				    	{
							e.printStackTrace();
						}
				    } 
				}
			}
		}
	}
	
	/**
	 * Save the actual state of the associations between symbols and tags 
	 * @param symbols : List of symbols and their tags to save
	 */
	public void save(ArrayList<Symbol> symbols)
	{
		// Verify that the directory is not a file and that there is symbols to save
		if (dir.isDirectory() && symbols.size() > 0)
		{
			AssociationsModel associationsModel = new AssociationsModel(symbols);
			
			// Create the file
			String filename = dir.getPath() + "/TagLinks." + EXTENSION.toLowerCase();
			File file = new File(filename);
			ObjectOutputStream oos = null;
			
			// Serialize the data into the file
			try (FileOutputStream out = new FileOutputStream(file)) 
			{
				oos = new ObjectOutputStream(out);
				oos.writeObject(associationsModel);
		    } catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} catch (IOException e) 
			{
				e.printStackTrace();
			} finally
			{
				// Close the stream
				if (oos != null)
				{
					try 
					{
						oos.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
