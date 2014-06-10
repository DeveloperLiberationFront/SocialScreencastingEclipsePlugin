package edu.ncsu.lubick.rating;



class ChangeRater extends AbstractRater {

	public static final float IDEAL_CHANGE = 10.0f;

	@Override
	public float rate(RatingData start, RatingData end)
	{
		float rating = 0;
		rating += tabChange(start, end);
		rating += differenceImage(start, end);
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

	private float differenceImage(RatingData start, RatingData end)
	{
		int width1 = start.getImage().getWidth(null);
		int width2 = end.getImage().getWidth(null);
		int height1 = start.getImage().getHeight(null);
		int height2 = end.getImage().getHeight(null);
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
				if (start.getImage().getRGB(i, j) != end.getImage().getRGB(i, j))
				{
					counter++;
				}
			}
		}
		
		float diff = 100 * counter/(float)(width1 * height1); //number from 0 to 100
		
		if (diff > IDEAL_CHANGE) 
		{
			return 2000/(diff-IDEAL_CHANGE+20); // Trust me it works
		}
		return -2000/(diff-IDEAL_CHANGE-20); //graph it if you don't believe me, http://tinyurl.com/mp5tjkq
	}

	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
