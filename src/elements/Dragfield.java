package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import content.Symbol;
import content.Tag;
import content.TagLibrary;
import panels.PanelSymbol;

/**
 * Field with tag lists that enables Drag and Drop operations between them
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Dragfield 
{
	private int x;
	private int y;
	private int w;
	private int h;
	private boolean scrollbarsInUse;
	private boolean addingTag;
	
	private final int ARC_DIAMETER = 10;
	private final int MARGING = 10;
	private final int HEIGHT_TAG = 60;
	private final int TRANSPARENCY = 120;
	private final int HEIGHT_ADD_TAG = 80;
	private final int SIZE_SYMBOLS_BUTTON = 60;
	private final int SIZE_COLOR_PICKER = 60;
	private final int OFFSET_IMAGE = 10;
	private final String ADD_TAG_MESSAGE = "Ajouter un nouveau tag";
	private final int WIDTH_TEXTFIELD = 300;
	
	private TagList attachedTags;
	private TagList libraryTags;
	private Tag dragedTag;
	private Button buttonAddTag;
	private Button buttonConfirmAdding;
	private Textfield textfield;
	private ColorPicker colorPicker;
	private PanelSymbol panelSymbol;
	
	public Dragfield(PanelSymbol panelSymbol, Symbol symbol)
	{
		this.panelSymbol = panelSymbol;
		x = 0;
		y = 0;
		w = 0;
		h = 0;
		scrollbarsInUse = false;
		addingTag = false;
		dragedTag = null;

		attachedTags = new TagList(panelSymbol, this, "Tags du symbole", symbol.getTags(), HEIGHT_TAG, false);
		libraryTags = new TagList(panelSymbol, this, "Bibliothèque", TagLibrary.getInstance().getTags(), HEIGHT_TAG, true);
		buttonAddTag = new Button(generateImage("/pictures/add.png", (SIZE_SYMBOLS_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_SYMBOLS_BUTTON, SIZE_SYMBOLS_BUTTON, 5, null);
		buttonConfirmAdding = new Button(generateImage("/pictures/add.png", (SIZE_SYMBOLS_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_SYMBOLS_BUTTON, SIZE_SYMBOLS_BUTTON, 5, null);
		textfield = new Textfield("Nom de tag");
		colorPicker = new ColorPicker();
	}
	
	public boolean isScrollbarsInUse()
	{
		return scrollbarsInUse;
	}
	
	public boolean tagSelected()
	{
		return dragedTag != null;
	}
	
	public void resetDragedTag()
	{
		dragedTag = null;
	}
	
	/**
	 * Helper method to generate an image
	 * @param path : Path of the image
	 * @param size : Desired size of the generated image
	 * @return the image from it's path and size
	 */
	private Image generateImage(String path, int size)
	{
		return new ImageIcon(Main.class.getResource(path)).getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
	}
	
	/**
	 * Associate a symbol to the Drag field
	 * @param symbol : New symbol to associate
	 */
	public void changeSymbol(Symbol symbol)
	{
		attachedTags.changeTags(symbol.getTags());
	}
	
	/**
	 * Update the drag field dimension and position
	 * @param x : Drag field x coordinate
	 * @param y : Drag field y coordinate
	 * @param w : Drag field width
	 * @param h : Drag field height
	 */
	public void adapt(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		// Adapt all TagLists in the DragField
		int wTagLists = (w - 3 * MARGING) / 2;
		int hTagLists = h - 5 * MARGING - HEIGHT_ADD_TAG;
		attachedTags.adapt(x + MARGING, y + 4 * MARGING, wTagLists, hTagLists);
		libraryTags.adapt(x + 2 * MARGING + wTagLists, y + 4 * MARGING, wTagLists, hTagLists);
		
		// Dimension of the DragField's footer
		int totalLength = 2 * SIZE_COLOR_PICKER + 2 * MARGING + WIDTH_TEXTFIELD;
		int xAdd = (w - totalLength) / 2;
		
		// Adapt the footer containing the TextField, ColorPicker and confirmation Button
		textfield.adapt(x + xAdd, y + h - (int)(1.5 * MARGING) - textfield.getHeight(), WIDTH_TEXTFIELD, SIZE_COLOR_PICKER);
		colorPicker.adapt(x + xAdd + textfield.getWidth() + MARGING, y + h - (int)(1.5 * MARGING) - SIZE_COLOR_PICKER,
				SIZE_COLOR_PICKER, SIZE_COLOR_PICKER);
		buttonConfirmAdding.adapt(x + xAdd + textfield.getWidth() + buttonConfirmAdding.getWidth() + 2 * MARGING, 
				y + h - (int)(1.5 * MARGING) - buttonConfirmAdding.getHeight());
	}
	
	/**
	 * Scroll the selected TagList in the DragField
	 * @param rotation : Rotation of the scroll wheel
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void scrollTags(double rotation, int mouseX, int mouseY)
	{
		// Check if the mouse is in one of the TagLists
		if (attachedTags.isMouseIn(mouseX, mouseY))
		{
			attachedTags.scrollTags(rotation);
		}
		if (libraryTags.isMouseIn(mouseX, mouseY))
		{
			libraryTags.scrollTags(rotation);
		}
	}
	
	/**
	 * Give all TextFields to add them to the Frame listeners
	 * @return all TextFields in the DragField 
	 */
	public ArrayList<Textfield> getTextfields()
	{
		ArrayList<Textfield> textfields = new ArrayList<>();
		textfields.add(textfield);
		textfields.add(attachedTags.getTextField());
		textfields.add(libraryTags.getTextField());
		return textfields;
	}
	
	/**
	 * Draw the DragField in the given graphic context
	 * @param g : Graphic context
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 * @param hasFocus : Focus of the Frame
	 */
	public void draw(Graphics g, int mouseX, int mouseY, boolean hasFocus)
	{
		// Background
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, w, h, ARC_DIAMETER, ARC_DIAMETER);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, w, h, ARC_DIAMETER, ARC_DIAMETER);
		
		// Check if a tag is selected in one of the TagLists
		Tag selectedTag = attachedTags.getSelectedTag();
		if (selectedTag == null) selectedTag = libraryTags.getSelectedTag();
		
		// If a tag is selected, delete it from it's TagList
		if (selectedTag != null && Mouse.leftPressed)
		{
			dragedTag = selectedTag;
			if (attachedTags.getSelectedTag() == null)
			{
				libraryTags.deleteTag(dragedTag);
			} else
			{
				attachedTags.deleteTag(dragedTag);
			}
		}
		
		// Adding a new tag
		if (addingTag)
		{
			// Draw the footer of the DragField
			textfield.draw(g, Mouse.leftClickedSymbol, Mouse.leftPressedSymbol);
			if (hasFocus) textfield.handleClick(mouseX, mouseY);
			colorPicker.draw(g);
			if (hasFocus) colorPicker.handleClick(mouseX, mouseY, Mouse.leftClickedSymbol);
			buttonConfirmAdding.draw(g);
			if (hasFocus) buttonConfirmAdding.handleClick(mouseX, mouseY, Mouse.leftClickedSymbol);
			
			// If the confirmation button is clicked
			if (buttonConfirmAdding.isClicked())
			{
				// Check the conditions
				Tag newTag = new Tag(colorPicker.getColor(), textfield.getContent());
				if (textfield.getContent().length() < 3)
				{
					panelSymbol.addMessage("Minimum 3 caractères", true);
				} else if (!libraryTags.isTagUnique(newTag))
				{
					panelSymbol.addMessage("Nom de tag déjà utilisé", true);
				} else
				{
					// Add the new tag
					addingTag = false;
					TagLibrary.getInstance().addTag(newTag);
					libraryTags.addTag(newTag);
					textfield.setContent("");
				}
			}
		} else
		{
			// Draw the add tag button to open the add footer of the DragField
			buttonAddTag.draw(g);
			if (hasFocus) buttonAddTag.handleClick(mouseX, mouseY, Mouse.leftClickedSymbol);
			
			// Dimension of the footer
			int lengthMessage = g.getFontMetrics().stringWidth(ADD_TAG_MESSAGE);
			int totalLength = lengthMessage + MARGING + buttonAddTag.getWidth();
			int xAddTag = (w - totalLength) / 2 + 5 * MARGING;
			
			// Adapt the footer
			g.drawString(ADD_TAG_MESSAGE, xAddTag, y + h - (int)(4 * MARGING));
			buttonAddTag.adapt(xAddTag + lengthMessage + MARGING, y + h - buttonAddTag.getHeight() - (int)(1.5 * MARGING));
			
			// Open the adding interface with a random color for the new tag by default
			if (buttonAddTag.isClicked())
			{
				addingTag = true;
				textfield.select();
				colorPicker.randomize();
			}
		}
		
		// Check if a scroll bar is currently used
		scrollbarsInUse = false;
		if (attachedTags.isScrollbarUsed() || libraryTags.isScrollbarUsed())
		{
			scrollbarsInUse = true;
		}
		
		// Inform the TagLists if one of the scroll bars is used
		attachedTags.setScollbarsInUse(scrollbarsInUse);
		libraryTags.setScollbarsInUse(scrollbarsInUse);
		
		// Put a flag on the TagLists to tell them that a tag has been placed
		if (!Mouse.leftPressedSymbol && dragedTag != null)
		{
			attachedTags.insertTagFlag();
			libraryTags.insertTagFlag();
		}
		
		// Transparent background when the user is Drag and Dropping a tag
		if (tagSelected())
		{
			g.setColor(new Color(0, 0, 0, 120));
			g.fillRect(x, y, w, h);
		}
		
		// Draw the TagLists
		attachedTags.draw(g, mouseX, mouseY, dragedTag, libraryTags.getTags());
		libraryTags.draw(g, mouseX, mouseY, dragedTag, attachedTags.getTags());
		
		// Draw the tag that is getting Drag and Dropped with a transparency mask
		if (tagSelected())
		{
			int wTag = (w - 3 * MARGING) / 2 - 2 * MARGING;
			int hTag = HEIGHT_TAG;
			
			g.setColor(new Color(255, 255, 255, TRANSPARENCY));
			g.fillRect(mouseX - wTag / 2, mouseY - hTag / 2, wTag, hTag);

			g.setColor(new Color(0, 0, 0, TRANSPARENCY));
			g.drawRect(mouseX - wTag / 2, mouseY - hTag / 2, wTag, hTag);
			
			Color c = dragedTag.getColor();
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), TRANSPARENCY));
			g.fillRect(mouseX - wTag / 2, mouseY - hTag / 2, 3 * MARGING, hTag);

			g.setColor(new Color(0, 0, 0, TRANSPARENCY));
			g.drawRect(mouseX - wTag / 2, mouseY - hTag / 2, 3 * MARGING, hTag);
			
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString(dragedTag.getDescription(), mouseX - wTag / 2 + 4 * MARGING, mouseY - hTag / 2 + (int)(0.64 * HEIGHT_TAG));
		} else 
		{
			// If no tag is getting Drag and Dropped, update TagLists states
			if (hasFocus) attachedTags.handleClick(mouseX, mouseY);
			if (hasFocus) libraryTags.handleClick(mouseX, mouseY);
		}
	}
}
