/* ############################################################################
 * #                                                                          #
 * #    PROJETO - Implementação do Protocolo LEACH no simulador SINALGO       #
 * #    ---------------------------------------------------------------       #
 * #                                                                          #
 * #	Professor: Dr. Alex Sandro Roschildt Pinto                            #
 * #    Matéria: Redes de Computadores                                        #
 * #                                                                          #
 * #    Alunos: Gabriel Henrique Martinez Saraiva        RA: 10139-7          #
 * #            Leonardo de Oliveira Santos	             RA: 10156-7          #
 * #            Igor Stefani Buttarelo	             	 RA: 00002-1 AC       #
 * #                                                                          #
 * #    ---------------------------------------------------------------       #
 * #                                                                          #
 * ############################################################################
 * 
 * 
 * ############################################################################
 * #                                                                          #
 * #   Arquivo: LeachEdge.java                                                #
 * #                                                                          #
 * #   Responsavel por definir se uma conexão será exibida ou não.            #
 * #   Isso é necessário pois os nós só se comunicam caso exista a conexão    #
 * #   entre eles. E todos os cluster heads necessitam se comunicar com todos #
 * #   os nós para poder fazer o broadcast de suas mensagens, conexões essas  #
 * #   que não devem ser "mostradas" para não confundir o usuário             #
 * #                                                                          #
 * ############################################################################
 * 
 */

package projects.leach.nodes.edges;

import java.awt.Graphics;
import projects.leach.Funcao;
import projects.leach.nodes.nodeImplementations.EstacaoBaseNode;
import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.edges.Edge;

public class LeachEdge extends Edge {

	@Override
	public void draw(Graphics g, PositionTransformation pt) {
		// TODO Auto-generated method stub

		if (true) {
			super.draw(g, pt);
			return;
		}

		if (startNode instanceof LeachNode && endNode instanceof LeachNode) {

			LeachNode x1 = (LeachNode) startNode;
			LeachNode x2 = (LeachNode) endNode;

			if (x1.isVivo() && x2.isVivo()) {
				if (x1.getClusterHead() == x2) {
					super.draw(g, pt);
				}
			}

		} else {

			if (startNode instanceof LeachNode && endNode instanceof EstacaoBaseNode) {

				LeachNode l = (LeachNode) startNode;

				if (l.getFuncao() == Funcao.ClusterHead && l.isVivo()) {
					super.draw(g, pt);
				}
			}
		}

	}
}
