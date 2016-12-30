 /** Author: Mohammed Ibrahim
About: Given two integers, m , n (m=length of prefix, n= length of suffix) and a list of vertices in a graph
		Outpue a list of start and end vertices and a set of vertices (of size m+n-2) to exclude when finding path

**/

/*  Todo: for every prefix/suffix possible, i.e(see if paths are actually existent and then count 
    how many work). i.s [1,3] [2,4,5,6]. [2,4,1] actually existent [3,5,6] existent? if yes, then count it. Do this for every 
    possible prefix-suffix using the excluded vertex.
*/
import java.util.*;
import java.util.Arrays;
import java.io.*;

import estimator.util.*;
import estimator.general.*;
//import recur.TarjanSCC;
//import recur.SCC;

public class PairAndSetGenerator
{
	//Test purposes only
    public static void main(String[] args) 
	{
        startGeneration(4, 1, 2);
	}
    
    public static void startGeneration(int prefSufLength, int source, int dest)
    {
       
            System.out.println("IN START GENERATION");
            startGenerationUtil(prefSufLength,source,dest);
            //length= estimator.general.Algorithm2.lengthDistribution(list_of_graphs, graph, source, destination);
       
    }
    public static void startGenerationUtil(int prefSufLength, int source, int dest)
    {
        
        File temp_object = new File("testOutput.txt");
        if(temp_object.exists()){
            temp_object.delete();
        }
        ArrayList<Integer> verts = new ArrayList<Integer>();
        Graph<Integer> graph = constructGraph(verts);

        int[] vertices = new int[verts.size()-2]; //11,12,13,14,15,16,17,18,19,20;
        int verticesCount = 0;
        for(int j=0; j<verts.size();j++)
        {
            if( (verts.get(j)!=source) && (verts.get(j)!=dest) ){
               vertices[verticesCount] = verts.get(j); 
               verticesCount++;
            }
        }
        int number_of_verts = verts.size();
        //System.out.println(number_of_verts);
        if(number_of_verts<=3){
            //insert BruteForce Solution
        }  
        else{
            int actual_excluded_verts = Math.min(prefSufLength,number_of_verts-2);
            System.out.println(" Actual # of excluded verts is :" + actual_excluded_verts+" , out of total: " +number_of_verts);
            generatePairAndSet(vertices,actual_excluded_verts, source, dest);
            try{
            testFileInput.initializeMapReduce(source, dest);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //subgraphGraphProcessor.init();
        }

    }

    public static Graph<Integer> constructGraph(ArrayList<Integer> verts)
    {
        //String currentDir = System.getProperty("user.dir");
        //System.out.println("Current dir using System:" +currentDir);
        File original = new File("subgraph.txt");
        Graph<Integer> originalGraph = new Graph<Integer>();
        try
        {
            Scanner in = new Scanner(original);
            int counter=0;
            while(in.hasNextLine())
            {
                
                if(counter>=2){
                    String line  = in.nextLine();
                    String[] lineArray = line.split(" ");
                    verts.add(Integer.parseInt(lineArray[0]));
                    for(int i = 1; i<lineArray.length; i++)
                    {
                        originalGraph.addEdge(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i]));
                    }
                }
                else{
                    String line =  in.nextLine();
                }
                counter++;
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return originalGraph;
    }

	public static void generatePairAndSet(int[] verts, int setLength, int source, int dest)
	{
		generateCombination(verts,verts.length,2,setLength, source, dest);
	}

	public static void combinationUtil(int[] arr, int[] data, int start,int end, int index, int r,int setLength, int source, int dest)
    {
        if (index == r)
        {
            generateAndPrintSet(arr,data,setLength-r,source,dest);
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r,setLength, source, dest);
        }
    }
 
    public static void generateCombination(int[] arr, int n, int r, int setLength, int source, int dest)
    {
        int[] data=new int[r];
        combinationUtil(arr, data, 0, n-1, 0, r,setLength, source,dest);
    }

     public static void generateAndPrintSet(int[] arr,int[] data,int actual_setLength,int source, int dest)
    {
        
        //Set<Integer> setA = new HashSet(Arrays.asList(temp_arr));
        Set<Integer> setB = new HashSet<Integer>();
        for (int k = 0; k<data.length;k++)
        {
            setB.add(data[k]);
        }

        int[] temp_arr = new int[arr.length-data.length];
        int l = 0;
        for (int m = 0; m<arr.length;m++)
        {
            if(!setB.contains(arr[m]))
            {
                temp_arr[l] = arr[m];
                l++;
            }
        }
        arr = temp_arr;
        int[] new_data=new int[actual_setLength];
        int n = arr.length;
        combinationUtility(arr, new_data, 0, n-1, 0, actual_setLength,data, source,dest);
    }
    public static void combinationUtility(int[] arr, int[] new_data, int start,int end, int index, int setLength,int[] data,int source, int dest)
    {
        
        if (index == setLength)
        {
            printSourceDestAndSet(new_data,data,source,dest);
            int[] data_reverse = new int[data.length]; //for reversing dest and source
            for (int q=data.length-1;q>=0;q--){
                data_reverse[data.length-q-1] = data[q];
            }
            printSourceDestAndSet(new_data,data_reverse,source,dest);
            return;
        }

        for (int i=start; i<=end && end-i+1 >= setLength-index; i++)
        {
            new_data[index] = arr[i];
            combinationUtility(arr, new_data, i+1, end, index+1, setLength,data,source,dest);
        }
    }
    public static void printSourceDestAndSet(int[] setToExclude, int[] source_dest, int source, int dest)
    {   
        try (
           FileWriter fw = new FileWriter("testOutput.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            String line = "("+source+","+dest+")" +" : " + Arrays.toString(source_dest) + " : "+ Arrays.toString(setToExclude);
            out.println(line);
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Error on graph write!");    
        }
    }

}