package content;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

/**
 * Load the symbols images in background
 * @author Etienne Hüsler
 * @version 2.0
 */
public class ImageLoader 
{
	private ExecutorService executor;
	private ArrayList<Future<?>> futures;
	
	private final int POOL_SIZE = 1;
	
	private static ImageLoader instance;
	
	private ImageLoader()
	{
		// Instantiate an executor that will accept new services
		executor = Executors.newFixedThreadPool(POOL_SIZE);
		futures = new ArrayList<>();
	}
	
	public static ImageLoader getInstance()
	{
		if (instance == null)
		{
			instance = new ImageLoader();
		}
		return instance;
	}
	
	/**
	 * Cancel all running threads and prevent new ones to start
	 */
	public void shutdown()
	{
		futures = new ArrayList<>();
		executor.shutdownNow();
		executor = Executors.newFixedThreadPool(POOL_SIZE);
	}
	
	/**
	 * Remove all results of finished services
	 */
	public void manageFutures()
	{
		if (futures.size() > 0)
		{
			for (int i = 0; i < futures.size(); i++)
			{
				if (futures.get(i).isDone())
				{
					futures.remove(futures.get(i));
				}
			}
		}
	}
	
	/**
	 * Check if the Loader is ready to accept a new service
	 * @return true if there is no service running
	 */
	public boolean isReady()
	{
		return futures.size() == 0;
	}
	
	/**
	 * Load an image of a symbol in a new service
	 * @param symbol : The symbol that will be referenced to Load it's image
	 */
	public void loadSymbol(Symbol symbol)
	{
		// Submit a new service
		Future<?> future = executor.submit(new Runnable() 
		{
			@Override
			public void run() 
			{
				// Load the image and retry until it succeed
				boolean finished = false;
				while (!finished)
				{
					if (Thread.interrupted()) break;
					try 
					{
						File file = symbol.getFile();
						if (file != null)
						{
							// Load the image
							BufferedImage image = ImageIO.read(file);
							symbol.setImage(image);
							finished = true;
						}
					} catch (IOException e) 
					{
						System.out.println("Error while loading the symbol " + symbol.getName());
					} catch (NullPointerException e)
					{
						System.out.println("No file found for the symbol " + symbol.getName());
					}
				}
			}
		});
		futures.add(future);
	}
}
