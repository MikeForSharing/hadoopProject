package org.mike.mr.ipLocation;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

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
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.mike.mr.ipStatistics.LogEntity;

public class LocationAnalysis {
	
	public static int ipNum = 0;
	
	public static class LAMapper extends MapReduceBase implements Mapper<Object,Text,Text,IntWritable> {
		IntWritable one = new IntWritable(1);
		Text ipAddr = new Text();
		
		@Override
		public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter report)
				throws IOException {
//			System.out.println("********************************key:" + key + "************************************");
//			System.out.println("********************************value:" + value.toString() + "************************************");
			LogEntity le = LogEntity.parser(value.toString());
			String ip = le.getUser_ip();
			ipAddr.set(ip);
			output.collect(ipAddr, one);
		}
	}
	
	public static class LAReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text,Text> {

		IPSeeker ips = IPSeeker.getInstance();
		
		@Override
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, Text> output, Reporter report)
				throws IOException {
			System.out.println("********************************LAReducer key:" + key + "************************************");
			String add = ips.getAddress(key.toString());
			Text ips = new Text();
			int sum = 0;
			while(values.hasNext()) {
				sum += values.next().get();
			}
			String outputValue = sum + "+" +add;
			ips.set(outputValue);
			output.collect(key, ips);
		}
	}
	
	public static class LAMapper2 extends MapReduceBase implements Mapper<Object,Text,Text,IntWritable> {

		@Override
		public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter report)
				throws IOException {
			String line = value.toString();
			System.out.println("********************************LAMapper2 key:" + key + "************************************");
			System.out.println("********************************LAMapper2 value:" + line + "************************************");
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			String str = "";
			while(tokenizer.hasMoreTokens()) {
				String tmp = tokenizer.nextToken();
				if(tmp.contains("+")) {
					str = tmp;
					break;
				}
			}
			int sep = str.indexOf("+");
			int num = Integer.parseInt(str.substring(0, sep));
			String address = str.substring(sep+1);
			ipNum += num;
			output.collect(new Text(address), new IntWritable(num));
		}
		
	}
	
	
	
	public static class LAReducer2 extends MapReduceBase implements Reducer<Text,IntWritable,Text,Text> {
		@Override
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
			int sum = 0;
			System.out.println("********************************LAReducer2 key:" + key + "************************************");

			while(values.hasNext()) {
				sum += values.next().get();
				System.out.println("sum:" + sum);
			}
			
			float percent = (float)sum/(float)ipNum ;
			String percent2 = " " + String.valueOf(percent) + "%";
			output.collect(key, new Text(percent2));
		}
	
	}
	
	public static void main(String[] args) throws IOException {
		String input = "hdfs://10.1.9.70:9000/inputLog/a2.log";
		String output = "hdfs://10.1.9.70:9000/outputIPLocation";
		Path tmpDir = new Path("hdfs://10.1.9.70:9000/tempLog");
		
		
	
		JobConf conf = new JobConf(LocationAnalysis.class);
		conf.setJobName("LocationAanlysis");
		
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(IntWritable.class);
		
		conf.setMapperClass(LAMapper.class);
		conf.setReducerClass(LAReducer.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, new Path(input));
		FileOutputFormat.setOutputPath(conf, tmpDir);
		
		JobClient.runJob(conf);
		
		JobConf conf2 = new JobConf(LocationAnalysis.class);
		conf2.setJobName("LocationAnalysis2");
		
		conf2.setMapOutputKeyClass(Text.class);
		conf2.setMapOutputValueClass(IntWritable.class);
		
		conf2.setOutputKeyClass(Text.class);
		conf2.setOutputValueClass(Text.class);
		
		conf2.setMapperClass(LAMapper2.class);
		conf2.setReducerClass(LAReducer2.class);
		
		conf2.setInputFormat(TextInputFormat.class);
		conf2.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf2, tmpDir);
		FileOutputFormat.setOutputPath(conf2, new Path(output));
		
		JobClient.runJob(conf2);
		System.exit(0);
		
		
		
	}
	
}
