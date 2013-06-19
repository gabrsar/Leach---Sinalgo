/* Projeto LEACH - Leonardo O. Santos e Gabriel H. M. Saraiva */

package projects.leach;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import projects.leach.nodes.nodeImplementations.LeachNode;

import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.tools.Tools;

/**
 * Essa Classe armazena métodos e estados do projeto que afetam o framework.
 * 
 * @see sinalgo.runtime.AbstractCustomGlobal for more details.
 */

public class CustomGlobal extends AbstractCustomGlobal {

	/**
	 * Variavel que ontrola o nivel de saida do simulador, quanto maior, mais
	 * detalhado
	 */
	public static int OUTPUT_LEVEL = 1;

	/**
	 * Função que exibe a saida para relatórios da simulação com base no limite
	 * definido por OUTPUT_LEVEL
	 * */

	public static void myOutput(int level, String texto) {

		if (OUTPUT_LEVEL > level) {
			String destaque = "|";

			for (int d = 0; d < level; d++) {
				destaque += "----";
			}

			if (level == 0 || level == 1) {
				Tools.appendToOutput(texto + "\n");
			}

			destaque += "> ";
			texto = destaque + texto;
			System.out.println(texto);

		}
	}

	/**
	 * Botão para definir o nivel de saida dos logs.
	 */
	@AbstractCustomGlobal.CustomButton(buttonText = "Nivel de Log")
	public void definirNivelDeLog() {

		// Query the user for an input
		String answer = JOptionPane.showInputDialog(null, "Entre com o nivel de LOG desejado de 0 até 5.\n"
				+ "Quanto maior o número mais informações serão exibidas.\n" + "O padrão é 0.");

		try {
			int nivel = Integer.parseInt(answer);
			if (nivel >= 0 && nivel <= 5) {
				OUTPUT_LEVEL = nivel;
			} else {
				JOptionPane.showMessageDialog(null, "Valor Inválido");
			}
		} catch (Exception e) {
		}

	}

	@AbstractCustomGlobal.CustomButton(buttonText = "Duração dos ROUNDS (LEACH)")
	public void definirRoundsPorRounds() {

		// Query the user for an input
		String answer = JOptionPane
				.showInputDialog(
						null,
						"Entre com o \"tempo\" de duração de um ROUND para os Cluster Heads, de 100 até 5000.\nEsse é o tempo que um nó terá para ser Cluster Head.");

		try {
			int rpr = Integer.parseInt(answer);
			if (rpr >= 100 && rpr <= 5000) {
				LeachNode.RODADAS_POR_ROUND = rpr;
			} else {
				JOptionPane.showMessageDialog(null, "Valor Inválido");
			}
		} catch (Exception e) {
		}

	}

	@AbstractCustomGlobal.CustomButton(buttonText = "% de Cluster Heads")
	public void definirPorcentagemDeCH() {

		// Query the user for an input
		String answer = JOptionPane
				.showInputDialog(
						null,
						"Entre com a porcentagem de Cluster Heads desejada.\n0 a 100 (sem o simbolo %).\nLembrete: 0% e 100% é o mesmo que transmissão direta.");

		try {
			int pct = Integer.parseInt(answer);
			if (pct >= 0 && pct <= 100) {
				LeachNode.P = pct / 100.0;
			} else {
				JOptionPane.showMessageDialog(null, "Valor Inválido");
			}
		} catch (Exception e) {
		}

	}

	@AbstractCustomGlobal.CustomButton(buttonText = "Bateria Inicial")
	public void definirBateriaInicial() {

		// Query the user for an input
		String answer = JOptionPane.showInputDialog(null,
				"Entre com a Quantidade de Energia inicial dos nós.\nPadrão: 1000000.");

		try {
			double energia = Double.parseDouble(answer);
			if (energia >= 0) {
				LeachNode.BATERIA_INICIAL = energia;
			} else {
				JOptionPane.showMessageDialog(null, "Valor Inválido");
			}
		} catch (Exception e) {
		}

	}

	/** Simulação não acaba nunca pois é possivel redefinir bateria para os nós */
	public boolean hasTerminated() {

		return false;
	}

}
