package com.example.learningapp

import org.graphstream.graph.Graph
import org.graphstream.graph.Node


class Dijkstra {
    var SOURCE_X : Float = 5000F
    var SOURCE_Y : Float = 10000F
    var CANVAS_WIDTH : Float = 10000F
    var Y_STEP : Float = 1000F
    var j = 0
    var t: Int = 0
    var ds: Int = 0
    private var graphSourceIndex : Int
    private var graphSourceNode : Node
    private lateinit var treeSourceNode : Node
    private var dest : String
    private lateinit var treeCurrentNode : Node
    private lateinit var graphCurrentNode: Node
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
        this.graphSourceNode =graph.getNode(source)
        this.graphSourceIndex= source.toInt()
        this.dest = dest


    }
    fun getCurrentNodeIndex(): Int {
        return currentIndex
    }

    fun initialize(){
        edgeCount = graph.edgeCount
        nodeCount = graph.nodeCount
        nodePaths = Array(this.nodeCount) {kotlin.collections.mutableListOf()}
        isSelected= Array(this.nodeCount){false}

        for (i in 0 until edgeCount){ // c
            upperBound += graph.getEdge(i).getAttribute("length").toString().toInt()
        }

        pathLengths=Array(this.nodeCount) {upperBound}
        previousPathLengths=Array(this.nodeCount) {upperBound}

        isSelected[graphSourceIndex] = true
        tree.addNode(graphSourceNode.id).
            setAttributes(mapOf("x" to SOURCE_X, "y" to SOURCE_Y, "width" to CANVAS_WIDTH))
        treeSourceNode = tree.getNode(graphSourceNode.id)

        val cnt = graph.getNode(graphSourceNode.id).neighborNodes().count().toInt()
        var i = 1
        graph.getNode(graphSourceNode.id).neighborNodes().iterator().forEach {
            candidates.add(it.id.toInt())
            val width = treeSourceNode.getAttribute("width").toString().toFloat()/cnt
            val x = treeSourceNode.getAttribute("width").toString().toFloat()/(cnt+1)*i
            val y = treeSourceNode.getAttribute("y").toString().toFloat()-Y_STEP

            tree.addNode(it.id).setAttributes(mapOf("x" to x, "y" to y, "width" to width))
            i+=1
            j+=1

        }

        graph.getNode(graphSourceNode.id).edges().iterator().forEach {
            var id = it.id.toString()
            candidateLengths.add(it.getAttribute("length").toString().toInt())
            tree.addEdge(id, graphSourceNode.id, it.node1.id).setAttribute("length", it.getAttribute("length"))
        }
        for (i in 0 until candidates.size) {
            j = candidates[i]
            pathLengths[j] = candidateLengths[i]
            nodePaths[j].add(graphSourceIndex)
        }
    }
    fun performStep(): MutableList<Int> {
        pathLengths.copyInto(previousPathLengths)
        minPathLength = upperBound

        for (i in 0 until candidates.size) {
            j = candidates[i]
            if (pathLengths[j] < minPathLength) {
                minPathLength = pathLengths[j]
                currentIndex = j
                graphCurrentNode = graph.getNode("$currentIndex")
                treeCurrentNode = tree.getNode("$currentIndex")
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
        graphCurrentNode.neighborNodes().iterator().forEach {
            currentNodeAdjacents.add(it.id.toInt())
        }

        currentNodeAdjLengths.clear()

        graphCurrentNode.edges().iterator().forEach {
            currentNodeAdjLengths.add(it.getAttribute("length").toString().toInt())

        }


            t = 1
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
                ds = minPathLength + unselectedAdjLengths[i]
                if (pathLengths[j] == upperBound && pathLengths[j] > ds) candidates.add(j)
                pathLengths[j] = ds
                nodePaths[j].clear()
                nodePaths[j].addAll(nodePaths[currentIndex])
                nodePaths[j].add(currentIndex)
            }
            nodePaths[currentIndex].clear()

        var cnt = graphCurrentNode.neighborNodes().count()
        var i = 1F
        var addedNodes= mutableListOf<String>()

        for(node in graphCurrentNode.neighborNodes()) {
            val idx = node.id
            if (tree.getNode(idx) != null) {
                if ((tree.getNode(idx).edges()
                        .count() != 0.toLong()) && (pathLengths[idx.toInt()] < previousPathLengths[idx.toInt()]))
                {
                    tree.removeNode(idx)
                    val width = treeCurrentNode.getAttribute("width").toString().toFloat() / cnt
                    val x = treeCurrentNode.getAttribute("x").toString().toFloat() +
                            treeCurrentNode.getAttribute("width").toString().toFloat()*(i/(cnt+1)-0.5)
                    val y = treeCurrentNode.getAttribute("y").toString().toFloat() - Y_STEP
                    tree.addNode(node.id).setAttributes(mapOf("x" to x, "y" to y, "width" to width))
                    addedNodes.add(node.id)
                    i += 1
                } else  cnt -= 1
                }else {
                val width = treeCurrentNode.getAttribute("width").toString().toFloat() / cnt
                val x = treeCurrentNode.getAttribute("x").toString().toFloat() +
                        treeCurrentNode.getAttribute("width").toString().toFloat()*(i/(cnt+1)-0.5)
                val y = treeCurrentNode.getAttribute("y").toString().toFloat() - Y_STEP
                tree.addNode(node.id).setAttributes(mapOf("x" to x, "y" to y, "width" to width))
                addedNodes.add(node.id)
                i += 1
            }
        }
        for (edge in graphCurrentNode.edges()) { //fix all edges to be added
            if ((addedNodes.contains(edge.node0.id))||addedNodes.contains(edge.node1.id) ){
                tree.addEdge(currentIndex.toString()+edge.getOpposite(graphCurrentNode).id,
                             currentIndex.toString(),
                             edge.getOpposite(graphCurrentNode).id)
                        .setAttribute("length",edge.getAttribute("length"))
            }
        }
        return nodePaths[currentIndex]
    }


}