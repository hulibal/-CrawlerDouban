package DataModel;

import java.util.concurrent.*;

public class WorkingQueue {
	
	private LinkedBlockingDeque<String> que;
	int max = 50;

	public WorkingQueue() {
		que = new LinkedBlockingDeque<String>();
	}
	
	public WorkingQueue(int max) {
		this.max =max;
		que = new LinkedBlockingDeque<String>(max);
	}
	
	@SuppressWarnings("finally")
	public String takeURL(){
		String re = null;
		try {
			re = que.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			return re;			
		}
	}
	public boolean contains(String url){
		return que.contains(url);
	}
	public int size(){
		return que.size();
	}
	public void putURL(String url){
		try {
			que.put(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
