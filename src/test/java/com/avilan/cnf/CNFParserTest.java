package com.avilan.cnf;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Test;

import com.avilan.sat.SATVariable;
import com.google.common.collect.ImmutableMap;

public class CNFParserTest {

	@Test
	public void testParse() throws IOException {
		assertTrue(CNFParser.fromFile(Paths.get("cnf/simple.cnf"))
				.satisfies(ImmutableMap.of(
						"x1", true,
						"x2", true,
						"x3", false,
						"x4", true,
						"x5", true).entrySet().stream()
						.collect(Collectors.toMap(
								entry -> new SATVariable(entry.getKey()),
								entry -> entry.getValue()))));
	}

}
