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
lateinit var dj:Dijkstra
var destinationIndex:Int = 0
var isInitialized = false


class Activity_withXML : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_xml)

        val numEdges = intent.getStringExtra("numVertices")?.toInt() ?: 0
        val addedEdges = intent.getSerializableExtra("addedEdges") as ArrayList<HashMap<String, Any>>?
        val source = intent.getStringExtra("startDijkstra") ?: ""
        val destination = intent.getStringExtra("endDijkstra") ?: ""

        destinationIndex = destination.toInt()
        dj = Dijkstra(graph,source,destination)

        for (i in 0 until numEdges) {
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
        graph.setAttribute(
            "ui.stylesheet", "" +
                    "graph { padding: 0px; fill-color: #FFF; }" +
                    "edge { text-size: 48; size: 5;}" +
                    "edge.path{ size: 8px; stroke-color: #FAF; stroke-width: 1px; stroke-mode: plain; }"+
                    "node { text-size: 48; fill-color: white; size: 40px, 40px; padding: 15px, 15px;" +
                    " stroke-mode: plain; stroke-color: #555; shape: circle; }" +
                    "node.path{fill-color: #503"+
                    "node#B {shape: circle;}"
        )
        graph.getNode(source).setAttribute("xy",100,100)
        graph.nodes().forEach{it.setAttribute("ui.label", it.id)}
        graph.edges().forEach{it.setAttribute(
            "ui.label",  it.getAttribute("length"))}

        display(savedInstanceState, graph, true)



    }
    fun onClick(view: View){
        var textView = findViewById<TextView>(R.id.pathView)
        if (!isInitialized){
            dj.initialize()
            isInitialized = true
        } else if(dj.getCurrentNodeIndex() != destinationIndex){
            textView.text= dj.performStep()?.toString()
        } else{
            textView.visibility = View.VISIBLE
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