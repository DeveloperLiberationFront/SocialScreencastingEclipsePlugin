package edu.ncsu.lubick.toolmanagement;

public interface IToolStreamReporter {

	void storeEvent(ToolEvent toolEvent);

	void isShuttingDown();

}
