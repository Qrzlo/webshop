package com.qrzlo.webshop.data.repository;

import com.qrzlo.webshop.data.domain.MediaFile;
import com.qrzlo.webshop.data.domain.Variant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MediaFileRepository extends CrudRepository<MediaFile, Long>
{
	List<MediaFile> findMediaFilesByVariantOrderByCreatedAt(Variant variant);
}
