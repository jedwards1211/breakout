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

import org.andork.collect.PriorityEntry;

/**
 * A bunch of slightly different interfaces to Dijkstra's algorithm.
 */
public class Dijkstra {
	public static <Node extends Comparable<Node>, Id> void traverse(Stream<Node> startNodes,
			Function<Node, Id> nodeId, Function<Node, Stream<Node>> visit, BooleanSupplier keepGoing) {
		PriorityQueue<Node> unvisited = new PriorityQueue<>();
		startNodes.forEach(n -> unvisited.add(n));

		Set<Id> visited = new HashSet<>();

		while (!unvisited.isEmpty() && keepGoing.getAsBoolean()) {
			Node node = unvisited.poll();
			Id id = nodeId.apply(node);
			if (visited.add(id)) {
				visit.apply(node).forEach(n -> unvisited.add(n));
			}
		}
	}

	public static <Node extends Comparable<Node>, Id> void traverse(Stream<Node> startNodes,
			Predicate<Node> nodeVisitor,
			Function<Node, Id> nodeId, Function<Node, Stream<Node>> getNeighbors, BooleanSupplier keepGoing) {
		traverse(
				startNodes,
				nodeId,
				n -> {
					if (!nodeVisitor.test(n)) {
						return Stream.empty();
					}
					return getNeighbors.apply(n);
				},
				keepGoing);
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
		traverse(startNodes.map(n -> new PriorityEntry<>(initialPriority.applyAsDouble(n), n)),
				p -> true,
				p -> p.getValue(),
				p -> connectedEdges.apply(p.getValue(), p.getKey()).map(
						e -> new PriorityEntry<>(p.getKey() + edgeCost.applyAsDouble(e),
								nextNode.apply(p.getValue(), e))),
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
		traverse(startNodes.map(n -> new PriorityEntry<>(initialPriority.applyAsDouble(n), n)),
				(PriorityEntry<Double, Node> p) -> nodeVisitor.test(p.getValue(), p.getKey()),
				p -> p.getValue(),
				p -> connectedEdges.apply(p.getValue()).map(
						e -> new PriorityEntry<>(p.getKey() + edgeCost.applyAsDouble(e),
								nextNode.apply(p.getValue(), e))),
				bgRunner, bgPeriod, keepGoing);
	}

	public static <Node> void traverse(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiPredicate<Node, Double> nodeVisitor,
			Function<Node, Stream<Node>> connected,
			ToDoubleFunction<Node> nodeCost) {
		traverse(startNodes.map(n -> new PriorityEntry<>(initialPriority.applyAsDouble(n), n)),
				(PriorityEntry<Double, Node> p) -> nodeVisitor.test(p.getValue(), p.getKey()),
				p -> p.getValue(),
				p -> connected.apply(p.getValue()).map(
						n -> new PriorityEntry<>(
								p.getKey() + nodeCost.applyAsDouble(p.getValue()), n)),
				() -> true);
	}

	public static <Node, Edge> void traverse(Stream<Node> startNodes,
			ToDoubleFunction<Node> initialPriority,
			BiPredicate<Node, Double> nodeVisitor,
			Function<Node, Stream<Edge>> connectedEdges,
			ToDoubleFunction<Edge> edgeCost,
			BiFunction<Node, Edge, Node> nextNode,
			BooleanSupplier keepGoing) {
		traverse(startNodes.map(n -> new PriorityEntry<>(initialPriority.applyAsDouble(n), n)),
				(PriorityEntry<Double, Node> p) -> nodeVisitor.test(p.getValue(), p.getKey()),
				p -> p.getValue(),
				p -> connectedEdges.apply(p.getValue()).map(
						e -> new PriorityEntry<>(p.getKey() + edgeCost.applyAsDouble(e),
								nextNode.apply(p.getValue(), e))),
				keepGoing);
	}

	/**
	 * Traverses edges using Dijkstra's algorithm
	 *
	 * @param startEdges
	 *            the edges to start from
	 * @param getEdgeLength
	 *            function that gets the length of an edge
	 * @param visitEdge
	 *            function that takes the edge and returns a stream of its
	 *            connected edges
	 * @param keepGoing
	 *            called at every step. if it returns false, the algorithm
	 *            stops.
	 */
	public static <Edge> void traverseEdges(Stream<Edge> startEdges,
			ToDoubleFunction<Edge> getEdgeLength,
			Function<Edge, Stream<Edge>> visitEdge,
			BooleanSupplier keepGoing) {
		traverse(startEdges.map(edge -> new PriorityEntry<>(getEdgeLength.applyAsDouble(edge), edge)),
				edge -> edge.getValue(),
				edge -> visitEdge.apply(edge.getValue()).map(connected -> new PriorityEntry<>(
						edge.getKey() + getEdgeLength.applyAsDouble(connected),
						connected)),
				keepGoing);
	}

	/**
	 * Traverses edges using Dijkstra's algorithm
	 *
	 * @param startEdges
	 *            the edges to start from
	 * @param getEdgeLength
	 *            function that gets the length of an edge
	 * @param visitEdge
	 *            function that takes the edge and returns a stream of its
	 *            connected edges
	 */
	public static <Edge> void traverseEdges(Stream<Edge> startEdges,
			ToDoubleFunction<Edge> getEdgeLength,
			Function<Edge, Stream<Edge>> visitEdge) {
		traverseEdges(startEdges, getEdgeLength, visitEdge, () -> true);
	}
}
