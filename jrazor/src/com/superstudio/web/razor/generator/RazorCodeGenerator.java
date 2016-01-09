package com.superstudio.web.razor.generator;

import com.superstudio.codedom.CodeConstructor;
import com.superstudio.codedom.CodeNamespaceImport;
import com.superstudio.codedom.CodeTypeReference;
import com.superstudio.codedom.MemberAttributes;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.CommonResources;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.RazorEngineHost;
import com.superstudio.web.razor.parser.ParserVisitor;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.parser.syntaxTree.Span;

import java.util.List;



public abstract class RazorCodeGenerator extends ParserVisitor
{
	private CodeGeneratorContext _context;

	protected RazorCodeGenerator(String className, String rootNamespaceName, String sourceFileName, RazorEngineHost host) throws Exception
	{
		if (StringHelper.isNullOrEmpty(className))
		{
			throw new IllegalArgumentException(CommonResources.getArgument_Cannot_Be_Null_Or_Empty()+ "className");
		}
		if (rootNamespaceName == null)
		{
			//throw new ArgumentNullException("rootNamespaceName");
		}
		if (host == null)
		{
			//throw new ArgumentNullException("host");
		}

		setClassName(className);
		setRootNamespaceName(rootNamespaceName);
		setSourceFileName(sourceFileName);
		setGenerateLinePragmas(StringHelper.isNullOrEmpty(getSourceFileName()) ? false : true);
		setHost(host);
	}

	// Data pulled from constructor
	private String privateClassName;
	public final String getClassName()
	{
		return privateClassName;
	}
	private void setClassName(String value)
	{
		privateClassName = value;
	}
	private String privateRootNamespaceName;
	public final String getRootNamespaceName()
	{
		return privateRootNamespaceName;
	}
	private void setRootNamespaceName(String value)
	{
		privateRootNamespaceName = value;
	}
	private String privateSourceFileName;
	public final String getSourceFileName()
	{
		return privateSourceFileName;
	}
	private void setSourceFileName(String value)
	{
		privateSourceFileName = value;
	}
	private RazorEngineHost privateHost;
	public final RazorEngineHost getHost()
	{
		return privateHost;
	}
	private void setHost(RazorEngineHost value)
	{
		privateHost = value;
	}

	// Generation settings
	private boolean privateGenerateLinePragmas;
	public final boolean getGenerateLinePragmas()
	{
		return privateGenerateLinePragmas;
	}
	public final void setGenerateLinePragmas(boolean value)
	{
		privateGenerateLinePragmas = value;
	}
	private boolean privateDesignTimeMode;
	public final boolean getDesignTimeMode()
	{
		return privateDesignTimeMode;
	}
	public final void setDesignTimeMode(boolean value)
	{
		privateDesignTimeMode = value;
	}

	public final CodeGeneratorContext getContext()
	{
		ensureContextInitialized();
		return _context;
	}

	public Func<CodeWriter> getCodeWriterFactory()
	{
		return null;
	}

	@Override
	public void visitStartBlock(Block block)
	{
		block.getCodeGenerator().generateStartBlockCode(block, getContext());
	}

	@Override
	public void visitEndBlock(Block block)
	{
		block.getCodeGenerator().generateEndBlockCode(block, getContext());
	}

	@Override
	public void visitSpan(Span span)
	{
		span.getCodeGenerator().generateCode(span, getContext());
	}

	@Override
	public void onComplete()
	{
		getContext().FlushBufferedStatement();
	}

	private void ensureContextInitialized()
	{
		if (_context == null)
		{
			_context = CodeGeneratorContext.create(getHost(), getCodeWriterFactory(), getClassName(), getRootNamespaceName(), getSourceFileName(), getGenerateLinePragmas());
			initialize(_context);
		}
	}

	
	public void visit(ParserResults result) throws Exception
    {
       
        if (result == null)
        {
           throw new ArgumentNullException("result");
        }

        result.getDocument().accept(this);
        for(RazorError error : result.getParserErrors())
        {
            visitError(error);
        }
        onComplete();
    }
	protected void initialize(CodeGeneratorContext context)
	{
		List<CodeNamespaceImport> imports=CollectionHelper.select(getHost().getNamespaceImports(),s -> new CodeNamespaceImport(s));
		context.getNamespace().getImports().AddRange(imports.toArray(new CodeNamespaceImport[imports.size()]));

		if (!StringHelper.isNullOrEmpty(getHost().getDefaultBaseClass()))
		{
			context.getGeneratedClass().getBaseTypes().add(new CodeTypeReference(getHost().getDefaultBaseClass()));
		}

		// Dev10 Bug 937438: Generate explicit Parameter-less constructor on Razor generated class
		CodeConstructor tempVar = new CodeConstructor();
		tempVar.setAttributes( MemberAttributes.forValue(MemberAttributes.Public)) ;
		context.getGeneratedClass().getMembers().Add(tempVar);
	}
}