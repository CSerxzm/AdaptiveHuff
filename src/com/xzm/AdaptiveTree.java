package com.xzm;

import java.util.NoSuchElementException;

public class AdaptiveTree {

	/* Adaptive Tree data members */
	protected Node tree[] = new Node[2*ALPH_SIZE];
	protected int root;
	
	/* Adaptive Tree constants */
	static final int ALPH_SIZE = 127;	// size of the alphabet
	static final char none = (char) 256;	// not a character
	static final char NYT = (char) 257;	// Not Yet transmitted code (NEW: 0)
	
	/**
	 * initiate an Adaptive Tree
	 * 
	 * Creates a single-node tree with the rootnumber as the root
	 */
	public void initAdaptiveTree(int rootnumber) {
		for (int i=0; i < 2*ALPH_SIZE; i++) {
			tree[i] = new Node();
		}
		root = rootnumber;
		tree[root].letter = NYT;
	}
	
	/**
	 * Swap
	 * swaps two nodes in the tree. Make sure its not an ancestor
	 */
	public synchronized void swap(int first, int second) {
		int temp;
		char tempchar;
		
		// swap return pointers (chirlden's parents)
		tree[tree[first].left].parent = second;
		tree[tree[first].right].parent = second;
		tree[tree[second].left].parent = first;
		tree[tree[second].right].parent = first;
		
		// swap left pointers
		temp = tree[second].left;
		tree[second].left = tree[first].left;
		tree[first].left = temp;
		
		// swap right pointers
		temp = tree[second].right;
		tree[second].right = tree[first].right;
		tree[first].right = temp;
		
		// swap data: letter, count
		tempchar = tree[second].letter;
		tree[second].letter = tree[first].letter;
		tree[first].letter = tempchar;
		
		temp = tree[second].count;
		tree[second].count = tree[first].count;
		tree[first].count = temp;
	}
	
	/**
	 * findChar
	 * 
	 * Returns the index of the character
	 */
	public int findChar (char letter) throws NoSuchElementException {
		for (int i=0; i < 2*ALPH_SIZE; i++) {
			if (tree[i].letter == letter) {
				return i;
			}
		}
		throw new NoSuchElementException(" in findChar");
	}
	
	/**
	 * code2char
	 * 
	 * Returns the character associated with the binary code
	 */
	public char code2char(String bincode) throws NoSuchElementException {
		Node current = tree[root];
		
		for (int i=0; i < bincode.length(); i++) {
			// check if current binary digit is valid
			if  (bincode.charAt(i) != '0'  && bincode.charAt(i) != '1') {
				throw new NoSuchElementException("Has a non-binary digit at " + i);
			}
			// go left
			if (bincode.charAt(i) == '0') {
				if (current.left == 0){
					throw new NoSuchElementException();
				} else {
					current = tree[current.left];
				}
			}
			//go right
			else if (bincode.charAt(i) == '1') {
				if (current.right == 0) {
					throw new NoSuchElementException();
				} else{
					current = tree[current.right];
				}
			}
		}
		if (current.letter == none) {
			throw new NoSuchElementException("Not a leaf node");
		} else {
			return current.letter;
		}
	}
	
	/**
	 * char2code
	 * 
	 * Returns the code associated with the character
	 */
	public String char2code(char letter) throws NoSuchElementException {
		StringBuffer bincode = new StringBuffer("");
		Node current = null;
		
		// find the letter
		current = tree[findChar(letter)];
		
		// Make the binary string
		while (current != tree[root]) {
			// is a left child
			if (tree[tree[current.parent].left] == current) {
				bincode.insert(0, '0');
			}
			// is a right child
			else if (tree[tree[current.parent].right] == current) {
				bincode.insert(0, '1');
			}
			// huh?
			else{
				throw new NoSuchElementException ("something is fucked");
			}
			
			// move  up
			current = tree[current.parent];
		}
		return bincode.toString();
	}
	
	/**
	 * spawn
	 * 
	 * Gives birth to new NYT and external node from old NYT node
	 * returns the value of the new NYT node
	 */
	public int spawn (char newchar) {
		int oldNYTindex;
		Node oldNYT;
		
		// Find the current NYT node (NEW node)
		oldNYTindex = findChar(NYT);
		oldNYT = tree[oldNYTindex];
		
		// create new nodes
		oldNYT.letter = none;	// not leaf
		
		oldNYT.right = oldNYTindex - 1;
		tree[oldNYT.right].letter = newchar;
		tree[oldNYT.right].count = 1;
		tree[oldNYT.right].parent = oldNYTindex;
		
		oldNYT.left = oldNYTindex - 2;
		tree[oldNYT.left].letter = NYT;
		tree[oldNYT.left].parent = oldNYTindex;
		
		return oldNYTindex - 2;
	}
	
	/**
	 * HighestInBlock
	 * 
	 * Returns the value of the node that is the highest in the block
	 * i.e. of all nodes with the same count
	 */
	public int highestInBlock (int count) {
		int highest = -1;
		
		for (int i=0; i < 2*ALPH_SIZE; i++) {
			if (tree[i].count == count) {
				highest = i;
			}
		}
		if (highest == -1){
			throw new NoSuchElementException("No such node with count of " + count);
		}
		return highest;
	}
}
