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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import junitparams.JUnitParamsRunner;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(JUnitParamsRunner.class)
public class AppEngineStandardDeployerTest {
  private static final String PROJECT_BUILD = "project-build";
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_BUILD = "version-build";
  private static final String VERSION_XML = "version-xml";

  private static final String GCLOUD_CONFIG = "GCLOUD_CONFIG";
  private static final String APPENGINE_CONFIG = "APPENGINE_CONFIG";

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();
  private File appengineWebXml;

  private AbstractDeployMojo deployMojo;
  private AppEngineStandardDeployer appEngineStandardDeployer;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    deployMojo = new DeployMojo();
    deployMojo.sourceDirectory = tempFolder.newFolder("source");
    appengineWebXml = new File(tempFolder.newFolder("source", "WEB-INF"), "appengine-web.xml");
    appEngineStandardDeployer = new AppEngineStandardDeployer(deployMojo);
  }

  @Test
  public void testUpdateGcloudProperties_fromBuildConfig()
      throws IOException, MojoExecutionException {
    createAppEngineWebXml(true, true);
    deployMojo.version = VERSION_BUILD;
    deployMojo.project = PROJECT_BUILD;
    appEngineStandardDeployer.updateGcloudProperties();
    Assert.assertEquals(VERSION_BUILD, deployMojo.getVersion());
    Assert.assertEquals(PROJECT_BUILD, deployMojo.getProject());
  }

  @Test
  public void testUpdateGcloudProperties_fromAppengineWebXml()
      throws IOException, MojoExecutionException {
    createAppEngineWebXml(true, true);
    deployMojo.version = APPENGINE_CONFIG;
    deployMojo.project = APPENGINE_CONFIG;
    appEngineStandardDeployer.updateGcloudProperties();
    Assert.assertEquals(VERSION_XML, deployMojo.getVersion());
    Assert.assertEquals(PROJECT_XML, deployMojo.getProject());
  }

  @Test
  public void testUpdateGcloudProperties_fromGcloud() throws IOException, MojoExecutionException {
    createAppEngineWebXml(true, true);
    deployMojo.version = GCLOUD_CONFIG;
    deployMojo.project = GCLOUD_CONFIG;
    appEngineStandardDeployer.updateGcloudProperties();
    Assert.assertEquals(null, deployMojo.getVersion());
    Assert.assertEquals(null, deployMojo.getProject());
  }

  @Test
  public void testUpdateGcloudProperties_fromAppengineWebXmlNoApplication() throws IOException {
    createAppEngineWebXml(false, true);
    deployMojo.version = APPENGINE_CONFIG;
    deployMojo.project = APPENGINE_CONFIG;
    try {
      appEngineStandardDeployer.updateGcloudProperties();
      Assert.fail();
    } catch (MojoExecutionException ex) {
      Assert.assertEquals("<application> was not found in appengine-web.xml", ex.getMessage());
    }
  }

  @Test
  public void testUpdateGcloudProperties_fromAppengineWebXmlNoVersion() throws IOException {
    createAppEngineWebXml(true, false);
    deployMojo.version = APPENGINE_CONFIG;
    deployMojo.project = APPENGINE_CONFIG;
    try {
      appEngineStandardDeployer.updateGcloudProperties();
      Assert.fail();
    } catch (MojoExecutionException ex) {
      Assert.assertEquals("<version> was not found in appengine-web.xml", ex.getMessage());
    }
  }

  @Test
  public void testUpdateGcloudProperties_noProjectSet() throws IOException {
    createAppEngineWebXml(true, true);
    deployMojo.version = VERSION_BUILD;
    deployMojo.project = null;
    try {
      appEngineStandardDeployer.updateGcloudProperties();
      Assert.fail();
    } catch (MojoExecutionException ex) {
      Assert.assertEquals(
          "Deployment project must be defined or configured to read from system state\n"
              + "1. Set <project>my-project-name</project>\n"
              + "2. Set <project>"
              + APPENGINE_CONFIG
              + "</project> to use <application> from appengine-web.xml\n"
              + "3. Set <project>"
              + GCLOUD_CONFIG
              + "</project> to use project from gcloud config.",
          ex.getMessage());
    }
  }

  @Test
  public void testUpdateGcloudProperties_noVersionSet() throws IOException {
    createAppEngineWebXml(true, true);
    deployMojo.version = null;
    deployMojo.project = PROJECT_BUILD;
    try {
      appEngineStandardDeployer.updateGcloudProperties();
      Assert.fail();
    } catch (MojoExecutionException ex) {
      Assert.assertEquals(
          "Deployment version must be defined or configured to read from system state\n"
              + "1. Set <version>my-version</version>\n"
              + "2. Set <version>"
              + APPENGINE_CONFIG
              + "</version> to use <version> from appengine-web.xml\n"
              + "3. Set <version>"
              + GCLOUD_CONFIG
              + "</version> to use version from gcloud config.",
          ex.getMessage());
    }
  }

  private void createAppEngineWebXml(boolean withProject, boolean withVersion) throws IOException {
    appengineWebXml.createNewFile();
    Files.write(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">"
            + (withProject ? "<application>" + PROJECT_XML + "</application>" : "")
            + (withVersion ? "<version>" + VERSION_XML + "</version>" : "")
            + "</appengine-web-app>",
        appengineWebXml,
        Charsets.UTF_8);
  }
}
