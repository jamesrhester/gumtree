package org.gumtree.gumnix.sics.internal.ui.login;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.gumtree.gumnix.sics.core.SicsCore;
import org.gumtree.gumnix.sics.core.SicsCoreProperties;
import org.gumtree.gumnix.sics.io.ISicsConnectionContext;
import org.gumtree.gumnix.sics.io.SicsProxyListenerAdapter;

public class LoginHandler extends SicsProxyListenerAdapter implements ILoginHandler {

	private static final String ARG_SICS_AUTO_LOGIN = "auto";
	
//	private static final String ARG_SICS_SKIP_LOGIN = "skip";
	
//	private static final String OPTION_SICS_INSTRUMENT = "sicsInstr";

	private boolean noMoreLogin = false;
	
	private LoginHandler instance;

	private boolean processing = false;
	
	public LoginHandler() {
		instance = this;
	}

	public void login(final boolean forced) {

		final String loginMode = SicsCoreProperties.LOGIN_MODE.getValue();
		if (!forced) {
			if (noMoreLogin || "skip".equals(loginMode)) {
				return;
			}
		}
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (processing) {
					return;
				}
				processing = true;
				
				// [Tony] [2008-07-08] Use workbench shell instead of display so the dialog
				// will be positioned correctly wrt the current workbench
				Shell shell = new Shell(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				boolean successed = false;
				
				BaseLoginDialog dialog = new InstrumentSpecificLoginDialog(shell, instance);
//				IInstrumentProfile defaultProfile = null;

//				ICommandLineOptions options = ServiceUtils.getService(ICommandLineOptions.class);
//				if(options.hasOption(OPTION_SICS_INSTRUMENT)) {
//					String instrumentId = options.getOptionValue(OPTION_SICS_INSTRUMENT);
//					defaultProfile = SicsCore.getSicsManager().service().getInstrumentProfile(instrumentId);
//				}

//				if(defaultProfile != null) {
//					dialog = new InstrumentSpecificLoginDialog(shell, instance);
//				} else {
//					dialog = new DefaultLoginDialog(shell, instance);
//				}

//				if(GTPlatform.getCommandLineOptions().hasOptionValue(OPTION_SICS_LOGIN)) {
				String loginMode = SicsCoreProperties.LOGIN_MODE.getValue();
				if ("auto".equals(loginMode)) {
					// Handles auto login
					try {
						final ISicsConnectionContext context = dialog.getConnectionContext();
//						final IInstrumentProfile profile = dialog.getInstrumentProfile();
						new LoginProgressMonitorDialog(shell).runDialog(context);
						successed = true;
					} catch (Exception e) {
						dialog.setInitialErrorMessage(e.getMessage());
					}
				}
//				} else if (!forced) {
//					// Handles no login
////					setNoMoreLogin(true);
//					// don't open dialog
//					successed = true;
//				}
//				if (.SICS_LOGIN.hasValue()) {
//					if(GTPlatform.getCommandLineOptions().getOptionValue(OPTION_SICS_LOGIN).equals(ARG_SICS_AUTO_LOGIN)) {
//					if(ARG_SICS_AUTO_LOGIN.equals(SystemProperties.SICS_LOGIN.getValue())) {
//						
//					} else if (!forced) {
//						// Handles no login
//						setNoMoreLogin(true);
//						// don't open dialog
//						successed = true;
//					}
//				}
				if(!successed) {
					dialog.open();
				}
				
				// [GT-72] Warn user for SICS problem
				if (SicsCore.getDefaultProxy().isConnected()) {
					try {
						int componentSize = SicsCore.getSicsController()
								.getSICSModel().getComponent().size();
						// Empty hipadaba model detected
						if (componentSize == 0) {
							MessageDialog
									.openWarning(
											shell,
											"SICS Warning",
											"SICS may not be initialised correctly\n(Reason: Empty instrument model detected.)");
						}
					} catch (Exception e) {
						MessageDialog
								.openError(
										shell,
										"SICS Error",
										"SICS may not be initialised correctly\n(Reason: Failed to load instrument model.)");
					}
				}
				processing = false;
			}
		});
	}
	
	public void proxyConnectionReqested() {
		if (!processing) {
			login(false);
		}
	}

	public void setNoMoreLogin(boolean noMoreLogin) {
		this.noMoreLogin = noMoreLogin;
	}

}
