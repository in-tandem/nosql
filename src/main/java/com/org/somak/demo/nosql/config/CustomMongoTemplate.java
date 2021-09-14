package com.org.somak.demo.nosql.config;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CursorPreparer;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoWriter;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import com.google.common.base.Stopwatch;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import lombok.extern.slf4j.Slf4j;

/***
 * 
 * Wrapper over {@link MongoTemplate} class in order to meet audit requirements.
 * Actual operations would still be handled by super class and this wrapper is
 * not expected/intended to perform any additional exception handling.
 * 
 * <br>
 * Class would be responsible for: <br>
 * <li>Generate an unique transaction Id before operation is executed</li>
 * <li>Generate a timestamp before operation is executed</li>
 * <li>Make a call to
 * {@linkplain MyMongoEventListener#emitEvent(String, Stopwatch)} to perform pre
 * mongo operation audit action</li>
 * <li>proxy to {@linkplain MongoTemplate} to perform intended operation</li>
 * <li>Make a call to
 * {@linkplain MyMongoEventListener#emitEvent(String, Stopwatch)} to perform
 * post mongo operation audit action</li>
 * 
 * API teams are expected to use this particular MongoTemplate wrapper in order
 * to meet audit requirements. In case API teams are using
 * {@link SimpleMongoRepository} / {@link CrudRepository} /
 * {@link MongoRepository} , this class would be auto invoked and no further
 * action would be required
 * 
 * @author somak
 *
 */
@Slf4j
public class CustomMongoTemplate extends MongoTemplate {

	@Autowired
	private MyMongoEventListener mongoEventListener;

	public CustomMongoTemplate(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
	}

	@Override
	protected void executeQuery(Query query, String collectionName, DocumentCallbackHandler documentCallbackHandler,
			@Nullable CursorPreparer preparer) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.EXECUTE);
		super.executeQuery(query, collectionName, documentCallbackHandler, preparer);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.EXECUTE);

	}

	@Override
	public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		T response = super.findOne(query, entityClass, collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return response;
	}

	@Override
	public <T> T save(T objectToSave, String collectionName) {
		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.SAVE);
		T response = super.save(objectToSave, collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.SAVE);
		return response;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass, String collectionName) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		List<T> response = super.findAll(entityClass, collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return response;
	}

	@Override
	protected <T> DeleteResult doRemove(String collectionName, Query query, @Nullable Class<T> entityClass,
			boolean multi) {
		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.DELETE);

		DeleteResult response = super.doRemove(collectionName, query, entityClass, multi);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.DELETE);

		return response;
	}

	@Override
	protected UpdateResult doUpdate(String collectionName, Query query, UpdateDefinition update,
			@Nullable Class<?> entityClass, boolean upsert, boolean multi) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.UPDATE);
		UpdateResult response = super.doUpdate(collectionName, query, update, entityClass, upsert, multi);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.UPDATE);
		return response;
	}

	@Override
	public <T> GroupByResults<T> group(@Nullable Criteria criteria, String inputCollectionName, GroupBy groupBy,
			Class<T> entityClass) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(inputCollectionName, Operation.GROUP);

		GroupByResults<T> response = super.group(criteria, inputCollectionName, groupBy, entityClass);

		postPerformOperationEmitEvent(timer, transactionId, inputCollectionName, Operation.GROUP);
		return response;
	}

	protected <O> AggregationResults<O> doAggregate(Aggregation aggregation, String collectionName, Class<O> outputType,
			AggregationOperationContext context) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.AGGREGATE);

		AggregationResults<O> response = super.doAggregate(aggregation, collectionName, outputType, context);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.AGGREGATE);
		return response;
	}

	@Override
	public <T> List<T> mapReduce(Query query, Class<?> domainType, String inputCollectionName, String mapFunction,
			String reduceFunction, @Nullable MapReduceOptions mapReduceOptions, Class<T> resultType) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(inputCollectionName, Operation.MAP_REDUCE);
		List<T> response = super.mapReduce(query, domainType, inputCollectionName, mapFunction, reduceFunction,
				mapReduceOptions, resultType);
		postPerformOperationEmitEvent(timer, transactionId, inputCollectionName, Operation.MAP_REDUCE);
		return response;

	}

	@Override
	public void dropCollection(String collectionName) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.DELETE);
		super.dropCollection(collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.DELETE);
	}

	@Override
	protected <T> List<T> doFind(String collectionName, Document query, Document fields, Class<T> entityClass,
			CursorPreparer preparer) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);

		List<T> response = super.doFind(collectionName, query, fields, entityClass, preparer);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return response;
	}

	@Override
	protected <T> T doFindOne(String collectionName, Document query, Document fields, CursorPreparer preparer,
			Class<T> entityClass) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		T response = super.doFindOne(collectionName, query, fields, preparer, entityClass);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return response;
	}

	@Override
	public boolean collectionExists(String collectionName) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		boolean isExist = super.collectionExists(collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return isExist;
	}

	@Override
	public <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass,
			Class<T> resultClass) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		List<T> response = super.findDistinct(query, field, entityClass, resultClass);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return response;
	}

	@Override
	protected <T> T doFindAndModify(String collectionName, Document query, Document fields, Document sort,
			Class<T> entityClass, UpdateDefinition update, @Nullable FindAndModifyOptions options) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FIND_UPDATE);
		T response = super.doFindAndModify(collectionName, query, fields, sort, entityClass, update, options);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FIND_UPDATE);
		return response;
	}

	@Override
	protected <T> T doInsert(String collectionName, T objectToSave, MongoWriter<T> writer) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.SAVE);

		T response = super.doInsert(collectionName, objectToSave, writer);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.SAVE);

		return response;
	}

	@Override
	protected <T> Collection<T> doInsertBatch(String collectionName, Collection<? extends T> batchToSave,
			MongoWriter<T> writer) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.SAVE);

		Collection<T> response = super.doInsertBatch(collectionName, batchToSave, writer);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.SAVE);

		return response;
	}

	@Override
	public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType,
			String collectionName, Class<T> resultType) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FIND_REPLACE);
		T response = super.findAndReplace(query, replacement, options, entityType, collectionName, resultType);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FIND_REPLACE);
		return response;
	}

	@Override
	public long count(Query query, @Nullable Class<?> entityClass, String collectionName) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		long count = super.count(query, entityClass, collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return count;
	}

	@Override
	protected <T> T doFindAndRemove(String collectionName, Document query, Document fields, Document sort,
			@Nullable Collation collation, Class<T> entityClass) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FIND_REMOVE);
		T response = super.doFindAndRemove(collectionName, query, fields, sort, collation, entityClass);

		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FIND_REMOVE);
		return response;
	}

	@Override
	public boolean exists(Query query, @Nullable Class<?> entityClass, String collectionName) {

		Stopwatch timer = Stopwatch.createStarted();
		String transactionId = prePerformOperationEmitEvent(collectionName, Operation.FETCH);
		boolean isExist = super.exists(query, entityClass, collectionName);
		postPerformOperationEmitEvent(timer, transactionId, collectionName, Operation.FETCH);
		return isExist;
	}

	private void postPerformOperationEmitEvent(Stopwatch timer, String transactionId, String collectionName,
			Operation operation) {

		mongoEventListener.onAfter(timer, transactionId, collectionName, operation);

	}

	private String prePerformOperationEmitEvent(String collectionName, Operation operation) {

		String transactionId = UUID.randomUUID().toString();
		mongoEventListener.onBefore(transactionId, collectionName, operation);

		return transactionId;

	}

}
