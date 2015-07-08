package br.com.guisi.simulador.trafego.environment;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.guisi.simulador.trafego.astar.PathFinding;

public class Environment {

	private TrafficTable trafficTable;
	
	private final int sizeX;
	private final int sizeY;
	
	public Environment(int sizeX, int sizeY, boolean random, boolean onlyAccessibleStates) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		boolean temLocalInacessivel;
		do {
			this.createEnviroment(random);
			
			temLocalInacessivel = false;
			if (random && onlyAccessibleStates) {
				//cria o ambiente de forma aleatoria que nao tenha nenhum ponto inacessivel
				for (Entry<State, TrafficSituation> entry : getTrafficTableEntrySet()) {
					State state = entry.getKey();
					if (this.isPossibleToMove(state)) {
						List<State> nodes = PathFinding.doAStar(this, state);
						if (nodes == null || nodes.isEmpty()) {
							temLocalInacessivel = true;
							break;
						}
					}
				}
			}
		} while (random && onlyAccessibleStates && temLocalInacessivel);
	}
	
	private void createEnviroment(boolean random) {
		trafficTable = new TrafficTable();
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				TrafficSituation situation = random ? TrafficSituation.getRandomTrafficSituation() : TrafficSituation.FREE;
				trafficTable.put(new State(x, y, situation), situation);
			}
		}
		
		//se está criando ambiente aleatório, já define um objetivo
		if (random) {
			Random r = new Random(System.currentTimeMillis());
			State target = new State(r.nextInt(sizeX), r.nextInt(sizeY), TrafficSituation.TARGET);
			trafficTable.remove(target);
			trafficTable.put(target, TrafficSituation.TARGET);
		}
	}
	
	/**
	 * Retorna se ambiente possui um objetivo
	 * @return
	 */
	public boolean hasTarget() {
		return trafficTable.hasTarget();
	}
	
	/**
	 * Retorna se estado passado é o objetivo
	 * @param state
	 * @return
	 */
	public boolean isTarget(State state) {
		return trafficTable.isTarget(state);
	}
	
	public State getTargetState() {
		return trafficTable.getTargetState();
	}
	
	public int getAccessibleStatesQuantity() {
		return getTrafficTableEntrySet().stream().filter((entry) -> 
			!entry.getValue().equals(TrafficSituation.TARGET) && !entry.getValue().equals(TrafficSituation.BLOCKED)).collect(Collectors.counting()).intValue();
	}
	
	/**
	 * Retorna um possível estado inicial para o agente
	 * @return
	 */
	public State getRandomInitialStateForAgent() {
		Random r = new Random(System.currentTimeMillis());
		int cont = 0;
		while (cont < sizeX * sizeY) {
			State state = new State(r.nextInt(sizeX), r.nextInt(sizeY));
			if (this.isPossibleToMove(state) && !trafficTable.getTrafficSituation(state).equals(TrafficSituation.TARGET)) {
				state.setTrafficSituation(getTrafficSituation(state));
				return state;
			}
			cont++;
		}
		throw new IllegalStateException("Não existe nenhum estado inicial para posicionar o agente!");
	}
	
	/**
	 * Executa a ação para o estado passado e retorna qual foi o resultado
	 * @param state
	 * @param action
	 * @return
	 */
	public ActionResult executeAction(State state, Action action) {
		State nextState = action.move(state);
		
		ActionResult actionResult = new ActionResult();
		if (this.isPossibleToMove(nextState)) {
			actionResult.setPossibleToMove(true);
			actionResult.setNextState(nextState);
			actionResult.setReward(trafficTable.getTrafficSituation(nextState).getReward());
		} else {
			actionResult.setPossibleToMove(false);
			actionResult.setNextState(state);
			// se tentou se mover para fora do ambiente, considera como se fosse um estado bloqueado
			actionResult.setReward(TrafficSituation.BLOCKED.getReward());
		}
		
		return actionResult;
	}
	
	/**
	 * Retorna os vizinhos válidos do estado passado
	 * @param state
	 * @return
	 */
	public Set<State> getNeighbours(State state) {
		Set<State> neighbours = new HashSet<State>();

		for (Action action : Action.values()) {
			State nextState = action.move(state);
			
			if (this.isPossibleToMove(nextState)) {
				nextState.setTrafficSituation(getTrafficSituation(nextState));
				neighbours.add(nextState);
			}
		}
		
		return neighbours;
	}
	
	/**
	 * Valida se estado está dentro dos limites do ambiente
	 * e se não está bloqueado
	 * @param state
	 * @return
	 */
	public boolean isPossibleToMove(State state) {
		return state.getX() >= 0 && state.getX() < this.sizeX
				&& state.getY() >= 0 && state.getY() < sizeY
				&& !trafficTable.getTrafficSituation(state).equals(TrafficSituation.BLOCKED);
	}
	
	public TrafficSituation getTrafficSituation(int x, int y) {
		return trafficTable.getTrafficSituation(x, y);
	}
	
	public TrafficSituation getTrafficSituation(State state) {
		return trafficTable.getTrafficSituation(state);
	}
	
	public Set<Entry<State, TrafficSituation>> getTrafficTableEntrySet() {
		return trafficTable.entrySet();
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}
}