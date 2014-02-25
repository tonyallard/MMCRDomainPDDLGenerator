package edu.pddl.logistics.model;

import java.util.IdentityHashMap;
import java.util.Map;

public class Transport {

	private static int numTransports = 0;

	private String name = null;
	private int capacity = 0;
	private int inventory = 0;
	private Location initialLocation = null;
	private Map<Location, Map<Location, Integer>> routes = null;

	public Transport(String name, int capacity, int inventory) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.inventory = inventory;
		this.routes = new IdentityHashMap<Location, Map<Location, Integer>>();
		numTransports++;
	}
	
	public void updateRoute(Location origin, Location destination, Integer travelTime) {
		Map<Location, Integer> destinationTravelTimeMap = routes.get(origin);
		if (destinationTravelTimeMap == null) {
			destinationTravelTimeMap = new IdentityHashMap<Location, Integer>();
			routes.put(origin, destinationTravelTimeMap);
		}
		destinationTravelTimeMap.put(destination, travelTime);
	}
	
	public void removeRoute(Location origin, Location destination) {
		Map<Location, Integer> destinationTravelTimeMap = routes.get(origin);
		if (destinationTravelTimeMap != null) {
			destinationTravelTimeMap.remove(destination);
		}
	}

	public Transport() {
		this("T" + (numTransports + 1), 0, 0);
	}

	public static int getNumTransports() {
		return numTransports;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public Location getInitialLocation() {
		return initialLocation;
	}

	public void setInitialLocation(Location initialLocation) {
		this.initialLocation = initialLocation;
	}
}
