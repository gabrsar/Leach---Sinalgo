package projects.leach.nodes.timers;

import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;

public class TimerSendMessage extends Timer {

	private Message message;
	private Node target;

	public TimerSendMessage(Message mensagem, Node alvo) {
		// TODO Auto-generated constructor stub
		this.message = mensagem;
		this.target = alvo;
	}

	@Override
	public void fire() {
		// TODO Auto-generated method stub
		LeachNode ln = ((LeachNode)node);
		
		ln.mySend(message, target);		
	}
}
