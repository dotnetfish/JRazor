package com.superstudio.codedom.compiler;

import java.io.FileNotFoundException;

import java.util.*;

import com.superstudio.codedom.*;
import com.superstudio.commons.SR;
import com.superstudio.commons.TypeAttributes;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.commons.io.TextWriter;

 
//ORIGINAL LINE: [PermissionSet(SecurityAction.LinkDemand, Name = "FullTrust"), PermissionSet(SecurityAction.InheritanceDemand, Name = "FullTrust")] public abstract class CodeGenerator : ICodeGenerator
public abstract class CodeGenerator implements ICodeGenerator
{
	private static final int ParameterMultilineThreshold = 15;

	private IndentedTextWriter output;

	private CodeGeneratorOptions options;

	private CodeTypeDeclaration currentClass;

	private CodeTypeMember currentMember;

	private boolean inNestedBinary;

	protected final CodeTypeDeclaration getCurrentClass()
	{
		return this.currentClass;
	}

	protected final String getCurrentTypeName()
	{
		if (this.currentClass != null)
		{
			return this.currentClass.getName();
		}
		return "<% unknown %>";
	}

	protected final CodeTypeMember getCurrentMember()
	{
		return this.currentMember;
	}

	protected final String getCurrentMemberName()
	{
		if (this.currentMember != null)
		{
			return this.currentMember.getName();
		}
		return "<% unknown %>";
	}

