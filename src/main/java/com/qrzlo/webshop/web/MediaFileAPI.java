package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.MediaFile;
import com.qrzlo.webshop.data.domain.Variant;
import com.qrzlo.webshop.data.repository.MediaFileRepository;
import com.qrzlo.webshop.data.repository.VariantRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/mediafile", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
public class MediaFileAPI
{

	private MediaFileRepository mediaFileRepository;
	private VariantRepository variantRepository;

	public MediaFileAPI(MediaFileRepository mediaFileRepository, VariantRepository variantRepository)
	{
		this.mediaFileRepository = mediaFileRepository;
		this.variantRepository = variantRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated MediaFile mediaFile)
	{
		try
		{
			mediaFileRepository.save(mediaFile);
			return ResponseEntity.ok(mediaFile);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "variant") Long variantId)
	{
		try
		{
			Variant variant = variantRepository.findById(variantId).orElseThrow();
			List<MediaFile> mediaFiles = mediaFileRepository.findMediaFilesByVariantOrderByCreatedAt(variant);
			return ResponseEntity.ok(mediaFiles);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().build();
		}
	}
}
