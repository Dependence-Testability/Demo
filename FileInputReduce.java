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
 public static int init_source;
    public static int init_dest;
    public static Graph<Integer> subGraph;
    //public static Context public_context;
    public static String public_prefSuf;
    public static String toExclude_public;



    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException 
    {
        public_context = context;
        ArrayList<Integer> verts = new ArrayList<Integer>();
        subGraph = constructGraph(verts);
        System.out.println(verts.toString());
        System.out.println(subGraph.toString());      
        String inputline;
        int[] pref_suff_array = new int[2]; 
        int[] toExclude_array;
        inputline = key.toString();
        String[] splitKey = inputline.split(" : ");
        String initSourceDest = splitKey[0];
        String pref_suff = splitKey[1];
        String toExclude = splitKey[2];

        public_prefSuf = splitKey[1]; //used in printin out
        toExclude_public = splitKey[2]; //used in printin out

        String[] source_dest_refined = initSourceDest.replace(')',' ').replace('(',' ').split(",");
        String[] toExclude_refined = toExclude.replace(']',' ').replace('[',' ').replace(',',' ').split("  ");
        String[] pref_suff_refined = pref_suff.replace(']',' ').replace('[',' ').replace(',',' ').split("  ");
        //System.out.println(Arrays.toString(source_dest_refined));
        init_source = Integer.parseInt(source_dest_refined[0].trim());
        init_dest = Integer.parseInt(source_dest_refined[1].trim());
        for(int i = 0;i<pref_suff_refined.length;i++)
        {
            pref_suff_array[i] = Integer.parseInt(pref_suff_refined[i].trim());
        }
        toExclude_array = new int[toExclude_refined.length];
        for(int i = 0;i<toExclude_refined.length;i++)
        {
            toExclude_array[i] = Integer.parseInt(toExclude_refined[i].trim());
        }

        int[] prefix_array = new int[1];
        int[] suffix_array = new int[1];
        prefix_array[0] = pref_suff_array[0];
        suffix_array[0] = pref_suff_array[1];
        
        permuteAndSplit(prefix_array,suffix_array,toExclude_array,toExclude_array.length,context);
       
    }

     public static void permuteAndSplit(int[] prefix_array, int[] suffix_array, int[] toExclude_array, int n,Context context){
        System.out.println("In Permuta And Split");
        if (n <= 1) {
            //int m = setB.length;
            splitSet(prefix_array,suffix_array,toExclude_array,context);
            return;
        }
        for (int i = 0; i < n; i++) {
            swap(toExclude_array, i, n-1);
            permuteAndSplit(prefix_array,suffix_array,toExclude_array,n-1,context);
            swap(toExclude_array, i, n-1);
        } 
    }

     public static void splitSet(int[] prefix_array, int[] suffix_array, int[] toExclude_array,Context context){
        System.out.println("In split Set");
        int length = toExclude_array.length;
        int i=0;
        if(length==0)
        {
            //ArrayList<int[]> splitted = splittedSet(toExclude_array,i);
            int[] arrayA = new int[0];
            int[] arrayB = new int[0];
             try{
                printPossible(prefix_array,suffix_array,arrayA,arrayB, context);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            while(i<length-1){
               ArrayList<int[]> splitted = splittedSet(toExclude_array,i);
               try{
                    printPossible(prefix_array,suffix_array,splitted.get(0),splitted.get(1), context);
                }
                catch(Exception e){
                    System.out.println("Fam we have an error!");
                    e.printStackTrace();
                }
               i++;
            }
        }
    }
    public static ArrayList<int[]> splittedSet(int[] toExclude_array,int i)
    {
        ArrayList<int[]> temp = new ArrayList<int[]>();
        int j = 0;
        int[] arrayA = new int[i];
        int[] arrayB  = new int[toExclude_array.length-i];
        while (j<i)
        {
            arrayA[j] = toExclude_array[j];
            j++;
        }
        int k = 0;
        while (j<toExclude_array.length)
        {
            arrayB[k]= toExclude_array[j];
            j++;
            k++;
        }
        //System.out.println(Arrays.toString(arrayA)+":" + Arrays.toString(arrayB));
        temp.add(arrayA);
        temp.add(arrayB);
        return temp;
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
        System.out.println("In printPossible!!!!!!!!!!!!!!");
        int[] merge_pref_1 = mergeArray(setA, prefix_array,true);
        int[] merged_suf_1 = mergeArray(suffix_array,setB,false);
        int[] merged_pref_suf = new int[2];
        merged_pref_suf[0] = prefix_array[0];
        merged_pref_suf[1] = suffix_array[0];
        System.out.println(Arrays.toString(merged_pref_suf)+ " | "+ Arrays.toString(merge_pref_1) +" : "+ Arrays.toString(merged_suf_1));

        if(checkValidPath(merge_pref_1) && checkValidPath(merged_suf_1))
        {
            Text word  = new Text(public_prefSuf+":"+ toExclude_public+":");//use colon as custom delimeter
            IntWritable one = new IntWritable(1);
            context.write(word, one);
        }
        int[] merge_pref_2 = mergeArray(setB, prefix_array,true);
        int[] merged_suf_2 = mergeArray(suffix_array,setA,false);
        System.out.println(Arrays.toString(merged_pref_suf)+ " | "+ Arrays.toString(merge_pref_2) +" : "+ Arrays.toString(merged_suf_2));

        if(checkValidPath(merge_pref_2) && checkValidPath(merged_suf_2))
        {
            Text word2  = new Text(public_prefSuf+":"+ toExclude_public+":"); //use colon as custom delimeter
            IntWritable one2 = new IntWritable(1);
            context.write(word2, one2);
        }
    }

    public static int[] mergeArray(int[] a, int[] b, boolean addtofront)
    {
        int[] merged = new int[a.length+b.length+1];
        
        if(addtofront){
            merged[0] = init_source;
            int k = 1;
            for(int i=0;i<a.length;i++){
                merged[k] = a[i];
                k++;
            }
            for(int j= 0;j<b.length;j++){
                merged[k] = b[j];
                k++;
            }
        }
        else{
            int k = 0;
            for(int i=0;i<a.length;i++){
                merged[k] = a[i];
                k++;
            }
            for(int j= 0;j<b.length;j++){
                merged[k] = b[j];
                k++;
            }
            merged[k] = init_dest;  
        }
        return merged;
    }


    public static Graph constructGraph(ArrayList<Integer> verts)
    {
        System.out.println("In Constructgraph");
        Graph<Integer> subGraph = new Graph<Integer>();
        try
        {
            Configuration config = new Configuration();
            config.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
            config.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
            Path filePath = new Path("/user/mibrahim17/Demo/subgraph.txt");
            FileSystem fs = filePath.getFileSystem(config);
            FSDataInputStream fsDataInputStream = fs.open(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fsDataInputStream));
            int counter = 0; //to discard first two lines;
            String line = br.readLine();
            while(line != null)
            {
                if (counter<2)
                {
                   line = br.readLine();
                   counter++; 
                }
                else{
                    String[] lineArray = line.split(" ");
                    verts.add(Integer.parseInt(lineArray[0]));
                    for(int i = 1; i<lineArray.length; i++)
                    {
                        subGraph.addEdge(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[i]));
                    }
                    line = br.readLine();
                }
            }
            br.close();
            //fs.close();
        }
        catch(Exception e)
        {
            System.err.println("FileInputReduce Error");
            e.printStackTrace();
            System.exit(1);
        }

        return subGraph;
    }

    public static boolean checkValidPath(int[] arr)
    {
        System.out.println("In CHECKVALIDPATH");
        if(arr.length>1)
        {
            for (int u = 0; u<arr.length-1; u++)
            {
                if(!(subGraph.getNode(arr[u]).hasEdge( subGraph.getNode(arr[u+1])))){
                    System.out.println("edge"+arr[u]+" to "+ arr[u+1]+" not found");
                    return false;
                }
            }
        }
        // System.out.println(Arrays.toString(arr));
        System.out.println("Valid path in array: " +Arrays.toString(arr));
        return true;
    }
 	///NExt brackt ends class
 }