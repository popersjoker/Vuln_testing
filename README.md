

# Vuln_testing

> 基于 AFL 的自动化漏洞模糊测试平台

## 📖 项目简介

Vuln_testing 是一个**自动化漏洞模糊测试平台**，采用前后端分离架构设计，将展示层与业务逻辑层解耦，提升系统的可维护性和扩展性。系统以开源模糊测试工具 **AFL（American Fuzzy Lop）** 为核心引擎，围绕 **文件上传 → 插桩编译 → 模糊测试 → 记录管理** 的漏洞检测全流程构建，实现了从源码到漏洞发现的一站式自动化测试能力。

## 🎯 项目目标

- 提供 C/C++ 源码文件的自动化插桩编译与模糊测试能力
- 集成 AFL 工具，构建专业级的漏洞检测环境
- 实现测试过程可视化、测试数据可追溯、测试结果可管理
- 降低安全测试门槛，提升漏洞发现效率

## 📋 技术架构

| 架构层次 | 技术选型 | 说明 |
| :--- | :--- | :--- |
| **前端展示层** | HTML / CSS / JavaScript | 用户交互与操作可视化 |
| **后端业务层** | Spring Boot | 业务逻辑处理与 API 服务 |
| **测试引擎** | AFL（American Fuzzy Lop） | 插桩编译与模糊测试核心 |
| **数据存储** | MySQL | 测试记录、结果数据持久化 |
| **构建工具** | Maven | 项目依赖管理与构建 |

## 📁 项目结构

```
Vuln_testing/
├── .github/                # GitHub Actions CI/CD 配置
├── Compile/                # AFL 插桩编译脚本
├── Fuzz-in/                # 种子输入池（测试输入素材）
├── Out-HTML/               # 测试报告输出目录
├── Source-Directory/       # 源码存储目录
├── ruoyi-admin/            # RuoYi 管理模块
├── ruoyi-common/           # RuoYi 公共模块
├── ruoyi-framework/        # RuoYi 框架核心
├── ruoyi-generator/        # 代码生成模块
├── ruoyi-quartz/           # 定时任务模块
├── ruoyi-system/           # 系统管理模块
├── ruoyi-ui/               # 前端界面模块
├── ruoyi-afl/              # AFL 适配模块
├── sql/                    # 数据库初始化脚本
├── test2/                  # 前端附加测试
├── pom.xml                 # Maven 父级配置
├── ry.sh / ry.bat          # 跨平台启动脚本
├── .gitignore              # Git 忽略配置
└── LICENSE                 # 开源许可证
```
