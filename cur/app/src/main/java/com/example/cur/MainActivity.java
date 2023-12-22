package com.example.cur;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.os.Bundle;

import com.example.cur.databinding.ActivityMainBinding;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding bindingClass = bindingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Graph graph = new DefaultGraph("tutorial");
        graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B").setAttribute("length",14);
        graph.addEdge("BC", "B", "C").setAttribute("length",14);
        graph.addEdge("CA", "C", "A").setAttribute("length",14);
        graph.nodes().forEach(n -> n.setAttribute("label", n.getId()));
        graph.edges().forEach(e -> e.setAttribute("label", "" + (int) e.getNumber("length")));
    }
}