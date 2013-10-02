package screencastingeclipseplugin;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Kevin Lubick
 */

@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin implements IStartup{

	// The plug-in ID
	public static final String PLUGIN_ID = "ScreenCastingEclipsePlugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private IInteractionEventListener interactionListener;

	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		System.out.println("normal startup");
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		MonitorUi.removeInteractionListener(this.interactionListener);
		this.interactionListener = null;
		
		plugin = null;
		

		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(WorkbenchPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(JavaPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(WorkbenchPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(EditorsPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(PDEPlugin.getDefault().getPreferenceStore());
		
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	public void earlyStartup() {
		System.out.println("Starting early");
		
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {

				UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
						WorkbenchPlugin.getDefault().getPreferenceStore());
				UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
						JavaPlugin.getDefault().getPreferenceStore());
				UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
						WorkbenchPlugin.getDefault().getPreferenceStore());
				UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
						EditorsPlugin.getDefault().getPreferenceStore());
				UiUsageMonitorPlugin.getDefault().addMonitoredPreferences(
						PDEPlugin.getDefault().getPreferenceStore());
				
				
			}
		});
		
		
		
		
		
		this.interactionListener = new MylynInteractionListener();
		MonitorUi.addInteractionListener(this.interactionListener);
	}
}
