/*******************************************************************************
 * Copyright (c) 2007 Australian Nuclear Science and Technology Organisation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Norman Xiong (Bragg Institute) - initial API and implementation
 *******************************************************************************/
package au.gov.ansto.bragg.echidna.ui.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.gumtree.gumnix.sics.batch.ui.SicsVisualBatchViewer;
import org.slf4j.Logger;

public class HighTemperaturePerspective implements IPerspectiveFactory {

	public static final String EXPERIMENT_PERSPECTIVE_ID = "au.gov.ansto.bragg.echidna.ui.HighTemperaturePerspective";
//	public static final String PLOT_VIEW_ID = "au.gov.ansto.bragg.kakadu.ui.views.PlotView";
//	public static final String MASK_PROPERTIES_VIEW_ID = "au.gov.ansto.bragg.kakadu.ui.views.MaskPropertiesView";
//	public static final String ALGORITHM_LIST_VIEW_ID = "au.gov.ansto.bragg.kakadu.ui.views.AlgorithmListView";
//	public static final String DATA_SOURCE_VIEW_ID = "au.gov.ansto.bragg.kakadu.ui.instrument.InstrumentDataSourceView";
//	public static final String OPERATION_PARAMETERS_VIEW_ID = "au.gov.ansto.bragg.kakadu.ui.views.OperationParametersView";
//	public static final String ANALYSIS_PARAMETERS_VIEW_ID = "au.gov.ansto.bragg.wombat.ui.views.WombatAnalysisControlView";
//	public static final String EXPORT_ALL_VIEW_ID = "au.gov.ansto.bragg.kowari.ui.views.ExportAllView";
//	public static final String BEANSHELL_VIEW_ID = "org.gumtree.ui.cli.beanShellTerminalview";
	public static final String WORKFLOW_VIEW_ID = "au.gov.ansto.bragg.echidna.ui.views.EchidnaBatchEditingView";
//	public static final String FILTERED_STATUS_MONITOR_VIEW_ID = "org.gumtree.dashboard.ui.rcp.FilteredStatusMonitorView";
	public static final String COMMAND_LINE_VIEW_ID = "org.gumtree.scripting.ui.commandLineView";
	public static final String SICS_TERMINAL_VIEW_ID = "au.gov.ansto.bragg.nbi.ui.SicsTerminalView";
	public static final String SICS_BUFFER_RUNNER = "org.gumtree.gumnix.sics.batch.ui.batchBufferManagerView";
	public static final String PROJECT_EXPLORER_VIEW_ID = "org.eclipse.ui.navigator.ProjectExplorer";
	public static final String CONTROL_VIEW_ID = "au.gov.ansto.bragg.echidna.ui.views.EchidnaControlView";
	public static final String ID_VIEW_SPY_VIEW = "org.gumtree.dashboard.ui.rcp.spyView";
	public static final String ID_TCL_EDITOR = "org.eclipse.dltk.tcl.ui.editor.TclEditor";
	public static final String ID_VIEW_ACTIVITY_MONITOR = "au.gov.ansto.bragg.nbi.ui.SicsRealtimeDataView";
	public static final String FOLDER_TEMPLATE = "Templates";
	public static final String FILE_TEMPLATE = "HighTemperatureTemplate.tmp";
	
	private static Logger logger;

	
//	private IFolderLayout top;
//	private IPlaceholderFolderLayout rightTop;
//	private IPlaceholderFolderLayout rightCenter;
//	private IPlaceholderFolderLayout rightBottom;
//	private IFolderLayout bottom;
//	private static KakaduDOM kakadu = KakaduDOMFactory.getKakaduDOM();
//	private static CicadaDOM cicada = (CicadaDOM) CicadaDOMFactory.getCicadaDOM();

