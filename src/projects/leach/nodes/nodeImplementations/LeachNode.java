/* ############################################################################
 * #                                                                          #
 * #    PROJETO - Implementação do Protocolo LEACH no simulador SINALGO       #
 * #    ---------------------------------------------------------------       #
 * #                                                                          #
 * #	Professor: Dr. Alex Roschild Pinto - Redes de Computadores            #
 * #    Alunos: Gabriel Henrique Martinez Saraiva        RA: 10139-7          #
 * #            Leonardo de Oliveira Santos	             RA: 10156-7          #
 * #                                                                          #
 * #    ---------------------------------------------------------------       #
 * #                                                                          #
 * #    Arquivo: LeachNode.java                                               #
 * #    Função: Implementação do Nó LeachNode que é a base do protocolo,      #
 * #            podendo assumir a posição de Cluster Head, liderando um       #
 * #            cluster e fazendo a ponte de comunicação entre os seus        #
 * #            nós e uma Estação Base                                        #
 * #                                                                          #
 * ############################################################################
 */

package projects.leach.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import projects.leach.Funcao;
import projects.leach.nodes.messages.MsgAfiliacaoCH;
import projects.leach.nodes.messages.MsgClusterHeadDesconectado;
import projects.leach.nodes.messages.MsgDados;
import projects.leach.nodes.messages.MsgEstacaoBaseFarol;
import projects.leach.nodes.messages.MsgInvitacao;
import projects.leach.nodes.messages.MsgNoDesconectado;
import projects.leach.nodes.messages.MsgRefuseConnection;
import projects.leach.nodes.messages.MsgSetupTDMA;
import projects.leach.nodes.timers.TimerDesconectarDeCH;
import projects.leach.nodes.timers.TimerInvitarNos;
import projects.leach.nodes.timers.TimerLiberarNos;
import projects.leach.nodes.timers.TimerLimparBuffer;
import projects.leach.nodes.timers.TimerSendMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.runtime.Global;
import sinalgo.tools.Tools;

public class LeachNode extends Node {

	// Constantes =============================================================

	private final static int DIVISAO = 10;

	/** Número de rodadas do sinalgo que são um ROUND para o Leach */
	private final static int RODADAS_POR_ROUND = 200;

	/** Número de rodadas entre cada coleta de informação */
	private final static int INTERVALO_DE_COLETA = RODADAS_POR_ROUND / DIVISAO;

	/** Formatador de números para os nós. */
	private final static DecimalFormat DF = new DecimalFormat("###.0");

	/** Porcentagem de Cluster Heads desejada. */
	public final static Double P = 0.05;

	/** Cor do Cluster, definida pelo Cluster Head e copiada pelos nós. */
	public final static Color BANDEIRA_LIVRE = Color.BLACK;

	/** Número máximo de nós que um cluster pode ter. */
	public final static int NUMERO_MAXIMO_DE_NOS_POR_CLUSTER = RODADAS_POR_ROUND / DIVISAO;

	/** Energia que cada nó tem no inicio da simulação. */
	private final static Double bateriaTotal = 1000000.0; // Joules

	// Dados Compartilhados ===================================================

	/** Número de Cluster Heads ativos. */
	public static int NUMEROS_DE_CH = 0;

	/** Número total de nós na simulação. */
	public static int NUMEROS_DE_NOS = 0;

	// Caracteristicas do Nó ==================================================

	/** Estação para que os nós devem enviar seus dados quando são C.H. */
	private EstacaoBaseNode estacaoBase = null;

	/** Indica se o nó possui energia para continuar trabalhando. */
	private boolean vivo = true;

	/**
	 * Indica se o nó está configurado em um Cluster e pode começar a trasmitir.
	 */
	private boolean configurado = false;

	/** Bateria atual do nó. Quanto igual a 0 o nó é dito Morto. */
	private Double bateria = bateriaTotal; // Joules

	/** Indica quem é o lider do cluster que o nó participa. */
	private LeachNode currentClusterHead = null;

	/** Número de slots de tempo para a configuração TDMA. */
	private int tamanhoTDMA = NUMERO_MAXIMO_DE_NOS_POR_CLUSTER;

