package com.makkajai;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.makkajai.Constants.*;
import java.util.Collections;

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

        if(args.length < 2) {
            System.out.println("Invalid Arguments!");
            System.out.println("Usage: java com.makkajai.ObjCToCpp \"<directory to scan for .h and .m files>\" \"<directory to write .h and .cpp files>\"");
            return;
        }

        String inputDirectory = args[0];
        String outputDirectory = args[1];
   //     String inputDirectory = "/Users/administrator/playground/projarea/math-monsters-2/makkajai-number-muncher/makkajai-ios/Makkajai/Makkajai/scenes";
   //     String outputDirectory = "/Users/administrator/playground/projarea/monster-math-cross-platform/monster-math-2/Classes/Makkajai/scenes";

        List<String> exceptFiles = new ArrayList<String>();

        if(args.length == 3) {
            BufferedReader bufferedInputStream = new BufferedReader(new FileReader(args[2]));
            String exceptFile = null;
            while((exceptFile = bufferedInputStream.readLine()) != null) {
                if(exceptFile.equals("")) continue;
                exceptFiles.add(exceptFile);
            }
        }

        //Getting all the files from the input directory.
        final List<File> files = new ArrayList<File>(FileUtils.listFiles(
                new File(inputDirectory),
                new RegexFileFilter(FILE_NAME_WITH_H_OR_M),
                DirectoryFileFilter.DIRECTORY
        ));
        Collections.sort(files);
        for(File file: files){
            System.out.println(file.getName());
        }

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
            System.out.println("\n\nfile " + i + " " + currentFile.getName());
            String filePathRelativeToInput = currentFile.getAbsolutePath().replace(inputDirectory, "");
            Date startTime = new Date();
            try {
                final TranslateFileInput translateFileInput = new TranslateFileInput(inputDirectory, outputDirectory, filePathRelativeToInput, false);
                if(nextFileIsM(currentFile, files, i)) {
                    try {
                        if(isIgnoredFile(filePathRelativeToInput, exceptFiles)) continue;
                        translateFileInput.dryRun = true;
                        visitor.translateFile(translateFileInput);
                        Date stopTime = new Date();
                        System.out.println("Dry run File: " + translateFileInput.filePathRelativeToInput + " Time Taken: " + getDelta(startTime, stopTime));

                        Date startTime1 = new Date();
                        translateFileInput.filePathRelativeToInput = filePathRelativeToInput.replace(H, M);
                        translateFileInput.dryRun = false;
                        visitor.translateFile(translateFileInput);
                        stopTime = new Date();
                        System.out.println("Processed File: " + translateFileInput.filePathRelativeToInput + " Time Taken: " + getDelta(startTime1, stopTime));

                        Date startTime2 = new Date();
                        translateFileInput.filePathRelativeToInput = filePathRelativeToInput;
                        System.out.println(filePathRelativeToInput);                        
                        translateFileInput.dryRun = false;
                        visitor.translateFile(translateFileInput);
                        stopTime = new Date();
                        System.out.println("Processed File: " + translateFileInput.filePathRelativeToInput + " Time Taken: " + getDelta(startTime2, stopTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("###########################Error Processing: " + filePathRelativeToInput + ", Continuing with next set of tiles");
                    } finally {
                        System.out.println(currentFile.getName());
                        i+=2;
                    }
                    continue;
                }
                if(!isIgnoredFile(filePathRelativeToInput, exceptFiles))
                    visitor.translateFile(translateFileInput);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("###########################Error Processing: " + filePathRelativeToInput + ", Continuing with next set of tiles");
            } finally {
                Date stopTime = new Date();
               System.out.println("Processed File(s): " + filePathRelativeToInput.replaceAll(H_OR_M, "") + " Time Taken: " + getDelta(startTime, stopTime));
            }
        }
    }

    private static boolean isIgnoredFile(String filePathRelativeToInput, List<String> exceptFiles) {
        for (String exceptFile : exceptFiles) {
            if(filePathRelativeToInput.contains(exceptFile))
            { 
                System.out.println("exceptfile: " + exceptFile);
                return true;
            }
        }
        return false;
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
