package edu.ncsu.lubick.plugin;

import java.util.Date;

public class CommandEvent implements InteractionEvent{
	
	
	

	private String commandId;
	private boolean invokedWithKeyboard;

	public CommandEvent(String commandId, boolean invokedWithKeyboard)
	{
		this.commandId = commandId;
		this.invokedWithKeyboard = invokedWithKeyboard;
		
	}

	public static CommandEvent makeCommandEvent(String commandId, boolean invokedWithKeyboard)
	{
		return new CommandEvent(commandId, invokedWithKeyboard);
	}

	public String getCommandId()
	{
		return commandId;
	}

	public boolean isInvokedWithKeyboard()
	{
		return invokedWithKeyboard;
	}

	@Override
	public boolean invokedWithKeyboardShortcut()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventType getType()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
