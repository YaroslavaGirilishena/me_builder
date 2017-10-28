package com.yg.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a graph structure, 
 * and gets all paths between any two nodes
 * 
 * @author Yaroslava Girilishena
 *
 */
public class Graph {
	
	private List<Vertex> vertices; // list of vertices
    private List<Edge> edges; // list of edges
    
    public List<List<Integer>> adj = new ArrayList<List<Integer>>(); // list of adjacent vertices for each vertex
    public List<List<Vertex>> paths = new ArrayList<List<Vertex>>(); // built paths between specified nodes
    
    
    /**
     * Constructor - empty
     */
    public Graph() {
    	vertices = new ArrayList<Vertex>();
    	initGraph();
    }
    
    /**
     * Constructor - vertices
     * @param vertices - list of vertices
     */
    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
        initGraph();
    }
    
    /**
     * Constructor - vertices & edges
     * @param vertices - list of vertices
     * @param edges - list of edges
     */
    public Graph(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }
    
    /**
     * Init variables
     */
    public void initGraph() {
    	// Init list of adjacent vertices
    	for (int i=0; i < this.vertices.size(); i++) {
    		adj.add(new ArrayList<Integer>());
    	}
    }
    
    /**
     * Add a vertex
     * @param v - vertex
     */
    public void addVertex(Vertex v) {
    	this.vertices.add(v);
    }
    
    /**
     * Print out i-th vertex
     * @param i - index of vertex
     */
    public void displayVertex(int i) { 
    	System.out.print(vertices.get(i).getData()); 
    }
    
    /**
     * Get vertices
     * @return vertices
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Add an edge
     * @param u - from vertex
     * @param v - to vertex
     */
    public void addNewEdge(int u, int v) { 
    	// Add adjacent vertex v to list of adjacent vertices of u
    	adj.get(u).add(v);
    	adj.get(v).add(u);
    }
    
    /**
     * Get edges
     * @return edges
     */
    public List<Edge> getEdges() {
        return edges;
    }
    
    /**
     * Get all possible paths between a source and a destination
     * @param s - index of source vertex
     * @param d - index of destination vertex
     * @return list of paths
     */
    public List<List<Vertex>> getAllPaths(int s, int d) {
        // Mark all the vertices as not visited
    	boolean[] visited = new boolean[d+1];
    	for (int i=0; i<d+1; i++) {
    		visited[i] = false;
    	}
    	
    	// Create a list to store paths
    	List<Vertex> newPath = new ArrayList<Vertex>();
    	for (int i=0; i<this.vertices.size(); i++) {
    		newPath.add(null);
    	}
    	int pathIndex = 0; // init path as empty
    	
    	// Call the recursive helper function to store all paths
    	getAllPathsUtil(s, d, visited, newPath, pathIndex);
    	
    	return paths;
    }
    
    public void getAllPathsUtil(int u, int d, boolean visited[], List<Vertex> path, int pathIndex) {
    	// Mark the current node and store it in a path
    	visited[u] = true;
    	path.set(pathIndex, vertices.get(u));
    	pathIndex++;
    	
    	// If current vertex is same as destination, then store current path
    	if (u == d) {
    		if (path.indexOf(null) != -1) {
    			paths.add(new ArrayList<Vertex>(path.subList(0, path.indexOf(null))));
    		} else {
    			paths.add(new ArrayList<Vertex>(path.subList(0, path.size())));
    		}
    	} else { // If current vertex is not a destination
    		// Recur for all the vertices adjacent to the current vertex
    		for (int i=0; i < adj.get(u).size(); i++) {
    	    	if (visited[adj.get(u).get(i)] == false) {
    				getAllPathsUtil(adj.get(u).get(i), d, visited, path, pathIndex);
    			}
    		}
    	}
    	
    	// Remove current vertex from the path and mark it as unvisited
    	pathIndex--;
    	path.set(pathIndex, null);
    	visited[u] = false;
    }
}
