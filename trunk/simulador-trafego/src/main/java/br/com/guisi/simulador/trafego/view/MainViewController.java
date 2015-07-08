package br.com.guisi.simulador.trafego.view;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map.Entry;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import br.com.guisi.simulador.trafego.astar.PathFinding;
import br.com.guisi.simulador.trafego.environment.Environment;
import br.com.guisi.simulador.trafego.environment.State;
import br.com.guisi.simulador.trafego.environment.TrafficSituation;
import br.com.guisi.simulador.trafego.qlearning.QLearningAgent;
import br.com.guisi.simulador.trafego.qlearning.QLearningStatus;
import br.com.guisi.simulador.trafego.qlearning.QValue;
import br.com.guisi.simulador.trafego.qlearning.stoppingcriteria.StaticPolicyStoppingCriteria;
import br.com.guisi.simulador.trafego.qlearning.stoppingcriteria.StoppingCriteria;
import br.com.guisi.simulador.trafego.util.Utils;

public class MainViewController {

	@FXML
	private EnvironmentPane qLearningPane;
	@FXML
	private EnvironmentPane aStarPane;
	@FXML
	private EnvironmentPane mapQLearningPane;
	@FXML
	private Pane infoPane;
	@FXML
	private Button btnRunQLearning;
	@FXML
	private Button btnStopQLearning;
	@FXML
	private Button btnCreateEnvironment;
	@FXML
	private Label lblRunTime;
	@FXML
	private Label lblEpisodeCount;
	@FXML
	private Label lblPolicyChangeCount;
	@FXML
	private Label lblQLearningCost;
	@FXML
	private Label lblAStarCost;
	@FXML
	private Label lblEfficiencyQLearning;
	@FXML
	private Label lblQLearningHits;
	@FXML
	private Label lblQLearningMisses;
	
	private QLearningAgent qLearningAgent;
	private Environment environment;
	private TaskQLearning task;
	
	private static final int ENVIRONMENT_SIZE_X = 10;
	private static final int ENVIRONMENT_SIZE_Y = 10;
	private static final int STATE_SIZE_PX = 35;
	
	private boolean disabled;
	
	public void initialize() {
		qLearningPane.setMaxWidth(ENVIRONMENT_SIZE_X * STATE_SIZE_PX);
		qLearningPane.setMaxHeight(ENVIRONMENT_SIZE_Y * STATE_SIZE_PX);
		
		mapQLearningPane.setMaxWidth(ENVIRONMENT_SIZE_X * STATE_SIZE_PX);
		mapQLearningPane.setMaxHeight(ENVIRONMENT_SIZE_Y * STATE_SIZE_PX);
		
		aStarPane.setMaxWidth(ENVIRONMENT_SIZE_X * STATE_SIZE_PX);
		aStarPane.setMaxHeight(ENVIRONMENT_SIZE_Y * STATE_SIZE_PX);
		
		infoPane.setMaxWidth(ENVIRONMENT_SIZE_X * STATE_SIZE_PX);
		infoPane.setMaxHeight(ENVIRONMENT_SIZE_Y * STATE_SIZE_PX);
		
		Image imageCheck = new Image(getClass().getResourceAsStream("/fxml/imgs/check.png"));
		btnRunQLearning.setGraphic(new ImageView(imageCheck));
		
		Image imageDelete = new Image(getClass().getResourceAsStream("/fxml/imgs/delete.png"));
		btnStopQLearning.setGraphic(new ImageView(imageDelete));
	}
	
	public void cancelTasks() {
		if (task != null) {
			task.cancel();
		}
	}
	
	public void clearEnviroment() {
		qLearningAgent = null;
		qLearningPane.getChildren().clear();
		aStarPane.getChildren().clear();
		mapQLearningPane.getChildren().clear();
	}
	
	public void clearTexts() {
		lblRunTime.setText("");
		lblEpisodeCount.setText("");
		lblPolicyChangeCount.setText("");
		lblQLearningCost.setText("");
		lblAStarCost.setText("");
		lblEfficiencyQLearning.setText("");
		lblQLearningHits.setText("");
		lblQLearningMisses.setText("");
	}
	
	private void disableScreen() {
		this.disabled = true;
		btnCreateEnvironment.setDisable(disabled);
		btnRunQLearning.setDisable(disabled);
		btnStopQLearning.setDisable(!disabled);
	}
	
	private void enableScreen() {
		this.disabled = false;
		btnCreateEnvironment.setDisable(disabled);
		btnRunQLearning.setDisable(disabled);
		btnStopQLearning.setDisable(!disabled);
	}
	
	public void createRandomEnvironment() {
		this.clearEnviroment();
		this.clearTexts();
		
		//cria o ambiente de forma aleatoria que nao tenha nenhum ponto inacessivel
		environment = new Environment(ENVIRONMENT_SIZE_X, ENVIRONMENT_SIZE_Y, true, true);

		environment.getTrafficTableEntrySet().forEach((entry) -> {
			//mapa q-learning
			StateRectangle r = new StateRectangle(entry.getKey(), STATE_SIZE_PX);
			r.setOnMouseClicked((event) -> { if (!disabled) { drawAgent((StateRectangle)event.getSource());} });
			qLearningPane.getChildren().add(r);
			
			//mapa A*
			r = new StateRectangle(entry.getKey(), STATE_SIZE_PX);
			aStarPane.getChildren().add(r);
		});
		
		btnRunQLearning.setDisable(false);
	}
	
