package com.ruoyi.afl.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;


import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.afl.domain.TableRecord;
import com.ruoyi.afl.service.ITableRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 测试记录Controller
 *
 * @author zyz
 * @date 2023-11-05
 */
@RestController
@RequestMapping("/afl/record")
public class TableRecordController extends BaseController {
    @Autowired
    private ITableRecordService tableRecordService;

    /**
     * 查询测试记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(TableRecord tableRecord) {
        startPage();
        List<TableRecord> list = tableRecordService.selectTableRecordList(tableRecord);
        return getDataTable(list);
    }

    /**
     * 导出测试记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:record:export')")
    @Log(title = "测试记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TableRecord tableRecord) {
        List<TableRecord> list = tableRecordService.selectTableRecordList(tableRecord);
        ExcelUtil<TableRecord> util = new ExcelUtil<TableRecord>(TableRecord.class);
        util.exportExcel(response, list, "测试记录数据");
    }

    /**
     * 获取测试记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('afl:record:query')")
    @GetMapping(value = "/{fid}")
    public AjaxResult getInfo(@PathVariable("fid") Long fid) {
        return success(tableRecordService.selectTableRecordByFid(fid));
    }

    /**
     * 新增测试记录
     */
    @PreAuthorize("@ss.hasPermi('afl:record:add')")
    @Log(title = "测试记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TableRecord tableRecord) {
        return toAjax(tableRecordService.insertTableRecord(tableRecord));
    }

    /**
     * 修改测试记录
     */
    @PreAuthorize("@ss.hasPermi('afl:record:edit')")
    @Log(title = "测试记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TableRecord tableRecord) {
        return toAjax(tableRecordService.updateTableRecord(tableRecord));
    }

    /**
     * 删除测试记录
     */
    @Log(title = "测试记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fids}")
    public AjaxResult remove(@PathVariable Long[] fids) {
        System.out.println(Arrays.toString(fids));
        return toAjax(tableRecordService.deleteTableRecordByFids(fids));
    }

    @GetMapping("/recordList")
    public TableDataInfo getRecordList(TableRecord tableRecord) {
        startPage();
        List<TableRecord> list123 = tableRecordService.selectTableRecordList(tableRecord);
//        List<TableFile> list123 = tableFileService.select3TableFile(tableFile);
        System.out.println("xieyao");
        System.out.println(list123);
        return getDataTable(list123);
    }

    // 今天写一下这里，查询fuzz-out的路径别忘了拼接上 /crashes/*  这条命令与fuzz_out_url拼接
    @GetMapping("/getRecordCrashes/{fid}")
    public AjaxResult getRecordCrashes(@PathVariable("fid") Long fid) {
        TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
        String fuzzOutUrl = tableRecord.getFuzzOutUrl();
        String crashesPath = fuzzOutUrl + "/" + "crashes";
        List<String> fileList = getFilesInDiectory(crashesPath);
//        System.out.println("作曲家！");
        System.out.println(fileList);
        return success(fileList);
    }

    @GetMapping("/getRecordHangs/{fid}")
    public AjaxResult getRecordHangs(@PathVariable("fid") Long fid) {
        TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
        String fuzzOutUrl = tableRecord.getFuzzOutUrl();
        String hangsPath = fuzzOutUrl + "/" + "hangs";
        List<String> fileList = getFilesInDiectory(hangsPath);
        System.out.println("otto!");
        System.out.println(fileList);
        return success(fileList);
    }

    @GetMapping("/getRecordQueue/{fid}")
    public AjaxResult getRecordQueue(@PathVariable("fid") Long fid) {
        TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
        String fuzzOutUrl = tableRecord.getFuzzOutUrl();
        String queuePath = fuzzOutUrl + "/" + "queue";
        List<String> fileList = getFilesInDiectory(queuePath);
//        System.out.println("caoyu!");
        System.out.println(fileList);
        return success(fileList);
    }

    @GetMapping("/getRecordFuzzerStats/{fid}")
    public AjaxResult getRecordFuzzerStats(@PathVariable("fid") Long fid) {
        TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
        String fuzzOutUrl = tableRecord.getFuzzOutUrl();
        String fuzzerstatsPath = fuzzOutUrl + "/" + "fuzzer_stats";
        String fileContent;
        try {
            fileContent = readFuzzerStatsContent(fuzzerstatsPath);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理读取文件失败的情况，例如返回一个错误响应
            return error("Failed to read file content");
        }
        return success(fileContent);
    }

