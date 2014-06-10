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
		float diff = counter/(float)(width1 * height1); //decimal from 0 to 1

		if (diff > IDEAL_CHANGE/100.f) //"ideal change" is 10% for now
		{
			return IDEAL_CHANGE/diff; // 10/0.2 gives a score of 50 (out of 100 max)
		}
		return diff / IDEAL_CHANGE * 10000.f; // 0.05/10 * 10000 gives 50 (out of 100)
	}

	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
