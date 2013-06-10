package projects.leach.nodes.timers;

import projects.leach.nodes.messages.MsgNoDesconectado;
import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.nodes.timers.Timer;

public class TimerDesconectarDeCH extends Timer {

	LeachNode ch;

	public TimerDesconectarDeCH(LeachNode c) {
		ch = c;
	}

	@Override
	public void fire() {
		// TODO Auto-generated method stub

		((LeachNode) node).mySend(new MsgNoDesconectado(), ch);

	}
}
