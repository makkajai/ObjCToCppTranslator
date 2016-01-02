package com.makkajai;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.makkajai.Constants.*;

/**
 * ObjCToCpp - The main class that will drive the entire conversion process.
 * (c) 2015 Makkajai
 * @author Deep Shah
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
public class ObjCToCpp {

    /**
     * Main Method
     *
     * @param args - First argument is the input directory to scan and second is the output directory to write files to.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        if(args.length != 2) {
            System.out.println("Invalid Arguments!");
            System.out.println("Usage: java com.makkajai.ObjCToCpp \"<directory to scan for .h and .m files>\" \"<directory to write .h and .cpp files>\"");
            return;
        }

        String inputDirectory = args[0];
        String outputDirectory = args[1];
        //Getting all the files from the input directory.
        final List<File> files = new ArrayList<File>(FileUtils.listFiles(
                new File(inputDirectory),
                new RegexFileFilter(FILE_NAME_WITH_H_OR_M),
                DirectoryFileFilter.DIRECTORY
        ));

//        String fileName =
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiEnum"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Utils/MakkajaiUtil"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Home"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/PlayStrategy"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Characters/Character"
//                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/Activities/gnumchmenu/GnumchScene"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/ParentScene"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/BaseSkillView"
////                "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/YDLayerBase"
//                ;
        //The instance of the translator.
        ObjCToCppTranslator visitor = new ObjCToCppTranslator();

        for (int i = 0; i < files.size();) {
            File currentFile = files.get(i);
            String filePathRelativeToInput = currentFile.getAbsolutePath().replace(inputDirectory, "");
            Date startTime = new Date();
            try {
                final TranslateFileInput translateFileInput = new TranslateFileInput(inputDirectory, outputDirectory, filePathRelativeToInput, false);
                if(nextFileIsM(currentFile, files, i)) {
                    translateFileInput.dryRun = true;
                    visitor.translateFile(translateFileInput);
                    Date stopTime = new Date();
                    System.out.println("Dry run File: " + filePathRelativeToInput + " Time Taken: " + getDelta(startTime, stopTime));

                    Date startTime1 = new Date();
                    translateFileInput.filePathRelativeToInput = filePathRelativeToInput.replace(H, M);
                    translateFileInput.dryRun = false;
                    visitor.translateFile(translateFileInput);
                    stopTime = new Date();
                    System.out.println("Processed File: " + filePathRelativeToInput + " Time Taken: " + getDelta(startTime1, stopTime));

                    Date startTime2 = new Date();
                    translateFileInput.filePathRelativeToInput = filePathRelativeToInput;
                    translateFileInput.dryRun = false;
                    visitor.translateFile(translateFileInput);
                    stopTime = new Date();
                    System.out.println("Processed File: " + filePathRelativeToInput + " Time Taken: " + getDelta(startTime2, stopTime));
                    i+=2;
                    continue;
                }
                visitor.translateFile(translateFileInput);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("###########################Continuing with the next set of files##########################");
            } finally {
                Date stopTime = new Date();
                System.out.println("Processed File(s): " + filePathRelativeToInput.replaceAll(H_OR_M, "") + " Time Taken: " + getDelta(startTime, stopTime));
            }
        }
    }

    private static double getDelta(Date startTime, Date stopTime) {
        return (stopTime.getTime() - startTime.getTime()) / 1000.0d;
    }

    private static boolean nextFileIsM(File currentFile, List<File> files, int i) {
        if(files.size() <= i + 1) return false;

        File nextFile = files.get(i + 1);
        return currentFile.getAbsolutePath().replaceAll(H_OR_M, "").equals(nextFile.getAbsolutePath().replaceAll(H_OR_M, ""));
    }
}
