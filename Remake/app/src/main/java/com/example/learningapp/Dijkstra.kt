package com.example.learningapp

import org.graphstream.graph.Graph
import org.graphstream.graph.Node


class Dijkstra {
    private val SOURCE_X: Int = 1000
    private val SOURCE_Y: Int = 1000
    private var sourceNode : Node
    private var sourceIndex : Int = 0
    private var dest : String
    private lateinit var currentNode: Node
    private var currentIndex: Int = 0 //r - индекс минимального элемента d в массиве L
    private var upperBound: Int = 10        //c - верхняя граница
    private var minPathLength: Int = 0     // d-минимальный элемент в массиве L
    private var edgeCount: Int = 0
    private var nodeCount: Int = 0   // n - мощность множества вершин
    private lateinit var isSelected : Array<Boolean>/* XZ - массив , в котором:
                 xz[i] = 0 , если вершина x[i] не выбрана
                 xz[i] = 1 - в противном случае*/
    private lateinit var previousPathLengths :Array<Int>
    private lateinit var pathLengths :Array<Int> // L - массив суммарных длин фрагментов пути
    private var candidates =
        mutableListOf<Int>() // Y – кандидаты на включение в цепь на текущем шаге алгоритма
    private var candidateLengths = mutableListOf<Int>() // VR - массив весов Y
    lateinit  var nodePaths: Array<MutableList<Int>>
         /* M[i] - список вершин, составляющих фрагмент из source
                                            в i-ую вершину */

    private var currentNodeAdjacents = mutableListOf<Int>() // F = массив смежных r вершин
    private var currentNodeAdjLengths = mutableListOf<Int>() // V - массив весов для F
    private var unselectedAdjacents = mutableListOf<Int>() // F = массив смежных r вершин, не принадлежащих XZ
    private var unselectedAdjLengths = mutableListOf<Int>() // V - массив весов для Z





    constructor(graph: Graph, source: String, dest: String) {
        this.sourceNode = graph.getNode(source)
        sourceIndex = sourceNode.id.toInt()
        this.dest = dest
    }
    fun getCurrentNodeIndex(): Int {
        return currentIndex
    }

    fun initialize(){
        var j =0
        edgeCount = graph.edgeCount
        nodeCount = graph.nodeCount
        nodePaths = Array(this.nodeCount) {kotlin.collections.mutableListOf()}
        isSelected= Array(this.nodeCount){false}

        for (i in 0 until edgeCount){ // c
            upperBound += graph.getEdge(i).getAttribute("length").toString().toInt()
        }

        pathLengths=Array(this.nodeCount) {upperBound}
        previousPathLengths=Array(this.nodeCount) {upperBound}

        isSelected[sourceIndex] = true
        tree.addNode(sourceNode.id)
        graph.getNode(sourceNode.id).neighborNodes().iterator().forEach {
            candidates.add(it.id.toInt())
            tree.addNode(it.id)
            j+=1
        }
        graph.getNode(sourceNode.id).edges().iterator().forEach {
            var id = it.id.toString()
            candidateLengths.add(it.getAttribute("length").toString().toInt())
            tree.addEdge(id,sourceNode.id,it.node1.id).setAttribute("length",it.getAttribute("length"))
        }
        for (i in 0 until candidates.size) {
            j = candidates[i]
            pathLengths[j] = candidateLengths[i]
            nodePaths[j].add(sourceIndex)
        }
    }
    fun performStep(): MutableList<Int> {
        var j =0
        pathLengths.copyInto(previousPathLengths)
        minPathLength = upperBound

        for (i in 0 until candidates.size) {
            j = candidates[i]
            if (pathLengths[j] < minPathLength) {
                minPathLength = pathLengths[j]
                currentIndex = j
                currentNode = graph.getNode("$currentIndex")
            }
        }

        isSelected[currentIndex] = true
        pathLengths[currentIndex] = upperBound
        candidates.remove(currentIndex)

        if (currentIndex ==dest.toInt()){
            nodePaths[currentIndex].add(currentIndex)
            return nodePaths[currentIndex]
        }


        currentNodeAdjacents.clear()
        currentNode.neighborNodes().iterator().forEach {
            currentNodeAdjacents.add(it.id.toInt())
        }

        currentNodeAdjLengths.clear()

        currentNode.edges().iterator().forEach {
            currentNodeAdjLengths.add(it.getAttribute("length").toString().toInt())

        }


            var t = 1
            unselectedAdjacents.clear()
            unselectedAdjLengths.clear()
            for (i in 0 until currentNodeAdjacents.size) {
                j = currentNodeAdjacents[i]
                if (!isSelected[j]) {
                    unselectedAdjacents.add(currentNodeAdjacents[i])
                    unselectedAdjLengths.add(currentNodeAdjLengths[i])
                    t += 1
                }
            }
            for (i in 0 until t - 1) {
                j = unselectedAdjacents[i]
                var ds = minPathLength + unselectedAdjLengths[i]
                if (pathLengths[j] == upperBound && pathLengths[j] > ds) candidates.add(j)
                pathLengths[j] = ds
                nodePaths[j].clear()
                nodePaths[j].addAll(nodePaths[currentIndex])
                nodePaths[j].add(currentIndex)
            }
            nodePaths[currentIndex].clear()

        for(node in currentNode.neighborNodes()) {
            val idx = node.id
            if (tree.getNode(idx)==null) {
                tree.addNode(idx)
            }else if((tree.getNode(idx).edges()
                    .count() != 0.toLong()) && pathLengths[idx.toInt()] < previousPathLengths[idx.toInt()]) {
                    tree.removeNode(idx)
                    tree.addNode(idx)
                }
            }
        for (edge in currentNode.edges()) {
            if ((tree.getEdge(edge.id) == null)&&(tree.getEdge(edge.id.reversed())==null)) {
                tree.addEdge(currentIndex.toString()+edge.getOpposite(currentNode).id,
                             currentIndex.toString(),
                             edge.getOpposite(currentNode).id)
                        .setAttribute("length",edge.getAttribute("length"))
            }
        }
        return nodePaths[currentIndex]

    }
}