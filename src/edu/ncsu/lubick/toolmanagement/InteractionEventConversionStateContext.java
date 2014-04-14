package edu.ncsu.lubick.toolmanagement;

public interface InteractionEventConversionStateContext 
{
	
	public void setState(InteractionEventConversionState newState);

	public void logUnusualBehavior(String behavior);

	public void postConvertedEvent(ToolEvent createdEvent);

	public void previousEventNeedsRerun(boolean b);
}
