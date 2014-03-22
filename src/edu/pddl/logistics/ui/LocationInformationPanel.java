package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import edu.pddl.logistics.controller.Constants;
import edu.pddl.logistics.controller.Controller;
import edu.pddl.logistics.model.Location;

public class LocationInformationPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;

	private Controller controller = null;

	private DefaultTableModel locationTableModel = null;
	private JTable locationTable = null;
	private JButton addLocation = null;
	private JButton removeLocation = null;

	private Map<Location, Integer> locationToRowMap = null;

	public LocationInformationPanel(Controller controller) {
		this.controller = controller;
		this.controller.addActionListener(this);
		this.locationToRowMap = new IdentityHashMap<Location, Integer>();
		setLayout(new BorderLayout());
		initLocationPanel();
	}

	private void initLocationPanel() {
		Object[] columnNames = { "Name", "Remaining Capacity",
				"Current Inventoy" };
		locationTableModel = new DefaultTableModel(columnNames, 0);
		locationTableModel.addTableModelListener(this);
		locationTable = new JTable(locationTableModel) {

			private static final long serialVersionUID = 1L;

			/*
			 * @Override public Class getColumnClass(int column) { return
			 * getValueAt(0, column).getClass(); }
			 */
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				default:
					return Integer.class;
				}
			}

			// Select all contents of the cell without overriding cell
			// validation
			@Override
			public Component prepareEditor(TableCellEditor editor, int row,
					int column) {
				Component c = super.prepareEditor(editor, row, column);
				if (c instanceof JTextComponent) {
					final JTextComponent jtc = (JTextComponent) c;
					jtc.requestFocus();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							jtc.selectAll();
						}
					});
				}
				return c;
			}
		};
		locationTable.setRowHeight(25);
		locationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationTable.getTableHeader().setReorderingAllowed(false);

		addLocation = new JButton("Add Location");
		addLocation.addActionListener(this);

		removeLocation = new JButton("Remove Location");
		removeLocation.addActionListener(this);

		JPanel southPanel = new JPanel(new GridLayout(1, 2));
		southPanel.add(addLocation);
		southPanel.add(removeLocation);

		JLabel locationInfoLabel = new JLabel("Location Information:");

		add(locationInfoLabel, BorderLayout.NORTH);
		add(new JScrollPane(locationTable), BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private void addLocation() {
		controller.addNewLocaiton();
	}

	private void addLocation(Location location) {
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		rowData.add(location.getRemainingCapacity());
		rowData.add(location.getCurrentInventory());
		locationToRowMap.put(location, locationTableModel.getRowCount());
		locationTableModel.addRow(rowData);
	}

	private void removeLocation() {
		int row = locationTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		Location location = getLocation(row);
		if (location != null) {
			controller.removeLocation(location);
		}
	}

	private void removeLocation(Location location) {
		Integer row = locationToRowMap.remove(location);
		if (row != null) {
			locationTableModel.removeRow(row);
		}
	}

	private void updateLocation(Location location) {
		Integer row = locationToRowMap.get(location);
		if (row != null) {
			@SuppressWarnings("unchecked")
			Vector<Object> rowData = (Vector<Object>) locationTableModel
					.getDataVector().get(row);
			rowData.setElementAt(location.getName(), 0);
			rowData.setElementAt(location.getRemainingCapacity(), 1);
			rowData.setElementAt(location.getCurrentInventory(), 2);
		}
	}

	private Location getLocation(int row) {
		for (Location location : locationToRowMap.keySet()) {
			if (row == locationToRowMap.get(location)) {
				return location;
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addLocation) {
			addLocation();
		} else if (source == removeLocation) {
			removeLocation();
		} else if (source instanceof Location) {
			Location location = (Location) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				addLocation(location);
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				removeLocation(location);
			} else if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				updateLocation(location);
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object newObj = locationTableModel.getValueAt(row, col);
			if (newObj == null) {
				return;
			}
			String newValue = newObj.toString();
			Location location = getLocation(row);
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					controller.setLocationName(location, newValue);
				} else {
					locationTableModel.setValueAt(location.getName(), row, col);
				}
				break;
			case 1:
				int cap = Integer.parseInt(newValue);
				controller.setLocationRemainingCapacity(location, cap);
				break;
			case 2:
				int inv = Integer.parseInt(newValue);
				controller.setLocationCurrentInventory(location, inv);
				break;
			}
		}
	}
}
