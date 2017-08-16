import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Hdfs;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.mahout.classifier.bayes.BayesParameters;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class ClassifierDriver {

	public static void main(String[] args) throws IOException {
		
		//设置bayes参数
		BayesParameters params = new BayesParameters();
		params.setBasePath(args[2]);
		params.set("classifierType", args[3]);
		params.set("alpha_i", "1.0");
		params.set("defaultCat", "unknown");
		params.setGramSize(1);
		
		
		//设置配置
		Configuration conf = new Configuration();
		conf.set("bayes.parameters",params.toString());
		
		//设定Job
		Job job = new Job(conf,"Classifier");
		job.setJarByClass(ClassifierDriver.class);
		
		//设定输入格式
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		
		//设定Mapper和Reducer
		job.setMapperClass(ClassifierMapper.class);
		job.setReducerClass(ClassifierReducer.class);
		
		//设置map和reduce的outputkey和outputvalue
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		//设置路径
		Path inPath = new Path(args[0]);
		Path outPath = new Path(args[1]);

		FileInputFormat.addInputPath(job, inPath);

		FileOutputFormat.setOutputPath(job, outPath);
		
		FileSystem hfds = outPath.getFileSystem(conf);
		
		if (hfds.exists(outPath)) {
			hfds.delete(outPath);
		}
		hfds.close();
	
	}

}
