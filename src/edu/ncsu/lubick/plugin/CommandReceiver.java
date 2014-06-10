package edu.ncsu.lubick.plugin;

public interface CommandReceiver {

	void handleInteractionEvent(InteractionEvent event);

	void isShuttingDown();

}
