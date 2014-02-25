package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;

public class CargoInformationPanel extends JPanel implements ActionListener,
		TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8878364677692218340L;

	private Vector<Location> locations = null;
	private List<Cargo> cargos = null;

	private DefaultTableModel cargoTableModel = null;
	private JTable cargoTable = null;
	private JButton addCargo = null;

	public CargoInformationPanel(Vector<Location> locations, List<Cargo> cargos) {
		this.locations = locations;
		this.cargos = cargos;
		setLayout(new BorderLayout());
		initCargoPanel();
	}

	private void initCargoPanel() {

		Object[] columnNames = { "Name", "Size", "Location" };
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
				default:
					return String.class;
				}
			}
		};
		cargoTable.setRowHeight(20);
		cargoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cargoTable.getTableHeader().setReorderingAllowed(false);

		TableColumn col = cargoTable.getColumnModel().getColumn(2);
		JComboBox<Location> locCombo = new JComboBox<Location>(locations);
		col.setCellEditor(new DefaultCellEditor(locCombo));

		addCargo = new JButton("Add Cargo");
		addCargo.addActionListener(this);

		add(new JScrollPane(cargoTable), BorderLayout.CENTER);
		add(addCargo, BorderLayout.NORTH);
	}

	private Location getLocation(String newVal) {
		for (Location loc : locations) {
			if (loc.getName().equals(newVal)) {
				return loc;
			}
		}
		return null;
	}

	private void addCargo() {
		Cargo cargo = new Cargo("C" + (Cargo.getNumCargo() + 1), 0);
		cargos.add(cargo);
		Vector<Object> rowData = new Vector<Object>();
		rowData.add(cargo.getName());
		rowData.add(cargo.getSize());
		cargoTableModel.addRow(rowData);
	}

	public void updateLocation(Location loc) {
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addCargo) {
			addCargo();
		}

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int col = e.getColumn();
			int row = e.getFirstRow();
			String newValue = cargoTableModel.getValueAt(row, col).toString();
			Cargo cargo = cargos.get(row);
			switch (col) {
			case 0:
				if ((newValue != null) && (newValue.length() > 0)) {
					cargo.setName(newValue);
				} else {
					cargoTableModel.setValueAt(cargo.getName(), row, col);
				}
				break;
			case 1:
				int size = Integer.parseInt(newValue);
				cargo.setSize(size);
				break;
			case 2:
				Location loc = getLocation(newValue);
				if (loc != null) {
					cargo.setInitialLocation(loc);
				} else {
					cargoTableModel.setValueAt(cargo.getInitialLocation(), row,
							col);
				}
				break;
			}
		}
	}
}
