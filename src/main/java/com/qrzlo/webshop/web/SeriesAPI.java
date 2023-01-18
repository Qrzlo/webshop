package com.qrzlo.webshop.web;

import com.qrzlo.webshop.data.domain.Series;
import com.qrzlo.webshop.data.repository.SeriesRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/series",
		consumes = {MediaType.APPLICATION_JSON_VALUE},
		produces = {MediaType.APPLICATION_JSON_VALUE})
public class SeriesAPI
{
	private SeriesRepository seriesRepository;

	public SeriesAPI(SeriesRepository seriesRepository)
	{
		this.seriesRepository = seriesRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Validated Series series)
	{
		var newSeries = seriesRepository.save(series);
		return ResponseEntity.ok(newSeries);
	}

	@GetMapping
	public ResponseEntity<?> read(@RequestParam(name = "series") Integer seriesId)
	{
		var series = seriesRepository.findById(seriesId).orElseThrow();
		return ResponseEntity.ok(series);
	}
}
