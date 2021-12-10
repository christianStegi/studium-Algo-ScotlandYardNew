package sim;
import java.awt.Color;
import java.awt.Point;
import java.io.IOException;

import gui.SimGUI;


/** <b>Scotland Yard Simulation, Algorithmen und Datenstrukturen, WS 2011/2012</b><p>
 * Dieses Programm dient als Visualisierung der in der Vorlesung Algorithmen und Datenstrukturen
 * behandelten Graphenalgorithmen. Es arbeitet mit der Karte aus dem Spiel Scotland Yard.
 * Es können grundsätzlich zwei Bewegungen visualisiert werden: <p>
 * 1. Besuchen einer Station, dabei wird die entsprechende Station durch einen Kreis markiert. <br>
 * 2. Fahren von einer Station zu einer anderen, dabei wird der Weg zwischen den Stationen durch eine Linie gekennzeichnet. <p>
 * Durch wiederholtes Anwenden dieser zwei Bewegungen kann so die Reihenfolge der besuchten Knoten (Stationen) und der gefundene Weg
 * am Beispiel der Scotland Yard Karte visuell dargestellt werden. <p>
 * <b>Funktionsweise:</b><p>
 * Durch Erzeugung einer Ojektinstanz wird das Hauptfenster mit der Scotland Yard Karte geöffnet. Das Programm unterstützt das
 * Simulieren mehrerer "Sequenzen" hintereinander. Eine Sequenz wird dabei durch die beiden Methoden startSequence() und stopSequence() gekennzeichnet.
 * Nach Aufruf von startSequence() können Stationen mit visitStation() besucht werden, oder es kann mit drive() gefahren werden. Das Ende
 * einer Sequenz wird mit dem Aufruf von stopSequence() gekennzeichnet. Erst danach kann mit startSequence() wieder eine neue Sequenz begonnen werden.
 * Dabei werden die Kartenmarkierungen der letzten Sequenz auf der Karte gelöscht, und die neue Sequenz wird simuliert.<p>
 * <b>Bedienelemente und Anzeige im Hauptfenster:</b><p>
 * <b>Play:</b><br>Setzt die Simulation nach drücken von 'Pause' wieder fort. Ebenso kann eine Sequenz nach deren Ablauf wiederholt werden. <br>
 * <b>Pause:</b><br>Pausiert die laufende Simulation, die Simulation kann mit 'Play' wieder fortgesetzt werden. <br>
 * <b>Stop:</b><br>Bricht die laufende Sequenz ab und die nächste Sequenz wird gestartet. Falls keine Sequenzen mehr vorhanden sind, stoppt
 * das Programm und das Hauptfenster kann geschlossen werden. <br>
 * <b>Speed:</b><br>Mit diesem Regler kann die Simulationsgeschwindigkeit eingestellt werden. <br> 
 * <b>Status Message:</b><br>Zeigt den aktuellen Status an bzw. welche Aktionen als nächstes möglich sind. <p>
 * <b>Beispielanwendung:</b><p>
 * Die Klasse <b>SYDemo</b> enthält eine Beispielanwendung mit zwei Simulationssequenzen.
 * @author Andreas Hermann, Inti Gabriel
 */
public class SYSimulation {
	private SimGUI gui;
	
	public SYSimulation() throws IOException {
		gui = new SimGUI();
	}
	

	/**
	 * Markiert den Beginn einer Simulationssequenz. Nach dem Aufruf dieser Methode können Simulationsereignisse mit visit() und drive()
	 * hinzugefügt werden.<p>
	 * <b>Achtung:</b> Der Aufruf von startSequence() ist <b>nicht blockierend</b>, d.h. das Hauptprogramm läuft weiter, während eine Simulationssequenz läuft. Da die
	 * Simulation zeitverzögert parallel zum Hauptprogramm läuft, wird sie durch einen separaten Thread durchgeführt.
	 * @param name Der Name für die Simulationssequenz, wird im Simulationsfenster in der Titelleiste angezeigt, damit der Anwender weiß, welche Sequenz
	 * gerade simuliert wird.
	 */
	public void startSequence(String name) {
		gui.startSequence(name);
	}
	
	/**
	 * Markiert das Ende einer Simulationssequenz. Danach kann mit startSequence() eine neue Sequenz begonnen werden.<p>
	 * <b>Achtung:</b> Der Aufruf von stopSequence() ist <b>blockierend</b>, d.h. das aufrufende Programm wird so lange gestoppt, bis der Anwender durch den 'Stop'
	 * Knopf die laufende Sequenz abbricht, oder das Simulationsfenster geschlossen wird.
	 */
	public void stopSequence() {
		gui.stopSequence();
	}
	
	/**
	 * Simuliert das Fahren von einer Station zu einer anderen, dabei werden die Stationen durch eine farbige Linie auf der Karte verbunden.
	 * @param from Die Stationssnummer, ab der losgefahren wird.
	 * @param to Die Stationsnummer der Zielstation.
	 * @param color Die Farbe, in der Verbindungslinie gezeichnet werden soll.
	 */
	public void drive(int from, int to, Color color) {
		gui.drive(from, to, color);
	}
	
	/**
	 * Simuliert das Fahren von einer Station zu einer anderen, dabei werden die Stationen durch eine schwarze Linie auf der Karte verbunden.
	 * @param from Die Stationssnummer, ab der losgefahren wird.
	 * @param to Die Stationsnummer der Zielstation.
	 */
	public void drive(int from, int to) {
		gui.drive(from, to);
	}
	
	/**
	 * Simuliert das Besuchen einer Station, dabei wird die Station durch einen farbigen Kreis auf der Karte gekennzeichnet.
	 * @param nr Die Nummer der Station, die besucht werden soll.
	 * @param color Die Farbe für den Kreis, mit der die Station markiert werden soll.
	 */
	public void visitStation(int nr, Color color) {
		gui.visitStation(nr, color);
	}
	
	/**
	 * Simuliert das Besuchen einer Station, dabei wird die Station durch einen schwarzen Kreis auf der Karte gekennzeichnet.
	 * @param nr Die Nummer der Station, die besucht werden soll.
	 */
	public void visitStation(int nr) {
		gui.visitStation(nr);
	}
	
	/**
	 * Liefert die Pixelkoordinaten einer bestimmten Station auf der Karte.
	 * @param nr Die Nummer der Station.
	 * @return Die Pixelkoordinaten (x,y) der Station.
	 */
	public Point getStationPos(int nr) {
		return gui.getPixelPos(nr);
	}
}
