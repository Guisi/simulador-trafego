package br.com.guisi.simulador.trafego.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import br.com.guisi.simulador.trafego.environment.State;

public class StateRectangle extends Rectangle {

	private final State state;
	
	public StateRectangle(State state, double sizePx) {
		this.state = state;
		setX(sizePx * state.getX());
		setY(sizePx * state.getY());
		setWidth(sizePx);
		setHeight(sizePx);
		setFill(Color.valueOf(state.getTrafficSituation().getColor()));
		setStroke(Color.GRAY);
	}

	public State getState() {
		return state;
	}
	
	public double getCenterX() {
		return getWidth() / 2 + getWidth() * getState().getX();
	}
	
	public double getCenterY() {
		return getHeight() / 2 + getHeight() * getState().getY();
	}
}
