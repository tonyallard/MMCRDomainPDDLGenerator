package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.table.DefaultTableModel;

import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class TransportAndRouteInformationPanel extends JPanel implements
		ActionListener, CellEditorListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6952549289264640594L;

	private Transport transport = null;
	private Vector<Location> locations = null;

	private JTable routeTable = null;
	private TransportInformationPanel tPanel = null;
	private DefaultTableModel routeTableModel = null;
	
	private List<ActionListener> listeners = null;

	public TransportAndRouteInformationPanel(Transport transport,
			Vector<Location> locations) {
		this.transport = transport;
		this.locations = locations;
		this.listeners = new ArrayList<ActionListener>();
		setLayout(new BorderLayout());
		initRouteTable();
		initTransportDataPanel();
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	private void initTransportDataPanel() {
		tPanel = new TransportInformationPanel(transport, locations);
		tPanel.addActionListener(this);
		add(tPanel, BorderLayout.NORTH);

	}

	private void initRouteTable() {
		// init center panel
		Object[] columnNames = { "Locations" };
		routeTableModel = new DefaultTableModel(columnNames, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				} else if (row + 1 == column) {
					return false;
				}
				return true;
			}
		};
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
		};
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
		routeTableModel.addColumn(location.getName(), columnData);
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		routeTableModel.addRow(rowData);
	}

	public void updateLocation(Location loc) {
		int idx = locations.indexOf(loc);
		// update row
		routeTable.setValueAt(loc.getName(), idx, 0);
		// update column
		routeTable.getTableHeader().getColumnModel().getColumn(idx + 1)
				.setHeaderValue(loc.getName());
		//Update tpt information
		tPanel.updateLocation(loc);
		repaint();
	}
	
	private void notifyListeners(final ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (ActionListener listener : listeners) {
					listener.actionPerformed(e);
				}
			}

		});
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
		Location origin = locations.get(row);
		Location destination = locations.get(col - 1);

		if ((newValue != null) && (newValue.length() > 0)) {
			try {
				Integer travelTime = Integer.parseInt(newValue);
				transport.updateRoute(origin, destination, travelTime);
			} catch (NumberFormatException exp) {
				return;
			}
		} else {
			transport.removeRoute(origin, destination);
		}
	}

	@Override
	public void editingCanceled(ChangeEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof Transport) {
			notifyListeners(e);
		}
	}
}
