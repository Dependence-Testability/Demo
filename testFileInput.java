import java.io.*;  
import java.io.IOException;
import java.util.*;
import java.util.Arrays;
import java.io.File;
import java.net.*;  
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class testFileInput{
    public static int counter = 0;
    public static void main(String[] args) throws Exception 
    {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "testFileInput");
        job.setJar("testFileInput.jar");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(FileInputMap.class);
        job.setReducerClass(FileInputReduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

    public static void initializeMapReduce(int source, int dest) throws Exception
    {
         Configuration conf = new Configuration();

        Job job = new Job(conf, "DemoReduce");
        job.setJar("DemoReduce.jar");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(FileInputMap.class);
        job.setReducerClass(FileInputReduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path("Demo/testOutput.txt"));
        FileOutputFormat.setOutputPath(job, new Path("DemoReduceOutput"+counter));

        job.waitForCompletion(true);
        if(job.isSuccessful()){
            subgraphGraphProcessor.initialize("DemoReduceOutput"+counter+"/part-r-00000");
        }
        counter++;
    }
}