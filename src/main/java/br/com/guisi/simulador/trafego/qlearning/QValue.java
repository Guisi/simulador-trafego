package br.com.guisi.simulador.trafego.qlearning;

import br.com.guisi.simulador.trafego.environment.Action;
import br.com.guisi.simulador.trafego.environment.State;

/**
 * @author Guisi
 *
 */
/**
 * @author Guisi
 *
 */
public class QValue {

	private State state;
	private Action action;
	private double reward;
	
	public QValue(State state, Action action) {
		this.state = state;
		this.action = action;
	}
	
	public QValue(State state, Action action, double reward) {
		this.state = state;
		this.action = action;
		this.reward = reward;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QValue other = (QValue) obj;
		if (action != other.action)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "QValue [state=" + state + ", action=" + action + ", reward=" + reward + "]";
	}
	
}
