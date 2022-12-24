package com.qrzlo.webshop.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrzlo.webshop.data.domain.Category;
import com.qrzlo.webshop.data.repository.CategoryRepository;
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
		try
		{
			categoryRepository.save(category);
			return ResponseEntity.ok(category);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping
	public ResponseEntity<?> getOne(@RequestParam("id") Integer id)
	{
		try
		{
			var found = categoryRepository.findById(id);
			return ResponseEntity.ok(found);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAll()
	{
		try
		{
			var all = categoryRepository.findAll();
			var top = all.stream().filter(a -> a.getParent() == null).collect(Collectors.toSet());
//			Tree tree = new Tree();
//			all.stream().forEach(category ->
//			{
//				Node tempNode = new Node(category);
//				Node found = tree.getNode(tempNode);
//				Node node = null;
//				if (found == null)
//					tree.addNode(node = tempNode);
//				else
//					node = found;
//
//				if (category.getParent() == null)
//					tree.setRoot(node);
//				else
//				{
//					Node tempParentNode = new Node(category.getParent());
//					Node foundParent = tree.getNode(tempParentNode);
//					Node parentNode = null;
//					if (foundParent == null)
//						tree.addNode(parentNode = tempParentNode);
//					else
//						parentNode = foundParent;
//					parentNode.addChild(node);
//				}
//			});
			return ResponseEntity.ok(top);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ResponseEntity.badRequest().body("error in /all");
		}
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
