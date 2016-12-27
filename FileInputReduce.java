import java.io.IOException;
import java.util.*;
import java.util.Arrays;
import java.io.File;
import java.net.*;  
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import estimator.util.*;
import estimator.general.*;

 public class FileInputReduce extends Reducer<Text, IntWritable, Text, IntWritable> 
{
 	// public static int num_of_trials;
  //   public static int source;
  //   public  static int destination;
    public static Graph<Integer> original_graph;



    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
    throws IOException, InterruptedException 
 	{
 		ArrayList<Integer> ver = new ArrayList<Integer>();
        original_graph = constructGraph(ver);
        String inputline;
 		int[] pref_suff_array = new int[2];	
 		int[] toExclude_array;
 		inputline = key.toString();
 		String[] splitKey = inputline.split(" : ");
        String pref_suff = splitKey[0];
        String toExclude = splitKey[1];
        String[] toExclude_refined = toExclude.replace(']',' ').replace('[',' ').replace(',',' ').split("  ");
        String[] pref_suff_refined = pref_suff.replace(']',' ').replace('[',' ').replace(',',' ').split("  ");
        for(int i = 0;i<pref_suff_refined.length;i++)
        {
            pref_suff_array[i] = Integer.parseInt(pref_suff_refined[i].trim());
        }
        toExclude_array = new int[toExclude_refined.length];
        for(int i = 0;i<toExclude_refined.length;i++)
        {
            toExclude_array[i] = Integer.parseInt(toExclude_refined[i].trim());
        }

        //System.out.println(Arrays.toString(toExclude_array));
        //System.out.println(Arrays.toString(pref_suff_array));

        int num = toExclude_array.length; //size of toExclude list, used to figure out # of combinations needed
        int num_of_combinations;
        if(((num+1)%2)>0)
        {
            num_of_combinations = ((num+1)/2)+1;
        }
        else{
            num_of_combinations = (num+1)/2;
        }
        //System.out.println(num_of_combinations);
        int comb = 0;
        int[] prefix_array = new int[1];
        int[] suffix_array = new int[1];
        prefix_array[0] = pref_suff_array[0];
        suffix_array[0] = pref_suff_array[1];
        while(comb<num_of_combinations){ //shoule be <= num_of-combinations
            //System.out.println(comb);
            splitAndPermute(prefix_array,suffix_array,toExclude_array,comb,context);//comb);
            comb++;
        }
    }
    public static void splitAndPermute (int[] prefix_array, int[] suffix_array, int[] toExclude_array, int toSplit,Context context)
    {
        
        int[] data = new int[toSplit];
        makeCombinations(toExclude_array, data , 0 ,toExclude_array.length-1 ,0 ,toSplit,prefix_array,suffix_array,context);
    }

