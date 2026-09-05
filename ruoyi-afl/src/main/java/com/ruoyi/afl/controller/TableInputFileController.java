package com.ruoyi.afl.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

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
import com.ruoyi.afl.domain.TableInputFile;
import com.ruoyi.afl.service.ITableInputFileService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 输入文件上传记录Controller
 *
 * @author zyz
 * @date 2023-10-26
 */
@RestController
@RequestMapping("/afl/uploadInput")
public class TableInputFileController extends BaseController {
    @Autowired
    private ITableInputFileService tableInputFileService;

    /**
     * 查询输入文件上传记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:list')")
    @GetMapping("/list")
    public TableDataInfo list(TableInputFile tableInputFile) {
        startPage();
        List<TableInputFile> list = tableInputFileService.selectTableInputFileList(tableInputFile);
        return getDataTable(list);
    }

    /**
     * 导出输入文件上传记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:export')")
    @Log(title = "输入文件上传记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TableInputFile tableInputFile) {
        List<TableInputFile> list = tableInputFileService.selectTableInputFileList(tableInputFile);
        ExcelUtil<TableInputFile> util = new ExcelUtil<TableInputFile>(TableInputFile.class);
        util.exportExcel(response, list, "输入文件上传记录数据");
    }

    /**
     * 获取输入文件上传记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:query')")
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") Long uid) {
        return success(tableInputFileService.selectTableInputFileByUid(uid));
    }

    /**
     * @param yid
     * @return 根据yid查询表中数据
     */
    @GetMapping("/getInputInfo/{yid}")
    public AjaxResult getInputInfo(@PathVariable("yid") String yid) {
        TableInputFile tableInputFile = tableInputFileService.selectByYid(Long.parseLong(yid));
        Map<String, Object> responseData = new HashMap<>();
        if (tableInputFile == null) {
            return AjaxResult.error("尚未上传输入文件，请先返回至源代码处理页面上传输入文件！");
        } else {
            responseData.put("name122", tableInputFile.getInputFileName());
            responseData.put("url11", tableInputFile.getUploadInputUrl());
            return success(responseData);
        }
    }

    /**
     * 新增输入文件上传记录
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:add')")
    @Log(title = "输入文件上传记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TableInputFile tableInputFile) {
        return toAjax(tableInputFileService.insertTableInputFile(tableInputFile));
    }

    /**
     * 修改输入文件上传记录
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:edit')")
    @Log(title = "输入文件上传记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TableInputFile tableInputFile) {
        return toAjax(tableInputFileService.updateTableInputFile(tableInputFile));
    }

    /**
     * 删除输入文件上传记录
     */
    @PreAuthorize("@ss.hasPermi('afl:uploadInput:remove')")
    @Log(title = "输入文件上传记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{uids}")
    public AjaxResult remove(@PathVariable Long[] uids) {
        return toAjax(tableInputFileService.deleteTableInputFileByUids(uids));
    }

    @GetMapping("/getInputFile/{yid}")
    public AjaxResult getInputFile(@PathVariable("yid") String yid) {
        TableInputFile tableInputFile = tableInputFileService.selectByYid(Long.parseLong(yid));
        String FileAndUrl = tableInputFile.getUploadInputUrl() + "/" + tableInputFile.getInputFileName();
        String[] command = {"cat", FileAndUrl};
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            StringBuilder errorOutput = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorOutput.append(errorLine).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("查询输入文件内容成功！");
                return AjaxResult.success(output.toString());
            } else {
                System.out.println("查询输入文件失败了");
                return AjaxResult.error(errorOutput.toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return AjaxResult.error();
        }
    }

}
