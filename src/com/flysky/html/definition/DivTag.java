package com.flysky.html.definition;

public class DivTag extends NodeDefinition{

	public DivTag() {
		super("<div", "</div>", new String[]{"<div", "<p", "<h2", "<a", "<main", "<table", "<ul", "<secion"});
	}

	public String getRegex() {
		return ".*(<div[^>]*>.*?<\\/div>)";
	}
}
