package edu.pddl.mmcr.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.pddl.mmcr.controller.Constants;
import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.model.Vehicle;

public class VehicleManagementPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297726541770260245L;

	private JButton addVehicle = null;
	private JButton removeVehicle = null;
	private JTabbedPane tabbedPane = null;

	private Controller controller = null;
	private Map<Vehicle, Integer> vehicleToTabIndexMap = null;

	public VehicleManagementPanel(Controller controller) {
		this.controller = controller;
		this.controller.addActionListener(this);
		this.vehicleToTabIndexMap = new IdentityHashMap<Vehicle, Integer>();

		setLayout(new BorderLayout());
		initVehiclePanel();
	}

	private void initVehiclePanel() {
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		addVehicle = new JButton("Add Vehicle");
		addVehicle.addActionListener(this);

		removeVehicle = new JButton("Remove Vehicle");
		removeVehicle.addActionListener(this);

		JLabel vehicleInfoLabel = new JLabel("Vehicle Information:");
		add(vehicleInfoLabel, BorderLayout.NORTH);

		JPanel cmdPanel = new JPanel(new FlowLayout());
		cmdPanel.add(addVehicle);
		cmdPanel.add(removeVehicle);
		add(cmdPanel, BorderLayout.SOUTH);
	}

	private void addVehicle() {
		controller.addNewVehicle();
	}

	private void addVehicle(Vehicle vehicle) {
		VehicleInformationPanel vehPanel = new VehicleInformationPanel(
				vehicle, controller);
		vehicleToTabIndexMap.put(vehicle, tabbedPane.getTabCount());
		tabbedPane.addTab(vehicle.getName(), vehPanel);
	}

	private void removeVehicle() {
		int index = tabbedPane.getSelectedIndex();
		Vehicle vehicle = getVehicle(index);
		if (vehicle != null) {
			controller.removeVehicle(vehicle);
		}
	}

	private void removeVehicle(Vehicle vehicle) {
		int index = vehicleToTabIndexMap.get(vehicle);
		tabbedPane.remove(index);
	}

	private void updateVehicle(Vehicle vehicle) {
		Integer idx = vehicleToTabIndexMap.get(vehicle);
		if (idx != null) {
			tabbedPane.setTitleAt(idx, vehicle.getName());
		}
	}

	private Vehicle getVehicle(int index) {
		for (Vehicle vehicle : vehicleToTabIndexMap.keySet()) {
			if (index == vehicleToTabIndexMap.get(vehicle)) {
				return vehicle;
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addVehicle) {
			addVehicle();
		} else if (source == removeVehicle) {
			removeVehicle();
		} else if (source instanceof Vehicle) {
			Vehicle vehicle = (Vehicle) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				addVehicle(vehicle);
			} else if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				updateVehicle(vehicle);
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				removeVehicle(vehicle);
			}
		}
	}
}
