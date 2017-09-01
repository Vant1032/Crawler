/**
 * Date: 2017年8月16日 下午12:35:22
 */
package crawlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

/**
 * @author Vant
 *
 */
public class ZhihuCrawler {
	public static final String zhihu = "https://www.zhihu.com";
	
	public static void main(String[] args) {
		String ctt = htmlContent(ZhihuCrawler.zhRecommendationURL);
		System.out.println(ctt);
		//寻找带有问题的链接
		Pattern pattern = Pattern.compile("<a class=\\\"question_link.+?href=\"(.*?)\".*?>(.*?)</a>");//组1是链接(不完整的)，组2是标题
		Matcher matcher = pattern.matcher(ctt);
		ArrayList<Pair<String, String>> questions = new ArrayList<>();//key是标题,value是链接
		while (matcher.find()) {
			questions.add(new Pair<String, String>(matcher.group(2), zhihu + matcher.group(1)));
		}
		System.out.println("爬取知乎推荐中     " + ZhihuCrawler.zhRecommendationURL);
		for(Pair<String, String> pair : questions) {
			System.out.println("标题    " + pair.getKey());
			System.out.println("链接    " + pair.getValue());
		}
		System.out.println("爬取知乎推荐结束");
		System.out.println("开始爬取问题");
		ArrayList<QuestionSave> saves = new ArrayList<>();
		for(Pair<String, String> pair : questions) {
			saves.add(questionCrawl(pair.getValue()));
		}
		System.out.println("爬取问题结束");
		System.out.println("开始保存");
		int saveCount = 0;//计算爬取了多少问题
		//保存在特定文件夹内
		String fileNameBase = "D:\\installdirector\\VantAppData\\zhihuCrawl\\";
		for(QuestionSave save : saves) {
			System.out.println(save.toString());
			String fileName;
			//标题过长则取标题的一部分作为文件的内容
			if(save.question.length() > 30) {
				fileName = fileNameBase + save.question.substring(0, 30) + ".txt";
			}else {
				fileName = fileNameBase + save.question + ".txt";
			}
			saveCount++;
			new LocalStore(new File(fileName)).store(save);
		}
		System.out.println("保存结束");
		System.out.println("总共爬取了: " + saveCount + "个问题");
	}
	
	
	public static String zhRecommendationURL = "https://www.zhihu.com/explore/recommendations";
	
	/**
	 * 
	 * @param url 类似：https://www.zhihu.com
	 * @return 网站内容
	 */
	public static String htmlContent(String url) {
		StringBuilder result = new StringBuilder(1000);
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			URLConnection urlConnection = realUrl.openConnection();
			urlConnection.connect();
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
	
	
	static final Pattern questionTitle = Pattern.compile("QuestionHeader-title\">(.+?)<");//标题在组1
	static final Pattern questionDescription = Pattern.compile("QuestionHeader-detail.+?<span(.+?)>(.+?)<");//第二组才是内容
	static final Pattern answerCount = Pattern.compile("([0-9]+?) 个回答");//第一组是答案数
	/**
	 * 
	 * @param questionUrl 在知乎的question界面进行爬取，网址类似：https://www.zhihu.com/question/61340492
	 * @return
	 */
	public static QuestionSave questionCrawl(String questionUrl) {
		String que = htmlContent(questionUrl);
		QuestionSave questionSave = new QuestionSave();
		Matcher titleMatcher = questionTitle.matcher(que);
		titleMatcher.find();
		questionSave.question = titleMatcher.group(1);
		Matcher questionMatcher = questionDescription.matcher(que);
		questionMatcher.find();
		questionSave.queStionDescription = questionMatcher.group(2);
		Matcher ansCountMatcher = answerCount.matcher(que);
		ansCountMatcher.find();
		questionSave.ansCount = Integer.parseInt(ansCountMatcher.group(1));
		questionSave.url = questionUrl;
		questionSave.ans.addAll(findAnswer(que));
		return questionSave;
	}
	
	
	static final Pattern answerPattern = Pattern.compile("RichContent-inner.+?\"text\">(.*?)</div>");//获得答案内容，
	/**
	 * @param page 给定知乎的网页
	 * @return 只返回回答的内容
	 * 
	 */
	public static ArrayList<String> findAnswer(String content) {
		Matcher matcher = answerPattern.matcher(content);
		ArrayList<String> anss = new ArrayList<>();
		while(matcher.find()) {
			anss.add(matcher.group(1));
		}
		return anss;
	}
	
}
