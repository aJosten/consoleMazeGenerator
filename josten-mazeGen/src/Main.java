/*
 * Andrew Josten
 * Assignment 5-maze generator
 */

/**
 * This main runs the maze generation
 * @author Andrew Josten
 *
 */
public class Main {
	public static void main(String args[]) {
		System.out.println("Debugging a 5x5 maze:");
		Maze m = new Maze(5,5,true);
		m.display();
		
		System.out.println("Other examples:\n");
		Maze a = new Maze(6,7,false);
		a.display();
		Maze b = new Maze(5,9,false);
		b.display();
	}
}
