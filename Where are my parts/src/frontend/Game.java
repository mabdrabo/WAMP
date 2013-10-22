package frontend;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;

import backend.WhereAreMyParts;

public class Game {

	private JFrame frame;
	WhereAreMyParts backend;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Game window = new Game();
					window.frame.setVisible(true);
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
		backend.GenGrid();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, backend.grid.width * 20, (backend.grid.height * 20) + 22);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, backend.grid.width * 20, backend.grid.height * 20);
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(backend.grid.height, backend.grid.width, 0, 0));
		for (int i=0; i<backend.grid.height; i++)
			for (int j=0; j<backend.grid.width; j++) {
				Label l = new Label();
				if (backend.grid.grid[i][j] == 'p') {
					l.setBackground(Color.GREEN);
					panel.add(l);
				}
				else
					if (backend.grid.grid[i][j] == 'o') {
						l.setBackground(Color.YELLOW);
						panel.add(l);
					}
					else
						if (backend.grid.grid[i][j] == 'e') {
							l.setBackground(Color.WHITE);
							panel.add(l);
						}
						else
							if (backend.grid.grid[i][j] == 'f') {
								l.setBackground(Color.RED);
								panel.add(l);
							}
			}
		panel.repaint();
	}

}
