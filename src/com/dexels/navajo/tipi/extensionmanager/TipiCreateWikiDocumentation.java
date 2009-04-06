package com.dexels.navajo.tipi.extensionmanager;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.dexels.navajo.tipi.util.XMLElement;


public class TipiCreateWikiDocumentation extends ExtensionClassdefProcessor {

	private final Map<String,String> typeExtension = new HashMap<String,String>();

	private OutputStream writeResource(String path) throws FileNotFoundException {
//		System.err.println("Userdir: "+System.getProperty("user.dir"));
		//File buildDir = new File()"build/");
		getOutputDir().mkdirs();
		File pathFile = new File(getOutputDir(),path);
		pathFile.getParentFile().mkdirs();
		System.err.println("Writing resource: "+path+" abs: "+pathFile.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(pathFile);
		return fos;
	}
	

	protected void filterExtensions(String extension, List<String> extensions, Map<String, XMLElement> allComponents, Map<String, XMLElement> allActions,
			Map<String, XMLElement> allEvents, Map<String, XMLElement> allValues, Map<String, XMLElement> allTypes,
			Map<String, XMLElement> allFunctions) {
		filterMapForExtension(extension,allComponents);
		filterMapForExtension(extension,allActions);
		filterMapForExtension(extension,allEvents);
		filterMapForExtension(extension,allValues);
		filterMapForExtension(extension,allTypes);
		filterMapForExtension(extension,allFunctions);
	}
	
	private void filterMapForExtension(String extension, Map<String, XMLElement> xmlMap) {
		Set<String> toBeRemoved = new HashSet<String>();
		for (Entry<String, XMLElement> e : xmlMap.entrySet()) {
			String extensionString = e.getValue().getStringAttribute("extension");
			if(!extension.equals(extensionString)) {
				toBeRemoved.add(extensionString);
			}

		}
		for (String string : toBeRemoved) {
			xmlMap.remove(string);
		}
		// TODO Auto-generated method stub
		
	}


	protected void processTipiContext(String currentExtension, List<String> extensions, Map<String, XMLElement> allComponents, Map<String, XMLElement> allActions,
			Map<String, XMLElement> allEvents, Map<String, XMLElement> allValues, Map<String, XMLElement> allTypes,
			Map<String, XMLElement> allFunctions) {
		filterExtensions(currentExtension, extensions, allComponents, allActions, allEvents, allValues, allTypes, allFunctions);

		Map<String, List<XMLElement>> extensionComponentMap = createExtensionMapFromList(allComponents);
		Map<String, List<XMLElement>> extensionActionMap = createExtensionMapFromList(allActions);
		Map<String, List<XMLElement>> extensionTypeMap = createExtensionMapFromList(allTypes);
		Map<String, List<XMLElement>> extensionFunctionMap = createExtensionMapFromList(allFunctions);
		
		
		System.err.println("Processing extensions: "+extensions+" current: "+currentExtension);
	//	System.err.println("Actions: "+extensionActionMap.keySet());
	//	System.err.println("Functions: "+extensionFunctionMap.keySet());
			try {
			for (String extension : extensionTypeMap.keySet()) {
				List<XMLElement> typeList = extensionTypeMap.get(extension);
				if(typeList!=null) {
					processTypes(currentExtension, typeList);
					processTypeHeaders(currentExtension, typeList);
				}
			}

			for (String extension : extensionComponentMap.keySet()) {
				processComponents(currentExtension, extensionComponentMap.get(extension), extensionActionMap.get(extension));
			}
			for (String extension : extensionActionMap.keySet()) {
				processActions(currentExtension, extensionActionMap.get(extension));
			}
			for (String extension : extensionFunctionMap.keySet()) {
				processFunctions(currentExtension, extensionFunctionMap.get(extension));
				processFunctionHeaders(currentExtension,extensionFunctionMap.get(extension));
			}
			
			createIndex(currentExtension, extensionComponentMap);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	private void processFunctionHeaders(String extension, List<XMLElement> list) throws IOException {
		OutputStream os = writeResource( extension + "/functions/list.txt");
		OutputStreamWriter osw = new OutputStreamWriter(os);
		Collections.sort(list, new Comparator<XMLElement>() {

			public int compare(XMLElement o1, XMLElement o2) {
				String s1 = o1.getStringAttribute("name");
				String s2 = o2.getStringAttribute("name");
				return s1.compareTo(s2);
			}
		});
		osw.write("=== Types ===\n");
			for (XMLElement e : list) {
				osw.write("  * Function: [[.:"+e.getStringAttribute("name")+"|"+e.getStringAttribute("name")+"]]\n");
			}
		osw.flush();
		os.flush();
		os.close();
	}

	private void processFunctions(String extension, List<XMLElement> list) throws IOException {
		Collections.sort(list, new Comparator<XMLElement>() {
			public int compare(XMLElement o1, XMLElement o2) {
				String s1 = o1.getStringAttribute("name");
				String s2 = o2.getStringAttribute("name");
				return s1.compareTo(s2);
			}
		});
	    System.err.println("Writing function definitions: ("+extension+")"+list.size());
		for (XMLElement element : list) {
			String resourceName = extension + "/functions/"+element.getStringAttribute("name")+".txt";
			OutputStream os = writeResource(resourceName.toLowerCase());
			OutputStreamWriter osw = new OutputStreamWriter(os);
			typeExtension.put(element.getStringAttribute("name"),extension);
			
			osw.write("==== Type: "+element.getStringAttribute("name")+" ====\n");
			osw.write("  * Java type: "+element.getStringAttribute("class")+"\n");
			
			appendDescriptorTag(osw, element.getChildByTagName("description"));

			osw.write("\n");

			XMLElement description = element.getChildByTagName("description");
			String desc = description==null?"":description.getContent();
			XMLElement input = element.getChildByTagName("input");
			String inp = input==null?"":input.getContent();
			XMLElement result = element.getChildByTagName("result");
			String res = result==null?"":result.getContent();

			
			// TODO DISABLED FOR NOW
			
			// Rewrite to xml based resolution
			
//			FunctionDefinition fd = new FunctionDefinition(element.getStringAttribute("class"),desc,inp,res);
//			if(fd.getResultParam()!=null && fd.getResultParam().length>0) {
//				osw.write("== Possible result values ==\n");
//				for (int i = 0; i < fd.getResultParam().length; i++) {
//					String param = fd.getResultParam()[i];
//					osw.write("  *"+getDataTypeLink(param)+"\n");
//				}
//				
//			} else {
//				osw.write("== No result type information ==\n");
//			}
//
//			if(fd.getInputParams()!=null && fd.getInputParams().length>0) {
//				osw.write("== Possible input values ==\n");
//				for (int i = 0; i < fd.getInputParams().length; i++) {
//					String[] params = fd.getInputParams()[i];
//					if (params.length==0) {
//						osw.write("  *"+element.getStringAttribute("name")+"( )\n");
//					} else {
//						osw.write("  *"+element.getStringAttribute("name")+"( ");
//						for (int j = 0; j < params.length; j++) {
//							osw.write(" "+getDataTypeLink(params[j]));
//							if(j<params.length-1) {
//								osw.write(" , ");
//							}
//						}
//						osw.write(")\n");
//					}
//				}
//			} else {
//				osw.write("== No parameter type information ==\n");
//			}

			osw.flush();
			os.flush();
			os.close();
			
		}
	}




	private void createIndex(String extension, Map<String, List<XMLElement>> extMap) throws IOException {
		OutputStream os = writeResource("tipi.txt");
		OutputStreamWriter osw = new OutputStreamWriter(os);
		osw.write("==== Tipi Extensions ====\n");
		osw.write("^ Extension ^ Components ^ Actions ^ Functions ^ Types ^\n");
		osw.write("I will reinstate this later\n");

		System.err.println("Output: "+getOutputDir().getAbsolutePath());

		// TODO DISABLED FOR NOW
//		System.err.println("Extensiojn lost"+extension);
//		for (String ext : extension) {
			createExtensionDetails(new File(getOutputDir().getParentFile(),"definition.xml"),extension);
			
//		}
//		for (TipiExtension e : extension) {
//			List<XMLElement> incls = extMap.get(e.getId());
//			if (incls == null || incls.size() == 0) {
//				continue;
//			}
//			osw.write("| [[tipidoc:" + e.getId() + ":details|"+e.getId()+"]]| [[tipidoc:" + e.getId() + ":componentlist|Components]] | [[tipidoc:" + e.getId() + ":actionlist|Actions]] | [[tipidoc:" + e.getId() + ":functions:list|Functions]] | [[tipidoc:" + e.getId() + ":types:list|Types]] |\n");
////			osw.write(" [[tipidoc:" + e.getId() + ":details|"+e.getId()+"]]\n");
//			createExtensionDetails(e);
//		}
		osw.flush();
		os.flush();
		os.close();
	}

	private void createExtensionDetails(File inputFile, String extension) throws IOException {

		OutputStream os = writeResource(extension + "/details.txt");
		OutputStreamWriter osw = new OutputStreamWriter(os);
		osw.write("==== Tipi extension: " + extension + " (project: " +"unknown" + " ) ====\n");

		XMLExtension te = new XMLExtension();
		te.loadXML(inputFile);
		// TODO DISABLED FOR NOW		
		osw.write("=== " + te.getDescription() + " ===\n");
		if (te.getDeploymentDescriptor() != null) {
			osw.write("Main repository deployment: [[http://www.navajo.nl/Tipi/" + te.getProjectName() + "/"
					+ te.getDeploymentDescriptor() + "|" + "http://www.navajo.nl/Tipi/" + te.getProjectName() + "/"
					+ te.getDeploymentDescriptor() + "]]\n\n");
		} else {
			osw.write("Main repository deployment: [not applicable]\n");
		}
		if (te.requiresMainImplementation() != null) {
			osw.write("Needs main implementation: [[tipidoc:" + te.requiresMainImplementation() + ":details|"
					+ te.requiresMainImplementation() + "]]\n");
		} else {
			osw.write("Needs main implementation: [any]\n");
		}
		if (te.getDependingProjectUrls() != null) {
			osw.write("=== Open source dependencies: ===\n");
			for (String ex : te.getDependingProjectUrls()) {
				osw.write("[[" + ex + "|" + ex + "]]\n\n");
			}
		}
		osw.write("\n-----\n");


	// TODO DISABLED FOR NOW		

		if (te.getRequiredExtensions() != null) {

			for (String ex : te.getRequiredExtensions()) {
				osw.write("Needs: [[tipidoc:" + ex + ":details|" + ex + "]]\n");
			}
		}
		osw.write("^ Main jars: ^ Id: ^\n");
		if (te.getMainJars() != null) {
			for (String ex : te.getMainJars()) {
				osw.write("| Main jar required: |" + ex + "|\n");
			}
		}
		osw.write("\n----\n");

		osw.write("^ Lib jars: ^ Id: ^\n");

		if (te.getLibraryJars() != null) {

			for (String ex : te.getLibraryJars()) {
				osw.write("| Lib jar required: |" + ex + "|\n");
			}
		}
		osw.write("\n\n");

		osw.write("Components: [[componentlist|Components]]\n\n");
		osw.write("Actions: [[actionlist|Actions]]\n\n");
		osw.write("Types: [[typelist|Types]]\n\n");
		osw.write("Functions: [[functionlist|Functions]]\n\n");
// 		osw.write("Actions: [[tipidoc:" + te.getId() + ":actionlist|Actions: " + te.getId() + "]]\n\n");
//		osw.write("Other: Not yet implemented\n\n");

		osw.flush();
		os.flush();
		os.close();
	}


	private void createExtensionDetails(String extension) throws IOException {
		

	}
	
	
	private void processTypeHeaders(String extension, List<XMLElement> list) throws IOException {
		OutputStream os = writeResource(extension + "/typelist.txt");
		OutputStreamWriter osw = new OutputStreamWriter(os);
		Collections.sort(list, new Comparator<XMLElement>() {

			public int compare(XMLElement o1, XMLElement o2) {
				String s1 = o1.getStringAttribute("name");
				String s2 = o2.getStringAttribute("name");
				return s1.compareTo(s2);
			}
		});
		 //<tipi-parser name="component" type="com.dexels.navajo.tipi.TipiComponent" parser="com.dexels.navajo.tipi.components.core.parsers.ComponentParser" />
		  
		osw.write("=== Types ===\n");
			for (XMLElement e : list) {
				osw.write("  * Type: [[.:"+e.getStringAttribute("name")+"|"+e.getStringAttribute("name")+"]]\n");
			}
		
		osw.flush();
		os.flush();
		os.close();
	}

	private void processTypes(String extension, List<XMLElement> list) throws IOException {
		for (XMLElement element : list) {
			OutputStream os = writeResource(extension + "/types/"+element.getStringAttribute("name")+".txt");
			OutputStreamWriter osw = new OutputStreamWriter(os);
			 //<tipi-parser name="component" type="com.dexels.navajo.tipi.TipiComponent" parser="com.dexels.navajo.tipi.components.core.parsers.ComponentParser" />
			typeExtension.put(element.getStringAttribute("name"),extension);
			osw.write("=== Type: "+element.getStringAttribute("name")+" ===\n");
			osw.write("  * Java type: "+element.getStringAttribute("type")+"\n");
			osw.write("  * Java parser: "+element.getStringAttribute("parser")+"\n");
			
			appendDescriptorTag(osw, element.getChildByTagName("description"));

		
			osw.flush();
			os.flush();
			os.close();
			
		}
	}
	private void processComponents(String extension, List<XMLElement> allComponents, List<XMLElement> allActions) throws IOException {
		if(allActions==null) {
			allActions = new ArrayList<XMLElement>();
		}
		OutputStream os = writeResource(extension + "/componentlist.txt");
		OutputStreamWriter osw = new OutputStreamWriter(os);
		Collections.sort(allComponents, new Comparator<XMLElement>() {

			public int compare(XMLElement o1, XMLElement o2) {
				String s1 = o1.getStringAttribute("name");
				String s2 = o2.getStringAttribute("name");
				return s1.compareTo(s2);
			}
		});
		Collections.sort(allActions, new Comparator<XMLElement>() {

			public int compare(XMLElement o1, XMLElement o2) {
				String s1 = o1.getStringAttribute("name");
				String s2 = o2.getStringAttribute("name");
				return s1.compareTo(s2);
			}
		});
		List<XMLElement> realComponents = new ArrayList<XMLElement>();
		List<XMLElement> connectorComponents = new ArrayList<XMLElement>();
		List<XMLElement> layoutComponents = new ArrayList<XMLElement>();
		List<XMLElement> otherComponents = new ArrayList<XMLElement>();

		for (XMLElement e : allComponents) {
			String type = e.getStringAttribute("type");
			if(type==null) {
				System.err.println("DOumentation found abstract class. IGnoring for now!");
				continue;
			}
			if (type.equals("component") || type.equals("tipi") ) {
				realComponents.add(e);
			} else if (type.equals("connector")) {
				connectorComponents.add(e);
			} else if (type.equals("layout")) {
				layoutComponents.add(e);
			} else {
				otherComponents.add(e);
			}
		}
		System.err.println("Real: "+realComponents.size()+" connector: "+connectorComponents.size());
		if (realComponents.size() > 0) {
			osw.write("=== Components ===\n");
			for (XMLElement e : realComponents) {
			
				writeComponentHeader(extension,e, osw);
				writeComponent(extension, e);
			}
		}
		if (connectorComponents.size() > 0) {
			osw.write("=== Connectors ===\n");
			for (XMLElement e : connectorComponents) {
				writeComponentHeader(extension,e, osw);
				writeComponent(extension, e);
			}
		}

		if (layoutComponents.size() > 0) {
			osw.write("=== Layout ===\n");
			for (XMLElement e : layoutComponents) {
				writeComponentHeader(extension,e, osw);
				writeComponent(extension, e);
			}
		}
		if (otherComponents.size() > 0) {
			osw.write("=== Other ===\n");
			for (XMLElement e : otherComponents) {
				writeComponentHeader(extension,e, osw);
				writeComponent(extension, e);
			}
		}
	
		
		osw.flush();
		os.flush();
		os.close();
	//	String name = extension + "/c." + element.getStringAttribute("name") + ".txt";
		
		os = writeResource(extension + "/actionlist.txt");
		osw = new OutputStreamWriter(os);
		if (allActions.size() > 0) {
			osw.write("=== Actions ===\n");
			for (XMLElement e : allActions) {

				writeActionHeader(extension,e, osw);
//				writeAction(extension,e);
			}
		}
		osw.flush();
		os.flush();
		os.close();

	}

	private void writeComponent(String extensionId, XMLElement element) throws IOException {
		String name = extensionId + "/components/c." + element.getStringAttribute("name") + ".txt";
		OutputStream os2 = writeResource(name);
		OutputStreamWriter osw2 = new OutputStreamWriter(os2);
		writeComponent(element, osw2);
		osw2.flush();
		os2.flush();
		os2.close();
	}

	private void processActions(String extension, List<XMLElement> allActions) throws IOException {

			for (XMLElement e : allActions) {
			writeAction(extension,e);
		}
	}

	private Map<String, List<XMLElement>> createExtensionMapFromList(Map<String, XMLElement> allElements) {
		Map<String, List<XMLElement>> result = new TreeMap<String, List<XMLElement>>();
		for (XMLElement x : allElements.values()) {
			String ext = x.getStringAttribute("extension");
			if(ext==null) {
				System.err.println("::::: "+x);
			}
			List<XMLElement> v = result.get(ext);
			if (v == null) {
				v = new ArrayList<XMLElement>();
				result.put(ext, v);
			}
			v.add(x);
		}
		return result;
	}

	private void writeAction(String extension, XMLElement component) throws IOException {
		OutputStream os = writeResource(extension + "/actions/"+component.getStringAttribute("name").toLowerCase()+".txt");

		OutputStreamWriter w = new OutputStreamWriter(os);


		w.write("=== " + component.getStringAttribute("name") + " ===\n");
		
		List<XMLElement> ll = component.getElementsByTagName("description");
		appendDescriptorTags(w, ll);
		List<XMLElement> values = component.getElementsByTagName("param");

		if (values.size() > 0) {
			w.write("== Parameters ==\n");
		}

		for (XMLElement element : values) {
			// <value direction="in" name="y" type="integer" value="0" />
			// w.write("<div class='value'>");
			String name = element.getStringAttribute("name");
			String type = element.getStringAttribute("type");
			String required = element.getStringAttribute("required");
//			w.write("  * " + type + " : " + name + "\n");
			String req;
			if ("true".equals(required)) {
				req = "*(requred)*";
			} else {
				req = "(optional)";

			}
			w.write("  *" +getDataTypeLink(type)+ " : " + name +" "+req+ "\n");

			appendDescriptorTag(w, element.getChildByTagName("description"));
		}
		w.write("------\n[[tipiremarks:"+extension+":" + component.getStringAttribute("name") + "|Remarks]]\n");

		w.flush();
		os.flush();
		os.close();

	}

	private void writeComponentHeader(String extension,XMLElement component, Writer w) throws IOException {
		if ("true".equals(component.getAttribute("deprecated"))) {
			w.write("  * <del>Component: [[tipidoc:"+extension+":components:c." + component.getStringAttribute("name") + "|"+ component.getStringAttribute("name") + "]]</del>\n");
		} else {
			w.write("  * Component: [[tipidoc:"+extension+":components:c." + component.getStringAttribute("name") + "|"+ component.getStringAttribute("name") + "]]\n");
		}

	}
	private void writeActionHeader(String extension,XMLElement component, Writer w) throws IOException {
		w.write("  * Action: [[tipidoc:"+extension+":actions:" + component.getStringAttribute("name") + "|"+ component.getStringAttribute("name") + "]]\n");

	}
	private void writeComponent(XMLElement component, Writer w) throws IOException {
		w.write("==== Component: " + component.getStringAttribute("name") + " (type: " + component.getStringAttribute("type")
				+ ") ==== \n");
		List<XMLElement> ll = component.getElementsByTagName("description");
		appendDescriptorTags(w, ll);

		List<XMLElement> values = component.getElementsByTagName("value");

		if (values.size() > 0) {
			w.write("=== Values ===\n");
		}

		for (XMLElement element : values) {
			// <value direction="in" name="y" type="integer" value="0" />
			// w.write("<div class='value'>");
			String name = element.getStringAttribute("name");
			String type = element.getStringAttribute("type");
			String direction = element.getStringAttribute("direction");
			boolean writable = ("in".equals(direction)) || ("inout".equals(direction));
			if (writable) {
		//		w.write("  *[[..:"+getExtensionOfDataType(type)+":types:" + type + " : " + name + " (writable) \n");
				w.write("  *" +getDataTypeLink(type)+ " : " + name + " (writable)\n");

			} else {
			//	w.write("  *" + type + " : " + name + "\n");
				w.write("  *" +getDataTypeLink(type)+ " : " + name + "\n");
			}
			appendDescriptorTag(w, element.getChildByTagName("description"));
			// w.write("<div class='value'>");
			//
			// appendAttribute("name",w,element);
			//			
			// appendAttribute("value",w,element);
			// appendAttribute("direction",w,element);
			// appendAttribute("type",w,element);
			// w.write("</div>\n");
		}

		List<XMLElement> methods = component.getElementsByTagName("method");
		// <method name="enableTab">
		// <param name="tabname" type="string" />
		// <param name="value" type="boolean" />
		// </method>
		if (methods.size() > 0) {

			w.write("=== Methods ===\n");
		}
		for (XMLElement element : methods) {
			// <value direction="in" name="y" type="integer" value="0" />
			w.write("== Method: " +component.getStringAttribute("name")+"."+element.getStringAttribute("name") + " ==\n");
			appendDescriptorTag(w, element.getChildByTagName("description"));
			// appendAttribute("name",w,element);
			List<XMLElement> methodParams = element.getElementsByTagName("param");
			if (methodParams.size() > 0) {
//				w.write("<div class='methodparam'>\n");
				for (XMLElement param : methodParams) {
					String name = param.getStringAttribute("name");
					String type = param.getStringAttribute("type");
					w.write("  *" +getDataTypeLink(type)+ " : " + name + "\n");

				}
			}
		}

		// <event name="onLoad">
		// <param name="service" type="string" />
		// <param name="navajo" type="navajo"/>
		// </event>

		List<XMLElement> events = component.getElementsByTagName("event");
		if (events.size() > 0) {
			w.write("=== Events ===\n");
		}

		for (XMLElement element : events) {
			// <value direction="in" name="y" type="integer" value="0" />
			w.write("== Event: " + element.getStringAttribute("name") + " ==\n");
			XMLElement descrTag = element.getChildByTagName("description");
			//System.err.println("Descr Tag: "+descr);
			appendDescriptorTag(w, descrTag);
			List<XMLElement> methodParams = element.getElementsByTagName("param");
			for (XMLElement param : methodParams) {
				String name = param.getStringAttribute("name");
				String type = param.getStringAttribute("type");
				w.write("  *" +getDataTypeLink(type)+ " : " + name + "\n");
			}
		}
		w.write("\n------\n[[tipiremarks:"+component.getStringAttribute("extension")+":" + component.getStringAttribute("name") + "|Remarks]]\n");

	}
	
	private String getDataTypeLink(String type) {
		return "[[tipidoc:"+getExtensionOfDataType(type)+":types:"+type+"|" + type + "]]";
	}

	private String getExtensionOfDataType(String datatype) {
		//System.err.println("Type: "+datatype+" map: "+typeExtension);
		return typeExtension.get(datatype);
	}
	
	private void appendDescriptorTags(Writer w, List<XMLElement> descriptionTags) throws IOException {
		if (descriptionTags == null) {
			return;
		}
		for (XMLElement element : descriptionTags) {
			appendDescriptorTag(w, element);
		}
	}

	private void appendDescriptorTag(Writer w, XMLElement element) throws IOException {
		if (element == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		String s  = element.getContent().trim();
		s = s.replaceAll("\t", "");
		s = s.replaceAll("\n", " ");
		s = s.replaceAll("\\\\[ \n\t]*", "\n");
		
//		w.write("<div class='" + cssClass + "'>" + element.getContent() + "</div>");
		w.write(s + "\n");

	}




	// private void appendAttribute(String cls, Writer w, XMLElement element)
	// throws IOException {
	// String stringAttribute = element.getStringAttribute(cls);
	// if (stringAttribute == null) {
	// return;
	// }
	// w.write("<div class='" + cls + "'>" + stringAttribute + "</div>\n");
	//
	// }

}
