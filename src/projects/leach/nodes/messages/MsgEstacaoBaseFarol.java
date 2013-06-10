package projects.leach.nodes.messages;

import sinalgo.nodes.messages.Message;

public class MsgEstacaoBaseFarol extends Message {

	/*
	 * Mensagem qeu é um farol de sinalização que é emitido pela estação base
	 * (que tem energia "infinita") apenas para sinalizar a todos os nós de quem
	 * é que manda nessa porra!
	 */

	@Override
	public Message clone() {
		// TODO Auto-generated method stub
		return this;
	}

}
