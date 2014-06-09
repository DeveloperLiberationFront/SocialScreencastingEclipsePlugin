package edu.ncsu.lubick.rating;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.ncsu.lubick.plugin.CommandEvent;

public class ToolStreamRater {

	private List<AbstractRater> raters;
	private CommandEvent commandEvent;
	private ExecutionEvent executionEvent;
	private ToolStreamRater toolStreamRater;
	private IWorkbench workbench;
	private IWorkbenchWindow workbenchWindow;
	private IWorkbenchPage activePage;
	private IWorkbenchPart activePart;
	
	private ToolStreamRater()
	{
		setWorkbench(PlatformUI.getWorkbench());
		setWorkbenchWindow(getWorkbench().getActiveWorkbenchWindow());
		setActivePage(getWorkbenchWindow().getActivePage());
		setActiveWindow(getActivePage().getActivePart());
	}
	
	public ToolStreamRater(ToolStreamRater toolStreamRater)
	{
		this();
		setCommandEvent(toolStreamRater.getCommandEvent());
		setExecutionEvent(toolStreamRater.getExecutionEvent());
	}
	
	public ToolStreamRater(CommandEvent commandEvent, ExecutionEvent executionEvent)
	{
		this();
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

	private void setCommandEvent(CommandEvent commandEvent) {
		this.commandEvent = commandEvent;
	}

	public ExecutionEvent getExecutionEvent() {
		return executionEvent;
	}

	private void setExecutionEvent(ExecutionEvent executionEvent) {
		this.executionEvent = executionEvent;
	}
	
	public ToolStreamRater getToolStreamRater() {
		return toolStreamRater;
	}
	
	private void setToolStreamRater(ToolStreamRater toolStreamRater) {
		this.toolStreamRater = toolStreamRater;
	}

	public IWorkbench getWorkbench() {
		return workbench;
	}

	private void setWorkbench(IWorkbench workbench) {
		this.workbench = workbench;
	}

	public IWorkbenchWindow getWorkbenchWindow() {
		return workbenchWindow;
	}

	private void setWorkbenchWindow(IWorkbenchWindow workbenchWindow) {
		this.workbenchWindow = workbenchWindow;
	}

	public IWorkbenchPage getActivePage() {
		return activePage;
	}

	private void setActivePage(IWorkbenchPage activePage) {
		this.activePage = activePage;
	}

	public IWorkbenchPart getActiveWindow() {
		return activePart;
	}

	private void setActiveWindow(IWorkbenchPart activePart) {
		this.activePart = activePart;
	}
	
	public IWorkbenchPart getActivePart() {
		return activePart;
	}
	
	private void setActivePart(IWorkbenchPart activePart) {
		this.activePart = activePart;
	}
}
