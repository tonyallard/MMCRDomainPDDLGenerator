package edu.pddl.logistics.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import edu.pddl.logistics.exception.PDDLModelIncompleteException;
import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.PDDLProblem;
import edu.pddl.logistics.model.Transport;
import edu.pddl.logistics.util.LocationUtil;
import edu.pddl.logistics.util.PDDLReaderUtil;
import edu.pddl.logistics.util.PDDLWriterUtil;

public class Controller {

	private PDDLProblem model = null;
	private boolean fileChangedFlag = false;

	private Vector<ActionListener> listeners = null;

	public Controller() {
		listeners = new Vector<ActionListener>();
		createNewModel();
	}

	public Controller(File selectedFile) throws IOException {
		listeners = new Vector<ActionListener>();
		model = PDDLReaderUtil.readProblem(selectedFile);
	}

	public boolean pendingChanges() {
		return fileChangedFlag;
	}

	public void addActionListener(final ActionListener listener) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				listeners.add(listener);
			}
		});
	}

	public void removeActionListener(final ActionListener listener) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				listeners.remove(listener);
			}
		});
	}

	public void createNewModel() {
		this.model = new PDDLProblem();
	}

	public void save(File selectedFile) throws IOException,
			PDDLModelIncompleteException {
		PDDLWriterUtil.writeProblem(selectedFile, model.getProblemName(),
				model.getLocations(), model.getTransports(), model.getCargos());
		fileChangedFlag = false;
	}

	public String getProblemName() {
		return model.getProblemName();
	}

	public void setProblemName(String newProblemName) {
		if (!model.getProblemName().equals(newProblemName)) {
			model.setProblemName(newProblemName);
			fileChangedFlag = true;
		}
	}

	// LOCATION CRUD METHODS
	public final Vector<Location> getLocations() {
		return model.getLocations();
	}

	public Location getLocationByName(String name) {
		return LocationUtil.getLocationByName(name, model.getLocations());
	}

	public void addNewLocaiton() {
		Location location = new Location(
				"L" + (Location.getNumLocations() + 1), 0, 0);
		model.getLocations().add(location);
		ActionEvent event = new ActionEvent(location,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void removeLocation(Location location) {
		model.getLocations().remove(location);
		for (Transport transport : model.getTransports()) {
			if (transport.getInitialLocation() == location) {
				transport.setInitialLocation(null);
				ActionEvent event = new ActionEvent(transport,
						ActionEvent.ACTION_PERFORMED,
						Constants.OPERATION_UPDATE);
				notifyListeners(event);
			}
			transport.removeRouteWithLocation(location);
		}
		for (Cargo cargo : model.getCargos()) {
			if (cargo.getInitialLocation() == location) {
				cargo.setInitialLocation(null);
				ActionEvent event = new ActionEvent(cargo,
						ActionEvent.ACTION_PERFORMED,
						Constants.OPERATION_UPDATE);
				notifyListeners(event);
			}
		}
		ActionEvent event = new ActionEvent(location,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_DELETE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void setLocationName(Location location, String newName) {
		if (!location.getName().equals(newName)) {
			location.setName(newName);
			ActionEvent event = new ActionEvent(location,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setLocationRemainingCapacity(Location location, int capacity) {
		if (location.getRemainingCapacity() != capacity) {
			location.setRemainingCapacity(capacity);
			ActionEvent event = new ActionEvent(location,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setLocationCurrentInventory(Location location, int inventory) {
		if (location.getCurrentInventory() != inventory) {
			location.setCurrentInventory(inventory);
			ActionEvent event = new ActionEvent(location,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	// TRANSPORT CRUD METHODS
	public void addNewTransport() {
		Transport transport = new Transport();
		model.getTransports().add(transport);
		ActionEvent event = new ActionEvent(transport,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void removeTransport(Transport transport) {
		model.getTransports().remove(transport);
		ActionEvent event = new ActionEvent(transport,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_DELETE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void setTransportName(Transport transport, String newName) {
		if (!transport.getName().equals(newName)) {
			transport.setName(newName);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportRemainingCapacity(Transport transport,
			int remainingCapacity) {
		if (transport.getRemainingCapacity() != remainingCapacity) {
			transport.setRemainingCapacity(remainingCapacity);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportCurrentInventory(Transport transport, int inv) {
		if (transport.getCurrentInventory() != inv) {
			transport.setCurrentInventory(inv);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportLoadTime(Transport transport, int load) {
		if (transport.getLoadTime() != load) {
			transport.setLoadTime(load);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportUnloadTime(Transport transport, int unload) {
		if (transport.getUnloadTime() != unload) {
			transport.setUnloadTime(unload);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportInitialLocation(Transport transport,
			Location location) {
		if (!transport.getInitialLocation().equals(location)) {
			transport.setInitialLocation(location);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportAvailableIn(Transport transport, int availableIn) {
		if (transport.getAvailableIn() != availableIn) {
			transport.setAvailableIn(availableIn);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setTransportRoute(Transport transport, Location origin,
			Location destination, Integer travelTime) {
		if (transport.getRouteTravelTime(origin, destination) != travelTime) {
			transport.updateRoute(origin, destination, travelTime);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
		}
		fileChangedFlag = true;
	}

	public void removeTransportRoute(Transport transport, Location origin,
			Location destination) {
		transport.removeRoute(origin, destination);
		ActionEvent event = new ActionEvent(transport,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	// CARGO CRUD METHODS
	public void addNewCargo() {
		Cargo cargo = new Cargo("C" + (Cargo.getNumCargo() + 1), 0, 0);
		model.getCargos().add(cargo);
		ActionEvent event = new ActionEvent(cargo,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void removeCargo(Cargo cargo) {
		model.getCargos().remove(cargo);
		ActionEvent event = new ActionEvent(cargo,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_DELETE);
		notifyListeners(event);
		fileChangedFlag = true;
	}

	public void updateCargoName(Cargo cargo, String newName) {
		if (!cargo.getName().equals(newName)) {
			cargo.setName(newName);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setCargoInitialLocation(Cargo cargo, Location location) {
		if (!cargo.getInitialLocation().equals(location)) {
			cargo.setInitialLocation(location);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void updateCargoSize(Cargo cargo, int size) {
		if (cargo.getSize() != size) {
			cargo.setSize(size);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setCargoAvailableIn(Cargo cargo, int availableIn) {
		if (cargo.getAvailableIn() != availableIn) {
			cargo.setAvailableIn(availableIn);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}
	
	public void invokeCreationEvents() {
		for (Location location : model.getLocations()) {
			ActionEvent event = new ActionEvent(location,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
			notifyListeners(event);
		}
		
		for (Transport transport : model.getTransports()) {
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
			notifyListeners(event);
		}
		
		for (Cargo cargo : model.getCargos()) {
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_CREATE);
			notifyListeners(event);
		}
	}

	private void notifyListeners(final ActionEvent event) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (ActionListener listener : listeners) {
					listener.actionPerformed(event);
				}
			}
		});
	}
}
