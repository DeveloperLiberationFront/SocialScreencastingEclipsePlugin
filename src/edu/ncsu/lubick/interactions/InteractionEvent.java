package edu.ncsu.lubick.interactions;

import java.util.Date;


public abstract class InteractionEvent {

	private long creationTime = System.currentTimeMillis();
	private Date lazyCreationDate = null;
	private EventType type;
	
	
	protected InteractionEvent() {
		//empty to allow children to handle types
	}
	
	public InteractionEvent(EventType type) {
		this.type = type;
	}
	
	public Date getDate()
	{
		if (null == lazyCreationDate) {
			lazyCreationDate = new Date(this.creationTime);
		}
		return lazyCreationDate;
	}

	public EventType getType() {
		return type;
	}
	
	

}

