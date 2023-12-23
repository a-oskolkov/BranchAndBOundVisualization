package com.example.learningapp

    import android.os.Bundle
    import android.view.View
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.FragmentManager
    import androidx.fragment.app.FragmentTransaction
    import org.graphstream.graph.Graph
    import org.graphstream.graph.implementations.MultiGraph
    import org.graphstream.ui.android_viewer.util.DefaultFragment

    var graph: Graph = MultiGraph("Graph")
    var tree: Graph = MultiGraph("Tree")

class Activity_withXML : AppCompatActivity() {

    private val fileManager = FileManager(this)
    lateinit var dj:Dijkstra
    var destinationIndex:Int = 0
    var isInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_xml)
        var numNodes :Int
        var addedEdges : ArrayList<HashMap<String, Any>>?
        var source  = ""
        var destination  = ""

        if (intent.hasExtra("numVertices")){
            numNodes = intent.getStringExtra("numVertices")?.toInt() ?: 0
            addedEdges = intent.getSerializableExtra("addedEdges") as ArrayList<HashMap<String, Any>>?
            source = intent.getStringExtra("startDijkstra") ?: ""
            destination = intent.getStringExtra("endDijkstra") ?: ""
            destinationIndex = destination.toInt()


            for (i in 0 until numNodes) {
                graph.addNode(i.toString())
            }
            addedEdges?.forEach { edgeData ->
                val startNode = edgeData["startVertex"].toString()
                val endNode = edgeData["endVertex"].toString()
                val edgeWeight = edgeData["edgeWeight"] as Int

                // Concatenate vertex IDs to create the edge ID
                val edgeId = "$startNode$endNode"

                // Add the edge to the graph with the specified length attribute
                graph.addEdge(edgeId, startNode, endNode).setAttribute("length", edgeWeight)
            }
        } else if(intent.hasExtra("fileName")){
            val fileName = intent.getStringExtra("fileName")
            source = intent.getStringExtra("startDijkstra") ?: ""
            destination = intent.getStringExtra("endDijkstra") ?: ""
            destinationIndex = destination.toInt()
            val savedGraphData = fileName?.let { fileManager.loadGraphDataFromFile(it) }
            if (savedGraphData != null) {
                // Initialize the graph with saved data
                initializeGraph(graph, savedGraphData.numVertices, savedGraphData.edges)
            }
        }


        tree.setAttribute(
            "ui.stylesheet", "" +
                    "graph { padding: 0px; fill-color: #FFF; }" +
                    "edge { text-size: 48; size: 5;}" +
                    "edge.path{ size: 8px; stroke-color: #FAF; stroke-width: 1px; stroke-mode: plain; }"+
                    "node { text-size: 48; fill-color: white; size: 40px, 40px; padding: 15px, 15px;" +
                    " stroke-mode: plain; stroke-color: #555; shape: circle; }" +
                    "node.path{fill-color: #503"+
                    "node#B {shape: circle;}"
        )
        dj = Dijkstra(graph,source,destination)
        display(savedInstanceState, tree, false)



    }

    private fun initializeGraph(graph: Graph, numVertices: Int, addedEdges: List<FileManager.Edge>) {
        // Add vertices to the graph
        for (i in 0 until numVertices) {
            graph.addNode(i.toString())
            // You can set node attributes or styles as needed
        }

        // Add edges to the graph
        for (edge in addedEdges) {
            val edgeId = edge.startVertex.toString() + edge.endVertex.toString()
            val startNode = graph.getNode(edge.startVertex.toString())
            val endNode = graph.getNode(edge.endVertex.toString())

            val newEdge = graph.addEdge(edgeId, startNode, endNode, true)
                .setAttribute("length", edge.weight)
            // You can set edge attributes or styles as needed
        }
    }

    fun onClick(view: View){
        var textView = findViewById<TextView>(R.id.pathView)
        if (!isInitialized){
            dj.initialize()
            isInitialized = true
        } else if(dj.getCurrentNodeIndex() != destinationIndex){
            textView.text= dj.performStep()?.toString()
            if(textView.text!="[]")  textView.visibility = View.VISIBLE
        }
        tree.nodes().forEach{it.setAttribute("ui.label", it.id)}
        tree.edges().forEach{it.setAttribute(
            "ui.label",  it.getAttribute("length"))}

    }

    fun display(savedInstanceState: Bundle?, graph: Graph?, autoLayout: Boolean) {
        if (savedInstanceState == null) {
            val fm: FragmentManager = supportFragmentManager
            var fragment = fm.findFragmentByTag("fragment_tag") as? DefaultFragment
            if (fragment == null ) {
                fragment = DefaultFragment()
                fragment.init(graph, autoLayout)
            }
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.layoutFragment, fragment).commit()
        }
    }
}