/**
 * 
 */
package crawl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author liuyanxiang
 * @data 2016年7月9日
 * @Description firefox下载器的线程池类
 * @version v 1.0
 */
public class FireFoxDriverPool {
	//日志记录对象
	private Logger logger = Logger.getLogger(getClass());
	//默认线程池大小
    private final static int DEFAULT_CAPACITY = 5;
    //线程池大小
    private final int capacity;
    //运行状态
    private final static int STAT_RUNNING = 1;
    //关闭状态
    private final static int STAT_CLODED = 2;
    //状态转换对象
    private AtomicInteger stat = new AtomicInteger(STAT_RUNNING);

    /**
     * store webDrivers created
     */
    private List<WebDriver> webDriverList = Collections.synchronizedList(new ArrayList<WebDriver>());

    /**
     * store webDrivers available
     */
    private BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>();
    /**
     * 构造函数
     * @param capacity 线程数量
     */
    public FireFoxDriverPool(int capacity) {
        this.capacity = capacity;
    }
    /**
     * 默认构造函数，设置默认线程数量
     */
    public FireFoxDriverPool() {
        this(DEFAULT_CAPACITY);
    }
    /**
     * 从线程池中获得下载器对象
     * @author liuyanxiang
     * @return 下载器对象
     * @throws InterruptedException
     */
    public WebDriver get() throws InterruptedException {
        checkRunning();
        WebDriver poll = innerQueue.poll();
        if (poll != null) {
            return poll;
        }
        if (webDriverList.size() < capacity) {
            synchronized (webDriverList) {
                if (webDriverList.size() < capacity) {
                	FirefoxDriver e = new FirefoxDriver();
                	e.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
                    innerQueue.add(e);
                    webDriverList.add(e);
                }
            }

        }
        return innerQueue.take();
    }
    /**
     * 将用完的下载器对象放回线程池中
     * @author liuyanxiang
     * @param webDriver 用完的下载器对象
     */
    public void returnToPool(WebDriver webDriver) {
        checkRunning();
        innerQueue.add(webDriver);
    }
    /**
     * 检查是否正在运行
     * @author liuyanxiang
     */
    protected void checkRunning() {
        if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
            throw new IllegalStateException("Already closed!");
        }
    }
    /**
     * 关闭所有创建的下载器对象
     * @author liuyanxiang
     */
    public void closeAll() {
        boolean b = stat.compareAndSet(STAT_RUNNING, STAT_CLODED);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        }
        for (WebDriver webDriver : webDriverList) {
            logger.info("Quit webDriver" + webDriver);
            webDriver.quit();
        }
    }
    /**
     * 关闭下载器对象，并从创建列表中移除
     * @author liuyanxiang
     * @param webDriver 待关闭的下载器对象
     * @return
     */
    public boolean close(WebDriver webDriver){
    	webDriver.quit();
    	return webDriverList.remove(webDriver);
    }
}
