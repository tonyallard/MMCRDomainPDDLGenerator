package edu.pddl.logistics.util;

import edu.pddl.logistics.model.Transport;

public class TransportUtil {

	private TransportUtil() {
		
	}
	
	public static Transport getTransportByName(String name, Iterable<Transport> transports) {
		for (Transport transport : transports) {
			if (transport.getName().equals(name)){
				return transport;
			}
		}
		return null;
	}
}
