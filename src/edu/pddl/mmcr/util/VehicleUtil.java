package edu.pddl.mmcr.util;

import edu.pddl.mmcr.model.Vehicle;

public class VehicleUtil {

	private VehicleUtil() {
		
	}
	
	public static Vehicle getVehicleByName(String name, Iterable<Vehicle> vehicles) {
		for (Vehicle vehicle : vehicles) {
			if (vehicle.getName().equals(name)){
				return vehicle;
			}
		}
		return null;
	}
}
