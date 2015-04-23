package edu.pddl.mmcr.model;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Transport {

	private static int numTransports = 0;

	private String name = null;
	private int remainingCapacity = 0;
	private Location initialLocation = null;
	private Map<Location, Map<Location, Integer>> routes = null;
	private Map<Location, Integer> loadingTimes = null;
	private Map<Location, Integer> unloadingTimes = null;
	private int availableIn = 0;

	public Transport(String name, int remainingCapacity,
			int availableIn) {
		super();
		this.name = name;
		this.remainingCapacity = remainingCapacity;
		this.availableIn = availableIn;
		this.routes = new IdentityHashMap<Location, Map<Location, Integer>>();
		this.loadingTimes = new HashMap<>();
		this.unloadingTimes = new HashMap<>();
		numTransports++;
	}

	public Transport() {
		this("T" + (numTransports + 1), 0, 0);
	}

	public void updateRoute(Location origin, Location destination,
			Integer travelTime) {
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
		for (Location origin : routes.keySet()) {
			Map<Location, Integer> destinationTravelTimeMap = routes
					.get(origin);
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

	public Integer getLoadingTime(Location loc) {
		return loadingTimes.get(loc);
	}

	public void setLoadingTime(Location loc, Integer loadingTime) {
		loadingTimes.put(loc, loadingTime);
	}

	public void removeLoadingTime(Location loc) {
		loadingTimes.remove(loc);		
	}

	public Integer getUnloadingTime(Location loc) {
		return unloadingTimes.get(loc);
	}

	public void setUnloadingTime(Location loc, Integer unloadingTime) {
		unloadingTimes.put(loc, unloadingTime);
	}

	public void removeUnloadingTime(Location loc) {
		unloadingTimes.remove(loc);		
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

	public Location getInitialLocation() {
		return initialLocation;
	}

	public void setInitialLocation(Location initialLocation) {
		this.initialLocation = initialLocation;
	}

	public Map<Location, Map<Location, Integer>> getRoutes() {
		return routes;
	}

	public Map<Location, Integer> getLoadingTimes() {
		return loadingTimes;
	}

	public Map<Location, Integer> getUnloadingTimes() {
		return unloadingTimes;
	}

	public int getAvailableIn() {
		return availableIn;
	}

	public void setAvailableIn(int availableIn) {
		this.availableIn = availableIn;
	}
}
