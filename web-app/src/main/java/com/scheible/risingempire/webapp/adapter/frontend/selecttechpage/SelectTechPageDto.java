package com.scheible.risingempire.webapp.adapter.frontend.selecttechpage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 * @author sj
 */
@JsonTypeInfo(use = Id.NAME)
class SelectTechPageDto {

	final TechDto researchedTech = new TechDto(null, "Super cow powers");

	final List<EntityModel<TechDto>> techs;

	SelectTechPageDto(List<EntityModel<TechDto>> techs) {
		this.techs = techs;
	}

	static class TechDto {

		final String id;

		final String name;

		final String description = "Something that tells you what something or someone is like.";

		final int expense = 120;

		TechDto(String id, String name) {
			this.id = id;
			this.name = name;
		}

	}

}
