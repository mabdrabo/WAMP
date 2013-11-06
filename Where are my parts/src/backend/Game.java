package backend;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Game {

	private JFrame frmWhereAreMy;
	private JPanel boardPanel;
	WhereAreMyParts backend;
	Timer animator;
	int goalPathIndex = 0; 
	
	
	public void paintBoard(String[][] gridState) {
		frmWhereAreMy.getContentPane().remove(boardPanel);
		boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(gridState.length, gridState[0].length, 0, 0));
		frmWhereAreMy.getContentPane().add(boardPanel);
		
		for (int i=0; i<gridState.length; i++)
			for (int j=0; j<gridState[0].length; j++) {
				Label l = new Label();
				if (gridState[i][j].contains("p")) {
					l.setBackground(Color.GREEN);
					l.setText(gridState[i][j]);
				}
				else
					if (gridState[i][j] == "b") {
						l.setBackground(Color.YELLOW);
					}
					else
						if (gridState[i][j] == "e") {
							l.setBackground(Color.WHITE);
						}
						else
							if (gridState[i][j] == "f") {
								l.setBackground(Color.RED);
							}
				boardPanel.add(l);
			}
		boardPanel.repaint();
	}
	
	public void SearchButton(SearchStrategy strategy) {
		backend = new WhereAreMyParts((Grid) backend.initial_state);
		Object[] returnList = backend.search(backend, strategy, true);
		String msg = "No Solution :(";
		if (returnList != null) {
			msg = "moves: " + ((ArrayList<String>) returnList[0]).toString();			
			msg += "\ncost: " + (int) returnList[1];
			msg += "\nexpansions: " + (int) returnList[2];
		}
		JOptionPane.showMessageDialog(frmWhereAreMy, msg, strategy.toString() + " stats", JOptionPane.INFORMATION_MESSAGE);
		if (backend.goalNode != null) {
			final ArrayList<String[][]> goalPath = new ArrayList<String[][]>();
			Grid node = backend.goalNode;
			goalPath.add(0, node.state);
			while (node.parent != null) {
				node = (Grid) node.parent;
				System.out.println(node);
				goalPath.add(0, node.state);
			}
			animator = new Timer(1000, null);
			animator.setDelay(2000);
			animator.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (goalPathIndex < goalPath.size()) {
						System.out.println("Timer print");
//						System.out.println(goalPath.get(goalPathIndex++));
						paintBoard(goalPath.get(goalPathIndex++));
						boardPanel.revalidate();
						boardPanel.repaint();
					}else {
						boardPanel.revalidate();
						boardPanel.repaint();
						animator.stop();
					}
				}
			});
			animator.start();
		}
//		JDialog stats = new JDialog(frmWhereAreMy, strategy.toString() + "stats");
//		stats.setVisible(true);
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Game window = new Game();
					window.frmWhereAreMy.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Game() {
		backend = new WhereAreMyParts();
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Grid initial_grid = ((Grid) backend.initial_state);
		frmWhereAreMy = new JFrame();
		frmWhereAreMy.setTitle("Where are my parts?!");
//		frmWhereAreMy.setResizable(false);
		frmWhereAreMy.setBounds(100, 100, initial_grid.width * 20 + 200, (initial_grid.height * 20) + 222);
		frmWhereAreMy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWhereAreMy.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		boardPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(boardPanel);
		boardPanel.setLayout(new GridLayout(initial_grid.height, initial_grid.width, 0, 0));
		
		JPanel controlPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(controlPanel);
		
		JButton DFButton = new JButton("Depth First");
		DFButton.setToolTipText("Apply Depth-first-search algorithm to solve problem");
		DFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.DF);
			}
		});
		
		JButton BFButton = new JButton("Breadth First");
		BFButton.setToolTipText("Apply Breadth-first-search algorithm to solve problem");
		BFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.BF);
			}
		});
		
		JButton IDButton = new JButton("Iterative Deepening");
		IDButton.setToolTipText("Apply Iterative-deepening algorithm to solve problem");
		IDButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.ID);
			}
		});
		controlPanel.setLayout(new GridLayout(3, 2, 0, 0));
		controlPanel.add(BFButton);
		controlPanel.add(DFButton);
		controlPanel.add(IDButton);
		
		paintBoard(initial_grid.state);
	}
}
