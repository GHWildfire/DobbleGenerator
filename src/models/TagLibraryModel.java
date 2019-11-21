package models;

import java.io.Serializable;
import java.util.Map;

import content.Tag;

/**
 * Model to serialize tags in the library
 * @author Etienne Hüsler
 * @version 2.0
 */
public class TagLibraryModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Map<Tag, Integer> tags;

	public TagLibraryModel(Map<Tag, Integer> tags)
	{
		this.tags = tags;
	}
	
	public Map<Tag, Integer> getTags()
	{
		return tags;
	}
}
