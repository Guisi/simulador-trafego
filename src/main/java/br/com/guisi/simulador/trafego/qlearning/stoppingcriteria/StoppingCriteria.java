package br.com.guisi.simulador.trafego.qlearning.stoppingcriteria;

public abstract class StoppingCriteria {

	private boolean stopForced = false;
	
	/**
	 * Define como o crit�rio trata o fim de uma intera��o
	 * @param randomAction Se a��o realizada foi rand�mica
	 * @param changedPolicy Se trocou a melhor a��o para o estado da intera��o
	 */
	public abstract void handleEpisode(boolean randomAction, boolean changedPolicy);
	
	/**
	 * Retorna se atingiu o crit�rio de parada
	 * @return
	 */
	public boolean isReached() {
		return stopForced;
	}
	
	public void forceStop() {
		this.stopForced = true;
	}
}
