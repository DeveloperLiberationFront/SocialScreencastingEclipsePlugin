package edu.ncsu.lubick.util;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.commands.ICommandService;

public class CommandNameDirectory 
{
	
	private static CommandNameServce service = null;

	public static String lookUpCommandName(String originId) 
	{
		return service.lookUpCommandName(originId);
	}

	public static void initializeCommandService(CommandNameServce commandService) {
		service = commandService;
	}
	
}
