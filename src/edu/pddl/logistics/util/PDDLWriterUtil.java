package edu.pddl.logistics.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class PDDLWriterUtil {

	private PDDLWriterUtil() {
	}

	public static void writeProblem(File file, String problemName,
			List<Location> locations, List<Transport> transports,
			List<Cargo> cargos) throws IOException {
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
			bw.write("\t\t(at-transport " + tpt.getName() + " "
					+ tpt.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Initial cargo locations
		for (Cargo cargo : cargos) {
			bw.write("\t\t(at-cargo " + cargo.getName() + " "
					+ cargo.getInitialLocation().getName() + ")");
			bw.newLine();
		}
		// Transport Capacity
		for (Transport tpt : transports) {
			bw.write("\t\t(= (transport-capacity " + tpt.getName() + ") "
					+ tpt.getCapacity() + ")");
			bw.newLine();
		}
		// Location Capacity
		for (Location loc : locations) {
			bw.write("\t\t(= (location-capacity " + loc.getName() + ") "
					+ loc.getCapacity() + ")");
			bw.newLine();
		}
		// Transport Inventory
		for (Transport tpt : transports) {
			bw.write("\t\t(= (transport-inventory " + tpt.getName() + ") "
					+ tpt.getInventory() + ")");
			bw.newLine();
		}
		// Location Inventory
		for (Location loc : locations) {
			bw.write("\t\t(= (location-inventory " + loc.getName() + ") "
					+ loc.getInventory() + ")");
			bw.newLine();
		}
		bw.close();
	}

}
