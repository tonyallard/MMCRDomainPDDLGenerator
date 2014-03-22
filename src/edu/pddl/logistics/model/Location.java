package edu.pddl.logistics.model;

public class Location {

	private static int numLocations = 0;

	private String name = null;
	private int reamainingCapacity = 0;
	private int currentInventory = 0;

	public Location(String name, int reamainingCapacity, int currentInventory) {
		super();
		this.name = name;
		this.reamainingCapacity = reamainingCapacity;
		this.currentInventory = currentInventory;
		numLocations++;
	}

	public static int getNumLocations() {
		return numLocations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRemainingCapacity() {
		return reamainingCapacity;
	}

	public void setRemainingCapacity(int capacity) {
		this.reamainingCapacity = capacity;
	}

	public int getCurrentInventory() {
		return currentInventory;
	}

	public void setCurrentInventory(int inventory) {
		this.currentInventory = inventory;
	}

	/**
	 * This is needed to display correctly in combo boxes.
	 */
	public String toString() {
		return name;
	}
}
