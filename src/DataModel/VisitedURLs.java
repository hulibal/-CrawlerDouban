package DataModel;

import java.util.*;
import java.util.concurrent.locks.*;

public class VisitedURLs {
	
	private HashSet<String> visitedurls;
	private ReentrantReadWriteLock Lock; 

	public VisitedURLs() {
		visitedurls = new HashSet<String>();
		Lock = new ReentrantReadWriteLock();
	}
	
	public int size(){
		return visitedurls.size();		
	}
	
	public boolean containsURL(String url){
		Lock.readLock().lock();
		boolean re = visitedurls.contains(url);
		Lock.readLock().unlock();
		return re;
	}
	
	public void writeURL(String url){
		Lock.writeLock().lock();
		visitedurls.add(url);
		Lock.writeLock().unlock();
	}
	
}
