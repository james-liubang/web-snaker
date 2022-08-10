package com.flysky.html.definition;

public class ATag extends NodeDefinition {

	public ATag() {
		super("<a", "</a>");
	}

	public String getRegex() {
		return ".*(<a[^>]*>.*?<\\/a>)";
	}
}
