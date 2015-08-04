package MutiThread;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.regex.*;

import DataModel.WaitingParse;

public class parseHtml implements Runnable {

	private WaitingParse waitingParse;

	public parseHtml(WaitingParse waitingParse) {
		this.waitingParse = waitingParse;
	}

	@Override
	public void run() {
		while (true) {
			String URL = waitingParse.takeURL();
			System.out.println("【" + Thread.currentThread().getName() + "】"
					+ "开始解析目标链接:" + URL);
			System.out.println("【" + Thread.currentThread().getName() + "】"
					+ "还未解析的目标链接数:" + waitingParse.size());
			switch (matchURL(URL)) {
			case 1:// 电影
				parseMovie(URL);
				break;
			case 2:
				try {
					parseBook(URL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					parseMusic(URL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 4:
				parseCelebrity(URL);
				break;
			default:
				break;
			}
		}
	}
	
	public void parseCelebrity(String url){
		
	}

	public void parseMovie(String url) {
		String htmlContent = getHtmlSource(url);
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// 匹配title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += "【" + mtitle.group().trim().replace(" (豆瓣)", "") + "】";
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// 匹配评分
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += "(" + mtitle.group().trim() + ")";
		}
		File directory = new File("E:\\douban\\movie\\" + score + name.replaceAll("[\\/:*?\"<>|]+", "#")  + "\\");// 创建文件夹
		if (directory.exists()) return;
		directory.mkdirs();
		System.out.println("【" + Thread.currentThread().getName() + "】"
					+ "创建文件夹:" + name);
			
		// 下载海报
		pTitle = Pattern
				.compile("(?<=movie_poster_cover/spst/public/)[0-9p]+(?=.jpg)");// 匹配图片种子
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/view/photo/photo/public/"
					+ mtitle.group() + ".jpg";
			downloadPic(picurl, directory, name);
		}
		// 下载基本信息
		File baseInfo = new File(directory, "基本信息.txt");
		if (!baseInfo.exists()) {
			try {
				baseInfo.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						baseInfo, true));
				pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// 匹配title
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write(mtitle.group().trim() + "\n");// 写入电影名
				}
				writer.write(url + "\n");// 写入连接
				// 影评评价
				writer.write("\n######---影片评价---#####\n");
				pTitle = Pattern
						.compile("(?<=v:average\">)[0-9.]+(?=</strong>)");// 匹配评分
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("分数:" + mtitle.group().trim() + "\n");// 写入分数
				}
				pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// 匹配打分人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("打分人数:" + mtitle.group().trim() + "\n");// 写入打分人数
				}
				pTitle = Pattern.compile("(?<=title=\"力荐\">).*?(?=<br)");// 打五星人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("★★★★★:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// 写入打五星比率
				}
				pTitle = Pattern.compile("(?<=title=\"推荐\">).*?(?=<br)");// 打四星人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("★★★★☆:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// 写入打四星比率
				}
				pTitle = Pattern.compile("(?<=title=\"还行\">).*?(?=<br)");// 打三星人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("★★★☆☆:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// 写入打三星比率
				}
				pTitle = Pattern.compile("(?<=title=\"较差\">).*?(?=<br)");// 打2星人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("★★☆☆☆:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// 写入打2星比率
				}
				pTitle = Pattern.compile("(?<=title=\"很差\">).*?(?=<br)");// 打1星人数
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("★☆☆☆☆:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// 写入打1星比率
				}

				writer.write("\n######---影片基本信息---######\n");
				pTitle = Pattern.compile("(?<=<div id=\"info\">).*?(?=</div>)");// 匹配基本信息
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					String info = mtitle.group();
					String[] arr = info.split("<br/>|<br />|<br>");
					for (int i = 0; i < arr.length; i++)
						writer.write(arr[i].replaceAll("<.*?>", "").trim()
								+ "\n");// 写入基本信息
				}

				writer.write("\n######---剧情简介---######\n");
				pTitle = Pattern.compile("(?<=all hidden\">).*?(?=</span>)");// 匹配剧情有hidden情况
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					String summary = mtitle.group().trim();
					String[] sums = summary.split("<br/>|<br />|<br>");
					for (int i = 0; i < sums.length; i++)
						writer.write("  " + sums[i].trim() + "\n");// 写入剧情
				} else {
					pTitle = Pattern
							.compile("(?<=v:summary\">|v:summary\" class=\"\">).*?(?=</span>)");// 匹配剧情没有hidden情况
					mtitle = pTitle.matcher(htmlContent);
					if (mtitle.find()) {
						String summary = mtitle.group().trim();
						String[] sums = summary.split("<.*?>");
						for (int i = 0; i < sums.length; i++)
							writer.write("  " + sums[i].trim() + "\n");// 写入剧情
					}
				}
				writer.close();
				System.out.println("【" + Thread.currentThread().getName() + "】"
						+ "下载基本信息成功:" + name);

				// 下载评论信息
				File Comments = new File(directory, "短评.txt");
				if (!Comments.exists()) {
					Comments.createNewFile();
					FileWriter wr = new FileWriter(Comments, true);

					String comments = url + "comments/";// 短评地址
					wr.write("#####--- 短评地址:" + comments + " ---#####\n\n");
					htmlContent = getHtmlSource(comments);
					pTitle = Pattern
							.compile("(?<= <div class=\"comment\">).*?(?=</div>)");// 匹配评论
					mtitle = pTitle.matcher(htmlContent);
					while (mtitle.find()) {// 对每条短评做处理
						String tmp = mtitle.group().trim();
						Pattern p = Pattern
								.compile("(?<=class=\"\">).*?(?=</a>)");// 评论人
						Matcher m = p.matcher(tmp);
						if (m.find()) {
							wr.write("评论人:" + m.group().trim());
						}
						p = Pattern.compile("(?<=title=\").*?(?=\">)");// 评价
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  评价:" + m.group().trim());
						}
						p = Pattern
								.compile("(?<=class=\"votes pr5\">).*?(?=</span>)");// 有用数
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  有用数:" + m.group().trim() + "\n");
						}
						p = Pattern.compile("(?<=<p class=\"\">).*?(?=</p>)");// 短评
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("评论:  "
									+ m.group().replaceAll("<.*>", "").trim()
									+ "\n\n");
						}
					}
					System.out.println("【" + Thread.currentThread().getName()
							+ "】" + "下载短评成功:" + name);
					wr.close();
				}

				File reviewsFile = new File(directory, "长影评.txt");
				if (!reviewsFile.exists()) {
					reviewsFile.createNewFile();
					FileWriter wr = new FileWriter(reviewsFile, true);
					String reviews = url + "reviews/";// 长评地址
					wr.write("#####--- 长评地址:" + reviews + " ---#####\n\n");
					htmlContent = getHtmlSource(reviews);
					pTitle = Pattern
							.compile("(?<=<div class=\"review\">).*?(?=<div class=\"review-short-ft\">)");// 匹配长评
					mtitle = pTitle.matcher(htmlContent);
					while (mtitle.find()) {// 对每长评做处理
						String tmp = mtitle
								.group()
								.replaceAll(
										"(?<=<div class=\"review-hd\">).*?(?=</div>)",
										"").trim();// 处理开头冗余部分
						Pattern p = Pattern
								.compile("(?<=class=\"\">).*?(?=</a>)");// 评论标题和评价者
						Matcher m = p.matcher(tmp);
						if (m.find()) {
							wr.write("影评:" + m.group().trim() + "\n");
						}
						if (m.find()) {
							wr.write("评价者:" + m.group().trim());
						}
						p = Pattern.compile("(?<=href=\")[0-9a-z:/.]+");// 评论网址
						m = p.matcher(tmp);
						String reurl = "";
						if (m.find()) {
							reurl = m.group().trim();// 记下网址
						}
						tmp = tmp.replaceAll(
								"(?<=<div class=\"review-hd\">).*?(?=</h3>)",
								"");
						p = Pattern.compile("(?<=title=\").*?(?=\"></span>)");// 评论
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  评价:" + m.group().trim() + "\n");
							wr.write(reurl + "\n");// 写入地址
						}
						p = Pattern
								.compile("(?<=<span class=\"\">).*?(?=</span>)");// 正文摘要
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write(m.group().trim() + "\n\n");
						}
					}
					System.out.println("【" + Thread.currentThread().getName()
							+ "】" + "下载长影评成功:" + name);
					wr.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void parseBook(String url) throws Exception {
		String htmlContent = getHtmlSource(url);
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// 匹配title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += mtitle.group().trim().replace(" (豆瓣)", "");
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// 匹配评分
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += mtitle.group().trim();
		}
		File directory = new File("E:\\douban\\book\\" +"("+ score + ")"+ "【" + name.replaceAll("[\\/:*?\"<>|]+", "#") + "】"
				 + "\\");// 创建文件夹
		if (directory.exists())	return;
		directory.mkdirs();
		System.out.println("【" + Thread.currentThread().getName() + "】"
				+ "创建文件夹:" + "【" + name + "】(" + score + ")");

		// 下载封面
		pTitle = Pattern.compile("(?<=mpic/).*?(?=.jpg)");// 匹配图片种子
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/lpic/" + mtitle.group()
					+ ".jpg";
			downloadPic(picurl, directory, name);
		}
		
		//下载基本信息
		File baseInfo = new File(directory,"图书基本信息.txt");
		if(baseInfo.exists()) return;
		FileWriter writer = new FileWriter(baseInfo,true);
		baseInfo.createNewFile();
		writer.write(name+"\n"+url+"\n\n");
		pTitle = Pattern.compile("(?<=<div id=\"info\" class=\"\">).*?(?=</div>)");// 匹配基本信息
		mtitle = pTitle.matcher(htmlContent);
		writer.write("#####---基本信息---#####\n");
		if (mtitle.find()) {
			String baseinfo = mtitle.group().trim();
			String[] arr = baseinfo.split("<br>|<br/>|<br />|</br>");
			for(String s:arr){
				s = s.replaceAll("<.*?>|&nbsp", "").trim();
				s = s.replaceAll("[ \n]*", "");
				writer.write(s+"\n");
			}
		}
		writer.write("\n######---图书评价---#####\n");
		writer.write("分数:" + score + "\n");// 写入分数
		pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// 匹配打分人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("打分人数:" + mtitle.group().trim() + "\n");// 写入打分人数
		}
		pTitle = Pattern.compile("(?<=title=\"力荐\">).*?(?=<br)");// 打五星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★★★:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打五星比率
		}
		pTitle = Pattern.compile("(?<=title=\"推荐\">).*?(?=<br)");// 打四星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★★☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打四星比率
		}
		pTitle = Pattern.compile("(?<=title=\"还行\">).*?(?=<br)");// 打三星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打三星比率
		}
		pTitle = Pattern.compile("(?<=title=\"较差\">).*?(?=<br)");// 打2星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★☆☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打2星比率
		}
		pTitle = Pattern.compile("(?<=title=\"很差\">).*?(?=<br)");// 打1星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★☆☆☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打1星比率
		}
		
		//下载简介
		writer.write("\n#####---内容简介---#####\n");
		pTitle = Pattern.compile("(?<=内容简介).*?(?=</div>)");// 匹配简介
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String tmp = mtitle.group().trim();
			Pattern p = Pattern.compile("(?<=<div class=\"intro\">).*(?=</p)");
			Matcher m = p.matcher(tmp);
			if(m.find()){
				String content = m.group();
				String[] arr = content.split("</p>");
				for(String s:arr){
					s = s.replaceAll("<.*?>", "").trim();
					writer.write(s+"\n");
				}
			}
		}
		
		writer.write("\n#####---作者简介---#####\n");
		pTitle = Pattern.compile("(?<=作者简介).*?(?=</div>)");// 匹配简介
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String tmp = mtitle.group().trim();
			Pattern p = Pattern.compile("(?<=<div class=\"intro\">).*(?=</p)");
			Matcher m = p.matcher(tmp);
			if(m.find()){
				String content = m.group();
				String[] arr = content.split("</p>");
				for(String s:arr){
					s = s.replaceAll("<.*?>", "").trim();
					writer.write(s+"\n");
				}
			}
		}
		
		writer.write("\n#####---目录---#####\n");
		pTitle = Pattern.compile("(?<=<div class=\"indent\" id=\"dir).*?(?=</div>)");// 匹配简介
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String tmp = mtitle.group().replaceFirst(".*?>", "").trim();			
			String[] arr = tmp.split("<br/>|<br />");
			for(String s:arr){
				s = s.replaceAll("<.*?>", "").trim();
				writer.write(s+"\n");
			}
		}
		writer.close();
		System.out.println("【" + Thread.currentThread().getName() + "】"
				+ "下载基本信息成功:" + name);
		
		// 下载评论信息
		File Comments = new File(directory, "短评.txt");
		if (!Comments.exists()) {
			Comments.createNewFile();
			FileWriter wr = new FileWriter(Comments, true);

			String comments = url + "comments/";// 短评地址
			wr.write("#####--- 短评地址:" + comments + " ---#####\n\n");
			htmlContent = getHtmlSource(comments);
			pTitle = Pattern
					.compile("(?<=<li class=\"comment-item\">).*?(?=</li>)");// 匹配评论
			mtitle = pTitle.matcher(htmlContent);
			while (mtitle.find()) {// 对每条短评做处理
				String tmp = mtitle.group().trim();
				Pattern p = Pattern
						.compile("(?<=title=\").*?(?=\")");// 评论人
				Matcher m = p.matcher(tmp);
				if (m.find()) {
					wr.write("评论人:" + m.group().trim());
				}
				p = Pattern.compile("力荐|推荐|还行|较差|很差");// 评价
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  评价:" + m.group().trim());
				}
				p = Pattern
						.compile("(?<=vote-count\">).*?(?=</span>)");// 有用数
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  有用数:" + m.group().trim() + "\n");
				}
				p = Pattern.compile("(?<=<p class=\"comment-content\">).*?(?=</p>)");// 短评
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("评论:  "
							+ m.group().replaceAll("<.*>", "").trim()
							+ "\n\n");
				}
			}
			System.out.println("【" + Thread.currentThread().getName()
					+ "】" + "下载短评成功:" + name);
			wr.close();
		}		
	}

	public void parseMusic(String url) throws Exception {
		String htmlContent = getHtmlSource(url);
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// 匹配title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += mtitle.group().trim().replace(" (豆瓣)", "");
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// 匹配评分
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += mtitle.group().trim();
		}
		File directory = new File("E:\\douban\\music\\" +"("+ score + ")"+ "【" + name.replaceAll("[\\/:*?\"<>|]+", "#") + "】"
				+ "\\");// 创建文件夹
		if (directory.exists())	return;
		directory.mkdirs();
		System.out.println("【" + Thread.currentThread().getName() + "】"
				+ "创建文件夹:" + "【" + name + "】(" + score + ")");

		// 下载封面
		pTitle = Pattern.compile("(?<=mpic/).*?(?=.jpg)");// 匹配图片种子
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/lpic/" + mtitle.group()
					+ ".jpg";
			downloadPic(picurl, directory, name);
		}
		
		//下载基本信息
		File baseInfo = new File(directory,"唱片基本信息.txt");
		if(baseInfo.exists()) return;
		FileWriter writer = new FileWriter(baseInfo,true);
		baseInfo.createNewFile();
		writer.write(name+"\n"+url+"\n\n");
		pTitle = Pattern.compile("(?<=<div id=\"info\" class=\"ckd-collect\">).*?(?=</div>)");// 匹配基本信息
		mtitle = pTitle.matcher(htmlContent);
		writer.write("#####---基本信息---#####\n");
		if (mtitle.find()) {
			String baseinfo = mtitle.group().trim();
			String[] arr = baseinfo.split("<br>|<br/>|<br />|</br>");
			for(String s:arr){
				s = s.replaceAll("<.*?>|&nbsp", "").trim();
				s = s.replaceAll("[ \n]*", "");
				writer.write(s+"\n");
			}
		}
		writer.write("\n######---唱片评价---#####\n");
		writer.write("分数:" + score + "\n");// 写入分数
		pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// 匹配打分人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("打分人数:" + mtitle.group().trim() + "\n");// 写入打分人数
		}
		pTitle = Pattern.compile("(?<=title=\"力荐\">).*?(?=<br)");// 打五星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★★★:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打五星比率
		}
		pTitle = Pattern.compile("(?<=title=\"推荐\">).*?(?=<br)");// 打四星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★★☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打四星比率
		}
		pTitle = Pattern.compile("(?<=title=\"还行\">).*?(?=<br)");// 打三星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★★☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打三星比率
		}
		pTitle = Pattern.compile("(?<=title=\"较差\">).*?(?=<br)");// 打2星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★★☆☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打2星比率
		}
		pTitle = Pattern.compile("(?<=title=\"很差\">).*?(?=<br)");// 打1星人数
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("★☆☆☆☆:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// 写入打1星比率
		}
		
		//下载简介
		writer.write("\n#####---简介---#####\n");
		pTitle = Pattern.compile("(?<=all hidden\">).*?(?=</span>)");// 匹配简介有hidden情况
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String summary = mtitle.group().trim();
			String[] sums = summary.split("<br/>|<br />|<br>");
			for (int i = 0; i < sums.length; i++)
				writer.write("  " + sums[i].trim() + "\n");// 写入简介
		} else {
			pTitle = Pattern
					.compile("(?<=v:summary\">|v:summary\">).*?(?=</span>)");// 匹配简介没有hidden情况
			mtitle = pTitle.matcher(htmlContent);
			if (mtitle.find()) {
				String summary = mtitle.group().trim();
				String[] sums = summary.split("<br/>|<br />|<br>");
				for (int i = 0; i < sums.length; i++)
					writer.write("  " + sums[i].trim() + "\n");// 写入简介
			}
		}		
		
		writer.write("\n#####---曲目---#####\n");
		pTitle = Pattern.compile("(?<=<li class=\"song-item\").*?(?=</span>)");// 匹配曲目
		mtitle = pTitle.matcher(htmlContent);
		int count = 1;
		while(mtitle.find()) {
			String tmp = mtitle.group().replaceFirst(".*?>", "").trim();			
			Pattern p = Pattern.compile("(?<=data-title=\").*?(?=\">)");//歌名
			Matcher m = p.matcher(tmp);
			if(m.find()){
				writer.write(count+":"+m.group().trim());
			}
			p = Pattern.compile("[0-9]+(?=歌单)");//歌单数
			m = p.matcher(tmp);
			if(m.find()){
				writer.write("   歌单数:"+m.group().trim()+"\n");
			}
			count++;
		}
		writer.close();
		System.out.println("【" + Thread.currentThread().getName() + "】"
				+ "下载基本信息成功:" + name);
		
		// 下载评论信息
		File Comments = new File(directory, "短评.txt");
		if (!Comments.exists()) {
			Comments.createNewFile();
			FileWriter wr = new FileWriter(Comments, true);

			String comments = url + "comments/";// 短评地址
			wr.write("#####--- 短评地址:" + comments + " ---#####\n\n");
			htmlContent = getHtmlSource(comments);
			pTitle = Pattern
					.compile("(?<=<li class=\"comment-item\">).*?(?=</li>)");// 匹配评论
			mtitle = pTitle.matcher(htmlContent);
			while (mtitle.find()) {// 对每条短评做处理
				String tmp = mtitle.group().trim();
				Pattern p = Pattern
						.compile("(?<=title=\").*?(?=\")");// 评论人
				Matcher m = p.matcher(tmp);
				if (m.find()) {
					wr.write("评论人:" + m.group().trim());
				}
				p = Pattern.compile("力荐|推荐|还行|较差|很差");// 评价
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  评价:" + m.group().trim());
				}
				p = Pattern
						.compile("(?<=vote-count\">).*?(?=</span>)");// 有用数
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  有用数:" + m.group().trim() + "\n");
				}
				p = Pattern.compile("(?<=<p class=\"comment-content\">).*?(?=</p>)");// 短评
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("评论:  "
							+ m.group().replaceAll("<.*>", "").trim()
							+ "\n\n");
				}
			}
			System.out.println("【" + Thread.currentThread().getName()
					+ "】" + "下载短评成功:" + name);
			wr.close();
		}		
	}
	
	public void downloadPic(String surl, File parent, String name) {
		try {
			File outFile = new File(parent, name.replaceAll("[\\/:*?\"<>|]+", "#") + ".jpg");
			if(outFile.exists()) return;
			int sleepTime = new Random().nextInt(3000) + 1000;
			System.out.println("下载图片"+name+"之前【"+Thread.currentThread().getName()+"】先沉睡"+sleepTime+"毫秒");
			Thread.sleep(sleepTime);
			URL url = new URL(surl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
			connection.setRequestProperty("Cookie", "bid=\"JrY/ZTrtQos\"; __utma=30149280.328126321.1386247598.1426729578.1429614479.76; __utmz=30149280.1417762892.71.14.utmcsr=baidu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.5293; viewed=\"26315791_20443850_26276802_26272194_20515891_7564480_4708781_25762738_25765123_21349359\"; ll=\"108306\"; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1431003494%2C%22http%3A%2F%2Fmusic.douban.com%2Fsubject%2F20443850%2F%22%5D; _pk_id.100001.8cb4=9a2322a426f4b49d.1403253226.12.1431003509.1429620892.; _pk_ses.100001.8cb4=*");
			OutputStream os = new FileOutputStream(outFile);
			InputStream is = connection.getInputStream();
			byte[] buff = new byte[1024];
			while (true) {
				int readed = is.read(buff, 0, 1024);
				if (readed == -1) {
					break;
				}
//				byte[] temp = new byte[readed];
//				System.arraycopy(buff, 0, temp, 0, readed);
				os.write(buff,0,readed);
			}
			System.out.println("【" + Thread.currentThread().getName() + "】"
					+ "下载海报成功:" + parent.getAbsolutePath());
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public int matchURL(String url) {
		if (url.contains("movie"))
			return 1;
		if (url.contains("book"))
			return 2;
		if (url.contains("music"))
			return 3;
		if (url.contains("celebrity"))
			return 4;
		return 0;
	}

	public String getHtmlSource(String HtmlUrl) {
		URL tempUrl;
		StringBuffer tempStore = new StringBuffer();
		try {
			int sleepTime = new Random().nextInt(3000) + 1000;
			System.out.println("下载网页内容之前【"+Thread.currentThread().getName()+"】先沉睡"+sleepTime+"毫秒");
			Thread.sleep(500l);
			tempUrl = new URL(HtmlUrl);
			HttpURLConnection connection = (HttpURLConnection) tempUrl
					.openConnection();
			connection.setRequestMethod("GET");
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
			String temp;
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
