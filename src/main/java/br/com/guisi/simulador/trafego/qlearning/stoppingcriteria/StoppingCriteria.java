package br.com.guisi.simulador.trafego.qlearning.stoppingcriteria;

public abstract class StoppingCriteria {

	private boolean stopForced = false;
	
	/**
	 * Define como o critério trata o fim de uma interação
	 * @param randomAction Se ação realizada foi randômica
	 * @param changedPolicy Se trocou a melhor ação para o estado da interação
	 */
	public abstract void handleEpisode(boolean randomAction, boolean changedPolicy);
	
	/**
	 * Retorna se atingiu o critério de parada
	 * @return
	 */
	public boolean isReached() {
		return stopForced;
	}
	
	public void forceStop() {
		this.stopForced = true;
	}
}
