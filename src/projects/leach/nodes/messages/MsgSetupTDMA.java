package projects.leach.nodes.messages;

import sinalgo.nodes.messages.Message;

public class MsgSetupTDMA extends Message {

	public int tamanhoTdma;
	public int slot;

	public MsgSetupTDMA(int tamanhoTdma, int slot) {
		// TODO Auto-generated constructor stub
		this.tamanhoTdma = tamanhoTdma;
		this.slot = slot;
	}

	@Override
	public Message clone() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String toString() {

		return "MsgSetupTDMA(" + tamanhoTdma + "," + slot + ")";
	}

}
