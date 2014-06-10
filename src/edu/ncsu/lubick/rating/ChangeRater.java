package edu.ncsu.lubick.rating;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


class ChangeRater extends AbstractRater {

	@Override
	public float rate(RatingData start, RatingData end)
	{
		float rating = 0;
		rating += tabChange(start, end);
		rating += differenceImage();
		// Should += any of functions
		rating /= 2.0f; // Divide by number of functions
		return rating;
	}
	
	private float tabChange(RatingData start, RatingData end)
	{
		if(start.getActivePart().equals(end.getActivePart()))
		{
			return 100.0f;
		}
		else
		{
			return 0.0f;
		}
	}
	
	private float differenceImage()
	{
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException awe) {
			//logger.info("Could not initialize Robot",awe);
		}
		 Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		 BufferedImage img1 = robot.createScreenCapture(screenRect);
		 try {
			ImageIO.write(img1, "jpg", new File("C:\\Users\\Owner\\Desktop\\before.jpg"));
		} catch (IOException e1) {
			//logger.fatal("Could not save image", e1);
		}
		 
		 try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 BufferedImage img2 = robot.createScreenCapture(screenRect);
		 try {
			ImageIO.write(img2, "jpg", new File("C:\\Users\\Owner\\Desktop\\after.jpg"));
		} catch (IOException e1) {
			//logger.fatal("Could not save image", e1);
		}
		
		
	    int width1 = img1.getWidth(null);
	    int width2 = img2.getWidth(null);
	    int height1 = img1.getHeight(null);
	    int height2 = img2.getHeight(null);
	    if ((width1 != width2) || (height1 != height2))
	    {
	      System.err.println("Error: Images dimensions mismatch");
	      System.exit(1);
	    }
	    int counter = 0;
	    for (int i = 0; i < width1; i++)
	    {
	    	for (int j = 0; j < height1; j++)
	    	{
	    		if (img1.getRGB(i, j) != img2.getRGB(i, j))
	    		{
	    			counter++;
	    		}
	    	}
	    }
	    float diff = counter/(float)(width1 * height1);
	    if (diff > 10)
	    {
	    	return 10.0f/diff;
	    }
	    return diff/10.0f;
  }
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