	/** Slot que um nó irá utilizar para saber quando transmitir */
	private int slotTDMA;

	// Caracteristicas do Nó quando é Cluster Head ============================

	/** Ultimo round que o nó foi cluster Head */
	private int ultimoRoundComoCH = -1;

	/** Lista de nós quando para quando o nó for um Cluster Head. */
	private ArrayList<LeachNode> listaDeNos = null;

	/** Armazena os dados de todo o cluster e para enviar para a Estação Base. */
	private StringBuilder bufferCH = null;

	/** Armazena os dados do nó, para enviar para um C.H. posteriormente. */
	private StringBuilder buffer = new StringBuilder();

	/** Flag para indicar se os dados do buffer ja podem ser descartados. */
	private boolean dadosEnviados = false;

	/** Cor do Cluster que o nó participa atualmente. Definida pelo CH. */
	private Color bandeira;

	/*
	 * #########################################################################
	 * #                Funções Sobrescritas da API do SINALGO                 #
	 * #########################################################################
	 */

	@Override
	public void init() {

		vivo = true;
		bateria = bateriaTotal;
		NUMEROS_DE_NOS++;
		ultimoRoundComoCH = (int) (-1 / P);

		slotTDMA = -1;
		tamanhoTDMA = -1;
		bandeira = BANDEIRA_LIVRE;

		if (NUMEROS_DE_CH < NUMEROS_DE_NOS * P && Math.random() < P) {
			transformarNoEmClusterHead();
		}

	}

	/** Trata as mensagens recebidas. E encaminha para alguma função handler* */
	@Override
	public void handleMessages(Inbox inbox) {

		boolean erro = false;

		// Processa todas as mensagens na fila.

		while (inbox.hasNext()) {

			Message msg = inbox.next();

			System.out.println("Nó " + ID + " -> recebeu '" + msg.getClass().getSimpleName() + "' do nó "
					+ inbox.getSender().ID + ".");

			// Mensagens da estação base --------------------------------------
			if (msg instanceof MsgEstacaoBaseFarol) {
				handleMsgEstacaoBaseFarol((EstacaoBaseNode) inbox.getSender());
				continue;
			}
			// ----------------------------------------------------------------

			LeachNode sender = (LeachNode) inbox.getSender();

			if (getFuncao() == Funcao.ClusterHead) { // Cluster Head

				// ------------------------------------------------------------
				if (msg instanceof MsgAfiliacaoCH) {
					handleMsgAckInvitacao(sender);
				}
				// ------------------------------------------------------------
				else if (msg instanceof MsgNoDesconectado) {
					handleMsgNoDesconectado(sender);
				}
				// ------------------------------------------------------------
				else if (msg instanceof MsgDados) {
					handleMsgDados((MsgDados) msg, sender);
				}
				// ------------------------------------------------------------
				else {
					erro = true;
				}

			} else { // Nó COMUM

				// ------------------------------------------------------------
				if (msg instanceof MsgInvitacao) {
					handleMsgInvitacao((LeachNode) inbox.getSender());
				}
				// ------------------------------------------------------------
				else if (msg instanceof MsgClusterHeadDesconectado) {
					if (sender == getClusterHead()) {
						handleMsgDeconexaoCH();
					}
				}
				// ------------------------------------------------------------
				else if (msg instanceof MsgSetupTDMA) {
					handleMsgSetupTDMA((MsgSetupTDMA) msg);
				}
				// ------------------------------------------------------------
				else if (msg instanceof MsgRefuseConnection) {
					handleMsgRefuseConnection((LeachNode) inbox.getSender());
				}
				// ------------------------------------------------------------
				else {
					erro = true;
				}
			}

			if (erro) {
				JOptionPane.showMessageDialog(null, "Nó " + ID + ":  Mensagem não esperada recebida: " + msg.toString()
						+ " de " + sender.ID);
				System.exit(-1);
			}
		}
	}

