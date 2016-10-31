package crawl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 爬虫的页面处理类
 * @author liuyanxiang
 * @data 2016年7月9日
 * @version v 1.0
 */
public class NormalPageProcessor implements PageProcessor {
	//基本参数
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	//需要爬取的内容表达式
	private String expression;
	//需要爬取的URL
	private String processUrl;
	//需要爬取的时间
	private String processTime;
	
	/**
	 * 构造方法
	 * @param processUrl 起始URL
	 * @param expression 表达式名称
	 * @param maxDate 该数据项在数据库里已经存在的最大日期
	 */
	public NormalPageProcessor(String processUrl, String expression, Date maxDate) {
		this.processUrl = processUrl;
		this.expression = expression.replace("[#]", ".*?").replace("[$]", "[0-9.]*")
				.replace("(", "\\(").replace(")", "\\)").replace("{", "(").replace("}", ")");

		if(maxDate == null){
			this.processTime = "2013";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int curYear = Integer.parseInt(sdf.format(new Date()));
			int startYear = 2013;
			while(++startYear <= curYear){
				this.processTime += "|" + String.valueOf(startYear);
			}
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			int curDate = Integer.parseInt(sdf.format(new Date()));
			int startDate = Integer.parseInt(sdf.format(maxDate));
			this.processTime = String.valueOf(startDate);
			while((startDate = addMonth(startDate)) <= curDate){
				processTime += "|" + String.valueOf(startDate);
			} 
		}
		
		System.err.println(" expression is " + this.expression);
	}
	/**
	 * 将日期增加一个月份
	 * @author liuyanxiang
	 * @param date 需要增加的日期
	 * @return 增加后的日期
	 */
	private int addMonth(int date){
    	if(date%100 == 12){
    		date += 100 - 11;
    	}else{
    		date++;
    	}
    	return date;
    }
	
	/**
	 * 页面处理方法
	 * @author liuyanxiang
	 * @param page 页面
	 */
	@Override
	public void process(Page page) {
		String regexStr = "("+this.processUrl+".*("+ processTime +"|index).*)";
		page.addTargetRequests(page.getHtml().links().regex(regexStr).all());
		Document doc = Jsoup.parse(page.getRawText());
		
		Elements title = doc.getElementsByTag("h4");
		Elements body = doc.getElementsByClass("nr");
		
		if(body.size() != 0 && title.size() != 0){
			Pattern valuePattern = Pattern.compile(this.expression);
			Matcher valueMatcher = valuePattern.matcher(body.get(0).text());
			Pattern timePattern = Pattern.compile(".*?(20.*?年(.*?月|.*?季度|.*?年)?)");
			Matcher timeMatcher = timePattern.matcher(title.get(0).text());
			if(valueMatcher.find() && timeMatcher.find()){	
				page.putField("time", timeMatcher.group(1));
				page.putField("value", valueMatcher.group(1));
				page.putField("url", page.getUrl().toString());
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

}
