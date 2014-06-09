package edu.ncsu.lubick.rating;


class ChangeRater extends AbstractRater {

	public ChangeRater(ToolStreamRater toolStreamRater)
	{
		super(toolStreamRater);
	}

	@Override
	public float rate()
	{
		tabChange();
		return 7.5f;
	}
	
	private float tabChange()
	{
		System.out.println(getActivePage());
		return 0;
	}
	
	@Override
	public String toString()
	{
		return "Change Rater";
	}

}
