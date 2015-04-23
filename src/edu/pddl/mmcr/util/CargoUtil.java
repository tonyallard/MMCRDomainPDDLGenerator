package edu.pddl.mmcr.util;

import edu.pddl.mmcr.model.Cargo;

public class CargoUtil {

	private CargoUtil() {
		
	}
	
	public static Cargo getCargoByName(String name, Iterable<Cargo> cargos) {
		for (Cargo cargo : cargos) {
			if (cargo.getName().equals(name)) {
				return cargo;
			}
		}
		return null;
	}
	
}
