package com.makkajai;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by administrator on 25/12/15.
 */
public class Gen extends ObjCBaseVisitor<Void> {

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


        // parse
        // this
        // file
        Gen visitor = new Gen(); // extends JavaBaseVisitor<Void>
        visitor.lexer = new ObjCLexer(input);
        visitor.tokens = new CommonTokenStream(visitor.lexer);
        ObjCParser parser = new ObjCParser(visitor.tokens);
        ParseTree tree = parser.translation_unit(); // see the grammar ->
        // starting point for
        // parsing a java file
//        System.out.println("Source name:\n" + visitor.tokens.getText());

        visitor.fileWriter = new FileWriter("/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.cpp");
        visitor.sourceFile = new StringBuilder(visitor.tokens.getText().replaceAll("import", "include"));
        // and overrides the methods
        // you're interested
        visitor.visit(tree);
        visitor.fileWriter.write(visitor.sourceFile.toString());
        visitor.fileWriter.flush();
        visitor.fileWriter.close();
    }

    @Override
    public Void visitExpression(ObjCParser.ExpressionContext ctx) {
//        System.out.println("Expression name:" + tokens.getText(ctx.getSourceInterval()));
        return super.visitExpression(ctx);
    }

    @Override
    public Void visitStatement(ObjCParser.StatementContext ctx) {
        String text = tokens.getText(ctx.getSourceInterval());
        int start = sourceFile.indexOf(text);
        int end = start + text.length();
        text = text.replaceAll("super", "base").replaceAll("self", "this");

        Pattern compile = Pattern.compile("\\[([A-Za-z]+)\\W([A-Za-z]+)\\]");
        Matcher matcher = compile.matcher(text);

        System.out.println("Text: " + text);
        while (matcher.find()) {
            System.out.println("Matcher: " + matcher.group(0));
            System.out.println("Matcher: " + matcher.group(1));
            System.out.println("Matcher: " + matcher.group(2));
        }

        if(start > 0)
            sourceFile.replace(start, end, text);

        return super.visitStatement(ctx);
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
