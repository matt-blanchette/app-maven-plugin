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

import com.google.cloud.tools.appengine.api.AppEngineException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractAppEngineDeployer implements AppEngineDeployer {

  AbstractDeployMojo deployMojo;
  AppEngineStager stager;

  @Override
  public abstract void deployAll() throws MojoExecutionException, MojoFailureException;

  abstract void updateGcloudProperties() throws MojoExecutionException;

  @Override
  public void deploy() throws MojoFailureException, MojoExecutionException {
    stager.stage();
    deployMojo.deployables.clear();
    deployMojo.deployables.add(deployMojo.stagingDirectory);

    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deploy(deployMojo);
    } catch (AppEngineException ex) {
      throw new MojoFailureException(ex.getMessage(), ex);
    }
  }

  @Override
  public void deployCron() throws MojoFailureException, MojoExecutionException {
    stager.configureAppEngineDirectory();
    stager.stage();
    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deployCron(deployMojo);
    } catch (AppEngineException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void deployDispatch() throws MojoFailureException, MojoExecutionException {
    stager.configureAppEngineDirectory();
    stager.stage();
    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deployDispatch(deployMojo);
    } catch (AppEngineException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void deployDos() throws MojoFailureException, MojoExecutionException {
    stager.configureAppEngineDirectory();
    stager.stage();
    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deployDos(deployMojo);
    } catch (AppEngineException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void deployIndex() throws MojoFailureException, MojoExecutionException {
    stager.configureAppEngineDirectory();
    stager.stage();
    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deployIndex(deployMojo);
    } catch (AppEngineException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void deployQueue() throws MojoFailureException, MojoExecutionException {
    stager.configureAppEngineDirectory();
    stager.stage();
    try {
      updateGcloudProperties();
      deployMojo.getAppEngineFactory().deployment().deployQueue(deployMojo);
    } catch (AppEngineException ex) {
      throw new RuntimeException(ex);
    }
  }
}
