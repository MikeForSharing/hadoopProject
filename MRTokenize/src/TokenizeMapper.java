import java.io.IOException;
import java.io.StringReader;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class TokenizeMapper extends Mapper<Text, Text, Text, Text>{
	Text outKey = new Text();
	Text outValue = new Text();
	PaodingAnalyzer analyzer = new PaodingAnalyzer();
	
	public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		outKey.set(key);
		String line = value.toString();
		StringReader sr = new StringReader(line);
		TokenStream ts = analyzer.tokenStream("", sr);
		StringBuilder sb = new StringBuilder();
		while(ts.incrementToken()) {
			CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
			sb.append(cta.toString());
			sb.append(" ");
		}
		
		outValue.set(sb.toString());
		
		context.write(key, outValue);
	}
}
