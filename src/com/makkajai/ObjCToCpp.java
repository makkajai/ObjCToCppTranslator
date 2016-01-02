package com.makkajai;

import java.io.IOException;

import static com.makkajai.Constants.H;
import static com.makkajai.Constants.M;

/**
 * Created by administrator on 02/01/16.
 */
public class ObjCToCpp {

    /**
     * Main Method
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String fileName =
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/PlayStrategy"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Characters/Character"
                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/ParentScene"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/BaseSkillView"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase"
                ;
        //The instance of the translator.
        ObjCToCppTranslator visitor = new ObjCToCppTranslator();
        visitor.translateFile(fileName + H , false);
        visitor.translateFile(fileName + M, true);
        visitor.translateFile(fileName + H, true);
    }
}
