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
 * ObjCToCppTranslator - The purpose of this class is to parse the Objective C file input and translate it to Cpp!
 * (c) 2015 Makkajai
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
    private static String categoryClassName;

    private String fileName;
    private CommonTokenStream tokens;
    private StringBuilder outputBuffer;
    private boolean isProcessingInstanceMethod;
    private Dictionary<String, String> sourceVsDestinationText;
    private boolean isProtocol;

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
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.PERCENTAGE_QUOTE, Keywords.PERCENTAGE_S);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_CLASS, Keywords.CLASS);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.AT_PROTOCOL, Keywords.CLASS);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SEQUENCE_ACTIONS, Keywords.SEQUENCE_CREATEWITHVARIABLELIST);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONFINITETIME, Keywords.FINITETIMEACTION);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONMOVETO_CREATEPOSITION, Keywords.MOVETO_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONREPEATFOREVER_ACTIONWITHACTION, Keywords.REPEATFOREVER_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONSCALETO_CREATESCALE, Keywords.SCALETO_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONFADETO_CREATEOPACITY, Keywords.MAKKAJAIFADETO_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONFADEIN_CREATEOPACITY, Keywords.MAKKAJAIFADEIN_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONFADEOUT_CREATEOPACITY, Keywords.MAKKAJAIFADEOUT_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ISEQUALTOSTRING, Keywords.ISEQUAL);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.UP_FLOAT, Keywords.FLOAT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONSPAWN_ACTIONS, Keywords.SPAWN_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONROTATEBY_CREATEANGLE, Keywords.ROTATEBY_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONSCALEBY_CREATESCALE, Keywords.SCALEBY_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SPRITE_SPRITEWITHIMAGENAMED, Keywords.SPRITE_CREATEWITHSPRITEFRAMENAME);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SPRITE_SPRITEWITHFILE, Keywords.SPRITE_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ACTIONEASEBOUNCEOUT_ACTIONWITHACTION, Keywords.EASEBOUNCEOUT_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SZWIN_GETWIDTH, Keywords.SZWIN_WIDTH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.SZWIN_GETHEIGHT, Keywords.SZWIN_HEIGHT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETSZWIN_GETWIDTH, Keywords.GETSZWIN_WIDTH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETSZWIN_GETHEIGHT, Keywords.GETSZWIN_HEIGHT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETCONTENTSIZE_GETWIDTH, Keywords.GETCONTENTSIZE_WIDTH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETCONTENTSIZE_GETHEIGHT, Keywords.GETCONTENTSIZE_HEIGHT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.CONST_STD_STRING_POINTER, Keywords.CONST_STD_STRING);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.CGPOINTZERO, Keywords.VEC2_ZERO);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.BUTTON, Keywords.UI_BUTTON);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.ARRAY_WITH, Keywords.CREATE_WITH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.DICTIONARY_WITH, Keywords.CREATE_WITH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.STRING_WITH, Keywords.CREATE_WITH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.DDLOGVERBOSE, Keywords.CCLOGINFO);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.DDLOGWARN, Keywords.CCLOGWARN);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.DDLOGERROR, Keywords.CCLOGERROR);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.TOBEDELETED_TEMP_STRING, Keywords.EMPTY);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.__ARRAY_ARRAY, Keywords.__ARRAY_CREATE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.DIRECTOR_SHARED_DIRECTOR, Keywords.DIRECTOR_GETINSTANCE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.CCBUTTON, Keywords.UI_BUTTON);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.UBYTE, Keywords.GLUBYTE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.TIME, Keywords.FLOAT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETX, Keywords.X);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETY, Keywords.Y);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETWIDTH, Keywords.WIDTH);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETHEIGHT, Keywords.HEIGHT);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETSIZE, Keywords.SIZE);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.GETORIGIN, Keywords.ORIGIN);
        KEYWORDS_VS_TRANSLATIONS.put(Keywords.CGPOINTMAKE, Keywords.VEC2);
        KEYWORDS_VS_TRANSLATIONS.put(Types.NS_STRING, Types.STRING);

        KEYWORDS_VS_TRANSLATIONS.put(Methods.CCP, Methods.VEC2);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.CG_RECT_MAKE, Methods.RECT);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.CG_SIZE_MAKE, Methods.SIZE);
        KEYWORDS_VS_TRANSLATIONS.put(Methods.ACTIONONETWO, Methods.CREATEWITHTWOACTIONS);

        TYPES_VS_TRANSLATIONS.put(Types.NS_OBJECT, Types.REF);
        TYPES_VS_TRANSLATIONS.put(Types.NS_STRING, Types.STRING);
        TYPES_VS_TRANSLATIONS.put(Types.CCNodeColor, Types.NODE);
        TYPES_VS_TRANSLATIONS.put(Types.NS_ARRAY, Types.ARRAY);
        TYPES_VS_TRANSLATIONS.put(Types.NS_MUTABLE_ARRAY, Types.ARRAY);
        TYPES_VS_TRANSLATIONS.put(Types.NS_SET, Types.SET);
        TYPES_VS_TRANSLATIONS.put(Types.NS_MUTABLE_SET, Types.SET);
        TYPES_VS_TRANSLATIONS.put(Types.BOOL, Types.BOOL.toLowerCase());
        TYPES_VS_TRANSLATIONS.put(Types.ID, Types.REF_POINTER);
        TYPES_VS_TRANSLATIONS.put(Types.CGPOINT, Types.VEC2);
        TYPES_VS_TRANSLATIONS.put(Types.CGSIZE, Types.SIZE);
        TYPES_VS_TRANSLATIONS.put(Types.CGRECT, Types.RECT);
        TYPES_VS_TRANSLATIONS.put(Types.NSUINTEGER, Types.INT);
        TYPES_VS_TRANSLATIONS.put(Types.NSINTEGER, Types.INT);
        TYPES_VS_TRANSLATIONS.put(Types.UP_FLOAT, Types.FLOAT);
        TYPES_VS_TRANSLATIONS.put(Types.NSDICTIONARY, Types.DICTIONARY);
        TYPES_VS_TRANSLATIONS.put(Types.NSMUTABLEDICTIONARY, Types.DICTIONARY);
        TYPES_VS_TRANSLATIONS.put(Types.CCTOUCHEVENT, Types.EVENT);
        TYPES_VS_TRANSLATIONS.put(Types.CCACTIONCALLFUNC, Types.CALLFUNC);
        TYPES_VS_TRANSLATIONS.put(Types.CCACTIONCALLFUNCO, Types.CALLFUNCO);
        TYPES_VS_TRANSLATIONS.put(Types.CCACTIONCALLBLOCK, Types.CALLFUNC);
        TYPES_VS_TRANSLATIONS.put(Types.CCACTIONDELAY, Types.DELAYTIME);
        TYPES_VS_TRANSLATIONS.put(Types.CCACTIONSEQUENCE, Types.SEQUENCE);
        TYPES_VS_TRANSLATIONS.put(Types.CCCOLOR, Types.MAKKAJAICOLOR);
        TYPES_VS_TRANSLATIONS.put(Types.CCLABELTTF, Types.LABEL);
        TYPES_VS_TRANSLATIONS.put(Types.NSNOTIFICATIONCENTER, Types.__NOTIFICATIONCENTER);
        TYPES_VS_TRANSLATIONS.put(Types.NSNUMBER, Types.__INTEGER);
        TYPES_VS_TRANSLATIONS.put(Types.SEL, Types.SEL_CALLFUNCO);

        METHODS_VS_TRANSLATIONS.put(Methods.STRING_WITH_FORMAT, Methods.CREATE_WITH_FORMAT);
        METHODS_VS_TRANSLATIONS.put(Methods.CCP, Methods.VEC2);
        METHODS_VS_TRANSLATIONS.put(Methods.CG_RECT_MAKE, Methods.RECT);
        METHODS_VS_TRANSLATIONS.put(Methods.CG_SIZE_MAKE, Methods.SIZE);
        METHODS_VS_TRANSLATIONS.put(Methods.ACTIONWITHBLOCK, Methods.CREATE);
        METHODS_VS_TRANSLATIONS.put(Methods.ACTIONWITHDURATION, Methods.CREATE);
        METHODS_VS_TRANSLATIONS.put(Methods.ACTIONONETWO, Methods.CREATEWITHTWOACTIONS);
        METHODS_VS_TRANSLATIONS.put(Methods.NODE, Methods.CREATE);
        METHODS_VS_TRANSLATIONS.put(Methods.ADDCHILDZ, Methods.ADDCHILD);
        METHODS_VS_TRANSLATIONS.put(Methods.SCHEDULEINTERVAL, Methods.SCHEDULE);
        METHODS_VS_TRANSLATIONS.put(Methods.ALLOC, Methods.CREATE);
        METHODS_VS_TRANSLATIONS.put(Methods.LABELWITHSTRING_FONTNAME_FONTSIZE, Methods.CREATEWITHTTF);
        METHODS_VS_TRANSLATIONS.put(Methods.ADDOBSERVER_SELECTOR_NAME_OBJECT, Methods.ADDOBSERVER);
        METHODS_VS_TRANSLATIONS.put(Methods.ADDCHILDNAME, Methods.ADDCHILD);
        METHODS_VS_TRANSLATIONS.put(Methods.ADDCHILDTAG, Methods.ADDCHILD);
        METHODS_VS_TRANSLATIONS.put(Methods.REMOVECHILDCLEANUP, Methods.REMOVECHILD);
        METHODS_VS_TRANSLATIONS.put(Methods.REMOVECHILDBYTAGCLEANUP, Methods.REMOVECHILDBYTAG);
        METHODS_VS_TRANSLATIONS.put(Methods.VIEWSIZE, Methods.GETWINSIZE);
        METHODS_VS_TRANSLATIONS.put(Methods.RUNNINGSCENE, Methods.GETRUNNINGSCENE);
        METHODS_VS_TRANSLATIONS.put(Methods.COPY, Methods.CLONE);
        METHODS_VS_TRANSLATIONS.put(Methods.OBJECTFORKEY, Methods.VALUEFORKEY);
        METHODS_VS_TRANSLATIONS.put(Methods.SETOBJECTFORKEY, Methods.SETOBJECT);

        instanceVariables = new ArrayList<String>();
        methodSignatures = new ArrayList<String>();
    }

    void translateFile(final TranslateFileInput translateFileInput) throws IOException {
        if(translateFileInput.filePathRelativeToInput.contains("IconWithText")) {
            System.out.println("Break!");
        }
        initializeVariables(translateFileInput);
        startParsing(translateFileInput);
        writeOutput(translateFileInput);
    }



    private void initializeVariables(final TranslateFileInput translateFileInput) throws IOException {
        //The input file to parse!
        File file = new File(translateFileInput.getAbsoluteInputFilePath());

        fileName = file.getName();
        //Getting the lexer.
        ObjCLexer lexer = new ObjCLexer(new ANTLRInputStream(new FileInputStream(file)));

        //This is the single most important guy.  Useful in getting the source set of tokens and stuff.
        tokens = new CommonTokenStream(lexer);
        outputBuffer = new StringBuilder().append(tokens.getText());
        sourceVsDestinationText = new Hashtable<String, String>();

        if(!isHeaderFile()) {
            instanceVariables.clear();
        }
        if(isHeaderFile() && translateFileInput.dryRun) {
            methodSignatures.clear();
        }
    }

    private void startParsing(final TranslateFileInput translateFileInput) {
        //Parser obviously.
        ObjCParser parser = new ObjCParser(tokens);
        ParseTree tree = parser.translation_unit();
        //This is where the entire file is parsed and appropreat callbacks are made to parse the input file.
        visit(tree);
    }

    private void writeOutput(final TranslateFileInput translateFileInput) throws IOException {
        //All done, lets write the output buffer to the output file and get done with it!
        if (translateFileInput.dryRun)
            return;

        File outputFile = new File(translateFileInput.getAbsoluteOutputFilePath());
        outputFile.getParentFile().mkdirs();

        FileWriter outputFileWriter = new FileWriter(outputFile);
        outputFileWriter.write(translateKeywords(getFileHeader() + outputBuffer.toString()));
        outputFileWriter.flush();
        outputFileWriter.close();
    }

    @Override
    public Void visitPreprocessor_declaration(ObjCParser.Preprocessor_declarationContext ctx) {
        String importText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(importText);

        if(startIndex < 0)
            return super.visitPreprocessor_declaration(ctx);

        int endIndex = startIndex + importText.length();

        String fileToImport = importText.replaceAll(Keywords.IMPORT, EMPTY_STRING);

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
            headerFileName += parts[i];
        }
        headerFileName += (isWithAngleBrackets? ">" : "\"") + "\n";
        //Ignoring certain headers.
        if(importText.contains("Foundation/Foundation.h")
                || importText.contains("ReflectionKit.h")
                || importText.contains("CoreGraphics.h")
                || importText.contains("OverlayEffects.h")
                || importText.contains("MultipeerConnectivity/MultipeerConnectivity.h")
                || importText.contains("GameKit/GameKit.h")
                )
            writeToOutputBuffer(startIndex, endIndex, importText, "#include \"cocos2d.h\"\n", true);
        else
            writeToOutputBuffer(startIndex, endIndex, importText, Keywords.INCLUDE + headerFileName, true);

        return super.visitPreprocessor_declaration(ctx);
    }

    @Override
    public Void visitClass_list(ObjCParser.Class_listContext ctx) {
        String classListText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classListText);

        if(startIndex < 0)
            return super.visitClass_list(ctx);

        List<ObjCParser.Class_nameContext> class_nameContexts = ctx.class_name();
        for (ObjCParser.Class_nameContext classNameContext : class_nameContexts) {
            String sourceClassName = tokens.getText(classNameContext);
            String translatedClassName = translateIdentifier(sourceClassName);
            int startClassNameIndex = outputBuffer.indexOf(sourceClassName, startIndex);
            writeToOutputBuffer(startClassNameIndex, startClassNameIndex + sourceClassName.length(), sourceClassName, translatedClassName, true);
        }

        return super.visitClass_list(ctx);
    }

    @Override
    public Void visitProtocol_declaration_list(ObjCParser.Protocol_declaration_listContext ctx) {
        String protocolListText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(protocolListText);

        if(startIndex < 0)
            return super.visitProtocol_declaration_list(ctx);

        List<ObjCParser.Protocol_nameContext> protocol_nameContexts = ctx.protocol_list().protocol_name();
        for (ObjCParser.Protocol_nameContext protocolNameContext : protocol_nameContexts) {
            String sourceClassName = tokens.getText(protocolNameContext);
            String translatedClassName = translateIdentifier(sourceClassName);
            int startClassNameIndex = outputBuffer.indexOf(sourceClassName, startIndex);
            writeToOutputBuffer(startClassNameIndex, startClassNameIndex + sourceClassName.length(), sourceClassName, translatedClassName, true);
        }

        return super.visitProtocol_declaration_list(ctx);
    }

    @Override
    public Void visitProtocol_declaration(ObjCParser.Protocol_declarationContext ctx) {
        String protocolInterfaceText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(protocolInterfaceText);
        int startEndIndexIndex = outputBuffer.lastIndexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitProtocol_declaration(ctx);

        isProtocol = true;
        className = ctx.protocol_name().IDENTIFIER().getText();
        superClassName = null;
        categoryClassName = null;
        int indexOfFirstNewLine = outputBuffer.indexOf("\n", startIndex);
        int indexOfBrace = outputBuffer.indexOf("{", startIndex);
        int finalIndexToConsider = (indexOfBrace >= 0)? indexOfBrace : indexOfFirstNewLine;
        String suffix = (indexOfBrace >= 0)? EMPTY_STRING : "{";

        className = translateIdentifier(className);
        superClassName = translateIdentifier(superClassName);

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(),
                        String.format("\n\n};\n\n#endif // __%s_H__", className.toUpperCase())
                )
                .replace(startIndex, finalIndexToConsider, translateClassDeclaration(ctx.protocol_reference_list()) + suffix + "\n\npublic:\n\n");

        return super.visitProtocol_declaration(ctx);
    }

    @Override
    public Void visitClass_interface(ObjCParser.Class_interfaceContext ctx) {
        String classInterfaceText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classInterfaceText);
        int startEndIndexIndex = outputBuffer.indexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitClass_interface(ctx);

        isProtocol = false;
        className = ctx.class_name().IDENTIFIER().getText();
        superClassName = ctx.superclass_name().IDENTIFIER().getText();
        categoryClassName = null;
        int indexOfFirstNewLine = outputBuffer.indexOf("\n", startIndex);
        int indexOfBrace = outputBuffer.indexOf("{", startIndex);
        int finalIndexToConsider = (indexOfBrace >= 0)? indexOfBrace : indexOfFirstNewLine;
        String suffix = ((indexOfBrace >= 0)? EMPTY_STRING : "{\n\npublic:\n\n");

        className = translateIdentifier(className);
        superClassName = translateIdentifier(superClassName);

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(),
                        String.format("\nCREATE_FUNC(%s);\n\n%s\n\n};\n\n#endif // __%s_H__", className, translatePrivateMethodsDeclaration(),
                                className.toUpperCase())
                )
                .replace(startIndex, finalIndexToConsider, translateClassDeclaration(ctx.protocol_reference_list()) + suffix);

        return super.visitClass_interface(ctx);
    }

    @Override
    public Void visitCategory_interface(ObjCParser.Category_interfaceContext ctx) {
        String classInterfaceText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classInterfaceText);
        int startEndIndexIndex = outputBuffer.indexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitCategory_interface(ctx);

        isProtocol = false;
        className = ctx.class_name().IDENTIFIER().getText();
        superClassName = null;
        categoryClassName = ctx.category_name() != null? ctx.category_name().IDENTIFIER().getText() : null;
        int indexOfFirstNewLine = outputBuffer.indexOf("\n", startIndex);
        int indexOfBrace = outputBuffer.indexOf("{", startIndex);
        int finalIndexToConsider = (indexOfBrace >= 0)? indexOfBrace : indexOfFirstNewLine;
        String suffix = ((indexOfBrace >= 0)? EMPTY_STRING : "{\n\npublic:\n\n");

        className = translateIdentifier(className);
        categoryClassName = translateIdentifier(categoryClassName);

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(),
                        String.format("\nCREATE_FUNC(%s);\n\n%s\n\n};\n\n#endif // __%s_H__", className, translatePrivateMethodsDeclaration(),
                                className.toUpperCase())
                )
                .replace(startIndex, finalIndexToConsider, translateClassDeclaration(ctx.protocol_reference_list()) + suffix);

        return super.visitCategory_interface(ctx);
    }

    @Override
    public Void visitClass_implementation(ObjCParser.Class_implementationContext ctx) {

        String classImplementationText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classImplementationText);
        int startEndIndexIndex = outputBuffer.indexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitClass_implementation(ctx);

        isProtocol = false;
        String originalClassName = ctx.class_name().getText();
        className = translateIdentifier(originalClassName);
        superClassName = ctx.superclass_name() != null ? ctx.superclass_name().getText() : superClassName;
        categoryClassName = null;
        int startClassNameIndex = outputBuffer.indexOf(originalClassName, startIndex);

        int endClassNameIndex = startClassNameIndex + originalClassName.length();

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(), EMPTY_STRING)
                .replace(startIndex, endClassNameIndex, USING_NS_CC);

        className = translateIdentifier(className);
        superClassName = translateIdentifier(superClassName);

        return super.visitClass_implementation(ctx);
    }

    @Override
    public Void visitCategory_implementation(ObjCParser.Category_implementationContext ctx) {
        String classImplementationText = tokens.getText(ctx.getSourceInterval());
        int startIndex = outputBuffer.indexOf(classImplementationText);
        int startEndIndexIndex = outputBuffer.indexOf(Keywords.END);

        if(startIndex < 0)
            return super.visitCategory_implementation(ctx);

        isProtocol = false;
        String originalClassName = ctx.class_name().getText();
        className = translateIdentifier(originalClassName);
        superClassName = null;
        categoryClassName = ctx.category_name() != null? ctx.category_name().IDENTIFIER().getText() : null;
        int startCategoryNameIndex = outputBuffer.indexOf(categoryClassName, startIndex);

        int endClassNameIndex = startCategoryNameIndex + categoryClassName.length() + 1;

        outputBuffer
                .replace(startEndIndexIndex, startEndIndexIndex + Keywords.END.length(), EMPTY_STRING)
                .replace(startIndex, endClassNameIndex, USING_NS_CC);

        categoryClassName = translateIdentifier(categoryClassName);
        className = translateIdentifier(className) + (categoryClassName != null?  "_" + categoryClassName : "");

        return super.visitCategory_implementation(ctx);
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
    public Void visitBlock_expression(ObjCParser.Block_expressionContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);
        if(start < 0) {
            return super.visitBlock_expression(ctx);
        }

        int indexOfBrace = sourceText.indexOf("{");

        String finalParameterTypeAndName = EMPTY_STRING;

        List<ObjCParser.Type_variable_declaratorContext> typeVariableDeclaratorContexts =
                ctx.block_parameters() == null? new ArrayList<ObjCParser.Type_variable_declaratorContext>()
                        : ctx.block_parameters().type_variable_declarator();
        for(ObjCParser.Type_variable_declaratorContext typeVariableDeclaratorContext : typeVariableDeclaratorContexts) {
            VariableTypeNameInfo variableTypeNameInfo = translateTypeVariableDeclarator(typeVariableDeclaratorContext);
            if(!finalParameterTypeAndName.equals(EMPTY_STRING)) {
                finalParameterTypeAndName += ", ";
            }
            finalParameterTypeAndName += variableTypeNameInfo.variableType + variableTypeNameInfo.pointer + " " + variableTypeNameInfo.variableName;
        }

        writeToOutputBuffer(start, start + indexOfBrace, sourceText.substring(0, indexOfBrace), "[] (" + finalParameterTypeAndName + ") ", true);

        return super.visitBlock_expression(ctx);
    }

    @Override
    public Void visitProperty_declaration(ObjCParser.Property_declarationContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);
        if(start < 0) {
            return super.visitProperty_declaration(ctx);
        }

        String type = tokens.getText(ctx.struct_declaration().specifier_qualifier_list());
        String variable = tokens.getText(ctx.struct_declaration().struct_declarator_list());
        String finalPropertyText = EMPTY_STRING;

        String translatedIdentifier = translateIdentifier(type);
        if(variable.contains(ASTERISK)) {
            finalPropertyText += CC_SYNTHESIZE + translatedIdentifier + ASTERISK;
        } else if (type.startsWith(Constants.ID)) {
            finalPropertyText += CC_SYNTHESIZE + translatedIdentifier;
        } else {
            finalPropertyText += CC_SYNTHESIZE_PASS_BY_REF + translatedIdentifier;
        }

        String transformedVariable = variable.replace('*', ' ').trim();
        finalPropertyText += ", " + transformedVariable + ", " + toUpperFirstLetter(transformedVariable) + ");";

        writeToOutputBuffer(start, start + sourceText.length(), sourceText, finalPropertyText, true);

        return super.visitProperty_declaration(ctx);
    }

    @Override
    public Void visitProperty_implementation(ObjCParser.Property_implementationContext ctx) {

        String sourcePropertyImplementation = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourcePropertyImplementation);
        if(start < 0) {
            return super.visitProperty_implementation(ctx);
        }

        writeToOutputBuffer(start, start + sourcePropertyImplementation.length(), sourcePropertyImplementation, "", true);

        return super.visitProperty_implementation(ctx);
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
            writeToOutputBuffer(start, start+sourceInstanceVariables.length(), sourceInstanceVariables, EMPTY_STRING, true);
            return super.visitInstance_variables(ctx);
        }

        String finalInstanceVariables = "{\n\n";
        for (final String variable : instanceVariables) {
            finalInstanceVariables += variable + "\n";
        }

        finalInstanceVariables += "\npublic:";

        writeToOutputBuffer(start, start+sourceInstanceVariables.length(), sourceInstanceVariables, finalInstanceVariables, true);


        return super.visitInstance_variables(ctx);
    }

    @Override
    public Void visitPrimary_expression(ObjCParser.Primary_expressionContext ctx) {
        if(ctx.STRING_LITERAL() != null) {
            String sourceString = tokens.getText(ctx);

            int start = outputBuffer.indexOf(sourceString);
            if(start < 0) {
                return super.visitPrimary_expression(ctx);
            }

            writeToOutputBuffer(start, start+sourceString.length(), sourceString, "cocos2d::__String::create("
                    + sourceString.substring(0, 1)
                    + "TOBEDELETED_TEMP_STRING"
                    +  sourceString.substring(1) + ")", true);
            return super.visitPrimary_expression(ctx);
        }
        return super.visitPrimary_expression(ctx);
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
                declarationSpecifierSourceText, translateIdentifier(declarationSpecifierSourceText), false);
        return super.visitDeclaration(ctx);
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

        writeToOutputBuffer(start, end, sourceText, finalMethod, true);
        return super.visitMessage_expression(ctx);
    }

    @Override
    public Void visitSelector_expression(ObjCParser.Selector_expressionContext ctx) {
        String sourceText = tokens.getText(ctx.getSourceInterval());
        int start = outputBuffer.indexOf(sourceText);
        if(start < 0) {
            return super.visitSelector_expression(ctx);
        }

        String macroToUse = CC_CALLBACK + (sourceText.length() - sourceText.replace(":", EMPTY_STRING).length()) + "(";
        List<ObjCParser.SelectorContext> selectorParts = ctx.selector_name().selector();
        String finalMethodName = EMPTY_STRING;
        for (ObjCParser.SelectorContext selector : selectorParts) {
            String currentSelectorPart = tokens.getText(selector);
            finalMethodName += finalMethodName.equals(EMPTY_STRING)? translateMethodName(currentSelectorPart) : toUpperFirstLetter(currentSelectorPart);
        }

        writeToOutputBuffer(start, start + sourceText.length(), sourceText, macroToUse
                + className + STATIC_INVOCATION_OPERATOR + translateMethodName(finalMethodName) + ", this)", true);

        return super.visitSelector_expression(ctx);
    }

    @Override
    public Void visitAssignment_expression(ObjCParser.Assignment_expressionContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);

        if(start < 0
                || ctx.assignment_operator() == null
                || ctx.unary_expression() == null
                || ctx.unary_expression().postfix_expression() == null
                ) return super.visitAssignment_expression(ctx);

        String postFixSetterExpression = visitPostFixExpression(ctx.unary_expression().postfix_expression(), true);
        if(!postFixSetterExpression.contains("("))
            return super.visitAssignment_expression(ctx);

        String postFixGetterExpression = visitPostFixExpression(ctx.unary_expression().postfix_expression(), false);
        String assignmentRHS = tokens.getText(ctx.assignment_expression());

        String assignmentOperator = tokens.getText(ctx.assignment_operator());
        assignmentOperator = assignmentOperator.replace("=", "");

        //What we are eseentially doing here is just adding a placeholder so that it can be replaced during further processing by other callback methods.
        String finalExpression = "(" + tokens.getText(ctx.unary_expression()) + SETTER_PLACEHOLDER;
        if(assignmentOperator.trim().equals("")) {
            finalExpression += assignmentRHS + "))";
        } else {
            finalExpression += postFixGetterExpression + " " + assignmentOperator + " " + assignmentRHS + "))";
        }

        writeToOutputBuffer(start, start + sourceText.length(), sourceText, finalExpression, true);
        return super.visitAssignment_expression(ctx);
    }

    @Override
    public Void visitPostfix_expression(ObjCParser.Postfix_expressionContext ctx) {
        String sourceText = tokens.getText(ctx);

        int start = outputBuffer.indexOf(sourceText);

        if(start < 0 || !(ctx.identifier() != null && ctx.identifier().size() > 0))
            return super.visitPostfix_expression(ctx);

        int endIndex = start + (sourceText + SETTER_PLACEHOLDER).length();
        endIndex = outputBuffer.length() <= endIndex? outputBuffer.length() - 1 : endIndex;
        boolean isSetter = outputBuffer.substring(start, endIndex).contains(SETTER_PLACEHOLDER);
        String finalExpression = visitPostFixExpression(ctx, isSetter);
        writeToOutputBuffer(start, start + (isSetter? (sourceText + SETTER_PLACEHOLDER).length() : sourceText.length()), sourceText, finalExpression, true);
        return super.visitPostfix_expression(ctx);
    }

    public String visitPostFixExpression(ObjCParser.Postfix_expressionContext ctx, boolean isSetter) {

        String finalExpression = EMPTY_STRING;
        for (int i=0; i<ctx.children.size(); i++) {
            String nodeText = tokens.getText(ctx.children.get(i).getSourceInterval());
            if(ctx.children.get(i) instanceof ObjCParser.IdentifierContext) {
                finalExpression += ((isSetter && i == ctx.children.size() - 1) ? "set" : "get") + toUpperFirstLetter(nodeText) + ((isSetter && i == ctx.children.size() - 1) ? "(" : "()");
                finalExpression = (isSetter ? "" : "(") + finalExpression + (isSetter ? "" : ")");
            } else {
                if(nodeText.equals(".")) {
                    nodeText = INSTANCE_INVOCATION_OPERATOR;
                }
                finalExpression += nodeText;
            }
        }
        return finalExpression;
    }

    @Override
    public Void visitFor_in_statement(ObjCParser.For_in_statementContext ctx) {
        String sourceForLoop = tokens.getText(ctx);

        int startForLoop = outputBuffer.indexOf(sourceForLoop);
        if(startForLoop < 0) {
            return super.visitFor_in_statement(ctx);
        }

        int indexOfPreviousNewline = outputBuffer.lastIndexOf("\n", startForLoop);
        int indexOfFirstNewLine = sourceForLoop.indexOf("\n");

        String whitespaceSeparator = outputBuffer.substring(indexOfPreviousNewline, startForLoop);
        String whitespaceSeparatorInLoop =
                sourceForLoop.substring(indexOfFirstNewLine + 1,
                        indexOfFirstNewLine + sourceForLoop.substring(indexOfFirstNewLine).length() - sourceForLoop.substring(indexOfFirstNewLine).trim().length());


        VariableTypeNameInfo variableTypeNameInfo = translateTypeVariableDeclarator(ctx.type_variable_declarator());

        String variableNameWithRef = variableTypeNameInfo.variableName + "_ref";

        //Please forgive me for this ugly code to also take care of intend.
        String finalForLoop =
                Types.REF_POINTER + " " + variableNameWithRef + ";"
                        + whitespaceSeparator
                        + CCARRAY_FOREACH + tokens.getText(ctx.expression()) + ", "
                        + variableNameWithRef + ") {\n"
                        + whitespaceSeparatorInLoop
                        + variableTypeNameInfo.variableType + " " + variableTypeNameInfo.pointer + variableTypeNameInfo.variableName + DYNAMIC_CAST
                        + variableTypeNameInfo.variableType
                        + variableTypeNameInfo.pointer
                        + ">(" + variableNameWithRef + ");";


        writeToOutputBuffer(startForLoop, startForLoop + sourceForLoop.length(), sourceForLoop, finalForLoop + sourceForLoop.substring(indexOfFirstNewLine), true);

        return super.visitFor_in_statement(ctx);
    }

    private String translateIdentifier(String identifier) {
        if(identifier == null) return null;
        Pattern beginsWithUpperCaseLetters = Pattern.compile(BEGINS_WITH_2_UPPER_CASE_LETTERS);
        Pattern beginsWithId = Pattern.compile(BEGINS_WITH_ID);
        Matcher matcherForUpperCaseLetters = beginsWithUpperCaseLetters.matcher(identifier);
        Matcher matcherForId = beginsWithId.matcher(identifier);
        boolean matchesUpperCaseLetters = matcherForUpperCaseLetters.matches();
        boolean matchesId = matcherForId.matches();

        if(matchesId) {
            String protocolName = matcherForId.group(1);
            return translateIdentifier(protocolName) + " * ";
        }

        for (final String key : Collections.list(TYPES_VS_TRANSLATIONS.keys())) {
            if(identifier.startsWith(key)) {
                return identifier.replace(key, TYPES_VS_TRANSLATIONS.get(key));
            }
        }

        if(identifier.startsWith(CC)) {
            return COCOS2D + matcherForUpperCaseLetters.group(2);
        }
        if(matchesUpperCaseLetters) {
            return matcherForUpperCaseLetters.group(2);
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
        Matcher matcher = pattern.matcher(receiver.replaceAll(COCOS2D, EMPTY_STRING));
        if(receiver.isEmpty()) return EMPTY_STRING;
        else if(matcher.matches()) {
            //Looks like we are making a static call.
            return STATIC_INVOCATION_OPERATOR;
        }
        return INSTANCE_INVOCATION_OPERATOR;
    }

    private String translateMethodNameAndParameters(ObjCParser.Message_selectorContext messageSelectorContext) {
        String finalMethodName = EMPTY_STRING;
        String finalParameters = EMPTY_STRING;

        //If this is just a method with no keyword argument we need to get just the selector and use it as the method name.
        if(messageSelectorContext.keyword_argument().size() == 0) {
            finalMethodName = tokens.getText(messageSelectorContext.selector().getSourceInterval());
        }

        //Well this method is made up of multiple parts we will need to upper case the subsequent parts to follow Camel case convention
        //and then pass the parameters as method call.
        for (int i = 0; i< messageSelectorContext.keyword_argument().size(); i++) {
            ObjCParser.Keyword_argumentContext keyword_argumentContext = messageSelectorContext.keyword_argument().get(i);
            ObjCParser.SelectorContext selector = keyword_argumentContext.selector();
            String sourceMethod = selector == null? EMPTY_STRING : tokens.getText(selector.getSourceInterval());
            String sourceParameter = tokens.getText(keyword_argumentContext.expression().getSourceInterval());
            if(i > 0) {
                sourceMethod = toUpperFirstLetter(sourceMethod);
                finalParameters += ", ";
            }
            finalMethodName += translateMethodName(sourceMethod);
            finalParameters += sourceParameter;
        }

        return translateMethodName(finalMethodName) + "(" + finalParameters + ")";
    }

    private void translateMethodDefination(ObjCParser.Method_typeContext method_typeContext, ObjCParser.Method_selectorContext method_selectorContext,
                                           String sourceText, int startMethodBody, String methodPrefix, boolean shouldAddClassname) {

        String sourceMethodTypeString = tokens.getText(method_typeContext.getSourceInterval());
        String methodTypeString = tokens.getText(method_typeContext.getSourceInterval());
        int startMethodType = outputBuffer.indexOf(methodTypeString, startMethodBody);
        int endMethodType = startMethodType + methodTypeString.length();

        String methodTypeSanitized = methodTypeString.replaceAll("\\(", EMPTY_STRING).replaceAll("\\)", " ");
        String finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix = translateMethodNameParameterTypeAndNames(method_selectorContext);
        methodTypeString = (shouldAddClassname? "" : methodPrefix) + translateIdentifier(finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix.startsWith(INIT)? Types.BOOL + " " : methodTypeSanitized);
        writeToOutputBuffer(startMethodBody, endMethodType, sourceMethodTypeString, methodTypeString, false);

        String classNamePrefix = shouldAddClassname? className + "::" : EMPTY_STRING;
        String finalMethodNameParameterTypeAndNames = classNamePrefix + finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix;
        String finalMethodTypeNameParameterTypeAndNames = (shouldAddClassname? methodPrefix : "") + methodTypeString + finalMethodNameParameterTypeAndNamesWithoutClassNamePrefix;

        String methodSelectorString = tokens.getText(method_selectorContext.getSourceInterval());
        int startMethodSelector = outputBuffer.indexOf(methodSelectorString, startMethodBody);
        int endMethodSelector = startMethodSelector + methodSelectorString.length();
        if(methodSignatures.contains(finalMethodTypeNameParameterTypeAndNames)) {
            methodSignatures.remove(finalMethodTypeNameParameterTypeAndNames);
        } else {
            methodSignatures.add(finalMethodTypeNameParameterTypeAndNames);
        }
        writeToOutputBuffer(startMethodSelector, endMethodSelector, sourceText, finalMethodNameParameterTypeAndNames, true);
    }

    private String translateMethodNameParameterTypeAndNames(ObjCParser.Method_selectorContext methodSelectorContext) {
        String finalMethodName = EMPTY_STRING;
        String finalParameters = EMPTY_STRING;

        //If this is just a method with no keyword argument we need to get just the selector and use it as the method name.
        if(methodSelectorContext.keyword_declarator().size() == 0) {
            finalMethodName = tokens.getText(methodSelectorContext.selector().getSourceInterval());
        }

        //Well this method is made up of multiple parts we will need to upper case the subsequent parts to follow Camel case convention
        //and then pass the parameters as method call.
        for (int i = 0; i< methodSelectorContext.keyword_declarator().size(); i++) {
            ObjCParser.Keyword_declaratorContext keyword_declaratorContext = methodSelectorContext.keyword_declarator().get(i);
            ObjCParser.SelectorContext selector = keyword_declaratorContext.selector();
            String sourceMethod = selector == null? EMPTY_STRING : tokens.getText(selector.getSourceInterval());
            String sourceType = tokens.getText(keyword_declaratorContext.method_type(0).getSourceInterval());
            String sourceParameter = tokens.getText(keyword_declaratorContext.IDENTIFIER().getSourceInterval());
            if(i > 0) {
                sourceMethod = toUpperFirstLetter(sourceMethod);
                finalParameters += ", ";
            }
            finalMethodName += sourceMethod;
            finalParameters += translateIdentifier(sourceType.replaceAll("\\(", EMPTY_STRING).replaceAll("\\)", EMPTY_STRING)) + " " + sourceParameter;
        }

        return finalMethodName + "(" + finalParameters + ")" + (isProtocol? PURE_VIRTUAL_METHOD_POSTFIX : EMPTY_STRING);
    }

    private String translateKeywords(String source) {
        //Final set of translations to get rid of the imports and supers and self and nil.
        for (final String key : Collections.list(sourceVsDestinationText.keys())) {
            if(sourceVsDestinationText.get(key).contains(key))
                continue;
            while(source.contains(key)) {
                source = source.replace(key, sourceVsDestinationText.get(key));
            }
        }

        //KEYWORDS_VS_TRANSLATIONS.remove(Keywords.SUPER);
        KEYWORDS_VS_TRANSLATIONS.put(Types.INSTANCETYPE, className + " " + ASTERISK);
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
        if(!isHeaderFile()) return EMPTY_STRING;
        return String.format("#ifndef __%s_H__\n#define __%s_H__\n\n", className.toUpperCase(), className.toUpperCase());
    }

    private String translateClassDeclaration(ObjCParser.Protocol_reference_listContext protocol_reference_listContext) {
        String classDeclaration = CLASS + translateIdentifier(className)
                + " :" + translateSuperClassDeclaration(superClassName);

        if(categoryClassName != null) {
            classDeclaration = CLASS + translateIdentifier(className)
                    + "_" + translateIdentifier(categoryClassName) + " : " + translateSuperClassDeclaration(className);
        }

        if(protocol_reference_listContext != null) {
            String separator = superClassName != null? "," : EMPTY_STRING;
            List<ObjCParser.Protocol_nameContext> protocol_nameContexts = protocol_reference_listContext.protocol_list().protocol_name();
            for (ObjCParser.Protocol_nameContext protocol_nameContext : protocol_nameContexts) {
                classDeclaration += separator + translateSuperClassDeclaration(translateIdentifier(protocol_nameContext.IDENTIFIER().getText()));
                separator = ",";
            }
        }

        return classDeclaration + " ";
    }

    private String translateSuperClassDeclaration(String cName) {
        if (cName == null) {
            return EMPTY_STRING;
        }

        return " public " + translateIdentifier(cName);
    }

    private String toUpperFirstLetter(final String sourceMethod) {
        return sourceMethod != null && sourceMethod.length() > 0? sourceMethod.substring(0, 1).toUpperCase() + sourceMethod.substring(1) : EMPTY_STRING;
    }

    private boolean isHeaderFile() {
        return fileName.endsWith(H);
    }

    private String translatePrivateMethodsDeclaration() {
        String finalPrivateMethodsDeclaration = "public: \n";
        for (final String methodDeclaration : methodSignatures) {
            finalPrivateMethodsDeclaration += "\n" + methodDeclaration + ";";
        }
        finalPrivateMethodsDeclaration += "\n";
        return finalPrivateMethodsDeclaration;
    }

    private void writeToOutputBuffer(int startIndex, int endIndex, String sourceText, String destinationText, boolean shouldWriteToDictionary) {
        outputBuffer.replace(startIndex, endIndex, destinationText);
        if(shouldWriteToDictionary)
            sourceVsDestinationText.put(sourceText, destinationText);
    }

    private VariableTypeNameInfo translateTypeVariableDeclarator(ObjCParser.Type_variable_declaratorContext context) {
        VariableTypeNameInfo variableTypeNameInfo = new VariableTypeNameInfo();
        variableTypeNameInfo.variableName = tokens.getText(context.declarator().direct_declarator());
        variableTypeNameInfo.variableType = translateIdentifier(tokens.getText(context.declaration_specifiers()));
        variableTypeNameInfo.pointer = context.declarator().pointer() != null ? tokens.getText(context.declarator().pointer()) : EMPTY_STRING;
        return variableTypeNameInfo;
    }
}
