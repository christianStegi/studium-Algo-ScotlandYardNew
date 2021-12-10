package shortestPath;

import shortestPath.directedGraph.*;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.TreeMap;


/**
 * Kürzeste Wege mit A* und Dijkstra 
 * für einen kleinen Beispielgraph (siehe auch ExampleGraph.jpg).
 * Als Kantengewichte und als Heuristik für A* wird der
 * Euklidische Abstand genommen.
 * 
 * @author Oliver Bittel
 * @since 29.10.2018
 */

public class ExampleGraph {
	
	private static class Point {
		int x;
		int y;
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point{" + "x=" + x + ", y=" + y + '}';
		}
		
	}
	
	// Koordinaten für jeden Knoten:
	private static Map<Integer,Point> coords = new TreeMap<>();
	
	// Gerichteter Graph:
	private static DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
	
	// Heuristik:
	private static Heuristic<Integer> heuristic = (v,w) -> dist(v,w);
	
	private static double dist(int v, int w) {
		Point vp = coords.get(v);
		Point wp = coords.get(w);
		return Math.sqrt((vp.x-wp.x)*(vp.x-wp.x) + (vp.y-wp.y)*(vp.y-wp.y));
	}
	
	private static void initializeCoordinates() {
		coords.put(1, new Point( 3,15));
		coords.put(2, new Point( 5,11));
		coords.put(3, new Point( 5, 9));
		coords.put(4, new Point( 9, 9));
		coords.put(5, new Point(16, 9));
		coords.put(6, new Point(19, 9));
		coords.put(7, new Point( 9, 7));
		coords.put(8, new Point(16, 7));
		coords.put(9, new Point( 5, 3));
		coords.put(10,new Point( 0, 0));	
	}

	private static void initializeGraph() {
		addEdgeBothDirections(1, 2);
		addEdgeBothDirections(1,10);	
		addEdgeBothDirections(2, 3);
		addEdgeBothDirections(3, 4);
		addEdgeBothDirections(3, 7);
		addEdgeBothDirections(3, 9);
		addEdgeBothDirections(3,10);
		addEdgeBothDirections(4, 5);
		addEdgeBothDirections(6, 8);
		addEdgeBothDirections(7, 8);
		addEdgeBothDirections(9,10);
	}
	
	private static void addEdgeBothDirections(int v, int w) {
		double d = dist(v,w);
		g.addEdge(v, w, d);
		g.addEdge(w, v, d);
	}

	public static void main(String[] args) throws FileNotFoundException {
		initializeCoordinates();
		initializeGraph();
		
		System.out.println("Dijkstra:");
		ShortestPath<Integer> spDijkstra = new ShortestPath<Integer>(g, null);
		spDijkstra.searchShortestPath(3, 6);
		System.out.print("Shortest Path = " + spDijkstra.getShortestPath());
		System.out.println(" Distance = " + spDijkstra.getDistance());
		// Dijkstra:
		// Besuche Knoten 3 mit d = 0.0
		// Besuche Knoten 2 mit d = 2.0
		// Besuche Knoten 4 mit d = 4.0
		// Besuche Knoten 7 mit d = 4.47213595499958
		// Besuche Knoten 9 mit d = 6.0
		// Besuche Knoten 1 mit d = 6.47213595499958
		// Besuche Knoten 10 mit d = 10.295630140987
		// Besuche Knoten 5 mit d = 11.0
		// Besuche Knoten 8 mit d = 11.47213595499958
		// Besuche Knoten 6 mit d = 15.07768723046357
		// Shortest Path = [3, 7, 8, 6] Distance = 15.07768723046357
		
		System.out.println("\nA-Star:");
		ShortestPath<Integer> spAStar = new ShortestPath<Integer>(g, heuristic);
		spAStar.searchShortestPath(3, 6);
		System.out.print("Shortest Path = " + spAStar.getShortestPath());
		System.out.println(" Distance = " + spAStar.getDistance());
		// A-Star:
		// Besuche Knoten 3 mit d = 0.0
		// Besuche Knoten 4 mit d = 4.0
		// Besuche Knoten 5 mit d = 11.0
		// Besuche Knoten 7 mit d = 4.47213595499958
		// Besuche Knoten 8 mit d = 11.47213595499958
		// Besuche Knoten 6 mit d = 15.07768723046357
		// Shortest Path = [3, 7, 8, 6] Distance = 15.07768723046357

		/* TODO
		*   Ausgabe korrigieren: Shortest Path (SearchShortestPath-Funktion) soll Ausgaben wie in main() erzeugen
		*
		*	dazu vermutlich dijkstra und astern methoden beide in eine Funktion bringen
		* 		also nur die distanz-unterschiede (blaue Zeilen in der Folie) unterschiedlich zuweisen.
		*
		*/
	}	
}