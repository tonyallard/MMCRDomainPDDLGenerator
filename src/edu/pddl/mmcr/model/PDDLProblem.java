package edu.pddl.mmcr.model;

import java.util.Vector;

public class PDDLProblem {
	private Vector<Location> locations = null;
	private Vector<Vehicle> vehicles = null;
	private Vector<Cargo> cargos = null;
	private String problemName = "Problem1";
	
	public PDDLProblem() {
		locations = new Vector<Location>();
		vehicles = new Vector<Vehicle>();
		cargos = new Vector<Cargo>();
	}
	
	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}
	
	public Vector<Location> getLocations() {
		return locations;
	}
	public Vector<Vehicle> getVehicles() {
		return vehicles;
	}
	public Vector<Cargo> getCargos() {
		return cargos;
	}
	public String getProblemName() {
		return problemName;
	}
}
