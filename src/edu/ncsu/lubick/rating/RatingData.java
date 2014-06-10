package edu.ncsu.lubick.rating;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Date;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class RatingData {
	private IWorkbench workbench;
	private IWorkbenchWindow workbenchWindow;
	private IWorkbenchPage activePage;
	private IWorkbenchPart activePart;
	private Date time;
	private BufferedImage img;
	
	public RatingData() {
		setWorkbench(PlatformUI.getWorkbench());
		setWorkbenchWindow(getWorkbench().getWorkbenchWindows()[0]); // TODO this is hacky and should be getActiveWorkbenchWindow(), it just isn't working correctly for the testing stuff...
		setActivePage(getWorkbenchWindow().getActivePage());
		setActivePart(getActivePage().getActivePart());
		setTime(new Date());
		setImage();
	}
	
	public BufferedImage getImage()
	{
		return img;
	}
	
	private void setImage()
	{
		try {
			Robot robot = new Robot();
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			img = robot.createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace();
		}
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
