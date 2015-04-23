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
import edu.pddl.mmcr.model.Transport;

public class PDDLWriterUtil {
	private static final String DOMAIN_FILENAME = "MMCR.pddl";

	private PDDLWriterUtil() {
	}

	public static void writeProblem(File file, String problemName,
			List<Location> locations, List<Transport> transports,
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
		// define transports
		String transportNames = "";
		for (Transport tpt : transports) {
			transportNames += tpt.getName() + " ";
		}
		bw.write("\t\t" + transportNames + " - VEHICLE");
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
		// Initial transport locations
		for (Transport tpt : transports) {
			if (tpt.getInitialLocation() == null) {
				bw.close();
				throw new PDDLModelIncompleteException("Vehicle "
						+ tpt.getName() + " does not have an initial location.");
			}
			bw.write("\t\t(at " + tpt.getName() + " "
					+ tpt.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Initial cargo locations
		for (Cargo cargo : cargos) {
			if (cargo.getInitialLocation() == null) {
				bw.close();
				throw new PDDLModelIncompleteException("Transport "
						+ cargo.getName()
						+ " does not have an initial location.");
			}
			bw.write("\t\t(at " + cargo.getName() + " "
					+ cargo.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Transport Ready for Loading
		for (Transport tpt : transports) {
			bw.write("\t\t(ready-loading " + tpt.getName() + ")");
			bw.newLine();
		}
		// Transport Capacity
		for (Transport tpt : transports) {
			bw.write("\t\t(= (remaining-capacity " + tpt.getName() + ") "
					+ tpt.getRemainingCapacity() + ")");
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
		// Transport Routes
		for (Transport tpt : transports) {
			Map<Location, Map<Location, Integer>> routes = tpt.getRoutes();
			for (Location origin : routes.keySet()) {
				Map<Location, Integer> destinationDistanceMap = routes
						.get(origin);
				for (Location destination : destinationDistanceMap.keySet()) {
					Integer distance = destinationDistanceMap.get(destination);
					bw.write("\t\t(= (travel-time " + tpt.getName() + " "
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
		// Transport Load Times
		for (Transport tpt : transports) {
			for (Location loc : tpt.getLoadingTimes().keySet()) {
				Integer loadingTime = tpt.getLoadingTime(loc);
				bw.write("\t\t(= (load-time " + tpt.getName() + " "
						+ loc.getName() + ") " + loadingTime + ")");
				bw.newLine();
			}
		}
		// Transport Unload Times
		for (Transport tpt : transports) {
			for (Location loc : tpt.getUnloadingTimes().keySet()) {
				Integer unloadingTime = tpt.getUnloadingTime(loc);
				bw.write("\t\t(= (unload-time " + tpt.getName() + " "
						+ loc.getName() + ") " + unloadingTime + ")");
				bw.newLine();
			}
		}
		// Transport Available Times
		for (Transport tpt : transports) {
			// If no timed availability omit TIL
			if (tpt.getAvailableIn() == 0) {
				bw.write("\t\t(available " + tpt.getName() + ")");
				bw.newLine();
			} else { // otherwise add it
				bw.write("\t\t(not (available " + tpt.getName() + "))");
				bw.newLine();
				bw.write("\t\t(at " + tpt.getAvailableIn() + " (available "
						+ tpt.getName() + "))");
				bw.newLine();
			}
		}

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
		bw.write("\t(:goal (and ))");
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
