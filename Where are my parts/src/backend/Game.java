package backend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Game {

	private JFrame frmWhereAreMy;
	private JPanel boardContainerPanel;
	private JPanel boardPanel;
	JLabel lblStateName;
	JLabel lblCost;
	JLabel lblexpansions;
	JLabel lblPathToGoal;
	JLabel lblElapsedTime;
	WhereAreMyParts backend;
	Timer animator;
	ArrayList<String[][]> goalPath;
	int goalPathIndex = 0; 


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Grid initial_grid = ((Grid) backend.initial_state);
		frmWhereAreMy = new JFrame();
		frmWhereAreMy.setTitle("Where are my parts?!");
//		frmWhereAreMy.setResizable(false);
		frmWhereAreMy.setBounds(0, 0, (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
//		frmWhereAreMy.setBounds(0, 0, 600, 600);
		frmWhereAreMy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWhereAreMy.getContentPane().setLayout(new BorderLayout(0, 0));
		
		lblStateName = new JLabel("Initial State");
		lblStateName.setHorizontalAlignment(SwingConstants.CENTER);
		lblStateName.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		frmWhereAreMy.getContentPane().add(lblStateName, BorderLayout.NORTH);
		
		JPanel controlPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(controlPanel, BorderLayout.WEST);
		boardContainerPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(boardContainerPanel, BorderLayout.CENTER);
		boardContainerPanel.setLayout(new BoxLayout(boardContainerPanel, BoxLayout.X_AXIS));
		boardPanel = new JPanel(new GridLayout(backend.initial_state.state.length, backend.initial_state.state[0].length));
		boardContainerPanel.add(boardPanel);
		boardPanel.updateUI();
		
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
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		JLabel lblSearchStrategy = new JLabel("Search Strategy");
		lblSearchStrategy.setHorizontalAlignment(SwingConstants.CENTER);
		lblSearchStrategy.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		controlPanel.add(lblSearchStrategy);
		controlPanel.add(BFButton);
		controlPanel.add(DFButton);
		controlPanel.add(IDButton);
		
		JButton btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				visualize();
			}
		});
		
		JButton GR1Button = new JButton("Greedy 1");
		GR1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.GR1);
			}
		});
		controlPanel.add(GR1Button);
		
		JButton GR2Button = new JButton("Greedy 2");
		GR2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.GR2);
			}
		});
		controlPanel.add(GR2Button);
		
		JButton AS1Button = new JButton("A Star 1");
		AS1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.AS1);
			}
		});
		controlPanel.add(AS1Button);
		
		JButton AS2Button = new JButton("A Star 2");
		AS2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SearchButton(SearchStrategy.AS2);
			}
		});
		controlPanel.add(AS2Button);
		controlPanel.add(btnVisualize);
		
		JPanel statsPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(statsPanel, BorderLayout.EAST);
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
		
		JLabel lblStat = new JLabel("Stats.");
		lblStat.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		lblStat.setHorizontalAlignment(SwingConstants.CENTER);
		statsPanel.add(lblStat);
		
		lblCost = new JLabel("Cost");
		statsPanel.add(lblCost);
		
		lblexpansions = new JLabel("#Expansions");
		statsPanel.add(lblexpansions);
		
		lblPathToGoal = new JLabel("Path to Goal");
		statsPanel.add(lblPathToGoal);
		
		lblElapsedTime = new JLabel("Elapsed Time");
		statsPanel.add(lblElapsedTime);
		
		paintBoard(initial_grid.state);
	}

	public void paintBoard(String[][] gridState) {
		boardPanel.removeAll();
		
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
		boardPanel.updateUI();
	}
	
	public void SearchButton(SearchStrategy strategy) {
		backend = new WhereAreMyParts((Grid) backend.initial_state);
		long start_time = System.nanoTime();
		Object[] returnList = backend.search(backend, strategy, true);
		long end_time = System.nanoTime();
		double timeTaken = (end_time - start_time)/1e6;
		lblCost.setText("Cost " + ((returnList == null)? "No Solution" : (int) returnList[1]));
		lblexpansions.setText("#Expansions " + ((returnList == null)? "No Solution" : (int) returnList[2]));
		lblElapsedTime.setText("Elapsed Time " + timeTaken + "ms");
		String moves = "<html>Path to Goal<br>";
		if (returnList == null)
			moves = "No Path To Goal";
		else {
			for (String move : (ArrayList<String>) returnList[0])
				moves += move + "<br>";
			moves += "<br><html>";
			lblPathToGoal.setText(moves);
		}
		if (backend.goalNode != null) {
			goalPath = new ArrayList<String[][]>();
			Grid node = backend.goalNode;
			goalPath.add(0, node.state);
			while (node.parent != null) {
				node = (Grid) node.parent;
				System.out.println(node);
				goalPath.add(0, node.state);
			}
			goalPath.add(0, node.state);

		}
	}
	
	public void visualize() {
		animator = new Timer(0, null);
		animator.setDelay(1000);
		animator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (goalPathIndex < goalPath.size()) {
					if (goalPathIndex == 0)
						lblStateName.setText("Initial State");
					else
						if (goalPathIndex == goalPath.size()-1)
							lblStateName.setText("Goal State");
						else
							lblStateName.setText("State #" + goalPathIndex);
					paintBoard(goalPath.get(goalPathIndex++));
					boardPanel.updateUI();
				}else {
					System.out.println("STOP TIMER");
					animator.stop();
					boardPanel.updateUI();
					goalPathIndex = 0;
				}
			}
		});
		animator.start();
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

	
}
