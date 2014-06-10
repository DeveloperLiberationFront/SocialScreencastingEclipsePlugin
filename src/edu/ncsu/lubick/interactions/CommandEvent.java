package edu.ncsu.lubick.interactions;


public class CommandEvent extends InteractionEvent{
	

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
	public EventType getType()
	{
		if (invokedWithKeyboard) {
			return EventType.INVOCATION_KEYBOARD_SHORTCUT;
		}
		return EventType.INVOCATION_GUI;
	}

}
