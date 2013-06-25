package projects.leach.nodes.timers;

import projects.leach.CustomGlobal;
import projects.leach.nodes.nodeImplementations.LeachNode;

import projects.leach.nodes.messages.MsgInvitacao;
import sinalgo.nodes.timers.Timer;

public class TimerInvitarNos extends Timer {

	@Override
	public void fire() {
		// TODO Auto-generated method stub

		((LeachNode) node).myBroadcast(new MsgInvitacao());

		CustomGlobal.myOutput(1, ((LeachNode) node).getPatente() + " disparando invitação a todos os nós no alcance.");
	}
}
