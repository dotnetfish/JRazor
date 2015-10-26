package com.superstudio.language.java.tokenizer;

import com.superstudio.language.java.symbols.JavaKeyword;

public final class JavaKeywordDetector {
	private static final java.util.HashMap<String, JavaKeyword> _keywords = new java.util.HashMap<String, JavaKeyword>() {
		/**
		* 
		*/
		private static final long serialVersionUID = 1L;

		{
			put("abstract", JavaKeyword.Abstract);
			put("byte", JavaKeyword.Byte);
			put("class", JavaKeyword.Class);
			// put( "delegate", JavaKeyword.Delegate );
			// put( "event", JavaKeyword.Event );
			// put( "fixed", JavaKeyword.Fixed );
			put("if", JavaKeyword.If);
			// put( "internal", JavaKeyword.Internal );
			put("new", JavaKeyword.New);
			put("override", JavaKeyword.Override);
			put("readonly", JavaKeyword.Readonly);
			put("short", JavaKeyword.Short);
			put("struct", JavaKeyword.Struct);
			put("try", JavaKeyword.Try);
			put("throws", JavaKeyword.Throws);
			put("volatile", JavaKeyword.Volatile);

			put("do", JavaKeyword.Do);
			put("instanceof", JavaKeyword.Instanceof);

			put("switch", JavaKeyword.Switch);
			put("ushort", JavaKeyword.Ushort);
			put("while", JavaKeyword.While);
			put("case", JavaKeyword.Case);
			put("const", JavaKeyword.Const);
			put("explicit", JavaKeyword.Explicit);
			put("float", JavaKeyword.Float);
			put("null", JavaKeyword.Null);

			put("implicit", JavaKeyword.Implicit);
			put("private", JavaKeyword.Private);
			put("this", JavaKeyword.This);
			put("import", JavaKeyword.Import);

			put("return", JavaKeyword.Return);

			put("super", JavaKeyword.Super);
			put("catch", JavaKeyword.Catch);
			put("continue", JavaKeyword.Continue);
			put("double", JavaKeyword.Double);
			put("for", JavaKeyword.For);
			//put("in", JavaKeyword.In);
			put("lock", JavaKeyword.Lock);
			put("object", JavaKeyword.Object);
			put("protected", JavaKeyword.Protected);
			put("static", JavaKeyword.Static);
			put("false", JavaKeyword.False);
			put("public", JavaKeyword.Public);
			//put("sbyte", JavaKeyword.Sbyte);
			put("throw", JavaKeyword.Throw);
		
			put("decimal", JavaKeyword.Decimal);
			put("else", JavaKeyword.Else);
			//put("operator", JavaKeyword.Operator);
			put("string", JavaKeyword.String);
			
			put("boolean", JavaKeyword.Bool);
			put("char", JavaKeyword.Char);
			put("default", JavaKeyword.Default);
			//put("foreach", JavaKeyword.Foreach);
			put("long", JavaKeyword.Long);
			put("void", JavaKeyword.Void);
			put("enum", JavaKeyword.Enum);
			put("finally", JavaKeyword.Finally);
			put("int", JavaKeyword.Int);
			put("out", JavaKeyword.Out);
			put("final", JavaKeyword.Final);
			put("true", JavaKeyword.True);
			// put("goto", JavaKeyword.Goto);
			// put("unchecked", JavaKeyword.Unchecked);
			put("interface", JavaKeyword.Interface);
			put("break", JavaKeyword.Break);
			put("checked", JavaKeyword.Checked);
			put("namespace", JavaKeyword.Namespace);
		}
	};

	public static JavaKeyword symbolTypeForIdentifier(String id) {
		JavaKeyword type = JavaKeyword.forValue(0);
		if (!((type = _keywords.get(id)) != null)) {
			return null;
		}
		return type;
	}
}