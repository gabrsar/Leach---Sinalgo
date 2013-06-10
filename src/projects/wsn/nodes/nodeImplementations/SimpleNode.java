package projects.wsn.nodes.nodeImplementations;

import projects.wsn.nodes.messages.WsnMsg;
import projects.wsn.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

public class SimpleNode extends Node {
	private static Integer qtd = 0;
	private Integer id = 0;
	private Integer myMessageId = 0;

	private Node proximoNoAteEstacaoBase;

	private Integer sequenceNumber = 0;

	@Override
	public void handleMessages(Inbox inbox) {

		System.out.println("Nó " + id + " SINAL: " + this.getRadioIntensity()
				+ ". Mensagem: " + myMessageId);

		while (inbox.hasNext()) {
			Message message = inbox.next();

			if (message instanceof WsnMsg) {
				Boolean encaminhar = Boolean.TRUE;
				WsnMsg wsnMessage = (WsnMsg) message;
				if (wsnMessage.forwardingHop.equals(this)) {
					encaminhar = Boolean.FALSE;

				} else if (wsnMessage.tipoMsg == 0) {
					if (proximoNoAteEstacaoBase == null) {
						proximoNoAteEstacaoBase = inbox.getSender();
						sequenceNumber = wsnMessage.sequenceID;

					} else if (sequenceNumber < wsnMessage.sequenceID) {
						sequenceNumber = wsnMessage.sequenceID;
					} else {
						encaminhar = Boolean.FALSE;
					}
				}
				if (encaminhar) {
					wsnMessage.forwardingHop = this;
					broadcast(wsnMessage);
				}
			}
		}
	}

	@NodePopupMethod(menuText = "Construir Arvore de Roteamento")
	public void construirRoteamento() {

		this.proximoNoAteEstacaoBase = this;
		WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0);

		WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);

		
		timer.startRelative(1, this);
		
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		id = qtd;
		qtd++;
		
		System.out.println("Iniciando nó " + id + ".");

	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub

	}


	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub

	}

}
