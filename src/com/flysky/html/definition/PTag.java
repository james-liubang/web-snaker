package com.flysky.html.definition;

public class PTag extends NodeDefinition {

	public PTag() {
		super("<p", "</p>", new String[]{});
	}

	public String getRegex() {
		return ".*(<p[^>]*>.*?<\\/p>)";
	}
}