	protected final boolean getIsCurrentInterface()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsInterface();
	}

	protected final boolean getIsCurrentClass()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsClass();
	}

	protected final boolean getIsCurrentStruct()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsStruct();
	}

	protected final boolean getIsCurrentEnum()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsEnum();
	}

	protected final boolean getIsCurrentDelegate()
	{
		return this.currentClass != null && this.currentClass instanceof CodeTypeDelegate;
	}

	protected final int getIndent()
	{
		return this.output.getIndent();
	}
	protected final void setIndent(int value)
	{
		this.output.setIndent(value);
	}

	protected abstract String getNullToken();

	protected final TextWriter getOutput()
	{
		return this.output;
	}

	protected final CodeGeneratorOptions getOptions()
	{
		return this.options;
	}

	private void GenerateType(CodeTypeDeclaration e) throws Exception {
		this.currentClass = e;
		if (e.getStartDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getStartDirectives());
		}
		this.GenerateCommentStatements(e.getComments());
		if (e.getLinePragma() != null)
		{
			this.GenerateLinePragmaStart(e.getLinePragma());
		}
		this.GenerateTypeStart(e);
		if (this.getOptions().getVerbatimOrder())
		{
			Iterator enumerator = e.getMembers().iterator();
			try
			{
				while (enumerator.hasNext())
				{
					CodeTypeMember member = (CodeTypeMember)enumerator.next();
					this.GenerateTypeMember(member, e);
				}
 
				this.currentClass = e;
				this.GenerateTypeEnd(e);
				if (e.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(e.getLinePragma());
				}
				if (e.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(e.getEndDirectives());
				}
				return;
			}
			finally
			{
				java.io.Closeable disposable = (java.io.Closeable)((enumerator instanceof java.io.Closeable) ? enumerator : null);
				if (disposable != null)
				{
					disposable.close();
				}
			}
		}
		this.GenerateFields(e);
		this.GenerateSnippetMembers(e);
		this.GenerateTypeConstructors(e);
		this.GenerateConstructors(e);
		this.GenerateProperties(e);
		this.GenerateEvents(e);
		this.GenerateMethods(e);
		this.GenerateNestedTypes(e);


	}

	protected void GenerateDirectives(CodeDirectiveCollection directives)
	{
	}

	private void GenerateTypeMember(CodeTypeMember member, CodeTypeDeclaration declaredType) throws Exception
	{
		if (this.options.getBlankLinesBetweenMembers())
		{
			this.getOutput().WriteLine();
		}
		if (member instanceof CodeTypeDeclaration)
		{
			((ICodeGenerator)this).generateCodeFromType((CodeTypeDeclaration)member, this.output.getInnerWriter(), this.options);
			this.currentClass = declaredType;
			return;
		}
		if (member.getStartDirectives().size() > 0)
		{
			this.GenerateDirectives(member.getStartDirectives());
		}
		this.GenerateCommentStatements(member.getComments());
		if (member.getLinePragma() != null)
		{
			this.GenerateLinePragmaStart(member.getLinePragma());
		}
		if (member instanceof CodeMemberField)
		{
			this.GenerateField((CodeMemberField)member);
		}
		else if (member instanceof CodeMemberProperty)
		{
			this.GenerateProperty((CodeMemberProperty)member, declaredType);
		}
		else if (member instanceof CodeMemberMethod)
		{
			if (member instanceof CodeConstructor)
			{
				this.GenerateConstructor((CodeConstructor)member, declaredType);
			}
			else if (member instanceof CodeTypeConstructor)
			{
				this.GenerateTypeConstructor((CodeTypeConstructor)member);
			}
			else if (member instanceof CodeEntryPointMethod)
			{
				this.GenerateEntryPointMethod((CodeEntryPointMethod)member, declaredType);
			}
			else
			{
				this.GenerateMethod((CodeMemberMethod)member, declaredType);
			}
		}
		else if (member instanceof CodeMemberEvent)
		{
			this.GenerateEvent((CodeMemberEvent)member, declaredType);
		}
		else if (member instanceof CodeSnippetTypeMember)
		{
			int indent = this.getIndent();
			this.setIndent(0);
			this.GenerateSnippetMember((CodeSnippetTypeMember)member);
			this.setIndent(indent);
			output.WriteLine();
		}
		if (member.getLinePragma() != null)
		{
			this.GenerateLinePragmaEnd(member.getLinePragma());
		}
		if (member.getEndDirectives().size() > 0)
		{
			this.GenerateDirectives(member.getEndDirectives());
		}
	}

	private void GenerateTypeConstructors(CodeTypeDeclaration e) throws Exception
	{
		Iterator<CodeTypeMember> enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeTypeConstructor)
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeTypeConstructor codeTypeConstructor = (CodeTypeConstructor)current;
				if (codeTypeConstructor.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeTypeConstructor.getLinePragma());
				}
				this.GenerateTypeConstructor(codeTypeConstructor);
				if (codeTypeConstructor.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeTypeConstructor.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	protected final void GenerateNamespaces(CodeCompileUnit e) throws Exception, Exception
	{
		for (Object e2 : e.getNamespaces())
		{
			((ICodeGenerator)this).generateCodeFromNamespace((CodeNamespace)e2, this.output.getInnerWriter(), this.options);
		}
	}

	protected final void GenerateTypes(CodeNamespace e) throws Exception, Exception
	{
		for (Object e2 : e.getTypes())
		{
			if (this.options.getBlankLinesBetweenMembers())
			{
				this.output.WriteLine();
			}
			((ICodeGenerator)this).generateCodeFromType((CodeTypeDeclaration)e2, this.output.getInnerWriter(), this.options);
		}
	}

	/*public final boolean Supports(GeneratorSupport support)
	{
		return this.Supports(support);
	}*/

	public final void generateCodeFromType(CodeTypeDeclaration e, TextWriter w, CodeGeneratorOptions o) throws FileNotFoundException
	{
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter())
		{
			throw new IllegalStateException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null)
		{
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try
		{
			this.GenerateType(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally
		{
			if (flag)
			{
				this.output = null;
				this.options = null;
			}
		}
	}

	public final void generateCodeFromExpression(CodeExpression e, TextWriter w, CodeGeneratorOptions o) throws Exception
	{
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter())
		{
			throw new IllegalStateException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null)
		{
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try
		{
			this.GenerateExpression(e);
		}
		finally
		{
			if (flag)
			{
				this.output = null;
				this.options = null;
			}
		}
	}

	public final void generateCodeFromCompileUnit(CodeCompileUnit e, TextWriter w, CodeGeneratorOptions o) throws Exception
	{
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter())
		{
			throw new IllegalStateException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null)
		{
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try
		{
			if (e instanceof CodeSnippetCompileUnit)
			{
				this.GenerateSnippetCompileUnit((CodeSnippetCompileUnit)e);
			}
			else
			{
				this.GenerateCompileUnit(e);
			}
		}
		finally
		{
			if (flag)
			{
				this.output = null;
				this.options = null;
			}
		}
	}

	public final void generateCodeFromNamespace(CodeNamespace e, TextWriter w, CodeGeneratorOptions o) throws Exception
	{
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter())
		{
			throw new IllegalStateException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null)
		{
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try
		{
			this.GenerateNamespace(e);
		}
		finally
		{
			if (flag)
			{
				this.output = null;
				this.options = null;
			}
		}
	}

	public final void generateCodeFromStatement(CodeStatement e, TextWriter w, CodeGeneratorOptions o)
			throws Exception
	{
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter())
		{
			throw new IllegalStateException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null)
		{
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try
		{
			this.GenerateStatement(e);
		}
		finally
		{
			if (flag)
			{
				this.output = null;
				this.options = null;
			}
		}
	}

	public void GenerateCodeFromMember(CodeTypeMember member, TextWriter writer, CodeGeneratorOptions options) throws Exception
	{
		if (this.output != null)
		{
			throw new IllegalStateException(SR.GetString("CodeGenReentrance"));
		}
		this.options = ((options == null) ? new CodeGeneratorOptions() : options);
		this.output = new IndentedTextWriter(writer, this.options.getIndentString());
		try
		{
			CodeTypeDeclaration declaredType = new CodeTypeDeclaration();
			this.currentClass = declaredType;
			this.GenerateTypeMember(member, declaredType);
		}
		finally
		{
			this.currentClass = null;
			this.output = null;
			this.options = null;
		}
	}

	/*public final boolean IsValidIdentifier(String value)
	{
		return this.IsValidIdentifier(value);
	}

	public final void ValidateIdentifier(String value)
	{
		this.ValidateIdentifier(value);
	}*/

	/*public final String CreateEscapedIdentifier(String value)
	{
		return this.CreateEscapedIdentifier(value);
	}
*/
/*	public final String CreateValidIdentifier(String value)
	{
		return this.CreateValidIdentifier(value);
	}*/

/*	public final String GetTypeOutput(CodeTypeReference type)
	{
		return this.GetTypeOutput(type);
	}
*/
	private void GenerateConstructors(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeConstructor)
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeConstructor codeConstructor = (CodeConstructor)current;
				if (codeConstructor.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeConstructor.getLinePragma());
				}
				this.GenerateConstructor(codeConstructor, e);
				if (codeConstructor.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeConstructor.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void GenerateEvents(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeMemberEvent)
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeMemberEvent codeMemberEvent = (CodeMemberEvent)current;
				if (codeMemberEvent.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeMemberEvent.getLinePragma());
				}
				this.GenerateEvent(codeMemberEvent, e);
				if (codeMemberEvent.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeMemberEvent.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	protected final void GenerateExpression(CodeExpression e) throws Exception
	{
		if (e instanceof CodeArrayCreateExpression)
		{
			this.GenerateArrayCreateExpression((CodeArrayCreateExpression)e);
			return;
		}
		if (e instanceof CodeBaseReferenceExpression)
		{
			this.GenerateBaseReferenceExpression((CodeBaseReferenceExpression)e);
			return;
		}
		if (e instanceof CodeBinaryOperatorExpression)
		{
			this.GenerateBinaryOperatorExpression((CodeBinaryOperatorExpression)e);
			return;
		}
		if (e instanceof CodeCastExpression)
		{
			this.GenerateCastExpression((CodeCastExpression)e);
			return;
		}
		if (e instanceof CodeDelegateCreateExpression)
		{
			this.GenerateDelegateCreateExpression((CodeDelegateCreateExpression)e);
			return;
		}
		if (e instanceof CodeFieldReferenceExpression)
		{
			this.GenerateFieldReferenceExpression((CodeFieldReferenceExpression)e);
			return;
		}
		if (e instanceof CodeArgumentReferenceExpression)
		{
			this.GenerateArgumentReferenceExpression((CodeArgumentReferenceExpression)e);
			return;
		}
		if (e instanceof CodeVariableReferenceExpression)
		{
			this.GenerateVariableReferenceExpression((CodeVariableReferenceExpression)e);
			return;
		}
		if (e instanceof CodeIndexerExpression)
		{
			this.GenerateIndexerExpression((CodeIndexerExpression)e);
			return;
		}
		if (e instanceof CodeArrayIndexerExpression)
		{
			this.GenerateArrayIndexerExpression((CodeArrayIndexerExpression)e);
			return;
		}
		if (e instanceof CodeSnippetExpression)
		{
			this.GenerateSnippetExpression((CodeSnippetExpression)e);
			return;
		}
		if (e instanceof CodeMethodInvokeExpression)
		{
			this.GenerateMethodInvokeExpression((CodeMethodInvokeExpression)e);
			return;
		}
		if (e instanceof CodeMethodReferenceExpression)
		{
			this.GenerateMethodReferenceExpression((CodeMethodReferenceExpression)e);
			return;
		}
		if (e instanceof CodeEventReferenceExpression)
		{
			this.GenerateEventReferenceExpression((CodeEventReferenceExpression)e);
			return;
		}
		if (e instanceof CodeDelegateInvokeExpression)
		{
			this.GenerateDelegateInvokeExpression((CodeDelegateInvokeExpression)e);
			return;
		}
		if (e instanceof CodeObjectCreateExpression)
		{
			this.GenerateObjectCreateExpression((CodeObjectCreateExpression)e);
			return;
		}
		if (e instanceof CodeParameterDeclarationExpression)
		{
			this.GenerateParameterDeclarationExpression((CodeParameterDeclarationExpression)e);
			return;
		}
		if (e instanceof CodeDirectionExpression)
		{
			this.GenerateDirectionExpression((CodeDirectionExpression)e);
			return;
		}
		if (e instanceof CodePrimitiveExpression)
		{
			this.GeneratePrimitiveExpression((CodePrimitiveExpression)e);
			return;
		}
		if (e instanceof CodePropertyReferenceExpression)
		{
			this.GeneratePropertyReferenceExpression((CodePropertyReferenceExpression)e);
			return;
		}
		if (e instanceof CodePropertySetValueReferenceExpression)
		{
			this.GeneratePropertySetValueReferenceExpression((CodePropertySetValueReferenceExpression)e);
			return;
		}
		if (e instanceof CodeThisReferenceExpression)
		{
			this.GenerateThisReferenceExpression((CodeThisReferenceExpression)e);
			return;
		}
		if (e instanceof CodeTypeReferenceExpression)
		{
			this.GenerateTypeReferenceExpression((CodeTypeReferenceExpression)e);
			return;
		}
		if (e instanceof CodeTypeOfExpression)
		{
			this.GenerateTypeOfExpression((CodeTypeOfExpression)e);
			return;
		}
		if (e instanceof CodeDefaultValueExpression)
		{
			this.GenerateDefaultValueExpression((CodeDefaultValueExpression)e);
			return;
		}
		if (e == null)
		{
			throw new IllegalArgumentException("e");
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void GenerateFields(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeMemberField)
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeMemberField codeMemberField = (CodeMemberField)current;
				if (codeMemberField.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeMemberField.getLinePragma());
				}
				this.GenerateField(codeMemberField);
				if (codeMemberField.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeMemberField.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void GenerateSnippetMembers(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		boolean flag = false;
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeSnippetTypeMember)
			{
				flag = true;
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					try {
						this.getOutput().WriteLine();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeSnippetTypeMember codeSnippetTypeMember = (CodeSnippetTypeMember)current;
				if (codeSnippetTypeMember.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeSnippetTypeMember.getLinePragma());
				}
				int indent = this.getIndent();
				this.setIndent(0);
				this.GenerateSnippetMember(codeSnippetTypeMember);
				this.setIndent(indent);
				if (codeSnippetTypeMember.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeSnippetTypeMember.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
		if (flag)
		{
			this.getOutput().WriteLine();
		}
	}

	protected void GenerateSnippetCompileUnit(CodeSnippetCompileUnit e) throws Exception
	{
		this.GenerateDirectives(e.getStartDirectives());
		if (e.getLinePragma() != null)
		{
			this.GenerateLinePragmaStart(e.getLinePragma());
		}
		this.getOutput().WriteLine(e.getValue());
		if (e.getLinePragma() != null)
		{
			this.GenerateLinePragmaEnd(e.getLinePragma());
		}
		if (e.getEndDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getEndDirectives());
		}
	}

	private void GenerateMethods(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeMemberMethod && !(current instanceof CodeTypeConstructor) && !(current instanceof CodeConstructor))
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeMemberMethod codeMemberMethod = (CodeMemberMethod)current;
				if (codeMemberMethod.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeMemberMethod.getLinePragma());
				}
				if (current instanceof CodeEntryPointMethod)
				{
					this.GenerateEntryPointMethod((CodeEntryPointMethod)current, e);
				}
				else
				{
					this.GenerateMethod(codeMemberMethod, e);
				}
				if (codeMemberMethod.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeMemberMethod.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void GenerateNestedTypes(CodeTypeDeclaration e) throws Exception, InvalidOperationException
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeTypeDeclaration)
			{
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				CodeTypeDeclaration e2 = (CodeTypeDeclaration)current;
				((ICodeGenerator)this).generateCodeFromType(e2, this.output.getInnerWriter(), this.options);
			}
		}
	}

	protected void GenerateCompileUnit(CodeCompileUnit e) throws Exception
	{
		this.GenerateCompileUnitStart(e);
		this.GenerateNamespaces(e);
		this.GenerateCompileUnitEnd(e);
	}

	protected void GenerateNamespace(CodeNamespace e) throws Exception
	{
		this.GenerateCommentStatements(e.getComments());
		this.GenerateNamespaceStart(e);
		this.GenerateNamespaceImports(e);
		this.getOutput().WriteLine("");
		this.GenerateTypes(e);
		this.GenerateNamespaceEnd(e);
	}

	protected final void GenerateNamespaceImports(CodeNamespace e)
	{
		Iterator<CodeNamespaceImport> enumerator = e.getImports().iterator();
		while (enumerator.hasNext())
		{
			CodeNamespaceImport codeNamespaceImport = enumerator.next();
			if (codeNamespaceImport.getLinePragma() != null)
			{
				this.GenerateLinePragmaStart(codeNamespaceImport.getLinePragma());
			}
			this.GenerateNamespaceImport(codeNamespaceImport);
			if (codeNamespaceImport.getLinePragma() != null)
			{
				this.GenerateLinePragmaEnd(codeNamespaceImport.getLinePragma());
			}
		}
	}

	private void GenerateProperties(CodeTypeDeclaration e) throws Exception
	{
		Iterator enumerator = e.getMembers().iterator();
		while (enumerator.hasNext())
		{
			Object current = enumerator.next();
			if (current instanceof CodeMemberProperty)
			{
				this.currentMember = (CodeTypeMember)current;
				if (this.options.getBlankLinesBetweenMembers())
				{
					this.getOutput().WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getStartDirectives());
				}
				this.GenerateCommentStatements(this.currentMember.getComments());
				CodeMemberProperty codeMemberProperty = (CodeMemberProperty)current;
				if (codeMemberProperty.getLinePragma() != null)
				{
					this.GenerateLinePragmaStart(codeMemberProperty.getLinePragma());
				}
				this.GenerateProperty(codeMemberProperty, e);
				if (codeMemberProperty.getLinePragma() != null)
				{
					this.GenerateLinePragmaEnd(codeMemberProperty.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0)
				{
					this.GenerateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}
	
	protected final void GenerateStatement(CodeStatement e) throws Exception
	{
		if (e.getStartDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getStartDirectives());
		}
		if (e.getLinePragma() != null)
		{
			this.GenerateLinePragmaStart(e.getLinePragma());
		}
		if (e instanceof CodeCommentStatement)
		{
			this.GenerateCommentStatement((CodeCommentStatement)e);
		}
		else if (e instanceof CodeMethodReturnStatement)
		{
			this.GenerateMethodReturnStatement((CodeMethodReturnStatement)e);
		}
		else if (e instanceof CodeConditionStatement)
		{
			this.GenerateConditionStatement((CodeConditionStatement)e);
		}
		else if (e instanceof CodeTryCatchFinallyStatement)
		{
			this.GenerateTryCatchFinallyStatement((CodeTryCatchFinallyStatement)e);
		}
		else if (e instanceof CodeAssignStatement)
		{
			this.GenerateAssignStatement((CodeAssignStatement)e);
		}
		else if (e instanceof CodeExpressionStatement)
		{
			this.GenerateExpressionStatement((CodeExpressionStatement)e);
		}
		else if (e instanceof CodeIterationStatement)
		{
			this.GenerateIterationStatement((CodeIterationStatement)e);
		}
		else if (e instanceof CodeThrowExceptionStatement)
		{
			this.GenerateThrowExceptionStatement((CodeThrowExceptionStatement)e);
		}
		else if (e instanceof CodeSnippetStatement)
		{
			int indent = this.getIndent();
			this.setIndent(0);// = 0;
			this.GenerateSnippetStatement((CodeSnippetStatement)e);
			this.setIndent(indent);// = indent;
		}
		else if (e instanceof CodeVariableDeclarationStatement)
		{
			this.GenerateVariableDeclarationStatement((CodeVariableDeclarationStatement)e);
		}
		else if (e instanceof CodeAttachEventStatement)
		{
			this.GenerateAttachEventStatement((CodeAttachEventStatement)e);
		}
		else if (e instanceof CodeRemoveEventStatement)
		{
			this.GenerateRemoveEventStatement((CodeRemoveEventStatement)e);
		}
		else if (e instanceof CodeGotoStatement)
		{
			this.GenerateGotoStatement((CodeGotoStatement)e);
		}
		else
		{
			if (!(e instanceof CodeLabeledStatement))
			{
				//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
			}
			this.GenerateLabeledStatement((CodeLabeledStatement)e);
		}
		if (e.getLinePragma() != null)
		{
			this.GenerateLinePragmaEnd(e.getLinePragma());
		}
		if (e.getEndDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getEndDirectives());
		}
	}

	protected final void GenerateStatements(CodeStatementCollection stms) throws Exception
	{
		Iterator<CodeStatement> enumerator = stms.iterator();
		while (enumerator.hasNext())
		{
			((ICodeGenerator)this).generateCodeFromStatement(enumerator.next(), this.output.getInnerWriter(), this.options);
		}
	}

	protected void OutputAttributeDeclarations(CodeAttributeDeclarationCollection attributes) throws Exception
	{
		if (attributes.size() == 0)
		{
			return;
		}
		this.GenerateAttributeDeclarationsStart(attributes);
		boolean flag = true;
		Iterator<CodeAttributeDeclaration> enumerator = attributes.iterator();
		while (enumerator.hasNext())
		{
			if (flag)
			{
				flag = false;
			}
			else
			{
				this.ContinueOnNewLine(", ");
			}
			CodeAttributeDeclaration codeAttributeDeclaration = enumerator.next();
			this.output.Write(codeAttributeDeclaration.getName());
			this.output.Write("(");
			boolean flag2 = true;
			for (Object arg : codeAttributeDeclaration.getArguments())
			{
				if (flag2)
				{
					flag2 = false;
				}
				else
				{
					this.output.Write(", ");
				}
				this.OutputAttributeArgument((CodeAttributeArgument)arg);
			}
			this.output.Write(")");
		}
		this.GenerateAttributeDeclarationsEnd(attributes);
	}

	protected void OutputAttributeArgument(CodeAttributeArgument arg) throws Exception
	{
		if (arg.getName() != null && arg.getName().length() > 0)
		{
			OutputIdentifier(arg.getName());
			this.output.Write("=");
		}
		((ICodeGenerator)this).generateCodeFromExpression(arg.getValue(), this.output.getInnerWriter(), this.options);
	}

	protected void OutputDirection(FieldDirection dir) throws Exception
	{
		switch (dir)
		{
			case In:
				break;
			case Out:
				this.output.Write("out ");
				return;
			case Ref:
				this.output.Write("ref ");
				break;
			default:
				return;
		}
	}

	protected void OutputFieldScopeModifier(MemberAttributes attributes) throws Exception
	{
		MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.VTableMask);
		if (memberAttributes.getValue() == MemberAttributes.New)
		{
			this.output.Write("new ");
		}
		switch ((attributes.getValue() & MemberAttributes.ScopeMask))
		{
			case MemberAttributes.Final:
			case MemberAttributes.Override:
				break;
			case MemberAttributes.Static:
				this.output.Write("static ");
				return;
			case MemberAttributes.Const:
				this.output.Write("const ");
				break;
			default:
				return;
		}
	}

	protected void OutputMemberAccessModifier(MemberAttributes attributes) throws Exception
	{
		MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.AccessMask);
		if (memberAttributes.getValue() <= MemberAttributes.Family)
		{
			if (memberAttributes.getValue() == MemberAttributes.Assembly)
			{
				this.output.Write("internal ");
				return;
			}
			if (memberAttributes.getValue() == MemberAttributes.FamilyAndAssembly)
			{
				this.output.Write("internal ");
				return;
			}
			if (memberAttributes.getValue() != MemberAttributes.Family)
			{
				return;
			}
			this.output.Write("protected ");
			return;
		}
		else
		{
			if (memberAttributes.getValue() == MemberAttributes.FamilyOrAssembly)
			{
				this.output.Write("protected internal ");
				return;
			}
			if (memberAttributes.getValue() == MemberAttributes.Private)
			{
				this.output.Write("private ");
				return;
			}
			if (memberAttributes.getValue() != MemberAttributes.Public)
			{
				return;
			}
			this.output.Write("public ");
			return;
		}
	}

	protected void OutputMemberScopeModifier(MemberAttributes attributes) throws Exception
	{
		MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.VTableMask);
		if (memberAttributes.getValue() == MemberAttributes.New)
		{
			this.output.Write("new ");
		}
		switch (attributes.getValue() & MemberAttributes.ScopeMask)
		{
			case MemberAttributes.Abstract:
				this.output.Write("abstract ");
				return;
			case MemberAttributes.Final:
				this.output.Write("");
				return;
			case MemberAttributes.Static:
				this.output.Write("static ");
				return;
			case MemberAttributes.Override:
				this.output.Write("override ");
				return;
			default:
				memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.AccessMask);
				if (memberAttributes.getValue() == MemberAttributes.Family || memberAttributes.getValue() == MemberAttributes.Public)
				{
					this.output.Write("virtual ");
				}
				return;
		}
	}

	protected abstract void OutputType(CodeTypeReference typeRef);

	protected void OutputTypeAttributes(TypeAttributes attributes, boolean isStruct, boolean isEnum) throws Exception
	{
		switch ((attributes.getValue() & TypeAttributes.VisibilityMask))
		{
			case TypeAttributes.Public:
			case TypeAttributes.NestedPublic:
				this.output.Write("public ");
				break;
			case TypeAttributes.NestedPrivate:
				this.output.Write("private ");
				break;
		}
		if (isStruct)
		{
			this.output.Write("struct ");
			return;
		}
		if (isEnum)
		{
			this.output.Write("enum ");
			return;
		}
		TypeAttributes typeAttributes = TypeAttributes.forValue(attributes.getValue() & TypeAttributes.ClassSemanticsMask);
		if (typeAttributes.getValue() == TypeAttributes.NotPublic)
		{
			if ((attributes.getValue() & TypeAttributes.Sealed) == TypeAttributes.Sealed)
			{
				this.output.Write("sealed ");
			}
			if ((attributes.getValue() & TypeAttributes.Abstract) == TypeAttributes.Abstract)
			{
				this.output.Write("abstract ");
			}
			this.output.Write("class ");
			return;
		}
		if (typeAttributes.getValue() != TypeAttributes.ClassSemanticsMask)
		{
			return;
		}
		this.output.Write("interface ");
	}

	protected void OutputTypeNamePair(CodeTypeReference typeRef, String name) throws Exception
	{
		this.OutputType(typeRef);
		this.output.Write(" ");
		this.OutputIdentifier(name);
	}

	protected void OutputIdentifier(String ident) throws Exception
	{
		this.output.Write(ident);
	}

	protected void OutputExpressionList(CodeExpressionCollection expressions) throws Exception
	{
		this.OutputExpressionList(expressions, false);
	}

	protected void OutputExpressionList(CodeExpressionCollection expressions, boolean newlineBetweenItems) throws Exception
	{
		boolean flag = true;
		Iterator<CodeExpression> enumerator = expressions.iterator();
		int indent = this.getIndent();
		this.setIndent(indent + 1) ;
		while (enumerator.hasNext())
		{
			if (flag)
			{
				flag = false;
			}
			else if (newlineBetweenItems)
			{
				this.ContinueOnNewLine(",");
			}
			else
			{
				this.output.Write(", ");
			}
			((ICodeGenerator)this).generateCodeFromExpression(enumerator.next(), this.output.getInnerWriter(), this.options);
		}
		indent = this.getIndent();
		this.setIndent(indent - 1)  ;
	}

	protected void OutputOperator(CodeBinaryOperatorType op) throws Exception
	{
		switch (op)
		{
			case Add:
				this.output.Write("+");
				return;
			case Subtract:
				this.output.Write("-");
				return;
			case Multiply:
				this.output.Write("*");
				return;
			case Divide:
				this.output.Write("/");
				return;
			case Modulus:
				this.output.Write("%");
				return;
			case Assign:
				this.output.Write("=");
				return;
			case IdentityInequality:
				this.output.Write("!=");
				return;
			case IdentityEquality:
				this.output.Write("==");
				return;
			case ValueEquality:
				this.output.Write("==");
				return;
			case BitwiseOr:
				this.output.Write("|");
				return;
			case BitwiseAnd:
				this.output.Write("&");
				return;
			case BooleanOr:
				this.output.Write("||");
				return;
			case BooleanAnd:
				this.output.Write("&&");
				return;
			case LessThan:
				this.output.Write("<");
				return;
			case LessThanOrEqual:
				this.output.Write("<=");
				return;
			case GreaterThan:
				this.output.Write(">");
				return;
			case GreaterThanOrEqual:
				this.output.Write(">=");
				return;
			default:
				return;
		}
	}

	protected void OutputParameters(CodeParameterDeclarationExpressionCollection parameters) throws Exception
	{
		boolean flag = true;
		boolean flag2 = parameters.size() > 15;
		if (flag2)
		{
			this.setIndent(this.getIndent()+3);
		}
		Iterator enumerator = parameters.iterator();
		while (enumerator.hasNext())
		{
			CodeParameterDeclarationExpression e = (CodeParameterDeclarationExpression)enumerator.next();
			if (flag)
			{
				flag = false;
			}
			else
			{
				this.output.Write(", ");
			}
			if (flag2)
			{
				this.ContinueOnNewLine("");
			}
			this.GenerateExpression(e);
		}
		if (flag2)
		{
			this.setIndent(this.getIndent()-3) ;//-= 3;
		}
	}

	protected abstract void GenerateArrayCreateExpression(CodeArrayCreateExpression e);

	protected abstract void GenerateBaseReferenceExpression(CodeBaseReferenceExpression e);

	protected void GenerateBinaryOperatorExpression(CodeBinaryOperatorExpression e) throws Exception
	{
		boolean flag = false;
		this.output.Write("(");
		this.GenerateExpression(e.getLeft());
		this.output.Write(" ");
		if (e.getLeft() instanceof CodeBinaryOperatorExpression || e.getRight() instanceof CodeBinaryOperatorExpression)
		{
			if (!this.inNestedBinary)
			{
				flag = true;
				this.inNestedBinary = true;
				this.setIndent(this.getIndent()+3);
			}
			this.ContinueOnNewLine("");
		}
		this.OutputOperator(e.getOperator());
		this.output.Write(" ");
		this.GenerateExpression(e.getRight());
		this.output.Write(")");
		if (flag)
		{
			this.setIndent(this.getIndent()-3);
			this.inNestedBinary = false;
		}
	}

	protected void ContinueOnNewLine(String st) throws Exception
	{
		this.output.WriteLine(st);
	}

	protected abstract void GenerateCastExpression(CodeCastExpression e);

	protected abstract void GenerateDelegateCreateExpression(CodeDelegateCreateExpression e);

	protected abstract void GenerateFieldReferenceExpression(CodeFieldReferenceExpression e);

	protected abstract void GenerateArgumentReferenceExpression(CodeArgumentReferenceExpression e);

	protected abstract void GenerateVariableReferenceExpression(CodeVariableReferenceExpression e);

	protected abstract void GenerateIndexerExpression(CodeIndexerExpression e);

	protected abstract void GenerateArrayIndexerExpression(CodeArrayIndexerExpression e);

	protected abstract void GenerateSnippetExpression(CodeSnippetExpression e);

	protected abstract void GenerateMethodInvokeExpression(CodeMethodInvokeExpression e);

	protected abstract void GenerateMethodReferenceExpression(CodeMethodReferenceExpression e);

	protected abstract void GenerateEventReferenceExpression(CodeEventReferenceExpression e);

	protected abstract void GenerateDelegateInvokeExpression(CodeDelegateInvokeExpression e);

	protected abstract void GenerateObjectCreateExpression(CodeObjectCreateExpression e);

	protected void GenerateParameterDeclarationExpression(CodeParameterDeclarationExpression e) throws Exception
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.OutputAttributeDeclarations(e.getCustomAttributes());
			this.output.Write(" ");
		}
		this.OutputDirection(e.getDirection());
		this.OutputTypeNamePair(e.getType(), e.getName());
	}

	protected void GenerateDirectionExpression(CodeDirectionExpression e) throws Exception
	{
		this.OutputDirection(e.getDirection());
		this.GenerateExpression(e.getExpression());
	}

	protected void GeneratePrimitiveExpression(CodePrimitiveExpression e) throws Exception
	{
		if (e.getValue() == null)
		{
			this.output.Write(this.getNullToken());
			return;
		}
		if (e.getValue() instanceof String)
		{
			this.output.Write(this.QuoteSnippetString((String)e.getValue()));
			return;
		}
		if (e.getValue() instanceof Character)
		{
			this.output.Write("'" + e.getValue().toString() + "'");
			return;
		}
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: if (e.getValue() is byte)
		if (e.getValue() instanceof Byte)
		{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: this.output.Write(((byte)e.getValue()).ToString(CultureInfo.InvariantCulture));
			this.output.Write((new Byte((byte)e.getValue())).toString());
			return;
		}
		if (e.getValue() instanceof Short)
		{
			this.output.Write((new Short((short)e.getValue())).toString());
			return;
		}
		if (e.getValue() instanceof Integer)
		{
			this.output.Write((new Integer((int)e.getValue())).toString());
			return;
		}
		if (e.getValue() instanceof Long)
		{
			this.output.Write(String.valueOf((new Long((long)e.getValue()))));
			return;
		}
		if (e.getValue() instanceof Float)
		{
			this.GenerateSingleFloatValue((float)e.getValue());
			return;
		}
		if (e.getValue() instanceof Double)
		{
			this.GenerateDoubleValue((double)e.getValue());
			return;
		}
		if (e.getValue() instanceof java.math.BigDecimal)
		{
			this.GenerateDecimalValue((java.math.BigDecimal)e.getValue());
			return;
		}
		if (!(e.getValue() instanceof Boolean))
		{
			throw new IllegalArgumentException(SR.GetString("InvalidPrimitiveType", new Object[] {e.getValue().getClass().toString()}));
		}
		if ((boolean)e.getValue())
		{
			this.output.Write("true");
			return;
		}
		this.output.Write("false");
	}

	protected void GenerateSingleFloatValue(float s) throws Exception
	{
		//this.output.Write((new Float(s)).toString("R"));
		this.output.Write((new Float(s)).toString());
	}

	protected void GenerateDoubleValue(double d) throws Exception
	{
		//this.output.Write((new Double(d)).toString("R"));
		this.output.Write((new Float(d)).toString());
	}

	protected void GenerateDecimalValue(java.math.BigDecimal d) throws Exception
	{
		this.output.Write(d.toString());
	}

	protected void GenerateDefaultValueExpression(CodeDefaultValueExpression e)
	{
	}

	protected abstract void GeneratePropertyReferenceExpression(CodePropertyReferenceExpression e);

	protected abstract void GeneratePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e);

	protected abstract void GenerateThisReferenceExpression(CodeThisReferenceExpression e);

	protected void GenerateTypeReferenceExpression(CodeTypeReferenceExpression e)
	{
		
		this.OutputType(e.getType());
	}

	protected void GenerateTypeOfExpression(CodeTypeOfExpression e) throws Exception
	{
		this.output.Write("typeof(");
		this.OutputType(e.getType());
		this.output.Write(")");
	}

	protected abstract void GenerateExpressionStatement(CodeExpressionStatement e);

	protected abstract void GenerateIterationStatement(CodeIterationStatement e);

	protected abstract void GenerateThrowExceptionStatement(CodeThrowExceptionStatement e);

	protected void GenerateCommentStatement(CodeCommentStatement e)
	{
		if (e.getComment() == null)
		{
			//throw new IllegalArgumentException(SR.GetString("Argument_NullComment", new Object[] {"e"}), "e");
		}
		this.GenerateComment(e.getComment());
	}

	protected void GenerateCommentStatements(CodeCommentStatementCollection e)
	{
		for (Object e2 : e)
		{
			this.GenerateCommentStatement((CodeCommentStatement)e2);
		}
	}

	protected abstract void GenerateComment(CodeComment e);

	protected abstract void GenerateMethodReturnStatement(CodeMethodReturnStatement e);

	protected abstract void GenerateConditionStatement(CodeConditionStatement e);

	protected abstract void GenerateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e);

	protected abstract void GenerateAssignStatement(CodeAssignStatement e);

	protected abstract void GenerateAttachEventStatement(CodeAttachEventStatement e);

	protected abstract void GenerateRemoveEventStatement(CodeRemoveEventStatement e);

	protected abstract void GenerateGotoStatement(CodeGotoStatement e);

	protected abstract void GenerateLabeledStatement(CodeLabeledStatement e);

	protected void GenerateSnippetStatement(CodeSnippetStatement e) throws Exception
	{
		this.output.WriteLine(e.getValue());
	}

	protected abstract void GenerateVariableDeclarationStatement(CodeVariableDeclarationStatement e);

	protected abstract void GenerateLinePragmaStart(CodeLinePragma e);

	protected abstract void GenerateLinePragmaEnd(CodeLinePragma e);

	protected abstract void GenerateEvent(CodeMemberEvent e, CodeTypeDeclaration c);

	protected abstract void GenerateField(CodeMemberField e);

	protected abstract void GenerateSnippetMember(CodeSnippetTypeMember e);

	protected abstract void GenerateEntryPointMethod(CodeEntryPointMethod e, CodeTypeDeclaration c);

	protected abstract void GenerateMethod(CodeMemberMethod e, CodeTypeDeclaration c);

	protected abstract void GenerateProperty(CodeMemberProperty e, CodeTypeDeclaration c);

	protected abstract void GenerateConstructor(CodeConstructor e, CodeTypeDeclaration c);

	protected abstract void GenerateTypeConstructor(CodeTypeConstructor e);

	protected abstract void GenerateTypeStart(CodeTypeDeclaration e);

	protected abstract void GenerateTypeEnd(CodeTypeDeclaration e);

	protected void GenerateCompileUnitStart(CodeCompileUnit e)
	{
		if (e.getStartDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getStartDirectives());
		}
	}

	protected void GenerateCompileUnitEnd(CodeCompileUnit e)
	{
		if (e.getEndDirectives().size() > 0)
		{
			this.GenerateDirectives(e.getEndDirectives());
		}
	}

	protected abstract void GenerateNamespaceStart(CodeNamespace e);

	protected abstract void GenerateNamespaceEnd(CodeNamespace e);

	protected abstract void GenerateNamespaceImport(CodeNamespaceImport e);

	protected abstract void GenerateAttributeDeclarationsStart(CodeAttributeDeclarationCollection attributes);

	protected abstract void GenerateAttributeDeclarationsEnd(CodeAttributeDeclarationCollection attributes);

	public abstract boolean supports(GeneratorSupport support);

	public abstract boolean isValidIdentifier(String value);

	public void validateIdentifier(String value)
	{
		if (!this.isValidIdentifier(value))
		{
			throw new IllegalArgumentException(SR.GetString("InvalidIdentifier", new Object[] {value}));
		}
	}

	public abstract String createEscapedIdentifier(String value);

	public abstract String createValidIdentifier(String value);

	public abstract String getTypeOutput(CodeTypeReference value);

	protected abstract String QuoteSnippetString(String value);

	public static boolean IsValidLanguageIndependentIdentifier(String value)
	{
		return CodeGenerator.IsValidTypeNameOrIdentifier(value, false);
	}

	public static boolean IsValidLanguageIndependentTypeName(String value)
	{
		return CodeGenerator.IsValidTypeNameOrIdentifier(value, true);
	}

	private static boolean IsValidTypeNameOrIdentifier(String value, boolean isTypeName)
	{
		boolean flag = true;
		if (value.length() == 0)
		{
			return false;
		}
		int i = 0;
		RefObject<Boolean> tempRef_flag = new RefObject<Boolean>(flag);
		boolean tempVar;
		while (i < value.length())
		{
			char c = value.charAt(i);
			switch (Character.getType(c))
			{
				case Character.UPPERCASE_LETTER://UppercaseLetter:
				case Character.LOWERCASE_LETTER://LowercaseLetter:
				case Character.TITLECASE_LETTER://TitlecaseLetter:
				case Character.MODIFIER_LETTER://ModifierLetter:
				case Character.OTHER_LETTER://OtherLetter:
				case Character.LETTER_NUMBER://LetterNumber:
					flag = false;
					break;
				case Character.NON_SPACING_MARK://NonSpacingMark:
				case Character.COMBINING_SPACING_MARK://SpacingCombiningMark:
				case Character.DECIMAL_DIGIT_NUMBER://DecimalDigitNumber:
				case Character.CONNECTOR_PUNCTUATION://ConnectorPunctuation:
					if (flag && c != '_')
					{
						return false;
					}
					flag = false;
					break;
				case Character.ENCLOSING_MARK://EnclosingMark:
				case Character.OTHER_NUMBER://OtherNumber:
				case Character.SPACE_SEPARATOR://SpaceSeparator:
				case Character.LINE_SEPARATOR://LineSeparator:
				case Character.PARAGRAPH_SEPARATOR://ParagraphSeparator:
				case Character.CONTROL://Control:
				case Character.FORMAT://Format:
				case Character.SURROGATE://Surrogate:
				case Character.PRIVATE_USE://PrivateUse:
 
					
						tempVar = !isTypeName || !CodeGenerator.IsSpecialTypeChar(c, tempRef_flag);
						flag = tempRef_flag.getRefObj();
					if (tempVar)
					{
						return false;
					}
				default:
 
					//RefObject<Boolean> tempRef_flag = new RefObject<Boolean>(flag);
					 tempVar = !isTypeName || !CodeGenerator.IsSpecialTypeChar(c, tempRef_flag);
						flag = tempRef_flag.getRefObj();
					if (tempVar)
					{
						return false;
					}
			}
			
			i++;
			continue;
			
		}
		return true;
	}

	private static boolean IsSpecialTypeChar(char ch, RefObject<Boolean> nextMustBeStartChar)
	{
		if (ch <= '>')
		{
			switch (ch)
			{
				case '$':
				case '&':
				case '*':
				case '+':
				case ',':
				case '-':
				case '.':
					break;
				case '%':
				case '\'':
				case '(':
				case ')':
					return false;
				default:
					switch (ch)
					{
						case ':':
						case '<':
						case '>':
							break;
						case ';':
						case '=':
							return false;
						default:
							return false;
					}
					break;
			}
		}
		else if (ch != '[' && ch != ']')
		{
			if (ch != '`')
			{
				return false;
			}
			return true;
		}
		nextMustBeStartChar.setRefObj(true); //= true;
		return true;
	}

	public static void ValidateIdentifiers(CodeObject e)
	{
		(new CodeValidator()).ValidateIdentifiers(e);
	}


}