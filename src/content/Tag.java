package content;

import java.awt.Color;
import java.io.Serializable;

/**
 * Tag used to filter symbols
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Tag implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Color color;
	private String description;
	
	public Tag(Color color, String description)
	{
		this.color = color;
		this.description = description;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Compare the description of two tags 
	 * @param other : The other tag
	 * @return true if they have the same description
	 */
	public boolean equals(Tag other)
	{
		return description.equals(other.getDescription());
	}
}
