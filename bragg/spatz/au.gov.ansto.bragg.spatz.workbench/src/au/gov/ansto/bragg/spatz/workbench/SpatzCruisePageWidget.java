package au.gov.ansto.bragg.spatz.workbench;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.pgroup.AbstractGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.ChevronsToggleRenderer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.SimpleGroupStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.gumtree.gumnix.sics.ui.widgets.HMVetoGadget;
import org.gumtree.gumnix.sics.widgets.swt.DeviceStatusWidget;
import org.gumtree.gumnix.sics.widgets.swt.EnvironmentControlWidget;
import org.gumtree.gumnix.sics.widgets.swt.ShutterStatusWidget;
import org.gumtree.gumnix.sics.widgets.swt.SicsStatusWidget;
import org.gumtree.service.dataaccess.IDataAccessManager;
import org.gumtree.ui.cruise.support.AbstractCruisePageWidget;
import org.gumtree.util.messaging.IDelayEventExecutor;
import org.gumtree.util.messaging.ReducedDelayEventExecutor;

import au.gov.ansto.bragg.nbi.ui.core.SharedImage;
import au.gov.ansto.bragg.nbi.workbench.ReactorStatusWidget;

public class SpatzCruisePageWidget extends AbstractCruisePageWidget {

	@Inject
	private IEclipseContext eclipseContext;

	private IDelayEventExecutor delayEventExecutor;

	@Inject
	private IDataAccessManager dataAccessManager;

	public SpatzCruisePageWidget(Composite parent, int style) {
		super(parent, style);
	}

