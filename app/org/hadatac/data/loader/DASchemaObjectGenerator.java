package org.hadatac.data.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.hadatac.console.controllers.annotator.AutoAnnotator;
import org.hadatac.console.http.ConfigUtils;

import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVRecord;

public class DASchemaObjectGenerator extends BasicGenerator {

	final String kbPrefix = ConfigUtils.getKbPrefix();
	String startTime = "";
	String SDDName = "";
	HashMap<String, String> codeMap;
	List<String> timeList = new ArrayList<String>();

	public DASchemaObjectGenerator(File file) {
		super(file);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line =  null;

			while((line = br.readLine()) != null){
				String str[] = line.split(",");
				if (str[4].length() > 0){
					timeList.add(str[4]);
					// System.out.println(str[0] + "-----" + str[5]);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Column	Attribute	attributeOf	Unit	Time	Entity	Role	Relation	inRelationTo	wasDerivedFrom	wasGeneratedBy	hasPosition	
	@Override
	void initMapping() {
		mapCol.clear();
		mapCol.put("Label", "Column");
		mapCol.put("AttributeType", "Attribute");
		mapCol.put("AttributeOf", "attributeOf");
		mapCol.put("Unit", "Unit");
		mapCol.put("Time", "Time");
		mapCol.put("Entity", "Entity");
		mapCol.put("Role", "Role");
		mapCol.put("Relation", "Relation");
		mapCol.put("InRelationTo", "inRelationTo");
		mapCol.put("WasDerivedFrom", "wasDerivedFrom");       
		mapCol.put("WasGeneratedBy", "wasGeneratedBy");
		mapCol.put("HasPosition", "hasPosition");
		// mapCol.put("??mother", "chear-kb:ObjectTypeMother");
		// mapCol.put("??child", "chear-kb:ObjectTypeChild");
		// mapCol.put("??birth", "chear-kb:ObjectTypeBirth");
		// mapCol.put("??household", "chear-kb:ObjectTypeHousehold");
		// mapCol.put("??headhousehold", "chear-kb:ObjectTypeHeadHousehold");
		// mapCol.put("??father", "chear-kb:ObjectTypeFather");
	}

	private String getLabel(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("Label"));
	}

	private String getAttribute(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("AttributeType"));
	}

	private String getUnit(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("Unit"));
	}

	private String getTime(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("Time"));
	}

	private String getEntity(CSVRecord rec) {
		String entity = getValueByColumnName(rec, mapCol.get("Entity"));
		if (entity.length() == 0) {
			return null;
		} else {
			if (codeMap.containsKey(entity)) {
				System.out.println("[DASO] code matched: " + entity); 
				return codeMap.get(entity);
			} else {
				return entity;
			}
		}
	}

	private String getRole(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("Role"));
	}

	private String getRelation(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("Relation"));
	}

	private String getInRelationTo(CSVRecord rec) {
		String inRelationTo = getValueByColumnName(rec, mapCol.get("InRelationTo"));
		if (inRelationTo.length() == 0) {
			return "";
		} else {
			List<String> items = new ArrayList<String>();
			for (String item : Arrays.asList(inRelationTo.split("\\s*,\\s*"))) {
				items.add(kbPrefix + "DASO-" + SDDName + "-" + item.replace("_","-").replace("??", ""));
			}
			return String.join(" & ", items);
		}
	}

	private String getWasDerivedFrom(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("WasDerivedFrom"));
	}

	private String getWasGeneratedBy(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("WasGeneratedBy"));
	}

	private Boolean checkVirtual(CSVRecord rec) {
		if (getLabel(rec).contains("??")){
			return true;
		} else {
			return false;
		}
	}

	private String getPosition(CSVRecord rec) {
		return getValueByColumnName(rec, mapCol.get("HasPosition"));
	}

	@Override
	public List< Map<String, Object> > createRows() throws Exception {
		SDDName = fileName.replace("SDD-","").replace(".csv","");
		codeMap = AutoAnnotator.codeMappings;
		rows.clear();
		int row_number = 0;
		for (CSVRecord record : records) {
			if (getEntity(record)  == null || getEntity(record).equals("") || timeList.contains(getLabel(record))){
				continue;
			} else {
				rows.add(createRow(record, ++row_number));
			}
		}

		return rows;
	}

	//Column	Attribute	attributeOf	Unit	Time	Entity	Role	Relation	inRelationTo	wasDerivedFrom	wasGeneratedBy	hasPosition   
	@Override
	Map<String, Object> createRow(CSVRecord rec, int row_number) throws Exception {
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("hasURI", kbPrefix + "DASO-" + SDDName + "-" + getLabel(rec).trim().replace(" ","").replace("_","-").replace("??", ""));
		row.put("a", "hasco:DASchemaObject");
		row.put("rdfs:label", getLabel(rec).trim().replace(" ","").replace("_","-").replace("??", ""));
		row.put("rdfs:comment", getLabel(rec).trim().replace(" ","").replace("_","-").replace("??", ""));
		row.put("hasco:partOfSchema", kbPrefix + "DAS-" + SDDName);
		row.put("hasco:hasEntity", getEntity(rec));
		row.put("hasco:hasRole", getRole(rec));
		row.put("sio:inRelationTo", getInRelationTo(rec));
		if (getInRelationTo(rec).length() > 0) {
			if (getRelation(rec).length() > 0) {
				row.put("sio:Relation", getRelation(rec));
			} else {
				row.put("sio:Relation", "sio:inRelationTo");
			}
		}
		row.put("hasco:hasUnit", getUnit(rec));
		row.put("hasco:isVirtual", checkVirtual(rec).toString());
		row.put("hasco:hasPosition", getPosition(rec));
		row.put("hasco:isPIConfirmed", "false");
		return row;
	}
}
