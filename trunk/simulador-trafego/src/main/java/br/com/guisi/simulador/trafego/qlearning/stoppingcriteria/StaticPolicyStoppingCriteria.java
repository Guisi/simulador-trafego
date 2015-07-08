package br.com.guisi.simulador.trafego.qlearning.stoppingcriteria;

/**
 * Critério de parada definido por um número máximo de episódios sem que o agente mude sua política
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
		//só trata ocorrência da interação caso a ação não tenha sido aleatória
		if (!randomAction) {
			this.staticEpisodesCount = changedPolicy ? 0 : this.staticEpisodesCount + 1;
		}
	}
	
	@Override
	public boolean isReached() {
		return super.isReached() || staticEpisodesCount == numberOfStaticEpisodes;
	}

}
