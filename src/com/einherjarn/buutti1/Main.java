package com.einherjarn.buutti1;

import java.util.Vector;

public class Main {

	public static void main(String[] args) {
		Parser parser = new Parser();
		Vector<String> maps = new Vector<String>();
		maps.add("resources/maze-task-first.txt");
		maps.add("resources/maze-task-second.txt");
		Vector<Integer> stepcounts = new Vector<Integer>();
		stepcounts.add(20);
		stepcounts.add(150);
		stepcounts.add(200);
		
		// for every map
		for(int map=0; map<maps.size(); map++) {
			Vector<Vector<Node>> nodes = parser.parse(maps.get(map));
			System.out.println("pathing map: "+maps.get(map));
			
			// find entrypoint and all exits
			int entryrow = 0;
			int entrycol = 0;
			//System.out.println(nodes.size());
			//System.out.println(nodes.get(0).size());
			Vector<Node> exits = new Vector<Node>();
			for(int i=0; i<nodes.size(); i++) {
				for(int j = 0; j < nodes.get(i).size(); j++) {
					if(nodes.get(i).get(j).c == '^') {
						entryrow = j;
						entrycol = i;
					} else if(nodes.get(i).get(j).c == 'E'){
						exits.add(nodes.get(i).get(j));
						System.out.println("exit: "+i+","+j);
					}
				}
			}
			
			// A* for all exits
			for(int i=0; i<exits.size(); i++) {
				Vector<Node> open = new Vector<Node>();
				Vector<Node> closed = new Vector<Node>();
				Node n = nodes.get(entrycol).get(entryrow);
				n.g = 0;
				n.h = findheurestic(n, exits.get(i));
				n.f = n.g + n.h;
				open.add(n);
				// while open not empty
				while(open.size() > 0) {
					//System.out.println("open: "+open.size());
					//System.out.println("closed: "+closed.size());
					Node m = open.get(findlowest(open));
					if(m.x == exits.get(i).x && m.y == exits.get(i).y) {
						System.out.println("found");
						int length = generatepath(nodes, exits.get(i));
						printboard(nodes);
						System.out.print("path length: "+length+". Valid for: ");
						if(length <= 20) {
							System.out.print(" 20");
						}
						if(length <= 150) {
							System.out.print(" 150");
						}
						if(length <= 200) {
							System.out.print(" 200");
						}
						System.out.println();
						//return;
					}
					open.remove(m);
					closed.add(m);
					Vector<Node> neibs = new Vector<Node>();
					//printboarddebug(nodes, open, closed, neibs, m);
					neibs = getneibs(nodes, m);
					for(int j=0; j<neibs.size(); j++) {
						if(closed.contains(neibs.get(j))) {
							continue;
						}
						if(open.contains(neibs.get(j)) && neibs.get(j).f < neibs.get(j).g) {
							open.remove(neibs.get(j));
						}
						if(closed.contains(neibs.get(j)) && neibs.get(j).f < neibs.get(j).g) {
							closed.remove(neibs.get(j));
						}
						if(!(open.contains(neibs.get(j))) && !(closed.contains(neibs.get(j)))) {
							//System.out.println(neibs.get(j).x+", "+neibs.get(j).y);
							open.add(neibs.get(j));
							neibs.get(j).g = m.g + 1;
							neibs.get(j).h = findheurestic(neibs.get(j), exits.get(i));
							neibs.get(j).f = neibs.get(j).g + neibs.get(j).h;
							neibs.get(j).parent = m;
						}
					}
				}
				System.out.println("finished for exit "+i);
				nodes = parser.parse("resources/maze-task-first.txt");
			}
		}
		
	}
	
	// find lowest f value of nodes as int place in vec
	private static int findlowest(Vector<Node> vec) {
		//System.out.println("findlow() vec size " +vec.size());
		int low=vec.get(0).f;
		int node=0;
		for(int i=0; i<vec.size(); i++) {
			if(vec.get(i).f < low) {
				low = vec.get(i).f;
				node = i;
			}
		}
		return node;
	}
	
	// get neighbours of node
	private static Vector<Node> getneibs(Vector<Vector<Node>> nodes, Node node) {
		Vector<Node> neibs = new Vector<Node>();
		for(int x=-1; x<=1; x=x+2) {
			int row = node.y;
			int col = node.x+x;
			if(nodes.get(0).size()-1 >= col && col >= 0) {
				if(nodes.get(row).get(col).c != '#') {
					neibs.add(nodes.get(row).get(col));
					//nodes.get(row).get(col).parent = node;
				}
			}
		}
		
		for(int y=-1; y<=1; y=y+2) {
			int row = node.y+y;
			int col = node.x;
			if(nodes.size()-2 >= row && row >= 0) {
				if(nodes.get(row).get(col).c != '#') {
					neibs.add(nodes.get(row).get(col));
					//nodes.get(row).get(col).parent = node;
				}
			}
		}
		//System.out.println("neibs found: "+neibs.size());
		return neibs;
	}
	
	// calculate heuristic for given node/exit
	private static int findheurestic(Node node, Node exit) {
		int i = Math.abs(node.x - exit.x) + Math.abs(node.y - exit.y);
		return i;
	}
	
	// print current status of algorithm for debugging
	private static void printboarddebug(Vector<Vector<Node>> nodes, Vector<Node> open, Vector<Node> closed, Vector<Node> neibs, Node m) {
		for(int i=0; i<nodes.size(); i++) {
			System.out.println();
			for(int j = 0; j < nodes.get(i).size(); j++) {
				if(nodes.get(i).get(j) == m) {
					System.out.print("M");
				}else if(open.contains(nodes.get(i).get(j))) {
					System.out.print("O");
				}else if(closed.contains(nodes.get(i).get(j))) {
					System.out.print("C");
				}else if(neibs.contains(nodes.get(i).get(j))) {
					System.out.print("N");
				}else {
					System.out.print(nodes.get(i).get(j).c);
				}
			}
		}
	}
	
	// walk back nodes from exit to show path
	private static int generatepath(Vector<Vector<Node>> nodes, Node exit) {
		if(exit != null) {
			nodes.get(exit.y).get(exit.x).c = 'X';
			//System.out.println(exit.y+", "+exit.x);
			return generatepath(nodes, exit.parent)+1;
		}
		return 1;
	}
	
	// print board chars
	private static void printboard(Vector<Vector<Node>> nodes) {
		for(int i=0; i<nodes.size(); i++) {
			System.out.println();
			for(int j = 0; j < nodes.get(i).size(); j++) {
				System.out.print(nodes.get(i).get(j).c);
			}
		}
	}
	
	// reset node parents
	private static void resetparents(Vector<Vector<Node>> nodes) {
		for(int i=0; i<nodes.size(); i++) {
			for(int j = 0; j < nodes.get(i).size(); j++) {
				nodes.get(i).get(j).parent = null;
			}
		}
	}
}
