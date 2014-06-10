package edu.ncsu.lubick.instrumentation;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

public class EclipsePartListener implements IPartListener2 {
	
	
	@Override
	public void partVisible(IWorkbenchPartReference partRef)
	{
		System.out.println("partVisible " + partRef);
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef)
	{
		System.out.println("partOpened " + partRef);				
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef)
	{
		System.out.println("partInputChanged " + partRef);
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef)
	{
		System.out.println("partHidden " + partRef);
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef)
	{
		System.out.println("partDeactivated " + partRef);
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef)
	{
		System.out.println("partClosed " + partRef);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef)
	{
		System.out.println("partBroughtToTop " + partRef);
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef)
	{
		System.out.println("partActivated " + partRef);
	}
}