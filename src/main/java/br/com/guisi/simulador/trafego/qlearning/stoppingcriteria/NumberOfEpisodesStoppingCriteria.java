package br.com.guisi.simulador.trafego.qlearning.stoppingcriteria;

/**
 * Critério de parada definido por um número máximo de episódios
 * @author douglasguisi
 */
public class NumberOfEpisodesStoppingCriteria extends StoppingCriteria {

	private final int numberOfEpisodes;
	private final boolean countRandomEpisodes;
	private int episodesCount;
	
	public NumberOfEpisodesStoppingCriteria(int numberOfEpisodes, boolean countRandomEpisodes) {
		this.numberOfEpisodes = numberOfEpisodes;
		this.countRandomEpisodes = countRandomEpisodes;
	}
	
	@Override
	public void handleEpisode(boolean randomAction, boolean changedPolicy) {
		if (countRandomEpisodes || !randomAction) {
			this.episodesCount++;
		}
	}
	
	@Override
	public boolean isReached() {
		return super.isReached() || episodesCount == numberOfEpisodes;
	}

}
