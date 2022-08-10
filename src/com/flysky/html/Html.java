package com.flysky.html;

import com.flysky.html.definition.HtmlTag;

public class Html extends NodeInstance {

	public Html(String htmlContent) {
		super(new HtmlTag(), htmlContent);
		this.getDefinition().load();
	}
	public String toValue() {
		return null;
	}
}
