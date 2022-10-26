package au.gov.ansto.bragg.koala.ui.sics;

import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.gumtree.control.core.IDriveableController;
import org.gumtree.control.core.IDynamicController;
import org.gumtree.control.core.ISicsController;
import org.gumtree.control.core.ISicsModel;
import org.gumtree.control.core.ISicsProxy;
import org.gumtree.control.core.SicsManager;
import org.gumtree.control.events.ISicsControllerListener;
import org.gumtree.control.events.ISicsProxyListener;
import org.gumtree.control.events.SicsProxyListenerAdapter;
import org.gumtree.control.exception.SicsException;
import org.gumtree.control.exception.SicsInterruptException;
import org.gumtree.control.exception.SicsModelException;
import org.gumtree.control.model.PropertyConstants.ControllerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ansto.bragg.koala.ui.Activator;
import au.gov.ansto.bragg.koala.ui.scan.ExperimentModel;
import au.gov.ansto.bragg.koala.ui.scan.KoalaInterruptionException;
import au.gov.ansto.bragg.koala.ui.scan.KoalaModelException;
import au.gov.ansto.bragg.koala.ui.scan.KoalaServerException;

public class ControlHelper {

	public enum InstrumentPhase {
		ERASE,
		EXPOSE,
		EXPOSE_ENDING,
		READ,
		SHUTTER_CLOSE,
		IDLE,
		ERROR;
		
		public String getText() {
			switch (this) {
			case ERASE:
				return "Erasing";
			case EXPOSE:
				return "Exposing";
			case EXPOSE_ENDING:
				return "Ending exposure";
			case READ:
				return "Reading";
			case SHUTTER_CLOSE:
				return "Closing shutter";
			case ERROR:
				return "Error:";
			case IDLE:
				return "Idle";
			default:
				return "Idle";
			}
		}
	};
	
	public static final String SAMPLE_PHI = "gumtree.koala.samplephi";
	public static final String SAMPLE_CHI = "gumtree.koala.samplechi";
	public static final String ENV_VALUE = "gumtree.koala.environmentValue";
	public static final String ENV_SETPOINT = "gumtree.koala.environmentSetpoint";
	public static final String SX_PATH = "gumtree.koala.sx";
	public static final String SY_PATH = "gumtree.koala.sy";
	public static final String SZ_PATH = "gumtree.koala.sz";
	public static final String STEP_PATH = "gumtree.koala.currpoint";
	public static final String FILENAME_PATH = "gumtree.koala.filename";
	public static final String PHASE_PATH = "gumtree.koala.phase";
	public static final String IMAGE_STATE_PATH = "gumtree.path.imagestate";
	public static final String EXPOSURE_TIME_PATH = "gumtree.path.exposuretime";
	public static final String ABORT_COLLECTION_PATH = "gumtree.path.abortCollection";
	public static final String GUMTREE_STATUS_PATH = "gumtree.path.gumtreestatus";
	public static final String GUMTREE_TIME_PATH = "gumtree.path.gumtreetime";
	public static final String GUMTREE_SAMPLE_NAME = "gumtree.koala.samplename";
	public static final String GUMTREE_COMMENTS = "gumtree.koala.comments";
	public static final String GUMTREE_USER_NAME = "gumtree.koala.username";
	
	private final static Color BUSY_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	private final static Color IDLE_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private final static Logger logger = LoggerFactory.getLogger(ControlHelper.class);
	
	public static String TEMP_DEVICE_NAME;
	public static String CHI_DEVICE_NAME;
	public static String PHI_DEVICE_NAME;
	public static String EXPO_TIME_NAME;
	public static String ABORT_COLLECTION_NAME;
	
	public static int ERASURE_TIME = 30;
	public static int READ_TIME = 240;
	public static String proposalFolder;
	public static ExperimentModel experimentModel;
	
	static {
		TEMP_DEVICE_NAME = System.getProperty(ENV_SETPOINT);
		CHI_DEVICE_NAME = System.getProperty(SAMPLE_CHI);
		PHI_DEVICE_NAME = System.getProperty(SAMPLE_PHI);
		EXPO_TIME_NAME = System.getProperty(EXPOSURE_TIME_PATH);
		ABORT_COLLECTION_NAME = System.getProperty(ABORT_COLLECTION_PATH);
	}
	
	public ControlHelper() {
		CollectionHelper.getInstance();
	}

	public static void driveTemperature(float value) 
			throws KoalaServerException, KoalaInterruptionException {
		logger.warn(String.format("drive {} {}", TEMP_DEVICE_NAME, value));
		syncDrive(TEMP_DEVICE_NAME, value);
	}

	public static void driveChi(float value) 
			throws KoalaServerException, KoalaInterruptionException {
		logger.warn(String.format("drive {} {}", CHI_DEVICE_NAME, value));
		syncDrive(CHI_DEVICE_NAME, value);
	}

	public static void drivePhi(float value) 
			throws KoalaServerException, KoalaInterruptionException {
		logger.warn(String.format("drive {} {}", PHI_DEVICE_NAME, value));
		syncDrive(PHI_DEVICE_NAME, value);
	}

	public static void setValue(String idOrPath, Object value) throws KoalaModelException {
		ISicsController device = getProxy().getSicsModel().findController(idOrPath);
		if (device == null) {
			throw new KoalaModelException("can't find model node: " + idOrPath);
		}
		if (device instanceof IDynamicController) {
			try {
				((IDynamicController) device).setValue(value);
			} catch (SicsException e) {
				throw new KoalaModelException("failed to set value to " + idOrPath);
			}
		} else {
			throw new KoalaModelException("invalid model node: " + idOrPath);
		}
	}
	
//	public static void scanPhi(float start, float inc, int numSteps, int erasure, int exposure) 
//			throws KoalaServerException, KoalaInterruptionException {
//		float pos;
//		for (int i = 0; i < numSteps; i++) {
//			pos = start + inc * i;
//			syncDrive(PHI_DEVICE_NAME, pos);
//			
//		}
//	}
	
