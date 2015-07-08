package br.com.guisi.simulador.trafego.environment;

import java.util.HashMap;

/**
 * Tabela auxiliar para melhorar a busca de situacao de tráfego pelo estado
 * @author Guisi
 *
 */
public class TrafficTable extends HashMap<State, TrafficSituation> {

	private static final long serialVersionUID = 1L;

	public boolean hasTarget() {
		return values().contains(TrafficSituation.TARGET);
	}
	
	public State getTargetState() {
		return entrySet().stream()
				.filter((entry) -> entry.getValue().equals(TrafficSituation.TARGET)).findFirst().get().getKey();
	}
	
	public boolean isTarget(State state) {
		return get(state).equals(TrafficSituation.TARGET);
	}
	
	public TrafficSituation getTrafficSituation(int x, int y) {
		return getTrafficSituation(new State(x, y));
	}
	
	public TrafficSituation getTrafficSituation(State state) {
		return get(state);
	}
}
