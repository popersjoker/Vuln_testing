package com.ruoyi.afl.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class AflCommand {
    private String uuid;
    private String result;
    private boolean flag;
    private boolean outflag;
    private String yid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testTime;


    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isOutflag() {
        return outflag;
    }

    public void setOutflag(boolean outflag) {
        this.outflag = outflag;
    }

    @Override
    public String toString() {
        return "AflCommand{" +
                "uuid='" + uuid + '\'' +
                ", result='" + result + '\'' +
                ", flag=" + flag +
                '}';
    }
}
