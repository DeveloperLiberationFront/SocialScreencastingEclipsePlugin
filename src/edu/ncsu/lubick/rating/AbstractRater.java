package edu.ncsu.lubick.rating;

import edu.ncsu.lubick.toolmanagement.ToolEventData;


abstract class AbstractRater {
	public abstract float rate(ToolEventData start, ToolEventData end);
}