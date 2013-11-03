package backend;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Game {

	private JFrame frmWhereAreMy;
	WhereAreMyParts backend;
	
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
		Grid random_grid = ((Grid) backend.initial_state);
		frmWhereAreMy = new JFrame();
		frmWhereAreMy.setTitle("Where are my parts?!");
		frmWhereAreMy.setResizable(false);
		frmWhereAreMy.setBounds(100, 100, random_grid.width * 20 + 200, (random_grid.height * 20) + 222);
		frmWhereAreMy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWhereAreMy.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel boardPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(boardPanel);
		boardPanel.setLayout(new GridLayout(random_grid.height, random_grid.width, 0, 0));
		
		JPanel controlPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(controlPanel);
		
		JButton BFButton = new JButton("Breadth First");
		BFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backend.state_space = new ArrayList<SearchTreeNode>();
				backend.state_space.add(backend.initial_state);
				backend.search(backend, SearchStrategy.BF, true);
			}
		});
		controlPanel.add(BFButton);
		
		JButton DFButton = new JButton("Depth First");
		DFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backend.state_space = new ArrayList<SearchTreeNode>();
				backend.state_space.add(backend.initial_state);
				backend.search(backend, SearchStrategy.DF, true);
			}
		});
		controlPanel.add(DFButton);
		for (int i=0; i<random_grid.height; i++)
			for (int j=0; j<random_grid.width; j++) {
				Label l = new Label();
				if (random_grid.state[i][j].contains("p")) {
					l.setBackground(Color.GREEN);
					l.setText(random_grid.state[i][j]);
					boardPanel.add(l);
				}
				else
					if (random_grid.state[i][j] == "b") {
						l.setBackground(Color.YELLOW);
						boardPanel.add(l);
					}
					else
						if (random_grid.state[i][j] == "e") {
							l.setBackground(Color.WHITE);
							boardPanel.add(l);
						}
						else
							if (random_grid.state[i][j] == "f") {
								l.setBackground(Color.RED);
								boardPanel.add(l);
							}
			}
		boardPanel.repaint();
	}
}
