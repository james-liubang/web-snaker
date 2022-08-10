package com.flysky.html.definition;

public class UlTag extends NodeDefinition {

	public UlTag() {
		super("<ul", "</ul>", new String[] {"<li"});
	}

	public String getRegex() {
		return ".*(<ul[^>]*>.*?<\\/ul>)";
	}
}
