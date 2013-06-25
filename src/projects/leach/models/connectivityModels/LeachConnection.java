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
 * #   Arquivo: LeachConnection.java                                          #
 * #                                                                          #
 * #   Responsavel por definir se um nó pode ser conectado com outro nó ou    #
 * #   não. E controlar as regras para que isso aconteça.                     #
 * #                                                                          #
 * ############################################################################
 * 
 */

package projects.leach.models.connectivityModels;

import projects.leach.Funcao;
import projects.leach.nodes.nodeImplementations.EstacaoBaseNode;
import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.models.ConnectivityModelHelper;
import sinalgo.nodes.Node;

// Implementa a conexão entre um Cluster Head e um Nó comum.
public class LeachConnection extends ConnectivityModelHelper {

	@Override
	protected boolean isConnected(Node from, Node to) {

		// Conexão entre Leach Node e Estação Base
		// Só pode caso o LN seja um CH e esteja vivo.
		if (from instanceof LeachNode && to instanceof EstacaoBaseNode) {

			LeachNode x1 = (LeachNode) from;

			if (x1.getFuncao() == Funcao.ClusterHead && x1.isVivo() == true) {
				return true;
			}

		}

		if (from instanceof LeachNode && to instanceof LeachNode) {

			LeachNode x1 = (LeachNode) from;
			LeachNode x2 = (LeachNode) to;

			// Se um dos dois nós estiverem mortos não conecta.
			if (!x1.isVivo() || !x2.isVivo()) {
				return false;
			}

			if (x1.getFuncao() == Funcao.ClusterHead) {

				if (x2.getFuncao() == Funcao.ClusterHead) {
					return false;
				}

				// Caso o cluster Head ainda não esteja configurado aceita conexão de todos.
				if (!x1.isConfigurado()) {
					return true;
				}

				if (x2.getClusterHead() == x1) {
					return true;
				}

			}

			if (!x1.isConfigurado()) {
				return true;
			}

		}

		return false;

	}
}