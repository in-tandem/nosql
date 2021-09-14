package com.org.somak.demo.nosql.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.org.somak.demo.nosql.entity.Container;
import com.org.somak.demo.nosql.entity.ContainerKey;
import com.org.somak.demo.nosql.repository.ContainerRepository;

@RestController
@RequestMapping("/rest/container/")
public class ContainerController {

	@Autowired
	private ContainerRepository repository;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Container add(@RequestBody Container container) {
		
		return repository.save(container);
	}
	
	@GetMapping	
	@ResponseStatus(HttpStatus.OK)
	public Container findById(@RequestParam("id") String id, @RequestParam("containerType") String containerType) {
		
		ContainerKey key = new ContainerKey(id, containerType);
		Optional<Container> container = repository.findById(key);
		return container.orElseThrow(()->new ResponseStatusException( HttpStatus.NOT_FOUND, "Resource not found"));
	}
}
