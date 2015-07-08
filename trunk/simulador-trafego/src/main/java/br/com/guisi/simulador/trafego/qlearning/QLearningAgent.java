package br.com.guisi.simulador.trafego.qlearning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.guisi.simulador.trafego.astar.PathFinding;
import br.com.guisi.simulador.trafego.constants.Constants;
import br.com.guisi.simulador.trafego.environment.Action;
import br.com.guisi.simulador.trafego.environment.ActionResult;
import br.com.guisi.simulador.trafego.environment.Environment;
import br.com.guisi.simulador.trafego.environment.State;
import br.com.guisi.simulador.trafego.environment.TrafficSituation;
import br.com.guisi.simulador.trafego.qlearning.stoppingcriteria.StoppingCriteria;
import br.com.guisi.simulador.trafego.util.Utils;

public class QLearningAgent extends Observable {
	
	private Environment environment;
	private final StoppingCriteria stoppingCriteria;
	private final QTable qTable;
	private final QLearningStatus status;
	private boolean notifyObservers;
	
	public QLearningAgent(Environment environment, StoppingCriteria stoppingCriteria, boolean notifyObservers) {
		this.environment = environment;
		this.stoppingCriteria = stoppingCriteria;
		this.qTable = new QTable();
		this.notifyObservers = notifyObservers;
		this.status = new QLearningStatus();
		this.status.setAccessiblesStates(environment.getAccessibleStatesQuantity());
	}
	
	/**
	 * Inicia a interação do agente a partir de um estado inicial aleatório
	 */
	public void run() {
		this.run(environment.getRandomInitialStateForAgent());
	}

