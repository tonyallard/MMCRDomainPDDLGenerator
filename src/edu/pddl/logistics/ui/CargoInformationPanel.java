package edu.pddl.logistics.ui;

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

import edu.pddl.logistics.controller.Constants;
import edu.pddl.logistics.controller.Controller;
import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;

public class CargoInformationPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;
	private static final int LOCATION_COLUMN_INDEX = 2;

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

		Object[] columnNames = { "Name", "Size", "Location", "Available At" };
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
				case 3:
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
		
		comboBoxModel = new DefaultComboBoxModel<Location>(new Vector<Location>(controller.getLocations()));

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn col = cargoTable.getColumnModel().getColumn(i);
			if (i == LOCATION_COLUMN_INDEX) {
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
		rowData.add(cargo.getInitialLocation());
		rowData.add(cargo.getAvailableIn());
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
			rowData.setElementAt(cargo.getInitialLocation(), 2);
			rowData.setElementAt(cargo.getAvailableIn(), 3);
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
			Location location = (Location)source;
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
			int col = e.getColumn();
			int row = e.getFirstRow();
			Object newObj = cargoTableModel.getValueAt(row, col);
			if (newObj == null) {
				return;
			}
			String newValue = newObj.toString();
			Cargo cargo = getCargo(row);
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					controller.updateCargoName(cargo, newValue);
				} else {
					cargoTableModel.setValueAt(cargo.getName(), row, col);
				}
				break;
			case 1:
				int size = Integer.parseInt(newValue);
				controller.updateCargoSize(cargo, size);
				break;
			case 2:
				Location location = controller.getLocationByName(newValue);
				if (location != null) {
					controller.setCargoInitialLocation(cargo, location);
				} else {
					cargoTableModel.setValueAt(cargo.getInitialLocation(), row,
							col);
				}
				break;
			case 3:
				int availableIn = Integer.parseInt(newValue);
				controller.setCargoAvailableIn(cargo, availableIn);
				break;
			}
		}
	}
}
