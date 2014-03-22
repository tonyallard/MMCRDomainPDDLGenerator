package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import edu.pddl.logistics.controller.Constants;
import edu.pddl.logistics.controller.Controller;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class TransportAndRouteInformationPanel extends JPanel implements
		ActionListener, CellEditorListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6952549289264640594L;

	private Transport transport = null;
	private Controller controller = null;

	private JTable routeTable = null;
	private TransportInformationPanel tPanel = null;
	private RouteTableModel routeTableModel = null;

	private Map<Location, Integer> locationToRowMap = null; //Origins
	private Map<Location, Integer> locationToColumnMap = null; //Destinations

	public TransportAndRouteInformationPanel(Transport transport,
			Controller controller) {
		this.transport = transport;
		this.controller = controller;
		this.controller.addActionListener(this);
		this.locationToRowMap = new IdentityHashMap<Location, Integer>();
		this.locationToColumnMap = new IdentityHashMap<Location, Integer>();
		setLayout(new BorderLayout());
		initRouteTable();
		initTransportDataPanel();
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

	public Transport getTransport() {
		return transport;
	}

	private void initLocations() {
		Vector<Location> locations = controller.getLocations();
		for (Location location : locations) {
			addLocation(location);
		}
	}

	private void initTransportDataPanel() {
		tPanel = new TransportInformationPanel(transport, controller);
		add(tPanel, BorderLayout.NORTH);
	}

	private void initRouteTable() {
		// init center panel
		Object[] columnNames = { "Origin \u25BC / Destination \u25BA" };
		routeTableModel = new RouteTableModel(columnNames, 0);
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
		routeTable.getDefaultEditor(Integer.class).addCellEditorListener(this);
		routeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		routeTable.getTableHeader().setReorderingAllowed(false);

		JLabel routesLabel = new JLabel("Transport Travel Times:");

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(routesLabel, BorderLayout.NORTH);
		centerPanel.add(new JScrollPane(routeTable), BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);
	}

	public void addLocation(Location location) {
		Vector<Object> columnData = new Vector<Object>();
		locationToColumnMap.put(location, routeTableModel.getColumnCount());
		routeTableModel.addColumn(location.getName(), columnData);
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		locationToRowMap.put(location, routeTableModel.getRowCount());
		routeTableModel.addRow(rowData);
	}

	public void updateLocation(Location location) {
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

	public void removeLocation(Location location) {
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
	public void editingStopped(ChangeEvent e) {
		Object source = e.getSource();
		DefaultCellEditor s = (DefaultCellEditor) source;
		String newValue = null;
		if (s.getCellEditorValue() != null) {
			newValue = s.getCellEditorValue().toString();
		}
		int row = routeTable.getSelectedRow();
		int col = routeTable.getSelectedColumn();
		Location origin = getLocationFromRow(row);
		Location destination = getLocationFromColumn(col);

		if ((newValue != null) && (newValue.length() > 0)) {
			try {
				Integer travelTime = Integer.parseInt(newValue);
				controller.setTransportRoute(transport, origin, destination,
						travelTime);
			} catch (NumberFormatException exp) {
				return;
			}
		} else {
			controller.removeTransportRoute(transport, origin, destination);
		}
	}

	@Override
	public void editingCanceled(ChangeEvent e) {

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
