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
import javax.swing.JSplitPane;
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
import edu.pddl.mmcr.model.Vehicle;

/**
 * This class is the root Vehicle UI Class
 * @author tony
 *
 */
public class VehicleInformationPanel extends JPanel implements
		TableModelListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;
	private static final int LOCATION_COLUMN_INDEX = 1;

	private Vehicle vehicle = null;
	private Controller controller = null;

	private DefaultTableModel vehicleTableModel = null;
	private JTable vehicleTable = null;
	private DefaultComboBoxModel<Location> comboBoxModel = null;
	
	private VehicleRoutePanel vehicleRoutePanel = null;
	private VehicleLoadUnloadPanel vehicleLoadUnloadPanel = null;

	public VehicleInformationPanel(Vehicle vehicle, Controller controller) {
		this.vehicle = vehicle;
		this.controller = controller;
		this.controller.addActionListener(this);
		setLayout(new BorderLayout());
		initVehiclePanel();
		initOtherVehiclePanels();
	}

	private void initVehiclePanel() {
		Object[] columnNames = { "Name", "Initial Location",
				"Remaining Capacity", "Available At" };
		Object[][] data = { { vehicle.getName(),
				vehicle.getInitialLocation(),
				vehicle.getRemainingCapacity(), vehicle.getAvailableIn() } };
		vehicleTableModel = new DefaultTableModel(data, columnNames);
		vehicleTableModel.addTableModelListener(this);
		vehicleTable = new JTable(vehicleTableModel) {

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
		vehicleTable.setRowHeight(25);
		vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vehicleTable.getTableHeader().setReorderingAllowed(false);

		comboBoxModel = new DefaultComboBoxModel<Location>(
				new Vector<Location>(controller.getLocations()));

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn col = vehicleTable.getColumnModel().getColumn(i);
			if (i == LOCATION_COLUMN_INDEX) {
				JComboBox<Location> locCombo = new JComboBox<Location>(
						comboBoxModel);
				col.setCellEditor(new DefaultCellEditor(locCombo));
			}
		}
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(vehicleTable.getTableHeader(), BorderLayout.NORTH);
		northPanel.add(vehicleTable, BorderLayout.CENTER);
		add(northPanel, BorderLayout.NORTH);
	}

	private void initOtherVehiclePanels() {
		this.vehicleRoutePanel = new VehicleRoutePanel(vehicle, controller);
		this.vehicleLoadUnloadPanel = new VehicleLoadUnloadPanel(vehicle, controller);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, vehicleRoutePanel, vehicleLoadUnloadPanel);

		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(750);//one third
		add(splitPane, BorderLayout.CENTER);
	}

	private void updateVehicle(Vehicle vehicle) {
		@SuppressWarnings("unchecked")
		Vector<Object> rowData = (Vector<Object>) vehicleTableModel
				.getDataVector().get(0);
		rowData.setElementAt(vehicle.getName(), 0);
		rowData.setElementAt(vehicle.getInitialLocation(), 1);
		rowData.setElementAt(vehicle.getRemainingCapacity(), 2);
		rowData.setElementAt(vehicle.getAvailableIn(), 3);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			Object newObj = vehicleTableModel.getValueAt(0, col);
			if ((newObj == null) || (newObj.toString().length() <= 0)) {
				
			}
			switch (col) {
			case 0:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						controller.setVehicleName(vehicle, newValue);
						break;
					}
				}
				throw new RuntimeException("Vehicle name cannot be null.");
			case 1:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Location location = controller.getLocationByName(newValue);
						if (location != null) {
							controller.setVehicleInitialLocation(vehicle, location);
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
							throw new RuntimeException("Vehicle capacity cannot be negative.");
						}
						controller.setVehicleRemainingCapacity(vehicle, cap);
						break;
					}
				}
				throw new RuntimeException("Vehicle capacity cannot be null.");
			case 3:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						int availableIn = Integer.parseInt(newValue);
						if (availableIn < 0) {
							throw new RuntimeException("Vehicle available in cannot be negative.");
						}
						controller.setVehicleAvailableIn(vehicle, availableIn);
						break;
					}
				}
				throw new RuntimeException("Vehicle available in cannot be null.");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof Vehicle) {
			Vehicle vehicle = (Vehicle) source;
			if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				if (this.vehicle == vehicle) {
					updateVehicle(vehicle);
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
