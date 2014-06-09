package edu.ncsu.lubick.util;

import org.eclipse.ui.keys.IBindingService;

public class EclipseKeyBindingService implements KeyBindingService{

	private IBindingService bindingService;

	public EclipseKeyBindingService(IBindingService bindingService) 
	{
		this.bindingService = bindingService;
	}

	@Override
	public String getKeyBindingFor(String commandId) 
	{
		return bindingService.getBestActiveBindingFormattedFor(commandId);
	}

}
