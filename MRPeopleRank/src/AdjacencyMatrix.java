import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import io.netty.handler.codec.http.HttpHeaders.Values;

public class AdjacencyMatrix {
	
	private static int nums;
	private static float d;
	
	
	public static class AdjacencyMatrixMapper extends Mapper<LongWritable, Text, Text, Text>{
		
		@Override
		public void map(LongWritable key,Text values, Context context) throws IOException, InterruptedException {
			String[] tokens = PeopleRankJob.DELIMITER.split(values.toString());
			Text k = new Text(tokens[0]);
			Text v = new Text(tokens[1]);
			context.write(k, v);
		}
	}
	
	public static class AdjacencyMatrixReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text key,Iterable<Text> values,Context context) throws IOException, InterruptedException {
			float[] G = new float[nums];
			Arrays.fill(G, (1-d)/G.length);
			int[] A = new int[nums];
			int sum = 0;
			
			for (Text value : values) {
				int index = Integer.parseInt(value.toString());
				A[index-1] = 1;
				sum++;
			}
			
			if (sum == 0) {
				sum = 1;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < G.length; i++) {
				sb.append("," + (G[i] + d*A[i]/sum));
			}
			
			Text v = new Text(sb.toString().substring(1));
			context.write(key, v);
		}
	}
	
	public static void run(Map<String, String> path) throws IOException, ClassNotFoundException, InterruptedException {
		 JobConf conf = PeopleRankJob.config();

	        String input = path.get("input");
	        String input_pr = path.get("input_pr");
	        String output = path.get("tmp1");
	        String page = path.get("page");
	        String pr = path.get("pr");
	        nums = Integer.parseInt(path.get("nums"));// 页面数
	        d = Float.parseFloat(path.get("d"));// 页面数


	        Job job = new Job(conf);
	        job.setJarByClass(AdjacencyMatrix.class);

	        job.setOutputKeyClass(Text.class);
	        job.setOutputValueClass(Text.class);

	        job.setMapperClass(AdjacencyMatrixMapper.class);
	        job.setReducerClass(AdjacencyMatrixReducer.class);

	        job.setInputFormatClass(TextInputFormat.class);
	        job.setOutputFormatClass(TextOutputFormat.class);

	        FileInputFormat.setInputPaths(job, new Path(page));
	        FileOutputFormat.setOutputPath(job, new Path(output));

	        job.waitForCompletion(true);
	}
	
}
