package edu.pddl.mmcr.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import edu.pddl.mmcr.model.Cargo;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.PDDLProblem;
import edu.pddl.mmcr.model.Vehicle;

/**
 * This is a really brittle reader. Just needed to get something working to
 * allow reading in saved files to fast track creating bigger problems.
 * 
 * @author tony
 * 
 */
public class PDDLReaderUtil {

	private static final String PROBLEM_NAME_LINE_DELIMETER = ".*\\((\\s*)?problem.*";
	private static final String PROBLEM_NAME_DELIMETER = ".*\\((\\s*)?problem(\\s*)";
	private static final String LOCATION_DEFINITION_DELIMETER = "- LOCATION";
	private static final String VEHICLE_DEFINITION_DELIMETER = "- VEHICLE";
	private static final String CARGO_DEFINITION_DELIMETER = "- CARGO";
	private static final String CAPACITY_DELIMETER = "\t\t(= (remaining-capacity ";
	private static final String LOCATION_DELIMETER = "\t\t(at ";
	private static final String ROUTES_DELIMETER = "\t\t(= (travel-time ";
	private static final String LOAD_DELIMETER = "\t\t(= (load-time ";
	private static final String UNLOAD_DELIMETER = "\t\t(= (unload-time ";
	private static final String AVAILABLE_DELIMETER = ".*\\(at \\d+ \\(available.+\\)\\)";
	private static final String REQUIRED_BY_DELIMETER = ".*\\(at \\d+.\\d* \\(not \\(available.+\\)\\)\\)";
	private static final String SIZE_DELIMETER = "\t\t(= (size ";
	private static final String GOAL_LOCATION_DELIMETER = "\t\t(at ";

	public static PDDLProblem readProblem(File file) throws IOException {
		PDDLProblem model = new PDDLProblem();
		RandomAccessFile reader = new RandomAccessFile(file, "r");
		reader.readLine();
		// Get Name
		String name = getProblemName(reader);
		model.setProblemName(name);
		// Get Locations
		List<Location> locations = getLocations(reader);
		List<Vehicle> vehicles = getVehicles(reader, locations);
		List<Cargo> cargos = getCargos(reader, locations);

		model.getLocations().addAll(locations);
		model.getVehicles().addAll(vehicles);
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
		// Find Pickup Location
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
					cargo.setPickupLocation(location);
				}
			}
			line = reader.readLine();
		}
		// Find Delivery Location
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.contains(GOAL_LOCATION_DELIMETER)) {
				String data = line.substring(GOAL_LOCATION_DELIMETER.length());
				String[] parts = data.split(" ");
				String name = parts[0].trim();
				String loc = parts[1].replace(')', ' ').trim();
				Cargo cargo = CargoUtil.getCargoByName(name, cargos);
				Location location = LocationUtil.getLocationByName(loc,
						locations);
				if ((cargo != null) && (location != null)) {
					cargo.setDeliveryLocation(location);
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
					cargo.setAvailableIn(Double.parseDouble(available));
				}
			}
			line = reader.readLine();
		}
		// Find Required By Time
		reader.seek(0);
		line = reader.readLine();
		while (line != null) {
			if (line.matches(REQUIRED_BY_DELIMETER)) {
				String[] parts = line.split(" ");
				String name = parts[4].replace(')', ' ').trim();
				String available = parts[1].trim();
				Cargo cargo = CargoUtil.getCargoByName(name, cargos);
				if (cargo != null) {
					cargo.setRequiredBy(Double.parseDouble(available));
				}
			}
			line = reader.readLine();
		}

		return cargos;
	}

	private static List<Vehicle> getVehicles(RandomAccessFile reader,
			List<Location> locations) throws IOException {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		// Find Definition
		reader.seek(0);
		String line = reader.readLine();
		while (line != null) {
			if (line.contains(VEHICLE_DEFINITION_DELIMETER)) {
				String names = line.split(VEHICLE_DEFINITION_DELIMETER)[0]
						.trim();
				String[] vehs = names.split(" ");
				for (String veh : vehs) {
					Vehicle vehicle = new Vehicle(veh, 0, 0);
					vehicles.add(vehicle);
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
				Vehicle vehicle = VehicleUtil.getVehicleByName(name,
						vehicles);
				if (vehicle != null) {
					vehicle.setRemainingCapacity(Integer.parseInt(cap));
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
				Vehicle vehicle = VehicleUtil.getVehicleByName(name,
						vehicles);
				Location location = LocationUtil.getLocationByName(loc,
						locations);
				if ((vehicle != null) && (location != null)) {
					vehicle.setInitialLocation(location);
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
				String vname = parts[0].trim();
				String oname = parts[1].trim();
				String dname = parts[2].replace(')', ' ').trim();
				String time = parts[3].replace(')', ' ').trim();

				Vehicle vehicle = VehicleUtil.getVehicleByName(vname,
						vehicles);
				Location origin = LocationUtil.getLocationByName(oname,
						locations);
				Location destination = LocationUtil.getLocationByName(dname,
						locations);
				if ((vehicle != null) && (origin != null)
						&& (destination != null)) {
					vehicle.updateRoute(origin, destination,
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
				String veh_name = parts[0].replace(')', ' ').trim();
				String loc_name = parts[1].replace(')', ' ').trim();
				String load = parts[2].replace(')', ' ').trim();
				Vehicle vehicle = VehicleUtil.getVehicleByName(
						veh_name, vehicles);
				Location loc = LocationUtil.getLocationByName(loc_name,
						locations);
				if (vehicle != null) {
					vehicle.setLoadingTime(loc, Integer.parseInt(load));
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
				String veh_name = parts[0].replace(')', ' ').trim();
				String loc_name = parts[1].replace(')', ' ').trim();
				String unload = parts[2].replace(')', ' ').trim();
				Vehicle vehicle = VehicleUtil.getVehicleByName(
						veh_name, vehicles);
				Location loc = LocationUtil.getLocationByName(loc_name,
						locations);
				if (vehicle != null) {
					vehicle.setUnloadingTime(loc, Integer.parseInt(unload));
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
				Vehicle vehicle = VehicleUtil.getVehicleByName(name,
						vehicles);
				if (vehicle != null) {
					vehicle.setAvailableIn(Integer.parseInt(available));
				}
			}
			line = reader.readLine();
		}

		return vehicles;
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
					Location location = new Location(loc);
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
		return locations;
	}

	private static String getProblemName(RandomAccessFile reader)
			throws IOException {
		reader.seek(0);
		String line = reader.readLine();
		while (line != null) {
			if (line.matches(PROBLEM_NAME_LINE_DELIMETER)) {
				String [] parts = line.split(PROBLEM_NAME_DELIMETER);
				int last = parts[1].indexOf(')');
				line = parts[1].substring(0, last);
				return line;
			}
			line = reader.readLine();
		}
		return null;
	}

}
