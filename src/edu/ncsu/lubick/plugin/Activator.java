package edu.ncsu.lubick.plugin;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.ncsu.lubick.instrumentation.EclipsePartListener;
import edu.ncsu.lubick.instrumentation.EclipseWindowListener;
import edu.ncsu.lubick.rating.ToolStreamRater;
import edu.ncsu.lubick.toolmanagement.NetworkToolStreamReporter;
import edu.ncsu.lubick.toolmanagement.ToolEventCompiler;
import edu.ncsu.lubick.toolmanagement.ToolStreamDiskWriter;
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
	
	private EclipseCommandListener commandListener;


	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		System.out.println("normal startup");
		
		
		

		
	}

	private void setupLog4j() {
		makeLoggerForToolStreams();
		makeGeneralPurposeLogger();

	}

	private void makeGeneralPurposeLogger()
	{
		FileAppender fa = new FileAppender();
		fa.setName("GeneralLogging");
		fa.setFile(ResourcesPlugin.getWorkspace().getRoot().getLocation().lastSegment() + "-ScreencastEclipseLog.log");
		fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c{1} %x - %m%n"));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(false);
		fa.activateOptions();	//make the logging file

		Logger.getRootLogger().addAppender(fa);
		
		NetworkToolStreamReporter.setLogger(Logger.getLogger("GeneralLogging." + NetworkToolStreamReporter.class.getName()));
		EclipseCommandListener.setLogger(Logger.getLogger("GeneralLogging." + EclipseCommandListener.class.getName()));
	}

	private void makeLoggerForToolStreams()
	{
		FileAppender fa = new FileAppender();
		fa.setName("ToolStreamLogging");
		fa.setFile(ResourcesPlugin.getWorkspace().getRoot().getLocation().lastSegment() + "-ToolStreamLog.log");
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
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (commandListener != null) {
			commandListener.stopMonitoring();
			commandListener = null;
		}
		
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
		IWorkbench workbench = PlatformUI.getWorkbench();

		File outputFolder = new File(MONITOR_FOLDER);
		if (!outputFolder.exists())
		{
			System.err.println("SERIOUS PROBLEM!  THE MONITOR FOLDER DOESN'T EXIST");
		}

		setupLog4j();
		
		EclipseKeyBindingService adapter = new EclipseKeyBindingService((IBindingService) workbench.getAdapter(IBindingService.class));
		KeyBindingDirectory.initializeBindingService(adapter);
		
		ICommandService systemCommandService = (ICommandService) workbench.getAdapter(ICommandService.class);
		EclipseCommandNameService commandService = new EclipseCommandNameService(systemCommandService);
		CommandNameDirectory.initializeCommandService(commandService);
		
		ToolStreamDiskWriter.setOutputFolder(outputFolder);
		
		ToolEventCompiler toolStreamCompiler = new ToolEventCompiler();
		
		commandListener = new EclipseCommandListener(toolStreamCompiler); 

		systemCommandService.addExecutionListener(commandListener);

		workbench.addWindowListener(new EclipseWindowListener());
		
		IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
		
		for(IWorkbenchWindow w:windows) {
			w.getPartService().addPartListener(new EclipsePartListener());
		}
		
		final Display display = workbench.getDisplay();
		display.asyncExec(new Runnable() {
			
			@Override
			public void run()
			{
				display.addFilter(SWT.Show, new Listener() {
					
					@Override
					public void handleEvent(Event event)
					{
						System.out.println("SWT.Show:");
						System.out.println("\t"+event);
						Shell shell = event.display.getActiveShell();
						shell.addShellListener(new ShellAdapter() {
							@Override
							public void shellClosed(ShellEvent e)
							{
								System.out.println("shellClosed: "+e);
							}
						});
					}
				});
			}
		});
	}

	private void muckingAround()
	{
		BindingManager bm = (BindingManager) PlatformUI.getWorkbench().getAdapter(BindingManager.class);
		
		System.out.println("Binding manager: "+ bm);
		if (bm != null) {
			try{
				Object map  = bm.getActiveBindingsDisregardingContext();
				System.out.println("Map of bindings:"+map);
				@SuppressWarnings("unchecked")
				Map<TriggerSequence, Collection<Binding>> castMap = (Map<TriggerSequence, Collection<Binding>>) map;
				System.out.println("Map was cast successfully " +castMap.hashCode());
			}
			catch (Exception e) {
				System.err.println("Problem casting");
				e.printStackTrace();
			}
			
		}
	}
}
