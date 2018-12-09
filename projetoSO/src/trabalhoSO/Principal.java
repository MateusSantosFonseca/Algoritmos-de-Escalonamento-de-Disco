package trabalhoSO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Principal {

	public static String pathReader = "in.txt";
	public static String pathWriter = "out.txt";

	public static void main(String[] args) {
		int numSectors, numTracks, startPosition, qntDeLinhas;
		int sector_track_position[] = new int[3];
		esvaziandoArquivoExportado(pathWriter);

		qntDeLinhas = pegarQuantidadeDeLinhas();

		int matrizArquivo[][] = new int[qntDeLinhas][4];

		// lendo arquivo

		// pegando os valores de numSectors, numTracks e startPosition
		sector_track_position = pegarValoresIniciaisDoArquivo();
		numSectors = sector_track_position[0];
		numTracks = sector_track_position[1];
		startPosition = sector_track_position[2];

		// pegando os valores de R
		matrizArquivo = pegarValoresDeR(qntDeLinhas);

		// verificando se os valores dos Rs sao validos (se não forem o programa acusa
		// ERRO e fecha)
		verificaValores(numSectors, numTracks, startPosition, matrizArquivo, qntDeLinhas);

		// mostrando os valores lidos

		System.out.println("numSectors = " + numSectors);
		System.out.println("numTracks = " + numTracks);
		System.out.println("startPosition = " + startPosition);

		System.out.println("");
		System.out.println("Os valores de R inseridos sao: ");
		System.out.println();

		for (int i = 0; i < qntDeLinhas; i++) {
			System.out.print("R" + (i + 1) + ":  ");
			for (int j = 0; j < 4; j++) {
				System.out.print(matrizArquivo[i][j] + ", ");
			}
			System.out.println("");
		}

		// adicionando os valores da matriz para um ArrayList de RS
		ArrayList<R> valores = new ArrayList<>();
		for (int i = 0; i < qntDeLinhas; i++) {
			R atual = new R(matrizArquivo[i][0], matrizArquivo[i][1], matrizArquivo[i][2],
					matrizArquivo[i][3]);
			valores.add(atual);
		}
		
		//somando tempos de chegada
		int somaTemposDeChegada = 0;
		for (R valorDeR : valores) {
			somaTemposDeChegada += valorDeR.getTempoDeChegada();
		}

		// chamando funcoes que exportarï¿½o arquivo resposta
		Algoritmos.FCFS(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
		Algoritmos.SSTF(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
		Algoritmos.SCAN(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
		Algoritmos.C_SCAN(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
		Algoritmos.C_LOOK(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
		Algoritmos.MY(numSectors, numTracks, startPosition, valores, qntDeLinhas, somaTemposDeChegada, pathWriter);
	}

	// contando quantas linhas tem exceto as linhas que contenham os numSectors,
	// numTracks,
	// startPosition e a ultima linha (;)
	private static int pegarQuantidadeDeLinhas() {
		int qntDeLinhas = 0;
		Scanner input = null;

		try {
			input = new Scanner(new File(pathReader));
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		}
		while (input.hasNextLine()) {
			qntDeLinhas++;
			input.nextLine();
		}

		input.close();
		// -1 pois a ultima linha contem somente ";"
		// -3 pois os 3 primeiros sao numTrack, numsector e startPosition
		return qntDeLinhas - 4;
	}

	// funcao que pega todas as linhas do arquivo que contenham valores de R e
	// coloca-as,
	// individualmente, em cada indice do vetor gerado
	private static int[][] pegarValoresDeR(int qntDeLinhas) {
		Scanner leitor = null;
		String vetorDoArquivo[] = new String[qntDeLinhas];
		int matrizFinal[][] = new int[qntDeLinhas][4];
		String linhaAtual;

		try {
			leitor = new Scanner(new File(pathReader));
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		}

		int i = 0;
		int indiceAux = 0;
		// enquanto tiver linha, coloca a linha atual no vetorDoArquivo no indice atual;
		while (leitor.hasNextLine()) {
			linhaAtual = leitor.nextLine();
			// se o arquivo tiver mais uma linha, quer dizer que nao chegou no final
			if (leitor.hasNextLine()) {
				// essa condicaoo faz com que somente seja pego os valores de R
				if (i > 2) {
					vetorDoArquivo[indiceAux] = linhaAtual;
					indiceAux++;
					// se ele chegou no final, nao atribui valor para ele, pois o valor final eh
					// somente um ";"
				}
				i++;
			}

		}

		leitor.close();

		matrizFinal = transformarRsEmMatriz(vetorDoArquivo);
		return matrizFinal;

	}

	// este mï¿½todo recebe uma array que possui a entrada com letras e sï¿½mbolos
	// primeiramente ï¿½ removido todos sï¿½mbolos, letras e nï¿½meros indesejado
	// e, logo apï¿½s, ï¿½ inserido os valores nas linhas da matriz ao mesmo tempo
	// que
	// os mesmos sï¿½o transformados de String para int
	private static int[][] transformarRsEmMatriz(String[] vetorArquivo) {

		int length = vetorArquivo.length;
		String[][] strArray = new String[length][4];
		int[][] strToMatriz = new int[length][4];
		// remover todos "=", letras e ";" do vetor
		for (int i = 0; i < length; i++) {
			vetorArquivo[i] = vetorArquivo[i].replaceAll("=", "");
			vetorArquivo[i] = vetorArquivo[i].replaceAll("[A-z]", "");
			vetorArquivo[i] = vetorArquivo[i].replaceFirst("[0-9]", "");
		}

		// criando a matriz de Rs (no qual cada linha do arquivo ï¿½ uma linha na
		// matriz)
		// e jï¿½ convertendo o valor de String para Int
		for (int i = 0; i < length; i++) {
			// pondo em cada ï¿½ndice deste array os valores que estï¿½o entre as vï¿½rgulas
			strArray[i] = vetorArquivo[i].split(",");
			for (int j = 0; j < 4; j++) {
				strToMatriz[i][j] = Integer.parseInt(strArray[i][j]);
			}
		}

		return strToMatriz;
	}

	// traz os valores de numTracks, numSectors e startPosition,
	// cada um para indice do vetor a ser retornado, ordenado respectivamente
	private static int[] pegarValoresIniciaisDoArquivo() {
		Scanner leitor = null;
		int sector_track_position[] = new int[3];
		int i = 0;
		String linhaAtual, aux;

		try {
			leitor = new Scanner(new File(pathReader));
		} catch (FileNotFoundException e) {
			e.getStackTrace();
		}
		while (i < 3) {
			linhaAtual = leitor.nextLine();
			// i = 0, pega o valor de numSectors e poe no indice 0
			if (i == 0) {
				aux = linhaAtual.replaceAll("[A-z]", "");
				aux = aux.replaceAll("=", "");
				sector_track_position[i] = Integer.parseInt(aux);

				// i = 1, pega o valor de numTracks e poe no indice 1
			} else if (i == 1) {
				aux = linhaAtual.replaceAll("[A-z]", "");
				aux = aux.replaceAll("=", "");
				sector_track_position[i] = Integer.parseInt(aux);

				// i = 2, pega o valor de startPosition e poe no indice 2
			} else if (i == 2) {
				aux = linhaAtual.replaceAll("[A-z]", "");
				aux = aux.replaceAll("=", "");
				sector_track_position[i] = Integer.parseInt(aux);
			}
			i++;
		}

		leitor.close();

		return sector_track_position;
	}

	// esvaziando o conteï¿½do lixo de out.txt
	private static void esvaziandoArquivoExportado(String pathWriter) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(pathWriter);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();

	}

	// este metodo verifica se os Rs tem valores validos
	private static void verificaValores(int numSectors, int numTracks, int startPosition, int[][] matrizArquivo,
			int qntDeLinhas) {
		// verificando se todos os valores C de cada R sao menores que numTracks
		// verificando também se o cilindro requerido pelo R esta dentro do limite do
		// numSectors
		for (int i = 0; i < qntDeLinhas; i++) {
			if (matrizArquivo[i][2] > numTracks || matrizArquivo[i][2] < 0) {
				System.out.println("ERRO! O valor C do R" + (i + 1) + " é inválido");
				System.out.println("Terminando o programa...");
				System.exit(-1);
			}

			if ((matrizArquivo[i][1] < 0) || (matrizArquivo[i][1] > (numSectors - 1))) {
				System.out.println("ERRO! O cilindro requerido por R" + (i + 1) + " é invalido!");
				System.out.println("Terminando o programa...");
				System.exit(-1);
			}
		}

	}

}