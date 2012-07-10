/*******************************************************************************
 * Copyright (c) 2007 Australian Nuclear Science and Technology Organisation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tony Lam (Bragg Institute) - initial API and implementation
 *******************************************************************************/

package org.gumtree.gumnix.sics.batch.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.gumtree.core.object.ObjectCreateException;
import org.gumtree.core.object.ObjectFactory;
import org.gumtree.gumnix.sics.batch.ui.definition.ArgType;
import org.gumtree.gumnix.sics.batch.ui.definition.CompositeCommand;
import org.gumtree.gumnix.sics.batch.ui.definition.ICompositeCommand;
import org.gumtree.gumnix.sics.batch.ui.definition.IPlainCommand;
import org.gumtree.gumnix.sics.batch.ui.definition.ISicsCommand;
import org.gumtree.gumnix.sics.batch.ui.definition.PlainCommand;
import org.gumtree.gumnix.sics.batch.ui.model.ISicsCommandElement;
import org.gumtree.gumnix.sics.batch.ui.model.SicsCommandType;
import org.gumtree.gumnix.sics.control.controllers.ICommandController;
import org.gumtree.gumnix.sics.control.controllers.IComponentController;
import org.gumtree.gumnix.sics.control.controllers.IDynamicController;
import org.gumtree.gumnix.sics.core.SicsCore;
import org.gumtree.gumnix.sics.core.SicsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SicsCommandFactory {
	
	private static Logger logger = LoggerFactory.getLogger(SicsCommandFactory.class);
	
	private static final String PROP_USER_COMMANDS_PATH = "sics.userCommands.path";
	
	private static final String HDB_PROP_ARGTYPE = "argtype";
	
	public static ISicsCommandElement createCommand(SicsCommandType type) throws ObjectCreateException {
		return ObjectFactory.instantiateObject(type.getCommandClass());
	}

	public static ISicsCommand[] createServerCommands() {
		List<ISicsCommand> serverCommandCache = new ArrayList<ISicsCommand>();
			
		// Wait command
		IPlainCommand waitCommand = new PlainCommand("wait");
		waitCommand.setDescription("Cause server to wait");
		waitCommand.addArgumentDefinition("time in sec", ArgType.INT);
		serverCommandCache.add(waitCommand);
		
		// Config command
		ICompositeCommand configCommand = new CompositeCommand("config");
		configCommand.setDescription("Change current user privilege");
		IPlainCommand subCommand = new PlainCommand("rights");
		subCommand.addArgumentDefinition("login", ArgType.STRING);
		subCommand.addArgumentDefinition("password", ArgType.STRING);
		configCommand.addSubCommand(subCommand);
		serverCommandCache.add(configCommand);

		return serverCommandCache.toArray(new ISicsCommand[serverCommandCache.size()]);
	}
	
	public static ISicsCommand[] createCountableCommands() {
		List<ISicsCommand> countableCommandCache = new ArrayList<ISicsCommand>();
		
		// Histmem
		ICompositeCommand histmemCommand = new CompositeCommand("histmem");
		histmemCommand.setDescription("Histogram memeory command");
		
		{
			// Start
			IPlainCommand subCommand = new PlainCommand("start");
			subCommand.setDescription("Start counting");
			histmemCommand.addSubCommand(subCommand);
			
			// Stop
			subCommand = new PlainCommand("stop");
			subCommand.setDescription("Stop counting");
			histmemCommand.addSubCommand(subCommand);
			
			// Pause
			subCommand = new PlainCommand("pause");
			subCommand.setDescription("Pause counting");
			histmemCommand.addSubCommand(subCommand);
			
			// Mode
			subCommand = new PlainCommand("mode");
			subCommand.setDescription("Histmem mode");
			subCommand.addArgumentDefinition("mode", ArgType.STRING);
			histmemCommand.addSubCommand(subCommand);
			
			// Preset
			subCommand = new PlainCommand("preset");
			subCommand.setDescription("Histmem preset");
			subCommand.addArgumentDefinition("val", ArgType.INT);
			histmemCommand.addSubCommand(subCommand);
		}
		
		countableCommandCache.add(histmemCommand);
		
		return countableCommandCache.toArray(new ISicsCommand[countableCommandCache.size()]);
	}
	
	public static ISicsCommand[] createUserCommands() {
		// SICS proxy is not yet ready
		if (SicsCore.getSicsController() == null) {
			logger.warn("SICS model is not available");
			return new ISicsCommand[0];
		}
			
		List<ISicsCommand> userCommandCache = new ArrayList<ISicsCommand>();
			
		// Find command holder
		String path = System.getProperty(PROP_USER_COMMANDS_PATH);
		IComponentController userCommandNode = SicsCore.getSicsController().findComponentController(path);
		for (IComponentController child : userCommandNode.getChildControllers()) {
			if (child instanceof ICommandController) {
				userCommandCache.add(createUserDefinedSicsCommand((ICommandController) child));
			}
		}
		
		return userCommandCache.toArray(new ISicsCommand[userCommandCache.size()]);
	}
	
	private static ISicsCommand createUserDefinedSicsCommand(ICommandController controller) {
		IPlainCommand command = new PlainCommand(controller.getId());
		for (IComponentController child : controller.getChildControllers()) {
			if (child instanceof IDynamicController) {
				String argType = SicsUtils.getPropertyFirstValue(child.getComponent(), HDB_PROP_ARGTYPE);
				if (argType.equals("int")) {
					command.addArgumentDefinition(child.getId(), ArgType.INT);
				} else if (argType.equals("float")) {
					command.addArgumentDefinition(child.getId(), ArgType.FLOAT);
				} else if (argType.equals("drivable")) {
					command.addArgumentDefinition(child.getId(), ArgType.DRIVABLE);
				} else {
					// Default: string type arg
					command.addArgumentDefinition(child.getId(), ArgType.STRING);
				}
			}
		}
		return command;
	}
	
	private SicsCommandFactory() {
		super();
	}
}
