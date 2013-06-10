package projects.wsn.nodes.timers;


import projects.wsn.nodes.messages.WsnMsg;
import projects.wsn.nodes.nodeImplementations.SimpleNode;

import sinalgo.nodes.timers.Timer;

public class WsnMessageTimer extends Timer {

	private WsnMsg message = null;

	public WsnMessageTimer(WsnMsg message) {
		this.message = message;
	}

	public void fire() {
		((SimpleNode) node).broadcast(message);
	}

}
