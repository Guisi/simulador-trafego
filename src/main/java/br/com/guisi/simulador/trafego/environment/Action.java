package br.com.guisi.simulador.trafego.environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Action implements MoveOperator {

	UP("Acima", (state) -> new State(state.getX(), state.getY() - 1) ),
	DOWN("Abaixo", (state) -> new State(state.getX(), state.getY() + 1) ),
	LEFT("Esquerda", (state) -> new State(state.getX() - 1, state.getY()) ),
	RIGHT("Direita", (state) -> new State(state.getX() + 1, state.getY()) );

	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	
	private String description;
	private MoveOperator moveOperator;
	
	private Action(String description, MoveOperator moveOperator) {
		this.description = description;
		this.moveOperator = moveOperator;
	}
	
	public String getDescription() {
		return this.description;
	}

	public static Action getRandomAction() {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}

	public static List<Action> getValues() {
		return VALUES;
	}

	@Override
	public State move(State state) {
		return this.moveOperator.move(state);
	}
}

@FunctionalInterface
interface MoveOperator {
	public State move(State state);
}
