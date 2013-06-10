package projects.leach.nodes.timers;

import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.nodes.timers.Timer;

public class TimerLimparBufferCH extends Timer {

	@Override
	public void fire() {
		// TODO Auto-generated method stub

		((LeachNode) node).limparBufferCh();

	}
}