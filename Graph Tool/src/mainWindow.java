import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Paint;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import javax.swing.JTable;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;

public class mainWindow {
	private JFrame frmGraphTool;
	private JTextField nodes;
	private JTextField edges;
	private JTextField from;
	private JTextField to;
	private JTextField cost;
	private JTable table;
	
	private JButton createbtn;
	private JButton addEdgebtn;
	private JButton clearbtn;
	private JButton btnOriginalGraph;

	private Integer nodesCnt , edgesCnt , edgesCounter;
	private Vector<edge> edgeList;
	private JButton btnDijkstra;
	private JButton btnMaxFlow;
	private JCheckBox isDirected;
	private JLabel lblSourceNode;
	private JTextField source;
	private JTextField destination;
	private JLabel lblDestination;
	private JLabel result_lbl;
	
	private JButton btnEnd , btnNext , controlbtn ;
	private int src,dist;
	private JPanel gwin;
	private JScrollPane scrollPane;
	
	private Vector<edge> currentG;
	private Integer counter;
	
	private UndirectedSparseGraph<Integer, Integer> undirectedGraph = new UndirectedSparseGraph<Integer, Integer>();
	private DirectedSparseGraph<Integer, Integer> directedGraph = new DirectedSparseGraph<Integer, Integer>();
	private VisualizationImageServer<Integer, Integer> vs;
	private Vector<String> costs;
	private boolean isDijkstra;
	private Integer result_value;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWindow window = new mainWindow();
					window.frmGraphTool.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainWindow() {
		initialize();
	}
	
