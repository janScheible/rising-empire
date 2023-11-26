package com.scheible.risingempire.webapp.adapter.frontend.techpage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationsDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 * @author sj
 */
class TechPageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final EntityModel<AllocationsDto> allocations;

	TechPageDto(EntityModel<AllocationsDto> allocations) {
		this.allocations = allocations;
	}

}
