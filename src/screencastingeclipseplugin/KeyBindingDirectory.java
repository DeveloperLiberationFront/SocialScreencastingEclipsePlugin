package screencastingeclipseplugin;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class KeyBindingDirectory {

	private static IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);


	
	public static String lookUpKeyBinding(String commandId) {
		return bindingService.getBestActiveBindingFormattedFor(commandId);
		
	}

}
