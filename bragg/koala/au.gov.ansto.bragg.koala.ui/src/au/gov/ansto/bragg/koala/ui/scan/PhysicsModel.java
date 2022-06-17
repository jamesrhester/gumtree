package au.gov.ansto.bragg.koala.ui.scan;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.gumtree.msw.ui.ktable.KTableCellEditor;
import org.gumtree.msw.ui.ktable.KTableCellRenderer;
import org.gumtree.msw.ui.ktable.editors.KTableCellEditorComboText;
import org.gumtree.msw.ui.ktable.editors.KTableCellEditorText;
import org.gumtree.msw.ui.ktable.renderers.DefaultCellRenderer;
import org.gumtree.msw.ui.ktable.renderers.FixedCellRenderer;
import org.gumtree.msw.ui.ktable.renderers.TextCellRenderer;

import au.gov.ansto.bragg.koala.ui.Activator;

public class PhysicsModel extends AbstractScanModel {

	private FixedCellRenderer fixedRenderer;
	private TextCellRenderer liteOddTextRenderer;
	private TextCellRenderer liteEvenTextRenderer;
	
	private static final int COLUMN_COUNTS = 13;
	private static final int[] COLUMN_WIDTH = {40, 40, 180, 120, 120, 80, 120, 80, 100, 120, 200, 80, 200};
	private static final String[] COLUMN_TITLE = {
			"", 
			"", 
			"Scan variable",
			"Start", 
			"INCR", 
			"NUM", 
			"Final", 
			"EXPO",
			"TEMP",
			"CHI",
			"Filename",
			"Status",
			"Comments"
	};
	
