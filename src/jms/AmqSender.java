package jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import jms.util.AmqUtil;

/**
 * activeMq的生产者类，用于创建生产者
 * @author hujiancai
 * @data 2016年7月27日
 * @Description 
 * @version v 1.0
 */
public class AmqSender extends AmqObject implements Serializable{

	private static final long serialVersionUID = 6218933345092453975L;
	
	protected MessageProducer producer; //生产者对象
	
	/**
	 * 调用父类的初始化方法
	 * @param mqName 待建立的mq队列的名称
	 * @param type 要初始化的队列的类型，1表示topic，2表示queue
	 */
	public AmqSender(String mqName,int type){
		super(mqName, type);
	}
	
	/**
	 * 调用基本的构造器，并进行初始化
	 * @param mqName 待建立的mq队列的名称
	 * @param type 要初始化的队列的类型，1表示topic，2表示queue
	 * @param startup 表示是否初始化，默认是不初始化的，当为true时默认初始化
	 */
	public AmqSender(String mqName,int type,boolean startup){
		this(mqName,type);
		if(startup) initSender();
	}
	
	/**
	 * 初始化生产者，默认是持久化的
	 */
	public void initSender(){
		try{
			connectionFactory = new ActiveMQConnectionFactory(AmqUtil.userName,AmqUtil.password,AmqUtil.brokerUrl);
			connection = connectionFactory.createConnection();
			connection.start();

			// 根据type的类型来选择创建的类型和是否开启事务，在选择queue的时候不开启事务
			if(type == AmqObject.TOPIC_TYPE){
				session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
				destination = session.createTopic(mqName);
			}else if(type == AmqObject.QUEUE_TYPE){
				session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
				destination = session.createQueue(mqName);
			}else{
				throw new Exception(" 输入的类型不对 ");
			}
			producer = session.createProducer(destination);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送消息，可以使用属性作为过滤器，发送给特定的消费者接受
	 * @param message 待发的消息
	 * @param property 要设置的属性
	 * @param value 对应属性的值
	 * @return 一个是否发送成功的标志，true表示发送成功，false表示发送失败
	 */
	public boolean senderMsg(Message message,String[] property,String[] value) {

		try {
			if(property != null && value != null){
				// 参数不一致，抛出异常
				if(property.length != value.length){
					throw new Exception(" 参数和值长度不一致，请检查property数组和value数组的大小 ");
				}
				// 添加属性值，作为selector的过滤器
				for(int i=0;i<property.length;i++){
					message.setStringProperty(property[i], value[i]);
				}
			}
			producer.send(message);
			if(type == AmqObject.TOPIC_TYPE) session.commit(); // commit这一步是很重要的，如果开启事务的话，这是必须的
			
			return true;
		} catch (JMSException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 发送消息，可以使用属性作为过滤器，发送给特定的消费者接受
	 * @param message 待发的消息的文本内容
	 * @param property 要设置的属性
	 * @param value 对应属性的值
	 * @return 一个是否发送成功的标志，true表示发送成功，false表示发送失败
	 */
	public boolean senderMsg(String message,String[] property,String[] value){
		try {
			TextMessage text = session.createTextMessage(message);
			return senderMsg(text,property,value);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送一条普通的文本消息
	 * @param message 待发送的消息文本内容
	 * @return 是否发送成功的标志
	 */
	public boolean senderMsg(String message) {
		try {
			TextMessage text = session.createTextMessage(message);
			return senderMsg(text,null,null);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送一条消息，没有涉及过滤器
	 * @param message 待发送的消息内容
	 * @return 是否发送成功的标志
	 */
	public boolean senderMsg(Message message) {
		return senderMsg(message,null,null);
	}

}