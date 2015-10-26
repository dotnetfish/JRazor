package com.superstudio.codedom.compiler;

import java.io.IOException;
import java.util.Iterator;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.Path;
import com.superstudio.codedom.*;
import com.superstudio.commons.SR;
import com.superstudio.commons.csharpbridge.StringComparison;

public class CodeValidator
{

	private static final char[] newLineChars = new char[] {'\r', '\n', '\u2028', '\u2029', '\u0085'};

	private CodeTypeDeclaration currentClass;

	private boolean getIsCurrentInterface()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsInterface();
	}

	private boolean getIsCurrentEnum()
	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsEnum();
	}

	private boolean getIsCurrentDelegate()
	{
		return this.currentClass != null && this.currentClass instanceof CodeTypeDelegate;
	}

	public final void ValidateIdentifiers(CodeObject e)
	{
		if (e instanceof CodeCompileUnit)
		{
			this.ValidateCodeCompileUnit((CodeCompileUnit)e);
			return;
		}
		if (e instanceof CodeComment)
		{
			this.ValidateComment((CodeComment)e);
			return;
		}
		if (e instanceof CodeExpression)
		{
			this.ValidateExpression((CodeExpression)e);
			return;
		}
		if (e instanceof CodeNamespace)
		{
			this.ValidateNamespace((CodeNamespace)e);
			return;
		}
		if (e instanceof CodeNamespaceImport)
		{
			CodeValidator.ValidateNamespaceImport((CodeNamespaceImport)e);
			return;
		}
		if (e instanceof CodeStatement)
		{
			this.ValidateStatement((CodeStatement)e);
			return;
		}
		if (e instanceof CodeTypeMember)
		{
			this.ValidateTypeMember((CodeTypeMember)e);
			return;
		}
		if (e instanceof CodeTypeReference)
		{
			CodeValidator.ValidateTypeReference((CodeTypeReference)e);
			return;
		}
		if (e instanceof CodeDirective)
		{
			CodeValidator.ValidateCodeDirective((CodeDirective)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void ValidateTypeMember(CodeTypeMember e)
	{
		this.ValidateCommentStatements(e.getComments());
		CodeValidator.ValidateCodeDirectives(e.getStartDirectives());
		CodeValidator.ValidateCodeDirectives(e.getEndDirectives());
		if (e.getLinePragma() != null)
		{
			this.ValidateLinePragmaStart(e.getLinePragma());
		}
		if (e instanceof CodeMemberEvent)
		{
			this.ValidateEvent((CodeMemberEvent)e);
			return;
		}
		if (e instanceof CodeMemberField)
		{
			this.ValidateField((CodeMemberField)e);
			return;
		}
		if (e instanceof CodeMemberMethod)
		{
			this.ValidateMemberMethod((CodeMemberMethod)e);
			return;
		}
		if (e instanceof CodeMemberProperty)
		{
			this.ValidateProperty((CodeMemberProperty)e);
			return;
		}
		if (e instanceof CodeSnippetTypeMember)
		{
			this.ValidateSnippetMember((CodeSnippetTypeMember)e);
			return;
		}
		if (e instanceof CodeTypeDeclaration)
		{
			this.ValidateTypeDeclaration((CodeTypeDeclaration)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void ValidateCodeCompileUnit(CodeCompileUnit e)
	{
		CodeValidator.ValidateCodeDirectives(e.getStartDirectives());
		CodeValidator.ValidateCodeDirectives(e.getEndDirectives());
		if (e instanceof CodeSnippetCompileUnit)
		{
			this.ValidateSnippetCompileUnit((CodeSnippetCompileUnit)e);
			return;
		}
		this.ValidateCompileUnitStart(e);
		this.ValidateNamespaces(e);
		this.ValidateCompileUnitEnd(e);
	}

	private void ValidateSnippetCompileUnit(CodeSnippetCompileUnit e)
	{
		if (e.getLinePragma() != null)
		{
			this.ValidateLinePragmaStart(e.getLinePragma());
		}
	}

	private void ValidateCompileUnitStart(CodeCompileUnit e)
	{
		if (e.getAssemblyCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getAssemblyCustomAttributes());
		}
	}

	private void ValidateCompileUnitEnd(CodeCompileUnit e)
	{
	}

	private void ValidateNamespaces(CodeCompileUnit e)
	{
		for (Object e2 : e.getNamespaces())
		{
			this.ValidateNamespace((CodeNamespace)e2);
		}
	}

	private void ValidateNamespace(CodeNamespace e)
	{
		this.ValidateCommentStatements(e.getComments());
		CodeValidator.ValidateNamespaceStart(e);
		this.ValidateNamespaceImports(e);
		this.ValidateTypes(e);
	}

	private static void ValidateNamespaceStart(CodeNamespace e)
	{
		if (e.getName() != null && e.getName().length() > 0)
		{
			CodeValidator.ValidateTypeName(e, "Name", e.getName());
		}
	}

	private void ValidateNamespaceImports(CodeNamespace e)
	{
		Iterator<CodeNamespaceImport> enumerator = e.getImports().iterator();
		while (enumerator.hasNext())
		{
			CodeNamespaceImport codeNamespaceImport = enumerator.next();
			if (codeNamespaceImport.getLinePragma() != null)
			{
				this.ValidateLinePragmaStart(codeNamespaceImport.getLinePragma());
			}
			CodeValidator.ValidateNamespaceImport(codeNamespaceImport);
		}
	}

	private static void ValidateNamespaceImport(CodeNamespaceImport e)
	{
		CodeValidator.ValidateTypeName(e, "Namespace", e.getNamespace());
	}

	private void ValidateAttributes(CodeAttributeDeclarationCollection attributes)
	{
		if (attributes.size() == 0)
		{
			return;
		}
		Iterator<CodeAttributeDeclaration> enumerator = attributes.iterator();
		while (enumerator.hasNext())
		{
			CodeAttributeDeclaration codeAttributeDeclaration = enumerator.next();
			CodeValidator.ValidateTypeName(codeAttributeDeclaration, "Name", codeAttributeDeclaration.getName());
			CodeValidator.ValidateTypeReference(codeAttributeDeclaration.getAttributeType());
			for (Object arg : codeAttributeDeclaration.getArguments())
			{
				this.ValidateAttributeArgument((CodeAttributeArgument)arg);
			}
		}
	}

	private void ValidateAttributeArgument(CodeAttributeArgument arg)
	{
		if (arg.getName() != null && arg.getName().length() > 0)
		{
			CodeValidator.ValidateIdentifier(arg, "Name", arg.getName());
		}
		this.ValidateExpression(arg.getValue());
	}

	private void ValidateTypes(CodeNamespace e)
	{
		for (Object e2 : e.getTypes())
		{
			this.ValidateTypeDeclaration((CodeTypeDeclaration)e2);
		}
	}

	private void ValidateTypeDeclaration(CodeTypeDeclaration e)
	{
		CodeTypeDeclaration codeTypeDeclaration = this.currentClass;
		this.currentClass = e;
		this.ValidateTypeStart(e);
		this.ValidateTypeParameters(e.getTypeParameters());
		this.ValidateTypeMembers(e);
		CodeValidator.ValidateTypeReferences(e.getBaseTypes());
		this.currentClass = codeTypeDeclaration;
	}

	private void ValidateTypeMembers(CodeTypeDeclaration e)
	{
		for (Object e2 : e.getMembers())
		{
			this.ValidateTypeMember((CodeTypeMember)e2);
		}
	}

	private void ValidateTypeParameters(CodeTypeParameterCollection parameters)
	{
		for (int i = 0; i < parameters.size(); i++)
		{
			this.ValidateTypeParameter(parameters.getItem(i));
		}
	}

	private void ValidateTypeParameter(CodeTypeParameter e)
	{
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		CodeValidator.ValidateTypeReferences(e.getConstraints());
		this.ValidateAttributes(e.getCustomAttributes());
	}

	private void ValidateField(CodeMemberField e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		if (!this.getIsCurrentEnum())
		{
			CodeValidator.ValidateTypeReference(e.getType());
		}
		if (e.getInitExpression() != null)
		{
			this.ValidateExpression(e.getInitExpression());
		}
	}

	private void ValidateConstructor(CodeConstructor e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		this.ValidateParameters(e.getParameters());
		CodeExpressionCollection baseConstructorArgs = e.getBaseConstructorArgs();
		CodeExpressionCollection chainedConstructorArgs = e.getChainedConstructorArgs();
		if (baseConstructorArgs.size() > 0)
		{
			this.ValidateExpressionList(baseConstructorArgs);
		}
		if (chainedConstructorArgs.size() > 0)
		{
			this.ValidateExpressionList(chainedConstructorArgs);
		}
		this.ValidateStatements(e.getStatements());
	}

	private void ValidateProperty(CodeMemberProperty e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		CodeValidator.ValidateTypeReference(e.getType());
		CodeValidator.ValidateTypeReferences(e.getImplementationTypes());
		if (e.getPrivateImplementationType() != null && !this.getIsCurrentInterface())
		{
			CodeValidator.ValidateTypeReference(e.getPrivateImplementationType());
		}
		if (e.getParameters().size() > 0 && StringHelper.Compare(e.getName(), "Item", StringComparison.OrdinalIgnoreCase) == 0)
		{
			this.ValidateParameters(e.getParameters());
		}
		else
		{
			CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		}
		if (e.getHasGet() && !this.getIsCurrentInterface() && (e.getAttributes().getValue() & MemberAttributes.ScopeMask) != MemberAttributes.Abstract)
		{
			this.ValidateStatements(e.getGetStatements());
		}
		if (e.getHasSet() && !this.getIsCurrentInterface() && (e.getAttributes().getValue() & MemberAttributes.ScopeMask) != MemberAttributes.Abstract)
		{
			this.ValidateStatements(e.getSetStatements());
		}
	}

	private void ValidateMemberMethod(CodeMemberMethod e)
	{
		this.ValidateCommentStatements(e.getComments());
		if (e.getLinePragma() != null)
		{
			this.ValidateLinePragmaStart(e.getLinePragma());
		}
		this.ValidateTypeParameters(e.getTypeParameters());
		CodeValidator.ValidateTypeReferences(e.getImplementationTypes());
		if (e instanceof CodeEntryPointMethod)
		{
			this.ValidateStatements(((CodeEntryPointMethod)e).getStatements());
			return;
		}
		if (e instanceof CodeConstructor)
		{
			this.ValidateConstructor((CodeConstructor)e);
			return;
		}
		if (e instanceof CodeTypeConstructor)
		{
			this.ValidateTypeConstructor((CodeTypeConstructor)e);
			return;
		}
		this.ValidateMethod(e);
	}

	private void ValidateTypeConstructor(CodeTypeConstructor e)
	{
		this.ValidateStatements(e.getStatements());
	}

	private void ValidateMethod(CodeMemberMethod e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		if (e.getReturnTypeCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getReturnTypeCustomAttributes());
		}
		CodeValidator.ValidateTypeReference(e.getReturnType());
		if (e.getPrivateImplementationType() != null)
		{
			CodeValidator.ValidateTypeReference(e.getPrivateImplementationType());
		}
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		this.ValidateParameters(e.getParameters());
		if (!this.getIsCurrentInterface() && ((e.getAttributes().getValue() & MemberAttributes.ScopeMask)) != MemberAttributes.Abstract)
		{
			this.ValidateStatements(e.getStatements());
		}
	}

	private void ValidateSnippetMember(CodeSnippetTypeMember e)
	{
	}

	private void ValidateTypeStart(CodeTypeDeclaration e)
	{
		this.ValidateCommentStatements(e.getComments());
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		if (this.getIsCurrentDelegate())
		{
			CodeTypeDelegate codeTypeDelegate = (CodeTypeDelegate)e;
			CodeValidator.ValidateTypeReference(codeTypeDelegate.getReturnType());
			this.ValidateParameters(codeTypeDelegate.getParameters());
			return;
		}
		Iterator<CodeTypeReference> enumerator = e.getBaseTypes().iterator();
		try
		{
			while (enumerator.hasNext())
			{
				CodeValidator.ValidateTypeReference(enumerator.next());
			}
		}
		finally
		{
			
			java.io.Closeable disposable = (java.io.Closeable)((enumerator instanceof java.io.Closeable) ? enumerator : null);
			if (disposable != null)
			{
				try {
					disposable.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
			}
		}
	}

	private void ValidateCommentStatements(CodeCommentStatementCollection e)
	{
		for (Object e2 : e)
		{
			this.ValidateCommentStatement((CodeCommentStatement)e2);
		}
	}

	private void ValidateCommentStatement(CodeCommentStatement e)
	{
		this.ValidateComment(e.getComment());
	}

	private void ValidateComment(CodeComment e)
	{
	}

	private void ValidateStatement(CodeStatement e)
	{
		CodeValidator.ValidateCodeDirectives(e.getStartDirectives());
		CodeValidator.ValidateCodeDirectives(e.getEndDirectives());
		if (e instanceof CodeCommentStatement)
		{
			this.ValidateCommentStatement((CodeCommentStatement)e);
			return;
		}
		if (e instanceof CodeMethodReturnStatement)
		{
			this.ValidateMethodReturnStatement((CodeMethodReturnStatement)e);
			return;
		}
		if (e instanceof CodeConditionStatement)
		{
			this.ValidateConditionStatement((CodeConditionStatement)e);
			return;
		}
		if (e instanceof CodeTryCatchFinallyStatement)
		{
			this.ValidateTryCatchFinallyStatement((CodeTryCatchFinallyStatement)e);
			return;
		}
		if (e instanceof CodeAssignStatement)
		{
			this.ValidateAssignStatement((CodeAssignStatement)e);
			return;
		}
		if (e instanceof CodeExpressionStatement)
		{
			this.ValidateExpressionStatement((CodeExpressionStatement)e);
			return;
		}
		if (e instanceof CodeIterationStatement)
		{
			this.ValidateIterationStatement((CodeIterationStatement)e);
			return;
		}
		if (e instanceof CodeThrowExceptionStatement)
		{
			this.ValidateThrowExceptionStatement((CodeThrowExceptionStatement)e);
			return;
		}
		if (e instanceof CodeSnippetStatement)
		{
			this.ValidateSnippetStatement((CodeSnippetStatement)e);
			return;
		}
		if (e instanceof CodeVariableDeclarationStatement)
		{
			this.ValidateVariableDeclarationStatement((CodeVariableDeclarationStatement)e);
			return;
		}
		if (e instanceof CodeAttachEventStatement)
		{
			this.ValidateAttachEventStatement((CodeAttachEventStatement)e);
			return;
		}
		if (e instanceof CodeRemoveEventStatement)
		{
			this.ValidateRemoveEventStatement((CodeRemoveEventStatement)e);
			return;
		}
		if (e instanceof CodeGotoStatement)
		{
			CodeValidator.ValidateGotoStatement((CodeGotoStatement)e);
			return;
		}
		if (e instanceof CodeLabeledStatement)
		{
			this.ValidateLabeledStatement((CodeLabeledStatement)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void ValidateStatements(CodeStatementCollection stms)
	{
		Iterator<CodeStatement> enumerator = stms.iterator();
		while (enumerator.hasNext())
		{
			this.ValidateStatement(enumerator.next());
		}
	}

	private void ValidateExpressionStatement(CodeExpressionStatement e)
	{
		this.ValidateExpression(e.getExpression());
	}

	private void ValidateIterationStatement(CodeIterationStatement e)
	{
		this.ValidateStatement(e.getInitStatement());
		this.ValidateExpression(e.getTestExpression());
		this.ValidateStatement(e.getIncrementStatement());
		this.ValidateStatements(e.getStatements());
	}

	private void ValidateThrowExceptionStatement(CodeThrowExceptionStatement e)
	{
		if (e.getToThrow() != null)
		{
			this.ValidateExpression(e.getToThrow());
		}
	}

	private void ValidateMethodReturnStatement(CodeMethodReturnStatement e)
	{
		if (e.getExpression() != null)
		{
			this.ValidateExpression(e.getExpression());
		}
	}

	private void ValidateConditionStatement(CodeConditionStatement e)
	{
		this.ValidateExpression(e.getCondition());
		this.ValidateStatements(e.getTrueStatements());
		if (e.getFalseStatements().size() > 0)
		{
			this.ValidateStatements(e.getFalseStatements());
		}
	}

	private void ValidateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e)
	{
		this.ValidateStatements(e.getTryStatements());
		CodeCatchClauseCollection catchClauses = e.getCatchClauses();
		if (catchClauses.size() > 0)
		{
			Iterator<CodeCatchClause> enumerator = catchClauses.iterator();
			while (enumerator.hasNext())
			{
				CodeCatchClause codeCatchClause = enumerator.next();
				CodeValidator.ValidateTypeReference(codeCatchClause.getCatchExceptionType());
				CodeValidator.ValidateIdentifier(codeCatchClause, "LocalName", codeCatchClause.getLocalName());
				this.ValidateStatements(codeCatchClause.getStatements());
			}
		}
		CodeStatementCollection finallyStatements = e.getFinallyStatements();
		if (finallyStatements.size() > 0)
		{
			this.ValidateStatements(finallyStatements);
		}
	}

	private void ValidateAssignStatement(CodeAssignStatement e)
	{
		this.ValidateExpression(e.getLeft());
		this.ValidateExpression(e.getRight());
	}

	private void ValidateAttachEventStatement(CodeAttachEventStatement e)
	{
		this.ValidateEventReferenceExpression(e.getEvent());
		this.ValidateExpression(e.getListener());
	}

	private void ValidateRemoveEventStatement(CodeRemoveEventStatement e)
	{
		this.ValidateEventReferenceExpression(e.getEvent());
		this.ValidateExpression(e.getListener());
	}

	private static void ValidateGotoStatement(CodeGotoStatement e)
	{
		CodeValidator.ValidateIdentifier(e, "Label", e.getLabel());
	}

	private void ValidateLabeledStatement(CodeLabeledStatement e)
	{
		CodeValidator.ValidateIdentifier(e, "Label", e.getLabel());
		if (e.getStatement() != null)
		{
			this.ValidateStatement(e.getStatement());
		}
	}

	private void ValidateVariableDeclarationStatement(CodeVariableDeclarationStatement e)
	{
		CodeValidator.ValidateTypeReference(e.getType());
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		if (e.getInitExpression() != null)
		{
			this.ValidateExpression(e.getInitExpression());
		}
	}

	private void ValidateLinePragmaStart(CodeLinePragma e)
	{
	}

	private void ValidateEvent(CodeMemberEvent e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		if (e.getPrivateImplementationType() != null)
		{
			CodeValidator.ValidateTypeReference(e.getType());
			CodeValidator.ValidateIdentifier(e, "Name", e.getName());
		}
		CodeValidator.ValidateTypeReferences(e.getImplementationTypes());
	}

	private void ValidateParameters(CodeParameterDeclarationExpressionCollection parameters)
	{
		Iterator<CodeParameterDeclarationExpression> enumerator = parameters.iterator();
		while (enumerator.hasNext())
		{
			CodeParameterDeclarationExpression e = enumerator.next();
			this.ValidateParameterDeclarationExpression(e);
		}
	}

	private void ValidateSnippetStatement(CodeSnippetStatement e)
	{
	}

	private void ValidateExpressionList(CodeExpressionCollection expressions)
	{
		Iterator<CodeExpression> enumerator = expressions.iterator();
		while (enumerator.hasNext())
		{
			this.ValidateExpression(enumerator.next());
		}
	}

	private static void ValidateTypeReference(CodeTypeReference e)
	{
		String baseType = e.getBaseType();
		CodeValidator.ValidateTypeName(e, "BaseType", baseType);
		CodeValidator.ValidateArity(e);
		CodeValidator.ValidateTypeReferences(e.getTypeArguments());
	}

	private static void ValidateTypeReferences(CodeTypeReferenceCollection refs)
	{
		for (int i = 0; i < refs.size(); i++)
		{
			CodeValidator.ValidateTypeReference(refs.getItem(i));
		}
	}

	private static void ValidateArity(CodeTypeReference e)
	{
		String baseType = e.getBaseType();
		int num = 0;
		for (int i = 0; i < baseType.length(); i++)
		{
			if (baseType.charAt(i) == '`')
			{
				i++;
				int num2 = 0;
				while (i < baseType.length() && baseType.charAt(i) >= '0' && baseType.charAt(i) <= '9')
				{
					num2 = num2 * 10 + (int)(baseType.charAt(i) - '0');
					i++;
				}
				num += num2;
			}
		}
		if (num != e.getTypeArguments().size() && e.getTypeArguments().size() != 0)
		{
			throw new IllegalArgumentException(SR.GetString("ArityDoesntMatch", new Object[] {baseType, e.getTypeArguments().size()}));
		}
	}

	private static void ValidateTypeName(Object e, String propertyName, String typeName)
	{
		if (!CodeGenerator.IsValidLanguageIndependentTypeName(typeName))
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidTypeName", new Object[] {typeName, propertyName, e.getClass().getName()}), "typeName");
		}
	}

	private static void ValidateIdentifier(Object e, String propertyName, String identifier)
	{
		if (!CodeGenerator.IsValidLanguageIndependentIdentifier(identifier))
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidLanguageIdentifier", new Object[] {identifier, propertyName, e.getClass().getName()}), "identifier");
		}
	}

	private void ValidateExpression(CodeExpression e)
	{
		if (e instanceof CodeArrayCreateExpression)
		{
			this.ValidateArrayCreateExpression((CodeArrayCreateExpression)e);
			return;
		}
		if (e instanceof CodeBaseReferenceExpression)
		{
			this.ValidateBaseReferenceExpression((CodeBaseReferenceExpression)e);
			return;
		}
		if (e instanceof CodeBinaryOperatorExpression)
		{
			this.ValidateBinaryOperatorExpression((CodeBinaryOperatorExpression)e);
			return;
		}
		if (e instanceof CodeCastExpression)
		{
			this.ValidateCastExpression((CodeCastExpression)e);
			return;
		}
		if (e instanceof CodeDefaultValueExpression)
		{
			CodeValidator.ValidateDefaultValueExpression((CodeDefaultValueExpression)e);
			return;
		}
		if (e instanceof CodeDelegateCreateExpression)
		{
			this.ValidateDelegateCreateExpression((CodeDelegateCreateExpression)e);
			return;
		}
		if (e instanceof CodeFieldReferenceExpression)
		{
			this.ValidateFieldReferenceExpression((CodeFieldReferenceExpression)e);
			return;
		}
		if (e instanceof CodeArgumentReferenceExpression)
		{
			CodeValidator.ValidateArgumentReferenceExpression((CodeArgumentReferenceExpression)e);
			return;
		}
		if (e instanceof CodeVariableReferenceExpression)
		{
			CodeValidator.ValidateVariableReferenceExpression((CodeVariableReferenceExpression)e);
			return;
		}
		if (e instanceof CodeIndexerExpression)
		{
			this.ValidateIndexerExpression((CodeIndexerExpression)e);
			return;
		}
		if (e instanceof CodeArrayIndexerExpression)
		{
			this.ValidateArrayIndexerExpression((CodeArrayIndexerExpression)e);
			return;
		}
		if (e instanceof CodeSnippetExpression)
		{
			this.ValidateSnippetExpression((CodeSnippetExpression)e);
			return;
		}
		if (e instanceof CodeMethodInvokeExpression)
		{
			this.ValidateMethodInvokeExpression((CodeMethodInvokeExpression)e);
			return;
		}
		if (e instanceof CodeMethodReferenceExpression)
		{
			this.ValidateMethodReferenceExpression((CodeMethodReferenceExpression)e);
			return;
		}
		if (e instanceof CodeEventReferenceExpression)
		{
			this.ValidateEventReferenceExpression((CodeEventReferenceExpression)e);
			return;
		}
		if (e instanceof CodeDelegateInvokeExpression)
		{
			this.ValidateDelegateInvokeExpression((CodeDelegateInvokeExpression)e);
			return;
		}
		if (e instanceof CodeObjectCreateExpression)
		{
			this.ValidateObjectCreateExpression((CodeObjectCreateExpression)e);
			return;
		}
		if (e instanceof CodeParameterDeclarationExpression)
		{
			this.ValidateParameterDeclarationExpression((CodeParameterDeclarationExpression)e);
			return;
		}
		if (e instanceof CodeDirectionExpression)
		{
			this.ValidateDirectionExpression((CodeDirectionExpression)e);
			return;
		}
		if (e instanceof CodePrimitiveExpression)
		{
			this.ValidatePrimitiveExpression((CodePrimitiveExpression)e);
			return;
		}
		if (e instanceof CodePropertyReferenceExpression)
		{
			this.ValidatePropertyReferenceExpression((CodePropertyReferenceExpression)e);
			return;
		}
		if (e instanceof CodePropertySetValueReferenceExpression)
		{
			this.ValidatePropertySetValueReferenceExpression((CodePropertySetValueReferenceExpression)e);
			return;
		}
		if (e instanceof CodeThisReferenceExpression)
		{
			this.ValidateThisReferenceExpression((CodeThisReferenceExpression)e);
			return;
		}
		if (e instanceof CodeTypeReferenceExpression)
		{
			CodeValidator.ValidateTypeReference(((CodeTypeReferenceExpression)e).getType());
			return;
		}
		if (e instanceof CodeTypeOfExpression)
		{
			CodeValidator.ValidateTypeOfExpression((CodeTypeOfExpression)e);
			return;
		}
		if (e == null)
		{
			throw new IllegalArgumentException("e");
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void ValidateArrayCreateExpression(CodeArrayCreateExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getCreateType());
		CodeExpressionCollection initializers = e.getInitializers();
		if (initializers.size() > 0)
		{
			this.ValidateExpressionList(initializers);
			return;
		}
		if (e.getSizeExpression() != null)
		{
			this.ValidateExpression(e.getSizeExpression());
		}
	}

	private void ValidateBaseReferenceExpression(CodeBaseReferenceExpression e)
	{
	}

	private void ValidateBinaryOperatorExpression(CodeBinaryOperatorExpression e)
	{
		this.ValidateExpression(e.getLeft());
		this.ValidateExpression(e.getRight());
	}

	private void ValidateCastExpression(CodeCastExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getTargetType());
		this.ValidateExpression(e.getExpression());
	}

	private static void ValidateDefaultValueExpression(CodeDefaultValueExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getType());
	}

	private void ValidateDelegateCreateExpression(CodeDelegateCreateExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getDelegateType());
		this.ValidateExpression(e.getTargetObject());
		CodeValidator.ValidateIdentifier(e, "MethodName", e.getMethodName());
	}

	private void ValidateFieldReferenceExpression(CodeFieldReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.ValidateExpression(e.getTargetObject());
		}
		CodeValidator.ValidateIdentifier(e, "FieldName", e.getFieldName());
	}

	private static void ValidateArgumentReferenceExpression(CodeArgumentReferenceExpression e)
	{
		CodeValidator.ValidateIdentifier(e, "ParameterName", e.getParameterName());
	}

	private static void ValidateVariableReferenceExpression(CodeVariableReferenceExpression e)
	{
		CodeValidator.ValidateIdentifier(e, "VariableName", e.getVariableName());
	}

	private void ValidateIndexerExpression(CodeIndexerExpression e)
	{
		this.ValidateExpression(e.getTargetObject());
		for (Object e2 : e.getIndices())
		{
			this.ValidateExpression((CodeExpression)e2);
		}
	}
	private void ValidateArrayIndexerExpression(CodeArrayIndexerExpression e)
	{
		this.ValidateExpression(e.getTargetObject());
		for (Object e2 : e.getIndices())
		{
			this.ValidateExpression((CodeExpression)e2);
		}
	}

	private void ValidateSnippetExpression(CodeSnippetExpression e)
	{
	}

	private void ValidateMethodInvokeExpression(CodeMethodInvokeExpression e)
	{
		this.ValidateMethodReferenceExpression(e.getMethod());
		this.ValidateExpressionList(e.getParameters());
	}

	private void ValidateMethodReferenceExpression(CodeMethodReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.ValidateExpression(e.getTargetObject());
		}
		CodeValidator.ValidateIdentifier(e, "MethodName", e.getMethodName());
		CodeValidator.ValidateTypeReferences(e.getTypeArguments());
	}

	private void ValidateEventReferenceExpression(CodeEventReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.ValidateExpression(e.getTargetObject());
		}
		CodeValidator.ValidateIdentifier(e, "EventName", e.getEventName());
	}

	private void ValidateDelegateInvokeExpression(CodeDelegateInvokeExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.ValidateExpression(e.getTargetObject());
		}
		this.ValidateExpressionList(e.getParameters());
	}

	private void ValidateObjectCreateExpression(CodeObjectCreateExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getCreateType());
		this.ValidateExpressionList(e.getParameters());
	}

	private void ValidateParameterDeclarationExpression(CodeParameterDeclarationExpression e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.ValidateAttributes(e.getCustomAttributes());
		}
		CodeValidator.ValidateTypeReference(e.getType());
		CodeValidator.ValidateIdentifier(e, "Name", e.getName());
	}

	private void ValidateDirectionExpression(CodeDirectionExpression e)
	{
		this.ValidateExpression(e.getExpression());
	}

	private void ValidatePrimitiveExpression(CodePrimitiveExpression e)
	{
	}

	private void ValidatePropertyReferenceExpression(CodePropertyReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.ValidateExpression(e.getTargetObject());
		}
		CodeValidator.ValidateIdentifier(e, "PropertyName", e.getPropertyName());
	}

	private void ValidatePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e)
	{
	}

	private void ValidateThisReferenceExpression(CodeThisReferenceExpression e)
	{
	}

	private static void ValidateTypeOfExpression(CodeTypeOfExpression e)
	{
		CodeValidator.ValidateTypeReference(e.getType());
	}

	private static void ValidateCodeDirectives(CodeDirectiveCollection e)
	{
		for (int i = 0; i < e.size(); i++)
		{
			CodeValidator.ValidateCodeDirective(e.getItem(i));
		}
	}

	private static void ValidateCodeDirective(CodeDirective e)
	{
		if (e instanceof CodeChecksumPragma)
		{
			CodeValidator.ValidateChecksumPragma((CodeChecksumPragma)e);
			return;
		}
		if (e instanceof CodeRegionDirective)
		{
			CodeValidator.ValidateRegionDirective((CodeRegionDirective)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private static void ValidateChecksumPragma(CodeChecksumPragma e)
	{
		//if (e.getFileName().IndexOfAny(Path.GetInvalidPathChars()) != -1)
		if(StringHelper.indexOfAny(e.getFileName(), Path.GetInvalidPathChars())!= -1)
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidPathCharsInChecksum", new Object[] {e.getFileName()}));
		}
	}

	private static void ValidateRegionDirective(CodeRegionDirective e)
	{
		//if (e.getRegionText().IndexOfAny(CodeValidator.newLineChars) != -1)
		if(StringHelper.indexOfAny(e.getRegionText(), CodeValidator.newLineChars)!=-1)
		{
			throw new IllegalArgumentException(SR.GetString("InvalidRegion", new Object[] {e.getRegionText()}));
		}
	}
}