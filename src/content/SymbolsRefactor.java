package content;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Class available to rename the images of the symbols
 * @author Etienne Hüsler
 * @version 2.0
 */
public class SymbolsRefactor 
{
	private static SymbolsRefactor instance;
	private final String[] EXTENSIONS = new String[] {"JPEG", "JPG", "Exif", "TIFF", "GIF", "BMP", "PNG", "PPM", "PGM", "PBM", "PNM", "WebP"};
	private FilenameFilter filter;
	private File dir;
	
	private SymbolsRefactor()
	{
		// Filter to search for images only
		filter = new FilenameFilter() 
		{
	        @Override
	        public boolean accept(final File dir, final String name) 
	        {
	            for (final String ext : EXTENSIONS) 
	            {
	                if (name.toLowerCase().endsWith("." + ext.toLowerCase())) 
	                {
	                    return (true);
	                }
	            }
	            return (false);
	        }
	    };
	}
	
	public static SymbolsRefactor getInstance()
	{
		if (instance == null)
		{
			instance = new SymbolsRefactor();
		}
		return instance;
	}
	
	/**
	 * Set the directory of the SymbolsRefactor
	 * @param file : New directory
	 */
	public void setDirectory(File file)
	{
		dir = file;
	}
	
	/**
	 * Rename an image file
	 * @param oldName : Previous name of the image
	 * @param newName : New name of the image
	 */
	public void renameSymbol(String oldName, String newName)
	{
		if (dir.isDirectory())
		{
			for (final File f : dir.listFiles(filter))
			{
				if (f.getName().equals(oldName))
				{
					File newFile = new File(dir.getPath() + "/" + newName);
					f.renameTo(newFile);
				}
			}
		}
	}
}
