import DataModel.*;
import MutiThread.*;

public class Entrance {
	
	public static void main(String[] args) {
		VisitedURLs VUrls = new VisitedURLs();
		WorkingQueue wQue = new WorkingQueue();
		WaitingParse wParse = new WaitingParse(100);//Ŀ�����Ӷ��г���
//		wQue.putURL("http://www.sina.com.cn/");
//		wQue.putURL("http://www.163.com/");
//		wQue.putURL("http://www.qq.com/");
		wQue.putURL("http://www.douban.com/doulist/38005034/");
		wQue.putURL("http://www.douban.com/doulist/2584464/");
		wQue.putURL("http://www.douban.com/doulist/171399/");
		for(int i =1;i<=2;i++){					//��n����ȡ�߳�
			new Thread(new extractor(VUrls,wQue,wParse),"������"+i+"��").start();
		}
		for(int i =1;i<=2;i++){					//��m����ȡ�߳�
			new Thread(new parseHtml(wParse),"������"+i+"��").start();
		}
	}	
}
