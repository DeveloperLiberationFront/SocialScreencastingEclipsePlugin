package edu.ncsu.lubick.util;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.commands.ICommandService;

public class EclipseCommandNameService implements CommandNameServce {

	
	private ICommandService service;


	public EclipseCommandNameService(ICommandService service) {
		this.service = service;
	}
	
	
	@Override
	public String lookUpCommandName(String originId) {  
		try {
			return service.getCommand(originId).getName();
		} 
		catch (NotDefinedException e) 
		{
			return service.getCommand(originId).getId();
		}
	}

	
}
