package gui;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class Toolbar extends JPanel {
	
	private JButton playButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JTextField statusField;
	private JSlider speedSlider;
	static final private int SPEED_MIN = 1;
	static final private int SPEED_MAX = 10;
	static final private int SPEED_INIT = 5;
	private ToolbarState curState;
	
	static final public String CMD_PLAY = "play";
	static final public String CMD_PAUSE = "pause";
	static final public String CMD_STOP = "stop";
	public enum ToolbarState {
		PLAY,
		PAUSED,
		FINISHED,
		STOPPED,
		NONE
	}
	static final private String STATUS_MSG_PLAY = " running ...";
	static final private String STATUS_MSG_PAUSED = " paused, press 'Play' to continue!";
	static final private String STATUS_MSG_FINISHED = " sequence finished, press 'Play' to repeat or 'Stop' to cancel!";
	static final private String STATUS_MSG_STOPPED = " no data available, simulation stopped!";
	
	public Toolbar(ActionListener al, ChangeListener cl) {
		curState = ToolbarState.NONE;
		playButton = makeButton("Play", CMD_PLAY, "Resume or restart simulation", al);
		pauseButton = makeButton("Pause", CMD_PAUSE, "Pause simulation", al);
		stopButton = makeButton("Stop", CMD_STOP, "Stop simulation", al);
		statusField = new JTextField();
		statusField.setEditable(false);
		speedSlider = new JSlider(SPEED_MIN, SPEED_MAX, SPEED_INIT);
		speedSlider.setMajorTickSpacing(9);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.addChangeListener(cl);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(playButton);
		this.add(pauseButton);
		this.add(stopButton);
		this.add(new JLabel("   Speed: "));
		this.add(speedSlider);
		this.add(new JLabel("   Status Message: "));
		this.add(statusField);
	}
	
	private JButton makeButton(String text, String actionCommand, String toolTipText, ActionListener al) {
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.addActionListener(al);
		button.setToolTipText(toolTipText);
		button.setText(text);
		
		return button;
	}
	
	public void setState(ToolbarState state) {
		if (curState == state) {
			return;
		}
		curState = state;
		switch (curState) {
		case STOPPED:
			pauseButton.setEnabled(false);
	        stopButton.setEnabled(false);
			playButton.setEnabled(false);
			statusField.setText(STATUS_MSG_STOPPED);
			break;
		case PLAY:
			pauseButton.setEnabled(true);
	        stopButton.setEnabled(true);
			playButton.setEnabled(false);
			statusField.setText(STATUS_MSG_PLAY);
			break;
		case PAUSED:
			pauseButton.setEnabled(false);
	        stopButton.setEnabled(false);
			playButton.setEnabled(true);
			statusField.setText(STATUS_MSG_PAUSED);
			break;
		case FINISHED:
			pauseButton.setEnabled(false);
	        stopButton.setEnabled(true);
			playButton.setEnabled(true);
			statusField.setText(STATUS_MSG_FINISHED);
			break;
		default:
			break;
		}
	}
	
}

