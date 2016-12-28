import java.util.*;
import java.awt.*;
import java.io.*;

import java.net.*; 

import estimator.util.*;
import estimator.general.*;
import recur.*;

public class subgraphGraphProcessor{
    public static int num_of_trials; ///not used at all rn
    public static int source;
    public static int destination;

    public static void init(){
        ArrayList<Integer> verts  = new ArrayList<Integer>();
        ArrayList<Graph<Integer>> list_of_graphs = new ArrayList<Graph<Integer>>();
        List<Point> points = constructGraph(verts);
        for (int i = 0; i<10000;i++){
            Graph<Integer> graph = new Graph<>();
            for (Point p : points) {
                graph.addEdge(p.x, p.y);
            }
            list_of_graphs.add(graph;)
        }
        Graph<Integer> graph = new Graph<>();
        for (Point p : points) {
            graph.addEdge(p.x, p.y);
        }
        TarjanSCC<Integer> checker = new TarjanSCC<Integer>(graph);
        List<SCC<Integer>> sccs = checker.getSCCs();
        double[] length;
        if (sccs.size() == graph.size())
        {
            length = estimator.general.PathFinder.dagTraversal(graph, source, destination);
        }
        else{
            length= estimator.general.Algorithm2.lengthDistribution(list_of_graphs, graph, source, destination);
        }
        boolean dag = recur.
        double[] length = estimator.general.Algorithm2.lengthDistribution(list_of_graphs, graph, source, destination);
        try (
           FileWriter fw = new FileWriter("results.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            String line = length[0]+ " "+ length[1];
            //System.out.println(line);
            out.println(line);
           // System.out.println("here");
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Error writing to result.txt");    
        }

    }

    public static Graph readSubGraph(ArrayList<Integer> verts)
    {
        File subgraph = new File("subgraph.txt");
        Graph<Integer> subgraphGraph = new Graph<Integer>();
        try
        {
            Scanner in = new Scanner(subgraph);
            
           /* Line 1 in subgraph should contain the number fo trails for Algoithm 2
            Line 2 should contain source and destnation 
            rest of graph in form of: 
            v1 verts adjacent to v1
           */
            String line1 = in.nextLine(); 
            num_of_trials = Integer.parseInt(line1); //nuber of trials for Algorithm2
            String line2 = in.nextLine(); //Source and destination
            String[] source_dest = line2.split(" ");
            source = Integer.parseInt(source_dest[0]);
            destination = Integer.parseInt(source_dest[1]);

            //Process to read graph
            while(in.hasNextLine())
            {
                String line  = in.nextLine();
                String[] lineArray = line.split(" ");
                verts.add(Integer.parseInt(lineArray[0]));
                for(int i = 1; i<lineArray.length; i++)
                {
                    subgraphGraph.addEdge(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i]));
                }
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        //mygraph = subgraphGraph;
        return subgraphGraph;
    }
    public static List<Point> constructGraph(ArrayList<Integer> verts)
    {
        Graph<Integer> originalGraph = new Graph<Integer>();
        try{
            System.out.println("In try block for Construct Graph");
            URL url = new URL("http://textuploader.com/ddxam/raw");
            URLConnection yc = url.openConnection();
            yc.setRequestProperty("User-Agent", "Mozilla/5.0");
            Scanner in = new Scanner(new InputStreamReader(yc.getInputStream()));
            List<Point> listPoints = new ArrayList<>();
            while(in.hasNextLine())
            {
                String line  = in.nextLine();
                String[] lineArray = line.split(" ");
                verts.add(Integer.parseInt(lineArray[0]));
                for(int i = 1; i<lineArray.length; i++)
                {
                    originalGraph.addEdge(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i]));
                    list.add(new Point(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i])));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list; //originalGraph;

    }
}