	private void showGraph(boolean enablebtn) {
		scrollPane.setVisible(false);
		gwin.setVisible(true);
		controlbtn.setEnabled(true);
		
		if ( enablebtn == true ) {	
			btnNext.setEnabled(true);
			btnEnd.setEnabled(true);
		}
	}
	private void getGraph() {
		edgeList = new Vector<edge>();
		
		int rows = table.getModel().getRowCount();
		int cols = table.getModel().getColumnCount();
		
		for ( int i = 0; i < rows; i++ ) {
			Integer from 	= Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
			Integer to 		= Integer.parseInt(table.getModel().getValueAt(i, 1).toString());
			Integer cost;
			
			if ( cols == 3 ) {
				cost = Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
			}
			else {
				cost = 0;
			}
			
			edgeList.add( new edge( from , to , cost ) );
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private Vector<edge> mergeEdges(){
		Vector<edge> ret = new Vector<edge>();
		
		int adj[][] = new int[nodesCnt + 1][nodesCnt + 1];
		for ( int i = 0; i <= nodesCnt; ++i ){
			for ( int j = 0; j <= nodesCnt; ++j ) {
				adj[i][j] = -1;
			}
		}
		
		for ( int i = 0; i < edgeList.size(); ++i ) {
			int u = edgeList.elementAt(i).from;
			int v = edgeList.elementAt(i).to;
			
			if ( adj[u][v] == -1 ) {
				adj[u][v]  = edgeList.elementAt(i).cost;
			}
			else {
				adj[u][v] += edgeList.elementAt(i).cost;	
			}
			
			if ( isDirected.isSelected() == false ) {
				if ( adj[v][u] == -1 ) {
					adj[v][u]  = edgeList.elementAt(i).cost;
				}
				else {
					adj[v][u] += edgeList.elementAt(i).cost;	
				}
			}
		}
		
		for ( int i = 1; i <= nodesCnt; ++i ) {
			for ( int j = 1; j <= nodesCnt; ++j ) {
				if ( adj[i][j] != -1 ) {
					ret.add( new edge(i , j , adj[i][j]) );
				}
			}
		}
		
		
		return ret;
	}
	private void initialize() {
		frmGraphTool = new JFrame();
		frmGraphTool.setResizable(false);
		frmGraphTool.setTitle("Graph Tool");
		frmGraphTool.setBounds(100, 100, 932, 530);
		frmGraphTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGraphTool.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 635, 420);
		frmGraphTool.getContentPane().add(scrollPane);
		
		btnOriginalGraph = new JButton("Original Graph");
		btnOriginalGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					gwin.removeAll();
					result_lbl.setText("Original graph");
					getGraph();
					Graph g = new Graph(nodesCnt , isDirected.isSelected());
					Vector<edge> newEdgeList = mergeEdges();
					for (int i=1 ; i <= nodesCnt ; i++)
						g.addVertex(i);
					for (int i=0 ; i < newEdgeList.size(); i++) {
						g.addEdge(newEdgeList.elementAt(i).from, newEdgeList.elementAt(i).to, newEdgeList.elementAt(i).cost);
					}
					VisualizationImageServer<Integer, Integer> draw = g.getVisualization();
					if (draw != null) {
						showGraph(false);
						gwin.add(draw);
					} else {
						JOptionPane.showMessageDialog(new JFrame(), "Parse Data Correctly \nEx:\n1 2 \n2 3 \n3 1", "Error", JOptionPane.ERROR_MESSAGE);
					}
			}
		});
		btnOriginalGraph.setBounds(666, 370, 246, 30);
		frmGraphTool.getContentPane().add(btnOriginalGraph);
		
		JLabel lblNumberOfEdges = new JLabel("Number of vertices");
		lblNumberOfEdges.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNumberOfEdges.setBounds(666, 13, 134, 14);
		frmGraphTool.getContentPane().add(lblNumberOfEdges);
		
		nodes = new JTextField();
		nodes.setBounds(801, 10, 111, 20);
		frmGraphTool.getContentPane().add(nodes);
		nodes.setColumns(10);
		
		JLabel label = new JLabel("Number of edges");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setBounds(666, 41, 134, 18);
		frmGraphTool.getContentPane().add(label);
		
		edges = new JTextField();
		edges.setColumns(10);
		edges.setBounds(801, 38, 111, 20);
		frmGraphTool.getContentPane().add(edges);
		
		createbtn = new JButton("Create Graph");
		createbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					edgesCounter = 0;
					nodesCnt = Integer.parseInt(nodes.getText());
					edgesCnt = Integer.parseInt(edges.getText());
										
					try {
						src = Integer.parseInt( source.getText() );
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(new JFrame(), "Source should be integer", "Invalid", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if ( src < 1 || src > nodesCnt ) {
						JOptionPane.showMessageDialog(new JFrame(), "Source node not exists in graph. Should be 1-Based and in range [1 -> N]", "Invalid", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					try {
						dist = Integer.parseInt( destination.getText() );
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(new JFrame(), "Destination should be integer", "Invalid", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if ( dist < 1 || dist > nodesCnt) {
						JOptionPane.showMessageDialog(new JFrame(), "Destination node not exists in graph. Should be 1-Based and in range [1 -> N]", "Invalid", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					nodes.setEnabled(false);
					edges.setEnabled(false);
					createbtn.setEnabled(false);
					isDirected.setEnabled(false);
					source.setEnabled(false);
					destination.setEnabled(false);
					
					addEdgebtn.setEnabled(true);
					clearbtn.setEnabled(true);
					from.setEnabled(true);
					to.setEnabled(true);
					cost.setEnabled(true);	
					
					
					table = new JTable();
					table.setModel(new DefaultTableModel(
						new Object[][] {
						},
						new String[] {
							"Source", "Destination", "Cost"
						}
					));
					
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment( JLabel.CENTER );
					table.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
					table.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
					table.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
					scrollPane.setViewportView(table);

				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(new JFrame(), "Number of nodes & number of edges must be numbers only", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		createbtn.setBounds(801, 134, 111, 30);
		frmGraphTool.getContentPane().add(createbtn);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(655, 10, 1, 480);
		frmGraphTool.getContentPane().add(separator_1);
		
		JLabel lblSource = new JLabel("Source Vertex");
		lblSource.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSource.setBounds(666, 188, 134, 14);
		frmGraphTool.getContentPane().add(lblSource);
		
		from = new JTextField();
		from.setColumns(10);
		from.setBounds(801, 185, 111, 20);
		frmGraphTool.getContentPane().add(from);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(655, 175, 257, 2);
		frmGraphTool.getContentPane().add(separator_2);
		
		JLabel lblDestinationVertex = new JLabel("Destination Vertex");
		lblDestinationVertex.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDestinationVertex.setBounds(666, 216, 134, 14);
		frmGraphTool.getContentPane().add(lblDestinationVertex);
		
		to = new JTextField();
		to.setColumns(10);
		to.setBounds(801, 213, 111, 20);
		frmGraphTool.getContentPane().add(to);
		
		JLabel lblEdgeCost = new JLabel("Edge Cost");
		lblEdgeCost.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEdgeCost.setBounds(666, 247, 134, 17);
		frmGraphTool.getContentPane().add(lblEdgeCost);
		
		cost = new JTextField();
		cost.setColumns(10);
		cost.setBounds(801, 244, 111, 20);
		frmGraphTool.getContentPane().add(cost);
		
		addEdgebtn = new JButton("Add Edge");
		addEdgebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Integer Source 		= Integer.parseInt(from.getText());
					Integer Destination = Integer.parseInt(to.getText());
					
					if ( Source <= 0 || Source > nodesCnt ) {
						JOptionPane.showMessageDialog(new JFrame(), "Source must be in range [1 : Number of nodes]", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if ( Destination <= 0 || Destination > nodesCnt ) {
						JOptionPane.showMessageDialog(new JFrame(), "Destination must be in range [1 : Number of nodes]", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					edgesCounter++;
					if ( edgesCounter + 1 > edgesCnt ) {
						addEdgebtn.setEnabled(false);
						btnDijkstra.setEnabled(true);
						btnOriginalGraph.setEnabled(true);
						btnMaxFlow.setEnabled(true);
					}
					
					String CostStr = "0";
					if ( !(cost.getText() == null || cost.getText().isEmpty()) ) {
						CostStr = cost.getText();
					}
					

					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.addRow(new String[] {from.getText() , to.getText() , CostStr});
					
					from.setText(""); to.setText(""); cost.setText("");
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(new JFrame(), "Source, Destination & cost must be numbers only", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addEdgebtn.setBounds(666, 275, 246, 30);
		frmGraphTool.getContentPane().add(addEdgebtn);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(655, 357, 257, 2);
		frmGraphTool.getContentPane().add(separator_3);
		
		clearbtn = new JButton("Clear This Graph");
		clearbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				result_lbl.setText("");
				from.setEnabled(false);
				to.setEnabled(false);
				cost.setEnabled(false);
				addEdgebtn.setEnabled(false);
				clearbtn.setEnabled(false);
				isDirected.setEnabled(true);
				btnNext.setEnabled(false);
				btnEnd.setEnabled(false);
				controlbtn.setEnabled(false);
				
				btnDijkstra.setEnabled(false);
				btnMaxFlow.setEnabled(false);
				btnOriginalGraph.setEnabled(false);
				
				nodes.setEnabled(true);
				edges.setEnabled(true);
				createbtn.setEnabled(true);
				source.setEnabled(true);
				destination.setEnabled(true);
				source.setText("");
				destination.setText("");
				
				gwin.setVisible(false);
				scrollPane.setVisible(true);
				
				directedGraph = new DirectedSparseGraph<Integer, Integer>();
				undirectedGraph = new UndirectedSparseGraph<Integer, Integer>();
				
				gwin.removeAll();
				counter = -1;
			
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
					}
				));	

				
				from.setText("");
				to.setText("");
				cost.setText("");
				nodes.setText("");
				edges.setText("");
			}
		});
		clearbtn.setBounds(666, 316, 246, 30);
		frmGraphTool.getContentPane().add(clearbtn);
		
		
		
		this.from.setEnabled(false);
		this.to.setEnabled(false);
		this.cost.setEnabled(false);
		this.addEdgebtn.setEnabled(false);
		this.clearbtn.setEnabled(false);
		
		btnDijkstra = new JButton("Dijkstra");
		btnDijkstra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				result_lbl.setText("");
				getGraph();
				Graph g = new Graph(nodesCnt , isDirected.isSelected());
				for ( int i = 0; i < edgeList.size(); i++ ) {
					g.addEdge(edgeList.elementAt(i).from, edgeList.elementAt(i).to, edgeList.elementAt(i).cost);
				}	
				
			
				pair<Integer,Vector<edge>> draw = g.dijkstra(edgeList , src , dist , nodesCnt);
				if (draw.value != null) {
					showGraph(true);
					btnOriginalGraph.doClick();
					counter = -1;
							
					currentG = draw.value;
					costs = new Vector<String>();
					
					isDijkstra = true;
					result_value = draw.key;
				
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "There is no shortest path in this graph", "Invalid", JOptionPane.DEFAULT_OPTION);
				}
			}
		});
		btnDijkstra.setBounds(666, 411, 246, 30);
		frmGraphTool.getContentPane().add(btnDijkstra);
		
		btnMaxFlow = new JButton("Max Flow");
		btnMaxFlow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result_lbl.setText("");
				getGraph();
				Graph g = new Graph(nodesCnt , isDirected.isSelected());
				for ( int i = 0; i < edgeList.size(); i++ ) {
					g.addEdge(edgeList.elementAt(i).from, edgeList.elementAt(i).to, edgeList.elementAt(i).cost);
				}	
				
			
				pair<Integer,Vector<edge>> draw = g.maxflow(edgeList , src , dist , nodesCnt);
				if (draw.value != null) {
					showGraph(true);
					btnOriginalGraph.doClick();
					counter = -1;
							
					currentG = draw.value;
					costs = new Vector<String>();
					
					isDijkstra = false;
					result_value = draw.key;
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "There is no path from source to destination in this graph", "Invalid", JOptionPane.DEFAULT_OPTION);
				}
			}
		});
		btnMaxFlow.setBounds(666, 452, 246, 30);
		frmGraphTool.getContentPane().add(btnMaxFlow);
		
		isDirected = new JCheckBox("  Directed");
		isDirected.setBounds(666, 138, 97, 23);
		frmGraphTool.getContentPane().add(isDirected);
		
		source = new JTextField();
		source.setHorizontalAlignment(SwingConstants.LEFT);
		source.setColumns(10);
		source.setBounds(801, 70, 111, 20);
		frmGraphTool.getContentPane().add(source);
		
		destination = new JTextField();
		destination.setHorizontalAlignment(SwingConstants.LEFT);
		destination.setColumns(10);
		destination.setBounds(801, 99, 111, 20);
		frmGraphTool.getContentPane().add(destination);
		
		lblDestination = new JLabel("Destination");
		lblDestination.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDestination.setBounds(666, 99, 134, 20);
		frmGraphTool.getContentPane().add(lblDestination);
		
		lblSourceNode = new JLabel("Source");
		lblSourceNode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSourceNode.setBounds(666, 70, 134, 18);
		frmGraphTool.getContentPane().add(lblSourceNode);
		
		controlbtn = new JButton("Edges table");
		controlbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ( controlbtn.getText().compareTo("Edges table") == 0 ) {
					gwin.setVisible(false);
					scrollPane.setVisible(true);
					controlbtn.setText("Show Graph");
				}
				else {
					scrollPane.setVisible(false);
					gwin.setVisible(true);
					controlbtn.setText("Edges table");
				}
			}
		});
		controlbtn.setBounds(10, 442, 111, 37);
		frmGraphTool.getContentPane().add(controlbtn);
		
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ( counter == -1 ) {	
	
					if ( isDirected.isSelected() == true ) {
						directedGraph = new DirectedSparseGraph<Integer, Integer>();
					}
					else {
						undirectedGraph = new UndirectedSparseGraph<Integer, Integer>();
					}
					
					if ( isDirected.isSelected() == true ) {
						for ( int i = 1; i <= nodesCnt; ++i ) {
							directedGraph.addVertex(i);
						}
						vs = new VisualizationImageServer<Integer, Integer>( new CircleLayout<Integer, Integer>(directedGraph), new Dimension(635,420));
					}
					else {
						for ( int i = 1; i <= nodesCnt; ++i ) {
							undirectedGraph.addVertex(i);
						}
						vs = new VisualizationImageServer<Integer, Integer>( new CircleLayout<Integer, Integer>(undirectedGraph), new Dimension(635,420));
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
					int colors [] = new int [nodesCnt+1];
					for (int i=0 ; i<colors.length ; i++)
						colors[i] = 0;
					colors[src] = 1;
					colors[dist] = 2;
					Transformer<Integer, Paint> ColorTransformer = new Transformer<Integer,Paint>() {
                        public Paint transform(Integer i) {
                        	switch (colors[i]){
								case 1 : return Color.GREEN;
								case 2 : return Color.BLUE;
								default : return Color.RED;
							}
                        }
                    };
                    vs.getRenderContext().setVertexFillPaintTransformer(ColorTransformer);
					result_lbl.setText("Green: Source     Blue: Destination");
                    vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
					vs.getRenderContext().setEdgeFontTransformer(edgeFont);
					vs.getRenderContext().setVertexFontTransformer(vertexFont);
					vs.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
					vs.getRenderContext().setEdgeLabelTransformer(new org.apache.commons.collections15.Transformer<Integer, String>() {
			
						public String transform(Integer e) {
							if ( costs.isEmpty() ) {
								return "";
							}
							return costs.get(e);
						}
					});
					
					gwin.removeAll();
					
					gwin.add(vs);
					counter++;
					
					return;
				}
				
				
				if ( isDirected.isSelected() ) {
					if ( counter < currentG.size() ) {
						directedGraph.addEdge( counter , currentG.elementAt(counter).from , currentG.elementAt(counter).to );
						costs.add( String.valueOf( currentG.elementAt(counter).cost) );
						counter++;
					}
				}
				else {
					if ( counter < currentG.size() ) {
						undirectedGraph.addEdge( counter , currentG.elementAt(counter).from , currentG.elementAt(counter).to );
						costs.add( String.valueOf( currentG.elementAt(counter).cost) );
						counter++;
					}
				}
				
				vs.updateUI();	
				if ( counter >= currentG.size() ) {
					btnNext.setEnabled(false);
					btnEnd.setEnabled(false);
					
					if ( isDijkstra ) {
						result_lbl.setText("Shortest path value: " + String.valueOf(result_value) );
						JOptionPane.showMessageDialog(new JFrame(), "Shortest path value: " + String.valueOf(result_value) , "Shortest path value", JOptionPane.DEFAULT_OPTION);
					}
					else {
						result_lbl.setText("Max flow value: "  + String.valueOf(result_value) );
						JOptionPane.showMessageDialog(new JFrame(), "Max flow value: " + String.valueOf(result_value) , "max flow value", JOptionPane.DEFAULT_OPTION);
					}
				}
							
			}
		});
		btnNext.setBounds(131, 442, 111, 37);
		frmGraphTool.getContentPane().add(btnNext);
		
		btnEnd = new JButton("End");
		btnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while ( counter < currentG.size() ) {
					btnNext.doClick();
				}
			}
		});
		btnEnd.setBounds(252, 442, 111, 37);
		frmGraphTool.getContentPane().add(btnEnd);
		
		btnNext.setEnabled(false);
		btnEnd.setEnabled(false);
		controlbtn.setEnabled(false);
		btnOriginalGraph.setEnabled(false);
		btnDijkstra.setEnabled(false);
		btnMaxFlow.setEnabled(false);
		
		gwin = new JPanel();
		gwin.setBounds(10, 11, 635, 420);
		frmGraphTool.getContentPane().add(gwin);
		
		result_lbl = new JLabel("");
		result_lbl.setBounds(373, 442, 272, 40);
		frmGraphTool.getContentPane().add(result_lbl);
		
		gwin.setVisible(false);
	}
}
