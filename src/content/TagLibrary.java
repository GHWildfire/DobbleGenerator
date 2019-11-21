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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import elements.TagFilter;
import models.TagLibraryModel;
import panels.Panel;

/**
 * Library containing a map of tags and the numbers of symbols they are associated with
 * @author Etienne Hüsler
 * @version 2.0
 */
public class TagLibrary 
{
	private static TagLibrary instance;
	private Map<Tag, Integer> tags;
	private TagFilter tagFilter;
	private File dir;
	private FilenameFilter filter;
	private Panel panel;
	
	private final String EXTENSION = "DGLF";
	
	private TagLibrary()
	{
		tags = new HashMap<>();
		tagFilter = null;
		
		// Filter to only accept DGLF files (tag libraires)
		filter = new FilenameFilter() 
		{
	        @Override
	        public boolean accept(final File dir, final String name) 
	        {
                return (name.toLowerCase().endsWith("." + EXTENSION.toLowerCase()));
	        }
	    };
	}
	
	public static TagLibrary getInstance()
	{
		if (instance == null)
		{
			instance = new TagLibrary();
		}
		return instance;
	}
	
	public void associatePanel(Panel panel)
	{
		this.panel = panel;
	}
	
	/**
	 * Set the directory of the library
	 * @param file : File representing the directory
	 */
	public void setDirectory(File file)
	{
		dir = file;
	}
	
	/**
	 * Save the actual content of the library into a file
	 */
	public void save()
	{
		// Check if the directory is valid and if tags are present in the library
		if (dir.isDirectory() && tags.size() > 0)
		{
			TagLibraryModel tagLibraryModel = new TagLibraryModel(tags);
			
			// Create a new file
			String filename = dir.getPath() + "/TagLibrary." + EXTENSION.toLowerCase();
			File file = new File(filename);
			ObjectOutputStream oos = null;
			
			// Serialize the TagsLibraryModel into it
			try (FileOutputStream out = new FileOutputStream(file)) 
			{
				oos = new ObjectOutputStream(out);
				oos.writeObject(tagLibraryModel);
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
	
	/**
	 * Read the content of the TagLibrary file and put it's content into the library 
	 */
	public void read()
	{
		// Check if the directory is valid
		if (dir.isDirectory())
		{
			// Search for a DGLF file
			File[] files = dir.listFiles(filter);
			if (files.length > 0)
			{
				// Take the first file found
				File file = files[0];
				
				// Deserialize the file into the model
				ObjectInputStream ois = null;
				try (FileInputStream streamIn = new FileInputStream(file)) 
				{
					ois = new ObjectInputStream(streamIn);
					TagLibraryModel tagLibraryModel = (TagLibraryModel) ois.readObject();
					tags = tagLibraryModel.getTags();
				} catch (Exception e) 
				{
				    e.printStackTrace();
				} finally 
				{
					// Close the stream
				    if (ois != null)
				    {
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
	 * Associate the tagFilter
	 * @param tagFilter : TagFilter to associate with
	 */
	public void associateTagFilter(TagFilter tagFilter)
	{
		this.tagFilter = tagFilter;
	}
	
	/**
	 * Return a list of all tags present in the library
	 * @return the list of all tags
	 */
	public ArrayList<Tag> getTags()
	{
		return new ArrayList<>(tags.keySet());
	}
	
	/**
	 * Return a list of tags that are used by at least one symbol
	 * @return the list of used tags
	 */
	public ArrayList<Tag> getUsedTags()
	{
		// Stream to filter the map into a list
		Map<Tag, Integer> usedTags = tags.entrySet().stream().parallel()
							.filter(x -> x.getValue() > 0)
							.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(),
			                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		return new ArrayList<Tag>(usedTags.keySet());
	}
	
	/**
	 * Add a tag to the library and increment it's used counter 
	 * @param tag : Tag to be added
	 */
	public void addUsedTag(Tag tag)
	{
		if (tags.keySet().contains(tag))
		{
			tags.replace(tag, tags.get(tag), tags.get(tag) + 1);
		}
	}
	
	/**
	 * Remove a tag from the library and decrement it's used counter 
	 * @param tag : Tag to be removed
	 */
	public void removeUsedTag(Tag tag)
	{
		if (tags.keySet().contains(tag))
		{
			tags.replace(tag, tags.get(tag), tags.get(tag) - 1);
			tagFilter.updateTags();
			
			if (tags.get(tag) <= 0)
			{
				tagFilter.removeTag(tag);
			}
		}
	}
	
	/**
	 * Add a new entry to the map for a brand new tag
	 * @param tag : Tag with it's new used counter of 0
	 */
	public void addTag(Tag tag)
	{
		if (!tags.containsKey(tag))
		{
			tags.put(tag, 0);
		}
	}
	
	/**
	 * Remove a tag from the library with all of it's occurrences
	 * @param tag : Tag to be removed
	 */
	public void removeTag(Tag tag)
	{
		tags.remove(tag);
		
		// Delete all occurrences of the tag in all symbols
		ArrayList<Symbol> symbols = panel.getSymbols();
		for (int i = 0; i < symbols.size(); i++)
		{
			ArrayList<Tag> symbolTags = symbols.get(i).getTags();
			for (int j = symbolTags.size() - 1; j >= 0 ; j--)
			{
				if (symbolTags.get(j).equals(tag))
				{
					symbolTags.remove(symbolTags.get(j));
				}
			}
		}
	}
}
