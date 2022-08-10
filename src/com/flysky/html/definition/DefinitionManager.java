package com.flysky.html.definition;

import java.util.HashMap;
import java.util.Map;

public class DefinitionManager {
    static void init(){
    	NodeDefinition defs[]= new NodeDefinition[] {
		new PTag(),
		new ATag(),
		new H2Tag(),
		new LiTag(),
		new UlTag(),
		new TBodyTag(),
		new TableTag(),
		new DivTag(),
		new MainTag(),
		new SectionTag(),
		new HeaderTag(),
		new BodyTag(),
		new HtmlTag()
    	};
    	for(NodeDefinition def: defs) {
    		definitions.put(def.getStartTag(), def);
    	}
    }
	private static Map<String, NodeDefinition> definitions=new HashMap<String, NodeDefinition>();;
	public static NodeDefinition findDefinition(String startTag) {
		if(definitions.size()==0) {
			init();
		}
		return definitions.get(startTag);
	}
	public static void register(String startTag, NodeDefinition nodeDefinition) {
		if(definitions.get(startTag)==null) {
			definitions.put(startTag, nodeDefinition);
		}
	}

}
