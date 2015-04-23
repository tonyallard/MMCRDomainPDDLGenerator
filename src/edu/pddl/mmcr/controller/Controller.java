package edu.pddl.mmcr.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import edu.pddl.mmcr.exception.PDDLModelIncompleteException;
import edu.pddl.mmcr.model.Cargo;
import edu.pddl.mmcr.model.Location;
import edu.pddl.mmcr.model.PDDLProblem;
import edu.pddl.mmcr.model.Transport;
import edu.pddl.mmcr.util.LocationUtil;
import edu.pddl.mmcr.util.PDDLReaderUtil;
import edu.pddl.mmcr.util.PDDLWriterUtil;

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
				"L" + (Location.getNumLocations() + 1));
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

	public void setLocationRemainingCapacity(Location location, Integer capacity) {
		if (capacity.equals(location.getRemainingCapacity())) {
			location.setRemainingCapacity(capacity);
			ActionEvent event = new ActionEvent(location,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}
	
	public void removeLocationRemainingCapacity(Location location) {
		location.setRemainingCapacity(null);
		ActionEvent event = new ActionEvent(location,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
		notifyListeners(event);
		fileChangedFlag = true;
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

	public void setTransportLoadingTime(Transport transport, Location loc, Integer loadingTime) {
		if (!loadingTime.equals(transport.getLoadingTime(loc))) {
			transport.setLoadingTime(loc, loadingTime);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}
	
	public void removeTransportLoadingTime(Transport transport,
			Location location) {
		transport.removeLoadingTime(location);
		ActionEvent event = new ActionEvent(transport,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
		notifyListeners(event);
		fileChangedFlag = true;	
	}

	public void setTransportUnloadingTime(Transport transport, Location loc, Integer unloadingTime) {
		if (!unloadingTime.equals(transport.getUnloadingTime(loc))) {
			transport.setUnloadingTime(loc, unloadingTime);
			ActionEvent event = new ActionEvent(transport,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void removeTransportUnloadingTime(Transport transport,
			Location location) {
		transport.removeUnloadingTime(location);
		ActionEvent event = new ActionEvent(transport,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
		notifyListeners(event);
		fileChangedFlag = true;			
	}

	public void setTransportInitialLocation(Transport transport,
			Location location) {
		if (!location.equals(transport.getInitialLocation())) {
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
		if (!location.equals(cargo.getInitialLocation())) {
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

	public void setCargoAvailableIn(Cargo cargo, Integer availableIn) {
		if (!availableIn.equals(cargo.getAvailableIn())) {
			cargo.setAvailableIn(availableIn);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void setCargoRequiredBy(Cargo cargo, Integer requiredBy) {
		if (!requiredBy.equals(cargo.getRequiredBy())) {
			cargo.setRequiredBy(requiredBy);
			ActionEvent event = new ActionEvent(cargo,
					ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
			notifyListeners(event);
			fileChangedFlag = true;
		}
	}

	public void removeCargoRequiredBy(Cargo cargo) {
		cargo.setRequiredBy(null);
		ActionEvent event = new ActionEvent(cargo,
				ActionEvent.ACTION_PERFORMED, Constants.OPERATION_UPDATE);
		notifyListeners(event);
		fileChangedFlag = true;		
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
