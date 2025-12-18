package com.en_chu.calculator_api_spring.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.service.MetadataService;

@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataController {

	@Autowired
	private MetadataService metadataService;

	@GetMapping
	public Map<String, Object> fetchAll() {
		return metadataService.getAllMetadata();
	}
}