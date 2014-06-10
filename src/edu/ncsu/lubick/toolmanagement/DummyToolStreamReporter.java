package edu.ncsu.lubick.toolmanagement;


/**
 * This is created if the normal diskWriter ran into problems.  So, this allows non-hairy failures.
 * @author KevinLubick
 *
 */
public class DummyToolStreamReporter implements IToolStreamReporter {

	@Override
	public void storeEvent(ToolEvent toolEvent) {/*empty*/}

	@Override
	public void isShuttingDown() {/*empty*/}

}
