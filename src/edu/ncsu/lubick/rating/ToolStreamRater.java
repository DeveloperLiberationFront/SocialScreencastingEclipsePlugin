package edu.ncsu.lubick.rating;

import java.util.ArrayList;
import java.util.List;

public class ToolStreamRater {

	private List<AbstractRater> raters;
	
	public ToolStreamRater()
	{
		raters = new ArrayList<>();
		raters.add(new ChangeRater());
	}
	
	public float rate(RatingData start, RatingData end)
	{
		float rate = 0;
		
		for(AbstractRater rater : raters)
		{
			float toolRate = rater.rate(start, end);
			rate += toolRate;
			System.out.println("Rate of " + rater + ": " + toolRate);
		}
		
		rate /= raters.size();
		
		System.out.println("Rate of Tool = " + rate);
		return rate;
	}
}