	@PostConstruct
	public void render() {
		GridLayoutFactory.swtDefaults().spacing(1, 0)
				.applyTo(this);
		getEclipseContext().set(IDelayEventExecutor.class,
				getDelayEventExecutor());
		
		// Reactor Source
		PGroup sourceGroup = createGroup("REACTOR SOURCE",
				SharedImage.REACTOR.getImage());
		ReactorStatusWidget reactorWidget = new ReactorStatusWidget(sourceGroup, SWT.NONE);
		reactorWidget.addDevice("reactorPower", "Power", "MW")
				.addDevice("cnsInTemp", "CNS Inlet Temp", "K")
				.addDevice("cnsOutTemp", "CNS Outlet Temp", "K");
		reactorWidget.createWidgetArea();
		configureWidget(reactorWidget);
		sourceGroup.setExpanded(false);
		reactorWidget.setExpandingEnabled(true);

		// Shutter Status
		PGroup shutterGroup = createGroup("SHUTTER STATUS",
				SharedImage.SHUTTER.getImage());
		ShutterStatusWidget shutterStatuswidget = new ShutterStatusWidget(
				shutterGroup, SWT.NONE);
		configureWidget(shutterStatuswidget);
		shutterGroup.setExpanded(false);

		// Server Status
		PGroup sicsStatusGroup = createGroup("SERVER STATUS", 
				SharedImage.SERVER.getImage());
		SicsStatusWidget statusWidget = new SicsStatusWidget(sicsStatusGroup,
				SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(statusWidget);
		configureWidget(statusWidget);

		// Pause Counter
		PGroup pauseGroup = createGroup("PAUSE COUNTING",
				SharedImage.SHUTTER.getImage());
		HMVetoGadget pauseStatuswidget = new HMVetoGadget(
				pauseGroup, SWT.NONE);
		configureWidget(pauseStatuswidget);

		DeviceStatusWidget deviceStatusWidget;
//		// Devices
		// Monitor Event Rate
		PGroup monitorGroup = createGroup("NEUTRON COUNTS",
				SharedImage.MONITOR.getImage());
		deviceStatusWidget = new DeviceStatusWidget(monitorGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/monitor/bm1_counts", "BM1 counts", null, "cts")
				.addDevice("/monitor/bm2_counts", "BM2 counts", null, "cts")
				.addDevice("/instrument/detector/total_counts", "Detector counts", null, "cts")
				;
		configureWidget(deviceStatusWidget);


		// Choppers
		PGroup chopperGroup = createGroup("CHOPPERS",
				SharedImage.POSITIONER.getImage());
		deviceStatusWidget = new DeviceStatusWidget(chopperGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/instrument/chpr/cp1/actspeed", "Ch1 speed", null, "rpm")
				.addDevice("/instrument/chpr/cp1/offset", "Ch1 phase offset", null, "\u00b0")
				.addDevice("/instrument/chpr/cp2/actspeed", "Ch2 speed", null, "rpm")
				.addDevice("/instrument/chpr/cp2/offset", "Ch2 phase offset", null, "\u00b0")
				.addDevice("/instrument/chpr/cp2b/actspeed", "Ch2b speed", null, "rpm")
				.addDevice("/instrument/chpr/cp2b/offset", "Ch2b phase offset", null, "\u00b0")
				.addDevice("/instrument/chpr/cp3/actspeed", "Ch3 speed", null, "rpm")
				.addDevice("/instrument/chpr/cp3/offset", "Ch3 phase offset", null, "\u00b0")
				;
		configureWidget(deviceStatusWidget);

//
//		// Detector
//		PGroup detectorGroup = createGroup("DETECTOR",
//				SharedImage.POWER.getImage());
//		deviceStatusWidget = new DeviceStatusWidget(detectorGroup, SWT.NONE);
//		deviceStatusWidget
//				.addDevice("/instrument/detector/total_counts", "total counts", null, "cts")
//				.addDevice("/instrument/detector/cdl", "cdl", null, null)
//				.addDevice("/instrument/detector/cdu", "cdu", null, null)
//				.addDevice("/instrument/det", "det", null, "")
//				;
//		configureWidget(deviceStatusWidget);

		// Choppers
//		PGroup chopperGroup = createGroup("CHOPPERS",
//				SharedImage.POSITIONER.getImage());
//		deviceStatusWidget = new DeviceStatusWidget(chopperGroup, SWT.NONE);
//		deviceStatusWidget
//				.addDevice("/instrument/master_chopper_id", "master chopper", null, "")
//				;
//		configureWidget(deviceStatusWidget);
//
//		// Sample Info
//		PGroup sampleGroup = createGroup("SAMPLE",
//				SharedImage.BEAKER.getImage());
//		deviceStatusWidget = new DeviceStatusWidget(sampleGroup, SWT.NONE);
//		deviceStatusWidget
//				.addDevice("/sample/name", "Name", null, "")
//				;
//		configureWidget(deviceStatusWidget);



		// Temperature TC1 Control
//		PGroup tempControlGroup = createGroup("TEMPERATURE CONTR",
//				SharedImage.FURNACE.getImage());
//		deviceStatusWidget = new DeviceStatusWidget(tempControlGroup, SWT.NONE);
//		deviceStatusWidget
//				.addDevice("/sample/tc1/sensor/sensorValueA", "TC1A",
//						SharedImage.A.getImage(), null)
//				.addDevice("/sample/tc1/sensor/sensorValueB", "TC1B",
//						SharedImage.B.getImage(), null)
//				.addDevice("/sample/tc1/sensor/sensorValueC", "TC1C",
//						SharedImage.C.getImage(), null)
//				.addDevice("/sample/tc1/sensor/sensorValueD", "TC1D",
//						SharedImage.D.getImage(), null);
//		configureWidget(deviceStatusWidget);

		// Slits Info
		PGroup slits1Group = createGroup("SLITS",
				SharedImage.ONE.getImage());
		deviceStatusWidget = new DeviceStatusWidget(slits1Group, SWT.NONE);
		deviceStatusWidget
				.addDevice("/instrument/slits/s2h", "s2h", null, "mm")
				.addDevice("/instrument/slits/s2v", "s2v", null, "mm")
				.addDevice("/instrument/slits/s3h", "s3h", null, "mm")
				.addDevice("/instrument/slits/s3v", "s3v", null, "mm")
				.addDevice("/instrument/slits/s4h", "s4h", null, "mm")
				.addDevice("/instrument/slits/s4v", "s4v", null, "mm")
				;
		configureWidget(deviceStatusWidget);

		// Monochromator
		PGroup monochromatorGroup = createGroup("MONOCHROMATOR",
				SharedImage.MONOCHROMATOR.getImage());
		deviceStatusWidget = new DeviceStatusWidget(monochromatorGroup, SWT.NONE);
		deviceStatusWidget
				.addDevice("/instrument/crystal/mom", "mom", null, "deg")
				.addDevice("/instrument/crystal/stth", "stth", null, "deg")
				;
		configureWidget(deviceStatusWidget);

		// Environment Group
		PGroup environmentGroup = createGroup("ENVIRONMENT CONTROLLERS",
				SharedImage.FURNACE.getImage());
		EnvironmentControlWidget controlWidget = new EnvironmentControlWidget(environmentGroup, SWT.NONE);
		configureWidget(controlWidget);
	}

	@Override
	protected void disposeWidget() {
		if (delayEventExecutor != null) {
			delayEventExecutor.deactivate();
			delayEventExecutor = null;
		}
		eclipseContext = null;
		dataAccessManager = null;
	}

	/*************************************************************************
	 * Components
	 *************************************************************************/

	public IEclipseContext getEclipseContext() {
		return eclipseContext;
	}

	public void setEclipseContext(IEclipseContext eclipseContext) {
		this.eclipseContext = eclipseContext.createChild();
	}

	public IDataAccessManager getDataAccessManager() {
		return dataAccessManager;
	}

	public void setDataAccessManager(IDataAccessManager dataAccessManager) {
		this.dataAccessManager = dataAccessManager;
	}

	public IDelayEventExecutor getDelayEventExecutor() {
		if (delayEventExecutor == null) {
			delayEventExecutor = new ReducedDelayEventExecutor(1000).activate();
		}
		return delayEventExecutor;
	}

	public void setDelayEventExecutor(IDelayEventExecutor delayEventExecutor) {
		this.delayEventExecutor = delayEventExecutor;
	}

	/*************************************************************************
	 * Utilities
	 *************************************************************************/

	protected PGroup createGroup(String text, Image image) {
		PGroup group = new PGroup(this, SWT.NONE);
		AbstractGroupStrategy strategy = new SimpleGroupStrategy();
		group.setStrategy(strategy);
		group.setToggleRenderer(new ChevronsToggleRenderer());
		group.setText(text);
		group.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT));
		group.setImage(image);
		group.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		group.setLinePosition(SWT.CENTER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(1).spacing(1, 1)
				.margins(10, SWT.DEFAULT).applyTo(group);
		return group;
	}

	protected void configureWidget(Control widget) {
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(widget);
		if (getEclipseContext() != null) {
			ContextInjectionFactory.inject(widget, getEclipseContext());
		}
	}

}
