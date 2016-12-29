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

    //for testing purposes only
    public static void main (String args[]){
        init2("/mapReduceOutput.txt");
    }

    public static void initialize(String filename){
        ArrayList<Integer> verts  = new ArrayList<Integer>();
        HashMap<String,Integer> myMap = mapKeyToValues(filename);
        HashMap<double, double> finalresultHolder = new HashMap<double, double>();
        Set<String> keys = myMap.keySet();
        for (String key: keys){
            String line  = key;
            String[] lineArray = line.split(":");
            String[] sourceDestArray =  lineArray[0].replace(']',' ').replace('[',' ').trim().split(",");
            String[] toExcludeArray =  lineArray[1].replace(']',' ').replace('[',' ').trim().split(",");
            int[] sourceDestArrayFinal = new int[sourceDestArray.length];
            int[] toExcludeArrayFinal = new int[toExcludeArray.length];
            for (int i = 0;i< sourceDestArray.length;i++)
            {
                sourceDestArrayFinal[i] = Integer.parseInt(sourceDestArray[i].trim());
            }
            for (int j = 0;j< toExcludeArray.length;j++)
            {
                toExcludeArrayFinal[j] = Integer.parseInt(toExcludeArray[j].trim());
            }

            //System.out.println(Arrays.toString(sourceDestArrayFinal));
            //System.out.println(Arrays.toString(toExcludeArrayFinal));
            double[] result = estimator.general.lengthDistribution(sourceDestArrayFinal,toExcludeArrayFinal);
            System.out.println(result+"RETURNED FROM TOBI");
            double num_of_paths_result = result[0];
            double avglength_result = result[1];
            double num_of_paths = num_of_paths_result * myMap.get(key);
            double avglength = avglength_tobi + toExcludeArray.length; 
            final_num_of_paths+=num_of_paths;
            final_avglength += (num_of_paths*avglength);
            finalresultHolder.put(num_of_paths,avglength);
        }
        // Set<String> keys2 = finalresultHolder.keySet();
        // double resultVal1;
        // double resultVal12;
        // for(double num: keys2){
        //     resultVal1+=num;
        //     resultVal2+= (num* finalresultHolder.get(num));
        // }
        try (
           FileWriter fw = new FileWriter("results.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            String line = "[" + final_num_of_paths+", "+ (final_avglength/final_num_of_paths) +"]";
            System.out.println("=================="+ line + "==============");
            out.println(line);
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Error writing to result.txt");    
        }
    }

        //TarjanSCC<Integer> checker = new TarjanSCC<Integer>(graph);
        //List<SCC<Integer>> sccs = checker.getSCCs();
        //double[] length;
        //ArrayList<double> length;
        // if (sccs.size() == graph.size())
        // {
        //     length = estimator.general.PathFinder.dagTraversal(graph, source, destination);
        // }
        // else{
        //     length= estimator.general.Algorithm2.lengthDistribution(list_of_graphs, graph, source, destination);
        // }
        // //boolean dag = recur.
        // double[] length = estimator.general.Algorithm2.lengthDistribution(list_of_graphs, graph, source, destination);
    }


    public static HashMap<String,Integer> mapKeyToValues(String file)
    {
        File subgraph = new File(file);
        HashMap<String,Integer> myMap = new HashMap<String,Integer>();
        try
        {
            Scanner in = new Scanner(subgraph);
            //Process to count # of pref/suff tha work for each
            while(in.hasNextLine())
            {
                String line = in.nextLine();
                if (myMap.containsKey(line)){
                    //System.out.println("Seen this before");
                    int curr_count = myMap.get(line);
                    myMap.put(line,curr_count+1);
                }
                else{
                myMap.put(line,1);
                }
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return myMap;
    }
}

