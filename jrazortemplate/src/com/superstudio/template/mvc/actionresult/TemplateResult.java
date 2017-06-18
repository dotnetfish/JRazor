package com.superstudio.template.mvc.actionresult;

import com.superstudio.commons.CodeExecuteTimeStatistic;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.template.mvc.context.RenderContext;

public class TemplateResult extends TemplateResultBase {

	private String masterName;

	public String getMasterName() {
		return masterName == null ? "" : masterName;
	}

	public void setMasterName(String value) {
		this.masterName = value;
	}

	@Override
	protected TemplateEngineResult findTemplate(RenderContext context)
			throws InvalidOperationException, ArgumentNullException, ArgumentException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
	long time=System.currentTimeMillis();
		TemplateEngineResult result = getTemplateEngine().findTemplate(context, getTemplateName(), getMasterName());
		long time2=System.currentTimeMillis();
		CodeExecuteTimeStatistic.evalute(this.getClass().getName()+"findTemplate",time2-time);
		if (result.getTemplate() != null) {
			return result;
		}

		// we need to generate an exception containing all the locations we
		// searched
		StringBuilder locationsText = new StringBuilder();
		for (String location : result.getSearchedLocations()) {
			locationsText.append("\t\n");
			locationsText.append(location);
		}
		throw new InvalidOperationException(
				StringHelper.format(CultureInfo.CurrentCulture,
				MvcResources.Common_TemplateNotFound,
						new Object[] { getTemplateName(), locationsText }));
	}

}
