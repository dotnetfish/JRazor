package com.superstudio.template.mvc.actionresult;

import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.template.mvc.context.RenderContext;

import java.io.IOException;

/**
 * Created by Chaoqun on 2015/11/11.
 */
public abstract class ActionResult {

    public abstract  void execute(RenderContext context) throws IOException, ArgumentNullException, InvalidOperationException, ArgumentException, InstantiationException, IllegalAccessException, ClassNotFoundException;
}
