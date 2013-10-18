package edu.ncsu.lubick.toolmanagement;


/**
 * This is created if the normal diskWriter ran into problems.  So, this allows non-hairy failures.
 * @author KevinLubick
 *
 */
public class DummyToolStreamDiskWriter implements IToolStreamDiskWriter {

	@Override
	public void storeEvent(ToolEvent toolEvent) {}

	@Override
	public void isShuttingDown() {}

}
