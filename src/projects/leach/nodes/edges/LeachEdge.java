package projects.leach.nodes.edges;

import java.awt.Color;
import java.awt.Graphics;

import projects.leach.Funcao;
import projects.leach.nodes.nodeImplementations.EstacaoBaseNode;
import projects.leach.nodes.nodeImplementations.LeachNode;

import sinalgo.gui.helper.Arrow;
import sinalgo.gui.transformation.PositionTransformation;

import sinalgo.nodes.Position;
import sinalgo.nodes.edges.Edge;

public class LeachEdge extends Edge {

	// Desativada por ser melhor utilizar a o modo simples. (Arcos em preto)
	//	public void drawLeachToLeach(Graphics g, PositionTransformation pt) {
	//
	//		LeachNode start = (LeachNode) startNode;
	//		LeachNode end = (LeachNode) endNode;
	//
	//		Color bandeira = end.getBandeira();
	//
	//		Position p1 = start.getPosition();
	//		pt.translateToGUIPosition(p1);
	//
	//		int fromX = pt.guiX, fromY = pt.guiY; 
	//
	//		Position p2 = end.getPosition();
	//		pt.translateToGUIPosition(p2);
	//
	//		if ((this.numberOfMessagesOnThisEdge == 0) && (this.oppositeEdge != null)
	//				&& (this.oppositeEdge.numberOfMessagesOnThisEdge > 0)) {
	//			// only draws the arrowHead (if drawArrows is true)
	//			Arrow.drawArrowHead(fromX, fromY, pt.guiX, pt.guiY, g, pt, getColor());
	//			if (numberOfMessagesOnThisEdge > 0) {
	//				Arrow.drawArrowHead(fromX, fromY, pt.guiX, pt.guiY, g, pt, getColor());
	//			} else {
	//				Arrow.drawArrowHead(fromX, fromY, pt.guiX, pt.guiY, g, pt, bandeira);
	//			}
	//		} else {
	//
	//			if (numberOfMessagesOnThisEdge > 0) {
	//				Arrow.drawArrow(fromX, fromY, pt.guiX, pt.guiY, g, pt, getColor());
	//			} else {
	//				Arrow.drawArrow(fromX, fromY, pt.guiX, pt.guiY, g, pt, bandeira);
	//			}
	//		}
	//	}

	@Override
	public void draw(Graphics g, PositionTransformation pt) {
		// TODO Auto-generated method stub

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
