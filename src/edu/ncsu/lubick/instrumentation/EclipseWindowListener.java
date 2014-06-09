package edu.ncsu.lubick.instrumentation;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class EclipseWindowListener implements IWindowListener {
	@Override
	public void windowActivated(IWorkbenchWindow window) {
		System.out.println("windowActivated " + window);
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		System.out.println("windowDeactivated " + window);
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		System.out.println("windowOpened " + window);
		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		}
		
		window.getPartService().addPartListener(new EclipsePartListener());
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		System.out.println("windowClosed " + window);
		//TODO remove listeners?
	}
}
