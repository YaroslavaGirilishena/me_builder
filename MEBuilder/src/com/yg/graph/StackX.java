package com.yg.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Stack of vertices
 * used by Graph class
 * @author YG
 *
 */
public class StackX {
	private List<Integer> stack;
	
	public StackX() {
		stack = new ArrayList<Integer>();
	}
	
	public void push(int j) { // put item on stack
		stack.add(j);
	}
	
	public int pop() { // take item off stack 
		int v = stack.get(stack.size()-1);
		stack.remove(stack.size()-1);
		return v;
	}

	public int peek() { // peek at top of stack
		return stack.get(stack.size()-1);
	}
	
	public boolean isEmpty() { // true if nothing on stack 
		return stack.isEmpty();
	}
	
	public int getSize() { // get the size of a stack
		return stack.size();
	}
	
	public List<Integer> getStack() {
		return stack;
	}
}
