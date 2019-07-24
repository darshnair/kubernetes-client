/*
 * Copyright (C) 2018 Red Hat inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.tekton.mock;

import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import io.fabric8.mockwebserver.dsl.MockServerExpectation;
import io.fabric8.tekton.client.TektonClient;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.rules.ExternalResource;

import java.util.HashMap;
import java.util.Queue;

public class TektonServer extends ExternalResource {

  protected TektonMockServer mock;
  private TektonClient client;

  private boolean https;
  private boolean curdMode;

  public TektonServer() {
    this(true, false);
  }

  public TektonServer(boolean https) {
    this(https, false);
  }

  public TektonServer(boolean https, boolean curdMode) {
    this.https = https;
    this.curdMode = curdMode;
  }

  public void before() {
    mock = curdMode
      ? new TektonMockServer(new Context(), new MockWebServer(), new HashMap<ServerRequest, Queue<ServerResponse>>(), new KubernetesCrudDispatcher(), true)
      : new TektonMockServer(https);
    mock.init();
    client = mock.createTekton();
  }

  public void after() {
    mock.destroy();
    client.close();
  }


  public TektonClient getTektonClient() {
    return client;
  }


  public MockServerExpectation expect() {
    return mock.expect();
  }

  @Deprecated
  public <T> void expectAndReturnAsJson(String path, int code, T body) {
    expect().withPath(path).andReturn(code, body).always();
  }

  @Deprecated
  public void expectAndReturnAsString(String path, int code, String body) {
    expect().withPath(path).andReturn(code, body).always();
  }

  public MockWebServer getMockServer() {
    return mock.getServer();
  }
}
