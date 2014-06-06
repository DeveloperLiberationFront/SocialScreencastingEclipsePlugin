package edu.ncsu.lubick.plugin;

import java.util.Date;


public interface InteractionEvent {

	boolean invokedWithKeyboardShortcut();

	String getCommandId();

	Date getDate();

	EventType getType();

}

