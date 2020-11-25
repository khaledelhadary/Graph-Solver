import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;

public class maxflow {
	private int vertex , adj[][] , path[] , distance[] , newAdj[][];
	private boolean visited[];
	
	private boolean isDirected;
	private pair<Integer, Vector<edge>> maxflow_result;
	
	public maxflow(int vertexCount , Vector<edge> edges , boolean isDirected){
		this.isDirected = isDirected;
		
		vertex = vertexCount;
		
		path = new int[vertex + 1];
		distance = new int[vertex + 1];
		visited = new boolean[vertex + 1];
		adj = new int[vertex + 1][vertex + 1];
		newAdj = new int[vertex + 1][vertex + 1];
		for ( int row = 0; row <= vertex; ++row ) {
			visited[row] = false;
			for ( int col = 0; col <= vertex; ++col ) {
				adj[row][col] = -1;
				newAdj[row][col] = 0;
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
		else{
			adj[e.from][e.to] += e.cost;
		}
		if ( isDirected == false ) {
			if ( adj[e.to][e.from] == -1 ) {
				adj[e.to][e.from] = e.cost;
			}
			else{
				adj[e.to][e.from] += e.cost;
			}
		}
	}
	

	private boolean minmax_path(int src , int dist) {
		for ( int i = 0; i <= vertex; ++i ) distance[i] = 0;
		path[src] = -1; distance[src] = Integer.MAX_VALUE;
		
		PriorityQueue<edge> pq = new PriorityQueue<edge>(new maxflowEdgeComparator());
		pq.add(new edge(-1, src, Integer.MAX_VALUE));
		
		while ( pq.isEmpty() == false ) {
			edge e = pq.poll();
			for ( int v = 1; v <= vertex; ++v ) {
				int mn = Math.min(adj[e.to][v], e.cost);
				if ( mn > distance[v] ) {
					distance[v] = mn;
					pq.add(new edge(e.to, v, distance[v]));
					path[v] = e.to;
				}
			}
		}
		
		distance[src] = 0;
		return (distance[dist] > 0);
	}
	
	public void getEdges( int src , int dist ) {
		for ( int i = 0; i <= vertex; ++i )
			for ( int j = 0; j <= vertex; ++j )
				adj[i][j] = newAdj[i][j];
		
		Vector<edge> edges = new Vector<edge>();
		while ( minmax_path(src , dist) == true ) {
			Vector<Integer> maxflow_path = new Vector<Integer>();
			for ( int u = dist; u != -1; u = path[u] ) {
				maxflow_path.add(u);
			}
			Collections.reverse(maxflow_path);
			for ( int idx = 1; idx < maxflow_path.size(); ++idx ) {
				int u = maxflow_path.elementAt(idx - 1);
				int v = maxflow_path.elementAt(  idx  );
				edges.add(new edge(u, v));
	
				adj[u][v] -= distance[dist];
				if ( adj[u][v] < 0 ) adj[u][v] = 0;
				adj[v][u] -= distance[dist];
				if ( adj[v][u] < 0 ) adj[v][u] = 0;
			}
		}
		
		for ( int i = 0; i < edges.size(); ++i ) {
			boolean same = false;
			for ( int j = i - 1; j >= 0; --j ) {
				if ( edges.elementAt(i).from == edges.elementAt(j).from && edges.elementAt(i).to == edges.elementAt(j).to ) {
					same = true; break;
				}
			}
			if ( same == false ) {
				int u = edges.elementAt(i).from;
				int v = edges.elementAt(i).to;
				maxflow_result.value.add(new edge(u , v, newAdj[u][v]));
			}
		}
		
	}
	
	public pair<Integer, Vector<edge>> Maxfow(int src , int dist){
		int maxflow = 0;
		while ( minmax_path(src , dist) == true ) {
			maxflow += distance[dist];
			
			Vector<Integer> maxflow_path = new Vector<Integer>();
			for ( int u = dist; u != -1; u = path[u] ) {
				maxflow_path.add(u);
			}
			Collections.reverse(maxflow_path);
			
			for ( int idx = 1; idx < maxflow_path.size(); ++idx ) {
				int u = maxflow_path.elementAt(idx - 1);
				int v = maxflow_path.elementAt(  idx  );
				
				newAdj[u][v] += distance[dist];
				if ( isDirected == false ) {
					newAdj[v][u] += distance[dist];
				}
				
				adj[u][v] -= distance[dist];
				adj[v][u] += distance[dist];
			}
		}
		
		maxflow_result = new pair<Integer, Vector<edge>>();
		maxflow_result.key = maxflow;
		maxflow_result.value = new Vector<edge>();
		
		getEdges(src , dist);
		
		return maxflow_result;
	}
}
