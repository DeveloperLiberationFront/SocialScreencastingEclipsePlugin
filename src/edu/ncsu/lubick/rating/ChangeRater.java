package edu.ncsu.lubick.rating;

import edu.ncsu.lubick.toolmanagement.ToolEventData;




public class ChangeRater extends AbstractRater {

	public static final float IDEAL_CHANGE = 10.0f;

	@Override
	public float rate(ToolEventData start, ToolEventData end)
	{
		float rating = 0;
		rating += tabChange(start, end);
		rating += differenceImage(start, end);
		// Should += any of functions
		rating /= 3.f; // Divide by number of functions
		return rating;
	}

	public float tabChange(ToolEventData start, ToolEventData end)
	{
		//getActivePage().isPartVisible(part)
		
		//System.out.println("Is the page visible: " + start.getActivePage().isPartVisible(start.getActivePart()));
		
		if(start.getActivePart().equals(end.getActivePart()))
		{
			return 100.f;
		}
		else
		{
			return 0.f;
		}
	}
	
	public float differenceImage(ToolEventData start, ToolEventData end)
	{
		int width1 = start.getImage().getWidth();
		int width2 = end.getImage().getWidth();
		int height1 = start.getImage().getHeight();
		int height2 = end.getImage().getHeight();
		
		int counter = 0;
		for (int i = 0; i < width1 && i < width2; i++)
		{
			for (int j = 0; j < height1 && j < height2; j++)
			{
				if (start.getImage().getRGB(i, j) != end.getImage().getRGB(i, j))
				{
					counter++;
				}
			}
		}
		
		float diff = 100 * counter/(float)(width1 * height1); //number from 0 to 100
		return getRatingBasedOnPercent(diff);
	}

	public float getRatingBasedOnPercent(float percent)
	{
		if (percent > IDEAL_CHANGE) 
		{
			return 2000/(percent -IDEAL_CHANGE+20); // Trust me it works
		}
		return -2000/(percent-IDEAL_CHANGE-20); //graph it if you don't believe me, http://tinyurl.com/mp5tjkq
	}
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
