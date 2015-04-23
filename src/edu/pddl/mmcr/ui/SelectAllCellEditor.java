package edu.pddl.mmcr.ui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class SelectAllCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7305489490503804996L;

	public SelectAllCellEditor(final JTextField textField) {
		super(textField);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		Component c = super.getTableCellEditorComponent(table, value,
				isSelected, row, column);
		final JTextComponent jtc = (JTextComponent) c;
		jtc.requestFocus();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jtc.selectAll();
			}
		});
		return c;

	}
}
