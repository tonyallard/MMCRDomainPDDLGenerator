package edu.pddl.logistics.model;

import java.util.IdentityHashMap;
import java.util.Map;

public class Transport {

	private static int numTransports = 0;

	private String name = null;
	private int remainingCapacity = 0;
	private int currentInventory = 0;
	private Location initialLocation = null;
	private Map<Location, Map<Location, Integer>> routes = null;
	private int loadTime = 0;
	private int unloadTime = 0;
	private int availableIn = 0;

	public Transport(String name, int remainingCapacity, int currentInventory, int loadTime, int unloadTime, int availableIn) {
		super();
		this.name = name;
		this.remainingCapacity = remainingCapacity;
		this.currentInventory = currentInventory;
		this.loadTime = loadTime;
		this.unloadTime = unloadTime;
		this.availableIn = availableIn;
		this.routes = new IdentityHashMap<Location, Map<Location, Integer>>();
		numTransports++;
	}

	public Transport() {
		this("T" + (numTransports + 1), 0, 0, 0, 0, 0);
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
	
	public void removeRouteWithLocation(Location loc) {
		routes.remove(loc);
		for (Location origin : routes.keySet()){
			Map<Location, Integer> destinationTravelTimeMap = routes.get(origin);
			destinationTravelTimeMap.remove(loc);
		}
	}

	public Integer getRouteTravelTime(Location origin, Location destination) {
		Map<Location, Integer> destinationTravelTimeMap = routes.get(origin);
		if (destinationTravelTimeMap != null) {
			return destinationTravelTimeMap.get(destination);
		}
		return null;
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
	
	public int getRemainingCapacity() {
		return remainingCapacity;
	}

	public void setRemainingCapacity(int capacity) {
		this.remainingCapacity = capacity;
	}

	public int getCurrentInventory() {
		return currentInventory;
	}

	public void setCurrentInventory(int inventory) {
		this.currentInventory = inventory;
	}

	public Location getInitialLocation() {
		return initialLocation;
	}

	public void setInitialLocation(Location initialLocation) {
		this.initialLocation = initialLocation;
	}

	public Map<Location, Map<Location, Integer>> getRoutes() {
		return routes;
	}

	public int getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(int loadTime) {
		this.loadTime = loadTime;
	}

	public int getUnloadTime() {
		return unloadTime;
	}

	public void setUnloadTime(int unloadTime) {
		this.unloadTime = unloadTime;
	}

	public int getAvailableIn() {
		return availableIn;
	}

	public void setAvailableIn(int availableIn) {
		this.availableIn = availableIn;
	}
}
