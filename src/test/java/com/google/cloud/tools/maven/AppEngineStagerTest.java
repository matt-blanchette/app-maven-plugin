/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

import java.io.IOException;
import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnitParamsRunner.class)
public class AppEngineStagerTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private StageMojo stageMojo;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    stageMojo.sourceDirectory = tempFolder.newFolder("source");
  }

  @Test
  public void testNewStager_standard() {
    Mockito.when(stageMojo.isStandardStaging()).thenReturn(true);

    AppEngineStager stager = AppEngineStager.Factory.newStager(stageMojo);
    Assert.assertTrue(stager.getClass().equals(AppEngineStandardStager.class));
  }

  @Test
  public void testNewStager_flexible() {
    AppEngineStager stager = AppEngineStager.Factory.newStager(stageMojo);
    Assert.assertTrue(stager.getClass().equals(AppEngineFlexibleStager.class));
  }
}
