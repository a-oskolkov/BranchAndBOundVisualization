package com.example.learningapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson

class ActivityNewGraph : AppCompatActivity() {


    private lateinit var verticesEditText: EditText
    private lateinit var startVertexSpinner: Spinner
    private lateinit var endVertexSpinner: Spinner
    private lateinit var edgeWeightEditText: EditText
    private lateinit var spinnerStartDijkstra: Spinner
    private lateinit var spinnerEndDijkstra: Spinner
    private lateinit var addButton: Button
    private lateinit var newGraphButton: Button
    private lateinit var listViewEdges: ListView
    private val addedEdges = mutableListOf<HashMap<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_graph)
        verticesEditText = findViewById(R.id.editTextVertices)
        startVertexSpinner = findViewById(R.id.spinnerStartVertex)
        endVertexSpinner = findViewById(R.id.spinnerEndVertex)
        edgeWeightEditText = findViewById(R.id.editTextEdgeWeight)
        addButton = findViewById(R.id.buttonAddEdge)
        newGraphButton = findViewById(R.id.NewGraphButton)
        listViewEdges = findViewById(R.id.listViewEdges)
        spinnerStartDijkstra = findViewById(R.id.spinnerStartDijkstra)
        spinnerEndDijkstra = findViewById(R.id.spinnerEndDijkstra)
        addButton.isClickable = false
        addButton.setOnClickListener {
            addEdge()
        }
        newGraphButton.setOnClickListener {
            startNewGraphActivity()
        }
        verticesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val numVerticesText = s?.toString() ?: ""
                val numVertices = numVerticesText.toIntOrNull() ?: 0
                if (numVertices > 10) {
                    showToast("Количество вершин должно быть меньше или равно 10.")
                    verticesEditText.setText("10")
                    verticesEditText.setSelection(verticesEditText.text.length) // Move cursor to the end
                }
                setupSpinner(startVertexSpinner)
                setupSpinner(endVertexSpinner)
                setupSpinner(spinnerStartDijkstra)
                setupSpinner(spinnerEndDijkstra)
                addButton.isClickable = true
            }
        })
        // Set up the ListView adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, addedEdges)
        listViewEdges.adapter = adapter

        // Set up item click listener to delete an edge when clicked
        listViewEdges.setOnItemClickListener {_, _, position, _ ->
            deleteEdge(position)
        }
    }

    fun onClickGoNewGraph(view: View) {
        val intent = Intent(this, Activity_withXML::class.java)
        startActivity(intent)
    }

    private fun setupSpinner(spinner: Spinner) {
        val numVertices = verticesEditText.text.toString().toIntOrNull() ?: 0
        val vertexNumbers = (0 until numVertices).toList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vertexNumbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun addEdge() {
        // Check if the number of vertices is entered
        val numVerticesText = verticesEditText.text.toString()
        if (numVerticesText.isBlank()) {
            showToast("Введите количество вершин.")
            return
        }

        val numVertices = numVerticesText.toInt()
        if (numVertices <= 0 || numVertices > 10) {
            showToast("Количество вершин должно быть целым положительным числом, меньшим или равным 10.")
            return
        }

        val startVertex = startVertexSpinner.selectedItem?.toString() ?: ""
        val endVertex = endVertexSpinner.selectedItem?.toString() ?: ""
        val edgeWeight = edgeWeightEditText.text.toString()

        // Validate the input data
        if (startVertex == endVertex) {
            showToast("Начальная и конечная вершины не могут быть одинаковыми.")
            return
        }

        if (edgeWeight.isEmpty() || edgeWeight.toIntOrNull() ?: 0 <= 0) {
            showToast("Вес ребра должен быть положительным ненулевым числом.")
            return
        }

        // Check if start vertex is less than end vertex
        val startVertexInt = startVertex.toInt()
        val endVertexInt = endVertex.toInt()
        if (startVertexInt >= numVertices || endVertexInt >= numVertices || startVertexInt >= endVertexInt) {
            showToast("Недопустимые вершины. Начальная вершина должна быть меньше конечной.")
            return
        }

        // Check for duplicate entry with the same start and end vertices
        val existingEdge = addedEdges.find {
            (it["startVertex"] == startVertex && it["endVertex"] == endVertex) ||
                    (it["startVertex"] == endVertex && it["endVertex"] == startVertex)
        }

        if (existingEdge != null) {
            showToast("Дублирующая запись. Ребро с такими же начальной и конечной вершинами уже существует.")
            return
        }

        // Add the edge to the list as a hashmap
        val newEdge = HashMap<String, Any>()
        newEdge["startVertex"] = startVertex
        newEdge["endVertex"] = endVertex
        newEdge["edgeWeight"] = edgeWeight.toInt()
        addedEdges.add(newEdge)

        // Update the ListView adapter
        (listViewEdges.adapter as ArrayAdapter<*>).notifyDataSetChanged()

        // Clear input fields
        edgeWeightEditText.text.clear()
    }


    private fun deleteEdge(position: Int) {
        addedEdges.removeAt(position)

        // Update the ListView adapter
        (listViewEdges.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startNewGraphActivity() {
        // Start NewGraphActivity
        val intent = Intent(this, Activity_withXML::class.java)

        val numVertices = verticesEditText.text.toString()
        val addedEdgesData = Gson().toJson(addedEdges) // Convert addedEdges list to JSON string


        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("numVertices", numVertices.toInt())
            put("addedEdges", addedEdgesData)
            put("startDijkstra", spinnerStartDijkstra.selectedItem.toString())
            put("endDijkstra", spinnerEndDijkstra.selectedItem.toString())
        }

        val newRowId = db.insert("graph_data", null, values)

        intent.putExtra("numVertices", verticesEditText.text.toString())
        intent.putExtra("addedEdges", ArrayList(addedEdges))
        intent.putExtra("startDijkstra", spinnerStartDijkstra.selectedItem.toString())
        intent.putExtra("endDijkstra", spinnerEndDijkstra.selectedItem.toString())

        // Start ShowGraphActivity
        startActivity(intent)
    }
}