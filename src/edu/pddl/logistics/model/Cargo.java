package edu.pddl.logistics.model;

public class Cargo {

	private static int numCargo = 0;

	private String name = "";
	private int size = 0;
	private Location initialLocation = null;

	public Cargo(String name, int size) {
		super();
		this.name = name;
		this.size = size;
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

	public void setInitialLocation(Location loc) {
		initialLocation = loc;
	}

	public Location getInitialLocation() {
		return initialLocation;
	}

}
