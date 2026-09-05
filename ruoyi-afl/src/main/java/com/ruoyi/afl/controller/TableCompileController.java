package com.ruoyi.afl.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.afl.domain.TableFile;
import com.ruoyi.afl.service.ITableFileService;
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
import com.ruoyi.afl.domain.TableCompile;
import com.ruoyi.afl.service.ITableCompileService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 编译记录Controller
 *
 * @author zyz
 * @date 2023-10-30
 */
@RestController
@RequestMapping("/afl/compile")
public class TableCompileController extends BaseController {
    @Autowired
    private ITableCompileService tableCompileService;

    @Autowired
    private ITableFileService tableFileService;

    /**
     * 查询编译记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:list')")
    @GetMapping("/list")
    public TableDataInfo list(TableCompile tableCompile) {
        startPage();
        List<TableCompile> list = tableCompileService.selectTableCompileList(tableCompile);
        return getDataTable(list);
    }

    /**
     * 导出编译记录列表
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:export')")
    @Log(title = "编译记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TableCompile tableCompile) {
        List<TableCompile> list = tableCompileService.selectTableCompileList(tableCompile);
        ExcelUtil<TableCompile> util = new ExcelUtil<TableCompile>(TableCompile.class);
        util.exportExcel(response, list, "编译记录数据");
    }

    /**
     * 获取编译记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:query')")
    @GetMapping(value = "/{cid}")
    public AjaxResult getInfo(@PathVariable("cid") Long cid) {
        return success(tableCompileService.selectTableCompileByCid(cid));
    }

    /**
     * 新增编译记录
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:add')")
    @Log(title = "编译记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TableCompile tableCompile) {
        return toAjax(tableCompileService.insertTableCompile(tableCompile));
    }

    /**
     * 修改编译记录
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:edit')")
    @Log(title = "编译记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TableCompile tableCompile) {
        return toAjax(tableCompileService.updateTableCompile(tableCompile));
    }

    /**
     * 删除编译记录
     */
    @PreAuthorize("@ss.hasPermi('afl:compile:remove')")
    @Log(title = "编译记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{cids}")
    public AjaxResult remove(@PathVariable Long[] cids) {
        return toAjax(tableCompileService.deleteTableCompileByCids(cids));
    }

    @GetMapping("/getCompile/{yid}")
    public AjaxResult getCompile(@PathVariable("yid") String yid) {
        try {
            TableFile tableFile = tableFileService.selectTableFileByYid(Long.parseLong(yid));
            String fileName = tableFile.getFileName();
            String uploadUrl = tableFile.getUploadUrl();
            TableCompile compile = tableCompileService.selectLatestTableCompileByYid(Long.parseLong(yid));
            String compiledName = compile.getCompiledName();
            String compiledUrl = compile.getCompiledUrl();
            Map<String, String> responseData = new HashMap<>();
            responseData.put("fileName1", fileName);
            responseData.put("uploadUrl1", uploadUrl);
            responseData.put("compiledName1", compiledName);
            responseData.put("compiledUrl1", compiledUrl);
            return AjaxResult.success(responseData);
        } catch (Exception e) {
            return AjaxResult.error("该源码文件尚未插桩编译，请先进行插桩编译再测试！");
        }
    }
}
