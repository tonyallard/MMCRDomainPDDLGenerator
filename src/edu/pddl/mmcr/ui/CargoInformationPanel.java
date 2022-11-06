package edu.pddl.mmcr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import edu.pddl.mmcr.controller.Constants;
import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.model.Cargo;
import edu.pddl.mmcr.model.Location;

public class CargoInformationPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;
	private static final int PICKUP_LOCATION_COLUMN_INDEX = 2;
	private static final int DELIVERY_LOCATION_COLUMN_INDEX = 3;

	private Controller controller = null;

	private DefaultTableModel cargoTableModel = null;
	private JTable cargoTable = null;
	private JButton addCargo = null;
	private JButton removeCargo = null;
	private DefaultComboBoxModel<Location> comboBoxModel = null;

	private Map<Cargo, Integer> cargoToRowMap = null;

	public CargoInformationPanel(Controller controller) {
		this.cargoToRowMap = new IdentityHashMap<Cargo, Integer>();
		this.controller = controller;
		this.controller.addActionListener(this);
		setLayout(new BorderLayout());
		initCargoPanel();
	}

	private void initCargoPanel() {

		Object[] columnNames = { "Name", "Size", "Pickup Location", "Delivery Location", "Available At",
				"Required By" };
		cargoTableModel = new DefaultTableModel(columnNames, 0);
		cargoTableModel.addTableModelListener(this);
		cargoTable = new JTable(cargoTableModel) {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 1:
					return Integer.class;
				case 4:
					return Integer.class;
				case 5:
					return Integer.class;
				default:
					return String.class;
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
		cargoTable.setRowHeight(25);
		cargoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cargoTable.getTableHeader().setReorderingAllowed(false);

		comboBoxModel = new DefaultComboBoxModel<Location>(
				new Vector<Location>(controller.getLocations()));

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn col = cargoTable.getColumnModel().getColumn(i);
			if ((i == PICKUP_LOCATION_COLUMN_INDEX) || (i == DELIVERY_LOCATION_COLUMN_INDEX)) {
				JComboBox<Location> locCombo = new JComboBox<Location>(
						comboBoxModel);
				col.setCellEditor(new DefaultCellEditor(locCombo));
			}
		}

		addCargo = new JButton("Add Cargo");
		addCargo.addActionListener(this);

		removeCargo = new JButton("Remove Cargo");
		removeCargo.addActionListener(this);

		JPanel southPanel = new JPanel(new GridLayout(1, 2));
		southPanel.add(addCargo);
		southPanel.add(removeCargo);

		JLabel cargoInfoLabel = new JLabel("Cargo Information:");

		add(cargoInfoLabel, BorderLayout.NORTH);
		add(new JScrollPane(cargoTable), BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private void addCargo() {
		controller.addNewCargo();
	}

	private void addCargo(Cargo cargo) {
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(cargo.getName());
		rowData.add(cargo.getSize());
		rowData.add(cargo.getPickupLocation());
		rowData.add(cargo.getDeliveryLocation());
		rowData.add(cargo.getAvailableIn());
		rowData.add(cargo.getRequiredBy());
		cargoToRowMap.put(cargo, cargoTableModel.getRowCount());
		cargoTableModel.addRow(rowData);
	}

	private void removeCargo() {
		int row = cargoTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		Cargo cargo = getCargo(row);
		if (cargo != null) {
			controller.removeCargo(cargo);
		}
	}

	private void removeCargo(Cargo cargo) {
		Integer row = cargoToRowMap.remove(cargo);
		if (row != null) {
			cargoTableModel.removeRow(row);
		}
	}

	private void updateCargo(Cargo cargo) {
		Integer row = cargoToRowMap.get(cargo);
		if (row != null) {
			@SuppressWarnings("unchecked")
			Vector<Object> rowData = (Vector<Object>) cargoTableModel
					.getDataVector().get(row);
			rowData.setElementAt(cargo.getName(), 0);
			rowData.setElementAt(cargo.getSize(), 1);
			rowData.setElementAt(cargo.getPickupLocation(), 2);
			rowData.setElementAt(cargo.getDeliveryLocation(), 3);
			rowData.setElementAt(cargo.getAvailableIn(), 4);
			rowData.setElementAt(cargo.getRequiredBy(), 5);
		}
	}

	private Cargo getCargo(int row) {
		for (Cargo cargo : cargoToRowMap.keySet()) {
			if (row == cargoToRowMap.get(cargo)) {
				return cargo;
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addCargo) {
			addCargo();
		} else if (source == removeCargo) {
			removeCargo();
		} else if (source instanceof Cargo) {
			Cargo cargo = (Cargo) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				addCargo(cargo);
			} else if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				updateCargo(cargo);
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				removeCargo(cargo);
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

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			Object newObj = cargoTableModel.getValueAt(row, col);

			Cargo cargo = getCargo(row);
			switch (col) {
			case 0:
				// Cargo Name
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						controller.updateCargoName(cargo, newValue);
						break;
					}
				}
				throw new RuntimeException("Cargo name cannot be null.");
			case 1:
				// Cargo Size
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						int size = Integer.parseInt(newValue);
						if (size < 0) {
							throw new RuntimeException("Cargo size cannot be negative.");
						}
						controller.updateCargoSize(cargo, size);
						break;
					}
				}
				throw new RuntimeException("Cargo size cannot be null.");				
			case 2:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Location location = controller.getLocationByName(newValue);
						if (location != null) {
							controller.setCargoPickupLocation(cargo, location);
							break;
						}
					}
				}
				// If you get here there probably aren't any locations defined
				break;
			case 3:
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						Location location = controller.getLocationByName(newValue);
						if (location != null) {
							controller.setCargoDeliveryLocation(cargo, location);
							break;
						}
					}
				}
				// If you get here there probably aren't any locations defined
				break;
			case 4:
				// Cargo available in
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						double availableIn = Double.parseDouble(newValue);
						if (availableIn < 0) {
							throw new RuntimeException("Cargo available in cannot be negative.");
						}
						controller.setCargoAvailableIn(cargo, availableIn);
						break;
					}
				}
				throw new RuntimeException("Cargo available in cannot be null.");	
			case 5:
				// Cargo required by
				if (newObj != null) {
					String newValue = newObj.toString();
					if (newValue.length() > 0) {
						double requiredBy = Double.parseDouble(newValue);
						if (requiredBy < 0) {
							throw new RuntimeException("Cargo required by cannot be negative.");
						}
						controller.setCargoRequiredBy(cargo, requiredBy);
						break;
					}
				}
				controller.removeCargoRequiredBy(cargo);
				break;
			}
		}
	}
}
