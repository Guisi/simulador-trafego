package br.com.guisi.simulador.trafego.astar;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import br.com.guisi.simulador.trafego.environment.Environment;
import br.com.guisi.simulador.trafego.environment.State;

public class PathFinding {

	public static List<State> doAStar(Environment environment, State start) {
		State goal = environment.getTargetState();
		Set<State> closed = new HashSet<State>();
		Map<State, State> fromMap = new HashMap<State, State>();
		List<State> route = new LinkedList<State>();
		Map<State, Double> gScore = new HashMap<State, Double>();
		final Map<State, Double> fScore = new HashMap<State, Double>();
		
		PriorityQueue<State> open = new PriorityQueue<State>(11, new Comparator<State>() {
			public int compare(State nodeA, State nodeB) {
				return Double.compare(fScore.get(nodeA), fScore.get(nodeB));
			}
		});

		gScore.put(start, 0.0);
		fScore.put(start, start.getHeuristic(goal));
		open.offer(start);

		while (!open.isEmpty()) {
			State current = open.poll();
			if (current.equals(goal)) {
				while (current != null) {
					route.add(0, current);
					current = fromMap.get(current);
				}

				return route;
			}

			closed.add(current);

			Set<State> neighbours = environment.getNeighbours(current);
			for (State neighbour : neighbours) {
				if (closed.contains(neighbour)) {
					continue;
				}

				double tentG = gScore.get(current)
						+ current.getTraversalCost();

				boolean contains = open.contains(neighbour);
				if (!contains || tentG < gScore.get(neighbour)) {
					gScore.put(neighbour, tentG);
					fScore.put(neighbour, tentG + neighbour.getHeuristic(goal));

					if (contains) {
						open.remove(neighbour);
					}

					open.offer(neighbour);
					fromMap.put(neighbour, current);
				}
			}
		}

		return null;
	}

}
