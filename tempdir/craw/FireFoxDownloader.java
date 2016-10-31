/**
 * 
 */
package crawl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * @author liuyanxiang
 * @data 2016年7月9日
 * @Description 利用firefox浏览器下载页面并解析
 * @version v 1.0
 */
public class FireFoxDownloader implements Downloader, Closeable{
	//本下载器的线程池
	private volatile FireFoxDriverPool webDriverPool;
	//日志记录对象
    private Logger logger = Logger.getLogger(getClass());
    //每次下载解析一个页面后的睡眠时间
    private int sleepTime = 0;
    //线程池的大小
    private int poolSize = 1;
    //上次超时页面的URL
    private String lastTimeOutUrl = "";
    //当前超时时间
    private int curTimeOutTime = 5;
    //最大超时时间
    private final static int MaxTimeOutTime = 20;
    //空白构造函数
    public FireFoxDownloader(){}
    /**
     * 构造函数
     * @param firefoxDriverPath 插件路径
     */
    public FireFoxDownloader(String firefoxDriverPath) {
    	System.getProperties().setProperty("webdriver.firefox.bin",firefoxDriverPath);
    }
    /**
     * 设置睡眠时间
     * @author liuyanxiang
     * @param sleepTime 睡眠时间
     * @return 本下载器对象
     */
    public FireFoxDownloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    /**
     * 检查是否初始化线程池
     * @author liuyanxiang
     */
    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this){
                webDriverPool = new FireFoxDriverPool(poolSize);
            }
        }
    }
	/**
	 * 关闭本下载器
	 */
	@Override
	public void close() throws IOException {
		webDriverPool.closeAll();
	}
	/**
	 * 下载函数
	 * @param requet 页面请求对象
	 * @param task 任务
	 */
	@Override
	public Page download(Request request, Task task) {
		checkInit();
        WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        logger.info("downloading page " + request.getUrl());
        try{
        	webDriver.get(request.getUrl());
        }catch(TimeoutException e){
        	webDriverPool.close(webDriver);
        	//是否该页面持续超时
        	if(lastTimeOutUrl.equals(request.getUrl())){
        		if(curTimeOutTime > MaxTimeOutTime){
        			curTimeOutTime = 5;
        			return null;
        		}else{
        			curTimeOutTime += 5;
        			try {
        	            webDriver = webDriverPool.get();
        	        } catch (InterruptedException el) {
        	            logger.warn("interrupted", el);
        	            return null;
        	        }
        			webDriver.manage().timeouts().pageLoadTimeout(curTimeOutTime, TimeUnit.SECONDS);
        			webDriverPool.returnToPool(webDriver);
        		}
        	}
        	lastTimeOutUrl = request.getUrl();
        	return this.download(request,task);
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        webDriverPool.returnToPool(webDriver);
        return page;
	}
	/**
	 * 设置本下载器的线程数量
	 * @param thread 线程大小
	 */
	@Override
	public void setThread(int thread) {
		this.poolSize = thread;
	}
}
