import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.namenode.startupprogress.StartupProgress.Counter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.bayes.Algorithm;
import org.apache.mahout.classifier.bayes.BayesAlgorithm;
import org.apache.mahout.classifier.bayes.BayesParameters;
import org.apache.mahout.classifier.bayes.CBayesAlgorithm;
import org.apache.mahout.classifier.bayes.ClassifierContext;
import org.apache.mahout.classifier.bayes.Datastore;
import org.apache.mahout.classifier.bayes.InMemoryBayesDatastore;
import org.apache.mahout.classifier.bayes.InvalidDatastoreException;
import org.apache.mahout.common.nlp.NGrams;
import org.slf4j.LoggerFactory;




public class ClassifierMapper extends Mapper<Text, Text, Text, IntWritable>{
	private Text outKey = new Text();
	private static final IntWritable ONE = new IntWritable(1);
	
	private int gramSize = 1;
	private ClassifierContext classifier;
	private String defaultCategory;
	
	private static final  Logger log = (Logger) LoggerFactory.getLogger(ClassifierMapper.class);
	
	@Override
	protected void map(Text key, Text value, Context context) throws IOException ,InterruptedException {
		String docLabel = "";
		String userID = key.toString();
		
		//ngrams为存储一片文章分词后对应的特征的集合
		List<String> ngrams = new NGrams(value.toString(), gramSize).generateNGramsWithoutLabel();
		
		ClassifierResult result;
		try {
			//用classifyDocument函数对这篇文章进行判别
			result = classifier.classifyDocument(ngrams.toArray(new String[ngrams.size()]),defaultCategory);
			docLabel = result.getLabel();
		} catch (InvalidDatastoreException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(),e);
		}
		
		outKey.set(userID + "|" + docLabel);
		context.write(key, ONE);
	}
	
	
	@Override	
	public void setup(Context context) throws IOException {
		Configuration conf = context.getConfiguration();
		BayesParameters params = new BayesParameters(conf.get("bayes.parameters",""));
		log.info("bayes parameter: " + params.print());
		Algorithm algorithm;
		Datastore datastore;   //保存模型结果
		
		if ("bayes".equalsIgnoreCase(params.get("classifierType"))) {
			algorithm = new BayesAlgorithm();
			datastore = new InMemoryBayesDatastore(params);
			
		}else if ("cbayes".equalsIgnoreCase(params.get("classifierType"))) {
			algorithm = new CBayesAlgorithm();
			datastore = new InMemoryBayesDatastore(params);
		}else {
			throw new IllegalArgumentException("Unrecongnized classifier param" + params.get("classifierType"));
			
		}
		classifier = new ClassifierContext(algorithm, datastore);
		try {
			classifier.initialize();
		} catch (InvalidDatastoreException e) {
			log.error(e.toString(),e);
		}
		
	}
	
	
}
