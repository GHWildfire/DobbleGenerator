package content;

import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import elements.Main;
import panels.Panel;

/**
 * Load the symbols from images in a given directory
 * @author Etienne Hüsler
 * @version 2.0
 */
public class SymbolsLoader 
{
	private File dir;
	private final String[] EXTENSIONS = new String[] {"JPEG", "JPG", "Exif", "TIFF", "BMP", "PNG", "PPM", "PGM", "PBM", "PNM", "WebP"};
	private FilenameFilter filter;
	private Panel panel;
	private int sizeSymbols;
	private Image loadingImage;
	
	private final int SIZE_LOADING_IMAGE = 60;
	private final int MAX_DEPTH_SEARCH = 2;
	
	public SymbolsLoader(String path, Panel panel, int sizeSymbols)
	{
		dir = new File(path);
		this.panel = panel;
		this.sizeSymbols = sizeSymbols;
		
		// Filter to search only for image files
		filter = new FilenameFilter() 
		{
	        @Override
	        public boolean accept(final File dir, final String name) 
	        {
	            for (final String ext : EXTENSIONS) 
	            {
	                if (name.toLowerCase().endsWith("." + ext.toLowerCase())) 
	                {
	                    return true;
	                }
	            }
	            return false;
	        }
	    };
	    
	    loadingImage = generateImage("/pictures/spinner.png", SIZE_LOADING_IMAGE);
	}
	
	/**
	 * Generate an image from a path and a size
	 * @param path : Path of the image
	 * @param size : Size of the image
	 * @return the generated image
	 */
	private Image generateImage(String path, int size)
	{
		Image image = new ImageIcon(Main.class.getResource(path)).getImage();
		return image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
	}
	
	/**
	 * Set the directory of the SymbolsLoader
	 * @param file
	 */
	public void setDirectory(File file)
	{
		dir = file;
	}
	
	public File getDirectory()
	{
		return dir;
	}
	
	/**
	 * Recursive directory search to find images corresponding to the filter and create the corresponding symbols
	 * @param recursiveDir : Directory to search in
	 * @param depth : Current depth of the search
	 * @return a list of symbols created from the images filtered
	 */
	public ArrayList<Symbol> load(File recursiveDir, int depth)
	{
		ArrayList<Symbol> symbols = new ArrayList<>();
		if (recursiveDir.isDirectory() && depth <= MAX_DEPTH_SEARCH)
		{
			// Search for sub directories
			for (final File file : recursiveDir.listFiles())
			{
				if (file.isDirectory())
				{
					symbols.addAll(load(file, depth + 1));
				}
			}
			
			// Search for images
			for (final File file : recursiveDir.listFiles(filter))
			{
				if (file != null)
				{
					String filename = file.getName();
					
					if (filename.indexOf(".") > 0)
					{
						String name = filename.substring(0, filename.lastIndexOf("."));
						String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
						
						Symbol symbol = new Symbol(panel, loadingImage, name, extension, sizeSymbols, file);
						symbols.add(symbol);
					}
				}
			}
		}
		
		return symbols;
	}
}
