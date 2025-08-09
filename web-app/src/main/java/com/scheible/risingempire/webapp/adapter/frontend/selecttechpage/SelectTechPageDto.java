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

	final String category = ""; // not yet supported

	final TechDto researchedTech;

	final List<EntityModel<TechDto>> techs;

	SelectTechPageDto(TechDto researchedTech, List<EntityModel<TechDto>> techs) {
		this.researchedTech = researchedTech;
		this.techs = techs;
	}

	static class TechDto {

		final String id;

		final String name;

		final String description;

		final int expense;

		TechDto(String id, String name, String description, int expense) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.expense = expense;
		}

	}

}
