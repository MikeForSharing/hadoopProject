package org.mike.mr.ipStatistics;

public class LogEntity {
	private String user_ip;
	private String user_name;
	private String time;
	private String request_url;
	private String http_status;
	private String boby_bytes_count;
	private boolean valid = true;   //判断数据是否合法
	
	public static LogEntity parser(String line) {
		LogEntity le = new LogEntity();
		String[] arr = line.split(" ");
		if(arr.length >= 10) {
			le.setUser_ip(arr[0]);
			le.setUser_name(arr[1]);
			le.setTime(arr[3].substring(1));
			if(arr[6] == "/") {
				le.setRequest_url("null page!!!");
			}else {
				le.setRequest_url(arr[6]);
			}
			le.setHttp_status(arr[8]);
			le.setBoby_bytes_count(arr[9]);
			if(Integer.parseInt(le.getHttp_status()) >= 400) {
				le.setValid(false);
			}
		}else {
			le.setValid(true);
		}
		return le;		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("用户IP为：" + this.user_ip + "\n");
		sb.append("用户名为：" + this.user_name + "\n");
		sb.append("访问时间为：" + this.time + "\n");
		sb.append("访问的url为：" + this.request_url + "\n");
		sb.append("返回的状态码为：" + this.http_status + "\n");
		sb.append("访问的字节数为：" + this.boby_bytes_count);
		return sb.toString();
	}
	
	
	public String getUser_ip() {
		return user_ip;
	}


	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRequest_url() {
		return request_url;
	}

	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}


	public String getHttp_status() {
		return http_status;
	}

	public void setHttp_status(String http_status) {
		this.http_status = http_status;
	}

	public String getBoby_bytes_count() {
		return boby_bytes_count;
	}

	public void setBoby_bytes_count(String boby_bytes_count) {
		this.boby_bytes_count = boby_bytes_count;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	 public static void main(String[] args) {
		String log = "127.0.0.1 - - [25/Nov/2011:00:30:08 +0800] \"GET /index.php?img=gifLogo HTTP/1.1\" 200 4549";
		LogEntity le = new LogEntity();
		LogEntity le2 = new LogEntity();
		le2 = le.parser(log);
		System.out.println(le2);
	}

}
