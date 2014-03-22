package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import edu.pddl.logistics.controller.Constants;
import edu.pddl.logistics.controller.Controller;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class TransportInformationPanel extends JPanel implements
		TableModelListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;
	private static final int LOCATION_COLUMN_INDEX = 5;

	private Transport transport = null;
	private Controller controller = null;

	private DefaultTableModel transportTableModel = null;
	private JTable transportTable = null;
	private DefaultComboBoxModel<Location> comboBoxModel = null;

	public TransportInformationPanel(Transport transport, Controller controller) {
		this.transport = transport;
		this.controller = controller;
		this.controller.addActionListener(this);
		setLayout(new BorderLayout());
		initTransportPanel();
	}

	private void initTransportPanel() {
		Object[] columnNames = { "Name", "Remaining Capacity",
				"Current Inventoy", "Load Time", "Unload Time",
				"Initial Location", "Available At" };
		Object[][] data = { { transport.getName(),
				transport.getRemainingCapacity(),
				transport.getCurrentInventory(), transport.getLoadTime(),
				transport.getUnloadTime(), transport.getInitialLocation(),
				transport.getAvailableIn() } };
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
				case 5:
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
		transportTable.setRowHeight(25);
		transportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transportTable.getTableHeader().setReorderingAllowed(false);

		comboBoxModel = new DefaultComboBoxModel<Location>(
				new Vector<Location>(controller.getLocations()));

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn col = transportTable.getColumnModel().getColumn(i);
			if (i == LOCATION_COLUMN_INDEX) {
				JComboBox<Location> locCombo = new JComboBox<Location>(
						comboBoxModel);
				col.setCellEditor(new DefaultCellEditor(locCombo));
			}
		}

		add(transportTable.getTableHeader(), BorderLayout.NORTH);
		add(transportTable, BorderLayout.CENTER);
	}

	private void updateTransport(Transport transport) {
		@SuppressWarnings("unchecked")
		Vector<Object> rowData = (Vector<Object>) transportTableModel
				.getDataVector().get(0);
		rowData.setElementAt(transport.getName(), 0);
		rowData.setElementAt(transport.getRemainingCapacity(), 1);
		rowData.setElementAt(transport.getCurrentInventory(), 2);
		rowData.setElementAt(transport.getLoadTime(), 3);
		rowData.setElementAt(transport.getUnloadTime(), 4);
		rowData.setElementAt(transport.getInitialLocation(), 5);
		rowData.setElementAt(transport.getAvailableIn(), 6);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			Object newObj = transportTableModel.getValueAt(0, col);
			if (newObj == null) {
				return;
			}
			String newValue = newObj.toString();
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					controller.setTransportName(transport, newValue);
				} else {
					transportTableModel.setValueAt(transport.getName(), 0, col);
				}
				break;
			case 1:
				int cap = Integer.parseInt(newValue);
				controller.setTransportRemainingCapacity(transport, cap);
				break;
			case 2:
				int inv = Integer.parseInt(newValue);
				controller.setTransportCurrentInventory(transport, inv);
				break;
			case 3:
				int load = Integer.parseInt(newValue);
				controller.setTransportLoadTime(transport, load);
				break;
			case 4:
				int unload = Integer.parseInt(newValue);
				controller.setTransportUnloadTime(transport, unload);
				break;
			case 5:
				Location location = controller.getLocationByName(newValue);
				if (location != null) {
					controller.setTransportInitialLocation(transport, location);
				} else {
					transportTableModel.setValueAt(
							transport.getInitialLocation(), 0, col);
				}
				break;
			case 6:
				int availableIn = Integer.parseInt(newValue);
				controller.setTransportAvailableIn(transport, availableIn);
				break;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof Transport) {
			Transport transport = (Transport) source;
			if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				if (this.transport == transport) {
					updateTransport(transport);
				}
			}
		} else if (source instanceof Location) {
			Location location = (Location) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				if (comboBoxModel.getIndexOf(location) == -1) {
					comboBoxModel.addElement(location);
				}
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				comboBoxModel.removeElement(location);
			}
		}
	}
}
