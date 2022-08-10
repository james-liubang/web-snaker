package com.flysky.html.definition;

public class HtmlTag extends NodeDefinition{

	public HtmlTag() {
		super("<html", "</html>", new String[]{"<header", "<body"});
	}

}
