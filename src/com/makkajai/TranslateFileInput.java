package com.makkajai;

/**
 * TranslateFileInput - The input to the translator program.
 * (c) 2015 Makkajai
 * @author Deep Shah
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
public class TranslateFileInput {

    public String inputDirectory;

    public String outputDirectory;

    public String filePathRelativeToInput;

    public boolean dryRun;

    public TranslateFileInput(final String inputDirectory, final String outputDirectory, final String filePathRelativeToInput, final boolean dryRun) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.filePathRelativeToInput = filePathRelativeToInput;
        this.dryRun = dryRun;
    }


    public String getAbsoluteInputFilePath() {
        return inputDirectory + filePathRelativeToInput;
    }

    public String getAbsoluteOutputFilePath() {
        return outputDirectory + filePathRelativeToInput.replace(Constants.M, Constants.CPP);
    }
}