	public PhysicsModel() {
		super();
		liteOddTextRenderer = new TextCellRenderer(DefaultCellRenderer.INDICATION_FOCUS_ROW);
		liteOddTextRenderer.setForeground(Activator.getLightForgroundColor());
		liteOddTextRenderer.setFont(Activator.getMiddleFont());
		
		liteEvenTextRenderer = new TextCellRenderer(DefaultCellRenderer.INDICATION_FOCUS_ROW);
		liteEvenTextRenderer.setForeground(Activator.getLightForgroundColor());
		liteEvenTextRenderer.setFont(Activator.getMiddleFont());
		liteEvenTextRenderer.setBackground(
				Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		fixedRenderer = new FixedCellRenderer(DefaultCellRenderer.STYLE_FLAT);
		fixedRenderer.setFont(Activator.getMiddleFont());
	}
	
//	@Override
//	public KTableCellRenderer getCellRenderer(int col, int row) {
//		if (row > 0) {
//			if (row % 2 == 1 && col == 8) {
//				if (getItem(row).getTarget().isTemperature()) {
//					return fixedRenderer;
//				}
//			}
////			if (row % 2 == 0 && col == 2) {
////				return fixedRenderer;
////			}
//		}
//		return super.getCellRenderer(col, row);
//	}
	@Override
	public KTableCellRenderer getCellRenderer(int col, int row) {
		if (row > 0) {
			if (col == 4) {
				SingleScan scan = scanList.get(row - 1);
				if (scan.getTarget().isPoints() && scan.getPoints().trim().length() == 0) {
					if (row % 2 == 0) {
						return liteEvenTextRenderer;
					} else {
						return liteOddTextRenderer;
					}
				}
			}
		}
		return super.getCellRenderer(col, row);
	}
	
	@Override
	public Object getContentAt(int col, int row) {
		if (row == 0) {
			return COLUMN_TITLE[col];
		}
		if (row > scanList.size()) {
			throw new IllegalArgumentException("scan row index out of range");
		}
		SingleScan scan = scanList.get(row - 1);
		switch (col) {
		case 2:
			return scan.getTarget().getText();
		case 3:
			if (scan.getTarget().isPoints()) {
				return "values:";
			} else {
				return scan.getStart();
			}
		case 4:
			if (scan.getTarget().isPoints()) {
				String points = scan.getPoints();
				if (points.trim().length() == 0) {
					return "#, #, ...";
				} else {
					return scan.getPoints();
				}
			} else {
				return scan.getInc();
			}
		case 5:
//			if (scan.getTarget().isPoints()) {
//				return "";
//			} else {
				return scan.getNumber();
//			}
		case 6:
//			if (scan.getTarget().isPoints()) {
//				return "";
//			} else {
				return scan.getEnd();
//			}
		case 7: 
			return scan.getExposure();
		case 8:
			float temp = scan.getTemp();
			if (Float.isNaN(temp) || temp == 0) {
				return "";
			} else {
				return temp;
			}
//			if (row == 1) {
//				return temp;
//			} else {
//				if (getItem(row).getTarget().isTemperature()) {
//					return "";
//				}
//				float last = scanList.get(row - 2).getTemp();
//				if (temp == last) {
//					return "";
//				} else {
//					return temp;
//				}
//			}
		case 9:
			float chi = scan.getChi();
			if (Float.isNaN(chi)) {
				return "";
			} else {
				return chi;
			}
//			if (row == 1) {
//				return chi;
//			} else {
//				float last = scanList.get(row - 2).getChi();
//				if (chi == last) {
//					return "";
//				} else {
//					return chi;
//				}
//			}
		case 10:
			return scan.getFilename();
		case 11:
			return scan.getStatus();
		case 12:
			return scan.getComments();
		default:
			break;
		}
		return "";
	}

	@Override
	public KTableCellEditor getCellEditor(int col, int row) {
		if (row <= 0) {
			return null;
		}
		if (col == 2) {
			KTableCellEditorComboText editor = new KTableCellEditorComboText();
			editor.setVisibleItemCount(4);
			editor.setFont(Activator.getMiddleFont());
			editor.setItems(ScanTarget.getAllText());
			return editor;
		} else if (col == 3) {
			if (getItem(row).getTarget().isPoints()) {
				return null;
			} else {
				return new KTableCellEditorText();
			}
		} else if (col == 8) {
			if (getItem(row).getTarget().isTemperature()) {
				return null;
			} else {
				return new KTableCellEditorText();
			}
		} else if (col > 1 && col != 10) {
			return new KTableCellEditorText();
		} else {
			return null;
		}
	}

	@Override
	public void setContentAt(int col, int row, Object value) {
		if (row > scanList.size()) {
			throw new IllegalArgumentException("scan row index out of range");
		}
		SingleScan scan = scanList.get(row - 1);
		switch (col) {
		case 2:
			scan.setTarget(ScanTarget.valueOfText(String.valueOf(value)));
			redrawRow(row);
			break;
		case 3:
			if (value instanceof Float) {
				scan.setStart((Float) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setStart(Float.valueOf(t));
				}
			}
			break;
		case 4:
			if (scan.getTarget().isPoints()) {
				scan.setPoints(String.valueOf(value));
			} else {
				if (value instanceof Float) {
					scan.setInc((Float) value);
				} else {
					String t = String.valueOf(value);
					if (t.length() > 0) {
						scan.setInc(Float.parseFloat(t));
					}
				}
			}
			redrawRow(row);
			break;
		case 5:
			if (value instanceof Integer) {
				scan.setNumber((Integer) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setNumber(Integer.parseInt(t));
				}
			}
			redrawRow(row);
			break;
		case 6:
			if (value instanceof Float) {
				scan.setEnd((Float) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setEnd(Float.parseFloat(t));
				}
			}
			redrawRow(row);
			break;
		case 7:
			if (value instanceof Integer) {
				scan.setExposure((Integer) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setExposure(Integer.parseInt(t));
				}
			}
			break;
		case 8:
			if (value instanceof Float) {
				scan.setTemp((Float) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setTemp(Float.parseFloat(t));
				}
			}
			break;
		case 9:
			if (value instanceof Float) {
				scan.setChi((Float) value);
			} else {
				String t = String.valueOf(value);
				if (t.length() > 0) {
					scan.setChi(Float.parseFloat(t));
				}
			}
			break;
		case 10:
			scan.setFilename(String.valueOf(value));
			break;
		case 11:
			break;
		case 12:
			scan.setComments(String.valueOf(value));
			break;
		default:
			break;
		}
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_COUNTS;
	}

	@Override
	public int getColumnWidth(int col) {
		return COLUMN_WIDTH[col];
	}

	@Override
	public Point belongsToCell(int col, int row) {
		if (row == 0) {
			if (col == 1) {
				return new Point(0, 0);
			} else {
				return null;
			}
		} else {
			if (col > 4 && col <= 6) {
				SingleScan scan = scanList.get(row - 1);
				if (scan.getTarget().isPoints()) {
					return new Point(4, row);
				}
			}
		}
//		else {
//			if (row % 2 == 1) {
//				if (col == 5 || col == 6) {
//					SingleScan scan = getItem(row);
//					if (scan.getTarget().isPoints()) {
//						return new Point(4, row);
//					}
//				} else {
//					return null;
//				}
//			} else {
//				if (col == 0) {
//					return new Point(0, row - 1);
//				} else if (col == 1) {
//					return new Point(1, row - 1);
//				} else if (col > 3 && col < 11) {
//					return new Point(3, row);
//				} else if (col == 11) {
//					return new Point(11, row - 1);
//				}
//			}
//		}
		return null;
	}
	
	private List<Float> getPointValues(SingleScan scan) {
		String[] items = scan.getPoints().split(",");
		List<Float> values = new ArrayList<Float>();
		for (int i = 0; i < items.length; i++) {
			float v = Float.valueOf(items[i].trim());
			if (!Float.isNaN(v)) {
				values.add(v);
			}
		}
		return values;
	}
	
	@Override
	public int getTimeEstimation() {
		int time = 0;
		for (SingleScan scan : scanList) {
			if (scan.getTarget().isPoints()) {
				List<Float> values = getPointValues(scan);
				if (values.size() > 0) {
					if (scan.getExposure() > 0) {
						time += values.size() * (
								PHI_TIME + 
								scan.getExposure() + 
								ERASURE_TIME + 
								READING_TIME);
					}
					if (!Float.isNaN(scan.getTemp())) {
						time += TEMP_TIME;
					}
					if (!Float.isNaN(scan.getChi())) {
						time += CHI_TIME;
					}
				}
			} else {
				if (scan.getExposure() > 0) {
					time += scan.getNumber() * (
							PHI_TIME + 
							scan.getExposure() + 
							ERASURE_TIME + 
							READING_TIME);
				}
				if (scan.getTarget() != ScanTarget.TEMP_LOOP 
						&& scan.getTarget() != ScanTarget.TEMP_POINTS) {
					if (!Float.isNaN(scan.getTemp())) {
						time += TEMP_TIME;
					}
				}
				if (!Float.isNaN(scan.getChi())) {
					time += CHI_TIME;
				}
			}
		}
		return time;
	}

	@Override
	public int getTimeLeft() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getFinishTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}
