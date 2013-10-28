package edu.ncsu.lubick.plugin;

import java.io.File;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ncsu.lubick.toolmanagement.ToolEventCompiler;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.EclipseCommandNameService;
import edu.ncsu.lubick.util.EclipseKeyBindingService;
import edu.ncsu.lubick.util.KeyBindingDirectory;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Kevin Lubick
 */

@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin implements IStartup
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ScreenCastingEclipsePlugin"; //$NON-NLS-1$
	private static final String MONITOR_FOLDER = "D:\\workspace\\ScreenCastingLocalHub\\HF";

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

	private void setupToolStreamFileLogging() {
		FileAppender fa = new FileAppender();
		fa.setName("ToolStreamLogging");
		fa.setFile("ToolStreamLog.log");
		fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c{1} %x - %m%n"));
		fa.setThreshold(Level.ALL);
		fa.setAppend(false);
		fa.activateOptions();	//make the logging file

		Logger.getRootLogger().addAppender(fa);

		ToolEventCompiler.setLogger(Logger.getLogger("ToolStreamLogging." + ToolEventCompiler.class.getName()));

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (interactionListener != null)
		{
			this.interactionListener.stopMonitoring();
			MonitorUi.removeInteractionListener(this.interactionListener);
		}
		this.interactionListener = null;

		plugin = null;


		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(WorkbenchPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(JavaPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(EditorsPlugin.getDefault().getPreferenceStore());
		UiUsageMonitorPlugin.getDefault().removeMonitoredPreferences(PDEPlugin.getDefault().getPreferenceStore());

		super.stop(context);
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
				//taken from the sample monitoring program.  Probably monitors more than I need
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



		File outputFolder = new File(MONITOR_FOLDER);
		if (!outputFolder.exists())
		{
			System.err.println("SERIOUS PROBLEM!  THE MONITOR FOLDER DOESN'T EXIST");
		}

		setupToolStreamFileLogging();
		
		EclipseKeyBindingService adapter = new EclipseKeyBindingService((IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class));
		KeyBindingDirectory.initializeBindingService(adapter);
		
		EclipseCommandNameService commandService = new EclipseCommandNameService((ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class));
		CommandNameDirectory.initializeCommandService(commandService);
		
		ToolEventCompiler toolStreamCompiler = new ToolEventCompiler(outputFolder);
		this.interactionListener = new MylynInteractionListener(toolStreamCompiler);
		MonitorUi.addInteractionListener(this.interactionListener);
	}
}
