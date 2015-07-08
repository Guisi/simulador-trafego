package br.com.guisi.simulador.trafego.environment;

import br.com.guisi.simulador.trafego.constants.Constants;

public class State implements Comparable<State> {

	private final int x;
	private final int y;
	private TrafficSituation trafficSituation;

	public State(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public State(int x, int y, TrafficSituation trafficSituation) {
		this.x = x;
		this.y = y;
		this.trafficSituation = trafficSituation;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public TrafficSituation getTrafficSituation() {
		return trafficSituation;
	}
	
	public void setTrafficSituation(TrafficSituation trafficSituation) {
		this.trafficSituation = trafficSituation;
	}

	public double getHeuristic(State goal) {
		return Constants.STEP_COST * (Math.abs(x - goal.x) + Math.abs(y - goal.y));
	}

	public double getTraversalCost() {
		return Constants.STEP_COST + Math.abs(trafficSituation.getReward());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		State other = (State) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")"; 
	}
	
	public int compareTo(State o) {
		if (x != o.x) {
			return x < o.x ? -1 : 1;
		} else if (y != o.y) {
			return y < o.y ? -1 : 1;
		} else {
			return 0;
		}
	}
}
