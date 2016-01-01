package com.makkajai;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.makkajai.Constants.*;

/**
 * Autogenerated by ObjCToCppTranslator (c) 2015 Makkajai
 * @author Deep Shah
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
public class ObjCToCppTranslator extends ObjCBaseVisitor<Void> {

    private static final Dictionary<String, String> KEYWORDS_VS_TRANSLATIONS = new Hashtable<String, String>();
    private static final Dictionary<String, String> TYPES_VS_TRANSLATIONS = new Hashtable<String, String>();
    private static final Dictionary<String, String> METHODS_VS_TRANSLATIONS = new Hashtable<String, String>();
    private static final List<String> instanceVariables;
    private static final List<String> methodSignatures;
    private static String className;
    private static String superClassName;

    private String fileName;
    private CommonTokenStream tokens;
    private StringBuilder outputBuffer;
    private boolean isProcessingInstanceMethod;
    private Dictionary<String, String> sourceVsDestinationText;

    static {
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SUPER, Keywords.BASE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SELF, Keywords.THIS);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.NIL, Keywords.NULL);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_PUBLIC, Keywords.PUBLIC);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_PROTECTED, Keywords.PROTECTED);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_PRIVATE, Keywords.PRIVATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.YES, Keywords.TRUE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.NO, Keywords.FALSE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_QUOTE, Keywords.QUOTE);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.CCP, Methods.VEC2);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.CG_RECT_MAKE, Methods.RECT);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.CG_SIZE_MAKE, Methods.SIZE);

        TYPES_VS_TRANSLATIONS.put(Types.NS_OBJECT, Types.REF);
        TYPES_VS_TRANSLATIONS.put(Types.NS_STRING, Types.STRING);
        TYPES_VS_TRANSLATIONS.put(Types.CCNodeColor, Types.NODE);
        TYPES_VS_TRANSLATIONS.put(Types.NS_ARRAY, Types.ARRAY);
        TYPES_VS_TRANSLATIONS.put(Types.NS_MUTABLE_ARRAY, Types.ARRAY);
        TYPES_VS_TRANSLATIONS.put(Types.BOOL, Types.BOOL.toLowerCase());
        TYPES_VS_TRANSLATIONS.put(Types.ID, Types.REF_POINTER);
        TYPES_VS_TRANSLATIONS.put(Types.CGPOINT, Types.VEC2);
        TYPES_VS_TRANSLATIONS.put(Types.CGSIZE, Types.SIZE);
        TYPES_VS_TRANSLATIONS.put(Types.CGRECT, Types.RECT);
        TYPES_VS_TRANSLATIONS.put(Types.NSUINTEGER, Types.INT);
        TYPES_VS_TRANSLATIONS.put(Types.UP_FLOAT, Types.FLOAT);
        TYPES_VS_TRANSLATIONS.put(Types.NSDICTIONARY, Types.DICTIONARY);

        METHODS_VS_TRANSLATIONS.put(Methods.STRING_WITH_FORMAT, Methods.CREATE_WITH_FORMAT);
        METHODS_VS_TRANSLATIONS.put(Methods.CCP, Methods.VEC2);
        METHODS_VS_TRANSLATIONS.put(Methods.CG_RECT_MAKE, Methods.RECT);
        METHODS_VS_TRANSLATIONS.put(Methods.CG_SIZE_MAKE, Methods.SIZE);

        instanceVariables = new ArrayList<String>();
        methodSignatures = new ArrayList<String>();
    }

    /**
     * Main Method
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        translateFile(
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home.m"
                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.h"
        , false);
        translateFile(
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home.m"
                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.h"
        , true);
        translateFile(
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home.m"
                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.h"
        , true);
//        translateFile(
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.h"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.m"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil.m"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home.m"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene.h"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.m"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase.h"
//        );
    }

    private static void translateFile(String fileNameWithPath, boolean shouldCreateOutput) throws IOException {
        //The input file to parse!
        File file = new File(fileNameWithPath);

        FileInputStream fileInputStream = new FileInputStream(file);
        ANTLRInputStream input = new ANTLRInputStream(fileInputStream);

        //The instance of the translator.
        ObjCToCppTranslator visitor = new ObjCToCppTranslator();

        visitor.fileName = file.getName();

        //Getting the lexer.
        ObjCLexer lexer = new ObjCLexer(input);

        //This is the single most important guy.  Useful in getting the source set of tokens and stuff.
        visitor.tokens = new CommonTokenStream(lexer);

        //Parser obviously.
        ObjCParser parser = new ObjCParser(visitor.tokens);
        ParseTree tree = parser.translation_unit();
        visitor.outputBuffer = new StringBuilder()
                .append(visitor.tokens.getText());

        visitor.sourceVsDestinationText = new Hashtable<String, String>();
        if(!visitor.isHeaderFile()) {
            instanceVariables.clear();
        }
        if(visitor.isHeaderFile() && !shouldCreateOutput) {
            methodSignatures.clear();
        }

        //This is where the entire file is parsed and appropreat callbacks are made to parse the input file.
        visitor.visit(tree);

        //All done, lets write the output buffer to the output file and get done with it!
        if(shouldCreateOutput) {
            String outputFileName =
                    visitor.isHeaderFile()?
                        "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.h1" :
                        "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum.cpp";
            FileWriter outputFile = new FileWriter(outputFileName);
            outputFile.write(visitor.translateKeywords(visitor.getFileHeader() + visitor.outputBuffer.toString()));
            outputFile.flush();
            outputFile.close();
        }
    }

    @Override
    public Void visitPreprocessor_declaration(ObjCParser.Preprocessor_declarationContext ctx) {
        String importText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(importText);

        if(startIndex < 0)
            return super.visitPreprocessor_declaration(ctx);

        int endIndex = startIndex + importText.length();

        String fileToImport = importText.replaceAll(Keywords.IMPORT, "");

        Pattern pattern = Pattern.compile("((\"(.*)\")|(<(.*)>))\\W");
        Matcher matcher = pattern.matcher(fileToImport);
        if (!matcher.matches()) {
            return super.visitPreprocessor_declaration(ctx);
        }

        String headerFileName = matcher.group(3) == null? matcher.group(5) : matcher.group(3);
        boolean isWithAngleBrackets = matcher.group(3) == null;

        String[] parts = headerFileName.split("/");
        headerFileName = isWithAngleBrackets? "<" : "\"";
        for (int i =0; i<parts.length; i++) {
            if(i > 0) {
                headerFileName += "/";
            }
            headerFileName += translateIdentifier(parts[i]);
        }
        headerFileName += (isWithAngleBrackets? ">" : "\"") + "\n";
        writeToOutputBuffer(startIndex, endIndex, importText, Keywords.INCLUDE + headerFileName);

        return super.visitPreprocessor_declaration(ctx);
    }

    @Override
    public Void visitClass_interface(ObjCParser.Class_interfaceContext ctx) {
        String classImplementationText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classImplementationText);
        int startEndIndexIndex = outputBuffer.lastIndexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitClass_interface(ctx);

        className = ctx.class_name().getText();
        superClassName = ctx.superclass_name().getText();
        int startClassNameIndex = outputBuffer.indexOf(className, startIndex);
        int startSuperClassNameIndex = outputBuffer.indexOf(superClassName, startClassNameIndex);

        int endClassNameIndex = startClassNameIndex + className.length();
        int endSuperClassNameIndex = (startSuperClassNameIndex > 0)? startSuperClassNameIndex + superClassName.length() : endClassNameIndex;

        className = translateIdentifier(className);
        superClassName = translateIdentifier(superClassName);

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(),
                        String.format("\nCREATE_FUNC(%s);\n\n%s\n\n};\n\n#endif // __%s_H__", className, translatePrivateMethodsDeclaration(),
                                className.toUpperCase())
                )
                .replace(startIndex, endSuperClassNameIndex, translateClassDeclaration());

        return super.visitClass_interface(ctx);
    }

    @Override
    public Void visitClass_implementation(ObjCParser.Class_implementationContext ctx) {

        String classImplementationText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classImplementationText);
        int startEndIndexIndex = outputBuffer.lastIndexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitClass_implementation(ctx);

        className = translateIdentifier(ctx.class_name().getText());
        superClassName = ctx.superclass_name() != null ? ctx.superclass_name().getText() : superClassName;
        int startClassNameIndex = outputBuffer.indexOf(className, startIndex);

        int endClassNameIndex = startClassNameIndex + className.length();

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(), "")
                .replace(startIndex, endClassNameIndex, USING_NS_CC);

        className = translateIdentifier(className);
        superClassName = translateIdentifier(superClassName);

        return super.visitClass_implementation(ctx);
    }

    @Override
    public Void visitClass_method_declaration(ObjCParser.Class_method_declarationContext ctx) {
        String methodBody = tokens.getText(ctx.getSourceInterval());

        int startMethodBody = outputBuffer.indexOf(methodBody);

        startMethodBody = outputBuffer.lastIndexOf("+", startMethodBody);

        if(startMethodBody < 0) {
            return super.visitClass_method_declaration(ctx);
        }

        ObjCParser.Method_declarationContext method_declarationContext = ctx.method_declaration();

        translateMethodDefination(method_declarationContext.method_type(),
                method_declarationContext.method_selector(), tokens.getText(method_declarationContext), startMethodBody, "static ", false);

        return super.visitClass_method_declaration(ctx);
    }

    @Override
    public Void visitInstance_method_declaration(ObjCParser.Instance_method_declarationContext ctx) {
        String methodBody = tokens.getText(ctx.getSourceInterval());

        int startMethodBody = outputBuffer.indexOf(methodBody);

        startMethodBody = outputBuffer.lastIndexOf("-", startMethodBody);

        if(startMethodBody < 0) {
            return super.visitInstance_method_declaration(ctx);
        }

        ObjCParser.Method_declarationContext method_declarationContext = ctx.method_declaration();

        translateMethodDefination(method_declarationContext.method_type(),
                method_declarationContext.method_selector(), tokens.getText(method_declarationContext), startMethodBody, "virtual ", false);

        return super.visitInstance_method_declaration(ctx);
    }

    @Override
    public Void visitInstance_method_definition(ObjCParser.Instance_method_definitionContext ctx) {
        String methodBody = tokens.getText(ctx.getSourceInterval());

        int startMethodBody = outputBuffer.indexOf(methodBody);

        startMethodBody = outputBuffer.lastIndexOf("-", startMethodBody);

        if(startMethodBody < 0) {
            return super.visitInstance_method_definition(ctx);
        }

        translateMethodDefination(ctx.method_definition().method_type(), ctx.method_definition().method_selector(), tokens.getText(ctx), startMethodBody,
                "virtual ", true);

        isProcessingInstanceMethod = true;

        return super.visitInstance_method_definition(ctx);
    }

    @Override
    public Void visitClass_method_definition(ObjCParser.Class_method_definitionContext ctx) {
        String methodBody = tokens.getText(ctx.getSourceInterval());

        int startMethodBody = outputBuffer.indexOf(methodBody);

        startMethodBody = outputBuffer.lastIndexOf("+", startMethodBody);

        if(startMethodBody < 0) {
            return super.visitClass_method_definition(ctx);
        }

        translateMethodDefination(ctx.method_definition().method_type(), ctx.method_definition().method_selector(), tokens.getText(ctx), startMethodBody,
                "static ", true);

        isProcessingInstanceMethod = false;

        return super.visitClass_method_definition(ctx);
    }

    @Override
    public Void visitProperty_declaration(ObjCParser.Property_declarationContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);
        if(start < 0) {
            super.visitProperty_declaration(ctx);
        }

        String type = tokens.getText(ctx.struct_declaration().specifier_qualifier_list());
        String variable = tokens.getText(ctx.struct_declaration().struct_declarator_list());
        String finalPropertyText = "";

        if(variable.contains(ASTERISK)) {
            finalPropertyText += CC_SYNTHESIZE + translateIdentifier(type) + ASTERISK;
        } else {
            finalPropertyText += CC_SYNTHESIZE_PASS_BY_REF + translateIdentifier(type);
        }

        String transformedVariable = variable.replace('*', ' ').trim();
        finalPropertyText += ", " + transformedVariable + ", " + toUpperFirstLetter(transformedVariable) + ");";

        writeToOutputBuffer(start, start + sourceText.length(), sourceText, finalPropertyText);

        return super.visitProperty_declaration(ctx);
    }

    @Override
    public Void visitInstance_variables(ObjCParser.Instance_variablesContext ctx) {

        String sourceInstanceVariables = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceInstanceVariables);
        if(start < 0) {
            return super.visitInstance_variables(ctx);
        }

        ObjCParser.Visibility_specificationContext visibilitySpecificationContext = ctx.visibility_specification();
        if(visibilitySpecificationContext != null)
            instanceVariables.add(tokens.getText(visibilitySpecificationContext));

        for (ObjCParser.Struct_declarationContext structDeclarationContext : ctx.struct_declaration()) {
            String type = tokens.getText(structDeclarationContext.specifier_qualifier_list());
            String variable = tokens.getText(structDeclarationContext.struct_declarator_list());

            instanceVariables.add(translateIdentifier(type) + " " + variable + ";");
        }

        if(!isHeaderFile()) {
            writeToOutputBuffer(start, start+sourceInstanceVariables.length(), sourceInstanceVariables, "");
            return super.visitInstance_variables(ctx);
        }

        String finalInstanceVariables = "{\n\n";
        for (final String variable : instanceVariables) {
            finalInstanceVariables += variable + "\n";
        }

        finalInstanceVariables += "\npublic:";

        writeToOutputBuffer(start, start+sourceInstanceVariables.length(), sourceInstanceVariables, finalInstanceVariables);


        return super.visitInstance_variables(ctx);
    }

    @Override
    public Void visitDeclaration(ObjCParser.DeclarationContext ctx) {
        String sourceDeclaration = tokens.getText(ctx.getSourceInterval());

        int start = outputBuffer.indexOf(sourceDeclaration);
        if(start < 0) {
            return super.visitDeclaration(ctx);
        }

        String declarationSpecifierSourceText = tokens.getText(ctx.declaration_specifiers().getSourceInterval());
        int startDeclarationSpecifier = outputBuffer.indexOf(sourceDeclaration, start);

        writeToOutputBuffer(startDeclarationSpecifier, startDeclarationSpecifier + declarationSpecifierSourceText.length(),
                sourceDeclaration, translateIdentifier(declarationSpecifierSourceText));
        return super.visitDeclaration(ctx);
    }

    @Override
    public Void visitPostfix_expression(ObjCParser.Postfix_expressionContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);

        if(start < 0 || !(ctx.identifier() != null && ctx.identifier().size() > 0)) return super.visitPostfix_expression(ctx);

        String finalExpression = "";
        for (int i=0; i<ctx.children.size(); i++) {
            String nodeText = tokens.getText(ctx.children.get(i).getSourceInterval());
            if(ctx.children.get(i) instanceof ObjCParser.IdentifierContext) {
                finalExpression += "get" + toUpperFirstLetter(nodeText) + "()";
                finalExpression = "(" + finalExpression + ")";
            } else {
                if(nodeText.equals(".")) {
                    nodeText = INSTANCE_INVOCATION_OPERATOR;
                }
                finalExpression += nodeText;
            }
        }
        writeToOutputBuffer(start, start + sourceText.length(), sourceText, finalExpression);
        return super.visitPostfix_expression(ctx);
    }



    @Override
    public Void visitMessage_expression(ObjCParser.Message_expressionContext ctx) {
        String sourceText = tokens.getText(ctx.getSourceInterval());
        int start = outputBuffer.indexOf(sourceText);
        if(start < 0) {
            return super.visitMessage_expression(ctx);
        }
        int end = start + sourceText.length();

        String receiver = translateIdentifier(tokens.getText(ctx.receiver().getSourceInterval()));
        String finalMethod = "(" + receiver + translateInvocationOperator(receiver);
        finalMethod += translateMethodNameAndParameters(ctx.message_selector()) + ")";

        writeToOutputBuffer(start, end, sourceText, finalMethod);
        return super.visitMessage_expression(ctx);
    }

    private String translateIdentifier(String identifier) {
        if(identifier == null) return null;
        Pattern pattern = Pattern.compile(BEGINS_WITH_2_UPPER_CASE_LETTERS);
        Matcher matcher = pattern.matcher(identifier);
        boolean matches = matcher.matches();

        for (final String key : Collections.list(TYPES_VS_TRANSLATIONS.keys())) {
            if(identifier.startsWith(key)) {
                return identifier.replace(key, TYPES_VS_TRANSLATIONS.get(key));
            }
        }

        if(identifier.startsWith(CC)) {
            return COCOS2D + matcher.group(2);
        }
        if(matches) {
            return matcher.group(2);
        }

        if(identifier.startsWith(Keywords.SELF) && !isProcessingInstanceMethod) {
            return className;
        }

        return identifier;
    }

    private String translateMethodName(String identifier) {
        if(identifier == null) return null;

        for (final String key : Collections.list(METHODS_VS_TRANSLATIONS.keys())) {
            if(identifier.startsWith(key)) {
                return identifier.replace(key, METHODS_VS_TRANSLATIONS.get(key));
            }
        }

        return identifier;
    }

    private String translateInvocationOperator(String receiver) {
        Pattern pattern = Pattern.compile(BEGINS_WITH_UPPER_CASE_LETTERS);
        Matcher matcher = pattern.matcher(receiver.replaceAll(COCOS2D, ""));
        if(receiver.isEmpty()) return "";
        else if(matcher.matches()) {
            //Looks like we are making a static call.
            return STATIC_INVOCATION_OPERATOR;
        }
        return INSTANCE_INVOCATION_OPERATOR;
    }

    private String translateMethodNameAndParameters(ObjCParser.Message_selectorContext messageSelectorContext) {
        String finalMethodName = "";
        String finalParameters = "";

        //If this is just a method with no keyword argument we need to get just the selector and use it as the method name.
        if(messageSelectorContext.keyword_argument().size() == 0) {
            finalMethodName = tokens.getText(messageSelectorContext.selector().getSourceInterval());
        }

        //Well this method is made up of multiple parts we will need to upper case the subsequent parts to follow Camel case convention
        //and then pass the parameters as method call.
        for (int i = 0; i< messageSelectorContext.keyword_argument().size(); i++) {
            ObjCParser.Keyword_argumentContext keyword_argumentContext = messageSelectorContext.keyword_argument().get(i);
            ObjCParser.SelectorContext selector = keyword_argumentContext.selector();
            String sourceMethod = selector == null? "" : tokens.getText(selector.getSourceInterval());
            String sourceParameter = tokens.getText(keyword_argumentContext.expression().getSourceInterval());
            if(i > 0) {
                sourceMethod = toUpperFirstLetter(sourceMethod);
                finalParameters += ", ";
            }
            finalMethodName += translateMethodName(sourceMethod);
            finalParameters += sourceParameter;
        }

        return finalMethodName + "(" + finalParameters + ")";
    }

    private void translateMethodDefination(ObjCParser.Method_typeContext method_typeContext, ObjCParser.Method_selectorContext method_selectorContext,
                                           String sourceText, int startMethodBody, String methodPrefix, boolean shouldAddClassname) {

        String sourceMethodTypeString = tokens.getText(method_typeContext.getSourceInterval());
        String methodTypeString = tokens.getText(method_typeContext.getSourceInterval());
        int startMethodType = outputBuffer.indexOf(methodTypeString, startMethodBody);
        int endMethodType = startMethodType + methodTypeString.length();

        methodTypeString = methodPrefix + translateIdentifier(methodTypeString.replaceAll("\\(", "").replaceAll("\\)", " "));
        writeToOutputBuffer(startMethodBody, endMethodType, sourceMethodTypeString, methodTypeString);

        String classNamePrefix = shouldAddClassname? className + "::" : "";
        String finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix = translateMethodNameParameterTypeAndNames(method_selectorContext);
        String finalMethodNameParameterTypeAndNames = classNamePrefix + finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix;
        String finalMethodTypeNameParameterTypeAndNames = methodTypeString + finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix;

        String methodSelectorString = tokens.getText(method_selectorContext.getSourceInterval());
        int startMethodSelector = outputBuffer.indexOf(methodSelectorString, startMethodBody);
        int endMethodSelector = startMethodSelector + methodSelectorString.length();
        if(methodSignatures.contains(finalMethodTypeNameParameterTypeAndNames)) {
            methodSignatures.remove(finalMethodTypeNameParameterTypeAndNames);
        } else {
            methodSignatures.add(finalMethodTypeNameParameterTypeAndNames);
        }
        writeToOutputBuffer(startMethodSelector, endMethodSelector, sourceText, finalMethodNameParameterTypeAndNames);
    }

    private String translateMethodNameParameterTypeAndNames(ObjCParser.Method_selectorContext methodSelectorContext) {
        String finalMethodName = "";
        String finalParameters = "";

        //If this is just a method with no keyword argument we need to get just the selector and use it as the method name.
        if(methodSelectorContext.keyword_declarator().size() == 0) {
            finalMethodName = tokens.getText(methodSelectorContext.selector().getSourceInterval());
        }

        //Well this method is made up of multiple parts we will need to upper case the subsequent parts to follow Camel case convention
        //and then pass the parameters as method call.
        for (int i = 0; i< methodSelectorContext.keyword_declarator().size(); i++) {
            ObjCParser.Keyword_declaratorContext keyword_declaratorContext = methodSelectorContext.keyword_declarator().get(i);
            ObjCParser.SelectorContext selector = keyword_declaratorContext.selector();
            String sourceMethod = selector == null? "" : tokens.getText(selector.getSourceInterval());
            String sourceType = tokens.getText(keyword_declaratorContext.method_type(0).getSourceInterval());
            String sourceParameter = tokens.getText(keyword_declaratorContext.IDENTIFIER().getSourceInterval());
            if(i > 0) {
                sourceMethod = toUpperFirstLetter(sourceMethod);
                finalParameters += ", ";
            }
            finalMethodName += sourceMethod;
            finalParameters += translateIdentifier(sourceType.replaceAll("\\(", "").replaceAll("\\)", "")) + " " + sourceParameter;
        }

        return finalMethodName + "(" + finalParameters + ")";
    }

    private String translateKeywords(String source) {
        //Final set of translations to get rid of the imports and supers and self and nil.
        for (final String key : Collections.list(sourceVsDestinationText.keys())) {
            while(source.contains(key)) {
                source = source.replace(key, sourceVsDestinationText.get(key));
            }
        }

        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SUPER, superClassName + STATIC_INVOCATION_OPERATOR);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SUPER + INSTANCE_INVOCATION_OPERATOR, superClassName + STATIC_INVOCATION_OPERATOR);
        for (final String key : Collections.list(KEYWORDS_VS_TRANSLATIONS.keys())) {
            source = source.replaceAll(key, KEYWORDS_VS_TRANSLATIONS.get(key));
        }
        return source;
    }

    private String getFileHeader() {

        return
                  "/**\n"
                + " * Autogenerated by ObjCToCppTranslator on "
                + new SimpleDateFormat("dd/MM/yyyy").format(new Date())
                + "\n"
                + " * @author Deep Shah\n"
                + " * (c) 2015 Makkajai\n"
                + " * This code is licensed under MIT license (see LICENSE.txt for details)\n"
                + " */\n\n"
                + getIfnDefForHeaderFiles();
    }

    private String getIfnDefForHeaderFiles() {
        if(!isHeaderFile()) return "";
        return String.format("#ifndef __%s_H__\n#define __%s_H__\n\n", className.toUpperCase(), className.toUpperCase());
    }

    private String translateClassDeclaration() {
        return "class " + translateIdentifier(className) + translateSuperClassDeclaration();
    }

    private String translateSuperClassDeclaration() {
        if (superClassName == null) {
            return "";
        }

        return " : public " + translateIdentifier(superClassName);
    }

    private String toUpperFirstLetter(final String sourceMethod) {
        return sourceMethod != null && sourceMethod.length() > 0? sourceMethod.substring(0, 1).toUpperCase() + sourceMethod.substring(1) : "";
    }

    private boolean isHeaderFile() {
        return fileName.endsWith(H);
    }

    private String translatePrivateMethodsDeclaration() {
        String finalPrivateMethodsDeclaration = "private: \n";
        for (final String methodDeclaration : methodSignatures) {
            finalPrivateMethodsDeclaration += "\n" + methodDeclaration + ";";
        }
        finalPrivateMethodsDeclaration += "\n";
        return finalPrivateMethodsDeclaration;
    }

    private void writeToOutputBuffer(int startIndex, int endIndex, String sourceText, String destinationText) {
        outputBuffer.replace(startIndex, endIndex, destinationText);
        sourceVsDestinationText.put(sourceText, destinationText);
    }
}
