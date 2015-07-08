package br.com.guisi.simulador.trafego.constants;

public interface Constants {

	/* custo de um passo do agente */
	double STEP_COST = 0.1;
	
	
	/** Q-Learning */
	
	/* E-greedy de 90% */
	double E_GREEDY = 0.9;

	/* Constante de aprendizagem (alpha) */
	double LEARNING_CONSTANT = 0.1;
	
	/* Fator de desconto (gamma) */
	double DISCOUNT_FACTOR = 0.9;
	
	
	/** ambiente */
	double TRAFFIC_REWARD_FREE = -0.1;
	double TRAFFIC_REWARD_LOW = -0.2;
	double TRAFFIC_REWARD_MEDIUM = -0.3;
	double TRAFFIC_REWARD_HIGH = -0.4;
	double TRAFFIC_REWARD_BLOCKED = -1;
	double TRAFFIC_REWARD_TARGET = 1;
	
}
