package edu.pddl.logistics.model;

public class Route {
	private Transport transport = null;
	private Location origin = null;
	private Location destination = null;
	private int travelTime = 0;
	
	public Route(Transport transport, Location origin, Location destination,
			int travelTime) {
		super();
		this.transport = transport;
		this.origin = origin;
		this.destination = destination;
		this.travelTime = travelTime;
	}
	public Transport getTransport() {
		return transport;
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
