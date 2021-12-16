// O. Bittel;
// 01.04.2021

package shortestPath;
//package scotlandYard_2021;

import SYSimulation.SYSimulation.src.sim.SYSimulation;
import shortestPath.directedGraph.*;
//import SYSimulation.SYSimulation.sim.*;

import java.awt.*;
import java.util.*;
import java.util.List;

// ...

/**
 * Kürzeste Wege in Graphen mit A*- und Dijkstra-Verfahren.
 * @author Oliver Bittel
 * @since 27.01.2015
 * @param <V> Knotentyp.
 */
public class ShortestPath<V> {
	
	SYSimulation sim = null;
	
	Map<V,Double> dist; 		// Distanz für jeden Knoten
	Map<V,V> pred; 				// Vorgänger für jeden Knoten
	IndexMinPQ<V,Double> cand; 	// Kandidaten als PriorityQueue PQ
	// ...
	DirectedGraph<V> graph;
	Heuristic<V> heur;
	V startNode;
	V endNode;


	/**
	 * Konstruiert ein Objekt, das im Graph g kürzeste Wege
	 * nach dem A*-Verfahren berechnen kann.
	 * Die Heuristik h schätzt die Kosten zwischen zwei Knoten ab.
	 * Wird h = null gewählt, dann ist das Verfahren identisch 
	 * mit dem Dijkstra-Verfahren.
	 * @param g Gerichteter Graph
	 * @param h Heuristik. Falls h == null, werden kürzeste Wege nach
	 * dem Dijkstra-Verfahren gesucht.
	 */
	public ShortestPath(DirectedGraph<V> g, Heuristic<V> h) {
		dist = new HashMap<>();
		pred = new HashMap<>();
		cand = new IndexMinPQ<>();
		// ...
		this.graph = g;
		this.heur = h;
	}


	/**
	 * Diese Methode sollte nur verwendet werden, 
	 * wenn kürzeste Wege in Scotland-Yard-Plan gesucht werden.
	 * Es ist dann ein Objekt für die Scotland-Yard-Simulation zu übergeben.
	 * <p>
	 * Ein typische Aufruf für ein SYSimulation-Objekt sim sieht wie folgt aus:
	 * <p><blockquote><pre>
	 *    if (sim != null)
	 *       sim.visitStation((Integer) v, Color.blue);
	 * </pre></blockquote>
	 * @param sim SYSimulation-Objekt.
	 */
	public void setSimulator(SYSimulation sim) {
		this.sim = sim;
	}


	/**
	 * Sucht den kürzesten Weg von Starknoten s zum Zielknoten g.
	 * <p>
	 * Falls die Simulation mit setSimulator(sim) aktiviert wurde, wird der Knoten,
	 * der als nächstes aus der Kandidatenliste besucht wird, animiert.
	 * @param s Startknoten
	 * @param g Zielknoten
	 */
	public void searchShortestPath(V s, V g) {

		startNode = s;
		endNode = g;

//		cand = new IndexMinPQ<>();

		if(heur == null){
			dijkstra(s,g);
		} else {
			astar(s,g);
		}



	}


	/**
	 * Liefert einen kürzesten Weg von Startknoten s nach Zielknoten g.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return kürzester Weg als Liste von Knoten.
	 */
	public List<V> getShortestPath() {

		List<V> shortestPath = new ArrayList<>();
		V aktuell = endNode;

		boolean weitermachen = true;

		/* läuft rückwärts vom Zielknoten bis zum Startknoten alle Vorgängerknoten
		ab und legt die so besuchten Knoten in einer Liste ab */

		while (weitermachen) {

			if (aktuell == startNode) {
				weitermachen = false;
			}
			shortestPath.add(aktuell);
			aktuell = pred.get(aktuell);
		}

		Collections.reverse(shortestPath);
		return shortestPath;
	}


	/**
	 * Liefert die Länge eines kürzesten Weges von Startknoten s nach Zielknoten g zurück.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return Länge eines kürzesten Weges.
	 */
	public double getDistance() {
		double distance = dist.get(endNode);
		return distance;
	}


	private void dijkstra(V s, V g){

		cand.clear();

		for(var v : graph.getVertexSet()){
			dist.put(v, Double.MAX_VALUE);
			pred.put(v, null);
		}

		dist.put(s,0.);
		cand.add(s,0.);



		while (!cand.isEmpty()){

			double d = cand.getMinValue();
			V v = cand.removeMin();

			if (sim != null) {
				sim.visitStation((int) v, Color.green);
			}

			for(var w : graph.getSuccessorVertexSet(v)){

				double newdistance = dist.get(v) + graph.getWeight(v,w);

				if (dist.get(w) == Double.MAX_VALUE){
					pred.put(w,v);
					dist.put(w, newdistance);
					cand.add(w,newdistance);
				} else if (newdistance < dist.get(w)){
					pred.put(w,v);
					dist.put(w, newdistance);
					cand.change(w, newdistance);
				}

				if (sim != null) {
					sim.drive((int) v, (int) w, Color.ORANGE);
					sim.visitStation((int)w, Color.green);
				}

				if (v.equals(g)) {
					return;
				}
			}
//			System.out.println("Besuche Knoten " + v + " mit d = " + dist.get(v));
		}
	}


	private boolean astar(V s, V g){

		cand = new IndexMinPQ<>();

		for (var v : graph.getVertexSet()){
			dist.put(v, Double.MAX_VALUE);
			pred.put(v, null);
		}

		dist.put(s,0.);
		cand.add(s,0. + heur.estimatedCost(s,g));

		while (!cand.isEmpty()){
			V v = cand.removeMin();

			if (sim != null) {
				sim.visitStation((int) v, Color.green);
			}

			if (v.equals(g)) {
//				System.out.println("Besuche Knoten " + v + " mit d = " + dist.get(v));
//				cand.clear();
//				cand = new IndexMinPQ<>();
				return true;
			}
			for (var w : graph.getSuccessorVertexSet(v)){
				double distance = dist.get(v) + graph.getWeight(v,w);
				if (dist.get(w) == Double.MAX_VALUE){
					pred.put(w,v);
					dist.put(w, distance);
					cand.add(w,distance + heur.estimatedCost(w,g));

				} else if (distance < dist.get(w)) {
					pred.put(w,v);
					dist.put(w, distance);
					System.out.println("v: " + v + ", w: " + w);

					cand.change(w, distance + heur.estimatedCost(w,g));
				}

				if (sim != null) {
					sim.drive((int) v, (int) w, Color.ORANGE);
					sim.visitStation((int)w, Color.green);
				}

			}
//			System.out.println("Besuche Knoten " + v + " mit d = " + dist.get(v));
		}
		return false;
	}

}