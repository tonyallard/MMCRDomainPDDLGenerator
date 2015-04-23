package edu.pddl.mmcr.ui;

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

import edu.pddl.mmcr.controller.Constants;
import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.Transport;

/**
 * This class is the root Transport UI Class
 * @author tony
 *
 */
public class TransportInformationPanel extends JPanel implements
		TableModelListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;
	private static final int LOCATION_COLUMN_INDEX = 1;

	private Transport transport = null;
	private Controller controller = null;

	private DefaultTableModel transportTableModel = null;
	private JTable transportTable = null;
	private DefaultComboBoxModel<Location> comboBoxModel = null;
	
	private TransportRoutePanel transportRoutePanel = null;
	private TransportLoadUnloadPanel transportLoadUnloadPanel = null;

	public TransportInformationPanel(Transport transport, Controller controller) {
		this.transport = transport;
		this.controller = controller;
		this.controller.addActionListener(this);
		setLayout(new BorderLayout());
		initTransportPanel();
		initOtherTransportPanels();
	}

	private void initTransportPanel() {
		Object[] columnNames = { "Name", "Initial Location",
				"Remaining Capacity", "Available At" };
		Object[][] data = { { transport.getName(),
				transport.getInitialLocation(),
				transport.getRemainingCapacity(), transport.getAvailableIn() } };
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
				case 1:
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
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(transportTable.getTableHeader(), BorderLayout.NORTH);
		northPanel.add(transportTable, BorderLayout.CENTER);
		add(northPanel, BorderLayout.NORTH);
	}

	private void initOtherTransportPanels() {
		JPanel centerPanel = new JPanel(new BorderLayout());
		this.transportRoutePanel = new TransportRoutePanel(transport, controller);
		this.transportLoadUnloadPanel = new TransportLoadUnloadPanel(transport, controller);
		centerPanel.add(transportRoutePanel, BorderLayout.CENTER);
		centerPanel.add(transportLoadUnloadPanel, BorderLayout.EAST);
		add(centerPanel, BorderLayout.CENTER);
	}

	private void updateTransport(Transport transport) {
		@SuppressWarnings("unchecked")
		Vector<Object> rowData = (Vector<Object>) transportTableModel
				.getDataVector().get(0);
		rowData.setElementAt(transport.getName(), 0);
		rowData.setElementAt(transport.getInitialLocation(), 1);
		rowData.setElementAt(transport.getRemainingCapacity(), 2);
		rowData.setElementAt(transport.getAvailableIn(), 3);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			Object newObj = transportTableModel.getValueAt(0, col);
			if ((newObj == null) || (newObj.toString().length() <= 0)) {
				
			}
			switch (col) {
			case 0:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						controller.setTransportName(transport, newValue);
						break;
					}
				}
				throw new RuntimeException("Transport name cannot be null.");
			case 1:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Location location = controller.getLocationByName(newValue);
						if (location != null) {
							controller.setTransportInitialLocation(transport, location);
							break;
						}
					}
				}
				// If you get here there probably aren't any locations defined
				break;
			case 2:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						int cap = Integer.parseInt(newValue);
						if (cap < 0) {
							throw new RuntimeException("Transport capacity cannot be negative.");
						}
						controller.setTransportRemainingCapacity(transport, cap);
						break;
					}
				}
				throw new RuntimeException("Transport capacity cannot be null.");
			case 3:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						int availableIn = Integer.parseInt(newValue);
						if (availableIn < 0) {
							throw new RuntimeException("Transport available in cannot be negative.");
						}
						controller.setTransportAvailableIn(transport, availableIn);
						break;
					}
				}
				throw new RuntimeException("Transport available in cannot be null.");
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
