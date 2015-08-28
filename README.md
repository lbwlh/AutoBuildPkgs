# AutoBuildPkgs
Automatically build packages for puma sdk write by JAVA.

使用方法：
1. 安装Firefox浏览器；
2. 安装JAVA SDK，并配置好环境变量（通过在命令行输入java来检查是否安装好），支持Windows，MAC和Linux平台；
3. 修改configure.properties配置登录Jenkins的用户名，密码，编译平台，参数选项，SVN_COMMENTS等；
4. 在命令行输入：
	java -jar AutoBuildPkgs.jar configure.properties
5. 等待打包完成，并验证通过后，在命令行提示输入的交互处输入：Y 或者 y
	Are you ready to release Windows_Online? Y/y:

缺陷：
1. 由于JAVA property只能读取一行，SVN_COMMENTS目前只能写在一行里，换行只能通过‘\n’；
2. 目前支持发布版本频繁的Android/iOS/MAC/Windows_Local/Windows_Online的branch分支，后续再添加tag、trunk分支；

