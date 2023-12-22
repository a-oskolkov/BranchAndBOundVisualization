package com.example.learningapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.learningapp.databinding.ActivityMainBinding
import org.graphstream.graph.Edge
import org.graphstream.graph.Graph
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.DefaultGraph

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)// содержит все элементы activity_main
        setContentView(binding.root)
    }
    fun onClickGoNewGraph(view: View){
        val intent = Intent(this,ActivityNewGraph::class.java)
        startActivity(intent)
    }

    fun onClickGoLoadGraph(view: View){
        val intent = Intent(this,LoadGraphActivity::class.java)
        startActivity(intent)
    }

}