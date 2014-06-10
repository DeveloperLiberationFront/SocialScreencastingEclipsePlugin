package edu.ncsu.lubick.plugin;

import edu.ncsu.lubick.interactions.InteractionEvent;

public interface CommandReceiver {

	void handleInteractionEvent(InteractionEvent event);

	void isShuttingDown();

}
