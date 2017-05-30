package org.hadatac.data.loader;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;

public class DASchemaAttrGenerator extends BasicGenerator {
	final String kbPrefix = "chear-kb:";
	String startTime = "";
	String SDDName = "";
	
	public DASchemaAttrGenerator(File file) {
		super(file);
		this.SDDName = file.getName();
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
//        mapCol.put("??mother", "chear-kb:ObjectTypeMother");
//        mapCol.put("??child", "chear-kb:ObjectTypeChild");
//        mapCol.put("??birth", "chear-kb:ObjectTypeBirth");
//        mapCol.put("??household", "chear-kb:ObjectTypeHousehold");
//        mapCol.put("??headhousehold", "chear-kb:ObjectTypeHeadHousehold");
//        mapCol.put("??father", "chear-kb:ObjectTypeFather");
	}
    
    private String getLabel(CSVRecord rec) {
    	return rec.get(mapCol.get("Label"));
    }
    
    private String getAttribute(CSVRecord rec) {
    	return rec.get(mapCol.get("AttributeType"));
    }
    
    private String getAttributeOf(CSVRecord rec) {
    		return kbPrefix + "DASO-" + rec.get(mapCol.get("AttributeOf")).replace("??", "") + "-PS" + SDDName.replaceAll("\\D+","");
    }
    
    private String getUnit(CSVRecord rec) {
    	if (rec.get(mapCol.get("Unit")) != null) {
    		return rec.get(mapCol.get("Unit"));
    	} else {
    		return "";
    	}
    }
    
    private String getTime(CSVRecord rec) {
    	return rec.get(mapCol.get("Time"));
    }
    
    private String getEntity(CSVRecord rec) {
    	return rec.get(mapCol.get("Entity"));
    }
    
    private String getRole(CSVRecord rec) {
    	return rec.get(mapCol.get("Role"));
    }
    
    private String getRelation(CSVRecord rec) {
    	return rec.get(mapCol.get("Relation"));
    }
    
    private String getInRelationTo(CSVRecord rec) {
    	return rec.get(mapCol.get("InRelationTo"));
    }
    
    private String getWasDerivedFrom(CSVRecord rec) {
    	return rec.get(mapCol.get("WasDerivedFrom"));
    }
    
    private String getWasGeneratedBy(CSVRecord rec) {
    	return rec.get(mapCol.get("WasGeneratedBy"));
    }
    
    private Boolean checkVirtual(CSVRecord rec) {
    	if (getLabel(rec).contains("??")){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private String getPosition(CSVRecord rec) {
    	return rec.get(mapCol.get("HasPosition"));
    }
    
    @Override
    public List< Map<String, Object> > createRows() throws Exception {
    	rows.clear();
    	int row_number = 0;
    	for (CSVRecord record : records) {
    		if (getAttribute(record)  == null || getAttribute(record).equals("")){
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
    	row.put("hasURI", kbPrefix + "DASA-" + getLabel(rec));
    	row.put("a", "hasneto:DASchemaAttribute");
    	row.put("rdfs:label", getLabel(rec));
    	row.put("rdfs:comment", getLabel(rec));
    	row.put("hasneto:partOfSchema", kbPrefix + "DAS-" + SDDName.replace(".csv", ""));
    	row.put("hasco:hasPosition", getPosition(rec));
    	row.put("hasneto:hasEntity", "");
    	row.put("hasneto:hasAttribute", getAttribute(rec));
    	row.put("hasneto:hasUnit", getUnit(rec));
    	row.put("hasco:hasSource", "");
    	row.put("hasco:isAttributeOf", getAttributeOf(rec));
    	row.put("hasco:isVirtual", checkVirtual(rec).toString());
    	row.put("hasco:isPIConfirmed", "false");
    	
    	return row;
    }
}