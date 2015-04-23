package edu.pddl.mmcr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import edu.pddl.mmcr.controller.Constants;
import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.Transport;

/**
 * 
 * @author tony
 *
 */
public class TransportRoutePanel extends JPanel implements ActionListener,
		TableModelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6952549289264640594L;

	private Transport transport = null;
	private Controller controller = null;

	private JTable routeTable = null;
	private RouteTableModel routeTableModel = null;

	private Map<Location, Integer> locationToRowMap = null; // Origins
	private Map<Location, Integer> locationToColumnMap = null; // Destinations

	public TransportRoutePanel(Transport transport, Controller controller) {
		this.transport = transport;
		this.controller = controller;
		this.controller.addActionListener(this);
		this.locationToRowMap = new IdentityHashMap<Location, Integer>();
		this.locationToColumnMap = new IdentityHashMap<Location, Integer>();
		setLayout(new BorderLayout());
		initRouteTable();
		initLocations();
		initRoutes();
	}

	private void initRoutes() {
		Map<Location, Map<Location, Integer>> routes = transport.getRoutes();
		for (Location origin : routes.keySet()) {
			Map<Location, Integer> destinationTimeMap = routes.get(origin);
			int row = locationToRowMap.get(origin);
			for (Location destination : destinationTimeMap.keySet()) {
				Integer time = destinationTimeMap.get(destination);
				if (time != null) {
					int col = locationToColumnMap.get(destination);
					@SuppressWarnings("unchecked")
					Vector<Object> rowData = (Vector<Object>) routeTableModel
							.getDataVector().get(row);
					rowData.setElementAt(time, col);
				}
			}
		}

	}

	private void initLocations() {
		Vector<Location> locations = controller.getLocations();
		for (Location location : locations) {
			addLocation(location);
		}
	}

	private void initRouteTable() {
		// init center panel
		Object[] columnNames = { "Origin \u25BC / Destination \u25BA" };
		routeTableModel = new RouteTableModel(columnNames, 0);
		routeTableModel.addTableModelListener(this);
		routeTable = new JTable(routeTableModel) {

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
		routeTable.setRowHeight(25);
		routeTable.setDefaultRenderer(Integer.class,
				new TransportLocationTableCellRenderer());
		routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		routeTable.getTableHeader().setReorderingAllowed(false);

		JLabel routesLabel = new JLabel("Travel Times:");

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(routesLabel, BorderLayout.NORTH);
		centerPanel.add(new JScrollPane(routeTable), BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
	}

	private void addLocation(Location location) {
		Vector<Object> columnData = new Vector<Object>();
		locationToColumnMap.put(location, routeTableModel.getColumnCount());
		routeTableModel.addColumn(location.getName(), columnData);
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		locationToRowMap.put(location, routeTableModel.getRowCount());
		routeTableModel.addRow(rowData);
	}

	private void updateLocation(Location location) {
		Integer row = locationToRowMap.get(location);
		Integer col = locationToColumnMap.get(location);
		if (row != null) {
			// update row
			routeTable.setValueAt(location.getName(), row, 0);
		}
		if (col != null) {
			// update column
			routeTable.getTableHeader().getColumnModel().getColumn(col)
					.setHeaderValue(location.getName());
		}
		repaint();
	}

	private void removeLocation(Location location) {
		Integer row = locationToRowMap.remove(location);
		Integer col = locationToColumnMap.remove(location);
		if (col != null) {
			TableColumn column = routeTable.getTableHeader().getColumnModel()
					.getColumn(col);
			routeTableModel.removeColumn(col);
			routeTable.removeColumn(column);
		}
		if (row != null) {
			routeTableModel.removeRow(row);
		}
	}

	private Location getLocationFromColumn(int col) {
		for (Location location : locationToColumnMap.keySet()) {
			if (col == locationToColumnMap.get(location)) {
				return location;
			}
		}
		return null;
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
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			if ((row < 0) || (col < 0)) {
				return;
			}
			Object newObj = routeTableModel.getValueAt(row, col);
			Location origin = getLocationFromRow(row);
			Location destination = getLocationFromColumn(col);
			if (newObj != null) {
				String newValue = newObj.toString();
				int travelTime = Integer.parseInt(newValue);
				if (travelTime < 0) {
					throw new RuntimeException("Transport route travel time cannot be negative.");
				}
				controller.setTransportRoute(transport, origin,
						destination, travelTime);
				return;
			}
			controller.removeTransportRoute(transport, origin, destination);
		}
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
}
