package com.ruoyi.afl.domain;

/*
 * 功能：
 * 作者：zyz
 * 日期：2023/11/5 15:11
 */
public class NameList {
    private String FileName;
    private String CompileName;
    private String InputFileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getCompileName() {
        return CompileName;
    }

    public void setCompileName(String compileName) {
        CompileName = compileName;
    }

    public String getInputFileName() {
        return InputFileName;
    }

    public void setInputFileName(String inputFileName) {
        InputFileName = inputFileName;
    }

    @Override
    public String toString() {
        return "NameList{" +
                "FileName='" + FileName + '\'' +
                ", CompileName='" + CompileName + '\'' +
                ", InputFileName='" + InputFileName + '\'' +
                '}';
    }
}
