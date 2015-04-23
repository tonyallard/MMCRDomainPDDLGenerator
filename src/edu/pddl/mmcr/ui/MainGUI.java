package edu.pddl.mmcr.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.pddl.mmcr.controller.Controller;
import edu.pddl.mmcr.exception.PDDLModelIncompleteException;
import edu.pddl.mmcr.util.PDDLWriterUtil;

public class MainGUI extends JFrame implements ActionListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6534047709842754324L;

	private JMenuItem newProblem = null;
	private JMenuItem saveProblem = null;
	private JMenuItem saveDomain = null;
	private JMenuItem openProblem = null;
	private JMenuItem exit = null;
	private JTextField problemNameField = null;
	private JFileChooser fileChooser = null;

	private Controller controller = null;

	public MainGUI() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final MainGUI me = this;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				if (me.checkCanDiscardChanges("Are you sure you would like to quit?")) {
					me.dispose();
				}
			}
		});
		setTitle("MMCR Domain PDDL Builder");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		fileChooser = new JFileChooser();

		this.controller = new Controller();

		initNorthPanel();
		initMainPanel();
		initSouthPanel();
		initMenuBar();
	}

	private boolean checkCanDiscardChanges(String question) {
		if (controller.pendingChanges()) {
			int selection = JOptionPane.showConfirmDialog(
					this,
					"There are unsaved changes to "
							+ controller.getProblemName() + ". " + question,
					"Quit?", JOptionPane.YES_NO_OPTION);
			if (selection == JOptionPane.NO_OPTION) {
				return false;
			}
		}
		return true;
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
		newProblem = new JMenuItem("New Problem File", KeyEvent.VK_N);
		newProblem.addActionListener(this);

		saveProblem = new JMenuItem("Save Problem File", KeyEvent.VK_S);
		saveProblem.addActionListener(this);

		saveDomain = new JMenuItem("Save Domain File", KeyEvent.VK_D);
		saveDomain.addActionListener(this);

		openProblem = new JMenuItem("Open Problem File", KeyEvent.VK_O);
		openProblem.addActionListener(this);

		exit = new JMenuItem("Exit", KeyEvent.VK_X);
		exit.addActionListener(this);

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(newProblem);
		fileMenu.addSeparator();
		fileMenu.add(openProblem);
		fileMenu.addSeparator();
		fileMenu.add(saveProblem);
		fileMenu.add(saveDomain);
		fileMenu.addSeparator();
		fileMenu.add(exit);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}

	private void initSouthPanel() {
		CargoInformationPanel cPanel = new CargoInformationPanel(controller);
		LocationInformationPanel lPanel = new LocationInformationPanel(
				controller);
		JPanel eastPanel = new JPanel(new GridLayout(1, 2));
		eastPanel.add(cPanel);
		eastPanel.add(lPanel);
		add(eastPanel, BorderLayout.SOUTH);
	}

	private void initMainPanel() {
		VehicleManagementPanel tPanel = new VehicleManagementPanel(controller);
		add(tPanel, BorderLayout.CENTER);
	}

	private void saveProblem() {
		fileChooser.setDialogTitle("Save Problem File");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDDL Files",
				"pddl"));
		fileChooser.setSelectedFile(new File(problemNameField.getText()
				+ ".pddl"));
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.exists()) {
				int option = JOptionPane.showConfirmDialog(this, "Are you sure you would like to overwrite "
						+ selectedFile.getName(), "File Exists!",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option == JOptionPane.NO_OPTION) {
					return;
				}
			}
			else if (selectedFile.getName().lastIndexOf('.') == -1) {
				selectedFile = new File(selectedFile + ".pddl");
			}
			try {
				controller.save(selectedFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error opening "
						+ selectedFile.getName() + " for writing",
						"PDDL Write Error", JOptionPane.ERROR_MESSAGE);
			} catch (PDDLModelIncompleteException e) {
				JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
						"PDDL Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveDomain() {
		fileChooser.setDialogTitle("Save Problem File");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDDL Files",
				"pddl"));
		fileChooser.setSelectedFile(new File("MMCRDomain.pddl"));
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.getName().lastIndexOf('.') == -1) {
				selectedFile = new File(selectedFile + ".pddl");
			}
			try {
				PDDLWriterUtil.writeDomain(selectedFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error opening "
						+ selectedFile.getName() + " for writing",
						"PDDL Write Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void openProblem() {
		if (checkCanDiscardChanges("Are you sure you would like to open a file?")) {
			fileChooser.setDialogTitle("Open Problem File");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileFilter(new FileNameExtensionFilter("PDDL Files",
					"pddl"));
			int returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				Controller oldController = controller;
				try {
					// Load new controller
					controller = new Controller(selectedFile);
					restart();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Error reading "
							+ selectedFile.getName(), "PDDL Write Error",
							JOptionPane.ERROR_MESSAGE);
					controller = oldController;
				}
			}
		}
	}

	private void newProblem() {
		if (checkCanDiscardChanges("Are you sure you would like to create a new file?")) {
			controller = new Controller();
			restart();
		}
	}

	private void restart() {
		// reset the GUI
		getContentPane().removeAll();
		initNorthPanel();
		initMainPanel();
		initSouthPanel();
		revalidate();
		// Hack to get everything to display again.
		controller.invokeCreationEvents();
		problemNameField.setText(controller.getProblemName());
	}

	private void exit() {
		if (checkCanDiscardChanges("Are you sure you would like to quit?")) {
			this.dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == saveProblem) {
			saveProblem();
		} else if (source == saveDomain) {
			saveDomain();
		} else if (source == openProblem) {
			openProblem();
		} else if (source == exit) {
			exit();
		} else if (source == newProblem) {
			newProblem();
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
				JOptionPane.showMessageDialog(this,
						"Problem name cannot be empty",
						"Problem Definition Error", JOptionPane.ERROR_MESSAGE);
				problemNameField.setText(controller.getProblemName());
			} else if (Pattern.compile("[^a-zA-Z0-9]").matcher(newProb).find()) {
				JOptionPane.showMessageDialog(this,
						"Problem name must be alphanumeric only",
						"Problem Definition Error", JOptionPane.ERROR_MESSAGE);
				problemNameField.setText(controller.getProblemName());
			} else {
				controller.setProblemName(newProb);
			}
		}
	}
}
