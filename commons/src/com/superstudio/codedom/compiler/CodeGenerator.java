package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.Resource;
import com.superstudio.commons.TypeAttributes;
import com.superstudio.commons.io.TextWriter;

import java.io.FileNotFoundException;
import java.util.Iterator;

public abstract class CodeGenerator implements ICodeGenerator {
    private static final int ParameterMultilineThreshold = 15;

    private IndentedTextWriter output;

    private CodeGeneratorOptions options;

    private CodeTypeDeclaration currentClass;

    private CodeTypeMember currentMember;

    private boolean inNestedBinary;

    protected final CodeTypeDeclaration getCurrentClass() {
        return this.currentClass;
    }

    protected final String getCurrentTypeName() {
        if (this.currentClass != null) {
            return this.currentClass.getName();
        }
        return "<% unknown %>";
    }

    protected final CodeTypeMember getCurrentMember() {
        return this.currentMember;
    }

    protected final String getCurrentMemberName() {
        if (this.currentMember != null) {
            return this.currentMember.getName();
        }
        return "<% unknown %>";
    }

    protected final boolean getIsCurrentInterface() {
        return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsInterface();
    }

    protected final boolean getIsCurrentClass() {
        return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsClass();
    }

    protected final boolean getIsCurrentStruct() {
        return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsStruct();
    }

