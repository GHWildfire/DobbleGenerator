package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import content.Tag;
import content.TagLibrary;
import panels.PanelSymbol;

/**
 * List containing tags that can be scrolled
 * @author Etienne Hüsler
 * @version 2.0
 */
public class TagList 
{
	private int x;
	private int y;
	private int w;
	private int h;
	private String name;
	private int heightTags;
	private boolean tagFlag;
	private int maxTags;
	private int indexTags;
	private boolean scollbarsInUse;
	private boolean isListStatic;
	
	private final int ARC_DIAMETER = 20;
	private final int MARGING = 10;
	private final int WIDTH_SCROLLBAR = 20;
	private final int SIZE_DELETE_TAG_BUTTON = 40;
	
	private Textfield textfield;
	private ArrayList<Tag> tags;
	private Tag selectedTag;
	private Dragfield parent;
	private Scrollbar scrollbar;
	private PanelSymbol panelSymbol;
	
	public TagList(PanelSymbol panelSymbol, Dragfield parent, String name, ArrayList<Tag> tags, int heightTags, boolean isListStatic)
	{
		this.panelSymbol = panelSymbol;
		this.parent = parent;
		this.name = name;
		this.tags = tags;
		this.heightTags = heightTags;
		this.isListStatic = isListStatic;
		x = 0;
		y = 0;
		w = 0;
		h = 0;
		maxTags = 0;
		indexTags = 0;
		selectedTag = null;
		tagFlag = false;
		scollbarsInUse = false;
		
		textfield = new Textfield("Filtrer...");
		scrollbar = new Scrollbar();
	}
	
	public ArrayList<Tag> getTags()
	{
		return tags;
	}
	
	public void changeTags(ArrayList<Tag> tags)
	{
		this.tags = tags;
	}
	
	public Tag getSelectedTag()
	{
		return selectedTag;
	}
	
	public void addTag(Tag tag)
	{
		tags.add(indexTags, tag);
	}
	
	public Textfield getTextField()
	{
		return textfield;
	}
	
	public void insertTagFlag()
	{
		tagFlag = true;
	}
	
	public void scrollTags(double rotation)
	{
		scrollbar.scroll(rotation);
	}
	
	public void setScollbarsInUse(boolean scollbarsInUse)
	{
		this.scollbarsInUse = scollbarsInUse;
	}
	
	public boolean isScrollbarUsed()
	{
		return scrollbar.isUsed();
	}
	
