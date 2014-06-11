package edu.ncsu.lubick.toolmanagement;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ToolEventData {
	private IWorkbench workbench;
	private IWorkbenchWindow workbenchWindow;
	private IWorkbenchPage activePage;
	private IWorkbenchPart activePart;
	private IDocument document;
	private Date time;
	private BufferedImage img;
	private List<DocumentEvent> changeEvents;
	
	public ToolEventData() {
		setWorkbench(PlatformUI.getWorkbench());
		setWorkbenchWindow(getWorkbench().getActiveWorkbenchWindow());
		setActivePage(getWorkbenchWindow().getActivePage());
		setActivePart(getActivePage().getActivePart());
		setTime(new Date());
		setupDocument();
		setImage();
	}
	
	public List<DocumentEvent> getChangeEvents()
	{
		return changeEvents;
	}
	
	public IDocument getDocument()
	{
		return document; 
	}
	
	private void setupDocument()
	{
		changeEvents = new ArrayList<DocumentEvent>();
		if(getActivePart() instanceof IEditorPart)
		{
			if(getActivePart() instanceof ITextEditor)
			{
				IEditorInput input = ((ITextEditor) getActivePart()).getEditorInput();
				
				document = ((ITextEditor) getActivePart()).getDocumentProvider().getDocument(input);
				
				document.addDocumentListener(new IDocumentListener() {
					@Override
					public void documentChanged(DocumentEvent event) {
						changeEvents.add(event);
					}
					
					@Override
					public void documentAboutToBeChanged(DocumentEvent event) {/* Don't care...*/}
				});
			}
		}
	}
	
	public BufferedImage getImage()
	{
		return img;
	}
	
	private void setImage()
	{
		try
		{
			final Robot robot = new Robot();
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					org.eclipse.swt.graphics.Rectangle bounds = getWorkbenchWindow().getShell().getBounds();
					Rectangle rect = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
					img = robot.createScreenCapture(rect);	
				}
			});
		}
		catch (AWTException e)
		{
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

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try
		{
			json.put("time", getTime());
			
			JSONArray changeEventsJson = new JSONArray();
			for(DocumentEvent event : getChangeEvents())
			{
				JSONObject eventJson = new JSONObject();
				eventJson.put("text", event.getText());
				eventJson.put("replaced_length", event.getLength());
			}
			json.put("events", changeEventsJson);
			
			json.put("active_document", getActivePart().getTitle());
			json.put("image", getImage().getRaster().toString());
		}
		catch(JSONException e)
		{
			System.out.println("Failed to convert ToolEventData to Json");
		}
		
		
		return json;
	}
}
