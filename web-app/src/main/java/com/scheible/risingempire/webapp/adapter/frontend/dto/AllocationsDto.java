package com.scheible.risingempire.webapp.adapter.frontend.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
public class AllocationsDto {

	final Map<String, AllocationCategoryDto> categories;
	@Nullable
	final String locked;

	public AllocationsDto(final Map<String, AllocationCategoryDto> categories, final Optional<String> locked) {
		this.categories = Collections.unmodifiableMap(categories);
		this.locked = locked.orElse(null);
	}
}
