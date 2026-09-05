package com.ruoyi.afl.service.impl;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.ruoyi.afl.domain.*;
import com.ruoyi.afl.mapper.*;
import com.ruoyi.afl.observer.AflObserver;
import com.ruoyi.afl.subject.AflSubject;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.afl.service.ITestaflService;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

/**
 * afl测试Service业务层处理
 *
 * @author lzh
 * @date 2023-09-25
 */
@Service
public class TestaflServiceImpl implements ITestaflService, AflSubject {
    @Autowired
    private TestaflMapper testaflMapper;
    @Autowired
    private TableCompileMapper tableCompileMapper;
    @Autowired
    private TableFileMapper tableFileMapper;
    @Autowired
    private TableInputFileMapper tableInputFileMapper;

    @Autowired
    private TableRecordMapper tableRecordMapper;
    TableRecord tableRecord = new TableRecord();

    static String quanju = "";

    /**
     * 查询afl测试
     *
     * @param fid afl测试主键
     * @return afl测试
     */
    @Override
    public Testafl selectTestaflByFid(Long fid) {
        return testaflMapper.selectTestaflByFid(fid);
    }

    /**
     * 查询afl测试列表
     *
     * @param testafl afl测试
     * @return afl测试
     */
    @Override
    public List<Testafl> selectTestaflList(Testafl testafl) {
        return testaflMapper.selectTestaflList(testafl);
    }

    /**
     * 新增afl测试
     *
     * @param testafl afl测试
     * @return 结果
     */
    @Override
    public int insertTestafl(Testafl testafl) {
        return testaflMapper.insertTestafl(testafl);
    }

    /**
     * 修改afl测试
     *
     * @param testafl afl测试
     * @return 结果
     */
    @Override
    public int updateTestafl(Testafl testafl) {
        return testaflMapper.updateTestafl(testafl);
    }

    /**
     * 批量删除afl测试
     *
     * @param fids 需要删除的afl测试主键
     * @return 结果
     */
    @Override
    public int deleteTestaflByFids(Long[] fids) {
        return testaflMapper.deleteTestaflByFids(fids);
    }

    /**
     * 删除afl测试信息
     *
     * @param fid afl测试主键
     * @return 结果
     */
    @Override
    public int deleteTestaflByFid(Long fid) {
        return testaflMapper.deleteTestaflByFid(fid);
    }


    // 随机生成一个目录的方法，获取这个随机生成目录的路径
    public static String generateUniqueDirectory(String basePath) throws IOException {
        String uniqueID = UUID.randomUUID().toString();
        Path directoryPath = Paths.get(basePath, uniqueID);
        while (Files.exists(directoryPath)) {
            uniqueID = UUID.randomUUID().toString();
            directoryPath = Paths.get(basePath, uniqueID);
        }
        Files.createDirectories(directoryPath);
        quanju = directoryPath.getFileName().toString(); // quanju用于存放目录的路径，
        System.out.println("这是随机在桌面生成的目录名和路径，用来存放输出文件：" + quanju);
        return directoryPath.toString();  // 返回目录的路径名没问题
    }

