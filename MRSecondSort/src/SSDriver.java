
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SSDriver {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		//设置配置
		Configuration conf = new Configuration();
		Path inPath = new Path(args[0]);
		Path outPath = new Path(args[1]);
		
		Job job = new Job(conf,"secondSort");
		job.setJarByClass(SSDriver.class);
		job.setMapperClass(MyMap.class);
		job.setReducerClass(MyReduce.class);
		
		job.setGroupingComparatorClass(GroupingComparator.class);
		
		job.setMapOutputKeyClass(IntPairs.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, inPath);
		FileOutputFormat.setOutputPath(job, outPath);
		
		System.exit(job.waitForCompletion(true) ? 0:1);
		
	}

}