	public void runLearning() {
		this.mapQLearningPane.getChildren().clear();
		this.clearTexts();
		this.disableScreen();
		lblRunTime.setText("executando...");
		
		StoppingCriteria stoppingCriteria = new StaticPolicyStoppingCriteria(500000);
		//StoppingCriteria stoppingCriteria = new NumberOfEpisodesStoppingCriteria(2000000, false);
		qLearningAgent = new QLearningAgent(environment, stoppingCriteria, false);
		
		task = new TaskQLearning(qLearningAgent);
		task.valueProperty().addListener((observableValue, oldState, newState) -> {
			if (newState != null) {
				updateQLearningStatus(newState);
			}
		});
		
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
            	this.enableScreen();
            	updateQLearningStatus(qLearningAgent.getStatus());
            	refreshQTableMap();
            }
        });
		
		new Thread(task).start();
	}
	
	public void stopLearning() {
		task.stopLearning();
		this.refreshQTableMap();
		this.updateQLearningStatus(qLearningAgent.getStatus());
		this.enableScreen();
	}
	
	private void updateQLearningStatus(QLearningStatus status) {
		lblRunTime.setText(NumberFormat.getInstance().format(status.getRunTime()) + " ms");
		lblEpisodeCount.setText(NumberFormat.getInstance().format(status.getEpisodesCount()));
		lblPolicyChangeCount.setText(NumberFormat.getInstance().format(status.getPolicyChangeCount()));
		
		int hits = status.getHits().size();
		lblQLearningHits.setText(String.valueOf((int) hits));
		lblQLearningMisses.setText(String.valueOf((int)(status.getAccessiblesStates() - hits)));
		lblEfficiencyQLearning.setText(status.getEfficiency() + "%");
	}
	
	private void drawAgent(StateRectangle stateRectangle) {
		State state = stateRectangle.getState();
		if (environment.isPossibleToMove(state) && !state.getTrafficSituation().equals(TrafficSituation.TARGET)) {
			//desenha o agente Q-Learning
			qLearningPane.drawAgent(stateRectangle);
			
			//desenha o agente A*
			aStarPane.drawAgent(stateRectangle);
			
			//desenha a linha do agente A* até o objetivo
			this.drawAStarLine(stateRectangle);
			
			if (qLearningAgent != null) {
				this.drawQLearningLine(stateRectangle);
			}
		}
	}
	
	/**
	 * Desenha o caminho do agente Q-Learning até o objetivo com base em sua tabela de aprendizado
	 * @param stateRectangle
	 */
	private void drawQLearningLine(StateRectangle stateRectangle) {
		State start = stateRectangle.getState();
		List<State> nodes = qLearningAgent.getRouteToTarget(start);
		
		qLearningPane.clearLines();
		if (nodes == null || nodes.isEmpty()) {
			lblQLearningCost.setText("");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Não foi possível encontrar o objetivo a partir deste estado.");
			alert.showAndWait();
		} else {
			qLearningPane.drawRoute(nodes);
			lblQLearningCost.setText(NumberFormat.getInstance().format(Utils.calculateCostToTarget(nodes)));
		}
	}
	
	/**
	 * Calcula o melhor caminho com base no algoritmo A*
	 * @param stateRectangle
	 */
	private void drawAStarLine(StateRectangle stateRectangle) {
		State start = stateRectangle.getState();
		List<State> nodes = PathFinding.doAStar(environment, start);
		
		aStarPane.clearLines();
		if (nodes == null || nodes.isEmpty()) {
			lblAStarCost.setText("");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText("Não foi possível encontrar o objetivo a partir deste estado.");
			alert.showAndWait();
		} else {
			aStarPane.drawRoute(nodes);
			lblAStarCost.setText(NumberFormat.getInstance().format(Utils.calculateCostToTarget(nodes)));
		}
	}
	
	private void refreshQTableMap() {
		double greaterReward = qLearningAgent.getGreaterReward();
		double lowerReward = qLearningAgent.getLowerReward();
		
		for (Entry<State, TrafficSituation> entry : environment.getTrafficTableEntrySet()) {
			Rectangle rect = new Rectangle();
			State state = entry.getKey();
			rect.setX((state.getX() * STATE_SIZE_PX));
			rect.setY((state.getY() * STATE_SIZE_PX));
			rect.setWidth(STATE_SIZE_PX);
			rect.setHeight(STATE_SIZE_PX);
			rect.setStroke(Color.GRAY);
			
			TrafficSituation situation = entry.getValue();
			if (situation == TrafficSituation.BLOCKED || situation == TrafficSituation.TARGET) {
				rect.setFill(Color.valueOf(entry.getValue().getColor()));
			} else {
				QValue qValue = qLearningAgent.getBestQValue(state, false);
				
				if (qValue != null) {
					double r = qValue.getReward();
					
					//double fraction = (r - lowerReward) / (greaterReward - lowerReward);
					double fraction;
					if (r < 0) {
						fraction = (r - lowerReward) / (0 - lowerReward);
						rect.setFill(Color.DARKGRAY.interpolate(Color.WHITE, fraction));
					} else {
						fraction = r / greaterReward;
						rect.setFill(Color.WHITE.interpolate(Color.PINK, fraction));
					}
					//System.out.println(state + " - Reward: " + r + " - Fraction: " + fraction);
				} else {
					//se o agente não possui um QValue para o estado
					rect.setFill(Color.ORANGE);
				}
				
			}
			mapQLearningPane.getChildren().add(rect);
		}
	}
}
