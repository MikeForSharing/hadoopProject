import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class PeopleRank {
	private static int nums;  //页面数量
	
	public static class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
		private String flag;
		
		@Override
		protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			FileSplit split = (FileSplit) context.getInputSplit();
			flag = split.getPath().getParent().getName();
		}

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String[] tokens = PeopleRankJob.DELIMITER.split(value.toString());
			if (flag.equals("tmp1")) {
				String row = tokens[0];
				for (int i = 1; i < tokens.length; i++) {
					Text k = new Text(String.valueOf(i));
					Text v = new Text("A" + row + "," + tokens[i]);
					context.write(k, v);
				}
			}else if (flag.equals("pr")) {
				for (int i = 1; i <= nums; i++) {
					Text k = new Text(String.valueOf(i));
					Text v = new Text("B" + tokens[0] + "," + tokens[1]);
					context.write(k, v);
					
				}
			}
			
			
			
		}
		
		
		
		
	}
	
	
	public static class PageRankReducer extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			Map<Integer,Float> mapA = new HashMap<Integer, Float>();
			Map<Integer,Float> mapB = new HashMap<Integer,Float>();
			float pr = 0f;
			for(Text line:values) {
				String val = String.valueOf(line);
				if (val.startsWith("A:")) {
					String[] tokenA = PeopleRankJob.DELIMITER.split(val.substring(2));
					mapA.put(Integer.parseInt(tokenA[0]), Float.parseFloat(tokenA[1]));
				}
				if (val.startsWith("B：")) {
					String[] tokenB = PeopleRankJob.DELIMITER.split(val.substring(2));
					mapB.put(Integer.parseInt(tokenB[0]), Float.parseFloat(tokenB[1]));
				}
			}
			
			
			Iterator<Integer> iter = mapA.keySet().iterator();
			while (iter.hasNext()) {
				int index = iter.next();
				float a = mapA.get(index);
				float b = mapB.get(index);
				pr += a * b;
			}
			context.write(key, new Text(PeopleRankJob.scaleFloat(pr)));
		}
		
	}
	
	
	public static void run(Map<String, String> path) {
		
	}

}
