package projects.leach.models.connectivityModels;

import projects.leach.Funcao;
import projects.leach.nodes.nodeImplementations.EstacaoBaseNode;
import projects.leach.nodes.nodeImplementations.LeachNode;
import sinalgo.models.ConnectivityModelHelper;
import sinalgo.nodes.Node;

// Implementa a conexão entre um Cluster Head e um Nó comum.
public class LeachConnection extends ConnectivityModelHelper {

	public LeachConnection() {
		// TODO Auto-generated constructor stub

	}

	@Override
	protected boolean isConnected(Node from, Node to) {

		// CONEXAO VAI DO X1 PRO X2
		// Testa primeiro para os nós e os CHS
		if (from instanceof LeachNode && to instanceof EstacaoBaseNode) {

			LeachNode x1 = (LeachNode) from;

			if (x1.getFuncao() == Funcao.ClusterHead) {
				return true;
			}

		}

		if (from instanceof LeachNode && to instanceof LeachNode) {

			LeachNode x1 = (LeachNode) from;
			LeachNode x2 = (LeachNode) to;

			if (x1.getFuncao() == Funcao.ClusterHead) {

				if (x2.getFuncao() == Funcao.ClusterHead) {
					return false;
				}

				if (!x1.isConfigurado()) {
					return true;
				}
			}

			if (!x1.isVivo() || !x2.isVivo()) {

				return false;
			}

			if (x1.getClusterHead() == x2 || x2.getClusterHead() == x1) {
				return true;
			}

			if (!x1.isConfigurado() || !x2.isConfigurado()) {
				return true;
			}

		}
		return false;

	}
}