import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.lang.math.Fraction;
import org.apache.hadoop.io.WritableComparable;

public class IntPairs implements WritableComparable<IntPairs> {

	private int first;
	private int second;
	
	public void set(int left, int right) {
		this.first = left;
		this.second = right;
	}
	
	public int getFirst() {
		return first;
	}
	
	public int getSecond() {
		return second;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		this.first = in.readInt();
		this.second = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(first);
		out.writeInt(second);
		
	}

	
	
	@Override
	public int compareTo(IntPairs o) {
		// TODO Auto-generated method stub
		if(first!= o.first) {
			return first - o.first;
		}else if (second != o.second) {
			return second - o.second;
		}else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntPairs) {
			IntPairs tmp = (IntPairs) obj;
			return tmp.first == first&&tmp.second == second;
		}else {
			return false;
		}
	}

}
