/*
 * Copyright 2017 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.maven;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.maven.util.SingleYamlFlexibleDeployTestHelper;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class FlexDeployQueueMojoTest {

  private DeployQueueMojo mojo = new DeployQueueMojo();

  private TemporaryFolder tempFolder = new TemporaryFolder();

  private SingleYamlFlexibleDeployTestHelper<DeployQueueMojo> testFixture =
      new SingleYamlFlexibleDeployTestHelper<>(mojo, tempFolder);

  @Rule public TestRule testRule = RuleChain.outerRule(tempFolder).around(testFixture);

  @Test
  @Parameters({"jar", "war"})
  public void testDeployFlexible(String packaging)
      throws MojoExecutionException, AppEngineException {
    when(mojo.getMavenProject().getPackaging()).thenReturn(packaging);

    mojo.execute();

    verify(testFixture.getDeploymentMock()).deployQueue(mojo);
  }
}
