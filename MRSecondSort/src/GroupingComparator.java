import org.apache.hadoop.io.RawComparator;

public class GroupingComparator implements RawComparator<IntPairs>{

	@Override
	public int compare(IntPairs arg0, IntPairs arg1) {
		int first1 = arg0.getFirst();
		int first2 = arg1.getFirst();
		return first1 - first2;
	}

	@Override
	public int compare(byte[] arg0, int arg1, int arg2, byte[] arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		return 0;
	}

}
