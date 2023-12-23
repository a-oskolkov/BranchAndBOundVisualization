package com.example.learningapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import java.io.File

class LoadGraphActivity : AppCompatActivity() {
    private lateinit var listViewSavedGraphs: ListView
    private lateinit var deleteFileNameEditText: EditText
    private lateinit var buttonDelete: Button
    private lateinit var savedGraphs: List<File>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_graph)

        deleteFileNameEditText = findViewById(R.id.editTextDeleteFileName)
        listViewSavedGraphs = findViewById(R.id.listViewSavedGraphs)
        buttonDelete = findViewById(R.id.buttonDelete)

        savedGraphs = getSavedGraphFiles()

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            savedGraphs.map { it.name } // Display file names
        )

        listViewSavedGraphs.adapter = adapter

        listViewSavedGraphs.setOnItemClickListener { _,_,position,_ ->
            val selectedFile = savedGraphs[position]
            promptForStartAndEndVertices(selectedFile)
        }

        buttonDelete.setOnClickListener {
            val fileNameToDelete = deleteFileNameEditText.text.toString()
            deleteSelectedFile(fileNameToDelete)
        }
    }

    private fun deleteSelectedFile(fileName: String) {
        // Check if the file exists
        val fileToDelete = savedGraphs.find { it.name == fileName }
        if (fileToDelete != null) {
            // Delete the file
            if (fileToDelete.delete()) {
                // File deleted successfully
                Toast.makeText(this, "Файл удален: $fileName", Toast.LENGTH_SHORT).show()
                refreshFileList() // Refresh the list of saved graphs
            } else {
                // Failed to delete the file
                Toast.makeText(this, "Не удалось удалить файл.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // File not found
            Toast.makeText(this, "Файл не найден: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshFileList() {
        // Refresh the list of saved graph files
        savedGraphs = getSavedGraphFiles()
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            savedGraphs.map { it.name }
        )
        listViewSavedGraphs.adapter = adapter
    }


    private fun getSavedGraphFiles(): List<File> {
        val filesDir = filesDir // Get the directory where your app's files are stored
        val allFiles = filesDir.listFiles()
        return allFiles?.filter { it.isFile && it.extension == "json" } ?: emptyList()
    }

    // Function to prompt for start and end vertices
    private fun promptForStartAndEndVertices(selectedFile: File) {
        try {
            val jsonContent = selectedFile.readText() // Read the JSON content from the selected file

            // Parse the JSON content to retrieve the number of vertices
            val jsonObject = JSONObject(jsonContent)
            val numVertices = jsonObject.getInt("numVertices")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Укажите начальную и конечную вершины алгоритма")

            val inputLayout = LinearLayout(this)
            inputLayout.orientation = LinearLayout.VERTICAL

            val startVertexSpinner = Spinner(this)
            val endVertexSpinner = Spinner(this)

            // Set up start and end vertex spinners based on the number of vertices
            val vertexNumbers = (0 until numVertices).toList()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vertexNumbers)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            startVertexSpinner.adapter = adapter
            endVertexSpinner.adapter = adapter

            inputLayout.addView(startVertexSpinner)
            inputLayout.addView(endVertexSpinner)

            builder.setView(inputLayout)

            builder.setPositiveButton("OK") { _, _ ->


                // Start ShowGraphActivity and pass the selected file name, start vertex, and end vertex
                val intent = Intent(this,Activity_withXML::class.java)
                intent.putExtra("fileName", selectedFile.name)
                intent.putExtra("startDijkstra", startVertexSpinner.selectedItem.toString())
                intent.putExtra("endDijkstra", endVertexSpinner.selectedItem.toString())
                startActivity(intent)

            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        } catch (e: Exception) {
            // Handle any exceptions that may occur while reading or parsing the file
            e.printStackTrace()
            Toast.makeText(this,"Ошибка при чтении файла.",Toast.LENGTH_SHORT).show()
        }
    }

}