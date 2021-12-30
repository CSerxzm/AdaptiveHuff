package com.xzm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AdaptiveHuff extends AdaptiveTree {

    Logger logger = LoggerFactory.getLogger(AdaptiveHuff.class);

	private String symbols = "";
	private String charSent = "";
	private String codeSent = "";
	private Map<Character, String> initCode = new HashMap<Character, String>();
	
	public AdaptiveHuff() {
		initCode();
	}


    /**
     * Initialize the data string
     */
	private void initCode() {
		initAdaptiveTree(100);
		symbols = NYT + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		initCode.put(NYT, "0");
		initCode.put('A', "00001");
		initCode.put('B', "00010");
		initCode.put('C', "00011");
		initCode.put('D', "00100");
		initCode.put('E', "00101");
		initCode.put('F', "00110");
		initCode.put('G', "00111");
		initCode.put('H', "01000");
		initCode.put('I', "01001");
		initCode.put('J', "01010");
		initCode.put('K', "01011");
		initCode.put('L', "01100");
		initCode.put('M', "01101");
		initCode.put('N', "01110");
		initCode.put('O', "01111");
		initCode.put('P', "10000");
		initCode.put('Q', "10001");
		initCode.put('R', "10010");
		initCode.put('S', "10011");
		initCode.put('T', "10100");
		initCode.put('U', "10101");
		initCode.put('V', "10110");
		initCode.put('W', "10111");
		initCode.put('X', "11000");
		initCode.put('Y', "11001");
		initCode.put('Z', "11010");
		
	}

	/**
	 * Update the tree
	 * 
	 * @param newchar
	 */
	private void updateTree(char newchar) {
		int current;
		int max;
		
		try {
			// first appearance for symbol
			current = findChar(newchar);
			// Go to symbol external node
			max = highestInBlock(tree[current].count);
			if (current != max && tree[current].parent != max) {
				printLog("    Swapping nodes " +current+ " and " +max);
                // Switch node with highest node in block
				swap(current, max);
				current = max;
			}
			printLog("    Increasing count for '" +newchar+ "'");
            // Increment node weight
			tree[current].count++;
		} catch (NoSuchElementException e) {
			printLog("    Spawning new node for '" +newchar+ "'");
			current = spawn(newchar);
			current = tree[current].parent;
			tree[current].count++;
		}
		
		while (current != root) {
            // Is this the root node?
            // Go to parent node
			current = tree[current].parent;
			max = highestInBlock(tree[current].count);
			if (current != max && tree[current].parent != max) {
				printLog("    Swapping nodes " +current+ " and " +max);
                // Switch node with highest node in block
				swap(current, max);
				current = max;
			}
            // Increment node weight
			tree[current].count++;
		}
	}
	
	/**
	 * Encode
	 * 
	 * @param hold		current char
	 * @return code		code of current char
	 */
	private String encodeCharacter(char hold) {
		String code;
		try {
			code = hold + "(" + char2code(hold) + ") ";
			printLog("Character '" + hold + "' FOUND:");
			printLog("    Sending code for '" +hold+ "'");
		} catch (NoSuchElementException d) {
			printLog("Character '" + hold + "' not found:");
			code = char2code(NYT);
			if (code.equals("")) {
				code = initCode.get(NYT);
			}
			code = "NEW(" + code + ") " + hold + "(" + initCode.get(hold) + ") ";
			printLog ("   Sending NYT and character '" + hold + "'");
		}
		return code;
	}
	
	private void printLog(String mess) {
		logger.info(mess);
	}
	
	public String encode(String charList) {
		charSent = charList;
		int i, j;
		char hold, symbol;
		for (i=0; i<charSent.length(); i++) {
			hold = charSent.charAt(i);

			//encode
			codeSent += encodeCharacter(hold);
			printLog("*Sequence of symbols and codes sent to the decoder:");
			printLog("   " + codeSent);
			
			printLog("Updating the tree:");
			//update tree
			updateTree(hold);
			
			printLog("*Print the code of each symbol:");
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
			printLog("   " + sb.toString());
			printLog("");
		}
		return codeSent.replaceAll("[^0-9.]", "");
	}

	/**
	 * decode
	 * @param input
	 * @return
	 */
	public String decode(String input){
		initCode();
		Map<Character, String> characterMap = new HashMap<Character, String>();
		String res = new String();
		Character symbol, key;
		String code,s,value;
		Boolean isFirst = false;
		for( int index= 0 ;index<input.length();){
			String temp = input.substring(index);
			key = null;
			code = null;
			if(!isFirst){
			    // is not first appear
				for (Map.Entry<Character, String> item : characterMap.entrySet()) {
					value = item.getValue();
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
					if(isFirst && symbol==NYT){
					    // already has NYT,skip
						continue;
					}
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
				//update
				characterMap.put(key,null);
				for (Map.Entry<Character, String> item : characterMap.entrySet()) {
					Character item_key = item.getKey();
					characterMap.put(item_key,char2code(item_key));
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
