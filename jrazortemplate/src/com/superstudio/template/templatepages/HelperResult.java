package com.superstudio.template.templatepages;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.superstudio.commons.csharpbridge.action.ActionOne;
import com.superstudio.commons.exception.ArgumentNullException;

public class HelperResult {
	private final ActionOne<Writer> _action;

    public HelperResult(ActionOne<Writer> action) throws ArgumentNullException
    {
        if (action == null)
        {
            throw new ArgumentNullException("action");
        }
        _action = action;
    }

    public String toHtmlString()
    {
        return toString();
    }
    @Override
    public  String toString() 
    {
        try (StringWriter writer = new StringWriter())
        {
            _action.execute(writer);
            return writer.getBuffer().toString();
        }catch(IOException e){
        	e.printStackTrace();
        	return  "";
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
    }

   

	public void writeTo(Writer writer) {
		// TODO Auto-generated method stub
		_action.execute(writer);
	}
}
