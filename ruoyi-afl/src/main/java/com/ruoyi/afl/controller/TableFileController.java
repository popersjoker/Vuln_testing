package com.ruoyi.afl.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jws.Oneway;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.afl.domain.*;
import com.ruoyi.afl.service.ITableCompileService;
import com.ruoyi.afl.service.ITableInputFileService;
import com.ruoyi.afl.service.ITableRecordService;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.afl.service.ITableFileService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;


/**
 * 源码文件信息Controller
 *
 * @author zyz
 * @date 2023-10-23
 */
@RestController
@RequestMapping("/afl/file")
public class TableFileController extends BaseController {
    @Autowired
    private ITableFileService tableFileService;
    @Autowired
    private ITableInputFileService tableInputFileService;
    @Autowired
    private ITableRecordService tableRecordService;
    @Autowired
    private ITableCompileService tableCompileService;

    /**
     * 查询源码文件信息列表
     */
    @PreAuthorize("@ss.hasPermi('afl:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(TableFile tableFile) {
        startPage();
        List<TableFile> list = tableFileService.selectTableFileList(tableFile);
        return getDataTable(list);
    }

    /**
     * 导出源码文件信息列表
     */
    @PreAuthorize("@ss.hasPermi('afl:file:export')")
    @Log(title = "源码文件信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TableFile tableFile) {
        List<TableFile> list = tableFileService.selectTableFileList(tableFile);
        ExcelUtil<TableFile> util = new ExcelUtil<TableFile>(TableFile.class);
        util.exportExcel(response, list, "源码文件信息数据");
    }

    /**
     * 获取源码文件信息详细信息  这里我要用 用在测试用例的参数设定上
     * 2023-11-08 在此使用 根据yid查询filename
     * author ： zyz
     */
    @GetMapping(value = "/{yid}")
    public AjaxResult getInfo(@PathVariable("yid") String yid) {

        return success(tableFileService.selectTableFileByYid(Long.parseLong(yid)));
    }

    /**
     * 新增源码文件信息
     */
    @PreAuthorize("@ss.hasPermi('afl:file:add')")
    @Log(title = "源码文件信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TableFile tableFile) {
        return toAjax(tableFileService.insertTableFile(tableFile));
    }

    /**
     * 修改源码文件信息
     */
    @PreAuthorize("@ss.hasPermi('afl:file:edit')")
    @Log(title = "源码文件信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TableFile tableFile) {
        return toAjax(tableFileService.updateTableFile(tableFile));
    }

    /**
     * 删除源码文件信息
     */
    @Log(title = "源码文件信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{yids}")
    public AjaxResult remove(@PathVariable Long[] yids) {
        return toAjax(tableFileService.deleteTableFileByYids(yids));
    }


