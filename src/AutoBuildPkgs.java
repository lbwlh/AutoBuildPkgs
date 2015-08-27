import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class AutoBuildPkgs {
	private static WebDriver driver;
	private static Actions action;
	private static String buildLink = "http://player-worker-dev001-sjhl.qiyi.virtual:8080/view/%E5%A4%A7%E6%92%AD%E6%94%BE%E5%86%85%E6%A0%B8%E6%8F%90%E6%B5%8B%E4%B8%93%E7%94%A8/";

	public static void main(String args[]) throws IOException, InterruptedException {
		
		Properties config = new Properties();
		InputStream input = new BufferedInputStream (new FileInputStream(args[0]));
		config.load(input);

	    String user = config.getProperty("user");
	    String password = config.getProperty("password");
	    String platform = config.getProperty("platform");
	    String versionpath = config.getProperty("versionpath");
	    String adtest = config.getProperty("adtest");
	    String interfacechanged = config.getProperty("interfacechanged");
	    String releasetombd = config.getProperty("interfacechanged");
	    String releasetotv = config.getProperty("releasetotv");
	    String releasetotvos = config.getProperty("releasetotvos");
	    String buildtype = config.getProperty("buildtype");
	    String ioscomments = config.getProperty("ioscomments");
	    String androidcomments = config.getProperty("androidcomments");
	    String windowscomments = config.getProperty("windowscomments");
	    String maccomments = config.getProperty("maccomments");
	    String buildorrelease = config.getProperty("buildorrelease");
	    
	    input.close();
		
		// 启动Firefox浏览器
		driver = new FirefoxDriver();
		//使用chrome浏览器
//		System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
//		driver = new ChromeDriver();
		action = new Actions(driver);
		driver.get(buildLink);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// 登陆Jenkins
		WebElement weUser = driver.findElement(By
				.xpath("//*[@id='j_username']"));
		weUser.clear();
		weUser.sendKeys(user);

		WebElement wePwd = driver.findElement(By
				.xpath("//*[@name='j_password']"));
		wePwd.clear();
		wePwd.sendKeys(password);

		WebElement submit = driver.findElement(By
				.xpath("//*[@id='yui-gen1-button']"));
		submit.submit();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		if (platform.equals("Android")) {
			if (!buildorrelease.equals("justrelease")) {
				System.out.println("You want to build " + platform +" puma package!!!");
				
				// 跳转至CompileBranch_android_ios_osx界面
				WebElement CompileBranch_android_ios_osx = driver.findElement(By
						.xpath("//*[@id='job_CompileBranch_android_ios_osx']/td[3]/a"));
				CompileBranch_android_ios_osx.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// 跳转至build配置界面
				WebElement build = driver.findElement(By
						.linkText("Build with Parameters"));
				build.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// 选择Rebuild开关
				WebElement rebuildParam = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[4]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
				rebuildParam.click();
				
				// 开始构建：打新包
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				WebElement startBuild = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[14]/tr[1]/td/span[@name='Submit']"));
				//startBuild.click();
			}
			
			// **********等待打包完成，验证通过后release**********
			Scanner release_or_not = new Scanner(System.in);
			String yorn = null;
			do {
				System.out.print("Are you ready to release " + platform + "? Y/y: \n");
				yorn = release_or_not.next(); 
			} while (!(yorn.equals("Y") || yorn.equals("y")));
			release_or_not.close();
			
		    // 将svn_comments转码为UTF-8，解决中文comments乱码问题
		    String comments_utf8 = new String(androidcomments.getBytes("ISO-8859-1"), "UTF-8");
		    System.out.println("Please confirm svn_comments: " + comments_utf8);
			
			// 跳转至Release界面: 1. 大播放内核提测专用
			WebElement tice = driver.findElement(By
					.linkText("大播放内核提测专用"));
			tice.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 跳转至Release界面: 2. Release_android
			WebElement Release_android = driver.findElement(By
					.xpath("//*[@id='job_Release_android']/td[3]/a"));
			Release_android.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 打开配置界面
			WebElement build_with_param = driver.findElement(By
					.linkText("Build with Parameters"));
			build_with_param.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 选择version_path
			if (versionpath.equals("tag_version")) {
				WebElement version_path = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div/select/option[@value='tag_version']"));
				version_path.click();
			} else if (versionpath.equals("trunk_version")) {
				WebElement version_path = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div/select/option[@value='trunk_version']"));
				version_path.click();
			}
			
			// 更新comments输入框
			WebElement android_comments = driver.findElement(By
					.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div/textarea"));
			android_comments.clear();
			android_comments.sendKeys(comments_utf8);
			
			// 广告测试选项
			if (adtest.equals("1")) {
				WebElement ad_test = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[4]/tr[1]/td[3]/div/input[@type='checkbox']"));
				ad_test.click();
			}
			
			// 接口变更选项
			if (interfacechanged.equals("1")) {
				WebElement interface_changed = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[5]/tr[1]/td[3]/div/input[@type='checkbox']"));
				interface_changed.click();
			}
			
			// 发送MBD选项
			if (releasetombd.equals("1")) {
				WebElement release_to_mbd = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[6]/tr[1]/td[3]/div/input[@type='checkbox']"));
				release_to_mbd.click();
			}
			
			// 发送TV选项
			if (releasetotv.equals("1")) {
				WebElement release_to_tv = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[7]/tr[1]/td[3]/div/input[@type='checkbox']"));
				release_to_tv.click();
			}
			
			// 发送TVOS选项
			if (releasetotvos.equals("1")) {
				WebElement release_to_tvos = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[8]/tr[1]/td[3]/div/input[@type='checkbox']"));
				release_to_tvos.click();
			}
			
			// 开始构建：发版本
			WebElement submittoMBD = driver.findElement(By
					.xpath("//*[@id='yui-gen1-button']"));
			//submittoMBD.submit();
			
		} else if (platform.equals("iOS") || platform.equals("MAC")) {
			if (!buildorrelease.equals("justrelease")) {
				System.out.println("You want to build " + platform +" puma package!!!");
				
				// 跳转至CompileBranch_android_ios_osx界面
				WebElement CompileBranch_android_ios_osx = driver.findElement(By
						.xpath("//*[@id='job_CompileBranch_android_ios_osx']/td[3]/a"));
				CompileBranch_android_ios_osx.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// 跳转至build配置界面
				WebElement build = driver.findElement(By
						.linkText("Build with Parameters"));
				build.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				if (platform.equals("iOS")) {
					// 选择iOS
					WebElement buildParamiOS = driver.findElement(By
							.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[2]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
					buildParamiOS.click();
				} else if (platform.equals("MAC")) {
					// 选择OSX
					WebElement buildParamiOS = driver.findElement(By
							.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
					buildParamiOS.click();
				}
			
				// 不选择Android
				WebElement buildParamAndroid = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
				buildParamAndroid.click();
				
				// 选择Rebuild开关
				WebElement rebuildParam = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[4]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
				rebuildParam.click();
				
				// 不选择Androidarm64
				WebElement buildParamARM64 = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[13]/tr[1]/td[3]/div[@name='parameter']/input[@name='value']"));
				buildParamARM64.click();
						
				// 开始构建：打新包
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				WebElement startBuild = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[14]/tr[1]/td/span[@name='Submit']"));
				//startBuild.click();
			}
			
			// **********等待打包完成，验证通过后release**********
			Scanner release_or_not = new Scanner(System.in);
			String yorn = null;
			do {
				System.out.print("Are you ready to release " + platform + "? Y/y: \n");
				yorn = release_or_not.next(); 
			} while (!(yorn.equals("Y") || yorn.equals("y")));
			release_or_not.close();
			
			// 跳转至Release界面: 1. 大播放内核提测专用
			WebElement tice = driver.findElement(By
					.linkText("大播放内核提测专用"));
			tice.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			if (platform.equals("iOS")) {
				// 跳转至Release界面: 2. Release_ios
				WebElement Release_ios = driver.findElement(By
						.xpath("//*[@id='job_Release_ios']/td[3]/a"));
				Release_ios.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			} else if (platform.equals("MAC")) {
				// 跳转至Release界面: 2. Release_osx
				WebElement Release_ios = driver.findElement(By
						.xpath("//*[@id='job_Release_osx']/td[3]/a"));
				Release_ios.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			}
			
			// 打开配置界面
			WebElement build_with_param = driver.findElement(By
					.linkText("Build with Parameters"));
			build_with_param.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 选择version_path
			if (versionpath.equals("tag_version")) {
				WebElement version_path = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div/select/option[@value='tag_version']"));
				version_path.click();
			}
			
			if (platform.equals("iOS")) {
			    // 将svn_comments转码为UTF-8，解决中文comments乱码问题
			    String comments_utf8 = new String(ioscomments.getBytes("ISO-8859-1"), "UTF-8");
			    System.out.println("Please confirm svn_comments: " + comments_utf8);
			    
				// 更新comments输入框
				WebElement ios_comments = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div/textarea"));
				ios_comments.clear();
				ios_comments.sendKeys(comments_utf8);
			} else if (platform.equals("MAC")) {
			    // 将svn_comments转码为UTF-8，解决中文comments乱码问题
			    String comments_utf8 = new String(maccomments.getBytes("ISO-8859-1"), "UTF-8");
			    System.out.println("Please confirm svn_comments: " + comments_utf8);
				
				// 更新comments输入框
				WebElement osx_comments = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div/textarea"));
				osx_comments.clear();
				osx_comments.sendKeys(comments_utf8);
			}
			
			// 广告测试选项
			if (adtest.equals("1")) {
				WebElement ad_test = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[4]/tr[1]/td[3]/div/input[@type='checkbox']"));
				ad_test.click();
			}
			
			// 接口变更选项
			if (interfacechanged.equals("1")) {
				WebElement interface_changed = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[5]/tr[1]/td[3]/div/input[@type='checkbox']"));
				interface_changed.click();
			}
			
			// 开始构建：发版本
			WebElement submittoMBD = driver.findElement(By
					.xpath("//*[@id='yui-gen1-button']"));
			//submittoMBD.submit();
			
		} else if (platform.equals("Windows_Local")) {
			if (!buildorrelease.equals("justrelease")) {
				System.out.println("You want to build " + platform +" puma package!!!");
				
				// 跳转至CompileBranch_win32_LocalPlayer界面
				WebElement CompileBranch_win32_LocalPlayer = driver.findElement(By
						.xpath("//*[@id='job_CompileBranch_win32_LocalPlayer']/td[3]/a"));
				CompileBranch_win32_LocalPlayer.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// 跳转至build配置界面
				WebElement build = driver.findElement(By
						.linkText("Build with Parameters"));
				build.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
				// 设置buildtype
				if (buildtype.equals("debug")) {
					WebElement buildType = driver.findElement(By
							.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div/select/option[@value='Debug']"));
					buildType.click();
				}
						
				// 开始构建：打新包
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				WebElement startBuild = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td/span[@id='yui-gen1']"));
				//startBuild.click();
			}
			
			// **********等待打包完成，验证通过后release**********
			Scanner release_or_not = new Scanner(System.in);
			String yorn = null;
			do {
				System.out.print("Are you ready to release " + platform + "? Y/y: \n");
				yorn = release_or_not.next(); 
			} while (!(yorn.equals("Y") || yorn.equals("y")));
			release_or_not.close();
			
		    // 将svn_comments转码为UTF-8，解决中文comments乱码问题
		    String comments_utf8 = new String(windowscomments.getBytes("ISO-8859-1"), "UTF-8");
		    System.out.println("Please confirm svn_comments: " + comments_utf8);
			
			// 跳转至Release界面: 1. 大播放内核提测专用
			WebElement tice = driver.findElement(By
					.linkText("大播放内核提测专用"));
			tice.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 跳转至Release界面: 2. Release_windows_local
			WebElement Release_windows_local = driver.findElement(By
					.xpath("//*[@id='job_Release_windows_local']/td[3]/a"));
			Release_windows_local.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 打开配置界面
			WebElement build_with_param = driver.findElement(By
					.linkText("Build with Parameters"));
			build_with_param.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 更新comments输入框
			WebElement ios_comments = driver.findElement(By
					.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[2]/tr[1]/td[3]/div/textarea"));
			ios_comments.clear();
			ios_comments.sendKeys(comments_utf8);
			
			// 接口变更选项
			if (interfacechanged.equals("1")) {
				WebElement interface_changed = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div/input[@type='checkbox']"));
				interface_changed.click();
			}
			
			// 开始构建：发版本
			WebElement submittoMBD = driver.findElement(By
					.xpath("//*[@id='yui-gen1-button']"));
			//submittoMBD.submit();
			
		} else if (platform.equals("Windows_Online")) {
			if (!buildorrelease.equals("justrelease")) {
				System.out.println("You want to build " + platform +" puma package!!!");
				
				// 跳转至CompileBranch_win32_OnLinePlayer界面
				WebElement CompileBranch_win32_OnLinePlayer = driver.findElement(By
						.xpath("//*[@id='job_CompileBranch_win32_OnLinePlayer']/td[3]/a"));
				CompileBranch_win32_OnLinePlayer.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// 跳转至build配置界面
				WebElement build = driver.findElement(By
						.linkText("Build with Parameters"));
				build.click();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
				// 设置buildtype
				if (buildtype.equals("debug")) {
					WebElement buildType = driver.findElement(By
							.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[1]/tr[1]/td[3]/div/select/option[@value='Debug']"));
					buildType.click();
				}
						
				// 开始构建：打新包
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				WebElement startBuild = driver.findElement(By
						.xpath("//*[@id='yui-gen1-button']"));
				//startBuild.click();
			}
			
			// **********等待打包完成，验证通过后release**********
			Scanner release_or_not = new Scanner(System.in);
			String yorn = null;
			do {
				System.out.print("Are you ready to release " + platform + "? Y/y: \n");
				yorn = release_or_not.next(); 
			} while (!(yorn.equals("Y") || yorn.equals("y")));
			release_or_not.close();
			
		    // 将svn_comments转码为UTF-8，解决中文comments乱码问题
		    String comments_utf8 = new String(windowscomments.getBytes("ISO-8859-1"), "UTF-8");
		    System.out.println("Please confirm svn_comments: " + comments_utf8);
			
			// 跳转至Release界面: 1. 大播放内核提测专用
			WebElement tice = driver.findElement(By
					.linkText("大播放内核提测专用"));
			tice.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 跳转至Release界面: 2. Release_windows
			WebElement Release_windows = driver.findElement(By
					.xpath("//*[@id='job_Release_windows']/td[3]/a"));
			Release_windows.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 打开配置界面
			WebElement build_with_param = driver.findElement(By
					.linkText("Build with Parameters"));
			build_with_param.click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// 更新comments输入框
			WebElement ios_comments = driver.findElement(By
					.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[2]/tr[1]/td[3]/div/textarea"));
			ios_comments.clear();
			ios_comments.sendKeys(comments_utf8);
			
			// 广告测试选项
			if (adtest.equals("1")) {
				WebElement ad_test = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[3]/tr[1]/td[3]/div/input[@type='checkbox']"));
				ad_test.click();
			}
			
			// 接口变更选项
			if (interfacechanged.equals("1")) {
				WebElement interface_changed = driver.findElement(By
						.xpath("//*[@id='main-panel']/form[@name='parameters']/table/tbody[4]/tr[1]/td[3]/div/input[@type='checkbox']"));
				interface_changed.click();
			}
			
			// 开始构建：发版本
			WebElement submittoMBD = driver.findElement(By
					.xpath("//*[@id='yui-gen1-button']"));
			//submittoMBD.submit();
		}
		
		//driver.quit();
		
	}
	
}
