package projects.leach.nodes.timers;

import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;

public class TimerSendMessage extends Timer {

	private Message message;
	private Node target;
	private boolean limparBuffers;

	public TimerSendMessage(Message mensagem, Node alvo, boolean limparBufferDepoisDeEnviar) {
		// TODO Auto-generated constructor stub
		this.message = mensagem;
		this.target = alvo;
		this.limparBuffers = limparBufferDepoisDeEnviar;
	}

	@Override
	public void fire() {
		// TODO Auto-generated method stub
		LeachNode ln = ((LeachNode) node);

		if (!ln.mySend(message, target)) {
			return;
		}

		if (limparBuffers) {
			ln.limparBuffer();
			ln.limparBufferCh();
		}

	}
}
