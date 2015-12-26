package com.makkajai;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by administrator on 25/12/15.
 */
public class ObjCToCppTranslator extends ObjCBaseVisitor<Void> {

    private BufferedOutputStream bufferedOutputStream;
    private FileWriter fileWriter;
    private ObjCLexer lexer;
    private CommonTokenStream tokens;
    private StringBuilder sourceFile;

    /**
     * Main Method
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(
                new FileInputStream("/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.m")); // we'll


        ObjCToCppTranslator visitor = new ObjCToCppTranslator(); // extends JavaBaseVisitor<Void>
        visitor.lexer = new ObjCLexer(input);
        visitor.tokens = new CommonTokenStream(visitor.lexer);
        ObjCParser parser = new ObjCParser(visitor.tokens);
        ParseTree tree = parser.translation_unit(); // see the grammar ->
        // starting point for
        // parsing a java file
//        System.out.println("Source name:\n" + visitor.tokens.getText());

        visitor.fileWriter = new FileWriter("/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.cpp");
        visitor.sourceFile = new StringBuilder(visitor.tokens.getText());
        // and overrides the methods
        // you're interested
        visitor.visit(tree);
        visitor.fileWriter.write(visitor.sourceFile.toString()
                .replaceAll("#import", "#include")
                .replaceAll("super", "base")
                .replaceAll("self", "this"));
        visitor.fileWriter.flush();
        visitor.fileWriter.close();
    }

    @Override
    public Void visitMethod_selector(ObjCParser.Method_selectorContext ctx) {
//        System.out.println("Selector:" + ctx.getText());
        return super.visitMethod_selector(ctx);
    }

    @Override
    public Void visitSelector(ObjCParser.SelectorContext ctx) {
//        System.out.println("Selector:" + ctx.getText());
        return super.visitSelector(ctx);
    }

    @Override
    public Void visitMethod_type(ObjCParser.Method_typeContext ctx) {
//        System.out.println("Method type:" + ctx.getText());
        return super.visitMethod_type(ctx);
    }

    @Override
    public Void visitDictionary_pair(ObjCParser.Dictionary_pairContext ctx) {
        System.out.println("Dictionary Pair:" + ctx.getText());
        return super.visitDictionary_pair(ctx);
    }

    @Override
    public Void visitArray_expression(ObjCParser.Array_expressionContext ctx) {
        System.out.println("Array expression:" + ctx.getText());
        return super.visitArray_expression(ctx);
    }

    @Override
    public Void visitMessage_expression(ObjCParser.Message_expressionContext ctx) {
        String text = tokens.getText(ctx.getSourceInterval());
        int start = sourceFile.indexOf(text);
        if(start < 0) {
            return super.visitMessage_expression(ctx);
        }
        int end = start + text.length();

        System.out.println("Receiver expression:" + ctx.receiver().getText());

        String finalMethod = "(" + tokens.getText(ctx.receiver().getSourceInterval()) + "->";

        String finalMethodName = "";
        String finalParameters = "";

        if(ctx.message_selector().keyword_argument().size() == 0) {
            finalMethodName = tokens.getText(ctx.message_selector().selector().getSourceInterval());
        }

        for (int i=0; i<ctx.message_selector().keyword_argument().size(); i++) {
            ObjCParser.Keyword_argumentContext keyword_argumentContext = ctx.message_selector().keyword_argument().get(i);
            String sourceMethodName = tokens.getText(keyword_argumentContext.selector().getSourceInterval());
            String sourceParameter = tokens.getText(keyword_argumentContext.expression().getSourceInterval());
            if(i > 0) {
                sourceMethodName = sourceMethodName.substring(0, 1).toUpperCase() + sourceMethodName.substring(1);
                finalParameters += ", ";
            }
            finalMethodName += sourceMethodName;
            finalParameters += sourceParameter;
            System.out.println("KeyValue expression:" + sourceMethodName + " Parameter: " + sourceParameter);
        }

        finalMethod += finalMethodName + "(" + finalParameters + "))";

        sourceFile.replace(start, end, finalMethod);
        return super.visitMessage_expression(ctx);
    }

    @Override
    public Void visitPrimary_expression(ObjCParser.Primary_expressionContext ctx) {
//        System.out.println("Primary expression:" + ctx.getText());

//        String text = tokens.getText(ctx.getSourceInterval());
//        int start = sourceFile.indexOf(text);
//        if(start < 0) {
//            return super.visitPrimary_expression(ctx);
//        }
//
//        int end = start + text.length();
//
//        Pattern compile = Pattern.compile("(\\[(?s:.+)\\])((?s:.+)\\])");
//        if(text.indexOf("]") == text.lastIndexOf("]")) {
//            compile = Pattern.compile("(\\[(?s:.+))\\W+((?s:.+)\\])");
//        }
//        Matcher matcher = compile.matcher(text);
//
//        boolean matches = matcher.matches();
//        if(!matches) {
//            return super.visitPrimary_expression(ctx);
//        }
//
////        System.out.println("Text: " + text);
//        String modifiedText = text;
//        modifiedText = "(" + matcher.group(1).replaceFirst("\\[", "") + "->" + matcher.group(2).replace("]", "()") + ")";
//        for (int i =1; i<=matcher.groupCount(); i++) {
////            System.out.println("Matcher: " + matcher.group(i));
//        }
//
//        sourceFile.replace(start, end, modifiedText);

        return super.visitPrimary_expression(ctx);
    }


    @Override
    public Void visitMethod_definition(ObjCParser.Method_definitionContext ctx) {
//        System.out.println("Method name:" + ctx.getText());
        return super.visitMethod_definition(ctx);
    }

    @Override
    public Void visitMethod_declaration(ObjCParser.Method_declarationContext ctx) {
//        System.out.println("Method name:" + ctx.getText());
        return super.visitMethod_declaration(ctx);
    }

    @Override
    public Void visitInstance_method_declaration(ObjCParser.Instance_method_declarationContext ctx) {
//        System.out.println("Method name:" + ctx.getText());
        return super.visitInstance_method_declaration(ctx);
    }

    @Override
    public Void visitClass_method_declaration(ObjCParser.Class_method_declarationContext ctx) {
//        System.out.println("Method name:" + ctx.getText());
        return super.visitClass_method_declaration(ctx);
    }
}
