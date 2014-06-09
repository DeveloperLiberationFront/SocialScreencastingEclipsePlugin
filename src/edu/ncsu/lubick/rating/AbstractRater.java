package edu.ncsu.lubick.rating;

import org.eclipse.core.commands.ExecutionEvent;

import edu.ncsu.lubick.plugin.CommandEvent;

abstract class AbstractRater {

	private ToolStreamRater toolStreamRater;
	
	public AbstractRater(ToolStreamRater toolStreamRater)
	{
		setToolStreamRater(toolStreamRater);
	}
	
	public abstract float rate();
	
	public void setToolStreamRater(ToolStreamRater toolStreamRater)
	{
		this.toolStreamRater = toolStreamRater;
	}
	
	public ToolStreamRater getToolStreamRater()
	{
		return toolStreamRater;
	}
	
	public ExecutionEvent getExecutionEvent()
	{
		return getToolStreamRater().getExecutionEvent();
	}
	
	public CommandEvent getCommandEvent()
	{
		return getToolStreamRater().getCommandEvent();
	}
}
