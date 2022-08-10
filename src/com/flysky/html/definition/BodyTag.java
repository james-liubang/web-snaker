package com.flysky.html.definition;

public class BodyTag extends NodeDefinition {

	public BodyTag() {
		super("<body", "</body>", new String[]{"<div"});
	}

	public String getRegex() {
		return ".*(<body[^>]*>.*?<\\/body>)";
	}
}