	/** Função executada antes de cada passo da simulação por cada nó */
	@Override
	public void preStep() {

		if (isVivo()) {

			// Coleta os dados e adiciona no buffer.
			coletarInformacaoDoSensor();

			// Gasta um round como ClusterHead
			if (getFuncao() == Funcao.ClusterHead) {
				if (ultimoRoundComoCH != getRound()) {
					transformarClusterHeadEmNo();
				}
			} else if (getFuncao() == Funcao.MembroDeCluster && isConfigurado()) {
				// Testa se ja é o seu SLOT de transmissão...
				if (Global.currentTime % tamanhoTDMA == slotTDMA) {
					transmitirDadosAoCH();
				}
			}

			// Deve se tornar CH?
			if (isInicioDeRound()) {

				if (getFuncao() != Funcao.ClusterHead) {

					Double r = Math.random();
					Double t = T();

					if (r < t) {
						transformarNoEmClusterHead();
					}
				}
			}
		}
	}

	/** Função que desenha o nó na tela */
	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {

		// Margem entre o texto e o final a borda do objeto
		int borda;
		Font font;
		int fontSize;
		String textoClasse;

		if (getFuncao() == Funcao.ClusterHead) {
			borda = 20;
			fontSize = 18;
			textoClasse = ID + " [" + listaDeNos.size() + "]";

		} else {
			borda = 6;
			fontSize = 14;
			textoClasse = ID + " (" + buffer.length() + ")";
		}

		if (!isVivo()) {
			borda = 1;
			fontSize = 14;

		}

		font = new Font(null, 0, (int) (fontSize * pt.getZoomFactor()));
		g.setFont(font);

		String textoEnergia = DF.format(getBateriaPorcentagem()) + "%";

		FontMetrics fm = g.getFontMetrics(font);

		// *2 pois são 2 linhas d texto
		int h = (int) Math.ceil(fm.getHeight());
		int wC = (int) Math.ceil(fm.stringWidth(textoClasse));
		int wE = (int) Math.ceil(fm.stringWidth(textoEnergia));
		int maior = Math.max(h, Math.max(wC, wE));

		drawingSizeInPixels = maior;

		pt.translateToGUIPosition(getPosition());

		g.setColor(getBandeira());

		int d = maior;

		if (highlight) {
			g.setColor(Color.RED);
			borda += 6;
		}

		borda *= pt.getZoomFactor();

		g.setColor(getBandeira());

		int meiaBorda = borda / 2;

		// Desenha o objeto com base em sua patente e se está vivo ou não

		if (isVivo()) {

			if (getFuncao() == Funcao.ClusterHead) {
				g.fillOval(pt.guiX - d / 2 - meiaBorda, pt.guiY - d / 2 - meiaBorda, d + borda, d + borda);
			} else {
				g.fillRect(pt.guiX - d / 2 - meiaBorda, pt.guiY - d / 2 - meiaBorda, d + borda, d + borda);
			}

		} else {

			g.setColor(Color.BLACK);
			g.fillRect(pt.guiX - d / 2 - meiaBorda, pt.guiY - d / 2 - meiaBorda, d + borda, d + borda);
			g.setColor(Color.RED);
			g.fillRect(pt.guiX - d / 2, pt.guiY - d / 2, d, d);

		}

		// Desenha o status no centro do objeto.

		g.setColor(Color.WHITE);
		if (isVivo()) {
			g.drawString(textoClasse, pt.guiX - wE / 2, pt.guiY - h / 8);
			g.drawString(textoEnergia, pt.guiX - wE / 2, pt.guiY + h - h / 8);
		} else {
			g.drawString(textoClasse, pt.guiX - wE / 2, pt.guiY - h / 8);
			g.drawString("=(", pt.guiX - wE / 2, pt.guiY + h - h / 8);

		}

	}

	/** Ações após o passo de simulação */
	@Override
	public void postStep() {

		if (getEnergiaRestante() == 0) {
			matarNo();
		}

	}