	public static ISicsProxy getProxy() {
		return SicsManager.getSicsProxy();
	}
	
	private static ISicsModel getModel() {
		return SicsManager.getSicsModel();
	}
	
	private static ControlHelper instance;
	
	public static synchronized ControlHelper getInstance() {
		if (instance == null) {
			instance = new ControlHelper();
		}
		return instance;
	}
	
	public void observePath(final String path, final Label currentControl, final Text targetControl) {
		final ISicsControllerListener listener = new ControllerListener(currentControl, targetControl);
		final ISicsController controller = SicsManager.getSicsModel().findController(path);
		if (controller != null) {
			controller.addControllerListener(listener);
		}
		targetControl.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				controller.removeControllerListener(listener);
			}
		});
		getProxy().addProxyListener(new SicsProxyListenerAdapter() {
			
			@Override
			public void connect() {
				if (controller instanceof IDynamicController) {
					try {
						Object value = ((IDynamicController) controller).getValue();
						currentControl.setText(String.valueOf(value));
					} catch (SicsModelException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public void addProxyListener(ISicsProxyListener listener) {
		getProxy().addProxyListener(listener);
	}
	
	public void removeProxyListener(ISicsProxyListener listener) {
		getProxy().removeProxyListener(listener);
	}
	
	public boolean isConnected() {
		return getProxy().isConnected();
	}
	
	class ControllerListener implements ISicsControllerListener {

		private Label currentControl;
		private Text targetControl;
		private Object currentValue;
		
		public ControllerListener(Label current, Text target) {
			this.currentControl = current;
			this.targetControl = target;
		}
		
		@Override
		public void updateState(final ControllerState oldState, final ControllerState newState) {
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (newState == ControllerState.BUSY) {
						currentControl.setForeground(BUSY_COLOR);
					} else {
						currentControl.setForeground(IDLE_COLOR);
					}
				}
			});
			
		}
		
		@Override
		public void updateValue(final Object oldValue, final Object newValue) {
			if (newValue != null && !newValue.toString().equals(currentValue)) {
				currentValue = newValue.toString();
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						currentControl.setText(String.valueOf(newValue));
					}
				});
			}
		}
		
		@Override
		public void updateEnabled(boolean isEnabled) {
		}
		
		@Override
		public void updateTarget(final Object oldValue, final Object newValue) {
			if (newValue != null) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						targetControl.setText(String.valueOf(newValue));
					}
				});
			}
		}
	}
	
	public static String syncExec(String command) throws KoalaServerException {
		String res;
		try {
			res = getProxy().syncRun(command);
		} catch (Exception e) {
			throw new KoalaServerException(e);
		}
		return res;
	}

	public static void asyncExec(String command) throws KoalaServerException {
		try {
			getProxy().asyncRun(command, null);
		} catch (Exception e) {
			throw new KoalaServerException(e);
		}
	}

	public static void publishGumtreeStatus(String status) throws KoalaServerException {
		try {
			getProxy().syncRun(String.format("hset %s %s", 
					System.getProperty(ControlHelper.GUMTREE_STATUS_PATH), status));
		} catch (Exception e) {
			throw new KoalaServerException(e);
		}
	}
	
	public static void interrupt() throws KoalaServerException {
//		asyncExec("INT1712 3");
		getProxy().interrupt();
		CollectionHelper.getInstance().abort();
		experimentModel.getPhysicsModel().finish();
		experimentModel.getChemistryModel().finish();
	}
	
	public static void syncDrive(String deviceName, float value) 
			throws KoalaServerException, KoalaInterruptionException {
		ISicsController device = getModel().findController(deviceName);
		if (device instanceof IDriveableController) {
			try {
				((IDriveableController) device).drive(value);
			} catch (SicsException e) {
				if (e instanceof SicsInterruptException) {
					throw new KoalaInterruptionException(e);
				} else {
					throw new KoalaServerException(e);
				}
			}
		} else {
			throw new KoalaServerException("device not driveable: " + deviceName);
		}
	}

	public static void syncMultiDrive(Map<String, Number> devices) 
			throws KoalaServerException, KoalaInterruptionException {
		try {
			getProxy().multiDrive(devices);
		} catch (SicsException e) {
			if (e instanceof SicsInterruptException) {
				throw new KoalaInterruptionException(e);
			} else {
				throw new KoalaServerException(e);
			}
		}
	}

	public static void syncCollect(int exposure) 
			throws KoalaServerException, KoalaInterruptionException {
		CollectionHelper.getInstance().collect(exposure);
	}
	
	public static void endExposure() throws KoalaServerException {
//		IDynamicController expTimeController = (IDynamicController) getModel().findController(EXPO_TIME_NAME);
//		if (expTimeController != null) {
//			try {
//				expTimeController.setValue(0);
//			} catch (SicsException e) {
//				// TODO Auto-generated catch block
//				throw new KoalaServerException("failed to end exposure: " + e.getMessage());
//			}
//		} else {
//			throw new KoalaServerException(String.format("failed to end exposure: can't find %s node in model", 
//					EXPO_TIME_NAME));
//		}
		CollectionHelper.getInstance().endExposure();
	}
	
	public static String getProposalFolder() {
		if (proposalFolder == null) {
			proposalFolder = Activator.getPreference(Activator.NAME_PROP_FOLDER);
		}
		return proposalFolder;
	}
	
	public void popupInfo(final String title, final String text, final Shell shell) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openInformation(shell, title, text);
			}
		});
	}

}