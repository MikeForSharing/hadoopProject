import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;


public class PeopleRankJob {
	public static final String HDFS = "hdfs://111.10.1.200:8020";
	public static final Pattern DELIMITER = Pattern.compile("[\t,]");
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		Map<String, String> path = peopleRank();
		AdjacencyMatrix.run(path);
		for (int i = 0; i < 10; i++) {
			PeopleRank.run(path);
		}
		Normal.run(path);
		
	}

	private static Map<String, String> peopleRank() {
		Map<String, String> path = new HashMap<String, String>();
		
		path.put("page", "logfile/pagerank/people.csv");// 本地的数据文件
        path.put("pr", "logfile/pagerank/peoplerank.csv");// 本地的数据文件
        path.put("nums", "25");// 用户数
        path.put("d", "0.85");// 阻尼系数

        path.put("input", HDFS + "/user/hdfs/pagerank");// HDFS的目录
        path.put("input_pr", HDFS + "/user/hdfs/pagerank/pr");// pr存储目
        path.put("tmp1", HDFS + "/user/hdfs/pagerank/tmp1");// 临时目录,存放邻接矩阵
        path.put("tmp2", HDFS + "/user/hdfs/pagerank/tmp2");// 临时目录,计算到得PR,覆盖input_pr

        path.put("result", HDFS + "/user/hdfs/pagerank/result");// 计算结果的PR
        return path;
		
	}

	
	public static JobConf config() {
		JobConf conf = new JobConf(PeopleRankJob.class);
		conf.setJobName("peopleName");
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/hdfs-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }
	
	
	public static String scaleFloat(float f) {
		DecimalFormat df = new DecimalFormat("##0.000000");
		return df.format(f);
	}

}