    public static void makeCombinations(int[] toExclude_array, int[] data, int start, int end, int index, int r, int[] prefix_array, int[] suffix_array,Context context)
    {
        if(index == r)
        {
            int[] setA = data;
            Set<Integer> auxSet = new HashSet<Integer>();
            for (int k = 0; k<data.length;k++)
            {
                auxSet.add(data[k]);
            }
            int l = 0;
            int [] tempSetB = new int[toExclude_array.length-data.length];
            for (int m = 0; m<toExclude_array.length;m++)
            {
                if(!auxSet.contains(toExclude_array[m]))
                {
                    tempSetB[l] = toExclude_array[m];
                    l++;
                }
            }
            int[] setB = tempSetB;
            //System.out.println(Arrays.toString(setA));
            //System.out.println(Arrays.toString(setB));
            mergePrefSufWithSplitExcluded(prefix_array,suffix_array,setA,setB,context);
            return;
        }
        else
        {
            for (int i=start; i<=end && end-i+1 >= r-index; i++)
            {
                data[index] = toExclude_array[i];
                makeCombinations(toExclude_array, data, i+1, end, index+1, r,prefix_array,suffix_array,context);
            }
        }

    }
    public static void mergePrefSufWithSplitExcluded(int[] prefix_array, int[] suffix_array,int[] setA, int[] setB,Context context)
    {
        int n = setA.length;
        permuteSetA(prefix_array,suffix_array,setA,setB,n,context);
    }
    public static void permuteSetA(int[] prefix_array, int[] suffix_array,int[] setA, int[] setB, int n,Context context)
    {
        //System.out.println("size of set A is : " + n);
        //System.out.println(Arrays.toString(setA));
        if (n <= 1) {
            int m = setB.length;
            permuteSetB(prefix_array,suffix_array,setA,setB,m,context);
            return;
        }
        for (int i = 0; i < n; i++) {
            swap(setA, i, n-1);
            permuteSetA(prefix_array,suffix_array,setA,setB,n-1,context);
            swap(setA, i, n-1);
        }
    } 
    public static void permuteSetB(int[] prefix_array, int[] suffix_array,int[] setA, int[] setB, int m,Context context)
    {
        //System.out.println("size of set B is : " + n);
        if (m <= 1) {
            try{
            printPossible(prefix_array,suffix_array,setA,setB,context);
            return;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        for (int i = 0; i < m; i++) {
            swap(setB, i, m-1);
            permuteSetB(prefix_array,suffix_array,setA,setB,m-1,context);
            swap(setB, i, m-1);
        }
    }
    private static void swap(int[] a, int i, int j) 
    {
       
        if(a.length!=0)
        {
            int c = a[i];
            a[i] = a[j];
            a[j] = c;
        }
    } 

    public static void printPossible(int[] prefix_array, int[] suffix_array, int[] setA, int[] setB,Context context)
    throws IOException, InterruptedException 
    {
        int[] merge_pref_1 = mergeArray(setA, prefix_array);
        int[] merged_suf_1 = mergeArray(suffix_array,setB);
        int[] merged_pref_suf = mergeArray(prefix_array,suffix_array);
       // System.out.println(merge_pref_1);
        System.out.println("In PrintPossible");

        if(checkValidPath(merge_pref_1) && checkValidPath(merged_suf_1))
        {
           System.out.println("valid path exist for" + Arrays.toString(merge_pref_1)+ "and" + Arrays.toString(merged_suf_1));
           Text word  = new Text(Arrays.toString(merged_pref_suf));
           IntWritable one = new IntWritable(1);
           context.write(word, one);
        }
        int[] merge_pref_2 = mergeArray(setB, prefix_array);
        int[] merged_suf_2 = mergeArray(suffix_array,setA);
        if(checkValidPath(merge_pref_2) && checkValidPath(merged_suf_2))
        {
            System.out.println("valid path exist for" + Arrays.toString(merge_pref_2)+ "and" + Arrays.toString(merged_suf_2));
            Text word2  = new Text(Arrays.toString(merged_pref_suf));
            IntWritable one = new IntWritable(1);
            context.write(word2, one);
        }
        Text word2 = new Text(Arrays.toString(merged_pref_suf)+ " | "+ Arrays.toString(merge_pref_2) +" : "+ Arrays.toString(merged_suf_2));
        IntWritable two = new IntWritable();
        context.write(word2,two);
    }
    public static int[] mergeArray(int[] a, int[] b)
    {
        int[] merged = new int[a.length+b.length];
        int k = 0;
        for(int i=0;i<a.length;i++){
            merged[k] = a[i];
            k++;
        }
        for(int j= 0;j<b.length;j++){
            merged[k] = b[j];
            k++;
        }
        return merged;
    }

    public static Graph constructGraph(ArrayList<Integer> verts)
    {
        Graph<Integer> originalGraph = new Graph<Integer>();
        try
        {
            Configuration config = new Configuration();
            config.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
            config.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
            Path filePath = new Path("/user/dmariselli17/CS490-2/original.txt");
            FileSystem fs = filePath.getFileSystem(config);
            FSDataInputStream fsDataInputStream = fs.open(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fsDataInputStream));
            String line = br.readLine();
            while(line != null)
            {
                String[] lineArray = line.split(" ");
                verts.add(Integer.parseInt(lineArray[0]));
                for(int i = 1; i<lineArray.length; i++)
                {
                    originalGraph.addEdge(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i]));
                }
                line = br.readLine();
            }
            br.close();
            fs.close();
        }
        catch(Exception e)
        {
            System.err.println("FileInputReduce Error");
            e.printStackTrace();
            System.exit(1);
        }

        return originalGraph;
    }

    public static boolean checkValidPath(int[] arr)
    {
        System.out.println(Arrays.toString(arr));
        if(arr.length>1)
        {
            for (int u = 0; u<arr.length-1; u++)
            {
                if(!(original_graph.getNode(arr[u]).hasEdge( original_graph.getNode(arr[u+1])))){
                    System.out.println("edge"+arr[u]+" to "+ arr[u+1]+" not found");
                    return false;
                }
            }
        }
        return true;
    }
 	///NExt brackt ends class
 }