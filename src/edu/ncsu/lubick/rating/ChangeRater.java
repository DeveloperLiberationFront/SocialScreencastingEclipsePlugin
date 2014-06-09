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

	public ChangeRater(ToolStreamRater toolStreamRater)
	{
		super(toolStreamRater);
	}

	@Override
	public float rate()
	{
		float rating = 0;
		rating += tabChange();
		// Should += any of functions
		rating /= 1.0f; // Divide by number of functions
		return rating;
	}
	
	private float tabChange()
	{
		return 0;
	}
	
	private void differenceImage()
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
		
		/*BufferedImage img1 = null;
		BufferedImage img2 = null;
		
		File file1 = new File ("C:\\Users\\Owner\\Desktop\\before.jpg");
		File file2 = new File ("C:\\Users\\Owner\\Desktop\\after.jpg");
		
		img1 = ImageIO.read(file1);
		img2 = ImageIO.read(file2);*/
		 
	    int width1 = img1.getWidth(null);
	    int width2 = img2.getWidth(null);
	    int height1 = img1.getHeight(null);
	    int height2 = img2.getHeight(null);
	    if ((width1 != width2) || (height1 != height2)) {
	      System.err.println("Error: Images dimensions mismatch");
	      System.exit(1);
	    }
	    long diff = 0;
	    int counter = 0;
	    for (int i = 0; i < width1; i++) {
	      for (int j = 0; j < height1; j++) {
	        /*int rgb1 = img1.getRGB(i, j);
	        int rgb2 = img2.getRGB(i, j);
	        int r1 = (rgb1 >> 16) & 0xff;
	        int g1 = (rgb1 >>  8) & 0xff;
	        int b1 = (rgb1      ) & 0xff;
	        int r2 = (rgb2 >> 16) & 0xff;
	        int g2 = (rgb2 >>  8) & 0xff;
	        int b2 = (rgb2      ) & 0xff;
	        diff += Math.abs(r1 - r2);
	        diff += Math.abs(g1 - g2);
	        diff += Math.abs(b1 - b2);*/
	    	 if (img1.getRGB(i, j) != img2.getRGB(i, j))
	    	 {
	    		 counter++;
	    	 }
	      }
	    }
	    double n = width1 * height1 * 3;
	    double p = diff / n / 255.0;
	   // System.out.println("diff percent: " + (p * 100.0));
	    System.out.println("diff percent: " + ((counter * 100.0)/(n/3.0)));
	    //return p * 100.0;
  }
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
