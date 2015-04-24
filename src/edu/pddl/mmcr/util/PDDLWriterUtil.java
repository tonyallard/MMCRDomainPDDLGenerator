package edu.pddl.mmcr.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import edu.pddl.mmcr.exception.PDDLModelIncompleteException;
import edu.pddl.mmcr.model.Cargo;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.Vehicle;

public class PDDLWriterUtil {
	private static final String DOMAIN_FILENAME = "MMCR.pddl";

	private PDDLWriterUtil() {
	}

	public static void writeProblem(File file, String problemName,
			List<Location> locations, List<Vehicle> vehicles,
			List<Cargo> cargos) throws IOException,
			PDDLModelIncompleteException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
		// HEADER
		bw.write("(define (problem " + problemName + ")");
		bw.newLine();
		bw.write("\t(:domain multi-modal-cargo-routing)");
		bw.newLine();
		// OBJECT DEFINITION
		bw.write("\t(:objects");
		bw.newLine();
		// define vehciles
		String vehicleNames = "";
		for (Vehicle veh : vehicles) {
			vehicleNames += veh.getName() + " ";
		}
		bw.write("\t\t" + vehicleNames + " - VEHICLE");
		bw.newLine();
		// define locations
		String locationNames = "";
		for (Location loc : locations) {
			locationNames += loc.getName() + " ";
		}
		bw.write("\t\t" + locationNames + " - LOCATION");
		bw.newLine();
		// define cargo
		String cargoNames = "";
		for (Cargo cargo : cargos) {
			cargoNames += cargo.getName() + " ";
		}
		bw.write("\t\t" + cargoNames + " - CARGO)");
		bw.newLine();

		// INITIAL STATE
		bw.write("\t(:init");
		bw.newLine();
		// Initial vehicle locations
		for (Vehicle veh : vehicles) {
			if (veh.getInitialLocation() == null) {
				bw.close();
				throw new PDDLModelIncompleteException("Vehicle "
						+ veh.getName() + " does not have an initial location.");
			}
			bw.write("\t\t(at " + veh.getName() + " "
					+ veh.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Initial cargo locations
		for (Cargo cargo : cargos) {
			if (cargo.getPickupLocation() == null) {
				bw.close();
				throw new PDDLModelIncompleteException("Cargo "
						+ cargo.getName()
						+ " does not have a pickup location.");
			}
			bw.write("\t\t(at " + cargo.getName() + " "
					+ cargo.getPickupLocation().getName() + ")");
			bw.newLine();
		}
		// Vehicle Ready for Loading
		for (Vehicle veh : vehicles) {
			bw.write("\t\t(ready-loading " + veh.getName() + ")");
			bw.newLine();
		}
		// Vehicle Capacity
		for (Vehicle veh : vehicles) {
			bw.write("\t\t(= (remaining-capacity " + veh.getName() + ") "
					+ veh.getRemainingCapacity() + ")");
			bw.newLine();
		}
		// Location Capacity
		for (Location loc : locations) {
			// If null then location has no capacity - omit from model
			if (loc.getRemainingCapacity() != null) {
				bw.write("\t\t(= (remaining-capacity " + loc.getName() + ") "
						+ loc.getRemainingCapacity() + ")");
				bw.newLine();
			}
		}
		// Vehicle Routes
		for (Vehicle veh : vehicles) {
			Map<Location, Map<Location, Integer>> routes = veh.getRoutes();
			for (Location origin : routes.keySet()) {
				Map<Location, Integer> destinationDistanceMap = routes
						.get(origin);
				for (Location destination : destinationDistanceMap.keySet()) {
					Integer distance = destinationDistanceMap.get(destination);
					bw.write("\t\t(= (travel-time " + veh.getName() + " "
							+ origin.getName() + " " + destination.getName()
							+ ") " + distance + ")");
					bw.newLine();
				}
			}
		}
		// Cargo Size
		for (Cargo cargo : cargos) {
			bw.write("\t\t(= (size " + cargo.getName() + ") " + cargo.getSize()
					+ ")");
			bw.newLine();
		}
		// Vehicle Load Times
		for (Vehicle veh : vehicles) {
			for (Location loc : veh.getLoadingTimes().keySet()) {
				Integer loadingTime = veh.getLoadingTime(loc);
				bw.write("\t\t(= (load-time " + veh.getName() + " "
						+ loc.getName() + ") " + loadingTime + ")");
				bw.newLine();
			}
		}
		// Vehicle Unload Times
		for (Vehicle veh : vehicles) {
			for (Location loc : veh.getUnloadingTimes().keySet()) {
				Integer unloadingTime = veh.getUnloadingTime(loc);
				bw.write("\t\t(= (unload-time " + veh.getName() + " "
						+ loc.getName() + ") " + unloadingTime + ")");
				bw.newLine();
			}
		}
		// Vehicle Available Times
		for (Vehicle veh : vehicles) {
			// If no timed availability omit TIL
			if (veh.getAvailableIn() == 0) {
				bw.write("\t\t(available " + veh.getName() + ")");
				bw.newLine();
			} else { // otherwise add it
				bw.write("\t\t(not (available " + veh.getName() + "))");
				bw.newLine();
				bw.write("\t\t(at " + veh.getAvailableIn() + " (available "
						+ veh.getName() + "))");
				bw.newLine();
			}
		}
		//Cargo Available Times
		for (Cargo cargo : cargos) {
			// Cargo Available In Times
			// If no timed availability omit TIL
			if (cargo.getAvailableIn() == 0) {
				bw.write("\t\t(available " + cargo.getName() + ")");
				bw.newLine();
			} else { // otherwise add it
				bw.write("\t\t(not (available " + cargo.getName() + "))");
				bw.newLine();
				bw.write("\t\t(at " + cargo.getAvailableIn() + " (available "
						+ cargo.getName() + "))");
				bw.newLine();
			}
			// Add required by if ... required
			if (cargo.getRequiredBy() != null) {
				bw.write("\t\t(at " + cargo.getRequiredBy()
						+ " (not (available " + cargo.getName() + ")))");
				bw.newLine();
			}
		}
		bw.write("\t)");
		bw.newLine();
		//Cargo Deliver Goals
		bw.write("\t(:goal");
		bw.newLine();
		bw.write("\t\t(and");
		bw.newLine();
		for (Cargo cargo : cargos) {
			if (cargo.getDeliveryLocation() == null) {
				bw.close();
				throw new PDDLModelIncompleteException("Cargo "
						+ cargo.getName()
						+ " does not have a delivery location.");
			}
			bw.write("\t\t\t(at " + cargo.getName() + " "
					+ cargo.getDeliveryLocation().getName() + ")");
			bw.newLine();
		}

		bw.write("\t\t)");
		bw.newLine();
		bw.write("\t)");
		bw.newLine();
		bw.write("\t(:metric minimize (total-cost))");
		bw.newLine();
		bw.write(")");
		bw.close();
	}

	public static void writeDomain(File file) throws IOException {
		// Write Domin File
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		// Read in Domain File
		InputStream is = PDDLWriterUtil.class
				.getResourceAsStream(DOMAIN_FILENAME);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		boolean firstLine = true;
		while (line != null) {
			if (!firstLine) {

				bw.newLine();
			}
			bw.write(line);
			line = br.readLine();
			firstLine = false;
		}
		bw.close();
	}
}
