import java.io.DataInputStream;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;

import org.apache.commons.collections.FastArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.CombineFileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.inject.Key;

public class MyRecordReader extends RecordReader<Text, Text>{
	private CombineFileSplit combineFileSplit;  //当前处理的分片
	private int totalLength;  //分片包含的文件数量
	private int currentIndex;   //当前处理的文件索引
	private float curProgress = 0;//当前处理的进度
	private Configuration conf;  //任务信息
	private Text currentKey = new Text();    //当前的key
	private Text currentValue = new Text();   //当前的value
	private boolean processed;   //记录当前文件是否已读
	
	public MyRecordReader (CombineFileSplit combineFileSplit,TaskAttemptContext context,int index) {
		super();
		this.currentIndex = index;
		this.conf = context.getConfiguration();
		this.combineFileSplit = combineFileSplit;
		this.totalLength = combineFileSplit.getPaths().length;
		processed = false;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (!processed) {
			//得到key
			Path filePath = combineFileSplit.getPath(currentIndex);
			currentKey.set(filePath.getParent().getName());
			//得到value
			DataInputStream dis = null;
			byte[] contents = new byte[(int) combineFileSplit.getLength(currentIndex)];
			try {
				FileSystem fs = filePath.getFileSystem(conf);
				dis = fs.open(filePath);
				dis.readFully(contents);
				currentValue.set(contents);
				processed = true;
				return true;
			} catch (Exception e) {
				// TODO: handle exception
			}finally {
				dis.close();
			}
			
		}
		return false;
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return currentKey;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return currentValue;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if(currentIndex>=0 && currentIndex<totalLength) {
			curProgress = (float)currentIndex/totalLength;
			return curProgress;
		}
		return curProgress;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}


}
