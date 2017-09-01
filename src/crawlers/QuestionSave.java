/**
 * Date: 2017年8月22日 下午1:34:28
 */
package crawlers;

import java.util.ArrayList;

/**
 * @author Vant
 */
public class QuestionSave {
	String url = "";//问题的
	String question = "";//问题标题
	String queStionDescription = "";//问题描述
	int ansCount = 0;//回答数量
	ArrayList<String> ans = new ArrayList<>();//问题的回答
	
	public QuestionSave() {
	}

	public QuestionSave(String url, String question, String questionDescription, ArrayList<String> ans) {
		this.url = url;
		this.question = question;
		this.queStionDescription = questionDescription;
		this.ans = ans;
	}
	
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(30);
		stringBuilder.append("标题:" + question).append("\n").append("描述:" + queStionDescription)
		.append("\n").append("回答数:" + ansCount).append("\n");
		for(int i = 0; i < ans.size(); i++) {
			stringBuilder.append("(" + (i + 1) + "):").append(ans.get(i)).append("\n");
		}
		return stringBuilder.toString();
	}
}
