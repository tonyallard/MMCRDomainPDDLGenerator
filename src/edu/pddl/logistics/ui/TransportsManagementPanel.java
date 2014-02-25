package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;

public class TransportsManagementPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297726541770260245L;

	private JButton addTransport = null;
	private List<TransportAndRouteInformationPanel> transportPanels = null;
	private JTabbedPane tabbedPane = null;

	private List<Transport> transports = null;
	private Vector<Location> locations = null;

	public TransportsManagementPanel(List<Transport> transports,
			Vector<Location> locations) {
		this.transports = transports;
		this.locations = locations;
		transportPanels = new ArrayList<TransportAndRouteInformationPanel>();

		setLayout(new BorderLayout());

		initTransportPanel();
	}

	private void initTransportPanel() {
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		addTransport = new JButton("Add Transport");
		addTransport.addActionListener(this);

		JPanel cmdPanel = new JPanel(new FlowLayout());
		cmdPanel.add(addTransport);
		add(cmdPanel, BorderLayout.SOUTH);
	}

	public void addLocation(Location loc) {
		for (TransportAndRouteInformationPanel tptPanel : transportPanels) {
			tptPanel.addLocation(loc);
		}
	}

	public void updateLocation(Location loc) {
		for (TransportAndRouteInformationPanel tptPanel : transportPanels) {
			tptPanel.updateLocation(loc);
		}
	}

	private void addTransport() {
		Transport tpt = new Transport();
		transports.add(tpt);
		TransportAndRouteInformationPanel tptPanel = new TransportAndRouteInformationPanel(tpt, locations);
		tptPanel.addActionListener(this);
		transportPanels.add(tptPanel);
		tabbedPane.addTab(tpt.getName(), tptPanel);
		for (Location loc : locations) {
			tptPanel.addLocation(loc);
		}
	}	
	
	private void updateTransport(Transport transport) {
		int idx = transports.indexOf(transport);
		tabbedPane.setTitleAt(idx, transport.getName());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addTransport) {
			addTransport();
		} else if (source instanceof Transport) {
			updateTransport((Transport)source);
		}
	}
}
