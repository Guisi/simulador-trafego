package br.com.guisi.simulador.trafego.qlearning.stoppingcriteria;

/**
 * Crit�rio de parada definido por um n�mero m�ximo de epis�dios sem que o agente mude sua pol�tica
 * 
 * @author douglasguisi
 */
public class StaticPolicyStoppingCriteria extends StoppingCriteria {

	private final int numberOfStaticEpisodes;
	private int staticEpisodesCount;
	
	public StaticPolicyStoppingCriteria(int numberOfStaticEpisodes) {
		this.numberOfStaticEpisodes = numberOfStaticEpisodes;
	}
	
	@Override
	public void handleEpisode(boolean randomAction, boolean changedPolicy) {
		//s� trata ocorr�ncia da intera��o caso a a��o n�o tenha sido aleat�ria
		if (!randomAction) {
			this.staticEpisodesCount = changedPolicy ? 0 : this.staticEpisodesCount + 1;
		}
	}
	
	@Override
	public boolean isReached() {
		return super.isReached() || staticEpisodesCount == numberOfStaticEpisodes;
	}

}
