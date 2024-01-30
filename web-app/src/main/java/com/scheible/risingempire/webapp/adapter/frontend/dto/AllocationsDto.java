package com.scheible.risingempire.webapp.adapter.frontend.dto;

import java.util.Collections;
import java.util.Map;

/**
 * @author sj
 */
public class AllocationsDto {

	final String id;

	final Map<String, AllocationCategoryDto> categories;

	public AllocationsDto(String id, Map<String, AllocationCategoryDto> categories) {
		this.id = id;
		this.categories = Collections.unmodifiableMap(categories);
	}

}
