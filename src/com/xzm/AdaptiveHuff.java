package com.xzm;

import java.util.*;

public class AdaptiveHuff extends AdaptiveTree {
	
	private String symbols = "";
	private String charSent = "";
	private String codeSent = "";	//table 7-4
	private Map<Character, String> initCode = new HashMap<Character, String>();
	
	public AdaptiveHuff() {
		initCode();
	}
	
	/**
	 * Initialize the data string
	 */
	public void initCode() {
		initAdaptiveTree(51);
		symbols = NYT + "ABCD";
		initCode.put(NYT, "0");
		initCode.put('A', "00001");
		initCode.put('B', "00010");
		initCode.put('C', "00011");
		initCode.put('D', "00100");
	}

	public Map<Character, String> getMap(){
		return initCode;
	}

	
	/**
	 * Update the tree
	 * 
	 * @param newchar
	 */
	public void updateTree(char newchar) {
		int current;
		int max;
		
		try {
			// first appearance for symbol
			current = findChar(newchar);
			// Go to symbol external node
			max = highestInBlock(tree[current].count);	// com.xzm.Node number max in block?
			if (current != max && tree[current].parent != max) {
				printMessage("    Swapping nodes " +current+ " and " +max);
				swap(current, max);	// Switch node with highest node in block
				current = max;
			}
			printMessage("    Increasing count for '" +newchar+ "'");
			tree[current].count++;	// Increment node weight
		} catch (NoSuchElementException e) {	// Yes
			printMessage("    Spawning new node for '" +newchar+ "'");
			current = spawn(newchar);	// NYT gives birth to new NYT and external node
			current = tree[current].parent;	// Go to old NYT node
			tree[current].count++;	// Increment count of old NYT node
		}
		
		while (current != root) {	// Is this the root node?
			current = tree[current].parent;	// Go to parent node
			max = highestInBlock(tree[current].count);	// com.xzm.Node number max in block?
			if (current != max && tree[current].parent != max) {
				printMessage("    Swapping nodes " +current+ " and " +max);
				swap(current, max);	// Switch node with highest node in block
				current = max;
			}
			tree[current].count++;	// Increment node weight
		}
	}
	
	/**
	 * Encode
	 * 
	 * @param hold		current char
	 * @return code		code of current char
	 */
	public String encode(char hold) {
		String code;
		try {
			code = hold + "(" + char2code(hold) + ") ";
			printMessage("Character '" + hold + "' FOUND:");
			printMessage("    Sending code for '" +hold+ "'");
		} catch (NoSuchElementException d) {
			printMessage("Character '" + hold + "' not found:");
			code = char2code(NYT);
			if (code.equals("")) {
				code = initCode.get(NYT);
			}
			code = "NEW(" + code + ") " + hold + "(" + initCode.get(hold) + ") ";
			printMessage ("   Sending NYT and character '" + hold + "'");
		}
		return code;
	}
	
	public void printMessage(String mess) {
		System.out.println(mess);
	}
	
	public String run(String charList) {
		charSent = charList;
		int i, j;
		char hold, symbol;
		for (i=0; i<charSent.length(); i++) {
			hold = charSent.charAt(i);	// current char
			
			codeSent += encode(hold);	// 1.encode
			printMessage("*Sequence of symbols and codes sent to the decoder:");
			printMessage("   " + codeSent);
			
			printMessage("Updating the tree:");
			updateTree(hold);	// 2.update
			
			printMessage("*Print the code of each symbol:");
			StringBuilder sb = new StringBuilder();
			for (j=0; j<symbols.length(); j++) {
				symbol = symbols.charAt(j);
				if (symbol == NYT) {
					sb.append("NEW").append("(");
				} else {
					sb.append(symbol).append("(");
				}
				try {
					sb.append(char2code(symbol));
				} catch (NoSuchElementException e) {
					sb.append(initCode.get(symbol));
				}
				sb.append(") ");
			}
			printMessage("   " + sb.toString());
			printMessage("");
		}
		String s = codeSent.replaceAll("[^0-9.]", "");
		return s;
	}

	/**
	 * decode
	 * @param input
	 * @return
	 */
	public String decode(String input){
		initCode();
		Map<Character, String> characterMap = new HashMap<>();
		String res = new String();
		Character symbol, key;
		String code;
		Boolean isFirst = false;
		for( int index= 0 ;index<input.length();){
			String temp = input.substring(index);
			key = null;
			code = null;
			if(!isFirst){
				for (Map.Entry<Character, String> item : characterMap.entrySet()) {
					String value = item.getValue();
					if(temp.startsWith(value)){
						key = item.getKey();
						code = value;
						break;
					}
				}
			}
			if(key==null){
				for (int j=0; j<symbols.length(); j++) {
					symbol = symbols.charAt(j);
					//已经找过NYT，直接跳过
					if(isFirst && symbol==NYT){
						continue;
					}
					String s;
					try{
						s = char2code(symbol);
						if (s.equals("")) {
							s = initCode.get(symbol);
						}
					}catch (NoSuchElementException e){
						s = initCode.get(symbol);
					}
					if(temp.startsWith(s) && !characterMap.containsKey(symbol)) {
						key = symbol;
						code = s;
						break;
					}
				}
			}
			if(key!=NYT){
				res += key;
				updateTree(key);
				characterMap.put(key,null);
				//更新map
				for (Map.Entry<Character, String> item : characterMap.entrySet()) {
					Character item_key = item.getKey();
					String value = char2code(item_key);
					characterMap.put(item_key,value);
				}
				isFirst = false;
			}else{
				isFirst = true;
			}
			index += code.length();
		}
		return res;
	}

}
