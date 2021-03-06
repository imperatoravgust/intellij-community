/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.tasks.context;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.Accessor;
import com.intellij.util.xmlb.SerializationFilter;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebuggerManagerImpl;
import com.intellij.xdebugger.impl.XDebuggerWatchesManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry Avdeev
 */
public class XDebuggerWatchesProvider extends WorkingContextProvider {

  private final XDebuggerWatchesManager myWatchesManager;

  public XDebuggerWatchesProvider(XDebuggerManager xDebuggerManager) {
    myWatchesManager = ((XDebuggerManagerImpl)xDebuggerManager).getWatchesManager();
  }

  @NotNull
  @Override
  public String getId() {
    return "watches";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Debugger watches";
  }

  @Override
  public void saveContext(Element toElement) throws WriteExternalException {
    XDebuggerWatchesManager.WatchesManagerState state = myWatchesManager.getState();
    Element serialize = XmlSerializer.serialize(state, new SerializationFilter() {
      @Override
      public boolean accepts(@NotNull Accessor accessor, Object bean) {
        return accessor.read(bean) != null;
      }
    });
    toElement.addContent(serialize.removeContent());
  }

  @Override
  public void loadContext(Element fromElement) throws InvalidDataException {
    XDebuggerWatchesManager.WatchesManagerState state =
      XmlSerializer.deserialize(fromElement, XDebuggerWatchesManager.WatchesManagerState.class);
    myWatchesManager.loadState(state);

  }

  @Override
  public void clearContext() {
    myWatchesManager.loadState(null);
  }
}