    public void CorePattern(String command) {
        try {
            List<String> commandList = new ArrayList<>();
            commandList.add("sudo");
            commandList.add("sh");
            commandList.add("-c");
            commandList.add(command);
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code:" + exitCode);
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    // 执行afl-fuzz命令，这里涉及到很多东西：
//    1. 根据yid查三个表的文件名和路径
//    2. 写命令的顺序，依次是 sudo sh -c 'echo "core" > /proc/sys/kernel/core_pattern'
//       afl-fuzz -i /fuzz-in -o /fuzz-out /compiled_file

    public void aflExecute(AflCommand aflCommand) throws InterruptedException {
        TableFile tableFile = tableFileMapper.selectTableFileByYid(Long.parseLong(aflCommand.getYid()));
        TableCompile compile = tableCompileMapper.selectLatestTableCompileByYid(Long.parseLong(aflCommand.getYid()));
        TableInputFile tableInputFile = tableInputFileMapper.selectByYid(Long.parseLong(aflCommand.getYid()));
        Date testTime = aflCommand.getTestTime(); // Tue Nov 07 15:52:59 CST 2023
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String basePath = "/home/user/Desktop/";
                Thread.currentThread().setName(aflCommand.getUuid());
                System.out.println(Thread.currentThread().getName());
                String compileDirAndFile = compile.getCompiledUrl() + "/" + compile.getCompiledName(); // 编译文件
                String inputDir = tableInputFile.getUploadInputUrl(); // 输入文件-测试用例

                try {
                    String corepattern = "echo \"core\" > /proc/sys/kernel/core_pattern"; // 执行sudo sh -c 'echo "core" > /proc/sys/kernel/core_pattern'
                    CorePattern(corepattern);
                    String FuzzOutDir = generateUniqueDirectory(basePath); // 已经生成了一个输出文件，fuzz-out
//                    System.out.println("理塘丁真");
                    // 指定要执行的命令及其参数 afl-fuzz -i /fuzz-in -o /fuzz-out compiled_file
                    // fuzz-in通过前端查找，compiled_file通过前端查找
                    String[] command = {
                            "afl-fuzz",
                            "-i",
                            inputDir,
                            "-o",
                            FuzzOutDir,
                            compileDirAndFile,
                    };
                    System.out.println("打印一下afl-fuzz命令：它是什么？" + Arrays.toString(command));
                    // 创建 ProcessBuilder 对象
                    ProcessBuilder processBuilder = new ProcessBuilder(command);

                    // 设置工作目录 就是afl的安装路径要使用
                    File workingDirectory = new File("/home/user/Desktop/afl-2.52b");
                    processBuilder.directory(workingDirectory);

                    // 执行命令
                    Process process = processBuilder.start();

                    StringBuilder s = new StringBuilder();
                    s.append(" ");

                    // 创建线程读取命令输出流
                    Thread outputThread = new Thread(() -> {
                        try {
                            InputStream inputStream = process.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
                            String jump = "<br>\n";
                            String line;
                            while ((line = reader.readLine()) != null && aflCommand.isOutflag()) {
                                System.out.println(line);
                                s.append(jump);
                                s.append(line);
                                aflCommand.setResult(s.toString());
                                aflCommand.setFlag(false);
                                notifyObserves(aflCommand);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    /*
                     * 我把赋值写在这里试一下，如果点击测试后，启动线程前将数据插入表中，看看合适不合适
                     *
                     * 对类全局变量tableRecord进行赋值
                     */
                    tableRecord.setCid(compile.getCid());
                    tableRecord.setUid(tableInputFile.getUid());
                    tableRecord.setYid(tableFile.getYid());
//                    System.out.println("假烟发现就跑路！");
//                    String ss = simpleDateFormat.format(testTime); // 这里我看看是什么字符串
//                    System.out.println(ss);
                    tableRecord.setTestTime(testTime); // new Date是什么？
                    tableRecord.setUuid(aflCommand.getUuid());
                    tableRecord.setFuzzOutUrl(FuzzOutDir);
//                    System.out.println("我是老爹！");
                    System.out.println(tableRecord.toString()); // 打印一下所有输出的信息
                    tableRecordMapper.insertTableRecord(tableRecord); // 现在插入可以生效，接下来弄一下保存功能
                    // 启动输出线程
                    outputThread.start();
                    outputThread.join();

                    aflCommand.setResult(s.toString());
                    aflCommand.setFlag(true);
                    notifyObserves(aflCommand);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    aflCommand.setOutflag(false);
                }
            }
        });
        t.start();
        t.join();
    }

    public void stopAfl(String uuid) {
        String uuid1 = tableRecord.getUuid();
        TableRecord tableRecord1 = tableRecordMapper.selectTableRecordByUuid(uuid1);
//      改变url，要先做一下移动操作
        String yuanFuzzOutPath = tableRecord1.getFuzzOutUrl(); // /home/user/Desktop/f767fa05-c259-434f-ab13-9d68cb4d7e0e
        String nowFuzzOutPath = "/home/user/Desktop/fuzz-out";
        File yuanDirectory = new File(yuanFuzzOutPath); // yuanDirectory: f767fa05-c259-434f-ab13-9d68cb4d7e0e
        File nowDirectory = new File(nowFuzzOutPath); // nowDirectory: fuzz-out 一定存在
        System.out.println(yuanDirectory.getPath() + "  " + nowDirectory.getPath());
//        System.out.println("哥们在这跟你说唱");
        try {
            String[] command = {"mv", yuanDirectory.getPath(), nowDirectory.getPath()};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            // 打印命令的输出
            // 请注意，这里仅打印标准输出，如果需要标准错误输出，也需要读取错误流
            System.out.println("Command executed with exit code: " + exitCode); // 1 2023/11/18 变成0了
            System.out.println("目录移动成功！");
            // 更新数据库信息：
            String sss = nowFuzzOutPath + "/" + quanju;// /mnt/hgfs/test/RuoYi-Vue-master/Fuzz-out//home/user/Desktop/997ed415-9eaa-425b-84f9-8b0b7faa7fd3
            tableRecord.setFuzzOutUrl(sss);
            System.out.println(sss);
            tableRecordMapper.updateTableRecord(tableRecord);
            System.out.println("table_record的fuzz-out已更新成功！");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("目录移动失败：" + e.getMessage());
        }
    }

    public void clear() throws IOException {
        Thread t = new Thread(() -> {
            try {
                // 指定要执行的删除命令
                String[] command = {
                        "/bin/sh",
                        "-c",
                        "sudo rm -rf /mnt/hgfs/test/Ruoyi-Vue-master/Fuzz-out/*"
                };

                // 创建 ProcessBuilder 对象
                ProcessBuilder processBuilder = new ProcessBuilder(command);

                // 设置工作目录
                File workingDirectory = new File("/home/user/Desktop/");
                processBuilder.directory(workingDirectory);

                // 执行删除命令
                Process process = processBuilder.start();

                // 等待命令执行完毕
                int exitCode = process.waitFor();
                System.out.println("删除命令执行完毕，退出码：" + exitCode);

                // 处理删除命令执行的结果，根据需要进行操作
                if (exitCode == 0) {
                    System.out.println("删除成功！");
                } else {
                    System.out.println("删除失败！");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                // 处理异常，例如记录日志等
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // 处理中断异常，例如记录日志等
        }
    }


    @Override
    public void addObserver(AflObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(AflObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserves(AflCommand cmd) {
        for (AflObserver observer : observers) {
            observer.update(cmd);
        }
    }
}
