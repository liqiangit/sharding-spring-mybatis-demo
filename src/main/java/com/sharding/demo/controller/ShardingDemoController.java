package com.sharding.demo.controller;

import java.util.Random;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharding.demo.bean.Order;
import com.sharding.demo.bean.OrderItem;
import com.sharding.demo.service.OrderItemService;
import com.sharding.demo.service.OrderService;

import io.shardingsphere.core.keygen.DefaultKeyGenerator;

@RestController
@RequestMapping(value = "/demo")
public class ShardingDemoController {

	private static Logger logger = LogManager.getLogger();

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "orderItemService")
	private OrderItemService orderItemService;

	// 注意这块没有事务控制，如果有报错，是没控制事务的
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void demo() {
		// 在3.1.0版本中，尾数大多为偶数的问题已彻底解决，参见：https://github.com/sharding-sphere/sharding-sphere/issues/1617
		DefaultKeyGenerator key = new DefaultKeyGenerator();
		// int userId = 46;
		Random rand = new Random();
		int userId = rand.nextInt(100);
		Number orderIdKey = key.generateKey();
		Long orderId = orderIdKey.longValue();
		// Long orderId = System.currentTimeMillis();
		logger.info("分布式主键orderId:" + orderId);

		Order order = new Order();
		order.setUserId(userId);
		order.setStatus("1");
		order.setOrderId(orderId);

		Long returnOrderId = orderService.insert(order);
		logger.info("插入成功后的returnOrderId:" + returnOrderId);

		OrderItem item = new OrderItem();
		item.setOrderId(orderId);
		item.setUserId(userId);
		item.setStatus("1");
		Long returnOrderItemId = orderItemService.insert(item);
		logger.info("插入成功后的returnOrderItemId:" + returnOrderItemId);

	}

}
