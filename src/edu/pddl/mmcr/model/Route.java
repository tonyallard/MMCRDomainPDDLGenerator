package edu.pddl.mmcr.model;

public class Route {
	private Vehicle vehicle = null;
	private Location origin = null;
	private Location destination = null;
	private int travelTime = 0;
	
	public Route(Vehicle vehicle, Location origin, Location destination,
			int travelTime) {
		super();
		this.vehicle = vehicle;
		this.origin = origin;
		this.destination = destination;
		this.travelTime = travelTime;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public Location getOrigin() {
		return origin;
	}
	public Location getDestination() {
		return destination;
	}
	public int getTravelTime() {
		return travelTime;
	}
}
