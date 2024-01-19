# Minecraft Client XRay

![Gradle](https://img.shields.io/badge/Gradle-v8%2E5-g?logo=gradle&style=flat-square&link=https://gradle.org)
![Zulu JDK](https://img.shields.io/badge/Zulu%20JDK-8-blue?style=flat-square&link=https://www.azul.com/)
![GitHub License](https://img.shields.io/github/license/ideal-state/minecraft-forge-xray?style=flat-square&link=.%2FLICENSE)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/ideal-state/minecraft-forge-xray?style=flat-square&logo=github)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ideal-state/minecraft-forge-xray/gradle-release.yml?style=flat-square)
![GitHub Release](https://img.shields.io/github/v/release/ideal-state/minecraft-forge-xray?style=flat-square&link=https://github.com/ideal-state/minecraft-forge-xray/releases)
![Discord](https://img.shields.io/discord/1191122625389396098?style=flat-square&logo=discord)

------------------------------------------------------

* 我的第 1.5 个 forge mod（啊？哪来的 0.5）
* 当前仅支持 1.7.10
* 基于 MinecraftForge-1.7.10-10.13.4.1614-1.7.10 开发
* 该项目使用 GPLv3 开源许可证

------------------------------------------------------

### 注意！！！

#### 这个模组仅用于好友联机，切勿用于专用服务器客户端（任何作弊行为造成的后果都由滥用者自行承担）

#### 永远不会新增有关多人游戏防作弊检测的内容

### 在哪下载 ?

> 前往 [releases](https://github.com/ideal-state/minecraft-forge-xray/releases) 页

### 如何构建

```shell
git clone git@github.com:ideal-state/redirect.git ./xray
```

```shell
cd ./xray
```

```shell
./gradlew.bat reobfJar
```

或

```shell
./gradlew reobfJar
```

> 等待构建完成，在 ./build/libs 下会生成 .jar 工件

### 关于开发配置

> 请查看 [gradle.properties](./gradle.properties)

### 怎样成为贡献者 ?

在贡献之前，你需要了解相应的规范。仔细阅读下列内容，对你所贡献的内容是否能够通过审核很有帮助！

> 🔔 首先，请先了解对应子组件所使用的开源许可证内容和 [Developer Certificate of Origin](https://developercertificate.org)
> 协议

#### 📏 一些规范

* 重要！！！贡献者须保证其所贡献的内容遵守了对应的开源许可证（以贡献内容所提交到的目标子组件所使用的开源许可证为准）中的条款
* 重要！！！每次提交贡献内容时须签署 [Developer Certificate of Origin](https://developercertificate.org)
  协议（idea：提交时勾选 `signed-off` 选项；cmd：提交时追加 `-s` 参数）
* 重要！！！为了保证本项目的独立性，本项目下的任何组件都应该避免引用来自第三方库的内容
* 统一缩进，即 4 个空格
* 任何可能会被开放给外部调用的类、方法等内容，都应该尽量为其添加文档注释说明（包括但不限于描述、参数、返回值、异常等必要说明）
* 贡献者可以在其添加或修改的内容上的注释说明中留下其名字，但不能随意地更改或删除已存在的其他贡献者的名字
* 只有 `dev` 分支会接受贡献请求
* 待补充……

#### 📌 步骤说明

1. `fork` 项目并 `clone` 项目到本地
2. 切换到 `dev` 分支，编辑你需要修改的部分
3. 提交并推送 `dev` 分支的改动到你 `fork` 后所创建的仓库
4. 点击 GitHub 页面顶部栏的 `pull request` ，认真填写与改动部分有关的说明信息后提交
5. 等待维护者审核通过后合并

