/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.issue.commonrule;

import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.server.computation.batch.TreeRootHolderRule;
import org.sonar.server.computation.component.Component;
import org.sonar.server.computation.component.DumbComponent;
import org.sonar.server.computation.component.FileAttributes;
import org.sonar.server.computation.measure.Measure;
import org.sonar.server.computation.measure.MeasureRepositoryRule;
import org.sonar.server.computation.metric.MetricRepositoryRule;
import org.sonar.server.computation.qualityprofile.ActiveRule;
import org.sonar.server.computation.qualityprofile.ActiveRulesHolderRule;
import org.sonar.server.rule.CommonRuleKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.server.computation.component.DumbComponent.DUMB_PROJECT;

public class DuplicatedBlockRuleTest {

  static RuleKey RULE_KEY = RuleKey.of(CommonRuleKeys.commonRepositoryForLang("java"), CommonRuleKeys.DUPLICATED_BLOCKS);

  static DumbComponent FILE = DumbComponent.builder(Component.Type.FILE, 1)
    .setFileAttributes(new FileAttributes(false, "java"))
    .build();

  @Rule
  public ActiveRulesHolderRule activeRuleHolder = new ActiveRulesHolderRule();

  @Rule
  public MetricRepositoryRule metricRepository = new MetricRepositoryRule()
    .add(CoreMetrics.DUPLICATED_BLOCKS);

  @Rule
  public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(DUMB_PROJECT);

  @Rule
  public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

  DuplicatedBlockRule underTest = new DuplicatedBlockRule(activeRuleHolder, measureRepository, metricRepository);

  @Test
  public void no_issue_if_no_duplicated_blocks() throws Exception {
    activeRuleHolder.put(new ActiveRule(RULE_KEY, Severity.CRITICAL, Collections.<String, String>emptyMap()));
    measureRepository.addRawMeasure(FILE.getReportAttributes().getRef(), CoreMetrics.DUPLICATED_BLOCKS_KEY, Measure.newMeasureBuilder().create(0));

    DefaultIssue issue = underTest.processFile(FILE, "java");

    assertThat(issue).isNull();
  }

  @Test
  public void issue_if_duplicated_blocks() throws Exception {
    activeRuleHolder.put(new ActiveRule(RULE_KEY, Severity.CRITICAL, Collections.<String, String>emptyMap()));
    measureRepository.addRawMeasure(FILE.getReportAttributes().getRef(), CoreMetrics.DUPLICATED_BLOCKS_KEY, Measure.newMeasureBuilder().create(3));

    DefaultIssue issue = underTest.processFile(FILE, "java");

    assertThat(issue.ruleKey()).isEqualTo(RULE_KEY);
    assertThat(issue.severity()).isEqualTo(Severity.CRITICAL);
    assertThat(issue.effortToFix()).isEqualTo(3.0);
    assertThat(issue.message()).isEqualTo("3 duplicated blocks of code must be removed.");
  }
}
