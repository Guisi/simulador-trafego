package br.com.guisi.simulador.trafego.view;

import java.util.Observable;
import java.util.Observer;

import javafx.concurrent.Task;
import br.com.guisi.simulador.trafego.qlearning.QLearningAgent;
import br.com.guisi.simulador.trafego.qlearning.QLearningStatus;

public class TaskQLearning extends Task<QLearningStatus> implements Observer {

	private final QLearningAgent qLearningAgent;
	
	public TaskQLearning(QLearningAgent qLearningAgent) {
		this.qLearningAgent = qLearningAgent;
	}
	
	@Override
	protected QLearningStatus call() throws Exception {
		qLearningAgent.addObserver(this);
		qLearningAgent.run();
		return null;
	}
	
	public void stopLearning() {
		qLearningAgent.stop();
		cancel();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		updateValue((QLearningStatus) arg);
	}
}
