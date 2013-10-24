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
		frmWhereAreMy = new JFrame();
		frmWhereAreMy.setTitle("Where are my parts?!");
		frmWhereAreMy.setResizable(false);
		frmWhereAreMy.setBounds(100, 100, backend.grid.width * 20 + 200, (backend.grid.height * 20) + 222);
		frmWhereAreMy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWhereAreMy.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel boardPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(boardPanel);
		boardPanel.setLayout(new GridLayout(backend.grid.height, backend.grid.width, 0, 0));
		
		JPanel controlPanel = new JPanel();
		frmWhereAreMy.getContentPane().add(controlPanel);
		
		JButton BFButton = new JButton("Breadth First");
		BFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backend.search(backend, SearchStrategy.BF, true);
			}
		});
		controlPanel.add(BFButton);
		for (int i=0; i<backend.grid.height; i++)
			for (int j=0; j<backend.grid.width; j++) {
				Label l = new Label();
				if (backend.grid.grid[i][j].contains("p")) {
					l.setBackground(Color.GREEN);
					l.setText(backend.grid.grid[i][j]);
					boardPanel.add(l);
				}
				else
					if (backend.grid.grid[i][j] == "o") {
						l.setBackground(Color.YELLOW);
						boardPanel.add(l);
					}
					else
						if (backend.grid.grid[i][j] == "e") {
							l.setBackground(Color.WHITE);
							boardPanel.add(l);
						}
						else
							if (backend.grid.grid[i][j] == "f") {
								l.setBackground(Color.RED);
								boardPanel.add(l);
							}
			}
		boardPanel.repaint();
	}
}
