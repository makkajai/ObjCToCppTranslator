package com.makkajai;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by administrator on 26/12/15.
 */
public class RegExpTest {

    /**
     * Main Method
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String text = "[NSString stringWithFormat:@\"Enum with name: %@ not found, eheck enum in the json file\", name]";
//        String text = "[[@{\n" +
//                "        @\"kIgnoreGameEvent\": @(kIgnoreGameEvent),\n" +
//                "        @\"kOnGameBoardBegin\": @(kOnGameBoardBegin),\n" +
//                "        @\"kOnGameEnd\": @(kOnGameEnd),\n" +
//                "        @\"kOnLevelBegin\": @(kOnLevelBegin),\n" +
//                "        @\"kOnLevelEnd\": @(kOnLevelEnd),\n" +
//                "        @\"kScheduleUpdate\": @(kScheduleUpdate),\n" +
//                "        @\"kPositionChanged\": @(kPositionChanged),\n" +
//                "        @\"kPromptVisible\": @(kPromptVisible),\n" +
//                "        @\"kTouchBegan\": @(kTouchBegan),\n" +
//                "        @\"kTouchEnded\": @(kTouchEnded),\n" +
//                "        @\"kCollectConsumable\": @(kCollectConsumable),\n" +
//                "        @\"kCollectIncorrectConsumable\": @(kCollectIncorrectConsumable),\n" +
//                "        @\"kFireShot\": @(kFireShot),\n" +
//                "        @\"kCandyHitEnemy\": @(kCandyHitEnemy),\n" +
//                "        @\"kGameInitialized\": @(kGameInitialized),\n" +
//                "        @\"kGameStarted\": @(kGameStarted),\n" +
//                "        @\"kStopUpdatingViewPort\": @(kStopUpdatingViewPort),\n" +
//                "        @\"kPowerUpNotAdded\": @(kPowerUpNotAdded),\n" +
//                "        @\"kPowerUpAdded\": @(kPowerUpAdded),\n" +
//                "        @\"kPowerUpRemoved\": @(kPowerUpRemoved),\n" +
//                "        @\"kPowerUpAcquired\": @(kPowerUpAcquired),\n" +
//                "        @\"kPowerUpConsumed\": @(kPowerUpConsumed),\n" +
//                "        @\"kBeforeShotFired\": @(kBeforeShotFired),\n" +
//                "        @\"kAfterShotFired\": @(kAfterShotFired),\n" +
//                "        @\"kHighlightRewind\": @(kHighlightRewind),\n" +
//                "        @\"kShowHelpingHand\": @(kShowHelpingHand),\n" +
//                "    } another] mutableCopy]";
//        Pattern compile = Pattern.compile("(\\[(?s:.+)\\])((?s:.+)\\])");

        ObjCLexer lexer = new ObjCLexer(new ANTLRInputStream(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ObjCParser parser = new ObjCParser(tokens);
        ParseTree tree = parser.translation_unit(); // see the grammar ->
        ObjCToCppTranslator visitor = new ObjCToCppTranslator(); // extends JavaBaseVisitor<Void>
        visitor.visit(tree);

        Pattern compile = Pattern.compile("(\\[(?s:.*))\\W+(((?s:.+):?(?s:.*))+\\])");
        Matcher matcher = compile.matcher(text);
        boolean matches = matcher.matches();
        if(!matches) {
            System.out.println("No Match found!");
            return;
        }
        for (int i =0; i<=matcher.groupCount(); i++) {
            System.out.println("Matcher: " + matcher.group(i));
        }
    }
}
