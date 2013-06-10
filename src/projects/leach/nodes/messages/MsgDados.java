package projects.leach.nodes.messages;

import sinalgo.nodes.messages.Message;

public class MsgDados extends Message {

	public String dados;

	public MsgDados(String dados) {
		// TODO Auto-generated constructor stub
		this.dados = dados;
	}

	@Override
	public Message clone() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MsgDados("+dados+")";
	}

}
