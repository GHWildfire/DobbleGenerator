package content;

/**
 * Print settings
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Settings 
{
	private static Settings instance;
	
	// Sliders
	public static int DPI = 300;  				// inches
	public static double sheetMargin = 0.5;  	// cm
	public static double cardSize = 5.0;     	// cm
	public static double cmToInch = 0.393701;   // cm -> inches
	
	// Check boxes
	public static boolean usingBorders = true;
	public static boolean usingLines = false;
	public static boolean usingMarkers = false;
	
	public static Settings getInstance()
	{
		if (instance == null)
		{
			instance = new Settings();
		}
		return instance;
	}
}
