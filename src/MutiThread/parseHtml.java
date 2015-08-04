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
			System.out.println("��" + Thread.currentThread().getName() + "��"
					+ "��ʼ����Ŀ������:" + URL);
			System.out.println("��" + Thread.currentThread().getName() + "��"
					+ "��δ������Ŀ��������:" + waitingParse.size());
			switch (matchURL(URL)) {
			case 1:// ��Ӱ
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
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// ƥ��title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += "��" + mtitle.group().trim().replace(" (����)", "") + "��";
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// ƥ������
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += "(" + mtitle.group().trim() + ")";
		}
		File directory = new File("E:\\douban\\movie\\" + score + name.replaceAll("[\\/:*?\"<>|]+", "#")  + "\\");// �����ļ���
		if (directory.exists()) return;
		directory.mkdirs();
		System.out.println("��" + Thread.currentThread().getName() + "��"
					+ "�����ļ���:" + name);
			
		// ���غ���
		pTitle = Pattern
				.compile("(?<=movie_poster_cover/spst/public/)[0-9p]+(?=.jpg)");// ƥ��ͼƬ����
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/view/photo/photo/public/"
					+ mtitle.group() + ".jpg";
			downloadPic(picurl, directory, name);
		}
		// ���ػ�����Ϣ
		File baseInfo = new File(directory, "������Ϣ.txt");
		if (!baseInfo.exists()) {
			try {
				baseInfo.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						baseInfo, true));
				pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// ƥ��title
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write(mtitle.group().trim() + "\n");// д���Ӱ��
				}
				writer.write(url + "\n");// д������
				// Ӱ������
				writer.write("\n######---ӰƬ����---#####\n");
				pTitle = Pattern
						.compile("(?<=v:average\">)[0-9.]+(?=</strong>)");// ƥ������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("����:" + mtitle.group().trim() + "\n");// д�����
				}
				pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// ƥ��������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("�������:" + mtitle.group().trim() + "\n");// д��������
				}
				pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("������:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// д������Ǳ���
				}
				pTitle = Pattern.compile("(?<=title=\"�Ƽ�\">).*?(?=<br)");// ����������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("������:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// д������Ǳ���
				}
				pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("������:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// д������Ǳ���
				}
				pTitle = Pattern.compile("(?<=title=\"�ϲ�\">).*?(?=<br)");// ��2������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("������:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// д���2�Ǳ���
				}
				pTitle = Pattern.compile("(?<=title=\"�ܲ�\">).*?(?=<br)");// ��1������
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					writer.write("������:"
							+ mtitle.group().replaceAll("<.*?>", "").trim()
							+ "\n");// д���1�Ǳ���
				}

				writer.write("\n######---ӰƬ������Ϣ---######\n");
				pTitle = Pattern.compile("(?<=<div id=\"info\">).*?(?=</div>)");// ƥ�������Ϣ
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					String info = mtitle.group();
					String[] arr = info.split("<br/>|<br />|<br>");
					for (int i = 0; i < arr.length; i++)
						writer.write(arr[i].replaceAll("<.*?>", "").trim()
								+ "\n");// д�������Ϣ
				}

				writer.write("\n######---������---######\n");
				pTitle = Pattern.compile("(?<=all hidden\">).*?(?=</span>)");// ƥ�������hidden���
				mtitle = pTitle.matcher(htmlContent);
				if (mtitle.find()) {
					String summary = mtitle.group().trim();
					String[] sums = summary.split("<br/>|<br />|<br>");
					for (int i = 0; i < sums.length; i++)
						writer.write("  " + sums[i].trim() + "\n");// д�����
				} else {
					pTitle = Pattern
							.compile("(?<=v:summary\">|v:summary\" class=\"\">).*?(?=</span>)");// ƥ�����û��hidden���
					mtitle = pTitle.matcher(htmlContent);
					if (mtitle.find()) {
						String summary = mtitle.group().trim();
						String[] sums = summary.split("<.*?>");
						for (int i = 0; i < sums.length; i++)
							writer.write("  " + sums[i].trim() + "\n");// д�����
					}
				}
				writer.close();
				System.out.println("��" + Thread.currentThread().getName() + "��"
						+ "���ػ�����Ϣ�ɹ�:" + name);

				// ����������Ϣ
				File Comments = new File(directory, "����.txt");
				if (!Comments.exists()) {
					Comments.createNewFile();
					FileWriter wr = new FileWriter(Comments, true);

					String comments = url + "comments/";// ������ַ
					wr.write("#####--- ������ַ:" + comments + " ---#####\n\n");
					htmlContent = getHtmlSource(comments);
					pTitle = Pattern
							.compile("(?<= <div class=\"comment\">).*?(?=</div>)");// ƥ������
					mtitle = pTitle.matcher(htmlContent);
					while (mtitle.find()) {// ��ÿ������������
						String tmp = mtitle.group().trim();
						Pattern p = Pattern
								.compile("(?<=class=\"\">).*?(?=</a>)");// ������
						Matcher m = p.matcher(tmp);
						if (m.find()) {
							wr.write("������:" + m.group().trim());
						}
						p = Pattern.compile("(?<=title=\").*?(?=\">)");// ����
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  ����:" + m.group().trim());
						}
						p = Pattern
								.compile("(?<=class=\"votes pr5\">).*?(?=</span>)");// ������
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  ������:" + m.group().trim() + "\n");
						}
						p = Pattern.compile("(?<=<p class=\"\">).*?(?=</p>)");// ����
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("����:  "
									+ m.group().replaceAll("<.*>", "").trim()
									+ "\n\n");
						}
					}
					System.out.println("��" + Thread.currentThread().getName()
							+ "��" + "���ض����ɹ�:" + name);
					wr.close();
				}

				File reviewsFile = new File(directory, "��Ӱ��.txt");
				if (!reviewsFile.exists()) {
					reviewsFile.createNewFile();
					FileWriter wr = new FileWriter(reviewsFile, true);
					String reviews = url + "reviews/";// ������ַ
					wr.write("#####--- ������ַ:" + reviews + " ---#####\n\n");
					htmlContent = getHtmlSource(reviews);
					pTitle = Pattern
							.compile("(?<=<div class=\"review\">).*?(?=<div class=\"review-short-ft\">)");// ƥ�䳤��
					mtitle = pTitle.matcher(htmlContent);
					while (mtitle.find()) {// ��ÿ����������
						String tmp = mtitle
								.group()
								.replaceAll(
										"(?<=<div class=\"review-hd\">).*?(?=</div>)",
										"").trim();// ����ͷ���ಿ��
						Pattern p = Pattern
								.compile("(?<=class=\"\">).*?(?=</a>)");// ���۱����������
						Matcher m = p.matcher(tmp);
						if (m.find()) {
							wr.write("Ӱ��:" + m.group().trim() + "\n");
						}
						if (m.find()) {
							wr.write("������:" + m.group().trim());
						}
						p = Pattern.compile("(?<=href=\")[0-9a-z:/.]+");// ������ַ
						m = p.matcher(tmp);
						String reurl = "";
						if (m.find()) {
							reurl = m.group().trim();// ������ַ
						}
						tmp = tmp.replaceAll(
								"(?<=<div class=\"review-hd\">).*?(?=</h3>)",
								"");
						p = Pattern.compile("(?<=title=\").*?(?=\"></span>)");// ����
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write("  ����:" + m.group().trim() + "\n");
							wr.write(reurl + "\n");// д���ַ
						}
						p = Pattern
								.compile("(?<=<span class=\"\">).*?(?=</span>)");// ����ժҪ
						m = p.matcher(tmp);
						if (m.find()) {
							wr.write(m.group().trim() + "\n\n");
						}
					}
					System.out.println("��" + Thread.currentThread().getName()
							+ "��" + "���س�Ӱ���ɹ�:" + name);
					wr.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void parseBook(String url) throws Exception {
		String htmlContent = getHtmlSource(url);
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// ƥ��title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += mtitle.group().trim().replace(" (����)", "");
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// ƥ������
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += mtitle.group().trim();
		}
		File directory = new File("E:\\douban\\book\\" +"("+ score + ")"+ "��" + name.replaceAll("[\\/:*?\"<>|]+", "#") + "��"
				 + "\\");// �����ļ���
		if (directory.exists())	return;
		directory.mkdirs();
		System.out.println("��" + Thread.currentThread().getName() + "��"
				+ "�����ļ���:" + "��" + name + "��(" + score + ")");

		// ���ط���
		pTitle = Pattern.compile("(?<=mpic/).*?(?=.jpg)");// ƥ��ͼƬ����
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/lpic/" + mtitle.group()
					+ ".jpg";
			downloadPic(picurl, directory, name);
		}
		
		//���ػ�����Ϣ
		File baseInfo = new File(directory,"ͼ�������Ϣ.txt");
		if(baseInfo.exists()) return;
		FileWriter writer = new FileWriter(baseInfo,true);
		baseInfo.createNewFile();
		writer.write(name+"\n"+url+"\n\n");
		pTitle = Pattern.compile("(?<=<div id=\"info\" class=\"\">).*?(?=</div>)");// ƥ�������Ϣ
		mtitle = pTitle.matcher(htmlContent);
		writer.write("#####---������Ϣ---#####\n");
		if (mtitle.find()) {
			String baseinfo = mtitle.group().trim();
			String[] arr = baseinfo.split("<br>|<br/>|<br />|</br>");
			for(String s:arr){
				s = s.replaceAll("<.*?>|&nbsp", "").trim();
				s = s.replaceAll("[ \n]*", "");
				writer.write(s+"\n");
			}
		}
		writer.write("\n######---ͼ������---#####\n");
		writer.write("����:" + score + "\n");// д�����
		pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// ƥ��������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("�������:" + mtitle.group().trim() + "\n");// д��������
		}
		pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�Ƽ�\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�ϲ�\">).*?(?=<br)");// ��2������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д���2�Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�ܲ�\">).*?(?=<br)");// ��1������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д���1�Ǳ���
		}
		
		//���ؼ��
		writer.write("\n#####---���ݼ��---#####\n");
		pTitle = Pattern.compile("(?<=���ݼ��).*?(?=</div>)");// ƥ����
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
		
		writer.write("\n#####---���߼��---#####\n");
		pTitle = Pattern.compile("(?<=���߼��).*?(?=</div>)");// ƥ����
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
		
		writer.write("\n#####---Ŀ¼---#####\n");
		pTitle = Pattern.compile("(?<=<div class=\"indent\" id=\"dir).*?(?=</div>)");// ƥ����
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
		System.out.println("��" + Thread.currentThread().getName() + "��"
				+ "���ػ�����Ϣ�ɹ�:" + name);
		
		// ����������Ϣ
		File Comments = new File(directory, "����.txt");
		if (!Comments.exists()) {
			Comments.createNewFile();
			FileWriter wr = new FileWriter(Comments, true);

			String comments = url + "comments/";// ������ַ
			wr.write("#####--- ������ַ:" + comments + " ---#####\n\n");
			htmlContent = getHtmlSource(comments);
			pTitle = Pattern
					.compile("(?<=<li class=\"comment-item\">).*?(?=</li>)");// ƥ������
			mtitle = pTitle.matcher(htmlContent);
			while (mtitle.find()) {// ��ÿ������������
				String tmp = mtitle.group().trim();
				Pattern p = Pattern
						.compile("(?<=title=\").*?(?=\")");// ������
				Matcher m = p.matcher(tmp);
				if (m.find()) {
					wr.write("������:" + m.group().trim());
				}
				p = Pattern.compile("����|�Ƽ�|����|�ϲ�|�ܲ�");// ����
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  ����:" + m.group().trim());
				}
				p = Pattern
						.compile("(?<=vote-count\">).*?(?=</span>)");// ������
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  ������:" + m.group().trim() + "\n");
				}
				p = Pattern.compile("(?<=<p class=\"comment-content\">).*?(?=</p>)");// ����
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("����:  "
							+ m.group().replaceAll("<.*>", "").trim()
							+ "\n\n");
				}
			}
			System.out.println("��" + Thread.currentThread().getName()
					+ "��" + "���ض����ɹ�:" + name);
			wr.close();
		}		
	}

	public void parseMusic(String url) throws Exception {
		String htmlContent = getHtmlSource(url);
		Pattern pTitle = Pattern.compile("(?<=<title>).*(?=</title>)");// ƥ��title
		Matcher mtitle = pTitle.matcher(htmlContent);
		String name = "";
		if (mtitle.find()) {
			name += mtitle.group().trim().replace(" (����)", "");
		}
		pTitle = Pattern.compile("(?<=v:average\">).*?(?=</strong>)");// ƥ������
		mtitle = pTitle.matcher(htmlContent);
		String score = "";
		if (mtitle.find()) {
			score += mtitle.group().trim();
		}
		File directory = new File("E:\\douban\\music\\" +"("+ score + ")"+ "��" + name.replaceAll("[\\/:*?\"<>|]+", "#") + "��"
				+ "\\");// �����ļ���
		if (directory.exists())	return;
		directory.mkdirs();
		System.out.println("��" + Thread.currentThread().getName() + "��"
				+ "�����ļ���:" + "��" + name + "��(" + score + ")");

		// ���ط���
		pTitle = Pattern.compile("(?<=mpic/).*?(?=.jpg)");// ƥ��ͼƬ����
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String picurl = "http://img5.douban.com/lpic/" + mtitle.group()
					+ ".jpg";
			downloadPic(picurl, directory, name);
		}
		
		//���ػ�����Ϣ
		File baseInfo = new File(directory,"��Ƭ������Ϣ.txt");
		if(baseInfo.exists()) return;
		FileWriter writer = new FileWriter(baseInfo,true);
		baseInfo.createNewFile();
		writer.write(name+"\n"+url+"\n\n");
		pTitle = Pattern.compile("(?<=<div id=\"info\" class=\"ckd-collect\">).*?(?=</div>)");// ƥ�������Ϣ
		mtitle = pTitle.matcher(htmlContent);
		writer.write("#####---������Ϣ---#####\n");
		if (mtitle.find()) {
			String baseinfo = mtitle.group().trim();
			String[] arr = baseinfo.split("<br>|<br/>|<br />|</br>");
			for(String s:arr){
				s = s.replaceAll("<.*?>|&nbsp", "").trim();
				s = s.replaceAll("[ \n]*", "");
				writer.write(s+"\n");
			}
		}
		writer.write("\n######---��Ƭ����---#####\n");
		writer.write("����:" + score + "\n");// д�����
		pTitle = Pattern.compile("(?<=\"v:votes\">)[0-9]+(?=</span>)");// ƥ��������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("�������:" + mtitle.group().trim() + "\n");// д��������
		}
		pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�Ƽ�\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"����\">).*?(?=<br)");// ����������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д������Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�ϲ�\">).*?(?=<br)");// ��2������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д���2�Ǳ���
		}
		pTitle = Pattern.compile("(?<=title=\"�ܲ�\">).*?(?=<br)");// ��1������
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			writer.write("������:"
					+ mtitle.group().replaceAll("<.*?>", "").trim()
					+ "\n");// д���1�Ǳ���
		}
		
		//���ؼ��
		writer.write("\n#####---���---#####\n");
		pTitle = Pattern.compile("(?<=all hidden\">).*?(?=</span>)");// ƥ������hidden���
		mtitle = pTitle.matcher(htmlContent);
		if (mtitle.find()) {
			String summary = mtitle.group().trim();
			String[] sums = summary.split("<br/>|<br />|<br>");
			for (int i = 0; i < sums.length; i++)
				writer.write("  " + sums[i].trim() + "\n");// д����
		} else {
			pTitle = Pattern
					.compile("(?<=v:summary\">|v:summary\">).*?(?=</span>)");// ƥ����û��hidden���
			mtitle = pTitle.matcher(htmlContent);
			if (mtitle.find()) {
				String summary = mtitle.group().trim();
				String[] sums = summary.split("<br/>|<br />|<br>");
				for (int i = 0; i < sums.length; i++)
					writer.write("  " + sums[i].trim() + "\n");// д����
			}
		}		
		
		writer.write("\n#####---��Ŀ---#####\n");
		pTitle = Pattern.compile("(?<=<li class=\"song-item\").*?(?=</span>)");// ƥ����Ŀ
		mtitle = pTitle.matcher(htmlContent);
		int count = 1;
		while(mtitle.find()) {
			String tmp = mtitle.group().replaceFirst(".*?>", "").trim();			
			Pattern p = Pattern.compile("(?<=data-title=\").*?(?=\">)");//����
			Matcher m = p.matcher(tmp);
			if(m.find()){
				writer.write(count+":"+m.group().trim());
			}
			p = Pattern.compile("[0-9]+(?=�赥)");//�赥��
			m = p.matcher(tmp);
			if(m.find()){
				writer.write("   �赥��:"+m.group().trim()+"\n");
			}
			count++;
		}
		writer.close();
		System.out.println("��" + Thread.currentThread().getName() + "��"
				+ "���ػ�����Ϣ�ɹ�:" + name);
		
		// ����������Ϣ
		File Comments = new File(directory, "����.txt");
		if (!Comments.exists()) {
			Comments.createNewFile();
			FileWriter wr = new FileWriter(Comments, true);

			String comments = url + "comments/";// ������ַ
			wr.write("#####--- ������ַ:" + comments + " ---#####\n\n");
			htmlContent = getHtmlSource(comments);
			pTitle = Pattern
					.compile("(?<=<li class=\"comment-item\">).*?(?=</li>)");// ƥ������
			mtitle = pTitle.matcher(htmlContent);
			while (mtitle.find()) {// ��ÿ������������
				String tmp = mtitle.group().trim();
				Pattern p = Pattern
						.compile("(?<=title=\").*?(?=\")");// ������
				Matcher m = p.matcher(tmp);
				if (m.find()) {
					wr.write("������:" + m.group().trim());
				}
				p = Pattern.compile("����|�Ƽ�|����|�ϲ�|�ܲ�");// ����
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  ����:" + m.group().trim());
				}
				p = Pattern
						.compile("(?<=vote-count\">).*?(?=</span>)");// ������
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("  ������:" + m.group().trim() + "\n");
				}
				p = Pattern.compile("(?<=<p class=\"comment-content\">).*?(?=</p>)");// ����
				m = p.matcher(tmp);
				if (m.find()) {
					wr.write("����:  "
							+ m.group().replaceAll("<.*>", "").trim()
							+ "\n\n");
				}
			}
			System.out.println("��" + Thread.currentThread().getName()
					+ "��" + "���ض����ɹ�:" + name);
			wr.close();
		}		
	}
	
	public void downloadPic(String surl, File parent, String name) {
		try {
			File outFile = new File(parent, name.replaceAll("[\\/:*?\"<>|]+", "#") + ".jpg");
			if(outFile.exists()) return;
			int sleepTime = new Random().nextInt(3000) + 1000;
			System.out.println("����ͼƬ"+name+"֮ǰ��"+Thread.currentThread().getName()+"���ȳ�˯"+sleepTime+"����");
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
			System.out.println("��" + Thread.currentThread().getName() + "��"
					+ "���غ����ɹ�:" + parent.getAbsolutePath());
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
			System.out.println("������ҳ����֮ǰ��"+Thread.currentThread().getName()+"���ȳ�˯"+sleepTime+"����");
			Thread.sleep(500l);
			tempUrl = new URL(HtmlUrl);
			HttpURLConnection connection = (HttpURLConnection) tempUrl
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");// ���ô���
			connection.setRequestProperty("Cookie", "bid=\"JrY/ZTrtQos\"; __utma=30149280.328126321.1386247598.1426729578.1429614479.76; __utmz=30149280.1417762892.71.14.utmcsr=baidu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.5293; viewed=\"26315791_20443850_26276802_26272194_20515891_7564480_4708781_25762738_25765123_21349359\"; ll=\"108306\"; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1431003494%2C%22http%3A%2F%2Fmusic.douban.com%2Fsubject%2F20443850%2F%22%5D; _pk_id.100001.8cb4=9a2322a426f4b49d.1403253226.12.1431003509.1429620892.; _pk_ses.100001.8cb4=*");
			connection.connect();
			
			int responseCode = connection.getResponseCode();
			if(HttpURLConnection.HTTP_FORBIDDEN==responseCode){
				System.out.println("\n��"+Thread.currentThread().getName()+"���������������ѺõĵĶ�����:"+responseCode);
				Thread.sleep(60000l);
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));// ��ȡ��ҳȫ������
			String temp;
			while ((temp = in.readLine()) != null) {
				tempStore.append(temp);
			}
			connection.disconnect();
			in.close();
		} catch (MalformedURLException e) {
			System.out.println("URL��ʽ������!");
		} catch (IOException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		return tempStore.toString();
	}
}
