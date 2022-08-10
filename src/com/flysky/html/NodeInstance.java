package com.flysky.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flysky.html.definition.DefinitionManager;
import com.flysky.html.definition.NodeDefinition;
/**
 * Simple Node for Html parser
 * @param parent
 */
public class NodeInstance {
	protected String htmlContent;
	protected NodeDefinition definition=null;
	public NodeInstance(String tag, String htmlContent) {
		this.definition = DefinitionManager.findDefinition(tag);
		this.htmlContent = htmlContent;
	}
	public NodeInstance(NodeDefinition definition, String htmlContent) {
		this.definition = definition;
		this.htmlContent = htmlContent;
	}

	protected NodeDefinition getDefinition() {
		return this.definition;
	}
	private List<NodeInstance> children = new ArrayList<NodeInstance>();
	public List<NodeInstance> getChildren(){
		return children;
	}
	public void parse() {
		
		for(NodeDefinition chidDef:definition.getChildren()) {
			String[] childenHtmls = parseHtmlElement(htmlContent, chidDef);
			String startTag = chidDef.getStartTag();
			//String endTag = chidDef.getEndTag();
			for(String htmlElement:childenHtmls) {
				NodeInstance child = new NodeInstance(startTag, htmlElement);
				child.parse();
				children.add(child);
			}
		}
	}
	
	public static final String[] EMPTY_STRING_ARRAY = {};
	protected String[] parseHtmlElement(String htmlContent, NodeDefinition chidDef) {
		String startTag = chidDef.getStartTag();
		String endTag = chidDef.getEndTag();
		if (htmlContent == null || StringsUtils.isEmpty(startTag) || StringsUtils.isEmpty(endTag)) {
			return EMPTY_STRING_ARRAY;
		}
		final int strLen = htmlContent.length();
		final int startLen = startTag.length();
		final int endLen = endTag.length();
		if (strLen == 0) {
			return EMPTY_STRING_ARRAY;
		}
		final List<String> list = new ArrayList<>();
		String pattern = chidDef.getRegex();
		if(pattern!=null) {
			Matcher matcher = Pattern.compile(pattern).matcher(htmlContent);
	        if (matcher.find()) {
	            String find = matcher.group(1); // prints [Hello, world!]
	            int findLen = find.length();
	            String subHtml=find.substring(startLen, findLen-endLen);
	            list.add(subHtml);
	        }
		}
		return list.toArray(EMPTY_STRING_ARRAY);
	}
	
	public String toValue() {
		return htmlContent;
	}
	
	private StringBuffer buffer=new StringBuffer();
	public String toXml() {
		String startTag = this.definition.getStartTag();
		String endTag = this.definition.getEndTag();
		String value = this.toValue();
		buffer.append(startTag+">");
		if(value!=null&&this.getChildren().size()==0) {
			buffer.append(value);
		}else {
			for(NodeInstance child:this.getChildren()) {
				buffer.append("\n");
				buffer.append(child.toXml());
				buffer.append("\n");
			}
		}
		buffer.append(endTag);
		return buffer.toString();
	}
}
