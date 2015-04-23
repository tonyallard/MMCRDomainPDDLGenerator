package edu.pddl.mmcr.util;

import edu.pddl.mmcr.model.Vehicle;

public class VehicleUtil {

	private VehicleUtil() {
		
	}
	
	public static Vehicle getVehicleByName(String name, Iterable<Vehicle> transports) {
		for (Vehicle transport : transports) {
			if (transport.getName().equals(name)){
				return transport;
			}
		}
		return null;
	}
}
