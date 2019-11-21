package panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import content.AssociationsHandler;
import content.Card;
import content.FileChooser;
import content.ImageLoader;
import content.PDFCreator;
import content.SetSaver;
import content.Settings;
import content.Symbol;
import content.SymbolsLoader;
import content.SymbolsRefactor;
import content.Tag;
import content.TagLibrary;
import elements.Button;
import elements.LoadingBar;
import elements.Main;
import elements.MessageHandler;
import elements.Mouse;
import elements.Scrollbar;
import elements.Selector;
import elements.TagFilter;
import elements.Textfield;
import elements.Widget;
import elements.Window;
import enums.CardShape;
import frames.Frame;
import frames.FrameAbout;
import frames.FrameOptions;
import frames.FrameSymbol;

/**
 * Panel for the Main Frame of the program
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Panel extends JPanel implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private Window fabWindow;
	private Window previewWindow;
	private Scrollbar scrollbar;
	private Selector selectorSymbols;
	private Button leftCard;
	private Button rightCard;
	private Button printerButton;
	private Button optionsButton;
	private Button aboutButton;
	private Button symbolsButton;
	private PDFCreator pdfCreator;
	private SymbolsLoader loader;
	private FrameSymbol frameSymbol;
	private FrameOptions frameOptions;
	private FrameAbout frameAbout;
	private MessageHandler messageHandler;
	private Image emptySymbolsImage;
	private Image loadingImage;
	private String emptySymbolsMessage;
	private TagFilter tagFilter;
	
	private ArrayList<Card> cards;
	private ArrayList<Symbol> symbols;
	private ArrayList<Symbol> filteredSymbols;
	private ArrayList<Symbol> selectedSymbols;
	private ArrayList<Widget> widgets;

	private final double PERCENT_WIDTH_USAGE = 0.9;
	private final int GAP_BETWEEN_SYMBOLS = 20;
	private final int SIZE_SYMBOLS = 90;
	private final int SIZE_PREVIEW_SYMBOLS = 60;
	private final int SIZE_ARROW_BUTTON = 60;
	private final int SIZE_DECK_BUTTON = 60;
	private final int SIZE_SYMBOLS_BUTTON = 60;
	private final int OFFSET_IMAGE = 10;
	private final int WIDTH_FILTER = 350;
	private final int HEIGHT_FILTER = 60;
	private final int SIZE_TAG = 40;
	private final int SIZE_TAG_DEPLOYED = 300;
	private final int MARGING_TAGS = 10;
	private final boolean DISPLAY_FPS = false;
	
	private int frameWidth;
	private int frameHeight;
	private int xSymbols;
	private int ySymbols;
	private int widthSymbols;
	private int heightSymbols;
	private int x0;
	private int y0;
	private int columns;
	private int rows;
	private int mouseX;
	private int mouseY;
	private int symbolIndex;
	private int xPreview;
	private int yPreview;
	private int nbSymbolsDisplayed;
	private int nbCards;
	private boolean savedSelection;
	private boolean controlKey;
	private boolean firstTimeControlKey;
	private boolean emptySymbols;
	private boolean isLoading;
	private boolean hasFocus;
	private Dimension posEmptySymbolsImage;
	private Dimension posEmptySymbolsMessage;
	private int emptySymbolsMessageSize;
	private int xRectSelection;
	private int yRectSelection;
	private int fps;
	private int counter;
	private long lastTime;
	private int maxTags;

	public Panel(int frameWidth, int frameHeight)
	{
		// Parameters
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Integers
		columns = 0;
		rows = 0;
		symbolIndex = 0;
		xPreview = 0;
		yPreview = 0;
		nbSymbolsDisplayed = 0;
		nbCards = 0;
		xRectSelection = 0;
		yRectSelection = 0;
		emptySymbolsMessageSize = 0;
		fps = 0;
		counter = 0;
		lastTime = 0;
		maxTags = 0;
		
		// Booleans
		savedSelection = false;
		controlKey = false;
		firstTimeControlKey = true;
		emptySymbols = false;
		isLoading = false;
		hasFocus = true;
		
		// Symbols and Persistence 
		loader = new SymbolsLoader(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop", this, SIZE_SYMBOLS);
		symbols = loader.load(loader.getDirectory(), 0);
		SymbolsRefactor.getInstance().setDirectory(loader.getDirectory());
		AssociationsHandler.getInstance().setDirectory(loader.getDirectory());
		AssociationsHandler.getInstance().read(symbols);
		TagLibrary.getInstance().setDirectory(loader.getDirectory());
		TagLibrary.getInstance().read();
		if (symbols.size() == 0) emptySymbols = true;
		else emptySymbols = false;
		filteredSymbols = new ArrayList<>();
		selectedSymbols = new ArrayList<>();
		symbolsButton = new Button(generateImage("/pictures/folder.png", (SIZE_SYMBOLS_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_SYMBOLS_BUTTON, SIZE_SYMBOLS_BUTTON, 5, previewWindow);
		emptySymbolsImage = generateImage("/pictures/empty.png", 50);
		emptySymbolsMessage = "Symboles introuvables";
		loadingImage = generateImage("/pictures/spinner.png", 80);
		
		// Components and widgets
		widgets = new ArrayList<>();
		fabWindow = new Window("Fabrication", true);
		widgets.add(fabWindow);
		previewWindow = new Window("Symboles  ", false);
		widgets.add(previewWindow);
		messageHandler = new MessageHandler();
		widgets.add(messageHandler);
		selectorSymbols = new Selector("Symboles par carte", 40, 77, new String[]{"2", "3", "4", "6", "8"}, fabWindow);
		widgets.add(selectorSymbols);
		
		scrollbar = new Scrollbar();
		leftCard = new Button(generateImage("/pictures/leftArrow.png", (SIZE_ARROW_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_ARROW_BUTTON, SIZE_ARROW_BUTTON, 5, previewWindow);
		rightCard = new Button(generateImage("/pictures/rightArrow.png", (SIZE_ARROW_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_ARROW_BUTTON, SIZE_ARROW_BUTTON, 5, previewWindow);
		printerButton = new Button(generateImage("/pictures/printer.png", (SIZE_DECK_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_ARROW_BUTTON, SIZE_ARROW_BUTTON, 5, fabWindow);
		optionsButton = new Button(generateImage("/pictures/settings.png", (SIZE_DECK_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_ARROW_BUTTON, SIZE_ARROW_BUTTON, 5, fabWindow);
		aboutButton = new Button(generateImage("/pictures/info.png", (SIZE_DECK_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_ARROW_BUTTON, SIZE_ARROW_BUTTON, 5, fabWindow);
		tagFilter = new TagFilter(this);
		
		// Adapt and initialize
		adaptWindows(frameWidth, frameHeight);
		computeNumberOfCards();
		updateSymbolDisplayed();
		
		// Associate the panel and it's filter to the TagLibrary
		TagLibrary.getInstance().associateTagFilter(tagFilter);
		TagLibrary.getInstance().associatePanel(this);
	}
	
	public ArrayList<Symbol> getSymbols()
	{
		return symbols;
	}
	
	public void setIsLoading(boolean isLoading)
	{
		this.isLoading = isLoading;
	}
	
	public void setFocus(boolean focus)
	{
		hasFocus = focus;
	}
	
	public Textfield getTexfield()
	{
		return tagFilter.getTextfield();
	}
	
	/**
	 * Add a message to the Panel Message Handler
	 * @param message : Message to display
	 * @param isError : Error type of the message
	 */
	public void addMessage(String message, boolean isError)
	{
		messageHandler.addMessage(message, isError);
	}
	
	/**
	 * Add a symbol to the list of selected symbols
	 * @param symbol : Symbol selected
	 * @return true if the symbol has been added successfully
	 */
	public boolean addSelectedSymbol(Symbol symbol)
	{
		if (selectedSymbols.size() < nbCards)
		{
			selectedSymbols.add(symbol);
			return true;
		} else 
		{
			messageHandler.addMessage("Trop de symboles sélectionnés", false);
			return false;
		}
		
	}
	
	/**
	 * Remove a symbol from the selected symbols list
	 * @param symbol : Symbol to be removed
	 */
	public void removeSelectedSymbol(Symbol symbol)
	{
		selectedSymbols.remove(symbol);
		if (symbolIndex >= selectedSymbols.size()) symbolIndex = selectedSymbols.size() - 1;
		if (symbolIndex < 0) symbolIndex = 0;
	}
	
	/**
	 * Open the symbol edition Frame
	 * @param symbol : Symbol to edit
	 */
	public void editSelectedSymbol(Symbol symbol)
	{
		// Check that the symbol is loaded
		if (symbol.isLoaded())
		{
			// Check if a symbol is already being edited
			if (frameSymbol == null)
			{
				frameSymbol = new FrameSymbol(symbol);
			} else
			{
				if (frameSymbol.isClosed())
				{
					// New instance of the Frame
					frameSymbol = new FrameSymbol(symbol);
				} else
				{
					// Modify the symbol attached to the Frame
					frameSymbol.changeSymbol(symbol);
					frameSymbol.toFront();
					frameSymbol.setState(Frame.NORMAL);
				}
			}
		} else 
		{
			messageHandler.addMessage("Le symbole n'a pas fini de charger", false);
		}
	}
	
	/**
	 * Helper method to generate an image
	 * @param path : Path of the image
	 * @param size : Size of the image
	 * @return the generated image
	 */
	private Image generateImage(String path, int size)
	{
		Image image = new ImageIcon(Main.class.getResource(path)).getImage();
		return image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
	}
	
	/**
	 * Compute the number of cards needed based on the symbols per card
	 */
	public void computeNumberOfCards()
	{
		// Number of cards
		int nbSymByCard = Integer.parseInt(selectorSymbols.getValue());
		nbCards = (int) Math.pow(nbSymByCard , 2) - nbSymByCard  + 1;
		
		// If the new number of selected symbol exceed the number of cards to select, automatically remove the last symbols selected to fit
		if (selectedSymbols.size() > nbCards)
		{
			// Compute the difference
			int diff = Math.abs(selectedSymbols.size() - nbCards);
			
			// Remove the last symbols in the list
			for (int i = 0; i < diff; i++)
			{
				Symbol symbol = selectedSymbols.remove(selectedSymbols.size() - 1);
				symbol.deselect();
			}
			
			// Warn the user
			messageHandler.addMessage("Trop de symboles, derniers supprimés", false);
		}
	}
	
	/**
	 * Algorithm to generate cards based on the current settings of the program.
	 * Based on the Python version available on the Dobble wiki page
	 */
	private void generateCards()
	{
		if (selectedSymbols.size() == 0) symbolIndex = 0;
		else symbolIndex = 1;
		
		// Settings
		int nbSymByCard = Integer.parseInt(selectorSymbols.getValue());
		nbCards = (int) Math.pow(nbSymByCard , 2) - nbSymByCard  + 1;
		cards = new ArrayList<>();
		int n = nbSymByCard - 1;
		ArrayList<ArrayList<ArrayList<Integer>>> t = new ArrayList<>();
		
		// Step 1
		t.add(new ArrayList<>());
		for (int i = 0; i < n; i++)
		{
			t.get(0).add(new ArrayList<>());
			for (int j = 0; j < n; j++)
			{
				t.get(0).get(i).add((j + 1) + (i * n));
			}	
		}
		
		// Step 2
		for (int ti = 0; ti < n - 1; ti++)
		{
			t.add(new ArrayList<>());
			for (int i = 0; i < n; i++)
			{
				t.get(ti + 1).add(new ArrayList<>());
				for (int j = 0; j < n; j++)
				{
					t.get(0).get((((ti + 1) * j) % n));
					t.get(ti + 1).get(i).add(t.get(0).get((((ti + 1) * j) % n)).get((i + j) % n));
				}	
			}
		}
		
		// Step 3
		t.add(new ArrayList<>());
		for (int i = 0; i < n; i++)
		{
			t.get(t.size() - 1).add(new ArrayList<>());
			for (int j = 0; j < n; j++)
			{
				t.get(t.size() - 1).get(i).add(t.get(0).get(j).get(i));
			}	
		}
		
		// Step 4
		for (int i = 0; i < n; i++)
		{
			t.get(0).get(i).add(nbCards - n);
			t.get(n).get(i).add(nbCards - n + 1);
			for (int ti = 0; ti < n - 1; ti++)
			{
				t.get(ti + 1).get(i).add(nbCards - n + 1 + ti + 1);
			}	
		}
		
		// Step 5
		t.add(new ArrayList<>());
		t.get(t.size() - 1).add(new ArrayList<>());
		for (int i = 0; i < nbSymByCard; i++)
		{
			t.get(t.size() - 1).get(0).add(i + nbCards - n);
		}
		
		// Creation of the cards based on the generated indices
		for (int i = 0; i < t.size(); i++)
		{
			for (int j = 0; j < t.get(i).size(); j++)
			{
				// Conversion of the List to an Array of selected symbols from indices
				int size = t.get(i).get(j).size();
				Symbol[] symbols = new Symbol[size];
				
				for (int m = 0; m < size; m++)
				{
					symbols[m] = selectedSymbols.get(t.get(i).get(j).get(m) - 1);
				}
				
				// Create cards
				cards.add(new Card(CardShape.SQUARE, symbols, (int)((Settings.cardSize * Settings.cmToInch) * Settings.DPI)));
			}
		}
	}
	
	/**
	 * Called by a resize event to adapt the application's content
	 * @param frameWidth : Width of the frame
	 * @param frameHeight : Height of the frame
	 */
	public void adaptWindows(int frameWidth, int frameHeight)
	{
		// Adapt the widgets
		for (int i = 0; i < widgets.size(); i++)
		{
			widgets.get(i).adapt(frameWidth, frameHeight);
		}
		
		// Adapt panel background
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Adapt the symbols border
		xSymbols = fabWindow.getX() + 50;
		ySymbols = fabWindow.getY() + 100;
		widthSymbols = fabWindow.getWidth() - 100;
		heightSymbols = fabWindow.getHeight() - 150;
		
		// Adapt the deck operations
		int shift = (int)((1 - PERCENT_WIDTH_USAGE) * frameWidth) / 2;
		printerButton.adapt(frameWidth - shift - SIZE_DECK_BUTTON, 20);
		optionsButton.adapt(frameWidth - shift - 2 * SIZE_DECK_BUTTON - 10, 20);
		aboutButton.adapt(frameWidth - shift - 3 * SIZE_DECK_BUTTON - 20, 20);
		
		// Adapt the symbols
		int scrollWidth = 50;
		columns = (int) Math.floor((widthSymbols - scrollWidth) / (SIZE_SYMBOLS + GAP_BETWEEN_SYMBOLS));
		rows = (int) Math.floor(heightSymbols / (SIZE_SYMBOLS + GAP_BETWEEN_SYMBOLS));
		y0 = ySymbols + (heightSymbols - rows * SIZE_SYMBOLS - (rows - 1) * GAP_BETWEEN_SYMBOLS) / 2;
		symbolsButton.adapt(fabWindow.getX() + fabWindow.getWidth() / 2 - symbolsButton.getWidth() / 2, fabWindow.getY() + 30);
		
		// Components
		scrollbar.adapt(xSymbols + widthSymbols - 30, ySymbols + 10, 20, heightSymbols - 20);
		scrollbar.setHeightButton(rows, (int) Math.ceil(1.0 * filteredSymbols.size() / columns)); 
		tagFilter.adapt(xSymbols + widthSymbols - WIDTH_FILTER, ySymbols - HEIGHT_FILTER - 10, WIDTH_FILTER, HEIGHT_FILTER);
		maxTags = fabWindow.getHeight() / (SIZE_TAG + MARGING_TAGS) - 2;
		
		// Cards preview
		xPreview = previewWindow.getX() + previewWindow.getWidth() / 2 - SIZE_PREVIEW_SYMBOLS / 2;
		yPreview = previewWindow.getY() + previewWindow.getHeight() / 2 - SIZE_PREVIEW_SYMBOLS / 2;
		leftCard.adapt(previewWindow.getX() + 10, previewWindow.getY() + previewWindow.getHeight() / 2 - leftCard.getHeight() / 2);
		rightCard.adapt(previewWindow.getX() + previewWindow.getWidth() - rightCard.getWidth() - 10, previewWindow.getY() + previewWindow.getHeight() / 2 - rightCard.getHeight() / 2);
		
		// Visibility of the ScrollBar
		if (filteredSymbols.size() > columns * rows)
		{
			x0 = (frameWidth - scrollWidth - columns * SIZE_SYMBOLS - (columns - 1) * GAP_BETWEEN_SYMBOLS) / 2;
			scrollbar.setVisibilty(true);
		} else
		{
			x0 = (frameWidth - columns * SIZE_SYMBOLS - (columns - 1) * GAP_BETWEEN_SYMBOLS) / 2;
			scrollbar.setVisibilty(false);
		}
		
		// LoadingBar
		LoadingBar.getInstance().adapt(frameWidth, frameHeight);
	}
	
	/**
	 * Compute the visibility and the position of the symbols
	 */
	private void adaptSymbols()
	{
		for (int i = 0; i < filteredSymbols.size(); i++)
		{
			// Filtered Symbol position
			Symbol filteredSymbol = filteredSymbols.get(i);
			filteredSymbol.replace(x0 + (i % columns) * (SIZE_SYMBOLS + GAP_BETWEEN_SYMBOLS), 
								   y0 + ((i / columns) - scrollbar.getIndex()) * (SIZE_SYMBOLS + GAP_BETWEEN_SYMBOLS));
			
			// Visibility depending on the ScrollBar position
			if (scrollbar.isRowInRange((i / columns)))
			{
				filteredSymbol.setActivation(true);
			}
			else
			{
				filteredSymbol.setActivation(false);
			}
		}
		
		// Check if the ImageLoader can load images preemptively
		int indexFirstSymbolDisplayed = scrollbar.getIndex() * columns;
		if (ImageLoader.getInstance().isReady())
		{
			if (indexFirstSymbolDisplayed >= 0 && indexFirstSymbolDisplayed < symbols.size())
			{
				for (int i = indexFirstSymbolDisplayed; i < symbols.size(); i++)
				{
					Symbol symbolPreemptive = symbols.get(i);
					if (!symbolPreemptive.isLoaded() && !symbolPreemptive.isImageLoading())
					{
						symbolPreemptive.setImageLoading(true);
						ImageLoader.getInstance().loadSymbol(symbolPreemptive);
						break;
					}
				}
			}
		}
		
		// Delete terminated tasks in the Loader
		ImageLoader.getInstance().manageFutures();
		
		// Update the position of the empty symbol list message
		posEmptySymbolsImage = new Dimension(fabWindow.getX() + fabWindow.getWidth() / 2 - emptySymbolsImage.getWidth(null) / 2, 
				fabWindow.getY() + fabWindow.getHeight() / 2 - emptySymbolsImage.getHeight(null) / 2);
		posEmptySymbolsMessage = new Dimension(fabWindow.getX() + fabWindow.getWidth() / 2 - emptySymbolsMessageSize / 2, 
				fabWindow.getY() + fabWindow.getHeight() / 2 + 50);
	}
	
	/**
	 * Called on a scroll event to adapt the symbols
	 * @param rotation : Wheel rotation
	 */
	public void scrollSymbols(double rotation)
	{
		scrollbar.scroll(rotation);
	}
	
	
	/**
	 * Draw the selected symbols at the bottom of the Frame
	 * @param g : Graphic context
	 */
	public void drawSelectedSymbols(Graphics g)
	{
		// Compute the number of symbols that can be displayed
		nbSymbolsDisplayed = Math.min((previewWindow.getWidth() - leftCard.getWidth() - rightCard.getWidth()) / (SIZE_PREVIEW_SYMBOLS + GAP_BETWEEN_SYMBOLS), 
				(int) Math.floor(selectedSymbols.size()));
		
		// If symbols are selected
		if (selectedSymbols.size() > 0)
		{
			// Draw the symbols that are visible within the Frame
			int start = (int) -Math.floor(nbSymbolsDisplayed / 2.0);
			int limit = (int) Math.min(selectedSymbols.size(), nbSymbolsDisplayed);
			for (int i = start; i < start + limit; i++)
			{
				int index = (symbolIndex + i + nbSymbolsDisplayed * selectedSymbols.size()) % selectedSymbols.size();
				int xPreviewAdapted = xPreview + i * (int)(SIZE_PREVIEW_SYMBOLS + GAP_BETWEEN_SYMBOLS);
				if (nbSymbolsDisplayed % 2 == 0) 
				{
					xPreviewAdapted += SIZE_PREVIEW_SYMBOLS / 2 + GAP_BETWEEN_SYMBOLS / 2;
				}
				
				selectedSymbols.get(index).drawPreview(g, xPreviewAdapted, yPreview);
			}
		}
		
		if (nbSymbolsDisplayed < selectedSymbols.size())
		{
			// Number of the symbols selected
			g.setColor(Color.white);
			g.fillRect(previewWindow.getX() + previewWindow.getWidth() / 2 - 75, previewWindow.getY() + previewWindow.getHeight() - 20, 150, 40);
			g.setColor(Color.black);
			g.drawRect(previewWindow.getX() + previewWindow.getWidth() / 2 - 75, previewWindow.getY() + previewWindow.getHeight() - 20, 150, 40);
			
			String indexDisplay;
			if (selectedSymbols.size() == 0) indexDisplay = "0 / " + selectedSymbols.size();
			else indexDisplay = (symbolIndex + 1) + " / " + selectedSymbols.size();
			
			g.setFont(new Font("Arial", Font.ITALIC, 28));
			g.drawString(indexDisplay, previewWindow.getX() + previewWindow.getWidth() / 2 - 6 * indexDisplay.length(), previewWindow.getY() + previewWindow.getHeight() + 10);
		}
	}
	
	/**
	 * Draw the rectangle of multiple selections
	 * @param g : Graphic context
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	private void drawSelectionSymbols(Graphics g, int mouseX, int mouseY)
	{
		if (Mouse.leftPressed)
		{
			// Save mouse position on the first use
			if (!savedSelection)
			{
				savedSelection = true;
				xRectSelection = mouseX;
				yRectSelection = mouseY;
			}
			g.setColor(new Color(100, 100, 100, 150));
			
			// Compute the dimension
			int width = Math.abs(mouseX - xRectSelection);
			int height = Math.abs(mouseY - yRectSelection);
			
			// Define the leftmost and topmost position between the mouse coordinates and the rectange coordinates
			int xAbsRectSelection = xRectSelection;
			int yAbsRectSelection = yRectSelection;
			if (mouseX < xRectSelection) xAbsRectSelection = mouseX;
			if (mouseY < yRectSelection) yAbsRectSelection = mouseY;
			g.fillRect(xAbsRectSelection, yAbsRectSelection, width, height);
			
			// Preselect symbols that are bounded by the rectangle
			for (int i = 0; i < filteredSymbols.size(); i++)
			{
				Symbol symbol = filteredSymbols.get(i);
				filteredSymbols.get(i).setPreselect(symbol.isActivated() && symbol.isBounded(xAbsRectSelection, yAbsRectSelection, width, height));
			}
		} else
		{
			// Apply the modification of the multiple selection
			savedSelection = false;
			for (int i = 0; i < filteredSymbols.size(); i++)
			{
				Symbol symbol = filteredSymbols.get(i);
				if (symbol.isPreselected())
				{
					// Disable the early selection of symbols
					symbol.setPreselect(false);
					if (controlKey)
					{
						// Disable the selection of symbols if the control key is pushed
						symbol.deselect();
						if (selectedSymbols.contains(symbol)) selectedSymbols.remove(symbol);
					} else
					{
						if (selectedSymbols.size() < nbCards)
						{
							// Select symbols if the control key is not pushed and that there is enough cards number left to add
							symbol.select();
							if (!selectedSymbols.contains(symbol)) selectedSymbols.add(symbol);
						} else
						{
							messageHandler.addMessage("Trop de symboles sélectionnés", false);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Generate a PDF in the given file
	 * @param file : File to generate the PDF in
	 */
	public void saveSet(File file)
	{
		// Initialize Loading Bar
		LoadingBar.getInstance().init("Création des cartes", 2 * cards.size());
		
		// Create and run the PDFCreator
        pdfCreator = new PDFCreator(this, filterPDF(file.getPath()));
        setFocus(false);
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				pdfCreator.printCards(cards);
			}
		}).start();
	}
	
	/**
	 * Change the folder containing the symbols and update everything concerned about it
	 * @param folder : New folder containing the symbols
	 */
	public void changeSymbolsFolder(File folder)
	{
		// Set the directory of the SymbolsLoader
		loader.setDirectory(folder);
		
		// Loading animation on
		isLoading = true;
		
		// Save both DGAF and DGLF files
		AssociationsHandler.getInstance().save(symbols);
		TagLibrary.getInstance().save();
		
		// Load symbols
		symbols = loader.load(loader.getDirectory(), 0);
		
		// Loading animation off
		isLoading = false;
		
		// Set the directory of the TagLibrary and the AssociationsHandler and read the content of the new folder
		SymbolsRefactor.getInstance().setDirectory(loader.getDirectory());
		TagLibrary.getInstance().setDirectory(loader.getDirectory());
		TagLibrary.getInstance().read();
		AssociationsHandler.getInstance().setDirectory(loader.getDirectory());
		AssociationsHandler.getInstance().read(symbols);
		
		// Adapt the windows to the new content
		adaptWindows(frameWidth, frameHeight);
		if (symbols.size() == 0) emptySymbols = true;
		else emptySymbols = false;
		selectedSymbols = new ArrayList<>();
		
		// Reset the filter
		tagFilter.deleteAllTags();
		tagFilter.getTextfield().setContent("");
		
		// Shutdown previous images operations to reset the ImageLoader
		ImageLoader.getInstance().shutdown();
		
		// Update visually the new symbols
		updateSymbolDisplayed();
	}
	
	/**
	 * Update visually the symbols display
	 */
	public void updateSymbolDisplayed()
	{
		// The program will filter symbols with tags
		filteredSymbols = new ArrayList<>();
		ArrayList<Tag> tags = tagFilter.getTags();
		
		// Check for filter acceptance on all symbols
		for (int i = 0; i < symbols.size(); i++)
		{
			boolean valid = true;
			Symbol symbol = symbols.get(i);
			for (int j = 0; j < tags.size(); j++)
			{
				// Search for tag completion
				ArrayList<Tag> tagsSymbol = symbol.getTags();
				boolean tagPresent = false;
				for (int m = 0; m < tagsSymbol.size(); m++)
				{
					if (tagsSymbol.get(m).equals(tags.get(j)))
					{
						tagPresent = true;
					}
				}
				if (!tagPresent)
				{
					// Missing a tag used to filter
					valid = false;
				}
			}
			
			if (valid)
			{
				// Symbol valid and corresponding to all filters, add it
				filteredSymbols.add(symbols.get(i));
			}
		}
		
		// Adapt the window to it's new content
		adaptWindows(frameWidth, frameHeight);
	}
	
	/**
	 * Manage the tags selected to filter the symbols
	 * @param g : Graphics context
	 * @param mousex : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleSelectedTags(Graphics g, int mousex, int mouseY)
	{
		// Coordinate of the filters
		int xTags = fabWindow.getX() - SIZE_TAG / 2;
		int yTags = fabWindow.getY() + 3 * SIZE_TAG / 4;
		ArrayList<Tag> tags = tagFilter.getTags();
		
		// Maximum tags check
		if (tags.size() > maxTags && tags.size() > 0)
		{
			tags.remove(tags.size() - 1);
			messageHandler.addMessage("Nombre de tags maximum atteint", true);
		}
		
		// General suppression button
		if (tags.size() > 0)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			
			// Hover graphic change
			if (mouseX > xTags && mouseX < xTags + SIZE_TAG && mouseY > yTags && mouseY < yTags + SIZE_TAG)
			{
				g2.setStroke(new BasicStroke(5));
			}
			
			// Background
			g.setFont(new Font("Arial", Font.BOLD, 34));
			g.setColor(Color.WHITE);
			g.fillOval(xTags - 4, yTags - 6, SIZE_TAG + 8, SIZE_TAG + 8);
			g.setColor(Color.BLACK);
			g.drawOval(xTags - 4, yTags - 6, SIZE_TAG + 8, SIZE_TAG + 8);
			g.drawString("x", xTags + (int)(1.1 * MARGING_TAGS), yTags + (int)(2.8 * MARGING_TAGS));

			g2.setStroke(new BasicStroke(2));
			g.setFont(new Font("Arial", Font.BOLD, 20));
			if (mouseX > xTags && mouseX < xTags + SIZE_TAG && mouseY > yTags && mouseY < yTags + SIZE_TAG)
			{
				if (Mouse.leftClicked)
				{
					// Button clicked, all tag filters deleted
					tagFilter.deleteAllTags();
					tagFilter.updateTags();
				}
			}
		}
		
		// Tag filters
		for (int i = 0; i < tags.size(); i++)
		{
			int yTagsAdapted = yTags + (i + 1) * (SIZE_TAG + MARGING_TAGS);
			
			// Display the tag informations on mouse hover
			g.setFont(new Font("Arial", Font.ITALIC, 20));
			if (mouseX > xTags && mouseX < xTags + SIZE_TAG && mouseY > yTagsAdapted && mouseY < yTagsAdapted + SIZE_TAG)
			{
				g.setColor(Color.WHITE);
				g.fillRect(xTags, yTagsAdapted, SIZE_TAG_DEPLOYED, SIZE_TAG);
				g.setColor(Color.BLACK);
				g.drawRect(xTags, yTagsAdapted, SIZE_TAG_DEPLOYED, SIZE_TAG);

				g.drawString(tags.get(i).getDescription(), xTags + SIZE_TAG + 2 * MARGING_TAGS, yTagsAdapted + (int)(2.8 * MARGING_TAGS));
			}

			// Background
			g.setFont(new Font("Arial", Font.ITALIC, 20));
			g.setColor(tags.get(i).getColor());
			g.fillRect(xTags, yTagsAdapted, SIZE_TAG, SIZE_TAG);
			g.setColor(Color.BLACK);
			g.drawRect(xTags, yTagsAdapted, SIZE_TAG, SIZE_TAG);

			
			g.setFont(new Font("Arial", Font.BOLD, 20));
			if (mouseX > xTags && mouseX < xTags + SIZE_TAG && mouseY > yTagsAdapted && mouseY < yTagsAdapted + SIZE_TAG)
			{
				g.drawString("X", xTags + (int)(1.3 * MARGING_TAGS), yTagsAdapted + (int)(2.8 * MARGING_TAGS));
				
				if (Mouse.leftClicked)
				{
					// Delete the specific tag on button click
					tags.remove(tags.get(i));
					tagFilter.updateTags();
				}
			}
		}
	}
	
	/**
	 * Paint method from the JPanel
	 * @param g : Graphic context
	 */
	@Override
	public void paint(Graphics g)
	{	
		// Render Quality
		Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke(2));
	    RenderingHints qualityHints = new RenderingHints(
	    		  RenderingHints.KEY_ANTIALIASING,
	    		  RenderingHints.VALUE_ANTIALIAS_ON );
	    qualityHints.put(
	    		  RenderingHints.KEY_RENDERING,
	    		  RenderingHints.VALUE_RENDER_QUALITY );
	    g2.setRenderingHints(qualityHints);
		
		// Mouse location
		if (MouseInfo.getPointerInfo() != null && getLocationOnScreen() != null)
		{
			mouseX = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
			mouseY = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;
		}
	    
	    // Background
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
		
		// Deck operations
		printerButton.draw(g);
		if (hasFocus && !Mouse.leftPressed) printerButton.handleClick(mouseX, mouseY, Mouse.leftClicked);
		if (printerButton.isClicked())
		{
			if (selectedSymbols.size() == nbCards)
			{
				boolean imagesLoaded = true;
				for (int i = 0; i < selectedSymbols.size(); i++)
				{
					if (!selectedSymbols.get(i).isLoaded())
					{
						imagesLoaded = false;
					}
				}
				
				if (imagesLoaded)
				{
				generateCards();
				printerButton.setActivation(true);
				isLoading = true;
				hasFocus = false;
				new Thread(new SetSaver(this)).start();
				} else 
				{
					messageHandler.addMessage("Les symboles séléctionnés n'ont pas fini de charger", false);
				}
			} else
			{
				messageHandler.addMessage("Veuillez d'abord sélectionner " + nbCards + " symboles", false);
			}
		}
		
		// Options
		optionsButton.draw(g);
		if (hasFocus && !Mouse.leftPressed) optionsButton.handleClick(mouseX, mouseY, Mouse.leftClicked);
		if (optionsButton.isClicked())
		{
			if (frameOptions == null)
			{
				frameOptions = new FrameOptions();
			} else
			{
				if (frameOptions.isClosed())
				{
					frameOptions = new FrameOptions();
				} else
				{
					frameOptions.toFront();
					frameOptions.setState(Frame.NORMAL);
				}
			}
		}
		
		// About
		aboutButton.draw(g);
		if (hasFocus && !Mouse.leftPressed) aboutButton.handleClick(mouseX, mouseY, Mouse.leftClicked);
		if (aboutButton.isClicked())
		{
			if (frameAbout == null)
			{
				frameAbout = new FrameAbout();
			} else
			{
				if (frameAbout.isClosed())
				{
					frameAbout = new FrameAbout();
				} else
				{
					frameAbout.toFront();
					frameAbout.setState(Frame.NORMAL);
				}
			}
		}
		
		// Widgets
		for (int i = 0; i < widgets.size(); i++)
		{
			widgets.get(i).draw(g);
			if (widgets.get(i) instanceof Selector)
			{
				// Symbol window
				g.setColor(new Color(240, 240, 240));
				g.fillRect(xSymbols, ySymbols, widthSymbols, heightSymbols);
				g.setColor(Color.black);
				g.drawRect(xSymbols, ySymbols, widthSymbols, heightSymbols);
				
				// Scroll bar
				scrollbar.draw(g);
				if (hasFocus) scrollbar.handleClick(mouseX, mouseY);
				
				// File selection
				symbolsButton.draw(g);
				if (hasFocus && !Mouse.leftPressed) symbolsButton.handleClick(mouseX, mouseY, Mouse.leftClicked);
				if (symbolsButton.isClicked())
				{
					// Load new directory for the symbols
					symbolsButton.setActivation(true);
					isLoading = true;
					hasFocus = false;
					new Thread(new FileChooser(this)).start();
				}
				
				adaptSymbols();
				
				// Draw symbols
				for(int j = 0; j < filteredSymbols.size(); j++)
				{
					if (filteredSymbols.get(j).isActivated())
					{
						filteredSymbols.get(j).draw(g);
						if (hasFocus && !tagFilter.getTextfield().isSelected() && !Mouse.leftPressed && j < filteredSymbols.size()) 
						{
							filteredSymbols.get(j).handleClick(mouseX, mouseY);
						}
					}
				}

				// Selector
				if (hasFocus && !Mouse.leftPressed) selectorSymbols.handleClick(this, mouseX, mouseY);
				g.setFont(new Font("Arial", Font.PLAIN, 20));
				g.setColor(Color.BLACK);
				g.drawString("Symboles sélectionnés: " + selectedSymbols.size() + " / " + nbCards, xSymbols, ySymbols + heightSymbols + 32);
				
				String totalSymbols = "Symboles disponibles: " + filteredSymbols.size();
				int totalSymbolsWidth = g.getFontMetrics().stringWidth(totalSymbols);
				g.drawString(totalSymbols, xSymbols + widthSymbols - totalSymbolsWidth, ySymbols + heightSymbols + 32);
				
				
				// Symbols preview
				drawSelectedSymbols(g);
				if (nbSymbolsDisplayed < selectedSymbols.size())
				{
					leftCard.draw(g);
					if (hasFocus && !Mouse.leftPressed) leftCard.handleClick(mouseX, mouseY, Mouse.leftClicked);
					if (selectedSymbols.size() > 0 && leftCard.isClicked()) symbolIndex = (symbolIndex - 1 + selectedSymbols.size()) % selectedSymbols.size();
					rightCard.draw(g);
					if (hasFocus && !Mouse.leftPressed) rightCard.handleClick(mouseX, mouseY, Mouse.leftClicked);
					if (selectedSymbols.size() > 0 && rightCard.isClicked()) symbolIndex = (symbolIndex + 1) % selectedSymbols.size();
				}
			}
		}
		
		// Tag Filter
		tagFilter.draw(g, mouseX, mouseY);
		if (hasFocus && !Mouse.leftPressed) tagFilter.handleClick(mouseX, mouseY);
		handleSelectedTags(g, mouseX, mouseY);
		
		// Symbols loader
		emptySymbolsMessageSize = g.getFontMetrics().stringWidth(emptySymbolsMessage);
		if (emptySymbols)
		{
			g.drawImage(emptySymbolsImage, posEmptySymbolsImage.width, posEmptySymbolsImage.height, null);
			g.drawString(emptySymbolsMessage, posEmptySymbolsMessage.width, posEmptySymbolsMessage.height);
		}
		if (isLoading)
		{
			// Loading display
			g.setColor(new Color(250, 250, 250, 200));
			g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();
			g2d.translate(frameWidth / 2 - loadingImage.getWidth(null) / 2, frameHeight / 2 - loadingImage.getHeight(null) / 2);
			AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians((System.currentTimeMillis() / 6.0) % 360), 
					loadingImage.getWidth(null) / 2, loadingImage.getHeight(null) / 2);
			g2d.drawImage(loadingImage, transform, null);
			g2d.setTransform(oldTransform);
		}
		
		// Loading Bar
		if (LoadingBar.getInstance().isActive())
		{
			LoadingBar.getInstance().draw(g, mouseX, mouseY);
		}
		
		// FPS (debug)
		if (DISPLAY_FPS)
		{
			g.drawString(fps + "", 9, 25);
			counter++;
			if (System.currentTimeMillis() - lastTime > 1000)
			{
				fps = counter;
				counter = 0;
				lastTime = System.currentTimeMillis();
			}
		}

		// Group selection 
		if (!scrollbar.isUsed() && hasFocus)
		{
			drawSelectionSymbols(g, mouseX, mouseY);
		}
		
		// Reset mouse click
		Mouse.leftClicked = false;
		Mouse.rightClicked = false;
	}
	
	/**
	 * Add a PDF extension to the path if not already present
	 * @param path : Path of the PDF document
	 * @return the valid path for the PDF document
	 */
	private String filterPDF(String path)
	{
		if (path.contains(".pdf")) 
		{
			return path;
		} else 
		{
			return path += ".pdf";
		}
	}

	/**
	 * Used for multiple selection
	 */
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		if (arg0.getKeyCode() == 17) 
		{
			if (firstTimeControlKey)
			{
				controlKey = true;
				firstTimeControlKey = false;
				for (int i = 0; i < filteredSymbols.size(); i++)
				{
					filteredSymbols.get(i).changePreselectionColor(true);
				}
			}
		}
	}

	/**
	 * Used for multiple selection
	 */
	@Override
	public void keyReleased(KeyEvent arg0) 
	{
		if (arg0.getKeyCode() == 17) 
		{
			controlKey = false;
			firstTimeControlKey = true;
			for (int i = 0; i < filteredSymbols.size(); i++)
			{
				filteredSymbols.get(i).changePreselectionColor(false);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
