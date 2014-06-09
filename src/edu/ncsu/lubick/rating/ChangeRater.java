package edu.ncsu.lubick.rating;


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
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
