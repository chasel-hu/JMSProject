package crawl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
/**
 * 每个季度爬取一次的爬虫的结果处理类
 * @author liuyanxiang
 * @data 2016年7月9日
 * @version v 1.0
 */
public class QuarterPipeline implements Pipeline {
private List<Map<String,Object>> result;
	/**
	 * 构造方法
	 * @param result 数据存放的集合
	 */
	public QuarterPipeline(List<Map<String,Object>> result) {
		this.result = result;
	}
	/**
	 * 每个页面的数据处理
	 * @author liuyanxiang
	 * @param resultItems 数据结果集合
	 */
	@Override
	public void process(ResultItems resultItems, Task task){
		if(resultItems.get("time") == null){
			return;
		}
		
		try {
			resultItems.put("time", getDate(resultItems.get("time").toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		result.add(resultItems.getAll());
	}
	/**
	 * 日期处理方法
	 * @author liuyanxiang
	 * @param time 待处理日期
	 * @return 处理后的日期
	 * @throws ParseException 日期格式化错误
	 */
	private Date getDate(String time) throws ParseException{
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		time = time.replace("全", "");
		String []times = time.split("年");
		if(times.length == 1){
			date = sdf.parse(times[0] + "-12-31");
		}
		else{
			Pattern p=Pattern.compile("(\\d+)月");  
			Matcher m=p.matcher(times[1]);
			m.find();
			int month = Integer.parseInt(m.group(1));
			switch(month){
				case 1:
				case 2:{
					int year = Integer.parseInt(times[0]) - 1 ;
					date = sdf.parse(year + "-12-31");
					break;
				}
				case 3:
				case 4:
				case 5:{
					date = sdf.parse(times[0] + "-3-31");
					break;
				}
				case 6:
				case 7:
				case 8:{
					date = sdf.parse(times[0] + "-6-30");
					break;
				}
				case 9:
				case 10:
				case 11:{
					date = sdf.parse(times[0] + "-9-30");
					break;
				}
				case 12:{
					date = sdf.parse(times[0] + "-12-31");
					break;
				}
				default:break;
			}
		}
		return date;
	}
}
