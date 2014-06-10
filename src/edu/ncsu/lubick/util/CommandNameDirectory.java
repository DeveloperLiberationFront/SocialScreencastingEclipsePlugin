package edu.ncsu.lubick.util;


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
