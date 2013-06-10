package projects.leach.nodes.timers;

import projects.leach.nodes.messages.MsgInvitacao;
import sinalgo.nodes.timers.Timer;

public class TimerInvitarNos extends Timer {

	@Override
	public void fire() {
		// TODO Auto-generated method stub
		node.broadcast(new MsgInvitacao());
		System.out.println("CH " + node.ID + " -> disparando invitação a todos os nós no alcance.");

	}
}
