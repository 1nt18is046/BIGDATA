import java.io.IOException;	//to throw input output operations exceptions
import java.util.StringTokenizer;	//allows application to break a string into tokens

import org.apache.hadoop.conf.Configuration;	//configuration of system parameters
import org.apache.hadoop.fs.Path;	//distributed implementation of filesystem for reading and writing	
import org.apache.hadoop.io.IntWritable;	//compares the intwritables
import org.apache.hadoop.io.Text;	//stores text
import org.apache.hadoop.mapreduce.Job;	//creates a new Job with no particular Cluster and given jobName, Configuration and JobStatus
import org.apache.hadoop.mapreduce.Mapper;	//maps input key/value pairs to a set of intermediate key/value pairs
import org.apache.hadoop.mapreduce.Reducer;	//reduces a set of intermediate values which share a key o a smaller set of values
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;	//describes the input specification for a Map-Reduce job
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;	//describes the output specification for a Map-Reduce job

public class WordCount {
	
	public static class TokenizerMapper extends Mapper<Object,Text,Text,IntWritable>{
		/*Tokenizermapper is user-defined class inheriting from Mapper with mentioned parameters ,The Object must be in <key,value> format */
		
		private final static IntWritable one = new IntWritable(1);
		
		/*creating instance of IntWritable called one that is private. We pass value 1 because for every word present we need to hardcode as 1*/
		
		private Text word = new Text();
		
		/*creating instance for Text Class as private*/
		
		public void map(Object key,Text value,Context  context) throws IOException,InterruptedException{
			
		/* The output of Mapper should be given to the Reducer.This is done by Context context.*/
			StringTokenizer itr = new StringTokenizer(value.toString());	//type conversion
			
			/* The input should be split into tokens */
			
			while(itr.hasMoreTokens()) {	//while there are more tokens
				word.set(itr.nextToken());
				context.write(word,one);
			
			}
		}
	}
	
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
		
		/* Output of the Reducer is in the form of Text and Intwritable is for how many times the string is repeated*/
		
		private IntWritable result = new IntWritable();
		
		public void reduce(Text key,Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			/* here we are overriding reduce  .Context context is used for pipeline concept .
			  Iterable<Intwritable> is used to iteratively search based on key,if it is repeated it should add up the values*/
			
			//main logic of Reducer
			int sum=0;
			for(IntWritable val:values)//values refers to values we need for Mapper
			{
				sum+=val.get();
			}
			
			/*w.r.t key one by one it will proceed, if repetition is present we club and add the values in sum */
			result.set(sum);	//setting the result on to the sum
			context.write(key,result);	//displaying the result stored in it
		}
	}
	
	public static void main(String[] args) throws Exception{	//set method works until work is done ,otherwise throws an Exception
		Configuration conf = new Configuration();  //checks configuration
		//Checking configuration and optionally giving name for job as word count
		
		Job job=Job.getInstance(conf,"word count");	//used to return the object by implementing the logic
		
		job.setJarByClass(WordCount.class);	//tells the nodes where to look for Mapper and Reducer classes
		
		job.setMapperClass(TokenizerMapper.class);	//used to set mapper class to the driver class

		job.setCombinerClass(IntSumReducer.class);	//it sets the combiner for job
		
		job.setReducerClass(IntSumReducer.class);	//sets the reducer for the job
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job,new Path(args[0]));
		FileOutputFormat.setOutputPath(job,new Path(args[1]));
		System.exit(job.waitForCompletion(true)?0:1);	//if the job is successfully executed it will return true and exit the system
	}
}
			
