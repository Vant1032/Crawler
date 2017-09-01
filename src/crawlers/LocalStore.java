/**
 * Date: 2017年8月24日 上午11:51:00
 */
package crawlers;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Vant
 *
 */
public class LocalStore {
	File file;
	
	/**
	 * 
	 * @param file 指定存储位置
	 */
	public LocalStore(File file) {
		this.file = file;
	}
	
	
	public void store(QuestionSave questionSave) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		// 建立dom树

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Document document = documentBuilder.newDocument();

		Element question = document.createElement("question");
		document.appendChild(question);
		System.out.println("ddd " + question);
		question.setAttribute("title", questionSave.question);
		question.setAttribute("description", questionSave.queStionDescription);
		question.setAttribute("url", questionSave.url);
		question.setAttribute("answerCount", String.valueOf(questionSave.ansCount));
		System.out.println("questioin : " + question.toString());

		for (String string : questionSave.ans) {
			Element answer = document.createElement("answer");
			answer.appendChild(document.createTextNode(string));
			question.appendChild(answer);
		}

		// 存储
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transformer.transform(new DOMSource(question), new StreamResult(file));
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	
	public QuestionSave get() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		QuestionSave questionSave = new QuestionSave();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			Element question = (Element) doc.getElementsByTagName("question").item(0);
			questionSave.question = question.getAttribute("title");
			questionSave.queStionDescription = question.getAttribute("description");
			questionSave.url = question.getAttribute("url");
			questionSave.ansCount = Integer.parseInt(question.getAttribute("answerCount"));
			NodeList anss = doc.getElementsByTagName("answer");
			for(int i = 0; i < anss.getLength(); i++) {
				 questionSave.ans.add(anss.item(i).getTextContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return questionSave;
	}
}
