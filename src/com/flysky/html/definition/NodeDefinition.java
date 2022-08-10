package com.flysky.html.definition;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeDefinition {
	private String startTag;
	private String endTag;
	private String[] subTags;
	public NodeDefinition(String startTag, String endTag) {
		this(startTag, endTag, new String[]{});
	}
	public NodeDefinition(String startTag, String endTag, String[] subTags) {
		this.startTag = startTag;
		this.endTag = endTag;
		this.subTags = subTags;
	}
	
	public String getStartTag() {
		return startTag;
	}

	public String getEndTag() {
		return endTag;
	}
	
	public String getRegex() {
		return null;
	}
	public List<NodeDefinition> getChildren() {
		return children;
	}
	public String[] getSubTags() {
		return new String[]{};
	}
	private List<NodeDefinition> children = new ArrayList<NodeDefinition>();
	private boolean loaded = false;
	public void load() {
		if(!loaded) {
			loaded = true;
			for(String tag: subTags) {
				NodeDefinition def = DefinitionManager.findDefinition(tag);
				if(def!=null) {
					if(!def.loaded) {
						def.load();
					}
					children.add(def);
				}
			}
			
		}
	}
}
