package projects.leach.nodes.timers;

import projects.leach.nodes.messages.MsgClusterHeadDesconectado;
import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.nodes.timers.Timer;

public class TimerLiberarNos extends Timer {

	@Override
	public void fire() {
		// TODO Auto-generated method stub

		((LeachNode) node).myBroadcast(new MsgClusterHeadDesconectado());

	}
}
