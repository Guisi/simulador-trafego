package br.com.guisi.simulador.trafego.qlearning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import br.com.guisi.simulador.trafego.environment.Action;
import br.com.guisi.simulador.trafego.environment.State;

public class QTable extends HashMap<StateAction, QValue> {

	private static final long serialVersionUID = 1L;

	/**
	 * Recupera a recompensa para o estado e ação passados
	 * @param state
	 * @param action
	 * @param createIfNull
	 * @return
	 */
	public QValue getQValue(State state, Action action, boolean createIfNull) {
		StateAction stateAction = new StateAction(state, action);
		
		QValue qValue = get(stateAction);
		if (createIfNull && qValue == null) {
			qValue = new QValue(state, action);
			put(stateAction, qValue);
		}
		return qValue;
	}
	
	/**
	 * Recupera a lista de recompensas para cada ação do estado passado
	 * @param state
	 * @return
	 */
	public List<QValue> getQValues(State state, boolean createIfNull) {
		List<QValue> qValues = new ArrayList<>();
		for (Action action : Action.values()) {
			QValue qValue = getQValue(state, action, createIfNull);
			if (qValue != null) {
				qValues.add(qValue);
			}
		}
		return qValues;
	}
	
	/**
	 * Retorna o melhor valor de recompensa para o estado passado
	 * @param state
	 * @return
	 */
	public synchronized QValue getBestQValue(State state, boolean createIfNull) {
		//Recupera valores para o estado
		List<QValue> qValues = getQValues(state, createIfNull);
		
		if (!qValues.isEmpty()) {
			//Verifica o maior valor de recompensa
			double max = qValues.stream().max(Comparator.comparing(value -> value.getReward())).get().getReward();
			
			//filtra por todas as acoes cuja recompensa seja igual a maior
			qValues = qValues.stream().filter(valor -> valor.getReward() == max).collect(Collectors.toList());
			
			//retorna uma das melhores acoes aleatoriamente
			return qValues.get(new Random(System.currentTimeMillis()).nextInt(qValues.size()));
		}
		return null;
	}
	
	/**
	 * Retorna a acao com melhor recompensa para o estado passado
	 * @param state
	 * @return
	 */
	public Action getBestAction(State state) {
		return this.getBestQValue(state, true).getAction();
	}
	
	/**
	 * Retorna o maior valor da maior recompensa existente na tabela
	 * @return
	 */
	public double getGreaterReward() {
		return values().stream().max(Comparator.comparing(value -> value.getReward())).get().getReward();
	}
	
	/**
	 * Retorna o menor valor da maior recompensa existente na tabela
	 * @return
	 */
	public double getLowerReward() {
		return values().stream().min(Comparator.comparing(value -> value.getReward())).get().getReward();
	}
}
