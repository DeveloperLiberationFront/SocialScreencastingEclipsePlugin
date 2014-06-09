package edu.ncsu.lubick.rating;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;

import edu.ncsu.lubick.plugin.CommandEvent;

public class ToolStreamRater {

	private List<AbstractRater> raters;
	private CommandEvent commandEvent;
	private ExecutionEvent executionEvent;
	private ToolStreamRater toolStreamRater;
	
	public ToolStreamRater(CommandEvent commandEvent, ExecutionEvent executionEvent)
	{
		raters = new ArrayList<>();
		raters.add(new ChangeRater(this));
		
		setCommandEvent(commandEvent);
		setExecutionEvent(executionEvent);
	}
	
	public float rate()
	{
		float rate = 0;
		
		for(AbstractRater rater : raters)
		{
			float toolRate = rater.rate();
			rate += toolRate;
			System.out.println("Rate of " + rater + ": " + toolRate);
		}
		
		rate /= raters.size();
		
		System.out.println("Rate of Tool = " + rate);
		return rate;
	}

	public CommandEvent getCommandEvent() {
		return commandEvent;
	}

	public void setCommandEvent(CommandEvent commandEvent) {
		this.commandEvent = commandEvent;
	}

	public ExecutionEvent getExecutionEvent() {
		return executionEvent;
	}

	public void setExecutionEvent(ExecutionEvent executionEvent) {
		this.executionEvent = executionEvent;
	}
	
	public ToolStreamRater getToolStreamRater() {
		return toolStreamRater;
	}
	
	public void setToolStreamRater(ToolStreamRater toolStreamRater) {
		this.toolStreamRater = toolStreamRater;
	}
}
