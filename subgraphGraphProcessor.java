import java.util.*;
import java.io.*;

import estimator.util.*;
import estimator.general.*;

public class subgraphGraphProcessor{
    public static int num_of_trials; ///not used at all rn
    public static int source;
    public static int destination;

    public static void init(){
        ArrayList<Integer> verts  = new ArrayList<Integer>();
        ArrayList<Graph<Integer>> list_of_graphs = new ArrayList<Graph<Integer>>();
        Graph<Integer> subgraph = readSubGraph(verts);
        for (int i = 0; i<10000;i++){
            list_of_graphs.add(subgraph);
        }
        double[] length= estimator.general.Algorithm2.lengthDistribution(list_of_graphs, subgraph, source, destination);
        try (
           FileWriter fw = new FileWriter("results.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            String line = Arrays.toString(length);
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
}