    private List<String> getFilesInDiectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();
        try {
            Path directory = Paths.get(directoryPath);
            Files.list(directory).forEach(file -> {
                fileList.add(file.getFileName().toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    @PostMapping("/getCrashesFile")
    public AjaxResult getCrashesFile(@RequestBody Object requestData) {
//        System.out.println("臭要饭的别挡我财路");
        try {
            Map<String, Object> params = (Map<String, Object>) requestData;
            Object globalFidObj = params.get("Fid");
            Object crashesNameObj = params.get("CrashesName");

            String globalFid = (globalFidObj != null) ? globalFidObj.toString() : null;
            String crashesName = (crashesNameObj != null) ? crashesNameObj.toString() : null;
            System.out.println(globalFid + ";';'" + crashesName);
            Long fid = null;
            if (globalFid != null) {
                fid = Long.parseLong(globalFid);
            }
            TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
            String s = tableRecord.getFuzzOutUrl() + "/crashes/" + crashesName;
            System.out.println(s);
            String hexContent = readHexContent(s);
            // 新增一个判断，README.txt没必要用十六进制显示 if - else 2023/11/19 10:51
            if (crashesName.equals("README.txt")) {
                String nohexRead = readFuzzerStatsContent(s);
                return AjaxResult.success(nohexRead);
            } else {
//                System.out.println("每天看四小时书");
//                System.out.println(tableRecord.getFuzzOutUrl() + "文化" + s + "显和" + hexContent);
                return AjaxResult.success(hexContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("失败", e.getMessage());
        }
    }

    @PostMapping("/getHangsFile")
    public AjaxResult getHangsFile(@RequestBody Object requestData) {
//        System.out.println("臭要饭的别挡我财路");
        try {
            Map<String, Object> params = (Map<String, Object>) requestData;
            Object globalFidObj = params.get("Fid");
            Object hangsNameObj = params.get("HangsName");

            String globalFid = (globalFidObj != null) ? globalFidObj.toString() : null;
            String hangsName = (hangsNameObj != null) ? hangsNameObj.toString() : null;
            Long fid = null;
            if (globalFid != null) {
                fid = Long.parseLong(globalFid);
            }
            TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
            String s = tableRecord.getFuzzOutUrl() + "/hangs/" + hangsName;
            String hexContent = readHexContent(s);
            if (hangsName.equals("README.txt")) {
                String nohexRead = readFuzzerStatsContent(s);
                return AjaxResult.success(nohexRead);
            } else {
                return AjaxResult.success(hexContent);
            }
        } catch (Exception e) {
            return AjaxResult.error("失败", e.getMessage());
        }
    }

    @PostMapping("/getQueueFile")
    public AjaxResult getQueueFile(@RequestBody Object requestData) {
//        System.out.println("臭要饭的别挡我财路");
        try {
            Map<String, Object> params = (Map<String, Object>) requestData;
            Object globalFidObj = params.get("Fid");
            Object queueNameObj = params.get("QueueName");

            String globalFid = (globalFidObj != null) ? globalFidObj.toString() : null;
            String queueName = (queueNameObj != null) ? queueNameObj.toString() : null;
            Long fid = null;
            if (globalFid != null) {
                fid = Long.parseLong(globalFid);
            }
            TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
            String s = tableRecord.getFuzzOutUrl() + "/queue/" + queueName;
            String hexContent = readHexContent(s);
            if (queueName.equals("README.txt")) {
                String nohexRead = readFuzzerStatsContent(s);
                return AjaxResult.success(nohexRead);
            } else {
                return AjaxResult.success(hexContent);
            }
        } catch (Exception e) {
            return AjaxResult.error("失败啊啊", e.getMessage());
        }
    }

    @GetMapping("/getPaintHtml/{fid}")
    public AjaxResult getPaintHtml(@PathVariable("fid") Long fid) {
        TableRecord tableRecord = tableRecordService.selectTableRecordByFid(fid);
        String fuzzOutUrl = tableRecord.getFuzzOutUrl();
        try {
            String outputDir = "/mnt/hgfs/test/RuoYi-Vue-master" + File.separator + "Out-HTML";
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            } else {
                outputDirFile.delete();
                outputDirFile.mkdirs();
            }
            String command = "afl-plot";
            ProcessBuilder processBuilder = new ProcessBuilder(command, fuzzOutUrl, outputDir);
            System.out.println(command+fuzzOutUrl+ outputDir);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 命令执行成功
                System.out.println("有原图了！");
//                String indexHtmlPath = outputDir + File.separator + "index.html";
                String fileUrl = "http://localhost:8080/ruoyi/Out-HTML/index.html";
                return AjaxResult.success(fileUrl);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return error();
        }
        return AjaxResult.success();
    }


    private String readHexContent(String filePath) throws IOException {
        System.out.println(">>????");
        System.out.println(filePath);
        try (FileInputStream fis = new FileInputStream(filePath)) {
            StringBuilder hexContent = new StringBuilder();
            int data;
            while ((data = fis.read()) != -1) {
                hexContent.append(String.format("%02X", data));
            }
            return hexContent.toString();
        }
    }

    private String readFuzzerStatsContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes);
    }
}