	/**
	 * Inicia a interação do agente a partir do estado inicial passado
	 * @param initialState
	 */
	public void run(State initialState) {
		//se ambiente não possui objetivo, não inicia interação
		if (!environment.hasTarget()) {
			throw new IllegalStateException("Ambiente não possui objetivo definido.");
		}
		
		this.getStatus().setRunning(true);
		long ini = System.currentTimeMillis();
		
		//faz a interação de acordo com o critério de parada definido
		while (!stoppingCriteria.isReached()) {
			State lastState = initialState;
			initialState = nextEpisode(initialState);
			
			if (notifyObservers) {
				this.getStatus().setRunTime(System.currentTimeMillis() - ini);
				
				lastState.setTrafficSituation(environment.getTrafficSituation(lastState));
				this.calculateEfficiency(lastState);
				
				try {
					QLearningStatus status = (QLearningStatus) this.getStatus().clone();
					setChanged();
					notifyObservers(status);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		this.calculateEfficiency();
		this.getStatus().setRunning(false);
		this.getStatus().setRunTime(System.currentTimeMillis() - ini);
	}
	
	public void stop() {
		stoppingCriteria.forceStop();
	}
	
	/**
	 * Realiza uma interação no ambiente
	 * @param state
	 * @return {@link State} estado para o qual o agente se moveu
	 */
	private State nextEpisode(State state) {
		//Se randomico menor que E-greedy, escolhe melhor acao
		boolean randomAction = (Math.random() >= Constants.E_GREEDY);
		Action action = randomAction ? Action.getRandomAction() : qTable.getBestAction(state);
		
		//verifica no ambiente qual é o resultado de executar a ação
		ActionResult actionResult = environment.executeAction(state, action);
		
		//recupera estado para o qual se moveu (será o mesmo atual caso não pode realizar a ação)
		State nextState = actionResult.getNextState();
		
		//recupera em sua QTable o valor de recompensa para o estado/ação que estava antes
		QValue qValue = qTable.getQValue(state, action, true);
        double q = qValue.getReward();
        
        //recupera em sua QTable o melhor valor para o estado para o qual se moveu
        double nextStateQ = qTable.getBestQValue(nextState, true).getReward();
        
        //recupera a recompensa retornada pelo ambiente por ter realizado a ação
        double r = actionResult.getReward();

        //Algoritmo Q-Learning -> calcula o novo valor para o estado/ação que estava antes
        double value = q + Constants.LEARNING_CONSTANT * (r + (Constants.DISCOUNT_FACTOR * nextStateQ) - q);
        
        //atualiza sua QTable com o valor calculado pelo algoritmo
        qValue.setReward(value);
        
        //incrementa contagem de episódios
        this.getStatus().incrementEpisodesCount();

        //verifica se mudou política, somente se ação não foi aleatória
        boolean changedPolicy = false;
        if (!randomAction) {
        	Action newBestAction = qTable.getBestAction(state);
        	changedPolicy = !action.equals(newBestAction);
        	this.getStatus().incrementPolicyChangeCount();
        }
        
        //critério de parada trata o fim de uma interação
        stoppingCriteria.handleEpisode(randomAction, changedPolicy);
        
        //se foi para o objetivo, vai para uma posição aleatória no ambiente
        if (environment.isTarget(nextState)) {
        	nextState = environment.getRandomInitialStateForAgent();
        }
        
        return nextState;
	}
	
	/**
	 * Verifica se após atualizar estado está acertando o caminho, e atualiza eficiência
	 * @param state
	 */
	private void calculateEfficiency(State state) {
		List<State> nodesQLearning = this.getRouteToTarget(state);
		List<State> nodesAStar = PathFinding.doAStar(environment, state);
		
		if (nodesQLearning != null) {
			BigDecimal costQ = Utils.calculateCostToTarget(nodesQLearning);
			BigDecimal costA = Utils.calculateCostToTarget(nodesAStar);
			
			if (costQ.equals(costA)) {
				getStatus().getHits().add(state);
			} else {
				getStatus().getHits().remove(state);
			}
			double hits = getStatus().getHits().size();
			double total = getStatus().getAccessiblesStates();
			BigDecimal efficiency = BigDecimal.valueOf(hits / total * 100).setScale(2, RoundingMode.HALF_UP);
			getStatus().setEfficiency(efficiency);
		}
	}
	
	/**
	 * Calcula a eficiência total
	 */
	private void calculateEfficiency() {
		getStatus().getHits().clear();
		for (Entry<State, TrafficSituation> entry : environment.getTrafficTableEntrySet()) {
			State state = entry.getKey();
			if (!state.getTrafficSituation().equals(TrafficSituation.BLOCKED)
					&& !state.getTrafficSituation().equals(TrafficSituation.TARGET)) {
				
				List<State> nodesQLearning = getRouteToTarget(state);
				List<State> nodesAStar = PathFinding.doAStar(environment, state);
				
				if (nodesQLearning != null) {
					BigDecimal costQ = Utils.calculateCostToTarget(nodesQLearning);
					BigDecimal costA = Utils.calculateCostToTarget(nodesAStar);
					
					if (costQ.equals(costA)) {
						getStatus().getHits().add(state);
					} else {
						getStatus().getHits().remove(state);
					}
				}
			}
		}
		double hits = getStatus().getHits().size();
		double total = getStatus().getAccessiblesStates();
		BigDecimal efficiency = BigDecimal.valueOf(hits / total * 100).setScale(2, RoundingMode.HALF_UP);
		getStatus().setEfficiency(efficiency);
	}
	
	/**
	 * Retorna uma lista dos estados representando o caminho do agente 
	 * do ponto inicial passado até o objetivo 
	 * @param start
	 * @return
	 */
	public List<State> getRouteToTarget(State start) {
		Set<State> route = new LinkedHashSet<>();
		route.add(start);
		
		State nextState = start;
		do {
			Action action = qTable.getBestAction(nextState);
			nextState = action.move(nextState);
			
			//se tentou se mover para um lugar que não pode, não irá encontrar o objetivo
			if (!environment.isPossibleToMove(nextState)) {
				break;
			}
			
			//se tentou se mover para um lugar que já tinha passado, também não irá, pode estar andando em círculos
			if (route.contains(nextState)) {
				break;
			}
			
			if (environment.isPossibleToMove(nextState)) {
				nextState.setTrafficSituation(environment.getTrafficSituation(nextState));
				route.add(nextState);
			}
		} while (!nextState.getTrafficSituation().equals(TrafficSituation.TARGET));
		
		//se nao encontrou objetivo, retorna lista nula
		if (nextState.getTrafficSituation() == null
				|| !nextState.getTrafficSituation().equals(TrafficSituation.TARGET)) {
			return null;
		}
		return new ArrayList<State>(route);
	}
	
	public QValue getBestQValue(State state, boolean createIfNull) {
		return qTable.getBestQValue(state, createIfNull);
	}
	
	public double getGreaterReward() {
		return qTable.getGreaterReward();
	}
	
	public double getLowerReward() {
		return qTable.getLowerReward();
	}

	public QLearningStatus getStatus() {
		return status;
	}

	/**
	 * Retorna uma string com os valores da QTable
	 * @return
	 */
	public String printQTable() {
		StringBuilder sb = new StringBuilder();
		this.qTable.entrySet().stream().sorted((e1, e2) -> e1.getKey().getState().compareTo(e2.getKey().getState()))
				.forEach((entry) -> sb.append(entry.getKey()).append(": ")
										.append(entry.getValue().getReward())
										.append(System.lineSeparator()) );
		return sb.toString();
	}
	
	/**
	 * Retorna uma string com uma lista da melhor ação por estado da QTable
	 * @return
	 */
	public String printPolicy() {
		StringBuilder sb = new StringBuilder();
		
		this.qTable.keySet().stream().collect(Collectors.groupingBy(StateAction::getState)).keySet()
				.stream().sorted((e1, e2) -> e1.compareTo(e2))
				.forEach((state) -> sb.append(state).append(": ")
									.append(qTable.getBestAction(state).getDescription())
									.append(System.lineSeparator()) );
		return sb.toString();
	}
}
