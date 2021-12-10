package gui;

import gui.Toolbar.ToolbarState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class SimGUI extends JFrame implements Runnable,ActionListener,ChangeListener, WindowListener {
	
	private Map map;
	private Toolbar tbar;
	private TreeMap<Integer,Station> stations;
	private Vector<MapEntry> mapEntries;
	private Vector<MapEntry> mapEntriesReplay;
	private Thread simThread;
	private Object lock;
	private static final int seqStarted = 1;
	private static final int seqStopped = 2;
	private static final int seqRunning = 3;
	private static final int seqPaused = 4;
	private static final int seqFinished = 5;
	private volatile int simState;
	private volatile int userState;
	private volatile boolean simAborted;
	private volatile int SLEEP_TIME;
	private static final int SLEEP_TIME_MAX = 1000; // 5000;
	private static final String windowName = "Scotland Yard Simulation";
	
	public SimGUI() throws IOException {
		stations = new TreeMap<Integer, Station>();
		readStationsFromFile(this.getClass().getResource("stations.txt").getPath());
		mapEntries = new Vector<MapEntry>();
		mapEntriesReplay = new Vector<MapEntry>();
		lock = new Object();
		simState = seqStopped;
		userState = seqStopped;
		simAborted = false;
		SLEEP_TIME = 2000;
		
		prepareGUI();
		this.pack();
        this.setVisible(true);
	}
	
	// construct the gui
	private void prepareGUI() throws IOException {
		// Toolbar with control buttons, speed slider and status message field
        tbar = new Toolbar(this, this);
        tbar.setState(ToolbarState.STOPPED);
		// Panel with london map
		map = new Map();
		
		// construct main window
		this.setTitle(windowName);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        this.setBackground(Color.black);
		this.setLayout(new BorderLayout());
        this.add(tbar, BorderLayout.NORTH);
        this.add(map, BorderLayout.CENTER);
	}
	
	// read pixel position for each station from file
	private void readStationsFromFile(String filename) throws FileNotFoundException {
		Scanner in = new Scanner(new File(filename));
        while (in.hasNext()) {
            int nr = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            stations.put(nr, new Station(x, y));
        }
        in.close();
        for (int i = 1; i <= 199; i++) {
        	if (! stations.containsKey(i)) {
        		System.out.println("Station " + i + "is missing!");
        	}
        }
	}
	
	public void startSequence(String name) {
        if (!simAborted) {
        	if (userState == seqStarted) {
        		System.out.println("Unable to start sequence " + name + " ,did you forget to stop previous sequence?");
        		userState = seqStopped;
        		return;
        	}
        	this.setTitle(windowName + " : " + name);
        	// create and start the drawing thread
        	userState = seqStarted;
        	simThread = new Thread(this);
        	try {
				map.clearMap();
			} catch (IOException e) {
				System.out.println("Failed to load map, unable to start sequence " + name + " !");
				return;
			}
        	mapEntries.clear();
        	mapEntriesReplay.clear();
        	simThread.start();
        }
	}
	
	public void stopSequence() {
		if (!simAborted) {
			userState = seqStopped;
			try {
				// wait for simulation thread to exit
				if (simThread != null) {
					simThread.join();
				}
			}
			catch (InterruptedException e) {
				System.out.println("Failed to stop sequence, simulation thread interrupted!");
			}
		}
	}
	
	public void drive(int start, int stop, Color color) {
		if (!simAborted && userState == seqStarted) {
			Station s1 = stations.get(start);
			Station s2 = stations.get(stop);
			if (s1 == null || s2 == null) {
				return;
			}
			synchronized(lock) {
				mapEntries.add(new Route(s1, s2, color));
			}
		}
	}
	
	public void drive(int start, int stop) {
		if (!simAborted && userState == seqStarted) {
			Station s1 = stations.get(start);
			Station s2 = stations.get(stop);
			if (s1 == null || s2 == null) {
				return;
			}
			synchronized(lock) {
				mapEntries.add(new Route(s1, s2));
			}
		}
	}
	
	public void visitStation(int nr, Color color) {
		if (!simAborted && userState == seqStarted) {
			Station s = stations.get(nr);
			if (s == null) {
				return;
			}
			s.setColor(color);
			synchronized(lock) {
				mapEntries.add(s);
			}
		}
	}
	
	public void visitStation(int nr) {
		if (!simAborted && userState == seqStarted) {
			Station s = stations.get(nr);
			if (s == null) {
				return;
			}
			synchronized(lock) {
				mapEntries.add(s);
			}
		}
	}
	
	public Point getPixelPos(int nr) {
		return stations.get(nr).getPos();
	}

	// simulation thread
	@Override
	public void run() {
		simState = seqRunning;
		MapEntry entry;
		
		while (!simAborted && simState != seqStopped) {
			
			switch (simState) {
			
				case seqRunning:
					tbar.setState(ToolbarState.PLAY);
					// no entry to draw
					if (mapEntries.isEmpty()) {
						// simulation finished
						if (userState == seqStopped) {
							simState = seqFinished;
						}
						break;
					}
					// remove first entry and push to replay map
					synchronized(lock) {
						entry = mapEntries.remove(0);
					}
					mapEntriesReplay.add(entry);

					// draw entry to map
					if (entry.type().equals("station")) {
						Station s = (Station) entry;
						map.drawCircle(s.getPos().x, s.getPos().y, s.getColor());
					}
					else if (entry.type().equals("route")) {
						Route r = (Route) entry;
						map.drawLine(r.start().getPos().x, r.start().getPos().y, r.stop().getPos().x, r.stop().getPos().y, r.getColor());
					}
					break;
			
				case seqPaused:
					// pause drawing
					tbar.setState(ToolbarState.PAUSED);
					break;
				
				case seqFinished:
					// simulation finished, stop drawing
					tbar.setState(ToolbarState.FINISHED);
					break;
				
			}
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
		this.setTitle(windowName);
		tbar.setState(ToolbarState.STOPPED);
	}

	// callback for button events
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {		
		if (Toolbar.CMD_PLAY.equals(e.getActionCommand())) {
			if (simState == seqFinished) {
				// clear map and replay simulation
				mapEntries = (Vector<MapEntry>) mapEntriesReplay.clone();
				mapEntriesReplay.clear();
				try {
					map.clearMap();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			// continue simulation
			simState = seqRunning;
		} 		
		else if (Toolbar.CMD_PAUSE.equals(e.getActionCommand())) {
			// pause simulation
			if (simState == seqRunning) {
				simState = seqPaused;
			}
		}		
		else if (Toolbar.CMD_STOP.equals(e.getActionCommand())) {
			// stop simulation
			simState = seqStopped;
		}
	}

	// callback for slider events
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (! source.getValueIsAdjusting()) {
			int speed = (int) source.getValue();
			SLEEP_TIME = SLEEP_TIME_MAX / speed;
		}	
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.setVisible(false);
		simAborted = true;
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
