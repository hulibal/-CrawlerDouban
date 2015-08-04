package MutiThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DataModel.*;

public class extractor implements Runnable{
	
	private VisitedURLs VisitedURLs;
	private WorkingQueue WorkingQue;
	private WaitingParse waitingParse;
	
	public extractor(VisitedURLs VisitedURLs,WorkingQueue WorkingQue,WaitingParse waitingParse) {
		this.VisitedURLs = VisitedURLs;
		this.waitingParse = waitingParse;
		this.WorkingQue = WorkingQue;
	}
	
	@Override
	public void run() {
		while(true){
			String url = WorkingQue.takeURL();
			VisitedURLs.writeURL(url);
			System.out.println("【"+Thread.currentThread().getName()+"】"+"处理链接:"+url);
			System.out.println("未处理连接数:"+WorkingQue.size());
			System.out.println("已处理连接数:"+VisitedURLs.size());
			String htmlContent = getHtmlSource(url);
			Pattern pURL = Pattern.compile("(?<=href=\")http:[?a-z/.0-9]*(?=\")");
			Matcher rURL = pURL.matcher(htmlContent);
			while(rURL.find()){
				String tempURL = rURL.group();
				Pattern sURL = Pattern.compile("(?<=subject/)[0-9/]+$");
				Matcher mURL = sURL.matcher(tempURL);
				if(mURL.find()&&(!VisitedURLs.containsURL(tempURL)||!waitingParse.contains(tempURL)||!WorkingQue.contains(tempURL))){
					System.out.println("【"+Thread.currentThread().getName()+"】"+"找到目标链接:"+tempURL);
					waitingParse.putURL(tempURL);
				}
				if(!VisitedURLs.containsURL(tempURL)&&!WorkingQue.contains(tempURL)){
					System.out.println("【"+Thread.currentThread().getName()+"】"+"链接入工作队列:"+tempURL);
					WorkingQue.putURL(tempURL);
				}else if(VisitedURLs.containsURL(tempURL)){
					System.out.println("【"+Thread.currentThread().getName()+"】"+"链接已处理不入工作队列:"+tempURL);
				}else if(WorkingQue.contains(tempURL)){
					System.out.println("【"+Thread.currentThread().getName()+"】"+"链接已入工作队列不处理:"+tempURL);
				}
			}
		}
	}
	
	public String getHtmlSource(String HtmlUrl) {
		URL tempUrl;		
		StringBuffer tempStore = new StringBuffer();
		try {
			int sleepTime = new Random().nextInt(3000) + 1000;
			System.out.println("下载网页内容之前【"+Thread.currentThread().getName()+"】先沉睡"+sleepTime+"毫秒");
			Thread.sleep(sleepTime);
			tempUrl = new URL(HtmlUrl);
			HttpURLConnection connection = (HttpURLConnection) tempUrl
					.openConnection();			
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(200000);
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");// 设置代理
			connection.setRequestProperty("Cookie", "bid=\"JrY/ZTrtQos\"; __utma=30149280.328126321.1386247598.1426729578.1429614479.76; __utmz=30149280.1417762892.71.14.utmcsr=baidu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.5293; viewed=\"26315791_20443850_26276802_26272194_20515891_7564480_4708781_25762738_25765123_21349359\"; ll=\"108306\"; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1431003494%2C%22http%3A%2F%2Fmusic.douban.com%2Fsubject%2F20443850%2F%22%5D; _pk_id.100001.8cb4=9a2322a426f4b49d.1403253226.12.1431003509.1429620892.; _pk_ses.100001.8cb4=*");
			connection.connect();
			
			int responseCode = connection.getResponseCode();
			if(HttpURLConnection.HTTP_FORBIDDEN==responseCode){
				System.out.println("\n【"+Thread.currentThread().getName()+"】服务器发来不友好的的东东了:"+responseCode);
				Thread.sleep(60000l);
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));// 读取网页全部内容
			String temp = "";
			while ((temp = in.readLine()) != null) {
				tempStore.append(temp);
			}
			connection.disconnect();
			in.close();
		} catch (MalformedURLException e) {
			System.out.println("URL格式有问题!");
		} catch (IOException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		return tempStore.toString();
	}

}
