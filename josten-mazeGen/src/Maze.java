import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/*
 * Andrew Josten
 * Assignment 5-maze generator
 */

/**
 * Represents and constructs a maze
 * 
 * 
 * @author Andrew Josten
 *
 */
public class Maze {
	
	boolean[][] matrix;
	//used for solution. 
	boolean[][] graph;
	boolean[] visited;

	int glblWidth;
	
	Random r;
	
	/**
	 * Creates a 2d maze of specified size
	 * @param width 
	 * @param depth
	 * @param debug True: shows the steps of maze creation
	 */
	public Maze(int width, int depth, boolean debug) {
		r = new Random();
		int numNodes = width * depth;
		
		matrix = new boolean[numNodes][numNodes];//adjacency matrix: x by x where x is the total number of vertices
		visited = new boolean[numNodes];//marks each node that has been visited		
		
		glblWidth = width;
		
		//Set up the visited matrix
		for(int i = 0; i < numNodes; i++) {
			visited[i] = false;
		}
		
		//Set up adjacency:
		//Node n (range of 1 to numNodes, inclusive) will be adjacent to 4 others max:
		//(n+1), (n-1), (n+width), (n-width)
		//Exceptions:
		//(n+width) > numNodes: no n+width
		//(n-width) < 1: no n-width
		//(n-1) % 5 = 0: no n-1
		//n % 5 = 0: no n+1		
		for(int i = 0; i < depth; i++) {
			for(int j = 0; j < width; j++) {
				int n = j + (i * width);
				//Right
				if(!((n+1) % width == 0)) {
					matrix[n][n+1] = true;
					matrix[n+1][n] = true;
				}
				
				//Left
				if(!(n % width == 0)) {
					matrix[n][n-1] = true;
					matrix[n-1][n] = true;
				}
				
				//Top
				if(!(n + width >= numNodes)) {
					matrix[n][n+width] = true;
					matrix[n+width][n] = true;
				}
				
				//Bottom
				if(!(n - width < 0)) {
					matrix[n][n-width] = true;
					matrix[n-width][n] = true;
				}
			}
		}
		
		//for solution stuff
		graph = new boolean[numNodes][numNodes];
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix.length; j++) {
				graph[i][j] = matrix[i][j];
			}
		}
		
		DFS(0, debug);		
	}
	
	/**
	 * Runs over entire graph in a randomized depth first search. 
	 * 
	 * @param n
	 */
	private void DFS(int n, boolean debug) {
		//System.out.println(n + " Do stuff");
		//Building the debug:
		//once we reach node/vertex n the maze is done, so we'll keep track of the nodes until then
		ArrayList<Integer> debugPath = new ArrayList<Integer>();
		debugPath.add(n);
		
		//Mark this node as visited
		visited[n] = true;
		
		if(debug) {
			printDebug(debugPath);
		}
		
		Set<Integer> edges = new HashSet<>();//set of all connecting edges to node n
		for(int i = 0; i < matrix.length; i++) {
			if(matrix[n][i] == true) {
				edges.add(i);
			}
		}
		
		//https://www.javacodeexamples.com/get-random-elements-from-java-hashset-example/2765
		Integer[] edgeArray = edges.toArray( new Integer[ edges.size() ] );
		boolean valid = true;
		int next = r.nextInt(edges.size());
		int nextN = edgeArray[next];
				
		//Keep randomly choosing a direction for as long as there is a valid direction to go
		while(valid) {
			valid = false;
			for(int node : edgeArray) {//recheck every time the while loop cycles to see if we can serach again 
				if(!visited[node]) {
					valid = true;
				}
			}
			
			if(!valid) {//break loop, we hit a wall
				return;
			}
			else {
				if(!visited[nextN]) {
					matrix[n][nextN] = false;//kill edge
					matrix[nextN][n] = false;
					DFS(nextN, debug);// nextN is unvisited, go there
				}
				else {
					next = r.nextInt(edges.size());
					nextN = edgeArray[next];//Loop again to try again
				}
			}			
		}
		
		return; //no more nodes		
	}

	private void printDebug(ArrayList<Integer> debugPath){		
		StringBuilder out = new StringBuilder();
		
		//First row
		out.append("X   X");
		for (int i = 0; i < 2*glblWidth-2; i++) {
			out.append(" X");
		}
		out.append("\n");
		
		//Following rows
		int depth = matrix.length/glblWidth;
		for(int i = 0; i < depth; i++) {
			int vert = 0;
			out.append("X");
			for(int j = 0; j < glblWidth; j++) {
				//Vertex
				vert = j + (i * glblWidth);
				if(debugPath.contains(vert)) {
					out.append(" * ");
				}
				else {
					out.append("   ");
				}
				
				//Walls
				if((vert+1 < matrix.length) && matrix[vert][vert+1]) {//there is a connection here, so print a wall
					out.append("X");
				}
				else {
					out.append(" ");//no wall here
				}
			}
			//get rid of fencepsot issue thingy
			out.delete(out.length()-1, out.length());
			out.append("X\n");
			//Add underlying row
			out.append("X");
			for(int k =  0; k < glblWidth; k++) {
				vert = k + (i * glblWidth); 
				if((vert + glblWidth < matrix.length) && !matrix[vert][vert+glblWidth]) {
					out.append("   X");
				}
				else {
					out.append(" X X");
				}
			}
			out.append("\n");
		}
		
		//Make exit port
		out.delete(out.length()-5, out.length());
		out.append("   X\n");
		System.out.println(out.toString());
		
	}	
	
	/**
	 * Creates a path that is the shortest from node 0 to the end
	 * 
	 * https://www.geeksforgeeks.org/shortest-path-unweighted-graph/
	 * https://www.quora.com/How-does-the-shortest-path-in-an-unweighted-graph-work
	 * 
	 * @return A set of all nodes from 0 to end in the shortest path
	 */
	private Set<Integer> findPath() {
		//return this
		Set<Integer> r = new HashSet<>();
		//child, parent
		HashMap<Integer, Integer> parents = new HashMap<>();
		
		//This matrix is used b/c I would remove edges whenever my algorithm knocked down a wall
		//This inverts a copy so I can use the edges to find the shortest path to the end node
		int maxNode = matrix.length;
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix.length; j++) {
				if(matrix[i][j]) {
					graph[i][j] = false;
					graph[j][i] = false;
				}
			}
		}		
		
		//Queue for BFS
		Queue<Integer> q = new LinkedList<>();		
		//keeps track of nodes we've visited
		boolean visit[] = new boolean[maxNode];		
		//[i] stores the distance of vertex i from the source vertex
        int distance[] = new int[maxNode];
        for(int i = 0; i < maxNode; i++) {
        	distance[i] = -1;
        }
        
        visit[0] = true;
        distance[0] = 0;
        q.add(0);
        
        //while the queue has nodes
        while(!q.isEmpty()) {
        	int n = q.poll();        	
        	//get all connected nodes
        	Set<Integer> edges = new HashSet<>();//set of all connecting edges to node n
        	for(int i = 0; i < graph.length; i++) {
    			if(graph[n][i] == true) {
    				edges.add(i);
    			}
    		}
        	//Go over every edge (BFS)
        	for (int e: edges) {
	        	if (distance[e] == -1) {
	        		parents.put(e, n);
	        		distance[e] = distance[n] + 1;
		        	q.add(e);
	        	}
        	}
        }
        
        int child = matrix.length-1;        
        while(child != 0) {
        	r.add(child);
        	child = parents.get(child);
        }        
        r.add(0);        
        return r;        
	}
	
	/**
	 * Prints out the maze and the solution path.
	 * (Note: essentially makes top border, then goes 2 rows at a time)
	 */
	public void display() {
		Set<Integer> sol = findPath();
		System.out.println();		
		
		int maxNode = matrix.length;		
		//use matrix[][]
		//where there is an edge between two nodes, print a wall
		StringBuilder out = new StringBuilder();
		
		//First row
		out.append("X   X");
		for (int i = 0; i < 2*glblWidth-2; i++) {
			out.append(" X");
		}
		out.append("\n");
		
		//Following rows
		int depth = matrix.length/glblWidth;
		for(int i = 0; i < depth; i++) {
			int n = 0;
			out.append("X");
			for(int j = 0; j < glblWidth; j++) {
				n = j + (i * glblWidth);
				
				if(sol.contains(n)) {
					out.append(" ^ ");
				}
				else {
					out.append("   ");
				}
				if((n+1 < maxNode) && matrix[n][n+1]) {//there is a connection here, so print a wall
					out.append("X");
				}
				else {
					out.append(" ");//no wall here
				}
			}
			//get rid of fencepsot issue thingy
			out.delete(out.length()-1, out.length());
			out.append("X\n");
			//Add underlying row
			out.append("X");
			for(int k =  0; k < glblWidth; k++) {
				n = k + (i * glblWidth); 
				if((n + glblWidth < maxNode) && !matrix[n][n+glblWidth]) {
					out.append("   X");
				}
				else {
					out.append(" X X");
				}
			}
			out.append("\n");
		}
		
		//Make exit port
		out.delete(out.length()-5, out.length());
		out.append("   X\n");
		System.out.println(out.toString());
	}
}