	public void createInitialLayout(IPageLayout factory) {
		
//		factory.addShowViewShortcut(PLOT_VIEW_ID);
		
		factory.addPerspectiveShortcut(EXPERIMENT_PERSPECTIVE_ID);

//		factory.addStandaloneView(CONTROL_VIEW_ID, false, 
//				IPageLayout.BOTTOM, 0.70f, factory.getEditorArea());
		
		factory.addStandaloneView(ID_VIEW_ACTIVITY_MONITOR, false, 
				IPageLayout.BOTTOM, 0.70f, factory.getEditorArea());
		
		factory.addStandaloneView(SICS_TERMINAL_VIEW_ID, false, 
				IPageLayout.LEFT, 0.50f, ID_VIEW_ACTIVITY_MONITOR);

		factory.addStandaloneView(PROJECT_EXPLORER_VIEW_ID, true, 
				IPageLayout.LEFT, 0.5f, SICS_TERMINAL_VIEW_ID);
		
//		factory.addStandaloneView(WORKFLOW_VIEW_ID, true, 
//				IPageLayout.TOP, 0.99f, factory.getEditorArea());

		factory.addStandaloneView(SICS_BUFFER_RUNNER, true, 
				IPageLayout.RIGHT, 0.502f, factory.getEditorArea());

//		IFolderLayout bottomCenter = factory.createFolder("bottomCenter", 
//				IPageLayout.LEFT, 0.382f, PLOT_VIEW_ID + ":3");
//		bottomCenter.addView(ANALYSIS_PARAMETERS_VIEW_ID);
		
//		factory.addStandaloneView(FILTERED_STATUS_MONITOR_VIEW_ID, false, 
//				IPageLayout.BOTTOM, 0.493f, PLOT_VIEW_ID + ":2");
//

//		IWorkbench workbench = PlatformUI.getWorkbench();
		
//		IEditorInput input = new NullEditorInput();
		String makeNewEditor = System.getProperty("gumtree.echidna.openNewEditor", "false");
		if (Boolean.parseBoolean(makeNewEditor)) {
			try {
				IFileEditorInput input = getFileStorage(0);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
						input, ID_TCL_EDITOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		workbench.getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {
//			
//			@Override
//			public void perspectiveChanged(IWorkbenchPage page,
//					IPerspectiveDescriptor perspective, String changeId) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void perspectiveActivated(IWorkbenchPage page,
//					IPerspectiveDescriptor perspective) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		 
//	    try {
//	        PlatformUI.getWorkbench().getActiveWorkbenchWindow().;
//	    } catch ( Exception e ) {
//	        e.printStackTrace();
//	    }

		
//		factory.addStandaloneView(EXPORT_ALL_VIEW_ID, false, 
//				IPageLayout.BOTTOM, 0.94f, factory.getEditorArea());

//		factory.addView(WORKFLOW_VIEW_ID, IPageLayout.TOP, 1f, factory.getEditorArea());
		
//		factory.addStandaloneView(FILTERED_STATUS_MONITOR_VIEW_ID, false, 
//				IPageLayout.BOTTOM, 0.663f, WORKFLOW_VIEW_ID);

//		IFolderLayout bottomLeft = factory.createFolder(
//				"bottomLeft", //NON-NLS-1
//				IPageLayout.BOTTOM,
//				0.663f,
//				WORKFLOW_VIEW_ID);
//		bottomLeft.addView(FILTERED_STATUS_MONITOR_VIEW_ID);
		
//		IFolderLayout bottomLeft2 = factory.createFolder(
//				"bottomLeft2", //NON-NLS-1
//				IPageLayout.RIGHT,
//				0.53f,
//				"bottomLeft");
//		bottomLeft2.addView(SICS_TERMINAL_VIEW_ID);
				
				
//		IPerspectiveDescriptor descriptor = factory.getDescriptor();
//		try {
//			PlotManager.resetPlotViewId();
//			kakadu.loadAlgorithm(cicada.loadAlgorithm("Online Reduction"), descriptor);
//			AlgorithmTask task = ProjectManager.getCurrentAlgorithmTask();
//			task.runAlgorithm();
////			ExportAllView.setAlgorithmTask(task);
//		} catch (NoneAlgorithmException e) {
//			e.printStackTrace();
//		}
//		factory.setEditorAreaVisible(false);
//		factory.getViewLayout(CONTROL_VIEW_ID).setCloseable(false);
//		factory.getViewLayout(WORKFLOW_VIEW_ID).setCloseable(false);
//		factory.getViewLayout(WORKFLOW_VIEW_ID).setMoveable(false);
//		factory.getViewLayout(PROJECT_EXPLORER_VIEW_ID).setCloseable(false);
//		factory.getViewLayout(PROJECT_EXPLORER_VIEW_ID).setMoveable(false);
//		factory.getViewLayout(SICS_BUFFER_RUNNER).setCloseable(false);
//		factory.getViewLayout(SICS_BUFFER_RUNNER).setMoveable(false);
		factory.setEditorAreaVisible(true);
//		factory.getViewLayout(FILTERED_STATUS_MONITOR_VIEW_ID).setCloseable(false);
//		factory.getViewLayout(ANALYSIS_PARAMETERS_VIEW_ID).setCloseable(false);
		
//		factory.setFixed(true);
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#finalize()
//	 */
//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//		IViewReference viewReferences[] = PlatformUI.getWorkbench()
//		.getActiveWorkbenchWindow().getActivePage().getViewReferences();
//		IViewPart view = null;
//		for (int i = 0; i < viewReferences.length; i++){
//			System.out.println(viewReferences[i].getId());
//			if (viewReferences[i].getId().equals(SICS_TERMINAL_VIEW_ID)){
//				view = viewReferences[i].getView(false);
//			}
//		}
//		if (view != null && view instanceof ICommandLineTerminal){
//			CommandLineTerminal.addViewActivationCount();
//			System.out.println("view available");
//			try {
//				ICommandLineTerminal terminal = (ICommandLineTerminal) view;
//				terminal.selectCommunicationAdapter(SICS_TELNET_ADAPTOR_ID);
//				terminal.connect();
//			} catch (Exception e) {
//				getLogger().error("can not open sics terminal", e);
//			}
//		}
//	}

//	public static CicadaDOM getCicadaDOM(){
//		if (cicada == null)
//			cicada = (CicadaDOM) CicadaDOMFactory.getCicadaDOM();
//		return cicada;
//	}
	private static String getBatchDateString() {
		Date date = Calendar.getInstance().getTime();
		String dateString = new SimpleDateFormat("yyMMddHHmmss").format(date);
		return "batch" + dateString;
	}
	
	private static IFileEditorInput getFileStorage(int index) throws CoreException {
		String filename = getBatchDateString();
		filename += (index > 0 ? "_" + index : "") + ".tcl";
		IFolder templateFolder = SicsVisualBatchViewer.getProjectFolder(
				SicsVisualBatchViewer.EXPERIMENT_PROJECT, FOLDER_TEMPLATE);
		IFile templateFile = templateFolder.getFile(FILE_TEMPLATE);
		IFolder folder = SicsVisualBatchViewer.getProjectFolder(
				SicsVisualBatchViewer.EXPERIMENT_PROJECT, SicsVisualBatchViewer.AUTOSAVE_FOLDER);
		IFileEditorInput input = new FileEditorInput(
				folder.getFile(filename));
		if (input.exists()) {
			return getFileStorage(index++);
		} else {
			try {
				byte[] read = null;
				if (templateFile.exists()) {
					long size = EFS.getStore(templateFile.getLocationURI()).fetchInfo().getLength();
//					long size = templateFolder.getLocation().toFile().length();
					read = new byte[(int) size];
					InputStream inputStream = templateFile.getContents();
					inputStream.read(read);
				} else {
					read = new byte[0];
					templateFile.create(new ByteArrayInputStream(read), 
							IResource.ALLOW_MISSING_LOCAL, null);
				}
				input.getFile().create(new ByteArrayInputStream(read), 
						IResource.ALLOW_MISSING_LOCAL, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return input;
	}
}
