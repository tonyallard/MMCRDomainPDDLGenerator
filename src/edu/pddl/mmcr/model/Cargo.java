package edu.pddl.mmcr.model;

public class Cargo {

	private static int numCargo = 0;

	private String name = "";
	private int size = 0;
	private Location pickupLocation = null;
	private Location deliveryLocation = null;
	private double availableIn = 0;
	private Double requiredBy = null;

	public Cargo(String name, int size, int availableIn) {
		super();
		this.name = name;
		this.size = size;
		this.availableIn = availableIn;
		numCargo++;
	}

	public static int getNumCargo() {
		return numCargo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setPickupLocation(Location loc) {
		pickupLocation = loc;
	}

	public Location getPickupLocation() {
		return pickupLocation;
	}
	
	public Location getDeliveryLocation() {
		return deliveryLocation;
	}

	public void setDeliveryLocation(Location deliveryLocation) {
		this.deliveryLocation = deliveryLocation;
	}

	public double getAvailableIn() {
		return availableIn;
	}

	public void setAvailableIn(double availableIn) {
		this.availableIn = availableIn;
	}

	public Double getRequiredBy() {
		return requiredBy;
	}

	public void setRequiredBy(Double requiredBy) {
		this.requiredBy = requiredBy;
	}
}
