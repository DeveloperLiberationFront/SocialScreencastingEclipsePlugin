package edu.ncsu.lubick.util;

import org.eclipse.ui.keys.IBindingService;

public class KeyBindingDirectory {

	private static IBindingService bindingService = null;

	public static String lookUpKeyBinding(String commandId) {
		return bindingService.getBestActiveBindingFormattedFor(commandId);
		
	}
	
	public static void initializeBindingService(IBindingService keyBindingService)
	{
		KeyBindingDirectory.bindingService = keyBindingService;
	}

}
