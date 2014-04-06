package edu.pddl.logistics.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.PDDLProblem;
import edu.pddl.logistics.model.Transport;

/**
 * This is a really brittle reader. Just needed to get something working to
 * allow reading in saved files to fast track creating bigger problems.
 * 
 * @author tony
 * 
 */
public class PDDLReaderUtil {

	private static final String PROBLEM_NAME_DELIMETER = "problem ";
	private static final String LOCATION_DEFINITION_DELIMETER = "- LOCATION";
	private static final String TRANSPORT_DEFINITION_DELIMETER = "- TRANSPORT";
	private static final String CARGO_DEFINITION_DELIMETER = "- CARGO";
	private static final String CAPACITY_DELIMETER = "\t\t(= (remaining-capacity ";
	private static final String INVENTORY_DELIMETER = "\t\t(= (current-inventory ";
	private static final String LOCATION_DELIMETER = "\t\t(at ";
	private static final String ROUTES_DELIMETER = "\t\t(= (travel-time ";
	private static final String LOAD_DELIMETER = "\t\t(= (load-time ";
	private static final String UNLOAD_DELIMETER = "\t\t(= (unload-time ";
	private static final String AVAILABLE_DELIMETER = ".+\\(at \\d+ \\(available.+\\)\\)";
	private static final String SIZE_DELIMETER = "\t\t(= (size ";

	public static PDDLProblem readProblem(File file) throws IOException {
		PDDLProblem model = new PDDLProblem();
		RandomAccessFile reader = new RandomAccessFile(file, "r");
		reader.readLine();
		// Get Name
		String name = getProblemName(reader);
		model.setProblemName(name);
		// Get Locations
		List<Location> locations = getLocations(reader);
		List<Transport> transports = getTransports(reader, locations);
		List<Cargo> cargos = getCargos(reader, locations);

		model.getLocations().addAll(locations);
		model.getTransports().addAll(transports);
		model.getCargos().addAll(cargos);

		return model;
	}

