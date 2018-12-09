package trabalhoSO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

// trabalho sendo desevolvido por Mateus e Yan
public class Algoritmos {

	public static void FCFS(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		String algoritmoExecutado = "FCFS";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;

		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresFCFS = clone;
		Iterator<R> iter = valoresFCFS.iterator();
		int cilindroAnterior = startPosition;
		int indexParciais = 0, distancia = 0;
		int[] accessTimesParciais = new int[qntDeRs];
		int rAccessTimeAtual = 0;
		// enquanto houver um proximo valor na lista
		while (iter.hasNext()) {
			// para cada valor na lista
			for (int i = 0; i < valoresFCFS.size(); i++) {
				// verifica se o tempo de chegada eh menor ou igual a soma dos accessTimes
				if (valoresFCFS.get(i).getTempoDeChegada() <= somaAccessTimes) {
					// calcula o accessTime de acordo com a formula (modulo entre cilindro anterior
					// - cilindro atual + C + D)
					distancia = Math.abs(valoresFCFS.get(i).getCilindroRequisitado() - cilindroAnterior);
					rAccessTimeAtual = valoresFCFS.get(i).getTrilha() + distancia
							+ valoresFCFS.get(i).getTransferTime();
					somaAccessTimes += rAccessTimeAtual;
					accessTimesParciais[indexParciais] = rAccessTimeAtual;
					cilindroAnterior = valoresFCFS.get(i).getCilindroRequisitado();
					valoresFCFS.remove(i);
					i--;
					indexParciais++;
				}
			}
		}

		// calcula accessTime medio e waitingTime medio
		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);
	}

	public static void SSTF(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresSSTF = clone;
		String algoritmoExecutado = "SSTF";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;
		int[] accessTimesParciais = new int[qntDeRs];
		int cilindroAnterior = startPosition;
		int menorDistancia = numSectors;
		int indexDeMenorDistancia = 0, indexParciais = 0, distancia = 0;
		Iterator<R> iter = valoresSSTF.iterator();
		// enquanto houver um proximo valor na lista
		while (iter.hasNext() == true) {
			// para cada valor na lista (indo de tras pra frente para pegar sempre o maior
			// em caso de igualdade de distancia)
			for (int i = valoresSSTF.size() - 1; i >= 0; i--) {
				// distancia é igual ao modulo da diferenca entre o cilindro requisitado - o
				// cilindro anterior
				distancia = Math.abs(valoresSSTF.get(i).getCilindroRequisitado() - cilindroAnterior);
				// se a distancia for menor que a menor distancia e o tempo de chegada for menor
				// que o accesstime
				if (distancia < menorDistancia && valoresSSTF.get(i).getTempoDeChegada() <= somaAccessTimes) {
					// a menor distancia eh atualizada e o indice do R que possuir esta menor
					// distancia eh atribuida a uma variavel
					menorDistancia = distancia;
					indexDeMenorDistancia = i;
				}
			}

			// informando o cilindro anterior como o cilindro que possuir a menor distancia
			// atual
			cilindroAnterior = valoresSSTF.get(indexDeMenorDistancia).getCilindroRequisitado();
			// definindo o accesstime do r atual e, logo apos, somando-o
			int rAccessTimeAtual = valoresSSTF.get(indexDeMenorDistancia).getTrilha() + menorDistancia
					+ valoresSSTF.get(indexDeMenorDistancia).getTransferTime();
			somaAccessTimes += rAccessTimeAtual;
			// colocando os accesstimes dos Rs parciais neste vetor para calcular o waiting
			// time posteriormente
			accessTimesParciais[indexParciais] = rAccessTimeAtual;
			// removendo o R lido
			valoresSSTF.remove(indexDeMenorDistancia);
			// atribuindo valor aleatorio ao index de menor distancia para nao confundir
			indexDeMenorDistancia = -1;
			// menordistancia recebe o maior valor possivel + 1 (não é possivel que nenhum
			// cilindro seja menor que isso)
			menorDistancia = numSectors;
			indexParciais++;

		}

		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);
	}

	public static void SCAN(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		String algoritmoExecutado = "SCAN";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;
		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresSCAN = clone;
		Iterator<R> iter = valoresSCAN.iterator();
		int cilindroAnterior = startPosition;
		int indexParciais = 0, distancia = 0;
		int[] accessTimesParciais = new int[qntDeRs];
		int rAccessTimeAtual = 0;
		int limiteDireita = numSectors - 1;
		int aux = 0; // esse auxiliar calcula a distancia do ultimo requisito ate o final do disco e
						// depois soma tambem o retorno completo do disco

		// ordenando por posicao do cilindro
		valoresSCAN.sort(Comparator.naturalOrder());

		// enquanto houver um proximo valor na lista
		while (iter.hasNext()) {
			// indo para a direita
			for (int i = 0; i < valoresSCAN.size(); i++) {

				if (valoresSCAN.get(i).getCilindroRequisitado() >= cilindroAnterior
						&& valoresSCAN.get(i).getTempoDeChegada() <= somaAccessTimes) {
					distancia = Math.abs(valoresSCAN.get(i).getCilindroRequisitado() - cilindroAnterior);
					// atribui accesstime do r atual o seu valor de C e D + a distancia + aux (que
					// eh toda a transferencia ate chegar nele, como o retorno do disco por exemplo)
					rAccessTimeAtual = valoresSCAN.get(i).getTrilha() + distancia + valoresSCAN.get(i).getTransferTime()
							+ aux;
					// incrementa a soma de accessTimes pelo valor do accessTime do r atual
					somaAccessTimes += rAccessTimeAtual;
					// coloca o accessTime do R atual no vetor para calcular o waiting time
					// posteriormente
					accessTimesParciais[indexParciais] = rAccessTimeAtual;
					// defini o cilindro anterior como o cilindro atual
					cilindroAnterior = valoresSCAN.get(i).getCilindroRequisitado();
					// removendo o cilindro lido
					valoresSCAN.remove(i);
					// decrementando i devido a remocao de um R
					i--;
					// incrementa a variavel que controla o vetor de accessTimes dos Rs parciais
					indexParciais++;
					// zera a transferencia que foi necessaria pra chegar nesse R
					aux = 0;

				}
			}
			// auxiliar calcula a distancia entre o limite a direita e o cilindro anterior
			aux = Math.abs(limiteDireita - cilindroAnterior);
			// cilindro anterior eh o limite a direita
			cilindroAnterior = limiteDireita;
			// indo para a esquerda
			for (int i = valoresSCAN.size() - 2; i >= 0; i--) {
				if (valoresSCAN.get(i).getCilindroRequisitado() <= cilindroAnterior
						&& valoresSCAN.get(i).getTempoDeChegada() <= somaAccessTimes) {
					distancia = Math.abs(valoresSCAN.get(i).getCilindroRequisitado() - cilindroAnterior);
					// atribui accesstime do r atual o seu valor de C e D + a distancia + aux (que
					// eh toda a transferencia ate chegar nele, como o retorno do disco por exemplo)
					rAccessTimeAtual = valoresSCAN.get(i).getTrilha() + distancia + valoresSCAN.get(i).getTransferTime()
							+ aux;
					// incrementa a soma de accessTimes pelo valor do accessTime do r atual
					somaAccessTimes += rAccessTimeAtual;
					// coloca o accessTime do R atual no vetor para calcular o waiting time
					// posteriormente
					accessTimesParciais[indexParciais] = rAccessTimeAtual;
					// defini o cilindro anterior como o cilindro atual
					cilindroAnterior = valoresSCAN.get(i).getCilindroRequisitado();
					// removendo o cilindro lido
					valoresSCAN.remove(i);
					// decrementando i devido a remocao de um R
					i++;
					// incrementa a variavel que controla o vetor de accessTimes dos Rs parciais
					indexParciais++;
					// zera a transferencia que foi necessaria pra chegar nesse R
					aux = 0;
				}
			}
			// aux eh o cilindro anterior - 0, ou seja, cilindro atenrior
			aux = Math.abs(cilindroAnterior);
			// zera cilindro anterior, pois ele esta na primeira posicao (retornou no disco)
			cilindroAnterior = 0;

		}

		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);

	}

	public static void C_SCAN(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		String algoritmoExecutado = "C-SCAN";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;
		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresC_SCAN = clone;
		Iterator<R> iter = valoresC_SCAN.iterator();
		int cilindroAnterior = startPosition;
		int indexParciais = 0, distancia = 0;
		int[] accessTimesParciais = new int[qntDeRs];
		int rAccessTimeAtual = 0;
		int limiteDireita = numSectors - 1;
		int aux = 0; // esse auxiliar calcula a distancia do ultimo requisito ate o final do disco e
						// depois soma tambem o retorno completo do disco

		// ordenando por posicao do cilindro
		valoresC_SCAN.sort(Comparator.naturalOrder());

		// enquanto houver um proximo valor na lista
		while (iter.hasNext()) {

			// percorrendo a lista
			for (int i = 0; i < valoresC_SCAN.size(); i++) {

				// se o cilindro requisitado for maior ou igual ao cilindro anterior e ja tiver
				// chegado
				if (valoresC_SCAN.get(i).getCilindroRequisitado() >= cilindroAnterior
						&& valoresC_SCAN.get(i).getTempoDeChegada() <= somaAccessTimes) {
					// calcula a distancia entre o cilindro requerido e o anterior
					distancia = Math.abs(valoresC_SCAN.get(i).getCilindroRequisitado() - cilindroAnterior);
					// atribui accesstime do r atual o seu valor de C e D + a distancia + aux (que
					// eh toda a transferencia ate chegar nele, como o retorno do disco por exemplo)
					rAccessTimeAtual = valoresC_SCAN.get(i).getTrilha() + distancia
							+ valoresC_SCAN.get(i).getTransferTime() + aux;
					// incrementa a soma de accessTimes pelo valor do accessTime do r atual
					somaAccessTimes += rAccessTimeAtual;
					// coloca o accessTime do R atual no vetor para calcular o waiting time
					// posteriormente
					accessTimesParciais[indexParciais] = rAccessTimeAtual;
					// defini o cilindro anterior como o cilindro atual
					cilindroAnterior = valoresC_SCAN.get(i).getCilindroRequisitado();
					// removendo o cilindro lido
					valoresC_SCAN.remove(i);
					// decrementando i devido a remocao de um R
					i--;
					// incrementa a variavel que controla o vetor de accessTimes dos Rs parciais
					indexParciais++;
					// zera a transferencia que foi necessaria pra chegar nesse R
					aux = 0;
				}

			}

			// quando chegar aqui, quer dizer que chegou na ultima requisicao e nao ha mais
			// nenhum ate o fim do disco
			// portanto sera necessario ir ultimo cilindro lido ate o limite a direita e
			// depois voltar tudo, ou seja,
			// modulo entre cilindro anterior e o limite da direita + limite da direita
			aux = Math.abs(cilindroAnterior - limiteDireita) + limiteDireita;
			// como o disco retornou ao cilindro inicial, o cilindro anterior se torna o
			// primeiro, ou seja, 0
			cilindroAnterior = 0;
		}

		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);

	}

	public static void C_LOOK(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		String algoritmoExecutado = "C-LOOK";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;
		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresC_LOOK = clone;
		Iterator<R> iter = valoresC_LOOK.iterator();
		int distancia = 0, rAccessTimeAtual = 0, indexParciais = 0;
		int cilindroAnterior = startPosition;
		int[] accessTimesParciais = new int[qntDeRs];
		int i;
		// ordenando por posicao do cilindro
		valoresC_LOOK.sort(Comparator.naturalOrder());

		// enquanto houver um proximo valor na lista
		while (iter.hasNext()) {
			// vai para a direita
			for (i = 0; i < valoresC_LOOK.size(); i++) {
				// se o cilindro do R atual for maior ou igual ao cilindro anterior e ele tiver
				// chegado, eh executado
				if (valoresC_LOOK.get(i).getCilindroRequisitado() >= cilindroAnterior
						&& valoresC_LOOK.get(i).getTempoDeChegada() <= somaAccessTimes) {

					// eh calculada a distancia, rAccesTimeAtual, soma dos tempos de acesso, etc
					distancia = Math.abs(valoresC_LOOK.get(i).getCilindroRequisitado() - cilindroAnterior);
					rAccessTimeAtual = valoresC_LOOK.get(i).getTrilha() + distancia
							+ valoresC_LOOK.get(i).getTransferTime();
					somaAccessTimes += rAccessTimeAtual;
					accessTimesParciais[indexParciais] = rAccessTimeAtual;
					// cilindro anterior passa a ser o cilindro lido
					cilindroAnterior = valoresC_LOOK.get(i).getCilindroRequisitado();
					// removido
					valoresC_LOOK.remove(i);
					i--;
					indexParciais++;
				}
			}

			// enquanto houver um proximo valor na lista
			if (iter.hasNext()) {
				// j eh o primeiro cilindro
				int j = 0;
				// este while serve para, no momento de retornar ao primeiro cilindro e retomar
				// os atendimentos, pegar o primeiro cilindro do R que chegou e partir dele para
				// a direita
				// enquanto o tempo de chegada do valor lido for maior que a soma do tempo de
				// acesso (nao chegou), e, enquanto J nao chegar no limite a direita
				while (valoresC_LOOK.get(j).getTempoDeChegada() > somaAccessTimes && j < valoresC_LOOK.size())
					// aumenta o j, pois esse cilindro nao pode ser lido
					j++;

				// achou um valor que eh possivel ser lido, portanto ele é lido (indice j)
				// a leitura eh exatamente igual ao if anterior, diferenca somente no indice
				distancia = Math.abs(valoresC_LOOK.get(j).getCilindroRequisitado() - cilindroAnterior);
				rAccessTimeAtual = valoresC_LOOK.get(j).getTrilha() + distancia
						+ valoresC_LOOK.get(j).getTransferTime();
				somaAccessTimes += rAccessTimeAtual;
				accessTimesParciais[indexParciais] = rAccessTimeAtual;
				cilindroAnterior = valoresC_LOOK.get(j).getCilindroRequisitado();
				valoresC_LOOK.remove(j);
				i--;
				indexParciais++;
			}
		}

		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);

	}

	public static void MY(int numSectors, int numTracks, int startPosition, ArrayList<R> valores, int qntDeRs,
			int somaTemposDeChegada, String pathWriter) {
		String algoritmoExecutado = "MY";
		float accessTime = 0, waitingTime = 0, somaAccessTimes = 0;
		ArrayList<R> clone = (ArrayList<R>) valores.clone();
		ArrayList<R> valoresMY = clone;
		Iterator<R> iter = valoresMY.iterator();
		int distancia = 0, rAccessTimeAtual = 0, indexParciais = 0;
		int cilindroAnterior = startPosition;
		int[] accessTimesParciais = new int[qntDeRs];
		boolean achou = false;
		int qntEsquerda = 0, qntDireita = 0;
		int i = 0;

		// ordenando por posicao do cilindro
		valoresMY.sort(Comparator.naturalOrder());

		// primeiramente eh calculado o acesso ate o primeiro que chegou
		while (achou == false) {
			if (valoresMY.get(i).getTempoDeChegada() <= somaAccessTimes) {
				// calcula o accessTime de acordo com a formula (modulo entre cilindro anterior
				// - cilindro atual + C + D)
				distancia = Math.abs(valoresMY.get(i).getCilindroRequisitado() - cilindroAnterior);
				rAccessTimeAtual = valoresMY.get(i).getTrilha() + distancia + valoresMY.get(i).getTransferTime();
				somaAccessTimes += rAccessTimeAtual;
				accessTimesParciais[indexParciais] = rAccessTimeAtual;
				cilindroAnterior = valoresMY.get(i).getCilindroRequisitado();
				valoresMY.remove(i);
				indexParciais++;
				i++;
				achou = true;
			}
			i++;
		}

		// levando em consideracao que geralmente os valores chegam apos a execucao do
		// primeiro, agora podemos verificar a quantidade de cilindros existentes a
		// esquerda e a direita do cilindro previamente lido (mesmo sabendo que
		// geralmente os cilindros já chegaram, ainda eh testado se ele chegou para caso
		// haja algo para chgar ainda uma vez que nao pode prever o futuro na execucao
		// do algoritmo)
		for (i = 0; i < valoresMY.size(); i++) {
			if (valoresMY.get(i).getCilindroRequisitado() >= cilindroAnterior
					&& valoresMY.get(i).getTempoDeChegada() < somaAccessTimes) {
				qntDireita++;
			} else if (valoresMY.get(i).getTempoDeChegada() < somaAccessTimes) {
				qntEsquerda++;
			}
		}

		if (qntDireita >= qntEsquerda) {
			while (iter.hasNext()) {

				for (i = 0; i < valoresMY.size(); i++) {
					// eh calculada a distancia, rAccesTimeAtual, soma dos tempos de acesso, etc
					if (valoresMY.get(i).getTempoDeChegada() <= somaAccessTimes) {

						distancia = Math.abs(valoresMY.get(i).getCilindroRequisitado() - cilindroAnterior);
						rAccessTimeAtual = valoresMY.get(i).getTrilha() + distancia
								+ valoresMY.get(i).getTransferTime();
						somaAccessTimes += rAccessTimeAtual;
						accessTimesParciais[indexParciais] = rAccessTimeAtual;
						// cilindro anterior passa a ser o cilindro lido
						cilindroAnterior = valoresMY.get(i).getCilindroRequisitado();
						// removido
						valoresMY.remove(i);
						i--;
						indexParciais++;
					}
				}

				for (i = valoresMY.size() - 1; i >= 0; i--) {
					if (valoresMY.get(i).getTempoDeChegada() <= somaAccessTimes) {
						distancia = Math.abs(valoresMY.get(i).getCilindroRequisitado() - cilindroAnterior);
						// atribui accesstime do r atual o seu valor de C e D + a distancia + aux (que
						// eh toda a transferencia ate chegar nele, como o retorno do disco por exemplo)
						rAccessTimeAtual = valoresMY.get(i).getTrilha() + distancia
								+ valoresMY.get(i).getTransferTime();
						// incrementa a soma de accessTimes pelo valor do accessTime do r atual
						somaAccessTimes += rAccessTimeAtual;
						// coloca o accessTime do R atual no vetor para calcular o waiting time
						// posteriormente
						accessTimesParciais[indexParciais] = rAccessTimeAtual;
						// defini o cilindro anterior como o cilindro atual
						cilindroAnterior = valoresMY.get(i).getCilindroRequisitado();
						// removendo o cilindro lido
						valoresMY.remove(i);
						// decrementando i devido a remocao de um R
						i++;
						// incrementa a variavel que controla o vetor de accessTimes dos Rs parciais
						indexParciais++;
						// zera a transferencia que foi necessaria pra chegar nesse R
					}
				}

			}
		} else {
			while (iter.hasNext()) {
				for (i = valoresMY.size() - 1; i >= 0; i--) {
					if (valoresMY.get(i).getTempoDeChegada() <= somaAccessTimes) {
						distancia = Math.abs(valoresMY.get(i).getCilindroRequisitado() - cilindroAnterior);
						// atribui accesstime do r atual o seu valor de C e D + a distancia + aux (que
						// eh toda a transferencia ate chegar nele, como o retorno do disco por exemplo)
						rAccessTimeAtual = valoresMY.get(i).getTrilha() + distancia
								+ valoresMY.get(i).getTransferTime();
						// incrementa a soma de accessTimes pelo valor do accessTime do r atual
						somaAccessTimes += rAccessTimeAtual;
						// coloca o accessTime do R atual no vetor para calcular o waiting time
						// posteriormente
						accessTimesParciais[indexParciais] = rAccessTimeAtual;
						// defini o cilindro anterior como o cilindro atual
						cilindroAnterior = valoresMY.get(i).getCilindroRequisitado();
						// removendo o cilindro lido
						valoresMY.remove(i);
						// decrementando i devido a remocao de um R
						i++;
						// incrementa a variavel que controla o vetor de accessTimes dos Rs parciais
						indexParciais++;
						// zera a transferencia que foi necessaria pra chegar nesse R
					}

					for (i = 0; i < valoresMY.size(); i++) {
						// eh calculada a distancia, rAccesTimeAtual, soma dos tempos de acesso, etc
						if (valoresMY.get(i).getTempoDeChegada() <= somaAccessTimes) {

							distancia = Math.abs(valoresMY.get(i).getCilindroRequisitado() - cilindroAnterior);
							rAccessTimeAtual = valoresMY.get(i).getTrilha() + distancia
									+ valoresMY.get(i).getTransferTime();
							somaAccessTimes += rAccessTimeAtual;
							accessTimesParciais[indexParciais] = rAccessTimeAtual;
							// cilindro anterior passa a ser o cilindro lido
							cilindroAnterior = valoresMY.get(i).getCilindroRequisitado();
							// removido
							valoresMY.remove(i);
							i--;
							indexParciais++;

						}
					}
				}

			}
		}

		accessTime = somaAccessTimes / qntDeRs;
		waitingTime = calcularWaitingTime(accessTimesParciais, qntDeRs, somaTemposDeChegada);
		exportarRespostaParaArquivo(algoritmoExecutado, pathWriter, accessTime, waitingTime);
	}

	public static float calcularWaitingTime(int[] accessTimesParciais, int qntDeRs, int somaTemposChegada) {
		float waitingTime = 0;
		int target = 1;

		// percorre todos os Rs
		for (int i = 0; i < qntDeRs - 1; i++) {
			int j = 0;
			// para cada R, e calculado o waitingTime o target é a variavel que controla
			// quantos Rs estavam na frente do do R acessado para que seja calculado o
			// waiting time somando o accessTime dos Rs anteriores ou seja, para que seja
			// aplicada a formula, (soma de todos os tempos de acesso dos Rs anteriores -
			// seu tempo de chegada) para cada R
			while (j < target) {
				waitingTime += accessTimesParciais[j];
				j++;
			}
			target++;
		}

		// calcula o waitingTime (soma de todos waitingTimes - a soma de todos os
		// tempos de chegada dos Rs) / quantidade de Rs
		waitingTime = ((waitingTime - somaTemposChegada) / qntDeRs);
		return waitingTime;
	}

	// metodo chamado pelos algoritmos para exportar as respostas para o arquivo
	// cuja path eh definida pela variavel pathWriter.
	public static void exportarRespostaParaArquivo(String algoritmoExecutado, String pathWriter, float accessTime,
			float waitingTime) {
		BufferedWriter bw = null;
		// definindo forma de saida do valor como especificado no trabalho
		DecimalFormat dec = new DecimalFormat("00.00");
		// definindo a quantidade maxima de casas decimais como 2
		dec.setMaximumFractionDigits(2);
		// truncando os valores de accessTime e waitingTime
		accessTime = truncaValores(accessTime);
		waitingTime = truncaValores(waitingTime);
		try {
			bw = new BufferedWriter(new FileWriter(pathWriter, true));
			bw.write(algoritmoExecutado + "\n");
			bw.newLine();
			// exportanto os valores de accessTime e waitingTime
			bw.write("\n-AccessTime=" + dec.format(accessTime).replaceAll(",", "."));
			bw.newLine();
			bw.write("\n-WaitingTime=" + dec.format(waitingTime).replaceAll(",", "."));
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // fechando o arquivo
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ioe2) {

				}
			}
		}
	}

	// metodo que trunca os valores
	public static float truncaValores(float valor) {
		LinkedList<Character> aux = new LinkedList<Character>();
		LinkedList<Character> auxFinal = new LinkedList<Character>();
		String stringAux = Float.toString(valor);
		stringAux = stringAux.replaceAll("\\.", ",");
		String stringFinal = "";

		// colocando os valores da stringAux na lista encadeada aux
		for (int i = 0; i < stringAux.length(); i++) {
			aux.add(stringAux.charAt(i));
		}

		// definindo o indice como 0
		int i = 0;
		// colocando na lista encadeada auxFinal os valores que nao forem decimais
		// e, ao mesmo tempo, removendo estes valores da lista aux
		while (aux.get(i) != ',') {
			auxFinal.add(aux.get(i));
			aux.remove(i);
		}

		// acrescentando a virgula na aux final e removendo a virgula da aux
		auxFinal.add(',');
		i++;
		aux.remove(0);

		// neste momento a lista aux so possui os valores decimais e a auxFinal possui
		// todos menos os valores decimais

		int j = 0;

		// portanto, se aux so tiver um valor somente, eh colocado este valor e logo
		// apos um 0
		if (aux.size() == 1) {
			auxFinal.add(aux.get(j));
			auxFinal.add('0');
			// se nao caso a aux tenha tamanho maior ou igual a 2, ou seja, tenha 2 casas
			// decimais para colocar em auxFinal, estes valores sao colocados em auxFinal
		} else if (aux.size() >= 2) {
			while (j < 2) {
				auxFinal.add(aux.get(j));
				j++;
			}
			// se nao, caso nao tenha nenhuma casa decimal para colocar, em auxFinal eh
			// colocado 0, 0.
		} else {
			auxFinal.add('0');
			auxFinal.add('0');
		}

		for (i = 0; i < auxFinal.size(); i++) {
			stringFinal += auxFinal.get(i);
		}
		// replaceAll pois o replace pode causar aleatoriedade no .
		stringFinal = stringFinal.replaceAll(",", "\\.");
		// convertendo para float
		valor = Float.valueOf(stringFinal);
		return valor;
	}

}
