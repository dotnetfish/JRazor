package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.SR;
import com.superstudio.commons.csharpbridge.StringComparison;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.Path;

import java.io.IOException;
import java.util.Iterator;

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

	public final void validateIdentifiers(CodeObject e)
	{
		if (e instanceof CodeCompileUnit)
		{
			this.validateCodeCompileUnit((CodeCompileUnit)e);
			return;
		}
		if (e instanceof CodeComment)
		{
			this.validateComment((CodeComment)e);
			return;
		}
		if (e instanceof CodeExpression)
		{
			this.validateExpression((CodeExpression)e);
			return;
		}
		if (e instanceof CodeNamespace)
		{
			this.validateNamespace((CodeNamespace)e);
			return;
		}
		if (e instanceof CodeNamespaceImport)
		{
			CodeValidator.validateNamespaceImport((CodeNamespaceImport)e);
			return;
		}
		if (e instanceof CodeStatement)
		{
			this.validateStatement((CodeStatement)e);
			return;
		}
		if (e instanceof CodeTypeMember)
		{
			this.validateTypeMember((CodeTypeMember)e);
			return;
		}
		if (e instanceof CodeTypeReference)
		{
			CodeValidator.validateTypeReference((CodeTypeReference)e);
			return;
		}
		if (e instanceof CodeDirective)
		{
			CodeValidator.validateCodeDirective((CodeDirective)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void validateTypeMember(CodeTypeMember e)
	{
		this.validateCommentStatements(e.getComments());
		CodeValidator.validateCodeDirectives(e.getStartDirectives());
		CodeValidator.validateCodeDirectives(e.getEndDirectives());
		if (e.getLinePragma() != null)
		{
			this.validateLinePragmaStart(e.getLinePragma());
		}
		if (e instanceof CodeMemberEvent)
		{
			this.validateEvent((CodeMemberEvent)e);
			return;
		}
		if (e instanceof CodeMemberField)
		{
			this.validateField((CodeMemberField)e);
			return;
		}
		if (e instanceof CodeMemberMethod)
		{
			this.validateMemberMethod((CodeMemberMethod)e);
			return;
		}
		if (e instanceof CodeMemberProperty)
		{
			this.validateProperty((CodeMemberProperty)e);
			return;
		}
		if (e instanceof CodeSnippetTypeMember)
		{
			this.validateSnippetMember((CodeSnippetTypeMember)e);
			return;
		}
		if (e instanceof CodeTypeDeclaration)
		{
			this.validateTypeDeclaration((CodeTypeDeclaration)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void validateCodeCompileUnit(CodeCompileUnit e)
	{
		CodeValidator.validateCodeDirectives(e.getStartDirectives());
		CodeValidator.validateCodeDirectives(e.getEndDirectives());
		if (e instanceof CodeSnippetCompileUnit)
		{
			this.validateSnippetCompileUnit((CodeSnippetCompileUnit)e);
			return;
		}
		this.validateCompileUnitStart(e);
		this.validateNamespaces(e);
		this.validateCompileUnitEnd(e);
	}

	private void validateSnippetCompileUnit(CodeSnippetCompileUnit e)
	{
		if (e.getLinePragma() != null)
		{
			this.validateLinePragmaStart(e.getLinePragma());
		}
	}

	private void validateCompileUnitStart(CodeCompileUnit e)
	{
		if (e.getAssemblyCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getAssemblyCustomAttributes());
		}
	}

	private void validateCompileUnitEnd(CodeCompileUnit e)
	{
	}

	private void validateNamespaces(CodeCompileUnit e)
	{
		for (Object e2 : e.getNamespaces())
		{
			this.validateNamespace((CodeNamespace)e2);
		}
	}

	private void validateNamespace(CodeNamespace e)
	{
		this.validateCommentStatements(e.getComments());
		CodeValidator.validateNamespaceStart(e);
		this.validateNamespaceImports(e);
		this.validateTypes(e);
	}

	private static void validateNamespaceStart(CodeNamespace e)
	{
		if (e.getName() != null && e.getName().length() > 0)
		{
			CodeValidator.validateTypeName(e, "Name", e.getName());
		}
	}

	private void validateNamespaceImports(CodeNamespace e)
	{
		Iterator enumerator = e.getImports().iterator();
		while (enumerator.hasNext())
		{
			CodeNamespaceImport codeNamespaceImport = (CodeNamespaceImport)enumerator.next();
			if (codeNamespaceImport.getLinePragma() != null)
			{
				this.validateLinePragmaStart(codeNamespaceImport.getLinePragma());
			}
			CodeValidator.validateNamespaceImport(codeNamespaceImport);
		}
	}

	private static void validateNamespaceImport(CodeNamespaceImport e)
	{
		CodeValidator.validateTypeName(e, "Namespace", e.getNamespace());
	}

	private void validateAttributes(CodeAttributeDeclarationCollection attributes)
	{
		if (attributes.size() == 0)
		{
			return;
		}
		Iterator enumerator = attributes.iterator();
		while (enumerator.hasNext())
		{
			CodeAttributeDeclaration codeAttributeDeclaration = (CodeAttributeDeclaration)enumerator.next();
			CodeValidator.validateTypeName(codeAttributeDeclaration, "Name", codeAttributeDeclaration.getName());
			CodeValidator.validateTypeReference(codeAttributeDeclaration.getAttributeType());
			for (Object arg : codeAttributeDeclaration.getArguments())
			{
				this.validateAttributeArgument((CodeAttributeArgument)arg);
			}
		}
	}

	private void validateAttributeArgument(CodeAttributeArgument arg)
	{
		if (arg.getName() != null && arg.getName().length() > 0)
		{
			CodeValidator.validateIdentifier(arg, "Name", arg.getName());
		}
		this.validateExpression(arg.getValue());
	}

	private void validateTypes(CodeNamespace e)
	{
		for (Object e2 : e.getTypes())
		{
			this.validateTypeDeclaration((CodeTypeDeclaration)e2);
		}
	}

	private void validateTypeDeclaration(CodeTypeDeclaration e)
	{
		CodeTypeDeclaration codeTypeDeclaration = this.currentClass;
		this.currentClass = e;
		this.validateTypeStart(e);
		this.validateTypeParameters(e.getTypeParameters());
		this.validateTypeMembers(e);
		CodeValidator.validateTypeReferences(e.getBaseTypes());
		this.currentClass = codeTypeDeclaration;
	}

	private void validateTypeMembers(CodeTypeDeclaration e)
	{
		for (Object e2 : e.getMembers())
		{
			this.validateTypeMember((CodeTypeMember)e2);
		}
	}

	private void validateTypeParameters(CodeTypeParameterCollection parameters)
	{
		for (int i = 0; i < parameters.size(); i++)
		{
			this.validateTypeParameter(parameters.getItem(i));
		}
	}

	private void validateTypeParameter(CodeTypeParameter e)
	{
		CodeValidator.validateIdentifier(e, "Name", e.getName());
		CodeValidator.validateTypeReferences(e.getConstraints());
		this.validateAttributes(e.getCustomAttributes());
	}

	private void validateField(CodeMemberField e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		CodeValidator.validateIdentifier(e, "Name", e.getName());
		if (!this.getIsCurrentEnum())
		{
			CodeValidator.validateTypeReference(e.getType());
		}
		if (e.getInitExpression() != null)
		{
			this.validateExpression(e.getInitExpression());
		}
	}

	private void validateConstructor(CodeConstructor e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		this.validateParameters(e.getParameters());
		CodeExpressionCollection baseConstructorArgs = e.getBaseConstructorArgs();
		CodeExpressionCollection chainedConstructorArgs = e.getChainedConstructorArgs();
		if (baseConstructorArgs.size() > 0)
		{
			this.validateExpressionList(baseConstructorArgs);
		}
		if (chainedConstructorArgs.size() > 0)
		{
			this.validateExpressionList(chainedConstructorArgs);
		}
		this.validateStatements(e.getStatements());
	}

	private void validateProperty(CodeMemberProperty e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		CodeValidator.validateTypeReference(e.getType());
		CodeValidator.validateTypeReferences(e.getImplementationTypes());
		if (e.getPrivateImplementationType() != null && !this.getIsCurrentInterface())
		{
			CodeValidator.validateTypeReference(e.getPrivateImplementationType());
		}
		if (e.getParameters().size() > 0 && StringHelper.Compare(e.getName(), "Item", StringComparison.OrdinalIgnoreCase) == 0)
		{
			this.validateParameters(e.getParameters());
		}
		else
		{
			CodeValidator.validateIdentifier(e, "Name", e.getName());
		}
		if (e.getHasGet() && !this.getIsCurrentInterface() && (e.getAttributes().getValue() & MemberAttributes.ScopeMask) != MemberAttributes.Abstract)
		{
			this.validateStatements(e.getGetStatements());
		}
		if (e.getHasSet() && !this.getIsCurrentInterface() && (e.getAttributes().getValue() & MemberAttributes.ScopeMask) != MemberAttributes.Abstract)
		{
			this.validateStatements(e.getSetStatements());
		}
	}

	private void validateMemberMethod(CodeMemberMethod e)
	{
		this.validateCommentStatements(e.getComments());
		if (e.getLinePragma() != null)
		{
			this.validateLinePragmaStart(e.getLinePragma());
		}
		this.validateTypeParameters(e.getTypeParameters());
		CodeValidator.validateTypeReferences(e.getImplementationTypes());
		if (e instanceof CodeEntryPointMethod)
		{
			this.validateStatements(e.getStatements());
			return;
		}
		if (e instanceof CodeConstructor)
		{
			this.validateConstructor((CodeConstructor)e);
			return;
		}
		if (e instanceof CodeTypeConstructor)
		{
			this.validateTypeConstructor((CodeTypeConstructor)e);
			return;
		}
		this.validateMethod(e);
	}

	private void validateTypeConstructor(CodeTypeConstructor e)
	{
		this.validateStatements(e.getStatements());
	}

	private void validateMethod(CodeMemberMethod e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		if (e.getReturnTypeCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getReturnTypeCustomAttributes());
		}
		CodeValidator.validateTypeReference(e.getReturnType());
		if (e.getPrivateImplementationType() != null)
		{
			CodeValidator.validateTypeReference(e.getPrivateImplementationType());
		}
		CodeValidator.validateIdentifier(e, "Name", e.getName());
		this.validateParameters(e.getParameters());
		if (!this.getIsCurrentInterface() && ((e.getAttributes().getValue() & MemberAttributes.ScopeMask)) != MemberAttributes.Abstract)
		{
			this.validateStatements(e.getStatements());
		}
	}

	private void validateSnippetMember(CodeSnippetTypeMember e)
	{
	}

	private void validateTypeStart(CodeTypeDeclaration e)
	{
		this.validateCommentStatements(e.getComments());
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		CodeValidator.validateIdentifier(e, "Name", e.getName());
		if (this.getIsCurrentDelegate())
		{
			CodeTypeDelegate codeTypeDelegate = (CodeTypeDelegate)e;
			CodeValidator.validateTypeReference(codeTypeDelegate.getReturnType());
			this.validateParameters(codeTypeDelegate.getParameters());
			return;
		}
		Iterator enumerator = e.getBaseTypes().iterator();
		try
		{
			while (enumerator.hasNext())
			{
				CodeValidator.validateTypeReference((CodeTypeReference)enumerator.next());
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

	private void validateCommentStatements(CodeCommentStatementCollection e)
	{
		for (Object e2 : e)
		{
			this.validateCommentStatement((CodeCommentStatement)e2);
		}
	}

	private void validateCommentStatement(CodeCommentStatement e)
	{
		this.validateComment(e.getComment());
	}

	private void validateComment(CodeComment e)
	{
	}

	private void validateStatement(CodeStatement e)
	{
		CodeValidator.validateCodeDirectives(e.getStartDirectives());
		CodeValidator.validateCodeDirectives(e.getEndDirectives());
		if (e instanceof CodeCommentStatement)
		{
			this.validateCommentStatement((CodeCommentStatement)e);
			return;
		}
		if (e instanceof CodeMethodReturnStatement)
		{
			this.validateMethodReturnStatement((CodeMethodReturnStatement)e);
			return;
		}
		if (e instanceof CodeConditionStatement)
		{
			this.validateConditionStatement((CodeConditionStatement)e);
			return;
		}
		if (e instanceof CodeTryCatchFinallyStatement)
		{
			this.validateTryCatchFinallyStatement((CodeTryCatchFinallyStatement)e);
			return;
		}
		if (e instanceof CodeAssignStatement)
		{
			this.validateAssignStatement((CodeAssignStatement)e);
			return;
		}
		if (e instanceof CodeExpressionStatement)
		{
			this.validateExpressionStatement((CodeExpressionStatement)e);
			return;
		}
		if (e instanceof CodeIterationStatement)
		{
			this.validateIterationStatement((CodeIterationStatement)e);
			return;
		}
		if (e instanceof CodeThrowExceptionStatement)
		{
			this.validateThrowExceptionStatement((CodeThrowExceptionStatement)e);
			return;
		}
		if (e instanceof CodeSnippetStatement)
		{
			this.validateSnippetStatement((CodeSnippetStatement)e);
			return;
		}
		if (e instanceof CodeVariableDeclarationStatement)
		{
			this.validateVariableDeclarationStatement((CodeVariableDeclarationStatement)e);
			return;
		}
		if (e instanceof CodeAttachEventStatement)
		{
			this.validateAttachEventStatement((CodeAttachEventStatement)e);
			return;
		}
		if (e instanceof CodeRemoveEventStatement)
		{
			this.validateRemoveEventStatement((CodeRemoveEventStatement)e);
			return;
		}
		if (e instanceof CodeGotoStatement)
		{
			CodeValidator.validateGotoStatement((CodeGotoStatement)e);
			return;
		}
		if (e instanceof CodeLabeledStatement)
		{
			this.validateLabeledStatement((CodeLabeledStatement)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void validateStatements(CodeStatementCollection stms)
	{
		Iterator enumerator = stms.iterator();
		while (enumerator.hasNext())
		{
			this.validateStatement((CodeStatement)enumerator.next());
		}
	}

	private void validateExpressionStatement(CodeExpressionStatement e)
	{
		this.validateExpression(e.getExpression());
	}

	private void validateIterationStatement(CodeIterationStatement e)
	{
		this.validateStatement(e.getInitStatement());
		this.validateExpression(e.getTestExpression());
		this.validateStatement(e.getIncrementStatement());
		this.validateStatements(e.getStatements());
	}

	private void validateThrowExceptionStatement(CodeThrowExceptionStatement e)
	{
		if (e.getToThrow() != null)
		{
			this.validateExpression(e.getToThrow());
		}
	}

	private void validateMethodReturnStatement(CodeMethodReturnStatement e)
	{
		if (e.getExpression() != null)
		{
			this.validateExpression(e.getExpression());
		}
	}

	private void validateConditionStatement(CodeConditionStatement e)
	{
		this.validateExpression(e.getCondition());
		this.validateStatements(e.getTrueStatements());
		if (e.getFalseStatements().size() > 0)
		{
			this.validateStatements(e.getFalseStatements());
		}
	}

	private void validateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e)
	{
		this.validateStatements(e.getTryStatements());
		CodeCatchClauseCollection catchClauses = e.getCatchClauses();
		if (catchClauses.size() > 0)
		{
			Iterator enumerator = catchClauses.iterator();
			while (enumerator.hasNext())
			{
				CodeCatchClause codeCatchClause = (CodeCatchClause)enumerator.next();
				CodeValidator.validateTypeReference(codeCatchClause.getCatchExceptionType());
				CodeValidator.validateIdentifier(codeCatchClause, "LocalName", codeCatchClause.getLocalName());
				this.validateStatements(codeCatchClause.getStatements());
			}
		}
		CodeStatementCollection finallyStatements = e.getFinallyStatements();
		if (finallyStatements.size() > 0)
		{
			this.validateStatements(finallyStatements);
		}
	}

	private void validateAssignStatement(CodeAssignStatement e)
	{
		this.validateExpression(e.getLeft());
		this.validateExpression(e.getRight());
	}

	private void validateAttachEventStatement(CodeAttachEventStatement e)
	{
		this.validateEventReferenceExpression(e.getEvent());
		this.validateExpression(e.getListener());
	}

	private void validateRemoveEventStatement(CodeRemoveEventStatement e)
	{
		this.validateEventReferenceExpression(e.getEvent());
		this.validateExpression(e.getListener());
	}

	private static void validateGotoStatement(CodeGotoStatement e)
	{
		CodeValidator.validateIdentifier(e, "Label", e.getLabel());
	}

	private void validateLabeledStatement(CodeLabeledStatement e)
	{
		CodeValidator.validateIdentifier(e, "Label", e.getLabel());
		if (e.getStatement() != null)
		{
			this.validateStatement(e.getStatement());
		}
	}

	private void validateVariableDeclarationStatement(CodeVariableDeclarationStatement e)
	{
		CodeValidator.validateTypeReference(e.getType());
		CodeValidator.validateIdentifier(e, "Name", e.getName());
		if (e.getInitExpression() != null)
		{
			this.validateExpression(e.getInitExpression());
		}
	}

	private void validateLinePragmaStart(CodeLinePragma e)
	{
	}

	private void validateEvent(CodeMemberEvent e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		if (e.getPrivateImplementationType() != null)
		{
			CodeValidator.validateTypeReference(e.getType());
			CodeValidator.validateIdentifier(e, "Name", e.getName());
		}
		CodeValidator.validateTypeReferences(e.getImplementationTypes());
	}

	private void validateParameters(CodeParameterDeclarationExpressionCollection parameters)
	{
		Iterator enumerator = parameters.iterator();
		while (enumerator.hasNext())
		{
			CodeParameterDeclarationExpression e = (CodeParameterDeclarationExpression)enumerator.next();
			this.validateParameterDeclarationExpression(e);
		}
	}

	private void validateSnippetStatement(CodeSnippetStatement e)
	{
	}

	private void validateExpressionList(CodeExpressionCollection expressions)
	{
		Iterator enumerator = expressions.iterator();
		while (enumerator.hasNext())
		{
			this.validateExpression((CodeExpression)enumerator.next());
		}
	}

	private static void validateTypeReference(CodeTypeReference e)
	{
		String baseType = e.getBaseType();
		CodeValidator.validateTypeName(e, "BaseType", baseType);
		CodeValidator.validateArity(e);
		CodeValidator.validateTypeReferences(e.getTypeArguments());
	}

	private static void validateTypeReferences(CodeTypeReferenceCollection refs)
	{
		for (int i = 0; i < refs.size(); i++)
		{
			CodeValidator.validateTypeReference(refs.getItem(i));
		}
	}

	private static void validateArity(CodeTypeReference e)
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
					num2 = num2 * 10 + baseType.charAt(i) - '0';
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

	private static void validateTypeName(Object e, String propertyName, String typeName)
	{
		if (!CodeGenerator.isValidLanguageIndependentTypeName(typeName))
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidTypeName", new Object[] {typeName, propertyName, e.getClass().getName()}), "typeName");
		}
	}

	private static void validateIdentifier(Object e, String propertyName, String identifier)
	{
		if (!CodeGenerator.isValidLanguageIndependentIdentifier(identifier))
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidLanguageIdentifier", new Object[] {identifier, propertyName, e.getClass().getName()}), "identifier");
		}
	}

	private void validateExpression(CodeExpression e)
	{
		if (e instanceof CodeArrayCreateExpression)
		{
			this.validateArrayCreateExpression((CodeArrayCreateExpression)e);
			return;
		}
		if (e instanceof CodeBaseReferenceExpression)
		{
			this.validateBaseReferenceExpression((CodeBaseReferenceExpression)e);
			return;
		}
		if (e instanceof CodeBinaryOperatorExpression)
		{
			this.validateBinaryOperatorExpression((CodeBinaryOperatorExpression)e);
			return;
		}
		if (e instanceof CodeCastExpression)
		{
			this.validateCastExpression((CodeCastExpression)e);
			return;
		}
		if (e instanceof CodeDefaultValueExpression)
		{
			CodeValidator.validateDefaultValueExpression((CodeDefaultValueExpression)e);
			return;
		}
		if (e instanceof CodeDelegateCreateExpression)
		{
			this.validateDelegateCreateExpression((CodeDelegateCreateExpression)e);
			return;
		}
		if (e instanceof CodeFieldReferenceExpression)
		{
			this.validateFieldReferenceExpression((CodeFieldReferenceExpression)e);
			return;
		}
		if (e instanceof CodeArgumentReferenceExpression)
		{
			CodeValidator.validateArgumentReferenceExpression((CodeArgumentReferenceExpression)e);
			return;
		}
		if (e instanceof CodeVariableReferenceExpression)
		{
			CodeValidator.validateVariableReferenceExpression((CodeVariableReferenceExpression)e);
			return;
		}
		if (e instanceof CodeIndexerExpression)
		{
			this.validateIndexerExpression((CodeIndexerExpression)e);
			return;
		}
		if (e instanceof CodeArrayIndexerExpression)
		{
			this.validateArrayIndexerExpression((CodeArrayIndexerExpression)e);
			return;
		}
		if (e instanceof CodeSnippetExpression)
		{
			this.validateSnippetExpression((CodeSnippetExpression)e);
			return;
		}
		if (e instanceof CodeMethodInvokeExpression)
		{
			this.validateMethodInvokeExpression((CodeMethodInvokeExpression)e);
			return;
		}
		if (e instanceof CodeMethodReferenceExpression)
		{
			this.validateMethodReferenceExpression((CodeMethodReferenceExpression)e);
			return;
		}
		if (e instanceof CodeEventReferenceExpression)
		{
			this.validateEventReferenceExpression((CodeEventReferenceExpression)e);
			return;
		}
		if (e instanceof CodeDelegateInvokeExpression)
		{
			this.validateDelegateInvokeExpression((CodeDelegateInvokeExpression)e);
			return;
		}
		if (e instanceof CodeObjectCreateExpression)
		{
			this.validateObjectCreateExpression((CodeObjectCreateExpression)e);
			return;
		}
		if (e instanceof CodeParameterDeclarationExpression)
		{
			this.validateParameterDeclarationExpression((CodeParameterDeclarationExpression)e);
			return;
		}
		if (e instanceof CodeDirectionExpression)
		{
			this.validateDirectionExpression((CodeDirectionExpression)e);
			return;
		}
		if (e instanceof CodePrimitiveExpression)
		{
			this.validatePrimitiveExpression((CodePrimitiveExpression)e);
			return;
		}
		if (e instanceof CodePropertyReferenceExpression)
		{
			this.validatePropertyReferenceExpression((CodePropertyReferenceExpression)e);
			return;
		}
		if (e instanceof CodePropertySetValueReferenceExpression)
		{
			this.validatePropertySetValueReferenceExpression((CodePropertySetValueReferenceExpression)e);
			return;
		}
		if (e instanceof CodeThisReferenceExpression)
		{
			this.validateThisReferenceExpression((CodeThisReferenceExpression)e);
			return;
		}
		if (e instanceof CodeTypeReferenceExpression)
		{
			CodeValidator.validateTypeReference(((CodeTypeReferenceExpression)e).getType());
			return;
		}
		if (e instanceof CodeTypeOfExpression)
		{
			CodeValidator.validateTypeOfExpression((CodeTypeOfExpression)e);
			return;
		}
		if (e == null)
		{
			throw new IllegalArgumentException("e");
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private void validateArrayCreateExpression(CodeArrayCreateExpression e)
	{
		CodeValidator.validateTypeReference(e.getCreateType());
		CodeExpressionCollection initializers = e.getInitializers();
		if (initializers.size() > 0)
		{
			this.validateExpressionList(initializers);
			return;
		}
		if (e.getSizeExpression() != null)
		{
			this.validateExpression(e.getSizeExpression());
		}
	}

	private void validateBaseReferenceExpression(CodeBaseReferenceExpression e)
	{
	}

	private void validateBinaryOperatorExpression(CodeBinaryOperatorExpression e)
	{
		this.validateExpression(e.getLeft());
		this.validateExpression(e.getRight());
	}

	private void validateCastExpression(CodeCastExpression e)
	{
		CodeValidator.validateTypeReference(e.getTargetType());
		this.validateExpression(e.getExpression());
	}

	private static void validateDefaultValueExpression(CodeDefaultValueExpression e)
	{
		CodeValidator.validateTypeReference(e.getType());
	}

	private void validateDelegateCreateExpression(CodeDelegateCreateExpression e)
	{
		CodeValidator.validateTypeReference(e.getDelegateType());
		this.validateExpression(e.getTargetObject());
		CodeValidator.validateIdentifier(e, "MethodName", e.getMethodName());
	}

	private void validateFieldReferenceExpression(CodeFieldReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.validateExpression(e.getTargetObject());
		}
		CodeValidator.validateIdentifier(e, "FieldName", e.getFieldName());
	}

	private static void validateArgumentReferenceExpression(CodeArgumentReferenceExpression e)
	{
		CodeValidator.validateIdentifier(e, "ParameterName", e.getParameterName());
	}

	private static void validateVariableReferenceExpression(CodeVariableReferenceExpression e)
	{
		CodeValidator.validateIdentifier(e, "VariableName", e.getVariableName());
	}

	private void validateIndexerExpression(CodeIndexerExpression e)
	{
		this.validateExpression(e.getTargetObject());
		for (Object e2 : e.getIndices())
		{
			this.validateExpression((CodeExpression)e2);
		}
	}
	private void validateArrayIndexerExpression(CodeArrayIndexerExpression e)
	{
		this.validateExpression(e.getTargetObject());
		for (Object e2 : e.getIndices())
		{
			this.validateExpression((CodeExpression)e2);
		}
	}

	private void validateSnippetExpression(CodeSnippetExpression e)
	{
	}

	private void validateMethodInvokeExpression(CodeMethodInvokeExpression e)
	{
		this.validateMethodReferenceExpression(e.getMethod());
		this.validateExpressionList(e.getParameters());
	}

	private void validateMethodReferenceExpression(CodeMethodReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.validateExpression(e.getTargetObject());
		}
		CodeValidator.validateIdentifier(e, "MethodName", e.getMethodName());
		CodeValidator.validateTypeReferences(e.getTypeArguments());
	}

	private void validateEventReferenceExpression(CodeEventReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.validateExpression(e.getTargetObject());
		}
		CodeValidator.validateIdentifier(e, "EventName", e.getEventName());
	}

	private void validateDelegateInvokeExpression(CodeDelegateInvokeExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.validateExpression(e.getTargetObject());
		}
		this.validateExpressionList(e.getParameters());
	}

	private void validateObjectCreateExpression(CodeObjectCreateExpression e)
	{
		CodeValidator.validateTypeReference(e.getCreateType());
		this.validateExpressionList(e.getParameters());
	}

	private void validateParameterDeclarationExpression(CodeParameterDeclarationExpression e)
	{
		if (e.getCustomAttributes().size() > 0)
		{
			this.validateAttributes(e.getCustomAttributes());
		}
		CodeValidator.validateTypeReference(e.getType());
		CodeValidator.validateIdentifier(e, "Name", e.getName());
	}

	private void validateDirectionExpression(CodeDirectionExpression e)
	{
		this.validateExpression(e.getExpression());
	}

	private void validatePrimitiveExpression(CodePrimitiveExpression e)
	{
	}

	private void validatePropertyReferenceExpression(CodePropertyReferenceExpression e)
	{
		if (e.getTargetObject() != null)
		{
			this.validateExpression(e.getTargetObject());
		}
		CodeValidator.validateIdentifier(e, "PropertyName", e.getPropertyName());
	}

	private void validatePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e)
	{
	}

	private void validateThisReferenceExpression(CodeThisReferenceExpression e)
	{
	}

	private static void validateTypeOfExpression(CodeTypeOfExpression e)
	{
		CodeValidator.validateTypeReference(e.getType());
	}

	private static void validateCodeDirectives(CodeDirectiveCollection e)
	{
		for (int i = 0; i < e.size(); i++)
		{
			CodeValidator.validateCodeDirective(e.getItem(i));
		}
	}

	private static void validateCodeDirective(CodeDirective e)
	{
		if (e instanceof CodeChecksumPragma)
		{
			CodeValidator.validateChecksumPragma((CodeChecksumPragma)e);
			return;
		}
		if (e instanceof CodeRegionDirective)
		{
			CodeValidator.validateRegionDirective((CodeRegionDirective)e);
			return;
		}
		//throw new IllegalArgumentException(SR.GetString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
	}

	private static void validateChecksumPragma(CodeChecksumPragma e)
	{
		//if (e.getFileName().IndexOfAny(Path.GetInvalidPathChars()) != -1)
		if(StringHelper.indexOfAny(e.getFileName(), Path.GetInvalidPathChars())!= -1)
		{
			//throw new IllegalArgumentException(SR.GetString("InvalidPathCharsInChecksum", new Object[] {e.getFileName()}));
		}
	}

	private static void validateRegionDirective(CodeRegionDirective e)
	{
		//if (e.getRegionText().IndexOfAny(CodeValidator.newLineChars) != -1)
		if(StringHelper.indexOfAny(e.getRegionText(), CodeValidator.newLineChars)!=-1)
		{
			throw new IllegalArgumentException(SR.GetString("InvalidRegion", new Object[] {e.getRegionText()}));
		}
	}
}