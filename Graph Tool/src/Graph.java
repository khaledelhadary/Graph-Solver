import java.util.Vector;
import java.awt.*;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Graph {
	private UndirectedSparseGraph <Integer, Integer> graph ;
	private DirectedSparseGraph <Integer, Integer> directedGraph;
	private Vector<String> Costs_of_intialGraph , edgeList;
	private int edges = 0;
	private boolean isDirected;
		
	public Graph(int nodes , boolean isDirected) {
		this.isDirected = isDirected;
		if ( isDirected == true ) {
			directedGraph = new DirectedSparseGraph<Integer, Integer>();
		}
		else {
			graph = new UndirectedSparseGraph<Integer, Integer>();
		}
		
		Costs_of_intialGraph 	= new Vector<String>();
		edgeList = new Vector<String>();
	}

	public void addEdge(int a, int b) {
		if ( isDirected == true ) {
			if (!directedGraph.containsVertex(a)) {
				directedGraph.addVertex(a); edgeList.add(String.valueOf(a));
			}

			if (!directedGraph.containsVertex(b)) {
				directedGraph.addVertex(b); edgeList.add(String.valueOf(b));
			}
			directedGraph.addEdge(edges++, a, b);
		}
		else {
			if (!graph.containsVertex(a)) {
				graph.addVertex(a); edgeList.add(String.valueOf(a));
			}

			if (!graph.containsVertex(b)) {
				graph.addVertex(b); edgeList.add(String.valueOf(b));
			}
			graph.addEdge(edges++, a, b);
		}
		
	}
	
	public void addEdge( int a , int b , int cost ) {
		if ( isDirected == true ) {
			if ( !directedGraph.containsVertex(a) )
				directedGraph.addVertex(a);
			if ( !directedGraph.containsVertex(b) )
				directedGraph.addVertex(b);
			directedGraph.addEdge(edges++, a, b);
			Costs_of_intialGraph.add( String.valueOf(cost) );
		}
		else {
			if ( !graph.containsVertex(a) )
				graph.addVertex(a);
			if ( !graph.containsVertex(b) )
				graph.addVertex(b);
			graph.addEdge(edges++, a, b);
			Costs_of_intialGraph.add( String.valueOf(cost) );
		}
		
	}
	
	public VisualizationImageServer<Integer, Integer> getVisualization() {
		VisualizationImageServer<Integer, Integer> vs;
		
		if ( isDirected == true ) {
			vs = new VisualizationImageServer<Integer, Integer>( new CircleLayout<Integer, Integer>(directedGraph), new Dimension(635,420));
		}
		else {
			vs = new VisualizationImageServer<Integer, Integer>( new CircleLayout<Integer, Integer>(graph), new Dimension(635,420));
		}
		
				
		
		Transformer<Integer, Font> edgeFont = new Transformer<Integer, Font>() {
			@Override
			public Font transform(Integer arg0) {
				return new Font("Arial", Font.PLAIN, 20);
			}
		};
		
		Transformer<Integer, Font> vertexFont = new Transformer<Integer, Font>() {
			@Override
			public Font transform(Integer arg0) {
				return new Font("Arial", Font.BOLD, 20);
			}
		};
       
		vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vs.getRenderContext().setEdgeFontTransformer(edgeFont);
		vs.getRenderContext().setVertexFontTransformer(vertexFont);
		vs.getRenderContext().setEdgeLabelTransformer(new org.apache.commons.collections15.Transformer<Integer, String>() {
			
			public String transform(Integer e) {
				if ( Costs_of_intialGraph.isEmpty() ) {
					return "";
				}
				return Costs_of_intialGraph.get(e);
			}
		});
		vs.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		return vs;
		
	}
	
	public pair<Integer,Vector<edge>> maxflow(Vector<edge> edges , int src , int dist , int nodeCount){
		int vertex = nodeCount;
		maxflow object = new maxflow(vertex , edges , isDirected);
		
		pair<Integer, Vector<edge>> maxflow = object.Maxfow(src,dist);
		if ( maxflow == null )
			return null;
		else
			return maxflow;
	}
	
	
	public pair<Integer,Vector<edge>> dijkstra(Vector<edge> edges , int src , int dist , int vertexCount){
		int vertex = vertexCount;
		
		dijkstra object = new dijkstra(vertex , edges , isDirected);
		
		pair<Integer, Vector<edge>> dijkstra = object.Dijkstra(src,dist);
		if ( dijkstra == null )
			return null;
		else 
			return dijkstra;
	}

	public void addVertex(int i) {
		if (isDirected) {
			directedGraph.addVertex(i);
		} else {
			graph.addVertex(i);
		}
		
	}
	
}
