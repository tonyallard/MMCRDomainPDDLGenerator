package edu.pddl.logistics.model;

public class Location {

	private static int numLocations = 0;

	private String name = null;
	private int capacity = 0;
	private int inventory = 0;

	public Location(String name, int capacity, int inventory) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.inventory = inventory;
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

	/**
	 * This is needed to display correctly in combo boxes.
	 */
	public String toString() {
		return name;
	}
}
