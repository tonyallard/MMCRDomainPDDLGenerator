package edu.pddl.logistics.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.pddl.logistics.model.Cargo;
import edu.pddl.logistics.model.Location;
import edu.pddl.logistics.model.Transport;
import edu.pddl.logistics.util.PDDLWriterConstants;
import edu.pddl.logistics.util.PDDLWriterUtil;

public class MainGUI extends JFrame implements ActionListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6534047709842754324L;

	private TransportsManagementPanel tPanel = null;
	private CargoInformationPanel cPanel = null;
	private JMenuItem saveProblem = null;
	private JMenuItem saveDomain = null;
	private JTextField problemNameField = null;

	private Vector<Location> locations = null;
	private List<Transport> transports = null;
	private List<Cargo> cargos = null;
	private String problemName = "Problem1";

	public MainGUI() {
		setSize(1200, 600);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		locations = new Vector<Location>();
		transports = new ArrayList<Transport>();
		cargos = new ArrayList<Cargo>();

		initNorthPanel();
		initMainPanel();
		initEastPanel();
		initMenuBar();
	}

	private void initNorthPanel() {
		JLabel problemNameLabel = new JLabel("Problem Name:");
		problemNameField = new JTextField("Problem1");
		problemNameField.setColumns(20);
		problemNameField.addFocusListener(this);
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(problemNameLabel);
		northPanel.add(problemNameField);
		
		add(northPanel, BorderLayout.NORTH);
	}

	private void initMenuBar() {
		saveProblem = new JMenuItem("Save Problem File");
		saveProblem.addActionListener(this);
		saveProblem.setMnemonic(KeyEvent.VK_S);

		saveDomain = new JMenuItem("Save Domain File");
		saveDomain.addActionListener(this);

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(saveProblem);
		fileMenu.add(saveDomain);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}

	private void initEastPanel() {
		cPanel = new CargoInformationPanel(locations, cargos);
		LocationInformationPanel lPanel = new LocationInformationPanel(
				locations);
		lPanel.addActionListener(this);

		JPanel eastPanel = new JPanel(new GridLayout(2, 1));
		eastPanel.add(cPanel);
		eastPanel.add(lPanel);
		add(eastPanel, BorderLayout.EAST);
	}

	private void initMainPanel() {
		tPanel = new TransportsManagementPanel(transports, locations);
		add(tPanel, BorderLayout.CENTER);
	}

	private void saveProblem() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Problem File");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDDL Files",
				".pddl"));
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.getName().lastIndexOf('.') == -1) {
				selectedFile = new File(selectedFile + ".pddl");
			}
			try {
				PDDLWriterUtil.writeProblem(selectedFile, problemName, locations,
						transports, cargos);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error opening "
						+ selectedFile.getName() + " for writing",
						"PDDL Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof Location) {
			Location loc = (Location) source;
			if (e.getActionCommand().equals(
					PDDLWriterConstants.LOCATION_ADD_MESSAGE)) {
				tPanel.addLocation(loc);
			} else if (e.getActionCommand().equals(
					PDDLWriterConstants.LOCATION_UPDATE_MESSAGE)) {
				tPanel.updateLocation(loc);
				cPanel.updateLocation(loc);
			}
		} else if (source == saveProblem) {
			saveProblem();
		} else if (source == saveDomain) {

		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source == problemNameField) {
			String newProb = problemNameField.getText();
			if ((newProb == null) || (newProb.length() <= 0)) {
				JOptionPane.showMessageDialog(this, "Problem name cannot be empty",
						"Problem Definition Error", JOptionPane.ERROR_MESSAGE);
				problemNameField.setText(problemName);
			} else if (Pattern.compile("[^a-zA-Z0-9]").matcher(newProb).find()) {
				JOptionPane.showMessageDialog(this, "Problem name must be alphanumeric only",
						"Problem Definition Error", JOptionPane.ERROR_MESSAGE);
				problemNameField.setText(problemName);
			} else {
				problemName = newProb;
			}
		}		
	}
}