	@Override
	public void neighborhoodChange() {

	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {

	}

	/* 
	 * ########################################################################
	 * #                        Métodos Próprios                              #
	 * #                        ----------------                              #
	 * #                      Handler de Mensagens                            #
	 * ########################################################################
	 */

	/**
	 * Atuação: Cluster Head<br>
	 * Descrição: Trata o sinal de aceite do convite do nó para o cluster atual
	 */
	public void handleMsgAckInvitacao(LeachNode sender) {

		if (listaDeNos.size() < NUMERO_MAXIMO_DE_NOS_POR_CLUSTER) {
			listaDeNos.add(sender);

			MsgSetupTDMA m = new MsgSetupTDMA(tamanhoTDMA, listaDeNos.size());

			TimerSendMessage t = new TimerSendMessage(m, sender);

			t.startRelative(1, this);
		} else {

			TimerSendMessage t = new TimerSendMessage(new MsgRefuseConnection(), sender);
			t.startRelative(1, this);

		}
	}

	/**
	 * Atuação: Cluster Head<br>
	 * Descrição: Trata os dados recebidos de um Nó e os armazena no buffer do
	 * CH.
	 */
	public void handleMsgDados(MsgDados m, LeachNode ln) {
		double custo = 0;
		if (estacaoBase != null) {
			custo = getCustoTransmissao(bufferCH.length() + m.toString().length(), estacaoBase);
		}
		if (custo < getEnergiaRestante()) {
			bufferCH.append("Nó " + ln.ID + ":'" + m.dados + "'\n");
			// System.out.println(bufferCH.toString());
		} else {

		}
	}

	/**
	 * Atuação: Nó<br>
	 * Descrição: Trata o sinal desconexão por parte do Cluster Head
	 */
	public void handleMsgDeconexaoCH() {

		setBandeira(BANDEIRA_LIVRE);
		setClusterHead(null);
		configurado = false;
		tamanhoTDMA = -1;
		slotTDMA = -1;

	}

	/**
	 * Atuação: Todos Nós<br>
	 * Descrição: Trata o sinal de sinalização emitido pelas estações base de
	 * tempo em tempo.
	 */
	public void handleMsgEstacaoBaseFarol(EstacaoBaseNode eb) {
		estacaoBase = eb;
	}

	/**
	 * Atuação: Nó<br>
	 * Descrição: Trata o sinal invitação de um CH baseado em sua distancia.
	 */
	public void handleMsgInvitacao(LeachNode ch) {

		// Se ainda não tiver um CH aceita, caso tenha escolhe pelo
		// mais próximo.

		TimerSendMessage tsm;

		if (getClusterHead() == null) {

			setClusterHead(ch);

			tsm = new TimerSendMessage(new MsgAfiliacaoCH(), ch);
			tsm.startRelative(1, this);

		} else {

			double distanciaCHAtual = getPosition().squareDistanceTo(getClusterHead().getPosition());

			double distanciaCHNovo = getPosition().squareDistanceTo(ch.getPosition());

			System.out.println("ATUAL:" + distanciaCHAtual + " NOVA:" + distanciaCHNovo);

			if (distanciaCHNovo < distanciaCHAtual) {

				TimerDesconectarDeCH tdc = new TimerDesconectarDeCH(getClusterHead());
				tdc.startRelative(1, this);

				setClusterHead(ch);

				tsm = new TimerSendMessage(new MsgAfiliacaoCH(), ch);
				tsm.startRelative(2, this);

			}
		}
	}

	/**
	 * Atuação: Cluster Head<br>
	 * Descrição: Trata mensagens de desconexão dos Nós
	 */
	public void handleMsgNoDesconectado(LeachNode sender)

	{
		listaDeNos.remove((LeachNode) inbox.getSender());

	}

	/**
	 * Atuação: Nó<br>
	 * Descrição: Trata de setup da transmissão do nó
	 */
	public void handleMsgSetupTDMA(MsgSetupTDMA m) {

		System.out.println("Nó " + ID + " -> ttdma:" + m.tamanhoTdma + " slot: " + m.slot);

		tamanhoTDMA = m.tamanhoTdma;
		slotTDMA = m.slot;
		setClusterHead((LeachNode) inbox.getSender());

		setConfigurado(true);
	}

	/**
	 * Atuação: Nó<br>
	 * Descrição: Trata quando o nó recebe uma mensagem de conexão rejeitada
	 */
	public void handleMsgRefuseConnection(LeachNode ch) {

		if (ch == getClusterHead()) {
			setClusterHead(null);
		}

	}

	/* 
	 * ########################################################################
	 * #                        Métodos Próprios                              #
	 * #                        ----------------                              #
	 * #                         Funções Extras                               #
	 * ########################################################################
	 */

	/**
	 * Função que cálcula o indice de Threshold para verificar se um nó pode ou
	 * não virar CH.
	 */
	public double T() {

		int r = getRound();

		if ((r - ultimoRoundComoCH) <= (1 / P)) {
			return 0;
		}

		return P / (1 - P * (r % (1 / P)));
	}

	/**
	 * Função que envia mensagens para um determinado nó consumindo energia com
	 * base na distancia do destino e no tamanho da mensagem
	 */
	public void mySend(Message m, Node target) {
		// Cálcula a distancia e a potencia necessária para enviar a mensagem

		double custo = getCustoTransmissao(m.toString().length(), target);
		if (consumirEnerigia(custo)) {
			System.out.println("Nó " + ID + " -> enviando " + m.getClass().getSimpleName() + " para " + target.ID
					+ " (" + custo + ")");
			send(m, target);
			vivo = true;
		} else {
			vivo = false;
		}
	}

	/** Define o nó como morto */
	public void matarNo() {

		setBandeira(BANDEIRA_LIVRE);
		tamanhoTDMA = -1;
		slotTDMA = -1;
		currentClusterHead = null;
		listaDeNos = null;
		bufferCH = null;
		buffer = null;

		vivo = false;
	}

	/** Rerorna a função atual do Nó na simulação */
	public Funcao getFuncao() {

		if (getClusterHead() == this) {
			return Funcao.ClusterHead;
		} else if (getClusterHead() != null) {
			return Funcao.MembroDeCluster;
		}

		return Funcao.No;

	}

	/** Cálcula o custo da transmissão de uma mensagem até um nó */
	public double getCustoTransmissao(int tamanhoMsg, Node destino) {

		Double custoEnergeticoPorBit = getPosition().squareDistanceTo(destino.getPosition());

		return custoEnergeticoPorBit * tamanhoMsg * 1 / 1000;

	}

	/** Transforma o Nó em um Cluster Head */
	private void transformarNoEmClusterHead() {

		if (getFuncao() == Funcao.MembroDeCluster) {
			sinalizarDesconexaoComClusterHead();
		}

		this.setConfigurado(false);

		setClusterHead(this);

		// Incrimenta o número de CH, devido ao limite de CHs ao mesmo tempo.

		NUMEROS_DE_CH++;

		// Define a cor do grupo.
		int cor[] = new int[3];
		cor[0] = (int) (Math.random() * 255);
		cor[1] = (int) (Math.random() * 255);
		cor[2] = (int) (Math.random() * 255);
		setBandeira(new Color(cor[0], cor[1], cor[2]));

		ultimoRoundComoCH = getRound();

		setListaDeNos(new ArrayList<LeachNode>());

		bufferCH = new StringBuilder();

		tamanhoTDMA = NUMERO_MAXIMO_DE_NOS_POR_CLUSTER;

		// Agenda invitação dos nós para o Cluster Head.
		TimerInvitarNos tin = new TimerInvitarNos();
		tin.startRelative(1, this);

		Tools.appendToOutput("Nó " + ID + " virou CH\n");
	}

	/**
	 * Função que transforma um Cluster Head em um nó normal
	 * <b>Só é utilizada para fazer um CH virar um nó normal.</b>
	 */
	private void transformarClusterHeadEmNo() {

		transmitirDadosParaEB();

		// Envia sinal de desconexão aos seus filinhos :(
		TimerLiberarNos tln = new TimerLiberarNos();
		tln.startRelative(1, this);

		// Sinaliza que existe um CH a menos.

		NUMEROS_DE_CH--;

		// Desconfigura o cluster head
		setClusterHead(null);
		setListaDeNos(null);
		setBandeira(BANDEIRA_LIVRE);
		tamanhoTDMA = -1;
		slotTDMA = -1;

		setConfigurado(false);

	}

	public void transmitirDadosParaEB() {

		if (estacaoBase != null) {
			MsgDados m = new MsgDados(bufferCH.toString());
			TimerSendMessage tsm = new TimerSendMessage(m, estacaoBase);

			tsm.startRelative(1, this);
		}
	}

	public boolean isVivo() {
		return vivo;
	}

	public void sinalizarDadosEnviados() {
		dadosEnviados = true;
	}

	public void limparBuffer() {
		buffer = new StringBuilder();
	}

	public Double getBateriaPorcentagem() {
		return bateria / bateriaTotal * 100;
	}

	public void transmitirDadosAoCH() {

		if (!(slotTDMA == -1 || tamanhoTDMA == -1)) {

			MsgDados m = new MsgDados(getBuffer());

			TimerSendMessage tsm = new TimerSendMessage((Message) m, currentClusterHead);
			tsm.startRelative(1, this);

			TimerLimparBuffer tlb = new TimerLimparBuffer();
			tlb.startRelative(2, this);

		}
	}

	@NodePopupMethod(menuText = "Transformar em Cluster Head")
	public void popUpMenuTranformarClusterHead() {

		transformarNoEmClusterHead();

	}

	@NodePopupMethod(menuText = "Transformar em Nó Normal")
	public void popUpMenuTranformarNo() {

		transformarClusterHeadEmNo();

	}

	@NodePopupMethod(menuText = "Exibir dados do Nó")
	public void popUpMenuExibirDadosDoNo() {

		String texto = x();
		JOptionPane.showMessageDialog(null, texto);

	}

	@NodePopupMethod(menuText = "Definir bateria no Nó")
	public void popUpMenuDefinirBateria() {

		String sqtd = JOptionPane.showInputDialog("Quantidade em %?");
		double qtd = Double.parseDouble(sqtd);
		if (qtd < 0) {
			JOptionPane.showMessageDialog(null, "Valor inválido!");

		} else {
			bateria = bateriaTotal * qtd / 100;
		}

	}

	@NodePopupMethod(menuText = "Exibir dados em Buffer")
	public void popUpMenuExibirDadosEmBuffer() {

		exibirDadosEmBuffer();

	}

	public void exibirDadosEmBuffer() {
		if (buffer != null) {
			JOptionPane.showMessageDialog(null, getBuffer());
		} else {
			JOptionPane.showMessageDialog(null, "Nó ainda não possui um buffer!");
		}
	}

	@NodePopupMethod(menuText = "Exibir Dados do CH")
	public void popUpMenuExibirNoCH() {
		if (bufferCH != null) {
			JOptionPane.showMessageDialog(null, getBufferCH());
		} else {
			JOptionPane.showMessageDialog(null, "Nó ainda não possui um buffer de Cluster Head!");
		}

	}

	public void exibirDadosNoCH() {
		JOptionPane.showMessageDialog(null, getBufferCH());
	}

	public Color getBandeira() {
		return bandeira;
	}

	public void setBandeira(Color bandeira) {
		this.bandeira = bandeira;
	}

	public ArrayList<LeachNode> getListaDeNos() {
		return listaDeNos;
	}

	public void setListaDeNos(ArrayList<LeachNode> listaDeNos) {
		this.listaDeNos = listaDeNos;
	}

	public LeachNode getClusterHead() {
		return currentClusterHead;
	}

	public void sinalizarDesconexaoComClusterHead() {

		TimerDesconectarDeCH tddc = new TimerDesconectarDeCH(getClusterHead());
		tddc.startRelative(1, this);

	}

	public void setClusterHead(LeachNode clusterHead) {

		currentClusterHead = clusterHead;

		if (currentClusterHead != null) {
			setBandeira(getClusterHead().getBandeira());
		} else {
			setBandeira(BANDEIRA_LIVRE);
		}
	}

	public Double getEnergiaRestante() {
		return bateria;
	}

	public void limparBufferCh() {
		bufferCH = new StringBuilder();
	}

	public boolean consumirEnerigia(Double j) {

		if (bateria - j >= 0) {
			bateria -= j;

			return true;
		}
		System.out.println("Nó " + ID + " -> sem energia! (" + DF.format(j) + "j)");
		return false;
	}

	public boolean isClusterHead() {
		return (getClusterHead() == this);
	}

	// =========================================

	private void coletarInformacaoDoSensor() {

		// A cada 10 turnos coleta informação
		if (dadosEnviados) {
			buffer = new StringBuilder();
			dadosEnviados = false;
		}

		if (Global.currentTime % INTERVALO_DE_COLETA == 0) {
			buffer.append((int) (Math.random() * 10));
		}
	}

	public String getBuffer() {
		return buffer.toString();
	}

	public String getBufferCH() {
		return bufferCH.toString();
	}

	public boolean isConfigurado() {
		return configurado;
	}

	public void setConfigurado(boolean configurado) {
		this.configurado = configurado;
	}

	public int getRound() {

		return (int) Global.currentTime / RODADAS_POR_ROUND;

	}

	public boolean isInicioDeRound() {
		return Global.currentTime % RODADAS_POR_ROUND == 0;
	}

	public String x() {
		String me = "";
		me += "SIMULAÇÃO DO LEACH - STATUS DE NÓ\n";
		me += "Round atual:			" + getRound() + "\n";
		me += "Leach Node ID:		" + ID + "\n";
		me += "Estado:				" + (isVivo() ? "vivo" : "morto") + "\n";
		me += "Função:				" + getFuncao() + "\n";
		me += "Cluster Head:		" + currentClusterHead != null ? currentClusterHead.ID : "NENHUM" + "\n";
		me += "Energia atual:		" + getBateriaPorcentagem() + " (" + getEnergiaRestante() + ")" + "\n";
		me += "Estação Base:		" + estacaoBase != null ? estacaoBase.ID : "NÃO DEFINIDO AINDA" + "\n";
		me += "Buffer interno:		" + "Tamanho: " + buffer.length() + " '" + buffer + "'" + "\n";
		me += "Ultimo Round como CH:" + ultimoRoundComoCH + "\n";
		if (getFuncao() == Funcao.ClusterHead) {
			me += "Buffer CH:			" + "Tamanho: " + bufferCH.length() + " '" + bufferCH + "'" + "\n";
			me += "Tamanho do Cluster:	" + listaDeNos.size();
		} else if (getFuncao() == Funcao.MembroDeCluster) {
			me += "TDMA - Tamanho:		" + tamanhoTDMA + "\n";
			me += "TDMA - Slot:			" + slotTDMA + "\n";
		}

		return me;
	}

	@Override
	public String toString() {

		String me = "";
		me += "SIMULAÇÃO DO LEACH - STATUS DE NÓ\n";
		me += "Round atual:			" + getRound() + "\n";
		me += "Leach Node ID:		" + ID + "\n";
		me += "Estado:				" + (isVivo() ? "vivo" : "morto") + "\n";
		me += "Função:				" + getFuncao() + "\n";
		me += "Cluster Head:		" + currentClusterHead != null ? currentClusterHead.ID : "NENHUM" + "\n";
		me += "Energia atual:		" + getBateriaPorcentagem() + " (" + getEnergiaRestante() + ")" + "\n";
		me += "Estação Base:		" + estacaoBase != null ? estacaoBase.ID : "NÃO DEFINIDO AINDA" + "\n";
		me += "Buffer interno:		" + "Tamanho: " + buffer.length() + " '" + buffer + "'" + "\n";
		me += "Ultimo Round como CH:" + ultimoRoundComoCH + "\n";
		if (getFuncao() == Funcao.ClusterHead) {
			me += "Buffer CH:			" + "Tamanho: " + bufferCH.length() + " '" + bufferCH + "'" + "\n";
			me += "Tamanho do Cluster:	" + listaDeNos.size();
		} else if (getFuncao() == Funcao.MembroDeCluster) {
			me += "TDMA - Tamanho:		" + tamanhoTDMA + "\n";
			me += "TDMA - Slot:			" + slotTDMA + "\n";
		}

		return me;
	}
}
