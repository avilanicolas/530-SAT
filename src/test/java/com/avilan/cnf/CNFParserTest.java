package com.avilan.cnf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.DecisionDiagramNode;
import com.csc530.sat.SATVariable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CNFParserTest {

   @Test
   public void testParse() throws IOException {
      DecisionDiagram actual = CNFParser.fromFile(Paths.get("cnf/simple.cnf"));
      List<DecisionDiagram> vars = ImmutableList.of("x1", "x2", "x3", "x4", "x5").stream()
            .map(v -> new SATVariable(v))
            .map(DecisionDiagramNode::of)
            .collect(Collectors.toList());
      DecisionDiagram expected = vars
            .get(0).or(vars.get(4).not()).or(vars.get(3))
            .and(vars.get(0).not().or(vars.get(4)).or(vars.get(2)).or(vars.get(3)))
            .and(vars.get(2).not().or(vars.get(3).not()));

      assertEquals(expected, actual);
      assertTrue(actual.satisfies(ImmutableMap.of(
            "x1", true,
            "x2", true,
            "x3", false,
            "x4", false,
            "x5", true).entrySet().stream()
            .collect(Collectors.toMap(
                  entry -> new SATVariable(entry.getKey()),
                  entry -> entry.getValue()))));
   }
}
