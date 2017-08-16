package org.mike.mr.ipStatistics;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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



public class IP {
	public static class IPMapper extends MapReduceBase implements Mapper<Object,Text,Text,Text> {
		private Text request = new Text();
		private Text ip = new Text();
		
		@Override
		public void map(Object key, Text value, OutputCollector<Text, Text> output, Reporter repoter)
				throws IOException {
			LogEntity le = new LogEntity();
			le = LogEntity.parser(value.toString());
			request.set(le.getRequest_url());
			ip.set(le.getUser_ip());
			output.collect(request, ip);
		}
		
	} 
	
	public static class IPReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text> {
		private Text ipCount = new Text();
		private Set<String> set = new HashSet<String>();
		
		int sum = 0;
		@Override
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output,
				Reporter reporter) throws IOException {
			while(values.hasNext()) {
				set.add(values.next().toString());
			}
			ipCount.set(String.valueOf(set.size()));
			output.collect(key, ipCount);
		}
		
	}
	
	public static class IPAnalysis {
		public static void main(String[] args) throws IOException {
			String inputPath = "hdfs://10.1.9.70:9000/inputLog/a2.log";
			String outputPath = "hdfs://10.1.9.70:9000/outputIPLog";
			
			JobConf conf = new JobConf(IP.class);
			conf.setJobName("IP");
			conf.setMapOutputKeyClass(Text.class);
			conf.setMapOutputValueClass(Text.class);
			
//			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(IntWritable.class);
			
			conf.setMapperClass(IPMapper.class);
			conf.setCombinerClass(IPReducer.class);
			conf.setReducerClass(IPReducer.class);
			
			FileInputFormat.setInputPaths(conf, new Path(inputPath));
			FileOutputFormat.setOutputPath(conf, new Path(outputPath));
			
			JobClient.runJob(conf);
			System.exit(0);
		}
	}
}
