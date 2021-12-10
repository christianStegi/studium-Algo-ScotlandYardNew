package sim;

import sim.SYSimulation;
import java.awt.Color;
import java.io.IOException;

public class SYDemo {
	public static void main(String[] args) {
        SYSimulation sim;
		try {
			sim = new SYSimulation();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		sim.startSequence("Test1");
        sim.visitStation(9);
        sim.visitStation(20);
        sim.visitStation(33);
        sim.visitStation(46);
        sim.visitStation(79);
        sim.visitStation(63);
		sim.drive(9, 20, Color.YELLOW);
        sim.drive(20, 33, Color.YELLOW);
        sim.drive(33, 46, Color.YELLOW);
        sim.drive(46, 79, Color.RED.darker());
        sim.drive(79, 63, Color.GREEN.darker());
        sim.stopSequence();
        
        sim.startSequence("Test2");
        sim.visitStation(15);
        sim.visitStation(16);
        sim.visitStation(28);
        sim.visitStation(41);
        sim.visitStation(29);
        sim.visitStation(55);
        sim.visitStation(89);
        sim.visitStation(67);
		sim.drive(15, 16, Color.YELLOW);
        sim.drive(16, 28, Color.YELLOW);
        sim.drive(28, 41, Color.YELLOW);
        sim.drive(41, 29, Color.GREEN.darker());
        sim.drive(29, 55, Color.GREEN.darker());
        sim.drive(55, 89, Color.GREEN.darker());
        sim.drive(89, 67, Color.RED.darker());
        sim.stopSequence();
    }
}
