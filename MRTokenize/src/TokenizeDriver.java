import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Hdfs;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TokenizeDriver {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		//设定conf
		Configuration conf = new Configuration();
		conf.setLong("mapreduce.input.fileinputstream.split.maxsize", 4000000);
		
		Job job = new Job(conf,"Tokenizer");
		job.setJarByClass(TokenizeDriver.class);
		
		job.setInputFormatClass(MyInputFormat.class);
		job.setMapperClass(TokenizeMapper.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		Path inPath = new Path(args[0]);
		Path outPath = new Path(args[1]);	
		
		FileSystem fSystem = inPath.getFileSystem(conf);
		FileStatus[] status = fSystem.listStatus(inPath);
		for(int i=0;i<status.length;i++) {
			FileInputFormat.addInputPath( job, status[i].getPath());
		}
		FileOutputFormat.setOutputPath(job, outPath);
		
		
		FileSystem hdfs = outPath.getFileSystem(conf);
		if(hdfs.exists(outPath)) {
			hdfs.delete(outPath);
		}
		System.exit(job.waitForCompletion(true) ?0:1);
	
	}
}
