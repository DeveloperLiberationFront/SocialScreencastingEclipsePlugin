package screencastingeclipseplugin;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class CommandNameDirectory 
{
	private static ICommandService service = null;

	public static String lookUpCommandName(String originId) 
	{
		if (service == null)
		{
			service = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		}

		try {
			return service.getCommand(originId).getName();
		} 
		catch (NotDefinedException e) 
		{
			return service.getCommand(originId).getId();
		}
	}
}
