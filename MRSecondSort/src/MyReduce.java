
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReduce extends Reducer<IntPairs, IntWritable, Text, IntWritable>{
	private static final Text SEPARATOR = new Text("------------------------------");
	Text first = new Text();
	
	@Override
	public void reduce(IntPairs key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
		context.write(SEPARATOR, null);
		first.set(Integer.toString(key.getFirst()));
		for(IntWritable value : values) {
			context.write(first, value);
		}
	}

}
