package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.pddl.logistics.controller.Constants;
import edu.pddl.logistics.controller.Controller;
import edu.pddl.logistics.model.Transport;

public class TransportsManagementPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297726541770260245L;

	private JButton addTransport = null;
	private JButton removeTransport = null;
	private JTabbedPane tabbedPane = null;

	private Controller controller = null;
	private Map<Transport, Integer> transportToTabIndexMap = null;

	public TransportsManagementPanel(Controller controller) {
		this.controller = controller;
		this.controller.addActionListener(this);
		this.transportToTabIndexMap = new IdentityHashMap<Transport, Integer>();

		setLayout(new BorderLayout());
		initTransportPanel();
	}

	private void initTransportPanel() {
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		addTransport = new JButton("Add Transport");
		addTransport.addActionListener(this);

		removeTransport = new JButton("Remove Transport");
		removeTransport.addActionListener(this);

		JLabel transportInfoLabel = new JLabel("Transport Information:");
		add(transportInfoLabel, BorderLayout.NORTH);

		JPanel cmdPanel = new JPanel(new FlowLayout());
		cmdPanel.add(addTransport);
		cmdPanel.add(removeTransport);
		add(cmdPanel, BorderLayout.SOUTH);
	}

	private void addTransport() {
		controller.addNewTransport();
	}

	private void addTransport(Transport transport) {
		TransportAndRouteInformationPanel tptPanel = new TransportAndRouteInformationPanel(
				transport, controller);
		transportToTabIndexMap.put(transport, tabbedPane.getTabCount());
		tabbedPane.addTab(transport.getName(), tptPanel);
	}

	private void removeTransport() {
		int index = tabbedPane.getSelectedIndex();
		Transport transport = getTransport(index);
		if (transport != null) {
			controller.removeTransport(transport);
		}
	}

	private void removeTransport(Transport transport) {
		int index = transportToTabIndexMap.get(transport);
		tabbedPane.remove(index);
	}

	private void updateTransport(Transport transport) {
		Integer idx = transportToTabIndexMap.get(transport);
		if (idx != null) {
			tabbedPane.setTitleAt(idx, transport.getName());
		}
	}

	private Transport getTransport(int index) {
		for (Transport transport : transportToTabIndexMap.keySet()) {
			if (index == transportToTabIndexMap.get(transport)) {
				return transport;
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addTransport) {
			addTransport();
		} else if (source == removeTransport) {
			removeTransport();
		} else if (source instanceof Transport) {
			Transport transport = (Transport) source;
			if (e.getActionCommand().equals(Constants.OPERATION_CREATE)) {
				addTransport(transport);
			} else if (e.getActionCommand().equals(Constants.OPERATION_UPDATE)) {
				updateTransport(transport);
			} else if (e.getActionCommand().equals(Constants.OPERATION_DELETE)) {
				removeTransport(transport);
			}
		}
	}
}
