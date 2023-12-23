package com.example.learningapp


import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class FileManager(private val context: Context) {

    // Define an edge data class (assuming you have one)
    data class Edge(val startVertex: Int, val endVertex: Int, val weight: Int)

    // Create a data class to represent the graph
    data class GraphData(val numVertices: Int, val edges: MutableList<Edge> = mutableListOf())

    // Save graph data to a file
    fun saveGraphDataToFile(graphData: GraphData, filename: String) {
        val gson = Gson()
        val json = gson.toJson(graphData)

        try {
            FileOutputStream(context.getFileStreamPath(filename)).use { fileOutputStream ->
                fileOutputStream.write(json.toByteArray())
            }
        } catch (e: IOException) {
            // Handle file I/O error
            e.printStackTrace()
        }
    }


    fun loadGraphDataFromFile(filename: String): GraphData? {
        try {
            val fileInputStream = context.openFileInput(filename)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val gson = Gson()
            val graphData = gson.fromJson(inputStreamReader, GraphData::class.java)

            fileInputStream.close()
            return graphData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun convertToEdgeList(hashMapList: MutableList<HashMap<String, Any>>): MutableList<FileManager.Edge> {
        val edgeList = mutableListOf<FileManager.Edge>()

        for (hashMap in hashMapList) {
            val startVertex = hashMap["startVertex"].toString().toInt()
            val endVertex = hashMap["endVertex"].toString().toInt()
            val edgeWeight = hashMap["edgeWeight"].toString().toInt()

            val edge = FileManager.Edge(startVertex, endVertex, edgeWeight)
            edgeList.add(edge)
        }

        return edgeList
    }

}
