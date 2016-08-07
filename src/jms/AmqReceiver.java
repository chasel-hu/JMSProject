package jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import jms.listener.DefaultListener;
import jms.util.AmqUtil;

/**
 * mq消息接收者的类，主要实现在不同方式下接收生产者发送的消息
 * @author hujiancai
 * @data 2016年7月27日
 * @Description 
 * @version v 1.0
 */
public class AmqReceiver extends AmqObject implements Serializable{

	private static final long serialVersionUID = -3865292144455492453L;
	
	private int prefetchSize; // 预读消息的条数
	private MessageConsumer consumer; // 消费者对象
	private String selector; // 选择器，作为过滤器使用


	/**
	 * 调用父类的初始化方法
	 * @param mqName 待建立的mq队列的名称
	 * @param type 要初始化的队列的类型，1表示topic，2表示queue
	 */
	public AmqReceiver(String mqName, int type) {
		super(mqName, type);
		prefetchSize = 1;
	}
	
	/**
	 * 调用父类的初始化方法
	 * @param mqName 待建立的mq队列的名称
	 * @param type 要初始化的队列的类型，1表示topic，2表示queue
	 * @param startup 如果该值为true，则默认自动启动
	 */
	public AmqReceiver(String mqName, int type,boolean startup) {
		this(mqName, type);
		if(startup) initReceiver();
	}
	
	/**
	 * 默认初始化消费者，不进行离线持久化
	 */
	public void initReceiver(){
		try{
			connectionFactory = new ActiveMQConnectionFactory(AmqUtil.userName,AmqUtil.password,AmqUtil.brokerUrl);
			connection = connectionFactory.createConnection();
			connection.start();
			
			//根据不同的类型创建不同的mq队列，再使用queue的时候，选择不开启事务
			if(type == AmqObject.QUEUE_TYPE){
				session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
				Queue queue = new ActiveMQQueue(mqName + "?consumer.prefetchSize=" + prefetchSize);
				consumer = session.createConsumer(queue,selector);
			}else if(type == AmqObject.TOPIC_TYPE){
				session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
				Topic topic = new ActiveMQTopic(mqName + "?consumer.prefetchSize=" + prefetchSize);
				consumer = session.createConsumer(topic,selector);
			}else{
				throw new Exception(" 输入的类型不对 ");
			}
			//设置默认的监听器
			consumer.setMessageListener(new DefaultListener());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 带客户端的初始化，只针对topic类型的MQ
	 * @param clientId 订阅者的客户端
	 * @param subName 订阅者的名称
	 */
	public void initDurableTopicReceiver(String clientId, String subName){
		try{
			connectionFactory = new ActiveMQConnectionFactory(AmqUtil.userName,AmqUtil.password,AmqUtil.brokerUrl);
			connection = connectionFactory.createConnection();
			connection.setClientID(clientId); // 设置客户端的id
			connection.start();
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Topic topic = new ActiveMQTopic(mqName + "?consumer.prefetchSize=" + prefetchSize);
			// 创建一个持久化的订阅者对象
			consumer = session.createDurableSubscriber(topic,subName,selector,false);
			//设置默认的监听器
			consumer.setMessageListener(new DefaultListener());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置监听器
	 * @param listener 待设置的监听器
	 */
	public void addListener(MessageListener listener){
		try {
			consumer.setMessageListener(listener);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public int getPrefetchSize() {
		return prefetchSize;
	}

	public void setPrefetchSize(int prefetchSize) {
		this.prefetchSize = prefetchSize;
	}
	
	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
}