    protected final boolean getIsCurrentEnum() {
        return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate) && this.currentClass.getIsEnum();
    }

    protected final boolean getIsCurrentDelegate() {
        return this.currentClass != null && this.currentClass instanceof CodeTypeDelegate;
    }

    protected final int getIndent() {
        return this.output.getIndent();
    }

    protected final void setIndent(int value) {
        this.output.setIndent(value);
    }

    protected abstract String getNullToken();

    protected final TextWriter getOutput() {
        return this.output;
    }

    protected final CodeGeneratorOptions getOptions() {
        return this.options;
    }

    private void generateType(CodeTypeDeclaration e) throws Exception {
        this.currentClass = e;
        if (e.getStartDirectives().size() > 0) {
            this.generateDirectives(e.getStartDirectives());
        }
        this.generateCommentStatements(e.getComments());
        if (e.getLinePragma() != null) {
            this.generateLinePragmaStart(e.getLinePragma());
        }
        this.generateTypeStart(e);
        if (this.getOptions().getVerbatimOrder()) {
            Iterator enumerator = e.getMembers().iterator();
            try {
                while (enumerator.hasNext()) {
                    CodeTypeMember member = (CodeTypeMember) enumerator.next();
                    this.generateTypeMember(member, e);
                }

                this.currentClass = e;
                this.generateTypeEnd(e);
                if (e.getLinePragma() != null) {
                    this.generateLinePragmaEnd(e.getLinePragma());
                }
                if (e.getEndDirectives().size() > 0) {
                    this.generateDirectives(e.getEndDirectives());
                }
                return;
            } finally {
                java.io.Closeable disposable = (java.io.Closeable) ((enumerator instanceof java.io.Closeable) ? enumerator : null);
                if (disposable != null) {
                    disposable.close();
                }
            }
        }
        this.generateFields(e);
        this.generateSnippetMembers(e);
        this.generateTypeConstructors(e);
        this.generateConstructors(e);
        this.generateProperties(e);
        this.generateEvents(e);
        this.generateMethods(e);
        this.generateNestedTypes(e);


    }

    protected void generateDirectives(CodeDirectiveCollection directives) {
    }

    private void generateTypeMember(CodeTypeMember member, CodeTypeDeclaration declaredType) throws Exception {
        if (this.options.getBlankLinesBetweenMembers()) {
            this.getOutput().writeLine();
        }
        if (member instanceof CodeTypeDeclaration) {
            ((ICodeGenerator) this).generateCodeFromType((CodeTypeDeclaration) member, this.output.getInnerWriter(), this.options);
            this.currentClass = declaredType;
            return;
        }
        if (member.getStartDirectives().size() > 0) {
            this.generateDirectives(member.getStartDirectives());
        }
        this.generateCommentStatements(member.getComments());
        if (member.getLinePragma() != null) {
            this.generateLinePragmaStart(member.getLinePragma());
        }
        if (member instanceof CodeMemberField) {
            this.generateField((CodeMemberField) member);
        } else if (member instanceof CodeMemberProperty) {
            this.generateProperty((CodeMemberProperty) member, declaredType);
        } else if (member instanceof CodeMemberMethod) {
            if (member instanceof CodeConstructor) {
                this.generateConstructor((CodeConstructor) member, declaredType);
            } else if (member instanceof CodeTypeConstructor) {
                this.generateTypeConstructor((CodeTypeConstructor) member);
            } else if (member instanceof CodeEntryPointMethod) {
                this.generateEntryPointMethod((CodeEntryPointMethod) member, declaredType);
            } else {
                this.generateMethod((CodeMemberMethod) member, declaredType);
            }
        } else if (member instanceof CodeMemberEvent) {
            this.generateEvent((CodeMemberEvent) member, declaredType);
        } else if (member instanceof CodeSnippetTypeMember) {
            int indent = this.getIndent();
            this.setIndent(0);
            this.generateSnippetMember((CodeSnippetTypeMember) member);
            this.setIndent(indent);
            output.writeLine();
        }
        if (member.getLinePragma() != null) {
            this.generateLinePragmaEnd(member.getLinePragma());
        }
        if (member.getEndDirectives().size() > 0) {
            this.generateDirectives(member.getEndDirectives());
        }
    }

    private void generateTypeConstructors(CodeTypeDeclaration e) throws Exception {
        Iterator<CodeTypeMember> enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeTypeConstructor) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.output.writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeTypeConstructor codeTypeConstructor = (CodeTypeConstructor) current;
                if (codeTypeConstructor.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeTypeConstructor.getLinePragma());
                }
                this.generateTypeConstructor(codeTypeConstructor);
                if (codeTypeConstructor.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeTypeConstructor.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    protected final void generateNamespaces(CodeCompileUnit e) throws Exception {
        for (Object e2 : e.getNamespaces()) {
            this.generateCodeFromNamespace((CodeNamespace) e2, this.output.getInnerWriter(), this.options);
        }
    }

    protected final void generateTypes(CodeNamespace e) throws Exception {
        for (Object e2 : e.getTypes()) {
            if (this.options.getBlankLinesBetweenMembers()) {
                this.output.writeLine();
            }
            ((ICodeGenerator) this).generateCodeFromType((CodeTypeDeclaration) e2, this.output.getInnerWriter(), this.options);
        }
    }

	/*public final boolean supports(GeneratorSupport support)
    {
		return this.supports(support);
	}*/

    public final void generateCodeFromType(CodeTypeDeclaration e, TextWriter w, CodeGeneratorOptions o) throws FileNotFoundException {
        boolean flag = false;
        if (this.output != null && w != this.output.getInnerWriter()) {
            throw new IllegalStateException(Resource.getString("CodeGenOutputWriter"));
        }
        if (this.output == null) {
            flag = true;
            this.options = ((o == null) ? new CodeGeneratorOptions() : o);
            this.output = new IndentedTextWriter(w, this.options.getIndentString());
        }
        try {
            this.generateType(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (flag) {
                this.output = null;
                this.options = null;
            }
        }
    }

    public final void generateCodeFromExpression(CodeExpression e, TextWriter w, CodeGeneratorOptions o) throws Exception {
        boolean flag = false;
        if (this.output != null && w != this.output.getInnerWriter()) {
            throw new IllegalStateException(Resource.getString("CodeGenOutputWriter"));
        }
        if (this.output == null) {
            flag = true;
            this.options = ((o == null) ? new CodeGeneratorOptions() : o);
            this.output = new IndentedTextWriter(w, this.options.getIndentString());
        }
        try {
            this.generateExpression(e);
        } finally {
            if (flag) {
                this.output = null;
                this.options = null;
            }
        }
    }

    public final void generateCodeFromCompileUnit(CodeCompileUnit e, TextWriter w, CodeGeneratorOptions o) throws Exception {
        boolean flag = false;
        if (this.output != null && w != this.output.getInnerWriter()) {
            throw new IllegalStateException(Resource.getString("CodeGenOutputWriter"));
        }
        if (this.output == null) {
            flag = true;
            this.options = ((o == null) ? new CodeGeneratorOptions() : o);
            this.output = new IndentedTextWriter(w, this.options.getIndentString());
        }
        try {
            if (e instanceof CodeSnippetCompileUnit) {
                this.generateSnippetCompileUnit((CodeSnippetCompileUnit) e);
            } else {
                this.generateCompileUnit(e);
            }
        } finally {
            if (flag) {
                this.output = null;
                this.options = null;
            }
        }
    }

    public final void generateCodeFromNamespace(CodeNamespace e, TextWriter w, CodeGeneratorOptions o) throws Exception {
        boolean flag = false;
        if (this.output != null && w != this.output.getInnerWriter()) {
            throw new IllegalStateException(Resource.getString("CodeGenOutputWriter"));
        }
        if (this.output == null) {
            flag = true;
            this.options = ((o == null) ? new CodeGeneratorOptions() : o);
            this.output = new IndentedTextWriter(w, this.options.getIndentString());
        }
        try {
            this.generateNamespace(e);
        } finally {
            if (flag) {
                this.output = null;
                this.options = null;
            }
        }
    }

    @Override
    public final void generateCodeFromStatement(CodeStatement e, TextWriter w, CodeGeneratorOptions o)
            throws Exception {
        boolean flag = false;
        if (this.output != null && w != this.output.getInnerWriter()) {
            throw new IllegalStateException(Resource.getString("CodeGenOutputWriter"));
        }
        if (this.output == null) {
            flag = true;
            this.options = ((o == null) ? new CodeGeneratorOptions() : o);
            this.output = new IndentedTextWriter(w, this.options.getIndentString());
        }
        try {
            this.generateStatement(e);
        } finally {
            if (flag) {
                this.output = null;
                this.options = null;
            }
        }
    }

    public void generateCodeFromMember(CodeTypeMember member, TextWriter writer, CodeGeneratorOptions options) throws Exception {
        if (this.output != null) {
            throw new IllegalStateException(Resource.getString("CodeGenReentrance"));
        }
        this.options = ((options == null) ? new CodeGeneratorOptions() : options);
        this.output = new IndentedTextWriter(writer, this.options.getIndentString());
        try {
            CodeTypeDeclaration declaredType = new CodeTypeDeclaration();
            this.currentClass = declaredType;
            this.generateTypeMember(member, declaredType);
        } finally {
            this.currentClass = null;
            this.output = null;
            this.options = null;
        }
    }

	/*public final boolean isValidIdentifier(String value)
	{
		return this.isValidIdentifier(value);
	}

	public final void validateIdentifier(String value)
	{
		this.validateIdentifier(value);
	}*/

	/*public final String createEscapedIdentifier(String value)
	{
		return this.createEscapedIdentifier(value);
	}
*/
/*	public final String createValidIdentifier(String value)
	{
		return this.createValidIdentifier(value);
	}*/

    /*	public final String getTypeOutput(CodeTypeReference type)
        {
            return this.getTypeOutput(type);
        }
    */
    private void generateConstructors(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeConstructor) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeConstructor codeConstructor = (CodeConstructor) current;
                if (codeConstructor.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeConstructor.getLinePragma());
                }
                this.generateConstructor(codeConstructor, e);
                if (codeConstructor.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeConstructor.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    private void generateEvents(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeMemberEvent) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeMemberEvent codeMemberEvent = (CodeMemberEvent) current;
                if (codeMemberEvent.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeMemberEvent.getLinePragma());
                }
                this.generateEvent(codeMemberEvent, e);
                if (codeMemberEvent.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeMemberEvent.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    protected final void generateExpression(CodeExpression e) throws Exception {
        if (e instanceof CodeArrayCreateExpression) {
            this.generateArrayCreateExpression((CodeArrayCreateExpression) e);
            return;
        }
        if (e instanceof CodeBaseReferenceExpression) {
            this.generateBaseReferenceExpression((CodeBaseReferenceExpression) e);
            return;
        }
        if (e instanceof CodeBinaryOperatorExpression) {
            this.generateBinaryOperatorExpression((CodeBinaryOperatorExpression) e);
            return;
        }
        if (e instanceof CodeCastExpression) {
            this.generateCastExpression((CodeCastExpression) e);
            return;
        }
        if (e instanceof CodeDelegateCreateExpression) {
            this.generateDelegateCreateExpression((CodeDelegateCreateExpression) e);
            return;
        }
        if (e instanceof CodeFieldReferenceExpression) {
            this.generateFieldReferenceExpression((CodeFieldReferenceExpression) e);
            return;
        }
        if (e instanceof CodeArgumentReferenceExpression) {
            this.generateArgumentReferenceExpression((CodeArgumentReferenceExpression) e);
            return;
        }
        if (e instanceof CodeVariableReferenceExpression) {
            this.generateVariableReferenceExpression((CodeVariableReferenceExpression) e);
            return;
        }
        if (e instanceof CodeIndexerExpression) {
            this.generateIndexerExpression((CodeIndexerExpression) e);
            return;
        }
        if (e instanceof CodeArrayIndexerExpression) {
            this.generateArrayIndexerExpression((CodeArrayIndexerExpression) e);
            return;
        }
        if (e instanceof CodeSnippetExpression) {
            this.generateSnippetExpression((CodeSnippetExpression) e);
            return;
        }
        if (e instanceof CodeMethodInvokeExpression) {
            this.generateMethodInvokeExpression((CodeMethodInvokeExpression) e);
            return;
        }
        if (e instanceof CodeMethodReferenceExpression) {
            this.generateMethodReferenceExpression((CodeMethodReferenceExpression) e);
            return;
        }
        if (e instanceof CodeEventReferenceExpression) {
            this.generateEventReferenceExpression((CodeEventReferenceExpression) e);
            return;
        }
        if (e instanceof CodeDelegateInvokeExpression) {
            this.generateDelegateInvokeExpression((CodeDelegateInvokeExpression) e);
            return;
        }
        if (e instanceof CodeObjectCreateExpression) {
            this.generateObjectCreateExpression((CodeObjectCreateExpression) e);
            return;
        }
        if (e instanceof CodeParameterDeclarationExpression) {
            this.generateParameterDeclarationExpression((CodeParameterDeclarationExpression) e);
            return;
        }
        if (e instanceof CodeDirectionExpression) {
            this.generateDirectionExpression((CodeDirectionExpression) e);
            return;
        }
        if (e instanceof CodePrimitiveExpression) {
            this.generatePrimitiveExpression((CodePrimitiveExpression) e);
            return;
        }
        if (e instanceof CodePropertyReferenceExpression) {
            this.generatePropertyReferenceExpression((CodePropertyReferenceExpression) e);
            return;
        }
        if (e instanceof CodePropertySetValueReferenceExpression) {
            this.generatePropertySetValueReferenceExpression((CodePropertySetValueReferenceExpression) e);
            return;
        }
        if (e instanceof CodeThisReferenceExpression) {
            this.generateThisReferenceExpression((CodeThisReferenceExpression) e);
            return;
        }
        if (e instanceof CodeTypeReferenceExpression) {
            this.generateTypeReferenceExpression((CodeTypeReferenceExpression) e);
            return;
        }
        if (e instanceof CodeTypeOfExpression) {
            this.generateTypeOfExpression((CodeTypeOfExpression) e);
            return;
        }
        if (e instanceof CodeDefaultValueExpression) {
            this.generateDefaultValueExpression((CodeDefaultValueExpression) e);
            return;
        }
        if (e == null) {
            throw new IllegalArgumentException("e");
        }
        //throw new IllegalArgumentException(Resource.getString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
    }

    private void generateFields(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeMemberField) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeMemberField codeMemberField = (CodeMemberField) current;
                if (codeMemberField.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeMemberField.getLinePragma());
                }
                this.generateField(codeMemberField);
                if (codeMemberField.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeMemberField.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    private void generateSnippetMembers(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        boolean flag = false;
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeSnippetTypeMember) {
                flag = true;
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    try {
                        this.getOutput().writeLine();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeSnippetTypeMember codeSnippetTypeMember = (CodeSnippetTypeMember) current;
                if (codeSnippetTypeMember.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeSnippetTypeMember.getLinePragma());
                }
                int indent = this.getIndent();
                this.setIndent(0);
                this.generateSnippetMember(codeSnippetTypeMember);
                this.setIndent(indent);
                if (codeSnippetTypeMember.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeSnippetTypeMember.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
        if (flag) {
            this.getOutput().writeLine();
        }
    }

    protected void generateSnippetCompileUnit(CodeSnippetCompileUnit e) throws Exception {
        this.generateDirectives(e.getStartDirectives());
        if (e.getLinePragma() != null) {
            this.generateLinePragmaStart(e.getLinePragma());
        }
        this.getOutput().writeLine(e.getValue());
        if (e.getLinePragma() != null) {
            this.generateLinePragmaEnd(e.getLinePragma());
        }
        if (e.getEndDirectives().size() > 0) {
            this.generateDirectives(e.getEndDirectives());
        }
    }

    private void generateMethods(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeMemberMethod && !(current instanceof CodeTypeConstructor) && !(current instanceof CodeConstructor)) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeMemberMethod codeMemberMethod = (CodeMemberMethod) current;
                if (codeMemberMethod.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeMemberMethod.getLinePragma());
                }
                if (current instanceof CodeEntryPointMethod) {
                    this.generateEntryPointMethod((CodeEntryPointMethod) current, e);
                } else {
                    this.generateMethod(codeMemberMethod, e);
                }
                if (codeMemberMethod.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeMemberMethod.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    private void generateNestedTypes(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeTypeDeclaration) {
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                CodeTypeDeclaration e2 = (CodeTypeDeclaration) current;
                ((ICodeGenerator) this).generateCodeFromType(e2, this.output.getInnerWriter(), this.options);
            }
        }
    }

    protected void generateCompileUnit(CodeCompileUnit e) throws Exception {
        this.generateCompileUnitStart(e);
        this.generateNamespaces(e);
        this.generateCompileUnitEnd(e);
    }

    protected void generateNamespace(CodeNamespace e) throws Exception {
        this.generateCommentStatements(e.getComments());
        this.generateNamespaceStart(e);
        this.generateNamespaceImports(e);
        this.getOutput().writeLine("");
        this.generateTypes(e);
        this.generateNamespaceEnd(e);
    }

    protected final void generateNamespaceImports(CodeNamespace e) {
        Iterator enumerator = e.getImports().iterator();
        while (enumerator.hasNext()) {
            CodeNamespaceImport codeNamespaceImport = (CodeNamespaceImport) enumerator.next();
            if (codeNamespaceImport.getLinePragma() != null) {
                this.generateLinePragmaStart(codeNamespaceImport.getLinePragma());
            }
            this.generateNamespaceImport(codeNamespaceImport);
            if (codeNamespaceImport.getLinePragma() != null) {
                this.generateLinePragmaEnd(codeNamespaceImport.getLinePragma());
            }
        }
    }

    private void generateProperties(CodeTypeDeclaration e) throws Exception {
        Iterator enumerator = e.getMembers().iterator();
        while (enumerator.hasNext()) {
            Object current = enumerator.next();
            if (current instanceof CodeMemberProperty) {
                this.currentMember = (CodeTypeMember) current;
                if (this.options.getBlankLinesBetweenMembers()) {
                    this.getOutput().writeLine();
                }
                if (this.currentMember.getStartDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getStartDirectives());
                }
                this.generateCommentStatements(this.currentMember.getComments());
                CodeMemberProperty codeMemberProperty = (CodeMemberProperty) current;
                if (codeMemberProperty.getLinePragma() != null) {
                    this.generateLinePragmaStart(codeMemberProperty.getLinePragma());
                }
                this.generateProperty(codeMemberProperty, e);
                if (codeMemberProperty.getLinePragma() != null) {
                    this.generateLinePragmaEnd(codeMemberProperty.getLinePragma());
                }
                if (this.currentMember.getEndDirectives().size() > 0) {
                    this.generateDirectives(this.currentMember.getEndDirectives());
                }
            }
        }
    }

    protected final void generateStatement(CodeStatement e) throws Exception {
        if (e.getStartDirectives().size() > 0) {
            this.generateDirectives(e.getStartDirectives());
        }
        if (e.getLinePragma() != null) {
            this.generateLinePragmaStart(e.getLinePragma());
        }
        if (e instanceof CodeCommentStatement) {
            this.generateCommentStatement((CodeCommentStatement) e);
        } else if (e instanceof CodeMethodReturnStatement) {
            this.generateMethodReturnStatement((CodeMethodReturnStatement) e);
        } else if (e instanceof CodeConditionStatement) {
            this.generateConditionStatement((CodeConditionStatement) e);
        } else if (e instanceof CodeTryCatchFinallyStatement) {
            this.generateTryCatchFinallyStatement((CodeTryCatchFinallyStatement) e);
        } else if (e instanceof CodeAssignStatement) {
            this.generateAssignStatement((CodeAssignStatement) e);
        } else if (e instanceof CodeExpressionStatement) {
            this.generateExpressionStatement((CodeExpressionStatement) e);
        } else if (e instanceof CodeIterationStatement) {
            this.generateIterationStatement((CodeIterationStatement) e);
        } else if (e instanceof CodeThrowExceptionStatement) {
            this.generateThrowExceptionStatement((CodeThrowExceptionStatement) e);
        } else if (e instanceof CodeSnippetStatement) {
            int indent = this.getIndent();
            this.setIndent(0);// = 0;
            this.generateSnippetStatement((CodeSnippetStatement) e);
            this.setIndent(indent);// = indent;
        } else if (e instanceof CodeVariableDeclarationStatement) {
            this.generateVariableDeclarationStatement((CodeVariableDeclarationStatement) e);
        } else if (e instanceof CodeAttachEventStatement) {
            this.generateAttachEventStatement((CodeAttachEventStatement) e);
        } else if (e instanceof CodeRemoveEventStatement) {
            this.generateRemoveEventStatement((CodeRemoveEventStatement) e);
        } else if (e instanceof CodeGotoStatement) {
            this.generateGotoStatement((CodeGotoStatement) e);
        } else {
            if (!(e instanceof CodeLabeledStatement)) {
                //throw new IllegalArgumentException(Resource.getString("InvalidElementType", new Object[] {e.getClass().getName()}), "e");
            }
            this.generateLabeledStatement((CodeLabeledStatement) e);
        }
        if (e.getLinePragma() != null) {
            this.generateLinePragmaEnd(e.getLinePragma());
        }
        if (e.getEndDirectives().size() > 0) {
            this.generateDirectives(e.getEndDirectives());
        }
    }

    protected final void generateStatements(CodeStatementCollection stms) throws Exception {
        Iterator enumerator = stms.iterator();
        while (enumerator.hasNext()) {
            this.generateCodeFromStatement((CodeStatement) enumerator.next(), this.output.getInnerWriter(), this.options);
        }
    }

    protected void outputAttributeDeclarations(CodeAttributeDeclarationCollection attributes) throws Exception {
        if (attributes.size() == 0) {
            return;
        }
        this.generateAttributeDeclarationsStart(attributes);
        boolean flag = true;
        Iterator enumerator = attributes.iterator();
        while (enumerator.hasNext()) {
            if (flag) {
                flag = false;
            } else {
                this.continueOnNewLine(", ");
            }
            CodeAttributeDeclaration codeAttributeDeclaration = (CodeAttributeDeclaration) enumerator.next();
            this.output.write(codeAttributeDeclaration.getName());
            this.output.write("(");
            boolean flag2 = true;
            for (Object arg : codeAttributeDeclaration.getArguments()) {
                if (flag2) {
                    flag2 = false;
                } else {
                    this.output.write(", ");
                }
                this.outputAttributeArgument((CodeAttributeArgument) arg);
            }
            this.output.write(")");
        }
        this.generateAttributeDeclarationsEnd(attributes);
    }

    protected void outputAttributeArgument(CodeAttributeArgument arg) throws Exception {
        if (arg.getName() != null && arg.getName().length() > 0) {
            outputIdentifier(arg.getName());
            this.output.write("=");
        }
        this.generateCodeFromExpression(arg.getValue(), this.output.getInnerWriter(), this.options);
    }

    protected void outputDirection(FieldDirection dir) throws Exception {
        switch (dir) {
            case In:
                break;
            case Out:
                this.output.write("out ");
                return;
            case Ref:
                this.output.write("ref ");
                break;
            default:
                return;
        }
    }

    protected void outputFieldScopeModifier(MemberAttributes attributes) throws Exception {
        MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.VTableMask);
        if (memberAttributes.getValue() == MemberAttributes.New) {
            this.output.write("new ");
        }
        switch ((attributes.getValue() & MemberAttributes.ScopeMask)) {
            case MemberAttributes.Final:
            case MemberAttributes.Override:
                break;
            case MemberAttributes.Static:
                this.output.write("static ");
                return;
            case MemberAttributes.Const:
                this.output.write("const ");
                break;
            default:
                return;
        }
    }

    protected void outputMemberAccessModifier(MemberAttributes attributes) throws Exception {
        MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.AccessMask);
        if (memberAttributes.getValue() <= MemberAttributes.Family) {
            if (memberAttributes.getValue() == MemberAttributes.Assembly) {
                this.output.write("internal ");
                return;
            }
            if (memberAttributes.getValue() == MemberAttributes.FamilyAndAssembly) {
                this.output.write("internal ");
                return;
            }
            if (memberAttributes.getValue() != MemberAttributes.Family) {
                return;
            }
            this.output.write("protected ");
            return;
        } else {
            if (memberAttributes.getValue() == MemberAttributes.FamilyOrAssembly) {
                this.output.write("protected internal ");
                return;
            }
            if (memberAttributes.getValue() == MemberAttributes.Private) {
                this.output.write("private ");
                return;
            }
            if (memberAttributes.getValue() != MemberAttributes.Public) {
                return;
            }
            this.output.write("public ");
            return;
        }
    }

    protected void outputMemberScopeModifier(MemberAttributes attributes) throws Exception {
        MemberAttributes memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.VTableMask);
        if (memberAttributes.getValue() == MemberAttributes.New) {
            this.output.write("new ");
        }
        switch (attributes.getValue() & MemberAttributes.ScopeMask) {
            case MemberAttributes.Abstract:
                this.output.write("abstract ");
                return;
            case MemberAttributes.Final:
                this.output.write("");
                return;
            case MemberAttributes.Static:
                this.output.write("static ");
                return;
            case MemberAttributes.Override:
                this.output.write("override ");
                return;
            default:
                memberAttributes = MemberAttributes.forValue(attributes.getValue() & MemberAttributes.AccessMask);
                if (memberAttributes.getValue() == MemberAttributes.Family || memberAttributes.getValue() == MemberAttributes.Public) {
                    this.output.write("virtual ");
                }
                return;
        }
    }

    protected abstract void outputType(CodeTypeReference typeRef);

    protected void outputTypeAttributes(TypeAttributes attributes, boolean isStruct, boolean isEnum) throws Exception {
        switch ((attributes.getValue() & TypeAttributes.VisibilityMask)) {
            case TypeAttributes.Public:
            case TypeAttributes.NestedPublic:
                this.output.write("public ");
                break;
            case TypeAttributes.NestedPrivate:
                this.output.write("private ");
                break;
        }
        if (isStruct) {
            this.output.write("struct ");
            return;
        }
        if (isEnum) {
            this.output.write("enum ");
            return;
        }
        TypeAttributes typeAttributes = TypeAttributes.forValue(attributes.getValue() & TypeAttributes.ClassSemanticsMask);
        if (typeAttributes.getValue() == TypeAttributes.NotPublic) {
            if ((attributes.getValue() & TypeAttributes.Sealed) == TypeAttributes.Sealed) {
                this.output.write("sealed ");
            }
            if ((attributes.getValue() & TypeAttributes.Abstract) == TypeAttributes.Abstract) {
                this.output.write("abstract ");
            }
            this.output.write("class ");
            return;
        }
        if (typeAttributes.getValue() != TypeAttributes.ClassSemanticsMask) {
            return;
        }
        this.output.write("interface ");
    }

    protected void outputTypeNamePair(CodeTypeReference typeRef, String name) throws Exception {
        this.outputType(typeRef);
        this.output.write(" ");
        this.outputIdentifier(name);
    }

    protected void outputIdentifier(String ident) throws Exception {
        this.output.write(ident);
    }

    protected void outputExpressionList(CodeExpressionCollection expressions) throws Exception {
        this.outputExpressionList(expressions, false);
    }

    protected void outputExpressionList(CodeExpressionCollection expressions, boolean newlineBetweenItems) throws Exception {
        boolean flag = true;
        Iterator enumerator = expressions.iterator();
        int indent = this.getIndent();
        this.setIndent(indent + 1);
        while (enumerator.hasNext()) {
            if (flag) {
                flag = false;
            } else if (newlineBetweenItems) {
                this.continueOnNewLine(",");
            } else {
                this.output.write(", ");
            }
            this.generateCodeFromExpression((CodeExpression) enumerator.next(), this.output.getInnerWriter(), this.options);
        }
        indent = this.getIndent();
        this.setIndent(indent - 1);
    }

    protected void outputOperator(CodeBinaryOperatorType op) throws Exception {
        switch (op) {
            case Add:
                this.output.write("+");
                return;
            case Subtract:
                this.output.write("-");
                return;
            case Multiply:
                this.output.write("*");
                return;
            case Divide:
                this.output.write("/");
                return;
            case Modulus:
                this.output.write("%");
                return;
            case Assign:
                this.output.write("=");
                return;
            case IdentityInequality:
                this.output.write("!=");
                return;
            case IdentityEquality:
                this.output.write("==");
                return;
            case ValueEquality:
                this.output.write("==");
                return;
            case BitwiseOr:
                this.output.write("|");
                return;
            case BitwiseAnd:
                this.output.write("&");
                return;
            case BooleanOr:
                this.output.write("||");
                return;
            case BooleanAnd:
                this.output.write("&&");
                return;
            case LessThan:
                this.output.write("<");
                return;
            case LessThanOrEqual:
                this.output.write("<=");
                return;
            case GreaterThan:
                this.output.write(">");
                return;
            case GreaterThanOrEqual:
                this.output.write(">=");
                return;
            default:
                return;
        }
    }

    protected void outputParameters(CodeParameterDeclarationExpressionCollection parameters) throws Exception {
        boolean flag = true;
        boolean flag2 = parameters.size() > 15;
        if (flag2) {
            this.setIndent(this.getIndent() + 3);
        }
        Iterator enumerator = parameters.iterator();
        while (enumerator.hasNext()) {
            CodeParameterDeclarationExpression e = (CodeParameterDeclarationExpression) enumerator.next();
            if (flag) {
                flag = false;
            } else {
                this.output.write(", ");
            }
            if (flag2) {
                this.continueOnNewLine("");
            }
            this.generateExpression(e);
        }
        if (flag2) {
            this.setIndent(this.getIndent() - 3);//-= 3;
        }
    }

    protected abstract void generateArrayCreateExpression(CodeArrayCreateExpression e);

    protected abstract void generateBaseReferenceExpression(CodeBaseReferenceExpression e);

    protected void generateBinaryOperatorExpression(CodeBinaryOperatorExpression e) throws Exception {
        boolean flag = false;
        this.output.write("(");
        this.generateExpression(e.getLeft());
        this.output.write(" ");
        if (e.getLeft() instanceof CodeBinaryOperatorExpression || e.getRight() instanceof CodeBinaryOperatorExpression) {
            if (!this.inNestedBinary) {
                flag = true;
                this.inNestedBinary = true;
                this.setIndent(this.getIndent() + 3);
            }
            this.continueOnNewLine("");
        }
        this.outputOperator(e.getOperator());
        this.output.write(" ");
        this.generateExpression(e.getRight());
        this.output.write(")");
        if (flag) {
            this.setIndent(this.getIndent() - 3);
            this.inNestedBinary = false;
        }
    }

    protected void continueOnNewLine(String st) throws Exception {
        this.output.writeLine(st);
    }

    protected abstract void generateCastExpression(CodeCastExpression e);

    protected abstract void generateDelegateCreateExpression(CodeDelegateCreateExpression e);

    protected abstract void generateFieldReferenceExpression(CodeFieldReferenceExpression e);

    protected abstract void generateArgumentReferenceExpression(CodeArgumentReferenceExpression e);

    protected abstract void generateVariableReferenceExpression(CodeVariableReferenceExpression e);

    protected abstract void generateIndexerExpression(CodeIndexerExpression e);

    protected abstract void generateArrayIndexerExpression(CodeArrayIndexerExpression e);

    protected abstract void generateSnippetExpression(CodeSnippetExpression e);

    protected abstract void generateMethodInvokeExpression(CodeMethodInvokeExpression e);

    protected abstract void generateMethodReferenceExpression(CodeMethodReferenceExpression e);

    protected abstract void generateEventReferenceExpression(CodeEventReferenceExpression e);

    protected abstract void generateDelegateInvokeExpression(CodeDelegateInvokeExpression e);

    protected abstract void generateObjectCreateExpression(CodeObjectCreateExpression e);

    protected void generateParameterDeclarationExpression(CodeParameterDeclarationExpression e) throws Exception {
        if (e.getCustomAttributes().size() > 0) {
            this.outputAttributeDeclarations(e.getCustomAttributes());
            this.output.write(" ");
        }
        this.outputDirection(e.getDirection());
        this.outputTypeNamePair(e.getType(), e.getName());
    }

    protected void generateDirectionExpression(CodeDirectionExpression e) throws Exception {
        this.outputDirection(e.getDirection());
        this.generateExpression(e.getExpression());
    }

    protected void generatePrimitiveExpression(CodePrimitiveExpression e) throws Exception {
        if (e.getValue() == null) {
            this.output.write(this.getNullToken());
            return;
        }
        if (e.getValue() instanceof String) {
            this.output.write(this.QuoteSnippetString((String) e.getValue()));
            return;
        }
        if (e.getValue() instanceof Character) {
            this.output.write("'" + e.getValue().toString() + "'");
            return;
        }


        if (e.getValue() instanceof Byte) {

            this.output.write((new Byte((byte) e.getValue())).toString());
            return;
        }
        if (e.getValue() instanceof Short) {
            this.output.write((new Short((short) e.getValue())).toString());
            return;
        }
        if (e.getValue() instanceof Integer) {
            this.output.write((new Integer((int) e.getValue())).toString());
            return;
        }
        if (e.getValue() instanceof Long) {
            this.output.write(String.valueOf((new Long((long) e.getValue()))));
            return;
        }
        if (e.getValue() instanceof Float) {
            this.generateSingleFloatValue((float) e.getValue());
            return;
        }
        if (e.getValue() instanceof Double) {
            this.generateDoubleValue((double) e.getValue());
            return;
        }
        if (e.getValue() instanceof java.math.BigDecimal) {
            this.generateDecimalValue((java.math.BigDecimal) e.getValue());
            return;
        }
        if (!(e.getValue() instanceof Boolean)) {
            throw new IllegalArgumentException(Resource.getString("InvalidPrimitiveType", new Object[]{e.getValue().getClass().toString()}));
        }
        if ((boolean) e.getValue()) {
            this.output.write("true");
            return;
        }
        this.output.write("false");
    }

    protected void generateSingleFloatValue(float s) throws Exception {
        //this.output.write((new Float(s)).toString("R"));
        this.output.write((new Float(s)).toString());
    }

    protected void generateDoubleValue(double d) throws Exception {
        //this.output.write((new Double(d)).toString("R"));
        this.output.write((new Float(d)).toString());
    }

    protected void generateDecimalValue(java.math.BigDecimal d) throws Exception {
        this.output.write(d.toString());
    }

    protected void generateDefaultValueExpression(CodeDefaultValueExpression e) {
    }

    protected abstract void generatePropertyReferenceExpression(CodePropertyReferenceExpression e);

    protected abstract void generatePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e);

    protected abstract void generateThisReferenceExpression(CodeThisReferenceExpression e);

    protected void generateTypeReferenceExpression(CodeTypeReferenceExpression e) {

        this.outputType(e.getType());
    }

    protected void generateTypeOfExpression(CodeTypeOfExpression e) throws Exception {
        this.output.write("typeof(");
        this.outputType(e.getType());
        this.output.write(")");
    }

    protected abstract void generateExpressionStatement(CodeExpressionStatement e);

    protected abstract void generateIterationStatement(CodeIterationStatement e);

    protected abstract void generateThrowExceptionStatement(CodeThrowExceptionStatement e);

    protected void generateCommentStatement(CodeCommentStatement e) {
        if (e.getComment() == null) {
            //throw new IllegalArgumentException(Resource.getString("Argument_NullComment", new Object[] {"e"}), "e");
        }
        this.generateComment(e.getComment());
    }

    protected void generateCommentStatements(CodeCommentStatementCollection e) {
        for (Object e2 : e) {
            this.generateCommentStatement((CodeCommentStatement) e2);
        }
    }

    protected abstract void generateComment(CodeComment e);

    protected abstract void generateMethodReturnStatement(CodeMethodReturnStatement e);

    protected abstract void generateConditionStatement(CodeConditionStatement e);

    protected abstract void generateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e);

    protected abstract void generateAssignStatement(CodeAssignStatement e);

    protected abstract void generateAttachEventStatement(CodeAttachEventStatement e);

    protected abstract void generateRemoveEventStatement(CodeRemoveEventStatement e);

    protected abstract void generateGotoStatement(CodeGotoStatement e);

    protected abstract void generateLabeledStatement(CodeLabeledStatement e);

    protected void generateSnippetStatement(CodeSnippetStatement e) throws Exception {
        this.output.writeLine(e.getValue());
    }

    protected abstract void generateVariableDeclarationStatement(CodeVariableDeclarationStatement e);

    protected abstract void generateLinePragmaStart(CodeLinePragma e);

    protected abstract void generateLinePragmaEnd(CodeLinePragma e);

    protected abstract void generateEvent(CodeMemberEvent e, CodeTypeDeclaration c);

    protected abstract void generateField(CodeMemberField e);

    protected abstract void generateSnippetMember(CodeSnippetTypeMember e);

    protected abstract void generateEntryPointMethod(CodeEntryPointMethod e, CodeTypeDeclaration c);

    protected abstract void generateMethod(CodeMemberMethod e, CodeTypeDeclaration c);

    protected abstract void generateProperty(CodeMemberProperty e, CodeTypeDeclaration c);

    protected abstract void generateConstructor(CodeConstructor e, CodeTypeDeclaration c);

    protected abstract void generateTypeConstructor(CodeTypeConstructor e);

    protected abstract void generateTypeStart(CodeTypeDeclaration e);

    protected abstract void generateTypeEnd(CodeTypeDeclaration e);

    protected void generateCompileUnitStart(CodeCompileUnit e) {
        if (e.getStartDirectives().size() > 0) {
            this.generateDirectives(e.getStartDirectives());
        }
    }

    protected void generateCompileUnitEnd(CodeCompileUnit e) {
        if (e.getEndDirectives().size() > 0) {
            this.generateDirectives(e.getEndDirectives());
        }
    }

    protected abstract void generateNamespaceStart(CodeNamespace e);

    protected abstract void generateNamespaceEnd(CodeNamespace e);

    protected abstract void generateNamespaceImport(CodeNamespaceImport e);

    protected abstract void generateAttributeDeclarationsStart(CodeAttributeDeclarationCollection attributes);

    protected abstract void generateAttributeDeclarationsEnd(CodeAttributeDeclarationCollection attributes);

    //public abstract boolean supports(GeneratorSupport support);

    public abstract boolean isValidIdentifier(String value);

    public void validateIdentifier(String value) {
        if (!this.isValidIdentifier(value)) {
            throw new IllegalArgumentException(Resource.getString("InvalidIdentifier", new Object[]{value}));
        }
    }

    public abstract String createEscapedIdentifier(String value);

    public abstract String createValidIdentifier(String value);

    public abstract String getTypeOutput(CodeTypeReference value);

    protected abstract String QuoteSnippetString(String value);

    public static boolean isValidLanguageIndependentIdentifier(String value) {
        return CodeGenerator.isValidTypeNameOrIdentifier(value, false);
    }

    public static boolean isValidLanguageIndependentTypeName(String value) {
        return CodeGenerator.isValidTypeNameOrIdentifier(value, true);
    }

    private static boolean isValidTypeNameOrIdentifier(String value, boolean isTypeName) {
        boolean flag = true;
        if (value.length() == 0) {
            return false;
        }
        int i = 0;
         boolean tempVar;
        while (i < value.length()) {
            char c = value.charAt(i);
            switch (Character.getType(c)) {

                case Character.UPPERCASE_LETTER:

                case Character.LOWERCASE_LETTER:
                case Character.TITLECASE_LETTER:
                case Character.MODIFIER_LETTER:
                case Character.OTHER_LETTER:
                case Character.LETTER_NUMBER:
                    flag = false;
                    break;
                case Character.NON_SPACING_MARK:
                case Character.COMBINING_SPACING_MARK:
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.CONNECTOR_PUNCTUATION:
                    if (flag && c != '_') {
                        return false;
                    }
                    flag = false;
                    break;
                default:
                case Character.ENCLOSING_MARK:
                case Character.OTHER_NUMBER:
                case Character.SPACE_SEPARATOR:
                case Character.LINE_SEPARATOR:
                case Character.PARAGRAPH_SEPARATOR:
                case Character.CONTROL:
                case Character.FORMAT:
                case Character.SURROGATE:
                case Character.PRIVATE_USE:
                    MutilReturn<Boolean,Boolean> isSpecialTypeChar=CodeGenerator.isSpecialTypeChar(c, flag);
                    tempVar=!isTypeName || !isSpecialTypeChar.gettReturn();
                    flag = isSpecialTypeChar.gettRef();
                    if (tempVar) {
                        return false;
                    }

            }

            i++;
            continue;

        }
        return true;
    }

    private  static MutilReturn<Boolean,Boolean> isSpecialTypeChar(char ch,Boolean nextMustBeStartChar){
        MutilReturn<Boolean,Boolean> result=new MutilReturn<>(false,nextMustBeStartChar);
        if (ch <= '>') {
            switch (ch) {
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
                    return MutilReturn.Return(false,nextMustBeStartChar);
                default:
                    switch (ch) {
                        case ':':
                        case '<':
                        case '>':
                            break;
                        case ';':
                        case '=':
                            return MutilReturn.Return(false,nextMustBeStartChar);

                        default:
                            return MutilReturn.Return(false,nextMustBeStartChar);

                    }
                    break;
            }
        } else if (ch != '[' && ch != ']') {
              return MutilReturn.Return(ch == '`',nextMustBeStartChar);

        }

        return MutilReturn.Return(true,true);

    }

   /* private static boolean isSpecialTypeChar(char ch,
                                             RefObject<Boolean> nextMustBeStartChar) {
        if (ch <= '>') {
            switch (ch) {
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
                    switch (ch) {
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
        } else if (ch != '[' && ch != ']') {
            return ch == '`';
        }
        nextMustBeStartChar.setRefObj(true); //= true;
        return true;
    }
*/

}