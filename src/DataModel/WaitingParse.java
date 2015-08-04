package DataModel;

import java.util.concurrent.LinkedBlockingDeque;

public class WaitingParse {
	
	private LinkedBlockingDeque<String> que;
	private int max = 20;
	
	public WaitingParse() {
		que = new LinkedBlockingDeque<String>(max);
	}
	
	public int size(){
		return que.size();		
	}
	
	public boolean contains(String url){
		return que.contains(url);
	}

	public WaitingParse(int max) {
		this.max = max;
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
	
	public void putURL(String url){
		try {
			que.put(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
