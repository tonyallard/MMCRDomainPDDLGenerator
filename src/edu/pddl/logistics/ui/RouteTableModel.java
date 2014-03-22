package edu.pddl.logistics.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class RouteTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2607190178489168183L;

	public RouteTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			return false;
		} else if (row + 1 == column) {
			return false;
		}
		return true;
	}
	
	public void removeColumn(int columnIndex) {
		for (Object data : dataVector) {
			@SuppressWarnings("rawtypes")
			Vector dataRow = (Vector)data;
			dataRow.removeElementAt(columnIndex);
		}
		columnIdentifiers.removeElementAt(columnIndex);
		this.fireTableStructureChanged();
	}

}
