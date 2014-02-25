package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;
import edu.pddl.logistics.util.PDDLWriterConstants;

public class TransportInformationPanel extends JPanel implements
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;

	private Transport transport = null;
	private Vector<Location> locations = null;

	private DefaultTableModel transportTableModel = null;
	private JTable transportTable = null;

	private List<ActionListener> listeners = null;

	public TransportInformationPanel(Transport transport,
			Vector<Location> locations) {
		this.transport = transport;
		this.locations = locations;
		this.listeners = new ArrayList<ActionListener>();
		setLayout(new BorderLayout());
		initTransportPanel();
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	private void initTransportPanel() {
		Object[] columnNames = { "Name", "Capacity", "Initial Inventoy",
				"Initial Location" };
		Object[][] data = { { transport.getName(), transport.getCapacity(),
				transport.getInventory() } };
		transportTableModel = new DefaultTableModel(data, columnNames);
		transportTableModel.addTableModelListener(this);
		transportTable = new JTable(transportTableModel) {

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
				case 3:
					return String.class;
				default:
					return Integer.class;
				}
			}
		};
		transportTable.setRowHeight(20);
		transportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transportTable.getTableHeader().setReorderingAllowed(false);

		TableColumn col = transportTable.getColumnModel().getColumn(3);
		JComboBox<Location> locCombo = new JComboBox<Location>(locations);
		col.setCellEditor(new DefaultCellEditor(locCombo));

		add(transportTable.getTableHeader(), BorderLayout.NORTH);
		add(transportTable, BorderLayout.CENTER);
	}

	public void updateLocation(Location loc) {
		repaint();
	}

	private Location getLocation(String newVal) {
		for (Location loc : locations) {
			if (loc.getName().equals(newVal)) {
				return loc;
			}
		}
		return null;
	}

	private void notifyListeners(final Transport transport,
			final String operation) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ActionEvent event = new ActionEvent(transport,
						ActionEvent.ACTION_PERFORMED, operation);
				for (ActionListener listener : listeners) {
					listener.actionPerformed(event);
				}
			}

		});
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			String newValue = transportTableModel.getValueAt(0, col).toString();
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					transport.setName(newValue);
					notifyListeners(transport,
							PDDLWriterConstants.LOCATION_UPDATE_MESSAGE);
				} else {
					transportTableModel.setValueAt(transport.getName(), 0, col);
				}
				break;
			case 1:
				int cap = Integer.parseInt(newValue);
				transport.setCapacity(cap);
				break;
			case 2:
				int inv = Integer.parseInt(newValue);
				transport.setInventory(inv);
				break;
			case 3:
				Location loc = getLocation(newValue);
				if (loc != null) {
					transport.setInitialLocation(loc);
				} else {
					transportTableModel.setValueAt(
							transport.getInitialLocation(), 0, col);
				}
				break;
			}
		}
	}
}
