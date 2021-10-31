package com.einherjarn.buutti1;

import java.io.*;
import java.util.*;

public class Parser {
	//"resources/maze-task-first.txt"
	//"resources/maze-task-second.txt"
	public Vector<Vector<Node>> parse(String file) {
		Vector<Vector<Node>> vec = new Vector<Vector<Node>>();
		vec.add(new Vector<Node>());
		try {
			FileReader input = new FileReader(file);
			int c = input.read();
			int line = 0;
			int col = 0;
			while(c != -1) {
				if(c != 10) {
					//System.out.print((char) c);
					Node n = new Node();
					n.x = col;
					n.y = line;
					n.c = (char) c;
					n.parent = null;
					//System.out.println("["+n.x+","+n.y+"], "+(char) c);
					col++;
					vec.get(line).add(n);
				}else {
					//System.out.print((char) c);
					vec.add(new Vector<Node>());
					line++;
					col = 0;
				}
				c = input.read();
			}
			input.close();
			
			System.out.println("maze parsed");
			
			//System.out.println(vec.get(0).get(7));
		} catch(Exception e) {
			System.out.println(e);
			return null;
		}
		return vec;
	}
}