    //    文件上传功能  将源码文件上传到/home/zyz/Desktop下  2023.11.14 改正：上传到/mnt/hgfs/test/RuoYi-Vue-master下
    @PostMapping("/upload")
    public void uploadFiles(@RequestParam("sourceFile") MultipartFile sourceFile, @RequestParam(value = "inputFile", required = false) MultipartFile inputFile, @RequestParam("currentTime") String currentTime) throws ParseException {
        if (sourceFile.isEmpty()) {
            System.out.println("null");
        }
        // 要在这把所有获得的能插入数据库表中的信息字段全部获取到
        TableFile tableFile = new TableFile();
        TableInputFile tableInputFile = new TableInputFile();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sourceUploadDir = "/mnt/hgfs/test/RuoYi-Vue-master/Source-Directory/";  // 后续要存储至数据库表table_file中的upload_url字段
        String sourceFileName = sourceFile.getOriginalFilename(); // 后续要存储至数据库表table_file中的file_name字段
        File sourceDest = new File(sourceUploadDir + sourceFileName);
        try {
            sourceFile.transferTo(sourceDest);
            System.out.println("源码文件上传成功");
            tableFile.setFileName(sourceFileName);
            tableFile.setUploadTime(dateFormat.parse(currentTime));
            tableFile.setUploadUrl(sourceUploadDir);
            addFileInfo(tableFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("源码文件上传失败了");
        }
        if (inputFile != null && !inputFile.isEmpty()) {
            String inputFileUploadDirSolid = "/mnt/hgfs/test/RuoYi-Vue-master/Fuzz-in/";
            String randomZD = UUID.randomUUID().toString();
            String inputFileUploadDir = inputFileUploadDirSolid  + randomZD; // /mnt/hgfs/test/RuoYi-Vue-master/Fuzz-in/20231114huiih13
            File inputDirectory = new File(inputFileUploadDir);
            if (!inputDirectory.exists()) { // 这里确定要上传的文件路径 /home/zyz/Desktop/randomZD  改正/mnt/hgfs/test/RuoYi-Vue-master/Fuzz-in/下
                inputDirectory.mkdirs();
            } else {
                inputDirectory.delete();
                inputDirectory.mkdirs();
            }
            File inputUploadDest = new File(inputFileUploadDir + "/" + inputFile.getOriginalFilename());
            try {
                inputFile.transferTo(inputUploadDest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(tableFile.getYid());  // 看看是不是有值
            tableInputFile.setYid(tableFile.getYid());
            tableInputFile.setUploadInputUrl(inputFileUploadDir);
            tableInputFile.setInputFileName(inputFile.getOriginalFilename());
            this.addInputFileInfo(tableInputFile);
            System.out.println("成功添加输入文件信息");
        }
    }

    // 单纯地上传输入文件的代码，与上传源码文件的功能类似，但是传递参数只需要传一个
    // 接收传递来的参数，包括上传的输入文件 和 对应行的yid，因为要保存到数据库中 四个字段 uid，input_file_name，upload_input_url，yid
    @PostMapping("/upload-input")
    public void InputFileUpload(@RequestParam("inputFile") MultipartFile inputFile, @RequestParam("yid") String yid) {
        if (inputFile.isEmpty()) {
            System.out.println("null");
        }
        TableInputFile tableInputFile = new TableInputFile();
        boolean b = tableInputFileService.selectTableInputFileByYid(Long.parseLong(yid));
        // 如果这个yid在输入记录表中没有记录，就插入一条数据，否则修改源数据
        try {
            String inputFileUploadDirSolid = "/mnt/hgfs/test/RuoYi-Vue-master/Fuzz-in/";  // 后续要存储至数据库表table_upload中的upload_input_url字段
            String randomZD = UUID.randomUUID().toString();
            String inputFileUploadDir = inputFileUploadDirSolid + randomZD;
            File inputDirectory = new File(inputFileUploadDir);
            if (!inputDirectory.exists()) {
                inputDirectory.mkdirs();
            } else {
                inputDirectory.delete();
                inputDirectory.mkdirs();
            }
            File inputUploadDest = new File(inputFileUploadDir + "/" + inputFile.getOriginalFilename());
            inputFile.transferTo(inputUploadDest);
            tableInputFile.setYid(Long.parseLong(yid));
            tableInputFile.setUploadInputUrl(inputFileUploadDir);
            tableInputFile.setInputFileName(inputFile.getOriginalFilename());
            if (b) { // 如果数据表中存在yid的记录
                TableInputFile tableInputFile1 = tableInputFileService.selectByYid(Long.parseLong(yid));  // 查询此记录的数据
                if (tableInputFile1.getUploadInputUrl() != null) {    // 如果记录中有具体的输入文件路径
                    System.out.println(tableInputFile1.getUploadInputUrl());
                    File onceDest = new File(tableInputFile1.getUploadInputUrl()); // 文件对象，用于一会判断表中url的目录是否存在
                    System.out.println(onceDest);
                    if (onceDest.exists()) {      // 如果该路径下的文件是存在的，就要把该文件进行删除
                        try {         // 执行删除操作 sudo rm -rf url
                            ProcessBuilder processBuilder = new ProcessBuilder("sudo", "rm", "-rf", tableInputFile1.getUploadInputUrl());
                            processBuilder.redirectErrorStream(true);
                            Process process = processBuilder.start();
                            process.waitFor();
                            int exitCode = process.exitValue();
                            System.out.println(exitCode);  // 1
                            if (exitCode == 0) {
                                System.out.println("删除成功");
                            } else {
                                System.out.println("删除失败");
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }// 输入文件目录删除完成
                tableInputFileService.updateTableInputFileByYid(tableInputFile); // 这个位置开始更新数据表中数据

            } else {
                tableInputFileService.insertTableInputFile(tableInputFile); // 说明数据库中无此条记录，直接进行插入
            }
            System.out.println("输入文件上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("输入文件上传失败！");
        }
    }

    // 修改上传文件
    @PostMapping("/updateFiles")
    public void updateFiles(@RequestParam("yid") String yid, @RequestParam("sourceFile") MultipartFile sourceFile, @RequestParam("currentTime") String currentTime) throws ParseException {
        TableFile tableFile = new TableFile();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sourceUploadDir = "/mnt/hgfs/test/RuoYi-Vue-master/Source-Directory/";  // 后续要存储至数据库表table_file中的upload_url字段
        String sourceFileName = sourceFile.getOriginalFilename(); // 后续要存储至数据库表table_file中的file_name字段
        File sourceDest = new File(sourceUploadDir + sourceFileName);
        TableCompile tableCompile = new TableCompile();
        try {
            tableFile = tableFileService.selectTableFileByYid(Long.parseLong(yid));
            tableFile.setYid(Long.parseLong(yid));
            String yuanFile = tableFile.getUploadUrl() + tableFile.getFileName();
            String[] command = {"sudo", "rm", yuanFile};
            // 删除原来的源码文件
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Command executed successfully
                System.out.println("Command executed successfully");
            } else {
                // Command execution failed
                System.out.println("Command execution failed with exit code: " + exitCode);
            }
            sourceFile.transferTo(sourceDest);
            System.out.println("源码文件修改成功");
            // 更新数据库记录
            tableFile.setFileName(sourceFileName);
            tableFile.setUploadTime(dateFormat.parse(currentTime));
            tableFile.setUploadUrl(sourceUploadDir);
            tableFileService.updateTableFile(tableFile);
            System.out.println("数据库记录已更新");
            //  这里写操作编译文件表，如果yid有记录，说明已经编译过，要把这个编译文件的路径找到删除，并更新编译文件表
            tableCompile = tableCompileService.selectLatestTableCompileByYid(Long.parseLong(yid));
            if (tableCompile != null) {
                String s = tableCompile.getCompiledUrl() + "/" + tableCompile.getCompiledName(); //编译文件路径，下一步该删除了
                Path path = Paths.get(s);
                Path path1 = Paths.get(tableCompile.getCompiledUrl());
                Files.delete(path);
                Files.delete(path1);
                System.out.println("编译文件删除成功");
                tableCompile.setCompiledUrl("");
                tableCompile.setCompiledName("");
                tableCompile.setYid(tableFile.getYid());
                tableCompileService.updateTableCompileByYid(tableCompile);
                System.out.println("编译记录更新成功！");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("源码文件修改失败了");
        }

    }

    public void addFileInfo(TableFile tableFile) {
        tableFileService.insertTableFile(tableFile);
    }

    public void addInputFileInfo(TableInputFile tableInputFile) { // 有错误
        tableInputFileService.insertTableInputFile(tableInputFile);
    }


    /**
     * info:查询源码文件信息表中的三列，yid，file_name，upload_time
     * by zyz
     * date : 2023-10-26
     */
    @GetMapping("/list123")
    public TableDataInfo list123(TableFile tableFile) {
        startPage();
        List<TableFile> list123 = tableFileService.select3TableFile(tableFile);
        return getDataTable(list123);
    }

    @PostMapping("/getYidByFileName") // 只输入文本才会触发，不能输入时间选择
    public TableDataInfo getYidByFileName(@RequestBody Map<String, Object> requestBody) {
        System.out.println("dingzhendingzhen");
        System.out.println(requestBody);

        TableFile tableFile = new TableFile();
        TableRecord tableRecord = new TableRecord();

        // 提取参数
        String fileName = (String) requestBody.get("fileName");
        Map<String, Object> params = (Map<String, Object>) requestBody.get("params");
        String beginTime = (String) params.get("beginTime");
        String endTime = (String) params.get("endTime");

        // 设置文件名到 tableFile 对象
        tableFile.setFileName(fileName);

        // 初始化列表
        List<RecordWithFileName> recordWithFileNames = new ArrayList<>();
        List<TableRecord> tableRecords;

        // 检查是否提供了开始和结束时间
        if (beginTime != null && endTime != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date o1 = dateFormat.parse(beginTime);
                Date o2 = dateFormat.parse(endTime);
                Date o3 = DateUtils.addDays(o2, 1);

                // 根据文件名查找对应的文件列表
                List<TableFile> tableFiles = tableFileService.selectTableFileList(tableFile);

                for (TableFile tableFile1 : tableFiles) {
                    Long yid = tableFile1.getYid();
                    String file = tableFile1.getFileName();
                    tableRecord.setYid(yid);

                    if (file.isEmpty()) {
                        tableRecords = tableRecordService.selectTableRecordList(tableRecord);
                    } else {
                        tableRecords = tableRecordService.selectTableRecordByYid(tableRecord.getYid());
                    }

                    for (TableRecord tableRecord1 : tableRecords) {
                        Date testTime = tableRecord1.getTestTime();
                        if (!testTime.before(o1) && !testTime.after(o3)) {
                            RecordWithFileName record = new RecordWithFileName();
                            record.setTableRecord(tableRecord1);
                            record.setFileName(file);
                            recordWithFileNames.add(record);
                        }
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (fileName != null && !fileName.isEmpty()) {
            // 只提供文件名，根据文件名查找记录
            tableRecords = tableRecordService.selectTableRecordList(tableRecord);
            for (TableRecord tableRecord1 : tableRecords) {
                RecordWithFileName record = new RecordWithFileName();
                record.setTableRecord(tableRecord1);
                record.setFileName(fileName);
                recordWithFileNames.add(record);
            }
        }
        System.out.println("wbg vs blg");
        System.out.println(recordWithFileNames);
        return getDataTable(recordWithFileNames);
    }

    @PostMapping("/getFileNameAndTwoTime")  // 只有输入时间选择才会触发 文本可以输入
    public TableDataInfo getFileNameAndTwoTime(@RequestBody Map<String, Object> requestBody) {
        System.out.println("dingzhendingzhen");
        System.out.println(requestBody);// 关键就是 beginTime endTime fileName
//        {pageNum=1, pageSize=10, testTime=null, fileName=, fid=null, yid=null, params={beginTime=2023-11-07, endTime=2023-11-09}}
        TableFile tableFile = new TableFile();
        TableRecord tableRecord = new TableRecord();
        tableFile.setFileName(requestBody.get("fileName").toString());

        List<TableFile> tableFiles = tableFileService.selectTableFileList(tableFile); // 根据文件名查到的行数据会以列表形式得到
        List<RecordWithFileName> recordWithFileNames = new ArrayList<>();
        List<TableRecord> tableRecords;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> params = (Map<String, Object>) requestBody.get("params");
        System.out.println(params.get("beginTime"));
        System.out.println(params.get("endTime"));
        try {  // 时间可能是空的，传过来的如果是空时间，直接报空指针异常，这里要判断一下，如果时间为空，就只根据
            Date o1 = dateFormat.parse((String) params.get("beginTime"));
            Date o2 = dateFormat.parse((String) params.get("endTime"));
            Date o3 = DateUtils.addDays(o2, 1);

            for (TableFile tableFile1 : tableFiles) { // 遍历每行
                Long yid = tableFile1.getYid();  // 取出yid
                String fileName = tableFile1.getFileName();
                tableRecord.setYid(yid);
                System.out.println("tabbo");
                if (fileName.isEmpty()) {
                    tableRecords = tableRecordService.selectTableRecordList(tableRecord); // 查到所有对应yid的行数据
                } else {
                    tableRecords = tableRecordService.selectTableRecordByYid(tableRecord.getYid());
                } // 查到所有对应yid的行数据
                for (TableRecord tableRecord1 : tableRecords) { // 判断该行的testTime是否在开始结束时间段内
                    Date testTime = tableRecord1.getTestTime();
                    if (!testTime.before(o1) && !testTime.after(o3)) {
                        RecordWithFileName record = new RecordWithFileName();
                        record.setTableRecord(tableRecord1);
                        record.setFileName(fileName);
                        recordWithFileNames.add(record);
                    }
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("wbg vs blg");
        System.out.println(recordWithFileNames);
        return getDataTable(recordWithFileNames);
    }
}

class RecordWithFileName {
    private TableRecord tableRecord;
    private String fileName;

    public TableRecord getTableRecord() {
        return tableRecord;
    }

    public void setTableRecord(TableRecord tableRecord) {
        this.tableRecord = tableRecord;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
