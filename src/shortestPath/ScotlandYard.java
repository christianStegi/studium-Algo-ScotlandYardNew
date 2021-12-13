package shortestPath;

import java.awt.*;
import java.io.FileNotFoundException;

import shortestPath.directedGraph.*;
import SYSimulation.SYSimulation.src.sim.SYSimulation;
import SYSimulation.SYSimulation.src.sim.SYDemo;

import java.io.IOException;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


/**
 * Kürzeste Wege im Scotland-Yard Spielplan mit A* und Dijkstra.
 * @author Oliver Bittel
 * @since 27.02.2019
 */
public class ScotlandYard {

	/**
	 * Fabrikmethode zur Erzeugung eines gerichteten Graphens für den Scotland-Yard-Spielplan.
	 * <p>
	 * Liest die Verbindungsdaten von der Datei ScotlandYard_Kanten.txt.
	 * Für die Verbindungen werden folgende Gewichte angenommen:
	 * U-Bahn = 5, Taxi = 2 und Bus = 3.
	 * Falls Knotenverbindungen unterschiedliche Beförderungsmittel gestatten,
	 * wird das billigste Beförderungsmittel gewählt. 
	 * Bei einer Vebindung von u nach v wird in den gerichteten Graph sowohl 
	 * eine Kante von u nach v als auch von v nach u eingetragen.
	 * @return Gerichteter und Gewichteter Graph für Scotland-Yard.
	 * @throws FileNotFoundException
	 */
	public static DirectedGraph<Integer> getGraph() throws FileNotFoundException {

		DirectedGraph<Integer> sy_graph = new AdjacencyListDirectedGraph<>();
		File fileKanten =new File("src/shortestPath/ScotlandYard_Kanten.txt");
		Scanner inKanten = new Scanner(fileKanten);
		File fileKnoten =new File("src/shortestPath/ScotlandYard_Knoten.txt");
		Scanner inKnoten = new Scanner(fileKnoten);

		Map<Integer, Point> knotenMap = new TreeMap<>();

		/* hier Knoten aus Datei in eine Datenstruktur einlesen */
		while ( inKnoten.hasNext() ) {

			String lineKnoten = inKnoten.nextLine();
			/* splitte die Zeilen entweder bei \t oder einem Leerzeichen */
			String[] knotenPerLine = lineKnoten.split("(\t| )");
			int knoten = Integer.parseInt(knotenPerLine[0]);
			int coord_x = Integer.parseInt(knotenPerLine[1]);
			int coord_y = Integer.parseInt(knotenPerLine[2]);

			Point coordinaten = new Point(coord_x, coord_y);
			knotenMap.put(knoten, coordinaten);

		}

		/* hier Kanten aus Datei auslesen und zum graph hinzufügen */
		while ( inKanten.hasNext() ) {

			String lineKanten = inKanten.nextLine();
			String[] kantenPerLine = lineKanten.split(" ");

			int knotenV = Integer.parseInt(kantenPerLine[0]);
			int knotenW = Integer.parseInt(kantenPerLine[1]);
			String verkehrsmittel = kantenPerLine[2];

			Heuristic<Point> heuristic = (v,w) -> dist(v,w);
			double weight = heuristic.estimatedCost((knotenMap.get(knotenV)), knotenMap.get(knotenW));

			sy_graph.addEdge(knotenV, knotenW, weight);
			sy_graph.addEdge(knotenW, knotenV, weight);
		}

		System.out.println(sy_graph.getNumberOfVertexes());

		// Test, ob alle Kanten eingelesen wurden: 
		System.out.println("Number of Vertices:       " + sy_graph.getNumberOfVertexes());	// 199
		System.out.println("Number of directed Edges: " + sy_graph.getNumberOfEdges());	  	// 862
		double wSum = 0.0;
		for (Integer v : sy_graph.getVertexSet())
			for (Integer w : sy_graph.getSuccessorVertexSet(v))
				wSum += sy_graph.getWeight(v,w);
		System.out.println("Sum of all Weights:       " + wSum);	// 1972.0
		
		return sy_graph;
	}


	private static double dist(Point v, Point w) {
		return Math.sqrt((v.x-w.x)*(v.x-w.x) + (v.y-w.y)*(v.y-w.y));
	}


	/**
	 * Fabrikmethode zur Erzeugung einer Heuristik für die Schätzung
	 * der Distanz zweier Knoten im Scotland-Yard-Spielplan.
	 * Die Heuristik wird für A* benötigt.
	 * <p>
	 * Liest die (x,y)-Koordinaten (Pixelkoordinaten) aller Knoten von der Datei
	 * ScotlandYard_Knoten.txt in eine Map ein.
	 * Die zurückgelieferte Heuristik-Funktion estimatedCost
	 * berechnet einen skalierten Euklidischen Abstand.
	 * @return Heuristik für Scotland-Yard.
	 * @throws FileNotFoundException
	 */
	public static Heuristic<Integer> getHeuristic() throws FileNotFoundException {
		return new ScotlandYardHeuristic();
	}

	/**
	 * Scotland-Yard Anwendung.
	 * @param args wird nicht verewendet.
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		DirectedGraph<Integer> syGraph = getGraph();

		Heuristic<Integer> syHeuristic = null; // Dijkstra
		//Heuristic<Integer> syHeuristic = getHeuristic(); // A*

		ShortestPath<Integer> sySp = new ShortestPath<Integer>(syGraph,syHeuristic);

		sySp.searchShortestPath(65,157);
		System.out.println("Distance = " + sySp.getDistance()); // 9.0

		sySp.searchShortestPath(1,175);
		System.out.println("Distance = " + sySp.getDistance()); // 25.0

		sySp.searchShortestPath(1,173);
		System.out.println("Distance = " + sySp.getDistance()); // 22.0


		SYSimulation sim;
		try {
			sim = new SYSimulation();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		sySp.setSimulator(sim);
		sim.startSequence("Shortest path from 1 to 173");

		//sySp.searchShortestPath(65,157); // 9.0
		//sySp.searchShortestPath(1,175); //25.0

		sySp.searchShortestPath(1,173); //22.0
		// bei Heuristik-Faktor von 1/10 wird nicht der optimale Pfad produziert.
		// bei 1/30 funktioniert es.

		System.out.println("Distance = " + sySp.getDistance());
		List<Integer> sp = sySp.getShortestPath();

		int a = -1;
		for (int b : sp) {
			if (a != -1)
			sim.drive(a, b, Color.RED.darker());
			sim.visitStation(b);
			a = b;
		}

        sim.stopSequence();


    }

}

class ScotlandYardHeuristic implements Heuristic<Integer> {
	private Map<Integer,Point> coord; // Ordnet jedem Knoten seine Koordinaten zu

	private static class Point {
		int x;
		int y;
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public ScotlandYardHeuristic() throws FileNotFoundException {
		// ...
	}

	public double estimatedCost(Integer u, Integer v) {
		// ...
		return 0.0;
	}
}


