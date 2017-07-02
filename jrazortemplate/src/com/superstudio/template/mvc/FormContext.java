package com.superstudio.template.mvc;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.superstudio.commons.csharpbridge.StringHelper;
import org.apache.commons.lang3.StringUtils;


public class FormContext
{
	private final HashMap<String, FieldValidationMetadata> _fieldValidators = new HashMap<String, FieldValidationMetadata>();
	private final HashMap<String, Boolean> _renderedFields = new HashMap<String, Boolean>();

	public final Map<String, FieldValidationMetadata> getFieldValidators()
	{
		return _fieldValidators;
	}

	private String FormId;
	public final String getFormId()
	{
		return FormId;
	}
	public final void setFormId(String value)
	{
		FormId = value;
	}

	private boolean replaceValidationSummary;
	public final boolean getReplaceValidationSummary()
	{
		return replaceValidationSummary;
	}
	public final void setReplaceValidationSummary(boolean value)
	{
		replaceValidationSummary = value;
	}

	private String validationSummaryId;
	public final String getValidationSummaryId()
	{
		return validationSummaryId;
	}
	public final void setValidationSummaryId(String value)
	{
		validationSummaryId = value;
	}

	public final String getJsonValidationMetadata()
	{
		//JavaScriptSerializer serializer = new JavaScriptSerializer();

		TreeMap<String, Object> dict = new TreeMap<String, Object>();
		dict.put("Fields", getFieldValidators().values());
		dict.put("FormId", getFormId());
		if (!StringUtils.isBlank(getValidationSummaryId()))
		{
			dict.put("validationSummaryId", getValidationSummaryId());
		}
		dict.put("replaceValidationSummary", getReplaceValidationSummary());
		return JSON.toJSONString(dict);
		//return serializer.Serialize(dict);
	}

	public final FieldValidationMetadata getValidationMetadataForField(String fieldName)
	{
		return getValidationMetadataForField(fieldName, false); // createIfNotFound
	}

	public final FieldValidationMetadata getValidationMetadataForField(String fieldName, boolean createIfNotFound)
	{
		if (StringUtils.isBlank(fieldName))
		{
			//throw Error.ParameterCannotBeNullOrEmpty("fieldName");
		}

		FieldValidationMetadata metadata = null;
		if (!(getFieldValidators().containsKey(fieldName) ? (metadata = getFieldValidators().get(fieldName)) == metadata : false))
		{
			if (createIfNotFound)
			{
				metadata = new FieldValidationMetadata();
				metadata.setFieldName(fieldName);
				getFieldValidators().put(fieldName, metadata);
			}
		}
		return metadata;
	}

	public final boolean renderedField(String fieldName)
	{
		boolean result = false;
		result = _renderedFields.get(fieldName);
		return result;
	}

	public final void renderedField(String fieldName, boolean value)
	{
		_renderedFields.put(fieldName, value);
	}
}