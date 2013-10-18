package edu.ncsu.lubick.toolmanagement;

public interface IToolStreamDiskWriter {

	void storeEvent(ToolEvent toolEvent);

	void isShuttingDown();

}
