package com.org.somak.demo.nosql.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.org.somak.demo.nosql.entity.Container;
import com.org.somak.demo.nosql.entity.ContainerKey;

public interface ContainerRepository extends MongoRepository<Container, ContainerKey> {

}
//public interface ContainerRepository {
//
//	Container save(Container entity);
//	
//	Container findById(ContainerKey key);
//}
