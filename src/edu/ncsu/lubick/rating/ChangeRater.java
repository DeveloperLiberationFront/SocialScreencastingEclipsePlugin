package edu.ncsu.lubick.rating;


class ChangeRater extends AbstractRater {

	@Override
	public float rate(RatingData start, RatingData end)
	{
		float rating = 0;
		rating += tabChange(start, end);
		// Should += any of functions
		rating /= 1.0f; // Divide by number of functions
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
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
