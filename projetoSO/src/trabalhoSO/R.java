package trabalhoSO;

public class R implements Comparable<R> {

	private int tempoDeChegada;
	private int cilindroRequisitado;
	private int trilha;
	private int transferTime;

	public R(int tempoDeChegada, int cilindroRequisitado, int trilha, int transferTime) {
		super();
		this.tempoDeChegada = tempoDeChegada;
		this.cilindroRequisitado = cilindroRequisitado;
		this.trilha = trilha;
		this.transferTime = transferTime;
	}

	public R() {

	}

	public int getTempoDeChegada() {
		return tempoDeChegada;
	}

	public int getCilindroRequisitado() {
		return cilindroRequisitado;
	}

	public int getTrilha() {
		return trilha;
	}

	public int getTransferTime() {
		return transferTime;
	}

	@Override
	public int compareTo(R r2) {

		if (this.getCilindroRequisitado() < r2.getCilindroRequisitado())
			return -1;
		if (this.getCilindroRequisitado() == r2.getCilindroRequisitado())
			return 0;
		return 1;

	}
	

	
	

}
