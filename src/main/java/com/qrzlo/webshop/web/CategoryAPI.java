package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.repository.CategoryRepository;
import com.qrzlo.webshop.util.exception.AbsentDataException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/category",
				produces = {MediaType.APPLICATION_JSON_VALUE},
				consumes = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryAPI
{
	private CategoryRepository categoryRepository;

	public CategoryAPI(CategoryRepository categoryRepository)
	{
		this.categoryRepository = categoryRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Category category)
	{
		var newCategory = categoryRepository.save(category);
		return ResponseEntity.ok(newCategory);
	}

	@GetMapping
	public ResponseEntity<?> getOne(@RequestParam("id") Integer id)
	{
		var found = categoryRepository
				.findById(id)
				.orElseThrow(() -> new AbsentDataException("The category cannot be found"));
		return ResponseEntity.ok(found);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAll()
	{
		var all = categoryRepository.findAll();
		var top = all.stream().filter(a -> a.getParent() == null).collect(Collectors.toSet());
		return ResponseEntity.ok(top);
	}

	private class Tree
	{
		private Node root;
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private Set<Node> all = new HashSet<>();

		public Node getRoot()
		{
			return root;
		}

		public void setRoot(Node root)
		{
			this.root = root;
		}

		public void addNode(Node n)
		{
			this.all.add(n);
		}

		public Node getNode(Node n)
		{
			for (Node node : all)
				if (node.equals(n))
					return node;
			return null;
		}

		public Tree()
		{
		}

	}
	private class Node
	{
		private Category value;
		private List<Node> children = new ArrayList();

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return Objects.equals(value, node.value);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(value);
		}

		public Node(Category value)
		{
			this.value = value;
		}

		public void addChild(Node c)
		{
			this.children.add(c);
		}
	}
}
