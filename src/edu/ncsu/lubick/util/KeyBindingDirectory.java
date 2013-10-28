package edu.ncsu.lubick.util;


public class KeyBindingDirectory {

	private static KeyBindingService bindingService = null;

	public static String lookUpKeyBinding(String commandId) {
		return bindingService.getKeyBindingFor(commandId);
		
	}
	
	public static void initializeBindingService(KeyBindingService keyBindingService)
	{
		KeyBindingDirectory.bindingService = keyBindingService;
	}

}
