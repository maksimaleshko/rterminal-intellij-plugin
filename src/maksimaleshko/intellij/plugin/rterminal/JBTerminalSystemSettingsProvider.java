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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.terminal.JBTerminalSystemSettingsProviderBase;
import com.jediterm.pty.PtyProcessTtyConnector;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TtyConnector;
import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public class JBTerminalSystemSettingsProvider extends JBTerminalSystemSettingsProviderBase {
  @NotNull
  protected final Project myProject;

  public JBTerminalSystemSettingsProvider(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public boolean shouldCloseTabOnLogout(TtyConnector ttyConnector) {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getCloseSessionOnLogout();
  }

  @Override
  public String tabName(TtyConnector ttyConnector, String sessionName) { //for local terminal use name from settings
    if (ttyConnector instanceof PtyProcessTtyConnector) {
      String tabName = RTerminalProjectOptionsProvider.Companion.getInstance(myProject).getTabName();
      if (StringUtil.isEmptyOrSpaces(tabName)) {
        tabName = RTerminalApplicationOptionsProvider.Companion.getInstance().getTabName();
      }

      return tabName;
    }
    else {
      return sessionName;
    }
  }


  @Override
  public boolean audibleBell() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getSoundBell();
  }

  @Override
  public boolean enableMouseReporting() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getReportMouse();
  }

  @Override
  public boolean copyOnSelect() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getCopyOnSelection();
  }

  @Override
  public boolean pasteOnMiddleMouseClick() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getPasteOnMiddleMouseButton();
  }

  @Override
  public boolean forceActionOnMouseReporting() {
    return true;
  }

  @Override
  public boolean overrideIdeShortcuts() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getOverrideIdeShortcuts();
  }

  @Override
  public HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode() {
    return RTerminalApplicationOptionsProvider.Companion.getInstance().getHighlightHyperlinks()
           ? HyperlinkStyle.HighlightMode.ALWAYS
           : HyperlinkStyle.HighlightMode.HOVER;
  }
}
