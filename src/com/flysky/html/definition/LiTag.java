package com.flysky.html.definition;

public class LiTag extends NodeDefinition {

	public LiTag() {
		super("<li", "</li>", new String[] {"<a"});
	}

	public String getRegex() {
		return ".*(<li[^>]*>.*?<\\/li>)";
	}
}
