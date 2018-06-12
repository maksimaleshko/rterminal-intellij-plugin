/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package maksimaleshko.intellij.plugin.rterminal;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author traff
 */
public class RTerminalOptionsConfigurable implements SearchableConfigurable, Disposable {
  private RTerminalSettingsPanel myPanel;
  private final RTerminalOptionsProvider myOptionsProvider;
  private final RTerminalProjectOptionsProvider myProjectOptionsProvider;

  public RTerminalOptionsConfigurable(@NotNull Project project) {
    myOptionsProvider = RTerminalOptionsProvider.Companion.getInstance();
    myProjectOptionsProvider = RTerminalProjectOptionsProvider.Companion.getInstance(project);
  }

  @NotNull
  @Override
  public String getId() {
    return "rterminal";
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "RTerminal";
  }

  @Override
  public JComponent createComponent() {
    myPanel = new RTerminalSettingsPanel();
    return myPanel.createPanel(myOptionsProvider, myProjectOptionsProvider);
  }

  @Override
  public boolean isModified() {
    return myPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    myPanel.apply();
  }

  @Override
  public void reset() {
    myPanel.reset();
  }

  @Override
  public void disposeUIResources() {
    Disposer.dispose(this);
  }

  @Override
  public void dispose() {
    myPanel = null;
  }
}
