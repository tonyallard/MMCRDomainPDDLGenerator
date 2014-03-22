package edu.pddl.logistics.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TransportLocationTableCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7106148298377355762L;

	public TransportLocationTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
      }
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setBackground(Color.WHITE);
		if (row + 1 == column) {
			c.setBackground(Color.GRAY);
		}
		c.setForeground(Color.BLACK);
        return c;
	}

}
