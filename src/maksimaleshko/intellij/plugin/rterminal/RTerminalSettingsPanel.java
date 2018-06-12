/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

import com.google.common.collect.Lists;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Arrays;

/**
 * @author traff
 */
public class RTerminalSettingsPanel {
  private JPanel myWholePanel;
  private TextFieldWithBrowseButton myStartDirectoryField;
  private TextFieldWithBrowseButton myProjectShellPathField;
  private JTextField myProjectTabNameTextField;
  private TextFieldWithBrowseButton myShellPathField;
  private JBCheckBox mySoundBellCheckBox;
  private JBCheckBox myCloseSessionCheckBox;
  private JBCheckBox myMouseReportCheckBox;
  private JTextField myTabNameTextField;
  private JBCheckBox myPasteOnMiddleButtonCheckBox;
  private JBCheckBox myCopyOnSelectionCheckBox;
  private JBCheckBox myOverrideIdeShortcuts;
  private JBCheckBox myShellIntegration;
  private JPanel myProjectSettingsPanel;
  private JPanel myGlobalSettingsPanel;
  private JPanel myConfigurablesPanel;
  private JBCheckBox myHighlightHyperlinks;
  private RTerminalOptionsProvider myOptionsProvider;
  private RTerminalProjectOptionsProvider myProjectOptionsProvider;

  private final java.util.List<UnnamedConfigurable> myConfigurables = Lists.newArrayList();

