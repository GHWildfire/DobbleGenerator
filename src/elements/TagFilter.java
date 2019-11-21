package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import content.Tag;
import content.TagLibrary;
import panels.Panel;

/**
 * Text field that auto completes tags to filter symbols
 * @author Etienne Hüsler
 * @version 2.0
 */
public class TagFilter 
{
	private int x;
	private int y;
	private int w;
	private int h;
	private int hExtended;
	private String lastContent;
	
	private final int MAX_LENGTH_DIFF = 10;
	private final int MAX_TAGS_DISPLAYED = 5;
	private final int HEIGHT_TAGS = 40;
	private final int MARGING = 10;
	private final int CURVE = 20;
	private final int WIDTH_TAG_COLOR = 40;
	
	private Textfield textfield;
	private ArrayList<Tag> tags;
	private ArrayList<Tag> tagsDisplayed;
	private Panel panel;
	
	public TagFilter(Panel panel)
	{
		this.panel = panel;
		x = 0;
		y = 0;
		w = 0;
		h = 0;
		hExtended = 0;
		lastContent = "";
		
		textfield = new Textfield("Filtrer par tags...");
		tags = new ArrayList<>();
		tagsDisplayed = new ArrayList<>();
	}
	
	public ArrayList<Tag> getTags()
	{
		return tags;
	}
	
	public void deleteAllTags()
	{
		tags = new ArrayList<>();
	}
	
	public Textfield getTextfield()
	{
		return textfield;
	}
	
	/**
	 * Update Tag filter position and dimension
	 * @param x : Filter x coordinate
	 * @param y : Filter y coordinate
	 * @param w : Filter width
	 * @param h : Filter height
	 */
	public void adapt(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		textfield.adapt(x, y, w, h);
	}
	
	/**
	 * Update the text field depending on the mouse activity
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		textfield.handleClick(mouseX, mouseY);
	}
	
	/**
	 * Remove a tag in the filter list
	 * @param tag : Tag to be removed
	 */
	public void removeTag(Tag tag)
	{
		if (tags.contains(tag))
		{
			tags.remove(tag);
		}
	}
	
	/**
	 * Auto completion to search for the maximum best 5 tags
	 * @return a list of tags corresponding to the current content of the text field
	 */
	private ArrayList<Tag> checkForOptions()
	{
		// Result
		ArrayList<Tag> bestOptions = new ArrayList<>();
		
		// Add all tags containing the text substring
		if (!textfield.getContent().equals(""))
		{
			ArrayList<Tag> tagsLibrary = TagLibrary.getInstance().getUsedTags();
			Map<Tag, Integer> options = new HashMap<>();
	
			for (int i = 0; i < tagsLibrary.size(); i++)
			{
				Tag tag = tagsLibrary.get(i);
				int distance = Math.abs(tag.getDescription().length() - textfield.getContent().length());
				if (tag.getDescription().toLowerCase().contains(textfield.getContent().toLowerCase()) && distance < MAX_LENGTH_DIFF
						&& !tags.contains(tag))
				{
					options.put(tag, distance);
				}
			}
			
			// Sorting with streams
			options = options.entrySet().parallelStream()
					.sorted(Map.Entry.<Tag, Integer>comparingByValue())
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(),
	                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
			// Add the limited amount of valid tags
			for (Map.Entry<Tag, Integer> entry : options.entrySet())
			{
				if (bestOptions.size() >= MAX_TAGS_DISPLAYED)
				{
					break;
				}
				bestOptions.add(entry.getKey());
			}
		}
		return bestOptions;
	}
	
	/**
	 * Update the display
	 */
	public void updateTags()
	{
		tagsDisplayed = checkForOptions();
		hExtended = h + MARGING + tagsDisplayed.size() * (HEIGHT_TAGS + MARGING);
		textfield.addHeightExtension(hExtended);
		panel.updateSymbolDisplayed();
	}
	
	/**
	 * Draw the tag filter
	 * @param g : Graphics context
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void draw(Graphics g, int mouseX, int mouseY)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		// Update tags if text field's content change
		if (!textfield.getContent().equals(lastContent))
		{
			lastContent = textfield.getContent();
			updateTags();
		}

		// Draw the suggested tags
		if (tagsDisplayed.size() > 0 && textfield.isSelected())
		{
			// Background
			g.setColor(Color.WHITE);
			g.fillRoundRect(x + MARGING, y, w - 2 * MARGING, hExtended, CURVE, CURVE);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x + MARGING, y, w - 2 * MARGING, hExtended, CURVE, CURVE);
			
			// Draw tags
			for (int i = 0; i < tagsDisplayed.size(); i++)
			{
				Tag tag = tagsDisplayed.get(i);
				
				int xTag = x + 2 * MARGING;
				int yTag = y + textfield.getHeight() + MARGING + i * (HEIGHT_TAGS + MARGING);
				int wTag = w - 4 * MARGING;
				int hTag = HEIGHT_TAGS;
				int fontStyle;
				if (mouseX > xTag && mouseX < xTag + wTag && mouseY > yTag && mouseY < yTag + hTag)
				{
					g2.setStroke(new BasicStroke(4));
					fontStyle = Font.BOLD;
					
					if (Mouse.leftClicked)
					{
						tags.add(tag);
						updateTags();
					}
				} else
				{
					g2.setStroke(new BasicStroke(2));
					fontStyle = Font.PLAIN;
				}
				g.setFont(new Font("Arial", fontStyle, 20));
				
				g.setColor(Color.WHITE);
				g.fillRect(xTag, yTag, wTag, hTag);
				g.setColor(Color.BLACK);
				g.drawRect(xTag, yTag, wTag, hTag);
				
				g.setColor(tag.getColor());
				g.fillRect(xTag, yTag, WIDTH_TAG_COLOR, hTag);
				g.setColor(Color.BLACK);
				g.drawRect(xTag, yTag, WIDTH_TAG_COLOR, hTag);
			
				g.drawString(tag.getDescription(), x + 3 * MARGING + WIDTH_TAG_COLOR, y + textfield.getHeight() + (int)(3.7 * MARGING) + i * (HEIGHT_TAGS + MARGING));
			}
		}

		g2.setStroke(new BasicStroke(2));
		textfield.draw(g, Mouse.leftClicked, Mouse.leftPressed);
	}
}
