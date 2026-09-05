package com.ruoyi.afl.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.afl.domain.AflCommand;
import com.ruoyi.afl.observer.AflObserver;
import com.ruoyi.afl.subject.AflSubject;
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
import com.ruoyi.afl.domain.Testafl;
import com.ruoyi.afl.service.ITestaflService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * afl测试Controller
 *
 * @author lzh
 * @date 2023-09-25
 */
@RestController
@RequestMapping("/afl/testafl")
public class TestaflController extends BaseController implements AflObserver {
    @Autowired
    private ITestaflService testaflService;
    @Autowired
    private AflSubject subject;


    private List<AflCommand> cmd = new ArrayList<AflCommand>();


    @PostConstruct
    public void init() throws IOException {
        AflCommand aflCommand = new AflCommand();
        cmd.add(aflCommand);
        subject.addObserver(this);
    }

    /**
     * 查询afl测试列表
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:list')")
    @GetMapping("/list")
    public TableDataInfo list(Testafl testafl) {
        startPage();
        List<Testafl> list = testaflService.selectTestaflList(testafl);
        return getDataTable(list);
    }

    /**
     * 导出afl测试列表
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:export')")
    @Log(title = "afl测试", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Testafl testafl) {
        List<Testafl> list = testaflService.selectTestaflList(testafl);
        ExcelUtil<Testafl> util = new ExcelUtil<Testafl>(Testafl.class);
        util.exportExcel(response, list, "afl测试数据");
    }

    /**
     * 获取afl测试详细信息
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:query')")
    @GetMapping(value = "/{fid}")
    public AjaxResult getInfo(@PathVariable("fid") Long fid) {
        return success(testaflService.selectTestaflByFid(fid));
    }

    /**
     * 新增afl测试
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:add')")
    @Log(title = "afl测试", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Testafl testafl) {
        return toAjax(testaflService.insertTestafl(testafl));
    }

    /**
     * 修改afl测试
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:edit')")
    @Log(title = "afl测试", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Testafl testafl) {
        return toAjax(testaflService.updateTestafl(testafl));
    }

    /**
     * 删除afl测试
     */
    @PreAuthorize("@ss.hasPermi('afl:testafl:remove')")
    @Log(title = "afl测试", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fids}")
    public AjaxResult remove(@PathVariable Long[] fids) {
        return toAjax(testaflService.deleteTestaflByFids(fids));
    }

    @GetMapping("/getResults")
    public AjaxResult getResults() {
        return AjaxResult.success(this.cmd.get(0));
    }


    @PostMapping("/startAfl")
    public AjaxResult startAfl(@RequestBody AflCommand aflCommand) throws IOException, InterruptedException {

        System.out.println("uuid:" + aflCommand.getUuid());
        System.out.println("yid:" + aflCommand.getYid());
        aflCommand.setOutflag(true);
        this.cmd.get(0).setResult(""); // 上面有cmd的定义：private List<AflCommand> cmd  = new ArrayList<AflCommand>();
        this.cmd.get(0).setFlag(false);
        testaflService.aflExecute(aflCommand);
        return AjaxResult.success();
    }

    @Override
    public void update(AflCommand cmd) {
        this.cmd.get(0).setResult(cmd.getResult());
    }

    @GetMapping("/stopAFl/{uuid}")
    public AjaxResult stopThread(@PathVariable String uuid) throws InterruptedException, IOException {

        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        System.out.println("现有线程个数：" + noThreads);
        for (int i = 0; i < noThreads; i++) {
            String threadName = lstThreads[i].getName();
            // 中断指定的线程
            if (threadName.equals(uuid)) {
                System.out.println("中断线程：" + lstThreads[i].getName());
                Thread.sleep(500);
                lstThreads[i].interrupt();
            }
        }
        testaflService.stopAfl(uuid);
//        System.out.println("尼古丁！");
        return AjaxResult.success("1");
    }

    @GetMapping("/deleteFuzzOut")
    public AjaxResult clearFuzzOut() throws IOException {
        testaflService.clear();
        return AjaxResult.success();
    }
}
