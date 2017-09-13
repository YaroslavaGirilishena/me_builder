package com.yg.graph.dijkstra;

/**
 * Vertex in a graph
 * used by Dijkstra algorithm
 * @author YG
 *
 */
public class Vertex {
	
	private String id;
    private String data;
    private boolean wasVisited;

    public Vertex(String id, String data) {
        this.id = id;
        this.data = data;
        this.setWasVisited(false);
    }
    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return data;
    }
    
	public boolean wasVisited() {
		return wasVisited;
	}
	
	public void setWasVisited(boolean wasVisited) {
		this.wasVisited = wasVisited;
	}
}
