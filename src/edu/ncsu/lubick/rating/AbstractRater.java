package edu.ncsu.lubick.rating;


abstract class AbstractRater extends ToolStreamRater {

	public AbstractRater(ToolStreamRater toolStreamRater)
	{
		super(toolStreamRater);
	}
	
	public abstract float rate();
}