  public JComponent createPanel(@NotNull RTerminalOptionsProvider provider, @NotNull RTerminalProjectOptionsProvider projectOptionsProvider) {
    myOptionsProvider = provider;
    myProjectOptionsProvider = projectOptionsProvider;

    myProjectSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));
    myGlobalSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));

    FileChooserDescriptor fileChooserDescriptor;

    fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
    myStartDirectoryField.addBrowseFolderListener(
      "",
      "Starting directory",
      null,
      fileChooserDescriptor,
      TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    myStartDirectoryField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent e) {
        myStartDirectoryField.getTextField().setForeground(
                StringUtil.equals(myStartDirectoryField.getText(), myProjectOptionsProvider.getDefaultStartingDirectory()) ?
                        getDefaultValueColor() :
                        getChangedValueColor());
      }
    });

    fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
    myProjectShellPathField.addBrowseFolderListener(
            "",
            "Shell executable path",
            null,
            fileChooserDescriptor,
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    myProjectShellPathField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent e) {
        myProjectShellPathField.getTextField().setForeground(
                StringUtil.equals(myProjectShellPathField.getText(), myProjectOptionsProvider.getDefaultShellPath()) ?
                        getDefaultValueColor() :
                        getChangedValueColor());
      }
    });

    fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
    myShellPathField.addBrowseFolderListener(
            "",
            "Shell executable path",
            null,
            fileChooserDescriptor,
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    myShellPathField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent e) {
        myShellPathField.getTextField().setForeground(
                StringUtil.equals(myShellPathField.getText(), myOptionsProvider.getDefaultShellPath()) ?
                        getDefaultValueColor() :
                        getChangedValueColor());
      }
    });

    for (LocalTerminalCustomizer c : LocalTerminalCustomizer.EP_NAME.getExtensions()) {
      UnnamedConfigurable configurable = c.getConfigurable(projectOptionsProvider.getProject());
      if (configurable != null) {
        myConfigurables.add(configurable);
        JComponent component = configurable.createComponent();
        if (component != null) {
          myConfigurablesPanel.add(component, BorderLayout.CENTER);
        }
      }
    }

    return myWholePanel;
  }

  public boolean isModified() {
    return !Comparing.equal(myStartDirectoryField.getText(), StringUtil.notNullize(myProjectOptionsProvider.getStartingDirectory()))
           || !Comparing.equal(myProjectShellPathField.getText(), myProjectOptionsProvider.getShellPath())
           || !Comparing.equal(myProjectTabNameTextField.getText(), myProjectOptionsProvider.getTabName())
           || !Comparing.equal(myShellPathField.getText(), myOptionsProvider.getShellPath())
           || !Comparing.equal(myTabNameTextField.getText(), myOptionsProvider.getTabName())
           || (myCloseSessionCheckBox.isSelected() != myOptionsProvider.closeSessionOnLogout())
           || (myMouseReportCheckBox.isSelected() != myOptionsProvider.enableMouseReporting())
           || (mySoundBellCheckBox.isSelected() != myOptionsProvider.audibleBell())
           || (myCopyOnSelectionCheckBox.isSelected() != myOptionsProvider.copyOnSelection())
           || (myPasteOnMiddleButtonCheckBox.isSelected() != myOptionsProvider.pasteOnMiddleMouseButton())
           || (myOverrideIdeShortcuts.isSelected() != myOptionsProvider.overrideIdeShortcuts())
           || (myShellIntegration.isSelected() != myOptionsProvider.shellIntegration())
           || (myHighlightHyperlinks.isSelected() != myOptionsProvider.highlightHyperlinks()) ||
           myConfigurables.stream().anyMatch(UnnamedConfigurable::isModified);
  }

  public void apply() {
    myProjectOptionsProvider.setStartingDirectory(myStartDirectoryField.getText());
    myProjectOptionsProvider.setShellPath(myProjectShellPathField.getText());
    myProjectOptionsProvider.setTabName(myProjectTabNameTextField.getText());
    myOptionsProvider.setShellPath(myShellPathField.getText());
    myOptionsProvider.setTabName(myTabNameTextField.getText());
    myOptionsProvider.setCloseSessionOnLogout(myCloseSessionCheckBox.isSelected());
    myOptionsProvider.setReportMouse(myMouseReportCheckBox.isSelected());
    myOptionsProvider.setSoundBell(mySoundBellCheckBox.isSelected());
    myOptionsProvider.setCopyOnSelection(myCopyOnSelectionCheckBox.isSelected());
    myOptionsProvider.setPasteOnMiddleMouseButton(myPasteOnMiddleButtonCheckBox.isSelected());
    myOptionsProvider.setOverrideIdeShortcuts(myOverrideIdeShortcuts.isSelected());
    myOptionsProvider.setShellIntegration(myShellIntegration.isSelected());
    myOptionsProvider.setHighlightHyperlinks(myHighlightHyperlinks.isSelected());
    myConfigurables.forEach(c -> {
      try {
        c.apply();
      }
      catch (ConfigurationException e) {
        //pass
      }
    });
  }

  public void reset() {
    myStartDirectoryField.setText(myProjectOptionsProvider.getStartingDirectory());
    myProjectShellPathField.setText(myProjectOptionsProvider.getShellPath());
    myProjectTabNameTextField.setText(myProjectOptionsProvider.getTabName());
    myShellPathField.setText(myOptionsProvider.getShellPath());
    myTabNameTextField.setText(myOptionsProvider.getTabName());
    myCloseSessionCheckBox.setSelected(myOptionsProvider.closeSessionOnLogout());
    myMouseReportCheckBox.setSelected(myOptionsProvider.enableMouseReporting());
    mySoundBellCheckBox.setSelected(myOptionsProvider.audibleBell());
    myCopyOnSelectionCheckBox.setSelected(myOptionsProvider.copyOnSelection());
    myPasteOnMiddleButtonCheckBox.setSelected(myOptionsProvider.pasteOnMiddleMouseButton());
    myOverrideIdeShortcuts.setSelected(myOptionsProvider.overrideIdeShortcuts());
    myShellIntegration.setSelected(myOptionsProvider.shellIntegration());
    myHighlightHyperlinks.setSelected(myOptionsProvider.highlightHyperlinks());
    myConfigurables.forEach(UnnamedConfigurable::reset);
  }

  public Color getDefaultValueColor() {
    return findColorByKey("TextField.inactiveForeground", "nimbusDisabledText");
  }

  @NotNull
  private static Color findColorByKey(String... colorKeys) {
    Color c = null;
    for (String key : colorKeys) {
      c = UIManager.getColor(key);
      if (c != null) {
        break;
      }
    }

    assert c != null : "Can't find color for keys " + Arrays.toString(colorKeys);
    return c;
  }

  public Color getChangedValueColor() {
    return findColorByKey("TextField.foreground");
  }
}
