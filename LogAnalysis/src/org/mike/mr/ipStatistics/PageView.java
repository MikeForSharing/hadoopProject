package org.mike.mr.ipStatistics;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;



public class PageView {
	public static class PGMapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
		private IntWritable one = new IntWritable(1);
		Text word = new Text();

		@Override
		public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
			// TODO Auto-generated method stub
			LogEntity lg = new LogEntity();
			lg = LogEntity.parser(value.toString());
			if(lg.isValid()) {
				word.set(lg.getRequest_url());
				output.collect(word, one);
			}
		}
	}
	
	public static class PGReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable> {
		IntWritable result = new IntWritable();
		@Override
		public void reduce(Text key,Iterator<IntWritable> values,OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException{
			int sum = 0;
			while(values.hasNext()) {
				sum += values.next().get();
			}
			result.set(sum);
			output.collect(key, result);
		} 
	}
	
	public static class LogAnalysis {
		public static void main(String[] args) {
			String inputPath = "hdfs://10.1.9.70:9000/inputLog/a2.log";
			String outputPath = "hdfs://10.1.9.70:9000/outputLog";
			
			JobConf conf = new JobConf(PageView.class);
			conf.setJobName("PageView");
			conf.setMapOutputKeyClass(Text.class);
			conf.setMapOutputValueClass(Text.class);
			
			
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(IntWritable.class);
			
			conf.setMapOutputKeyClass(Text.class);
			conf.setMapOutputValueClass(IntWritable.class);
			
			conf.setMapperClass(PGMapper.class);
			conf.setCombinerClass(PGReducer.class);
			conf.setReducerClass(PGReducer.class);
			
			conf.setInputFormat(TextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			
			FileInputFormat.setInputPaths(conf, new Path(inputPath));
			FileOutputFormat.setOutputPath(conf, new Path(outputPath));
			
			try {
				JobClient.runJob(conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
			
		}
	}
}
