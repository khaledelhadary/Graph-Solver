import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;


public class dijkstra {
	private static int vertex , adj[][] , path[] , distance[];

	public dijkstra(int vertexCount , Vector<edge> edges , boolean isDirected){
		vertex = vertexCount;
		
		path = new int[vertex + 1];
		distance = new int[vertex + 1];
		
		adj = new int[vertex + 1][vertex + 1];
		for ( int row = 0; row <= vertex; ++row ) {
			for ( int col = 0; col <= vertex; ++col ) {
				adj[row][col] = -1;
			}
		}
		
		for ( int itr = 0; itr < edges.size(); ++itr ) {
			addEdge(edges.elementAt(itr) , isDirected);
		}
	}
	
	private void addEdge(edge e , boolean isDirected) {
		if ( adj[e.from][e.to] == -1 ) {
			adj[e.from][e.to] = e.cost;
		}
		else if ( e.cost < adj[e.from][e.to] ) {
			adj[e.from][e.to] = e.cost;
		}
		if ( isDirected == false ) {
			if ( adj[e.to][e.from] == -1 ) {
				adj[e.to][e.from] = e.cost;
			}
			else if ( e.cost < adj[e.to][e.from] ){
				adj[e.to][e.from] = e.cost;
			}
		}
	}
	
	
	public pair<Integer, Vector<edge>> Dijkstra(int src , int dist){
		PriorityQueue<edge> pq = new PriorityQueue<edge>(new edgeComparator());
		pq.add(new edge(-1, src, 0));
		for ( int i = 0; i <= vertex; ++i ) {
			distance[i] = Integer.MAX_VALUE;
		}
		path[src] = -1; distance[src] = 0;
		
		while ( pq.isEmpty() == false ) {
			edge e = pq.poll();
			
			for ( int v = 1; v <= vertex; ++v ) {
				if ( adj[e.to][v] != -1 ) {
					if ( distance[v] > distance[e.to] + adj[e.to][v] ) {
						distance[v] = distance[e.to] + adj[e.to][v];
						pq.add(new edge(e.to, v, distance[v]));
						path[v] = e.to;
					}
				}
			}
		}
		
		pair<Integer, Vector<edge>> dijkstra_result = new pair<Integer, Vector<edge>>();
		dijkstra_result.key = distance[dist];
		dijkstra_result.value = new Vector<edge>();
		
		Vector<Integer> nodes = new Vector<Integer>();
		if ( distance[dist] != Integer.MAX_VALUE ) {
			for ( int u = dist; u != -1; u = path[u] ) {
				nodes.add(u);
			}
			Collections.reverse(nodes);
			for ( int itr = 1; itr < nodes.size(); ++itr ) {
				int u = nodes.elementAt(itr - 1);
				int v = nodes.elementAt(itr);
				dijkstra_result.value.add(new edge(u , v , adj[u][v]));
			}
		}
		else {
			dijkstra_result.value = null;
		}
		
		
		return dijkstra_result;
	}
}
