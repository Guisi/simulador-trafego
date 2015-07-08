package br.com.guisi.simulador.trafego.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import br.com.guisi.simulador.trafego.constants.Constants;
import br.com.guisi.simulador.trafego.environment.State;
import br.com.guisi.simulador.trafego.environment.TrafficSituation;

public class Utils {

	private Utils() {}
	
	public static BigDecimal calculateCostToTarget(List<State> nodes) {
		BigDecimal d = BigDecimal.ZERO;
		//conta o passo a partir do ponto de inicio
		d = d.add(BigDecimal.valueOf(Constants.STEP_COST));
		//mas itera sem contar o ponto de inicio para nao somar o seu custo
		for (int i = 1; i < nodes.size(); i++) {
			State state = nodes.get(i);
			if (!state.getTrafficSituation().equals(TrafficSituation.TARGET)) {
				d = d.add(BigDecimal.valueOf(state.getTraversalCost()).setScale(1, RoundingMode.HALF_UP));
			}
		}
		
		return d.setScale(1, RoundingMode.HALF_UP);
	}
}