	/**
	 * Check for uniqueness
	 * @param tag : Tag to check
	 * @return if the tag is unique or not
	 */
	public boolean isTagUnique(Tag tag)
	{
		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).getDescription().equals(tag.getDescription()))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Update the position and dimension of the tag list
	 * @param x : Tag list x coordinate
	 * @param y : Tag list y coordinate
	 * @param w : Tag list width
	 * @param h : Tag list height
	 */
	public void adapt(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		textfield.adapt(x + MARGING, y + MARGING, w - 2 * MARGING, 60);
		scrollbar.adapt(x + w - MARGING - WIDTH_SCROLLBAR, y + 3 * MARGING + textfield.getHeight(), 
				WIDTH_SCROLLBAR, h - 5 * MARGING - textfield.getHeight());
		
		maxTags = (h - 5 * MARGING - textfield.getHeight()) / (MARGING + heightTags);
	}
	
	/**
	 * Update the list of tags depending on the mouse activity
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		scrollbar.handleClick(mouseX, mouseY);
		if (!scollbarsInUse)
		{
			textfield.handleClick(mouseX, mouseY);
		}
	}
	
	/**
	 * Delete a tag only if the list is static
	 * @param tag : Tag to be deleted
	 */
	public void deleteTag(Tag tag)
	{
		if (tagInList(tags, tag) && !isListStatic)
		{
			tags.remove(tag);
			TagLibrary.getInstance().removeUsedTag(tag);
		}
	}
	
	/**
	 * Check if the mouse is in the list
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 * @return true if the mouse is in the list
	 */
	public boolean isMouseIn(int mouseX, int mouseY)
	{
		if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Add a tag but check for uniqueness
	 * @param tag : Tag to add
	 * @param index : Index of the tag in the list
	 */
	public void addUniqueTag(Tag tag, int index)
	{
		boolean unique = true;
		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).getDescription().equals(tag.getDescription()))
			{
				unique = false;
			}
		}
		
		if (unique)
		{
			tags.add(index, tag);
		} else
		{
			panelSymbol.addMessage("Tag déjà associé au symbole", true);
		}
	}
	
	/**
	 * Helper method to verify if the given tag is in the movedTags 
	 * @param tags : List of all moved tags
	 * @param tag : Tag to check
	 * @return true if the tag is in the moved tags list
	 */
	private boolean tagInList(ArrayList<Tag> tags, Tag tag)
	{
		for (int i = 0; i < tags.size(); i++)
		{
			if (tags.get(i).equals(tag))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Draw the Tag list
	 * @param g : Graphic context
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 * @param tagSelected : Tag selected in the Drag and Drop operation
	 * @param tagsMoved : List of moved tags
	 */
	public void draw(Graphics g, int mouseX, int mouseY, Tag tagSelected, ArrayList<Tag> tagsMoved)
	{		
		// Title
		g.setFont(new Font("Arial", Font.ITALIC, 20));
		g.drawString(name, x + (w - g.getFontMetrics().stringWidth(name)) / 2, y - 10);
		
		// Tag insertion and finding insertion index
		int indexTagSelected = -1;
		int yTags = y + 3 * MARGING + textfield.getHeight();
		Graphics2D g2 = (Graphics2D) g;
		if (tagSelected != null || tagFlag)
		{
			if (isMouseIn(mouseX, mouseY))
			{
				indexTagSelected = (mouseY - yTags + heightTags / 2 + MARGING) / (heightTags + MARGING);
			    g2.setStroke(new BasicStroke(6));
			}
		}
		
		// Body
		g.setColor(new Color(240, 240, 240));
		g.fillRoundRect(x, y, w, h, ARC_DIAMETER, ARC_DIAMETER);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, w, h, ARC_DIAMETER, ARC_DIAMETER);
		g2.setStroke(new BasicStroke(2));
		
		// Search bar
		textfield.draw(g, Mouse.leftClickedSymbol, Mouse.leftPressedSymbol);
		
		// Scroll bar
		scrollbar.draw(g);
		int filteredTags = 0;
		for (int i = 0; i < tags.size(); i++)
		{
			if ((tags.get(i).getDescription().contains(textfield.getContent()) || textfield.getContent().equals("")) 
					&& (!tagInList(tagsMoved, tags.get(i)) || !isListStatic) && tags.get(i) != tagSelected)
			{
				filteredTags++;
			}
		}
		scrollbar.setHeightButton(maxTags, filteredTags);
		if (filteredTags > maxTags) scrollbar.setVisibilty(true);
		else scrollbar.setVisibilty(false);
		indexTags = scrollbar.getIndex();
		
		// Tags
		int counter = 0;
		selectedTag = null;
		for (int i = 0; i < tags.size(); i++)
		{	
			Tag tag = tags.get(i);
			
			// Insertion in the list
			if (i == indexTagSelected && !isListStatic) 
			{
				if (tagFlag)
				{
					addUniqueTag(tagSelected, counter);
					tagFlag = false;
					parent.resetDragedTag();
					TagLibrary.getInstance().addUsedTag(tag);
				}
				counter++;
			}
			
			if (counter >= maxTags + indexTags) break;
			
			// Filter
			if ((tag.getDescription().contains(textfield.getContent()) || textfield.getContent().equals("")) 
					&& (!tagInList(tagsMoved, tag) || !isListStatic) && tag != tagSelected)
			{
				if (counter >= indexTags)
				{
					// Tag data
					int xTag = x + MARGING;
					int yTag = yTags + (counter - indexTags) * (MARGING + heightTags);
					int wTag = w - 2 * MARGING;
					int hTag = heightTags;
					int shiftScrollbar = 0;
					
					// Reduce the length of the tag if the scroll bar is visible
					if (scrollbar.isVisible()) 
					{
						shiftScrollbar = WIDTH_SCROLLBAR + MARGING;
					}
					
					// Delete button
					int xDelete = xTag + wTag - MARGING - SIZE_DELETE_TAG_BUTTON - shiftScrollbar;
					int yDelete = yTag + MARGING;
					boolean inDelete = false;
					if (mouseX > xDelete && mouseX < xDelete + SIZE_DELETE_TAG_BUTTON &&
							mouseY > yDelete && mouseY < yDelete + SIZE_DELETE_TAG_BUTTON)
					{
						inDelete = true;
						if (Mouse.leftClickedSymbol)
						{
							tags.remove(tag);
							TagLibrary.getInstance().removeTag(tag);
						}
					} 
					
					// Draw the tag (selected or not)
					g.setFont(new Font("Arial", Font.BOLD, 20));
					if (mouseX > xTag && mouseX < xTag + wTag - shiftScrollbar && mouseY > yTag && mouseY < yTag + hTag 
							&& tagSelected == null && !scollbarsInUse && !inDelete)
					{
						g.setColor(Color.WHITE);
						g.fillRect(xTag, yTag, wTag - shiftScrollbar, hTag);
						
						g.setColor(Color.BLACK);
						g.drawRect(xTag, yTag, wTag - shiftScrollbar, hTag);
						
						g.setColor(tag.getColor());
						g.fillRect(xTag, yTag, 4 * MARGING, hTag);
						
						g.setColor(Color.BLACK);
						g.drawRect(xTag, yTag, 4 * MARGING, hTag);
						
						g.drawString(tag.getDescription(), xTag + 5 * MARGING, yTag + (int)(0.64 * heightTags));
						
						selectedTag = tag;
					} else
					{
						g.setColor(Color.WHITE);
						g.fillRect(xTag, yTag, wTag - shiftScrollbar, hTag);
						
						g.setColor(Color.BLACK);
						g.drawRect(xTag, yTag, wTag - shiftScrollbar, hTag);
						
						g.setColor(tag.getColor());
						g.fillRect(xTag, yTag, 3 * MARGING, hTag);
						
						g.setColor(Color.BLACK);
						g.drawRect(xTag, yTag, 3 * MARGING, hTag);
						
						g.drawString(tag.getDescription(), xTag + 4 * MARGING, yTag + (int)(0.64 * heightTags));
					}
					
					// Draw delete button
					if (inDelete)
					{
						g2.setStroke(new BasicStroke(4));
					}
					g.setColor(new Color(240, 240, 240));
					g.setFont(new Font("Arial", Font.BOLD, 20));
					g.fillRect(xDelete, yDelete, SIZE_DELETE_TAG_BUTTON, SIZE_DELETE_TAG_BUTTON);
					g.setColor(Color.BLACK);
					g.drawRect(xDelete, yDelete, SIZE_DELETE_TAG_BUTTON, SIZE_DELETE_TAG_BUTTON);
					g2.setStroke(new BasicStroke(2));
					g.drawString("X", xDelete + (int)(1.4 * MARGING), yDelete + (int)(2.8 * MARGING));
				}
				
				counter++;
			}
		}
		// Insertion for the last place in the list
		if (tagFlag)
		{
			if (indexTagSelected != -1)
			{
				parent.resetDragedTag();
				if (!isListStatic)
				{
					addUniqueTag(tagSelected, tags.size());
					TagLibrary.getInstance().addUsedTag(tagSelected);
				}
			}
			tagFlag = false;
		}
	}
}
