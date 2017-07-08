package com.superstudio.template.templatepages;

import com.superstudio.commons.exception.ArgumentNullException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Consumer;

public class HelperResult {
	private final Consumer<Writer> _action;

    public HelperResult(Consumer<Writer> action) throws ArgumentNullException
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
            _action.accept(writer);
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

		_action.accept(writer);
	}
}
