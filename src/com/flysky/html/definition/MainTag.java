package com.flysky.html.definition;

public class MainTag extends NodeDefinition {

	public MainTag() {
		super("<main", "</main>", new String[] {"<div", "<secion"});
	}

	public String getRegex() {
		return ".*(<main[^>]*>.*?<\\/main>)";
	}
}
