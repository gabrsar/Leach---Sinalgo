package projects.leach.nodes.timers;

import projects.leach.nodes.messages.MsgClusterHeadDesconectado;
import sinalgo.nodes.timers.Timer;

public class TimerLiberarNos extends Timer {

	@Override
	public void fire() {
		// TODO Auto-generated method stub

		node.broadcast(new MsgClusterHeadDesconectado());

	}
}
