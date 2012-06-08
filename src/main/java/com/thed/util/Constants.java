package com.thed.util;

import com.thed.model.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Constants{

	public static final String IMPORT_JOB_NEW = "11001";
	public static final String IMPORT_JOB_NORMALIZATION_IN_PROGRESS = "11002";
	public static final String IMPORT_JOB_NORMALIZATION_SUCCESS = "11003";
	public static final String IMPORT_JOB_NORMALIZATION_FAILED = "11004";
	public static final String IMPORT_JOB_IMPORT_IN_PROGRESS = "11005";
	public static final String IMPORT_JOB_IMPORT_FAILED = "11006";
	public static final String IMPORT_JOB_IMPORT_SUCCESS = "11007";
	public static final String IMPORT_JOB_IMPORT_PARTIAL_SUCCESS = "11008";

	public static final String BY_EMPTY_ROW = "byemptyrow";
	public static final String BY_ID_CHANGE = "byidchange";
	public static final String BY_TESTCASE_NAME_CHANGE = "bytestcasenamechange";

	public static final HashMap<String, FieldConfig> fieldConfigs = new LinkedHashMap<String, FieldConfig>();
	public static final HashMap<String, FieldConfig> systemFieldConfigs = new LinkedHashMap<String, FieldConfig>();
    public static final HashMap<String, FieldTypeMetadata> fieldTypeMetadataMap = new LinkedHashMap<String, FieldTypeMetadata>();
	static{
        systemFieldConfigs.put(ZephyrFieldEnum.NAME, new FieldConfig(ZephyrFieldEnum.NAME, "testcase", true, "string", "name", "name", "Name *", "This is Name", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.STEPS, new FieldConfig(ZephyrFieldEnum.STEPS, "testcase", true, "string", "step", "step", "Step *", "This is Step", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.RESULT, new FieldConfig(ZephyrFieldEnum.RESULT, "testcase", true, "string", "result", "result", "Result *", "This is Result", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.TESTDATA, new FieldConfig(ZephyrFieldEnum.TESTDATA, "testcase", true, "string", "testdata", "testdata", "Testdata *", "This is Result", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.EXTERNAL_ID, new FieldConfig(ZephyrFieldEnum.EXTERNAL_ID, "testcase", true, "string", "externalId", "externalId", "ExternalId *", "This is Name", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.LABELS, new FieldConfig(ZephyrFieldEnum.LABELS, "testcase", true, "string", "labels", "labels", "Labels", "This is labels", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.COMMENTS, new FieldConfig(ZephyrFieldEnum.COMMENTS, "testcase", true, "string", "comments", "comments", "Comments", "This is comments", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.FIX_VERSION, new FieldConfig(ZephyrFieldEnum.FIX_VERSION, "testcase", true, "string", "version", "version", "Versions", "This is fix version", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.COMPONENT, new FieldConfig(ZephyrFieldEnum.COMPONENT, "testcase", true, "string", "components", "components", "Components", "This is Name", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.PRIORITY, new FieldConfig(ZephyrFieldEnum.PRIORITY, "testcase", true, "string", "priority", "priority", "Priority", "This is Priority", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.ASSIGNEE, new FieldConfig(ZephyrFieldEnum.ASSIGNEE, "testcase", true, "string", "externalId", "externalId", "Assignee", "This is assignee", true, true, true, true, 255, null));
        systemFieldConfigs.put(ZephyrFieldEnum.DESCRIPTION, new FieldConfig(ZephyrFieldEnum.DESCRIPTION, "testcase", true, "string", "description", "description", "Description", "This is description", true, true, true, true, 255, null));
	}
	
}