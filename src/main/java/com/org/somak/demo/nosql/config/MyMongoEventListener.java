package com.org.somak.demo.nosql.config;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyMongoEventListener extends AbstractMongoEventListener<Object> {

	public void onBefore(String transactionId, String collectionName, Operation operation) {

		log.info("Starting to perform {} operation on collection {} with transactionId {}", operation.name(),
				collectionName, transactionId);

	}

	public void onAfter(Stopwatch timer, String transactionId, String collectionName, Operation operation) {

		log.info("Completed {} operation on collection {} with transactionId {} time taken {}", operation.name(),
				collectionName, transactionId, timer.stop());

	}
}
