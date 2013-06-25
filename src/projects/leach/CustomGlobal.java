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
 * #   Arquivo: CustomGlobal.java                                             #
 * #                                                                          #
 * #   Responsavel por definir Constantes, Configurações métodos responsáveis #
 * #   por controlar a simulação do projeto                                   #
 * #                                                                          #
 * ############################################################################
 * 
 */

package projects.leach;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import sinalgo.configuration.Configuration;
import sinalgo.runtime.AbstractCustomGlobal;
import sinalgo.runtime.Main;
import sinalgo.tools.Tools;

/**
 * Essa Classe armazena métodos e estados do projeto que afetam o framework.
 * 
 * @see sinalgo.runtime.AbstractCustomGlobal for more details.
 */

public class CustomGlobal extends AbstractCustomGlobal {
	/*
	 *  =======================================================================
	 *  =            Variaveis globais e de controle da simulação             =
	 *  =======================================================================
	 */

	/** Número de rodadas do sinalgo que são um ROUND para o Leach */
	public static int RODADAS_POR_ROUND = 200;

	/** Porcentagem de Cluster Heads desejada. */
	public static Double PORCENTAGEM_CH = 0.1;

	/** Número máximo de nós que um cluster pode ter. */
	public static int NUMERO_MAXIMO_DE_NOS_POR_CLUSTER = (RODADAS_POR_ROUND / 4) - 10;

	/** Formatador de números para os nós. */
	public static DecimalFormat DF = new DecimalFormat("##0.#");

	/** Cor dos nós quando não estão em nenhum cluster. */
	public final static Color BANDEIRA_LIVRE = Color.BLACK;

	/** Cor dos nós quando nós quando estão mortos. */
	public final static Color BANDEIRA_MORTO = Color.RED;

	/** Número de rodadas entre cada coleta de informação */
	public static int INTERVALO_DE_COLETA = 5;

	/** Energia que cada nó tem no inicio da simulação. */
	public static Double BATERIA_INICIAL = 10000000.0;

	/** Variavel que ontrola o nivel de saida dos dados */
	public static int OUTPUT_LEVEL = 0;

	/*
	 *  =======================================================================
	 *  =         Opções de Controle do Projeto em Tempo de Execução          =
	 *  =======================================================================
	 */

	/* Configuração do Nivel de LOGS */
	@AbstractCustomGlobal.CustomButton(buttonText = "Configurar: Nivel de Log")
	public void definirNivelDeLog() {

		JSlider js = new JSlider(0, 5, OUTPUT_LEVEL);
		js.setMajorTickSpacing(1);
		js.setMinorTickSpacing(1);
		js.setPreferredSize(new Dimension(600, 100));
		js.setPaintLabels(true);
		js.setPaintTicks(true);

		JOptionPane.showMessageDialog(null, js, "Nivel de LOG", JOptionPane.QUESTION_MESSAGE);

		int nivel = js.getValue();
		OUTPUT_LEVEL = nivel;
		myOutput(0, "Nivel de Log ajustado para " + nivel + ".");

	}

	/* Configuração da duração dos Rounds LEACH */
	@AbstractCustomGlobal.CustomButton(buttonText = "Configurar: Duração dos Rounds")
	public void definirRoundsPorRounds() {

		JSlider js = new JSlider(100, 2000, RODADAS_POR_ROUND);
		js.setMajorTickSpacing(100);
		js.setMinorTickSpacing(100);
		js.setPreferredSize(new Dimension(600, 100));
		js.setSnapToTicks(true);
		js.setPaintLabels(true);
		js.setPaintTicks(true);

		JOptionPane.showMessageDialog(null, js, "Qual o tamanho do Round Leach?", JOptionPane.QUESTION_MESSAGE);

		int rpr = js.getValue();
		RODADAS_POR_ROUND = rpr;
		myOutput(0, "Tamanho do Round Leach ajustado para " + RODADAS_POR_ROUND + "rounds.");

	}

	/* Configuração da porcentagem de Cluster Heads desejada na simulação */

	@AbstractCustomGlobal.CustomButton(buttonText = "Configurar: % de Cluster Heads")
	public void definirPorcentagemDeCH() {

		JSlider js = new JSlider(1, 100, (int) (100 * PORCENTAGEM_CH));
		js.setMajorTickSpacing(5);
		js.setMinorTickSpacing(1);
		js.setPreferredSize(new Dimension(600, 100));
		js.setPaintLabels(true);
		js.setPaintTicks(true);

		JOptionPane.showMessageDialog(null, js, "Porcentagem de Cluster Heads desejada", JOptionPane.QUESTION_MESSAGE);

		double pct = js.getValue();

		PORCENTAGEM_CH = pct / 100.0;
		myOutput(0, "Porcentagem de CH ajustada para  " + DF.format(PORCENTAGEM_CH * 100) + "%.");

	}

	/* Configuração da bateria inicial dos nós dada ao serem criados */
	@AbstractCustomGlobal.CustomButton(buttonText = "Configurar: Bateria Inicial")
	public void definirBateriaInicial() {

		SpinnerNumberModel snm = new SpinnerNumberModel();
		snm.setMaximum(999999999);
		snm.setMinimum(0);
		snm.setStepSize(100000);

		JSpinner js = new JSpinner(snm);
		js.setPreferredSize(new Dimension(600, 100));
		snm.setValue(BATERIA_INICIAL);
		JOptionPane.showMessageDialog(null, js, "Quantidade de Energia Inicial dos Nós", JOptionPane.QUESTION_MESSAGE);

		BATERIA_INICIAL = snm.getNumber().doubleValue();
		myOutput(0, "Bateria inicial dos nós ajustada para " + BATERIA_INICIAL + ".");

	}

	/*
	 *  =======================================================================
	 *  =              Funções sobrescritas para o projeto atual.             =
	 *  =======================================================================
	 */

	/**
	 * Simulação não acaba nunca pois é possivel redefinir bateria para os nós,
	 * caso seja desejado ou necessário.
	 */
	@Override
	public boolean hasTerminated() {

		return false;
	}

	/** Carrega as configuraçoes e define as variaveis globais */
	@Override
	public void checkProjectRequirements() {

		try {

			RODADAS_POR_ROUND = Configuration.getIntegerParameter("LeachNode/RodadasPorRound");
			PORCENTAGEM_CH = Configuration.getDoubleParameter("LeachNode/PorcentagemDeCH");
			NUMERO_MAXIMO_DE_NOS_POR_CLUSTER = Configuration
					.getIntegerParameter("LeachNode/NumeroMaximoDeNosPorCluster");
			INTERVALO_DE_COLETA = Configuration.getIntegerParameter("LeachNode/IntervaloDeColetaDeDados");
			BATERIA_INICIAL = Configuration.getDoubleParameter("LeachNode/BateriaInicial");
			OUTPUT_LEVEL = Configuration.getIntegerParameter("Simulacao/NivelDeInformacao");

		} catch (Exception e) {
			Main.fatalError(e.getStackTrace().toString());
		}

	}

	/*
	 *  =======================================================================
	 *  =                         Funções de auxilio                          =
	 *  =======================================================================
	 */

	/** Função que exibe a saida para relatórios da simulação com base no limite */
	public static void myOutput(int level, String texto) {

		if (OUTPUT_LEVEL >= level) {
			String destaque = "|-(" + level + ")-";

			for (int d = 0; d < level; d++) {
				destaque += "------";
			}

			Tools.appendToOutput(texto + "\n");

			destaque += "> ";
			texto = destaque + texto;
			System.out.println(texto);

		}
	}

}
