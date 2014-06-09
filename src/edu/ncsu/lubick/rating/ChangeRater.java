package edu.ncsu.lubick.rating;

class ChangeRater extends AbstractRater {

	public ChangeRater(ToolStreamRater toolStreamRater) {
		super(toolStreamRater);
	}

	@Override
	public float rate()
	{		
		return 7.5f;
	}
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
