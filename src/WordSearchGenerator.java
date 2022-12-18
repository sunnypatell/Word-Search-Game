/**
 * WordSearchGenerator.java
 * @author Sunny Patel
 * ICS4U Java Review Assignment - Word Search Generator Project
 * February 27, 2022
 * @version Java 1.0 (SE 17)
 */

//Imports for the GUI Dialog interface (Abstract Window Toolkit).
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Point;

//Imports for Java IO Package (Input and Output System).
import java.io.FileNotFoundException;
import java.io.FileWriter;

//Imports for Java Utilities Package.
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This is the class that runs the puzzle generator algorithm and the GUI Interface.
 * @author Sunny Patel
 * @version 1.0
 */
public class WordSearchGenerator {
	
	/** 
	 * @param args
	 */
	public static void main(String[] args) {

		/** This large chunk of code is to create and initizale the input system and GUI interface to allow the user
		 * to input array (puzzle size), wordlist, unsolved, and solved worsearch. */
		
		//Initializing the graphical components
		JFrame f = new JFrame();
		Dimension size = new Dimension(350,350);
		JPanel panel = new JPanel();

		//Setting up the frame.
		f.setSize(size.width, size.height);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(panel);
		f.setTitle("Word Search Generator");
		
		//Setting up the panel
		//The layout determines how components will be added
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		//Setting up the text field to enter the grid size.
		JTextField number = new JTextField("Size of puzzle, please enter one number (n x n)");
		number.setSize(100, 50);
		number.setColumns(15);
		number.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				number.setText("");
			}
		});
		
		panel.add(number);

		//Setting up the text field to enter the path to the wordlist.
		JTextField wfile = new JTextField("Enter filename For Wordlist (Input Extension)");
		wfile.setSize(100, 50);
		wfile.setColumns(15);
		wfile.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				wfile.setText("");
			}
		});
		
		panel.add(wfile);

		// Setting up the text field to enter the path to the unsolved output.
		JTextField unfile = new JTextField("Enter filename For New Unsolved Puzzle (Input Extension)");
		unfile.setSize(100, 50);
		unfile.setColumns(15);
		unfile.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				unfile.setText("");
			}
		});
		
		panel.add(unfile);
		
		// Setting up the text field to enter the path to the solved output.
		JTextField sfile = new JTextField("Enter filename For Solved Puzzle (Input Extension)");
		sfile.setSize(100, 50);
		sfile.setColumns(15);
		sfile.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sfile.setText("");
			}
		});
		
		panel.add(sfile);
		
		//Setting up the button to generate the puzzle.
		JButton confirm = new JButton("Create Wordsearch Puzzle");
		confirm.setSize(100,50);
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = -1;
				//Checking if the inputs are in the correct format.
				try {
					n = Integer.parseInt(number.getText());
				}catch(Exception exc) {
					number.setText("Invalid input");
				}
				
				if(n != -1) {
					String[] files = {wfile.getText(), unfile.getText(), sfile.getText()};
					JTextField[] tfs = {wfile, unfile, sfile};
					//checking if the wordlist path exists or not.
					boolean valid = true;
					if(!new File(files[0]).exists()) {
						tfs[0].setText("Invalid path");
						valid = false;
					}
					
					if(valid) {
						WordSearchGenerator wsg = new WordSearchGenerator(n, files[0], files[1], files[2]);
						JOptionPane.showMessageDialog(panel, "The unsolved and solved output files are created.");
						System.exit(0);
					}
				}
			}
		});
		
		panel.add(confirm);
		
		f.setVisible(true);
	}


	
	private char[][] puzzle;
	private ArrayList<String> words = new ArrayList<>();
	
	private String unsolved = "puzzle.txt";
	private String solved = "solution.txt";
	
	/**
	 * The default constructor
	 * @param n The grid size.
	 * @param wordlist The path to the wordlist
	 * @param unsolved The path of the unsolved output file.
	 * @param solved The path to the solved output file.
	 */
	public WordSearchGenerator(int n, String wordlist, String unsolved, String solved) {
		
		//Loading the words from the wordlist.
		loadWords(wordlist);
		
		//Initializing global variables.
		this.unsolved = unsolved;
		this.solved = solved;
		puzzle = new char[n][n];
		
		//An array list containing the empty spaces of the grid
		ArrayList<Point> vacant = new ArrayList<Point>();
		
		//Initializing the puzzle
		for(int x = 0; x<puzzle.length; x++) {
			for(int y = 0; y<puzzle[0].length; y++) {
				puzzle[y][x] = ' ';
				vacant.add(new Point(x, y));
			}
		}
		
		generatePuzzle(vacant);
	}
	
	/**
	 * This method loads the words from the wordlist.
	 * @param path The wordlist path.
	 */
	private void loadWords(String path) {
		File f = new File(path);
		try {
			Scanner fs = new Scanner(f);
			while(fs.hasNextLine()) {
				words.add(fs.nextLine());
			}
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method uses the other available methods to generate a puzzle. 
	 * @param vacant The list of vacant spaces in grid.
	 */
	public void generatePuzzle(ArrayList<Point> vacant) {
		try {
			// Here we go through each word one by one and randomly position it on the grid.
			for(String word : words) {
				ArrayList<Point[]> possiblepos = getPossiblePositions(word);
				int idx = (int)(Math.random() * possiblepos.size());
				Point pos = possiblepos.get(idx)[0];
				Point dir = possiblepos.get(idx)[1];
				for(int i = 0; i<word.length(); i++) {
					puzzle[pos.y][pos.x] = word.charAt(i);
					vacant.remove(pos);
					pos.x += dir.x;
					pos.y += dir.y;
				}
			}
			
			// Now the puzzle has the solution words in their random positions.
			// Therefore the puzzle is currently in solved state.
			
			// The solved puzzle is saved.
			displayPuzzle();
			savePuzzle(solved);
			
			// The remaining vacant spaces are filled with random letters from the alphabet.
			fillPuzzle(vacant);
			
			// The unsolved puzzle is saved.
			displayPuzzle();
			savePuzzle(unsolved);
			
		}catch(Exception e) {
			// If the random configuration was faulty, or invalid, the puzzle is generated again.
			clearPuzzle();
			generatePuzzle(vacant);
		}
	}
	
	/**
	 * This method saves the current state of the puzzle in a given file path.
	 * @param path The path of the file to save the puzzle to.
	 */
	public void savePuzzle(String path) {
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(getPuzzleString());
			fw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method gives a text representation of the puzzle
	 * @return The text representation
	 */
	public String getPuzzleString() {
		String ret = "";
		for(char[] ch : puzzle) {
			for(char c : ch) {
				ret += c+" ";
			}
			ret += "\n";
		}
		return ret;
	}
	
	/**
	 * This method prints the current state of the puzzle on the console.
	 */
	public void displayPuzzle() {
		for(char[] c : puzzle) {
			System.out.println(Arrays.toString(c));
		}
		System.out.println();
	}
	
	/**
	 * This method fills the vacant spaces of the grid with random letters of the alphabet.
	 * @param vacant The list of vacant spaces in the grid.
	 */
	public void fillPuzzle(ArrayList<Point> vacant) {
		char[] alphabet = "abcdefghijklmnopqstuvwxyz".toCharArray(); // Creating an array of a-z in the alphabet for filling array
		int idx = 0;
		while(vacant.size() > 0) {
			Point ran = vacant.get((int)(Math.random() * vacant.size()));
			char ranchar = alphabet[idx];
			puzzle[ran.y][ran.x] = ranchar;
			vacant.remove(ran);
			idx += 1;
			if(idx > alphabet.length-1) idx = 0;
		}
	}
	
	/**
	 * This method reinitializes the puzzle grid.
	 */
	public void clearPuzzle() {
		puzzle = new char[puzzle.length][puzzle.length];
		
		for(int x = 0; x<puzzle.length; x++) {
			for(int y = 0; y<puzzle[0].length; y++) {
				puzzle[y][x] = ' ';
			}
		}
		
	}
	
	/**
	 * This method returns the possible number places and configurations a word can be put in the
	 * current state of the puzzle grid.
	 * @param w The word to put
	 * @return An ArrayList of an array of points where, [0] = the position and [1] = the direction
	 */
	public ArrayList<Point[]> getPossiblePositions(String w) {
		ArrayList<Point[]> ret = new ArrayList<>();
		
		//This is done by going through each of the grid space and checking in each direction possible
		//If the word can be fitted in that direction.
		/**For Example... Essentially, at a specific position e.g. (0, 0), and a direction
		 * which is the x and y pos. offset e.g. (1, 1). It takes a character from a word (from input file),
		 * places a characters, then moves 1 step by the offset, places another character if applicable.
		 * If it is not applicable, it will try to rearrange the word in a different direction. 
		 * If the program is able to put all the characters down, it is a valid configuration. 
		 */
		for(int x = 0; x<puzzle.length; x++) {
			for(int y = 0; y<puzzle[0].length; y++) {
				Point[] offsets = {
					new Point(1,0),
					new Point(-1,0),
					new Point(0,1),
					new Point(0,-1),
					new Point(1,1),
					new Point(1,-1),
					new Point(-1,-1),
					new Point(-1,1)
				};
				
				for(Point p : offsets) {
					Point point = new Point(x,y);
					int lenleft = w.length();
					for(int i = 0; i < w.length(); i++) {
						if(point.x >= 0 && point.x <= puzzle.length-1 && point.y >= 0 && point.y <= puzzle.length-1 &&
								(puzzle[point.y][point.x] == ' ' || puzzle[point.y][point.x] == w.charAt(i))) {
							lenleft --;
						}else {	
							break;
						}
						point.x += p.x;
						point.y += p.y;
					}
					if(lenleft == 0) {
						ret.add(new Point[] {new Point(x,y), p});
					}
				}
			}
		}
		
		return ret;
	}
	
}



