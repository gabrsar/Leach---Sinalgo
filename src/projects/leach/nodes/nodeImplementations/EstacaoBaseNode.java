package projects.leach.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;

import projects.leach.nodes.messages.MsgDados;
import projects.leach.nodes.timers.TimerEstacaoBaseSinalizar;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.runtime.Global;

public class EstacaoBaseNode extends Node {

	public final static Double tempoDeSinalizacao = 100.0;

	@Override
	public void handleMessages(Inbox inbox) {

		while (inbox.hasNext()) {

			Message m = inbox.next();

			if (m instanceof MsgDados) {
				System.out.println("EB " + ID + " -> Recebeu dados do CH " + inbox.getSender().ID);
				System.out.println(((MsgDados) m).dados);
			}
		}

	}

	public void ativarSinalizacaoFarol() {

		System.out.println("EB " + ID + " -> Enviando SINAL de farol com potencia máxima");
		TimerEstacaoBaseSinalizar tebs = new TimerEstacaoBaseSinalizar();
		tebs.startRelative(1, this);

	}

	@Override
	public void preStep() {
		if (Global.currentTime % tempoDeSinalizacao == 0) {
			ativarSinalizacaoFarol();
		}
	}

	@Override
	public void init() {
		ativarSinalizacaoFarol();
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

	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub

		super.drawAsRoute(g, pt, highlight, (int) (40 * pt.getZoomFactor()));
		super.drawNodeAsDiskWithText(g, pt, highlight, ID + "", 14, Color.WHITE);

	}

}
