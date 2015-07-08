package br.com.guisi.simulador.trafego.qlearning;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import br.com.guisi.simulador.trafego.environment.State;

public class QLearningStatus implements Cloneable {

	private int episodesCount;
	private int policyChangeCount;
	private boolean running;
	private long runTime;
	private Set<State> hits = new HashSet<>();
	private int accessiblesStates;
	private BigDecimal efficiency;

	public void incrementEpisodesCount() {
		episodesCount++;
	}
	
	public void incrementPolicyChangeCount() {
		policyChangeCount++;
	}
	
	public Set<State> getHits() {
		return hits;
	}

	public int getEpisodesCount() {
		return episodesCount;
	}
	
	public int getPolicyChangeCount() {
		return policyChangeCount;
	}

	public void setPolicyChangeCount(int policyChangeCount) {
		this.policyChangeCount = policyChangeCount;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getRunTime() {
		return runTime;
	}

	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public BigDecimal getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(BigDecimal efficiency) {
		this.efficiency = efficiency;
	}

	public int getAccessiblesStates() {
		return accessiblesStates;
	}

	public void setAccessiblesStates(int accessiblesStates) {
		this.accessiblesStates = accessiblesStates;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
