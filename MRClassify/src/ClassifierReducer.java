import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ClassifierReducer extends Reducer<Text, IntWritable, NullWritable, Text> {
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		Text outValue = new Text();
		int num = 0;
		for(IntWritable value:values) {
			num += value.get();
		}
		outValue.set(key.toString() + "|" + num);
		context.write(NullWritable.get(), outValue);
		
	}
}
