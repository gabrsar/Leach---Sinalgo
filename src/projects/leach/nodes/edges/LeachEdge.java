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

		if (startNode instanceof LeachNode && endNode instanceof LeachNode) {

			LeachNode x1 = (LeachNode) startNode;
			LeachNode x2 = (LeachNode) endNode;

			if (x1.getClusterHead() == x2) {
				super.draw(g, pt);
			}

		} else {

			if (startNode instanceof LeachNode && endNode instanceof EstacaoBaseNode) {
				LeachNode l = (LeachNode) startNode;
				if (l.getFuncao() == Funcao.ClusterHead) {
					super.draw(g, pt);
				}
			}
		}

	}
}
