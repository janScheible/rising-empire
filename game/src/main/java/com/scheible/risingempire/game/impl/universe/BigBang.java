package com.scheible.risingempire.game.impl.universe;

import static java.util.Collections.unmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.universe.Location;

/**
 *
 * @author sj
 */
public interface BigBang {

	// source: https://en.wikipedia.org/wiki/List_of_brightest_stars
	List<String> STAR_NAMES = unmodifiableList(Arrays.asList("Sirius", "Canopus", "Rigil", "Arcturus", "Vega",
			"Capella", "Rigel", "Procyon", "Achernar", "Betelgeuse", "Hadar", "Altair", "Acrux", "Aldebaran", "Antares",
			"Spica", "Pollux", "Fomalhaut", "Deneb", "Mimosa", "Regulus", "Adhara", "Shaula", "Castor", "Gacrux",
			"Bellatrix", "Elnath", "Miaplacidus", "Alnilam", "Regor", "Alnair", "Alioth", "Alnitak", "Dubhe", "Mirfak",
			"Wezen", "Sargas", "Kaus Australis", "Avior", "Alkaid", "Menkalinan", "Atria", "Alhena", "Peacock",
			"Alsephina", "Mirzam", "Alphard", "Polaris", "Hamal", "Algieba", "Diphda", "Mizar", "Nunki", "Menkent",
			"Mirach", "Alpheratz", "Rasalhague", "Kochab", "Saiph", "Denebola", "Algol", "Tiaki", "Muhlifain",
			"Aspidiske", "Suhail", "Alphecca", "Mintaka", "Sadr", "Eltanin", "Schedar", "Naos", "Almach", "Caph",
			"Izar", "Dschubba", "Larawag", "Merak", "Ankaa", "Girtab", "Enif", "Scheat", "Sabik", "Phecda", "Aludra",
			"Markeb", "Navi", "Markab", "Aljanah", "Acrab"));

	static BigBang get() {
		return new UniformBigBang();
	}

	Set<Location> getSystemLocations(GalaxySize galaxySize, int maxSystemDistance);
}
