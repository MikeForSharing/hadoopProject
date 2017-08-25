import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMap extends Mapper<LongWritable, Text, IntPairs, IntWritable> {
	private IntPairs key = new IntPairs();
	private IntWritable value = new IntWritable();
	
	@Override
	public void map(LongWritable inkey, Text invalue, Context context) throws IOException, InterruptedException {
		
		String[] strArr = value.toString().split(" ");
		int left = 0;
		int right = 0;
		
		if (strArr[0].length() !=0) {
			left = Integer.parseInt(strArr[0]);
			if (strArr[1].length()!=0) {
				right = Integer.parseInt(strArr[1]);
			}
			
		}
		
		key.set(left,right);
		value.set(right);
		
		context.write(key, value);
		
	}
	
	
}
