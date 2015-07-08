package br.com.guisi.simulador.trafego.view;

import java.util.Iterator;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import br.com.guisi.simulador.trafego.environment.State;

public class EnvironmentPane extends Pane {
	
	private static final String AGENT_CIRCLE_ID = "agentCircle";

	private StateRectangle getStateRectangleFromPane(State state) {
		ObservableList<Node> children = getChildren();
		
		for (Node node : children) {
			if (node instanceof StateRectangle) {
				State s = ((StateRectangle)node).getState();
				if (s.equals(state)) {
					return ((StateRectangle)node);
				}
			}
		}
		return null;
	}
	
	private Circle getAgentCircle() {
		ObservableList<Node> children = getChildren();
		
		//procura o Circle na lista de children 
		for (Node node : children) {
			if (AGENT_CIRCLE_ID.equals(node.getId())) {
				return (Circle) node;
			}
		}
		
		//se nao encontrar, cria e adiciona
		Circle circle = new Circle();
		circle.setRadius(10);
		circle.setFill(Color.GREEN);
		circle.setId(AGENT_CIRCLE_ID);
		children.add(circle);
		
		return circle;
	}
	
	public void drawAgent(State state) {
		this.drawAgent(getStateRectangleFromPane(state));
	}
	
	public void drawAgent(StateRectangle stateRectangle) {
		//desenha o agente Q-Learning
		Circle qLearningCircle = getAgentCircle();
		qLearningCircle.setCenterX(stateRectangle.getCenterX());
		qLearningCircle.setCenterY(stateRectangle.getCenterY());
	}
	
	public void clearLines() {
		//remove as linhas anteriores
		ObservableList<Node> children = getChildren();
		for (Iterator<Node> iterator = children.iterator(); iterator.hasNext();) {
			if (iterator.next() instanceof Line) {
				iterator.remove();
			}
		}
	}
	
	public void drawRoute(List<State> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (i < nodes.size() - 1) {
				StateRectangle rectangleFrom = getStateRectangleFromPane(nodes.get(i));
				StateRectangle rectangleTo = getStateRectangleFromPane(nodes.get(i+1));
				
				Line line = new Line();
				line.setStroke(Color.GREEN);
				line.setStrokeWidth(2);
				line.setStartX(rectangleFrom.getCenterX());
				line.setStartY(rectangleFrom.getCenterY());
				line.setEndX(rectangleTo.getCenterX());
				line.setEndY(rectangleTo.getCenterY());
				getChildren().add(line);
			}
		}
	}
}
