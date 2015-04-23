package edu.pddl.mmcr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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

import edu.pddl.mmcr.controller.Constants;
import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.Transport;

public class TransportLoadUnloadPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5502210460884451123L;

	private Transport transport = null;
	private Controller controller = null;

	private JTable loadUnloadTable = null;
	private DefaultTableModel loadUnloadTableModel = null;
	private Map<Location, Integer> locationToRowMap = null; // locations

	public TransportLoadUnloadPanel(Transport transport, Controller controller) {
		this.transport = transport;
		this.controller = controller;
		this.controller.addActionListener(this);
		this.locationToRowMap = new HashMap<>();
		setLayout(new BorderLayout());
		initLoadUnloadTable();
		initLocations();
		initTransport();
	}

	private void initLocations() {
		Vector<Location> locations = controller.getLocations();
		for (Location location : locations) {
			addLocation(location);
		}
	}

	private void initLoadUnloadTable() {
		// init center panel
		Object[] columnNames = { "Location", "Loading Time", "Unloading Time" };
		loadUnloadTableModel = new DefaultTableModel(columnNames, 0) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				}
				return true;
			}
		};
		loadUnloadTableModel.addTableModelListener(this);
		loadUnloadTable = new JTable(loadUnloadTableModel) {

			private static final long serialVersionUID = 1L;

			/*
			 * @Override public Class getColumnClass(int column) { return
			 * getValueAt(0, column).getClass(); }
			 */
			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
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

		loadUnloadTable.setRowHeight(25);
		loadUnloadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		loadUnloadTable.getTableHeader().setReorderingAllowed(false);

		JLabel loadUnloadLabel = new JLabel("Load/Unload Times:");

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(loadUnloadLabel, BorderLayout.NORTH);
		centerPanel.add(new JScrollPane(loadUnloadTable), BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
	}

	private void initTransport() {
		for (Location loc : controller.getLocations()) {
			Integer row = locationToRowMap.get(loc);
			Integer loadingTime = transport.getLoadingTime(loc);
			Integer unloadingTime = transport.getUnloadingTime(loc);
			@SuppressWarnings("unchecked")
			Vector<Object> rowData = (Vector<Object>) loadUnloadTableModel
					.getDataVector().get(row);
			if (loadingTime != null) {
				rowData.setElementAt(loadingTime, 1);
			}
			if (unloadingTime != null) {
				rowData.setElementAt(unloadingTime, 2);
			}
		}
	}

	private void addLocation(Location location) {
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		locationToRowMap.put(location, loadUnloadTableModel.getRowCount());
		loadUnloadTableModel.addRow(rowData);
	}

	private void updateLocation(Location location) {
		Integer row = locationToRowMap.get(location);
		if (row != null) {
			// update row
			loadUnloadTable.setValueAt(location.getName(), row, 0);
		}
		repaint();
	}

	private void removeLocation(Location location) {
		Integer row = locationToRowMap.remove(location);
		if (row != null) {
			loadUnloadTableModel.removeRow(row);
		}
	}

	private Location getLocationFromRow(int row) {
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
		if (source instanceof Location) {
			Location location = (Location) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				addLocation(location);
			} else if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				updateLocation(location);
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				removeLocation(location);
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object newObj = loadUnloadTableModel.getValueAt(row, col);
			Location location = getLocationFromRow(row);
			switch (col) {
			case 0:
				// This is the location name row do nothingtravelTimeme
				break;
			case 1:
				// This is loading time
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Integer loadingTime = Integer.parseInt(newValue);
						if (loadingTime < 0) {
							throw new RuntimeException(
									"Transport loading time cannot be negative");
						}
						controller.setTransportLoadingTime(transport, location,
								loadingTime);
						break;
					}
				}
				controller.removeTransportLoadingTime(transport, location);
				break;
			case 2:
				// This is unloading time
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Integer unloadingTime = Integer.parseInt(newValue);
						if (unloadingTime < 0) {
							throw new RuntimeException(
									"Transport unloading time cannot be negative");
						}
						controller.setTransportUnloadingTime(transport,
								location, unloadingTime);
						break;
					}
				}
				controller.removeTransportUnloadingTime(transport, location);
				break;
			default:
				// shouldn't get here
				throw new RuntimeException(
						"An error occured setting loading/unloading time.");
			}
		}
	}
}