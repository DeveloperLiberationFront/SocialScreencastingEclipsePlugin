package edu.ncsu.lubick.rating;

import java.util.Date;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Terrible name....
 * @author michael
 *
 */
public class RatingTime {
	private IWorkbench workbench;
	private IWorkbenchWindow workbenchWindow;
	private IWorkbenchPage activePage;
	private IWorkbenchPart activePart;
	private Date time;
	
	public RatingTime() {
		setWorkbench(PlatformUI.getWorkbench());
		setWorkbenchWindow(getWorkbench().getWorkbenchWindows()[0]); // TODO this is hacky and should be getActiveWorkbenchWindow(), it just isn't working correctly for the testing stuff...
		setActivePage(getWorkbenchWindow().getActivePage());
		setActivePart(getActivePage().getActivePart());
		setTime(new Date());
	}
	
	public IWorkbench getWorkbench() {
		return workbench;
	}

	private void setWorkbench(IWorkbench workbench) {
		this.workbench = workbench;
	}

	public IWorkbenchWindow getWorkbenchWindow() {
		return workbenchWindow;
	}

	private void setWorkbenchWindow(IWorkbenchWindow workbenchWindow) {
		this.workbenchWindow = workbenchWindow;
	}

	public IWorkbenchPage getActivePage() {
		return activePage;
	}

	private void setActivePage(IWorkbenchPage activePage) {
		this.activePage = activePage;
	}

	public IWorkbenchPart getActivePart() {
		return activePart;
	}

	private void setActivePart(IWorkbenchPart activePart) {
		this.activePart = activePart;
	}
	
	
	public Date getTime() {
		return time;
	}
	
	private void setTime(Date time) {
		this.time = time;
	}
}
