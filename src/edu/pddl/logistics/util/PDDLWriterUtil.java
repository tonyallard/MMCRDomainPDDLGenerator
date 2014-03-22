package edu.pddl.logistics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import edu.pddl.logistics.exception.PDDLModelIncompleteException;
import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class PDDLWriterUtil {
	private static final String DOMAIN_FILENAME = "LogisticsDomain.pddl";

	private PDDLWriterUtil() {
	}

	public static void writeProblem(File file, String problemName,
			List<Location> locations, List<Transport> transports,
			List<Cargo> cargos) throws IOException, PDDLModelIncompleteException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		// HEADER
		bw.write("(define (problem " + problemName + ")");
		bw.newLine();
		bw.write("\t(:domain logistics)");
		bw.newLine();
		// OBJECT DEFINITION
		bw.write("\t(:objects");
		bw.newLine();
		// define transports
		String transportNames = "";
		for (Transport tpt : transports) {
			transportNames += tpt.getName() + " ";
		}
		bw.write("\t\t" + transportNames + " - TRANSPORT");
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
			if (tpt.getInitialLocation() == null){
				bw.close();
				throw new PDDLModelIncompleteException("Transport " + tpt.getName() + " does not have an initial location.");
			}
			bw.write("\t\t(at " + tpt.getName() + " "
					+ tpt.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Initial cargo locations
		for (Cargo cargo : cargos) {
			if (cargo.getInitialLocation() == null){
				bw.close();
				throw new PDDLModelIncompleteException("Transport " + cargo.getName() + " does not have an initial location.");
			}
			bw.write("\t\t(at " + cargo.getName() + " "
					+ cargo.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Transports Ready for Loading
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
			bw.write("\t\t(= (remaining-capacity " + loc.getName() + ") "
					+ loc.getRemainingCapacity() + ")");
			bw.newLine();
		}
		// Transport Inventory
		for (Transport tpt : transports) {
			bw.write("\t\t(= (current-inventory " + tpt.getName() + ") "
					+ tpt.getCurrentInventory() + ")");
			bw.newLine();
		}
		// Location Inventory
		for (Location loc : locations) {
			bw.write("\t\t(= (current-inventory " + loc.getName() + ") "
					+ loc.getCurrentInventory() + ")");
			bw.newLine();
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
			bw.write("\t\t(= (load-time " + tpt.getName() + ") "
					+ tpt.getLoadTime() + ")");
			bw.newLine();
		}
		// Transport Unload Times
		for (Transport tpt : transports) {
			bw.write("\t\t(= (unload-time " + tpt.getName() + ") "
					+ tpt.getUnloadTime() + ")");
			bw.newLine();
		}
		// Transport Available Times
		for (Transport tpt : transports) {
			bw.write("\t\t(= (available-in " + tpt.getName() + ") "
					+ tpt.getAvailableIn() + ")");
			bw.newLine();
		}
		// Cargo Available Times
		for (Cargo cargo : cargos) {
			bw.write("\t\t(= (available-in " + cargo.getName() + ") "
					+ cargo.getAvailableIn() + ")");
			bw.newLine();
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
		//Read in Domain File
		InputStream is = PDDLWriterUtil.class.getResourceAsStream(DOMAIN_FILENAME);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		boolean firstLine = true;
		while (line != null){
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
