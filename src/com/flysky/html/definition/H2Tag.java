package com.flysky.html.definition;

public class H2Tag extends NodeDefinition {

	public H2Tag() {
		super("<h2", "</h2>");
	}

	public String getRegex() {
		return ".*(<h2[^>]*>.*?<\\/h2>)";
	}
}
