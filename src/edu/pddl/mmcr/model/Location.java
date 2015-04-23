package edu.pddl.mmcr.model;

public class Location {

	private static int numLocations = 0;

	private String name = null;
	private Integer reamainingCapacity = null;

	public Location(String name) {
		super();
		this.name = name;
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

	public Integer getRemainingCapacity() {
		return reamainingCapacity;
	}

	public void setRemainingCapacity(Integer capacity) {
		this.reamainingCapacity = capacity;
	}

	/**
	 * This is needed to display correctly in combo boxes.
	 */
	public String toString() {
		return name;
	}
}
