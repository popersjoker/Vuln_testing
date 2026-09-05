package com.ruoyi.afl.controller;

import com.ruoyi.afl.domain.TableCompile;
import com.ruoyi.afl.domain.TableFile;
import com.ruoyi.afl.service.ITableCompileService;
import com.ruoyi.afl.service.ITableFileService;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Arrays;

/*
 * 功能：
 * 作者：zyz
 * 日期：2023/10/27 9:20
 */
@RestController
@RequestMapping("/afl/code")
public class SourceCodeController {

    @Autowired
    private ITableFileService tableFileService;
    @Autowired
    private ITableCompileService tableCompileService;

    @GetMapping(value = "/getCode/{yid}")
    public String getSourceCode(@PathVariable("yid") String yid) {
        System.out.println(yid);
        TableFile tableFile = tableFileService.selectTableFileByYid(Long.parseLong(yid));

//        String fileName = tableFile.getFileName().replace(" ", "\\ ");
//        String FileAndUrl = "\"" + tableFile.getUploadUrl() + fileName + "\"";
//        遗留问题  文件名或者路径名字带有空格该怎么办？？？已解决 前端限制一下输入的文件名
        String FileAndUrl = tableFile.getUploadUrl() + tableFile.getFileName();
        System.out.println(FileAndUrl);
        String[] command = {
                "cat",
                FileAndUrl
        };
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
                System.out.println("Command executed successfully:");
                System.out.println(output);  // 这里都是可以成功执行的
                StringBuilder ooo = output;
                System.out.println(ooo.toString());
                return ooo.toString();
            } else {
                System.err.println("Error executing command:");
                System.err.println(errorOutput.toString());
                return "Error fetching source code";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching source code";
        }
    }

    @GetMapping(value = "/AflGcc/{yid}")
    public String aflGcc(@PathVariable("yid") String yid) {
        System.out.println(yid);
        TableFile tableFile = tableFileService.selectTableFileByYid(Long.parseLong(yid)); // 获取源码文件的yid，url和文件名
        boolean yidExists = tableCompileService.existsByYid(tableFile.getYid()); // 通过yid判断table_compile中是否有记录
        System.out.println(yidExists);
        TableCompile compile = tableCompileService.selectLatestTableCompileByYid(tableFile.getYid()); // 根据yid查表，查出compiled_url，compiled_name
        if (yidExists) { // 表中有记录并且目录存在
            if (compile.getCompiledUrl() != null) {
                System.out.println(compile.getCompiledUrl());
                File onceDest = new File(compile.getCompiledUrl()); // 文件对象，用于一会判断表中url的目录是否存在
                System.out.println(onceDest);
                if (onceDest.exists()) {
                    try {         // 执行删除操作 sudo rm -rf url
                        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "rm", "-rf", compile.getCompiledUrl());
                        processBuilder.redirectErrorStream(true);
                        Process process = processBuilder.start();
                        process.waitFor();
                        int exitCode = process.exitValue();
                        System.out.println(exitCode);  // 1
                        if (exitCode == 0) {
                            System.out.println("删除成功");
                        } else {
                            System.out.println("删除失败");
                            return "失败，exitCode不为0";
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        return "删除目录失败，有异常";
                    }
                }
            }
            // 上述代码的作用就是一个判断，因为不排除点击插桩编译时出现多次点击的现象，这样会导致大量的编译文件生成，而数据库数据不便管理
            // 所以，逻辑是先去找源码文件yid 对应编译表是否有数据，如果有数据就先删除之前生成的编译文件
            // 继续写执行afl-gcc
            String destFile = tableFile.getUploadUrl() + tableFile.getFileName(); // 第二个参数 /mnt/hgfs/tets/Ruoyi-Vue-master/Source-Directory/xieyao.rar
            System.out.println(destFile); // 选择的源码文件
            String jieyatoDir = "/mnt/hgfs/test/Ruoyi-Vue-master/Source-Directory/" + UUID.randomUUID();
            if (destFile.toLowerCase().endsWith(".rar")) { // 上传的文件是rar文件 ， 要先进行解压，找到解压后的文件或文件夹下的文件
                File compileDirectory = new File(jieyatoDir);  // 文件对象 编译的路径
                if (!compileDirectory.exists()) {
                    compileDirectory.mkdirs();
                }
                String[] unrarcmd = {"unrar", "x", destFile, "-d", jieyatoDir};
                try {  // 执行rar解压操作
                    Process process = Runtime.getRuntime().exec(unrarcmd);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("RAR 文件解压成功！");
                        String extratedFilePath = findCFiles(jieyatoDir);
                        if (extratedFilePath != null) {
                            destFile = extratedFilePath;
                            System.out.println("找到.c文件" + destFile);
                        } else {
                            System.out.println("未找到.c文件");
                        }
                    } else {
                        System.out.println("RAR 文件解压失败！");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (destFile.toLowerCase().endsWith(".zip")) {
                File compileDirectory = new File(jieyatoDir);  // 文件对象 编译的路径
                if (!compileDirectory.exists()) {
                    compileDirectory.mkdirs();
                }
                String[] unzipcmd = {"unzip", destFile, "-d", jieyatoDir};
                try {  // 执行zip解压操作
                    Process process = Runtime.getRuntime().exec(unzipcmd);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("ZIP 文件解压成功！");
                        String extractedFilePath = findCFiles(jieyatoDir);
                        if (extractedFilePath != null) {
                            destFile = extractedFilePath;
                            System.out.println("找到.c文件：" + destFile);
                        } else {
                            System.out.println("未找到.c文件！");
                        }
                    } else {
                        System.out.println("ZIP 文件解压失败！");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String randomDirectoryName = UUID.randomUUID().toString();  // 随机生成的目录名字
            String compileDirectoryPath = "/mnt/hgfs/test/RuoYi-Vue-master/Compile/" + randomDirectoryName; // 目录在/home/zyz/Desktop/随机生成的名字下
            File compileDirectory = new File(compileDirectoryPath);  // 文件对象 编译的路径
            if (!compileDirectory.exists()) {
                compileDirectory.mkdirs();
            }
            System.out.println(compileDirectory.isDirectory());
            // 编译文件名要求随机生成
            String compileFileName = "compile_file_" + UUID.randomUUID().toString(); // 随机生成的编译文件名
            String compileFilePath = compileDirectoryPath + "/" + compileFileName;  // 将目录路径和编译文件名进行拼接
            System.out.println(compileFilePath + "????" + destFile); // afl-gcc -g -o /mnt/hgfs/test/RuoYi-Vue-master/Compile/123431y31/compile_file_147614 /mnt/hgfs/test/Ruoyi-Vue-master/Source-Directory/xy/xieyao.c
            String[] command = {
                    "afl-gcc",
                    "-std=gnu99",
                    "-g",
                    "-o",
                    compileFilePath,
                    destFile
            };
            System.out.println("你得告诉我怎么做的命令？1 " + Arrays.toString(command));
            // 这里开始执行afl-gcc的操作
            try {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                int exitCode = process.exitValue();
                System.out.println(exitCode);
                if (exitCode == 0) {
                    System.out.println("编译成功");
                    TableCompile compile1 = new TableCompile();
                    compile1.setCompiledName(compileFileName); // compiled_name
                    compile1.setCompiledUrl(compileDirectoryPath); // compiled_url
                    compile1.setYid(tableFile.getYid());   // yid
                    System.out.println(compile1.getCompiledName() + " " + compile1.getCompiledUrl() + " " + compile1.getYid());
                    tableCompileService.updateTableCompileByYid(compile1);
//                    tableCompileService.insertTableCompile(compile1);
                    return "成功";
                } else {
                    System.out.println("编译失败");
                    return "失败，exitCode不为0";
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "失败，有异常";
            }
        } else {  // 表中无记录并且目录不存在 yid -> compiled_url = null  -> compiled_name = null
            System.out.println("我帅不帅？");
            String destFile = tableFile.getUploadUrl() + tableFile.getFileName(); // 第二个参数
            System.out.println(destFile); // 选择的源码文件
            String jieyatoDir = "/mnt/hgfs/test/Ruoyi-Vue-master/Source-Directory/" + UUID.randomUUID();
            if (destFile.toLowerCase().endsWith(".rar")) { // 上传的文件是rar文件 ， 要先进行解压，找到解压后的文件或文件夹下的文件
                File compileDirectory = new File(jieyatoDir);  // 文件对象 编译的路径
                if (!compileDirectory.exists()) {
                    compileDirectory.mkdirs();
                }
                String[] unrarcmd = {"unrar", "x", destFile, "-d", jieyatoDir};
                try {  // 执行rar解压操作
                    Process process = Runtime.getRuntime().exec(unrarcmd);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("RAR 文件解压成功！");
                        String extratedFilePath = findCFiles(jieyatoDir);
                        if (extratedFilePath != null) {
                            destFile = extratedFilePath;
                            System.out.println("找到.c文件" + destFile);
                        } else {
                            System.out.println("未找到.c文件");
                        }
                    } else {
                        System.out.println("RAR 文件解压失败！");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (destFile.toLowerCase().endsWith(".zip")) {
                File compileDirectory = new File(jieyatoDir);  // 文件对象 编译的路径
                if (!compileDirectory.exists()) {
                    compileDirectory.mkdirs();
                }
                String[] unzipcmd = {"unzip", destFile, "-d", jieyatoDir};
                try {  // 执行zip解压操作
                    Process process = Runtime.getRuntime().exec(unzipcmd);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("ZIP 文件解压成功！");
                        String extractedFilePath = findCFiles(jieyatoDir);
                        if (extractedFilePath != null) {
                            destFile = extractedFilePath;
                            System.out.println("找到.c文件：" + destFile);
                        } else {
                            System.out.println("未找到.c文件！");
                        }
                    } else {
                        System.out.println("ZIP 文件解压失败！");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String randomDirectoryName = UUID.randomUUID().toString();  // 随机生成的目录名字
            String compileDirectoryPath = "/mnt/hgfs/test/RuoYi-Vue-master/Compile/" + randomDirectoryName; // 目录在/home/zyz/Desktop/随机生成的名字下
            File compileDirectory = new File(compileDirectoryPath);  // 文件对象 编译的路径
            if (!compileDirectory.exists()) {
                compileDirectory.mkdirs();
            }
            System.out.println(compileDirectory.isDirectory());
            //        编译文件名要求随机生成
            String compileFileName = "compile_file_" + UUID.randomUUID().toString(); // 随机生成的编译文件名
            String compileFilePath = compileDirectoryPath + "/" + compileFileName;  // 将目录路径和编译文件名进行拼接
            System.out.println(compileFilePath + " " + destFile);
            String[] command = {
                    "afl-gcc",
                    "-g",
                    "-o",
                    compileFilePath,
                    destFile
            };
            System.out.println("你得告诉我怎么做的命令？2 " + Arrays.toString(command));
            // 直接afl-gcc
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
//                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                int exitCode = process.exitValue();
                System.out.println(exitCode);  // 1
                if (exitCode == 0) {
                    System.out.println("编译成功");
                    TableCompile compile1 = new TableCompile();
                    compile1.setCompiledName(compileFileName); // compiled_name
                    compile1.setCompiledUrl(compileDirectoryPath); // compiled_url
                    compile1.setYid(tableFile.getYid());   // yid
                    System.out.println(compile1.getCompiledName() + " " + compile1.getCompiledUrl() + " " + compile1.getYid());
                    tableCompileService.insertTableCompile(compile1);
                    return "成功";
                } else {
                    System.out.println("编译失败");
                    return "失败，exitCode不为0";
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "失败，有异常";
            }
        }
    }

    // 查找解压后文件中是否存在.c文件 后续可能要改，比如找的是一个main.c文件
    private static String findCFiles(String directory) {  // 传过来的是jieyatoDir = /mnt/hgfs/test/Ruoyi-Vue-master/Source-Directory/d62f38cf-e309-49ca-a765-64e585f668bf
        File dir = new File(directory);
        if (dir.isDirectory()) { // 这个dir一定是一个目录，因为传过来的 就是解压到的路径，即 Source-Directory下的随机目录
            File[] files = dir.listFiles();// 文件列表files 可能包含xy.c, xy ,....
            if (files != null) { // 文件列表不为空
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".c")) { // 判断当前文件是文件吗，是.c结尾的文件吗？
                        return file.getAbsolutePath();   // 如果是文件并且以.c结尾，返回该文件的绝对路径
                    } else if (file.isDirectory()) { // 如果当前文件是目录
                        String filePath = findCFiles(file.getAbsolutePath()); // 递归这个方法，把当前目录的绝对路径传过去
                        if (filePath != null) {    // 如果这个目录内有.c文件，就拿到了它的路径，如果没有就返回null
                            return filePath;
                        }
                    }// else-if
                }// for
            }
        }
        return null;
    }
}

