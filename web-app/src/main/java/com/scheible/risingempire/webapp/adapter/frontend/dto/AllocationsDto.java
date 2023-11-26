package com.scheible.risingempire.webapp.adapter.frontend.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author sj
 */
public class AllocationsDto {

	final Map<String, AllocationCategoryDto> categories;

	final Optional<String> locked;

	public AllocationsDto(Map<String, AllocationCategoryDto> categories, Optional<String> locked) {
		this.categories = Collections.unmodifiableMap(categories);
		this.locked = locked;
	}

}
