package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.util.PDDLWriterConstants;

public class LocationInformationPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;

	private Vector<Location> locations = null;

	private DefaultTableModel locationTableModel = null;
	private JTable locationTable = null;
	private JButton addLocation = null;

	private List<ActionListener> listeners = null;

	public LocationInformationPanel(Vector<Location> locations) {
		this.locations = locations;
		this.listeners = new ArrayList<ActionListener>();
		setLayout(new BorderLayout());
		initLocationPanel();
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	private void initLocationPanel() {
		Object[] columnNames = { "Name", "Capacity", "Initial Inventoy" };
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
		};
		locationTable.setRowHeight(20);
		locationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationTable.getTableHeader().setReorderingAllowed(false);

		addLocation = new JButton("Add Location");
		addLocation.addActionListener(this);

		add(new JScrollPane(locationTable), BorderLayout.CENTER);
		add(addLocation, BorderLayout.NORTH);
	}

	private void addLocation() {
		Location location = new Location(
				"L" + (Location.getNumLocations() + 1), 0, 0);
		locations.add(location);
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(location.getName());
		rowData.add(location.getCapacity());
		rowData.add(location.getInventory());
		locationTableModel.addRow(rowData);
		notifyListeners(location, PDDLWriterConstants.LOCATION_ADD_MESSAGE);
	}

	private void notifyListeners(final Location location, final String operation) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ActionEvent event = new ActionEvent(location,
						ActionEvent.ACTION_PERFORMED, operation);
				for (ActionListener listener : listeners) {
					listener.actionPerformed(event);
				}
			}

		});

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addLocation) {
			addLocation();
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			String newValue = locationTableModel.getValueAt(row, col).toString();
			Location location = locations.get(row);
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					location.setName(newValue);
					notifyListeners(location,
							PDDLWriterConstants.LOCATION_UPDATE_MESSAGE);
				} else {
					locationTableModel.setValueAt(location.getName(), row, col);
				}
				break;
			case 1:
				int cap = Integer.parseInt(newValue);
				location.setCapacity(cap);
				break;
			case 2:
				int inv = Integer.parseInt(newValue);
				location.setInventory(inv);
				break;
			}
		}
	}
}
