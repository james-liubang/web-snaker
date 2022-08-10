package com.flysky.html.definition;

public class HeaderTag extends NodeDefinition{

	public HeaderTag() {
		super("<header", "</header>", new String[] {"<h2", "<div"});
	}

	public String getRegex() {
		return ".*(<header[^>]*>.*?<\\/header>)";
	}
}

