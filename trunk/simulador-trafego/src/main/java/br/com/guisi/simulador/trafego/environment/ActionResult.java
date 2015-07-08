package br.com.guisi.simulador.trafego.environment;


public class ActionResult {

	private boolean possibleToMove;
	private double reward;
	private State nextState;

	public boolean isPossibleToMove() {
		return possibleToMove;
	}
	public void setPossibleToMove(boolean possibleToMove) {
		this.possibleToMove = possibleToMove;
	}
	public State getNextState() {
		return nextState;
	}
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
}
