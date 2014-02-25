package edu.pddl.logistics;

import edu.pddl.logistics.ui.MainGUI;

public class Launcher {
	private Launcher()
	{}
	
	public static void main (String [] args) {
		MainGUI gui = new MainGUI();
		gui.setVisible(true);
	}
}
