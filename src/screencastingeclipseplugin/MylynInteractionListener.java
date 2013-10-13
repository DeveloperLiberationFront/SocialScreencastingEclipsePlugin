package screencastingeclipseplugin;

import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

public class MylynInteractionListener implements IInteractionEventListener {


	private ToolEventCompiler toolHandler;


	public MylynInteractionListener(ToolEventCompiler compiler) 
	{
		this.toolHandler = compiler;
	}

	@Override
	public void interactionObserved(InteractionEvent event) {	
		System.out.println(makePrintable(event));

		if (event.getDelta().equals("keybinding"))
		{

			System.out.println(KeyBindingDirectory.lookUpKeyBinding(event.getOriginId()));
			System.out.println(CommandNameDirectory.lookUpCommandName(event.getOriginId()));
		}

		toolHandler.handleInteractionEvent(event);

	}

	@Override
	public void startMonitoring() {}

	@Override
	public void stopMonitoring() {
		//This is the signal that we are shutting down
		System.err.println("Recieved command to shutdown");
		if (toolHandler != null)
		{
			toolHandler.isShuttingDown();
			toolHandler = null;
		}
	}


	public static String makePrintable(InteractionEvent event)
	{
		if (event == null) {
			return "[null InteractionEvent]";
		}
		String prebuiltString = event.toString();
		StringBuilder builder = new StringBuilder("[start");
		//Removes the square brackets
		builder.append(prebuiltString.substring(1, prebuiltString.length()-1));
		builder.append(", endDate: ");
		builder.append(event.getEndDate());
		builder.append(", navigation: ");
		builder.append(event.getNavigation());
		builder.append(", interestContribution: ");
		builder.append(event.getInterestContribution());
		builder.append(", StructureKind: ");
		builder.append(event.getStructureKind());
		builder.append(", StructureHandle: ");
		builder.append(event.getStructureHandle());
		builder.append("]");
		return builder.toString();
	}

}
