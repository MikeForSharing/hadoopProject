package org.mike.mr.ipStatistics;

public class test {
	public static void main(String[] args) {
		String name = "sfs [25/Nov/2011:00:30:08 +0800]";
		String s2 = name.substring(1);
		
//		System.out.println(s2);
		
		String log = "/ 112.97.24.243 - - [31/Jan/2012:00:14:48 +0800] \"GET /data/cache/style_2_common.css?AZH HTTP/1.1\" 200 57752 \"http://f.dataguru.cn/forum-58-1.html\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 5_0_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9A406\"";
		String[] arr = log.split(" ");
		for(int i=0;i<arr.length;i++) {
			System.out.println(arr[i]);
		}
	}
}
