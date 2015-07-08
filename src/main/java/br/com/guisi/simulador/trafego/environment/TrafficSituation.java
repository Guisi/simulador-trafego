package br.com.guisi.simulador.trafego.environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import br.com.guisi.simulador.trafego.constants.Constants;

public enum TrafficSituation {

	FREE("Livre", Constants.TRAFFIC_REWARD_FREE, "#E6FFFF"),
	LOW_TRAFFIC("Pouco congestionado", Constants.TRAFFIC_REWARD_LOW, "#96C8FF"), 
	MEDIUM_TRAFFIC("Congestionado", Constants.TRAFFIC_REWARD_MEDIUM, "#3264FF"),
	HIGH_TRAFFIC("Muito congestionado", Constants.TRAFFIC_REWARD_HIGH, "#0000C8"),
	BLOCKED("Bloqueado", Constants.TRAFFIC_REWARD_BLOCKED, "#000000"),
	TARGET("Objetivo", Constants.TRAFFIC_REWARD_TARGET, "#FFFF00");
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final List<TrafficSituation> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size() - 1; //-1 para nao considerar o objetivo no randomico 
	
	private String description;
	private double reward;
	private final String color;
	
	private TrafficSituation(String description, double reward, String color) {
		this.description = description;
		this.reward = reward;
		this.color = color;
	}

	public String getDescription() {
		return description;
	}

	public double getReward() {
		return reward;
	}
	
	public String getColor() {
		return color;
	}

	public static TrafficSituation getRandomTrafficSituation() {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
	
}
