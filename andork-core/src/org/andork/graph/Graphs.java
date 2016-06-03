/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.graph;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class Graphs {
	public static class PriorityNode<N, P extends Comparable<P>> implements Comparable<PriorityNode<N, P>> {
		public final N node;
		public final P priority;

		public PriorityNode(N node, P priority) {
			super();
			this.node = node;
			this.priority = priority;
		}

		@Override
		public int compareTo(PriorityNode<N, P> o) {
			return priority.compareTo(o.priority);
		}
	}

	public static <Node extends Comparable<Node>, Id> void traverse(Stream<Node> startNodes,
			Predicate<Node> nodeVisitor,
			Function<Node, Id> nodeId, Function<Node, Stream<Node>> connected, BooleanSupplier keepGoing) {
		PriorityQueue<Node> queue = new PriorityQueue<>();
		startNodes.forEach(n -> queue.add(n));

		Set<Id> visited = new HashSet<>();

		while (!queue.isEmpty() && keepGoing.getAsBoolean()) {
			Node node = queue.poll();
			Id id = nodeId.apply(node);
			if (!visited.add(id)) {
				continue;
			}

			if (!nodeVisitor.test(node)) {
				continue;
			}

			connected.apply(node).forEach(n -> queue.add(n));
		}
	}

	public static <Node extends Comparable<Node>, Id> void traverse(Stream<Node> startNodes,
			Predicate<Node> nodeVisitor,
			Function<Node, Id> nodeId, Function<Node, Stream<Node>> connected, Consumer<Runnable> bgRunner,
			long bgPeriod, BooleanSupplier keepGoing) {
		PriorityQueue<Node> queue = new PriorityQueue<>();
		startNodes.forEach(n -> queue.add(n));

		Set<Id> visited = new HashSet<>();

		while (!queue.isEmpty() && keepGoing.getAsBoolean()) {
			bgRunner.accept(() -> {
				long startTime = System.currentTimeMillis();
				while (!queue.isEmpty() && keepGoing.getAsBoolean()) {
					Node node = queue.poll();
					Id id = nodeId.apply(node);
					if (!visited.add(id)) {
						continue;
					}

					if (!nodeVisitor.test(node)) {
						continue;
					}

					connected.apply(node).forEach(n -> queue.add(n));

					if (System.currentTimeMillis() - startTime > bgPeriod) {
						return;
					}
				}
			});
		}
	}

	public static <Node, Edge> void traverse(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiFunction<Node, Double, Stream<Edge>> connectedEdges,
			ToDoubleFunction<Edge> edgeCost,
			BiFunction<Node, Edge, Node> nextNode,
			Consumer<Runnable> bgRunner,
			long bgPeriod,
			BooleanSupplier keepGoing) {
		traverse(startNodes.map(n -> new PriorityNode<>(n, initialPriority.applyAsDouble(n))),
				p -> true,
				(PriorityNode<Node, Double> p) -> p.node,
				p -> connectedEdges.apply(p.node, p.priority).map(
						e -> new PriorityNode<>(nextNode.apply(p.node, e),
								p.priority + edgeCost.applyAsDouble(e))),
				bgRunner, bgPeriod, keepGoing);
	}

	public static <Node, Edge> void traverse(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiPredicate<Node, Double> nodeVisitor,
			Function<Node, Stream<Edge>> connectedEdges,
			ToDoubleFunction<Edge> edgeCost,
			BiFunction<Node, Edge, Node> nextNode,
			Consumer<Runnable> bgRunner,
			long bgPeriod,
			BooleanSupplier keepGoing) {
		traverse(startNodes.map(n -> new PriorityNode<>(n, initialPriority.applyAsDouble(n))),
				(PriorityNode<Node, Double> p) -> nodeVisitor.test(p.node, p.priority),
				p -> p.node,
				p -> connectedEdges.apply(p.node).map(
						e -> new PriorityNode<>(nextNode.apply(p.node, e),
								p.priority + edgeCost.applyAsDouble(e))),
				bgRunner, bgPeriod, keepGoing);
	}

	public static <Node> void traverse(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiPredicate<Node, Double> nodeVisitor,
			Function<Node, Stream<Node>> connected,
			ToDoubleFunction<Node> nodeCost) {
		traverse(startNodes.map(n -> new PriorityNode<Node, Double>(n, initialPriority.applyAsDouble(n))),
				(PriorityNode<Node, Double> p) -> nodeVisitor.test(p.node, p.priority),
				p -> p.node,
				p -> connected.apply(p.node).map(
						n -> new PriorityNode<Node, Double>(n, p.priority + nodeCost.applyAsDouble(p.node))),
				() -> true);
	}

	public static <Node, Edge> void traverse2(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiPredicate<Node, Double> nodeVisitor,
			Function<Node, Stream<Edge>> connectedEdges,
			ToDoubleFunction<Edge> edgeCost,
			BiFunction<Node, Edge, Node> nextNode,
			BooleanSupplier keepGoing) {
		traverse(startNodes.map(n -> new PriorityNode<>(n, initialPriority.applyAsDouble(n))),
				(PriorityNode<Node, Double> p) -> nodeVisitor.test(p.node, p.priority),
				p -> p.node,
				p -> connectedEdges.apply(p.node).map(
						e -> new PriorityNode<>(nextNode.apply(p.node, e),
								p.priority + edgeCost.applyAsDouble(e))),
				keepGoing);
	}
}