	private static List<Cargo> getCargos(RandomAccessFile reader,
			List<Location> locations) throws IOException {
		List<Cargo> cargos = new ArrayList<Cargo>();
		// Find Definition
		reader.seek(0);
		String line = reader.readLine();
		while (line != null) {
			if (line.contains(CARGO_DEFINITION_DELIMETER)) {
				String names = line.split(CARGO_DEFINITION_DELIMETER)[0].trim();
				String[] cargoNames = names.split(" ");
				for (String cargoName : cargoNames) {
					Cargo cargo = new Cargo(cargoName, 0, 0);
					cargos.add(cargo);
				}
			}
			line = reader.readLine();
		}
		// Find Size
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(SIZE_DELIMETER)) {
				String data = line.substring(SIZE_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String size = parts[1].trim();
				Cargo cargo = CargoUtil.getCargoByName(name, cargos);
				if (cargo != null) {
					cargo.setSize(Integer.parseInt(size));
				}
			}
			line = reader.readLine();
		}
		// Find Initial Location
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(LOCATION_DELIMETER)) {
				String data = line.substring(LOCATION_DELIMETER.length());
				String[] parts = data.split(" ");
				String name = parts[0].trim();
				String loc = parts[1].replace(')', ' ').trim();
				Cargo cargo = CargoUtil.getCargoByName(name, cargos);
				Location location = LocationUtil.getLocationByName(loc,
						locations);
				if ((cargo != null) && (location != null)) {
					cargo.setInitialLocation(location);
				}
			}
			line = reader.readLine();
		}
		// Find Available In Time
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.matches(AVAILABLE_DELIMETER)) {
				String[] parts = line.split(" ");
				String name = parts[3].replace(')', ' ').trim();
				String available = parts[1].trim();
				Cargo cargo = CargoUtil.getCargoByName(name, cargos);
				if (cargo != null) {
					cargo.setAvailableIn(Integer.parseInt(available));
				}
			}
			line = reader.readLine();
		}

		return cargos;
	}

	private static List<Transport> getTransports(RandomAccessFile reader,
			List<Location> locations) throws IOException {
		List<Transport> transports = new ArrayList<Transport>();
		// Find Definition
		reader.seek(0);
		String line = reader.readLine();
		while (line != null) {
			if (line.contains(TRANSPORT_DEFINITION_DELIMETER)) {
				String names = line.split(TRANSPORT_DEFINITION_DELIMETER)[0]
						.trim();
				String[] tpts = names.split(" ");
				for (String tpt : tpts) {
					Transport transport = new Transport(tpt, 0, 0, 0, 0, 0);
					transports.add(transport);
				}
			}
			line = reader.readLine();
		}
		// Find Remaining Capacity
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(CAPACITY_DELIMETER)) {
				String data = line.substring(CAPACITY_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String cap = parts[1].trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				if (transport != null) {
					transport.setRemainingCapacity(Integer.parseInt(cap));
				}
			}
			line = reader.readLine();
		}
		// Find Current Inventory Capacity
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(INVENTORY_DELIMETER)) {
				String data = line.substring(INVENTORY_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String inv = parts[1].trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				if (transport != null) {
					transport.setCurrentInventory(Integer.parseInt(inv));
				}
			}
			line = reader.readLine();
		}
		// Find Initial Location
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(LOCATION_DELIMETER)) {
				String data = line.substring(LOCATION_DELIMETER.length());
				String[] parts = data.split(" ");
				String name = parts[0].trim();
				String loc = parts[1].replace(')', ' ').trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				Location location = LocationUtil.getLocationByName(loc,
						locations);
				if ((transport != null) && (location != null)) {
					transport.setInitialLocation(location);
				}
			}
			line = reader.readLine();
		}
		// Find Routes
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(ROUTES_DELIMETER)) {
				String data = line.substring(ROUTES_DELIMETER.length());
				String[] parts = data.split(" ");
				String tname = parts[0].trim();
				String oname = parts[1].trim();
				String dname = parts[2].replace(')', ' ').trim();
				String time = parts[3].replace(')', ' ').trim();

				Transport transport = TransportUtil.getTransportByName(tname,
						transports);
				Location origin = LocationUtil.getLocationByName(oname,
						locations);
				Location destination = LocationUtil.getLocationByName(dname,
						locations);
				if ((transport != null) && (origin != null)
						&& (destination != null)) {
					transport.updateRoute(origin, destination,
							Integer.parseInt(time));
				}
			}
			line = reader.readLine();
		}
		// Find Load Time
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(LOAD_DELIMETER)) {
				String data = line.substring(LOAD_DELIMETER.length());
				String[] parts = data.split(" ");
				String name = parts[0].replace(')', ' ').trim();
				String load = parts[1].replace(')', ' ').trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				if (transport != null) {
					transport.setLoadTime(Integer.parseInt(load));
				}
			}
			line = reader.readLine();
		}
		// Find Unload Time
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(UNLOAD_DELIMETER)) {
				String data = line.substring(UNLOAD_DELIMETER.length());
				String[] parts = data.split(" ");
				String name = parts[0].replace(')', ' ').trim();
				String unload = parts[1].replace(')', ' ').trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				if (transport != null) {
					transport.setUnloadTime(Integer.parseInt(unload));
				}
			}
			line = reader.readLine();
		}
		// Find Available In Time
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.matches(AVAILABLE_DELIMETER)) {
				String[] parts = line.split(" ");
				String name = parts[3].replace(')', ' ').trim();
				String available = parts[1].trim();
				Transport transport = TransportUtil.getTransportByName(name,
						transports);
				if (transport != null) {
					transport.setAvailableIn(Integer.parseInt(available));
				}
			}
			line = reader.readLine();
		}

		return transports;
	}

	private static List<Location> getLocations(RandomAccessFile reader)
			throws IOException {
		List<Location> locations = new ArrayList<Location>();
		// Find Definition
		reader.seek(0);
		String line = reader.readLine();
		while (line != null) {
			if (line.contains(LOCATION_DEFINITION_DELIMETER)) {
				String names = line.split(LOCATION_DEFINITION_DELIMETER)[0]
						.trim();
				String[] locs = names.split(" ");
				for (String loc : locs) {
					Location location = new Location(loc, 0, 0);
					locations.add(location);
				}
			}
			line = reader.readLine();
		}
		// Find Remaining Capacity For Locations
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(CAPACITY_DELIMETER)) {
				String data = line.substring(CAPACITY_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String cap = parts[1].trim();
				Location location = LocationUtil.getLocationByName(name,
						locations);
				if (location != null) {
					location.setRemainingCapacity(Integer.parseInt(cap));
				}
			}
			line = reader.readLine();
		}
		// Find Current Inventory Capacity For Locations
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(INVENTORY_DELIMETER)) {
				String data = line.substring(INVENTORY_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String inv = parts[1].trim();
				Location location = LocationUtil.getLocationByName(name,
						locations);
				if (location != null) {
					location.setCurrentInventory(Integer.parseInt(inv));
				}
			}
			line = reader.readLine();
		}
		// Find Remaining Capacity For Locations
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(CAPACITY_DELIMETER)) {
				String data = line.substring(CAPACITY_DELIMETER.length());
				String[] parts = data.split("\\)");
				String name = parts[0].trim();
				String cap = parts[1].trim();
				Location location = LocationUtil.getLocationByName(name,
						locations);
				if (location != null) {
					location.setRemainingCapacity(Integer.parseInt(cap));
				}
			}
			line = reader.readLine();
		}
		return locations;
	}

	private static String getProblemName(RandomAccessFile reader)
			throws IOException {
		reader.seek(0);
		String line = reader.readLine();
		int first = line.indexOf('(', 1);
		int last = line.indexOf(')');
		line = line.substring(first + 1, last);
		return line.split(PROBLEM_NAME_DELIMETER)[1];
	}

}
