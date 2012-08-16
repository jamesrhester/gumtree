package org.gumtree.ui.tasklet.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.eclipse.ui.part.FileEditorInput;
import org.gumtree.ui.internal.InternalImage;
import org.gumtree.ui.tasklet.ITasklet;
import org.gumtree.ui.tasklet.ITaskletRegistry;
import org.gumtree.ui.util.jface.ITreeNode;
import org.gumtree.ui.util.jface.TreeContentProvider;
import org.gumtree.ui.util.jface.TreeLabelProvider;
import org.gumtree.ui.util.jface.TreeNode;
import org.gumtree.ui.util.swt.IDNDHandler;
import org.gumtree.ui.widgets.ExtendedComposite;

import ch.lambdaj.collection.LambdaCollections;

@SuppressWarnings("restriction")
public class TaskletRegistryViewer extends ExtendedComposite {

	private ITaskletRegistry taskletRegistry;

	private IDNDHandler<ITaskletRegistry> dndHandler;

	@Inject
	public TaskletRegistryViewer(Composite parent, @Optional int style) {
		super(parent, style);
	}

	@PostConstruct
	public void render() {
		GridLayoutFactory.swtDefaults().applyTo(this);

		// Create search box
		Text searchText = getWidgetFactory().createText(this, "", SWT.SEARCH);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(searchText);

		// Create tree
		TreeViewer treeViewer = new TreeViewer(this);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL)
				.grab(true, true).applyTo(treeViewer.getControl());
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		treeViewer.setInput(createTreeNode("", false));

		// DnD
		Transfer[] transfers = new Transfer[] {
				LocalSelectionTransfer.getTransfer(),
				FileTransfer.getInstance(), EditorInputTransfer.getInstance() };
		treeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transfers,
				new DropTargetAdapter() {
					public void drop(DropTargetEvent event) {
						if (FileTransfer.getInstance().isSupportedType(
								event.currentDataType)) {
							// 1. Handle dropping from file system
							String[] filenames = (String[]) event.data;
							if (filenames.length == 1) {
								File file = new File(filenames[0]);
								launchAddTaskletDialog(file.toURI().toString());
							}
						} else if (EditorInputTransfer.getInstance()
								.isSupportedType(event.currentDataType)
								&& event.data instanceof EditorInputData[]) {
							// 2. Handle dropping from remote system explorer
							EditorInputData[] inputDatas = ((EditorInputData[]) event.data);
							if (inputDatas.length == 1) {
								IEditorInput input = inputDatas[0].input;
								if (input instanceof FileEditorInput) {
									IFile file = ((FileEditorInput) input)
											.getFile();
									launchAddTaskletDialog(file.getLocationURI().toString());
								}
							}
						} else if (LocalSelectionTransfer.getTransfer()
								.isSupportedType(event.currentDataType)
								&& event.data instanceof IStructuredSelection) {
							// 3. Handle dropping from project explorer
							List<?> files = ((IStructuredSelection) event.data)
									.toList();
							if (files != null && files.size() == 1) {
								if (files.get(0) instanceof IFile) {
									IFile file = (IFile) files.get(0);
									launchAddTaskletDialog(file.getLocationURI().toString());
								}
							}
						}
					}
				});
	}

	@Override
	protected void disposeWidget() {
	}

	/*************************************************************************
	 * Components
	 *************************************************************************/

	public ITaskletRegistry getTaskletRegistry() {
		return taskletRegistry;
	}

	@Inject
	public void setTaskletRegistry(ITaskletRegistry taskletRegistry) {
		this.taskletRegistry = taskletRegistry;
	}

	/*************************************************************************
	 * Utilities
	 *************************************************************************/

	private void launchAddTaskletDialog(String contributionUri) {
		AddTaskletDialog dialog = new AddTaskletDialog(getShell());
		dialog.setContributionUri(contributionUri);
		dialog.setTaskletRegistry(getTaskletRegistry());
		dialog.open();
	}
	
	protected ITreeNode[] createTreeNode(String filter, boolean isHierarchical) {
		List<ITreeNode> treeNodes = new ArrayList<ITreeNode>(2);
		for (ITasklet tasklet : getTaskletRegistry().getTasklets()) {
			treeNodes.add(new TaskletTreeNode(tasklet));
		}
		return LambdaCollections.with(treeNodes).toArray(ITreeNode.class);
	}

	public class TaskletTreeNode extends TreeNode {

		private ITasklet tasklet;

		public TaskletTreeNode(ITasklet tasklet) {
			this.tasklet = tasklet;
		}

		public String getText() {
			return getTasklet().getLabel();
		}

		public Image getImage() {
			return InternalImage.TASKLET_16.getImage();
		}

		public ITasklet getTasklet() {
			return tasklet;
		}

	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		TaskletRegistryViewer viewer = new TaskletRegistryViewer(shell,
				SWT.NONE);

		ITaskletRegistry registry = new TaskletRegistry();
		viewer.setTaskletRegistry(registry);
		ITasklet tasklet = new Tasklet();
		tasklet.setLabel("1D Scan");
		tasklet.setTags("experiment");
		registry.addTasklet(tasklet);
		tasklet = new Tasklet();
		tasklet.setLabel("Histgram Memory");
		tasklet.setTags("control, status");
		registry.addTasklet(tasklet);

		viewer.render();

		shell.setSize(500, 500);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
