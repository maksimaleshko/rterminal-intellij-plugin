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
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public class RTerminalSettingsPanel {
    private TextFieldWithBrowseButton projectStartingDirectory;
    private TextFieldWithBrowseButton projectShellPath;
    private JTextField projectTabName;
    private TextFieldWithBrowseButton applicationShellPath;
    private JTextField applicationTabName;
    private JBCheckBox applicationSoundBell;
    private JBCheckBox applicationCloseSessionOnLogout;
    private JBCheckBox applicationReportMouse;
    private JBCheckBox applicationCopyOnSelection;
    private JBCheckBox applicationPasteOnMiddleMouseButton;
    private JBCheckBox applicationOverrideIdeShortcuts;
    private JBCheckBox applicationShellIntegration;
    private JBCheckBox applicationHighlightHyperlinks;
    private JPanel projectSettingsPanel;
    private JPanel applicationSettingsPanel;
    private JPanel configurablesPanel;
    private JPanel panel;
    private RTerminalApplicationOptionsProvider applicationOptionsProvider;
    private RTerminalProjectOptionsProvider projectOptionsProvider;

    private final java.util.List<UnnamedConfigurable> configurables = Lists.newArrayList();

    public JComponent createPanel(@NotNull RTerminalApplicationOptionsProvider applicationOptionsProvider, @NotNull RTerminalProjectOptionsProvider projectOptionsProvider) {
        this.applicationOptionsProvider = applicationOptionsProvider;
        this.projectOptionsProvider = projectOptionsProvider;

        projectSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Project settings"));
        applicationSettingsPanel.setBorder(IdeBorderFactory.createTitledBorder("Application settings"));

        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        projectStartingDirectory.addBrowseFolderListener(
                "",
                "Starting directory",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
        projectStartingDirectory.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                projectStartingDirectory.getTextField().setForeground(
                        StringUtil.equals(projectStartingDirectory.getText(), RTerminalSettingsPanel.this.projectOptionsProvider.getDefaultStartingDirectory()) ?
                                getDefaultValueColor() :
                                getChangedValueColor());
            }
        });

        fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        projectShellPath.addBrowseFolderListener(
                "",
                "Shell executable path",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
        projectShellPath.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                projectShellPath.getTextField().setForeground(
                        StringUtil.equals(projectShellPath.getText(), RTerminalSettingsPanel.this.projectOptionsProvider.getDefaultShellPath()) ?
                                getDefaultValueColor() :
                                getChangedValueColor());
            }
        });

        fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        applicationShellPath.addBrowseFolderListener(
                "",
                "Shell executable path",
                null,
                fileChooserDescriptor,
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
        applicationShellPath.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                applicationShellPath.getTextField().setForeground(
                        StringUtil.equals(applicationShellPath.getText(), applicationOptionsProvider.getDefaultShellPath()) ?
                                getDefaultValueColor() :
                                getChangedValueColor());
            }
        });

        for (LocalTerminalCustomizer customizer : LocalTerminalCustomizer.EP_NAME.getExtensions()) {
            UnnamedConfigurable configurable = customizer.getConfigurable(projectOptionsProvider.getProject());
            if (configurable != null) {
                configurables.add(configurable);
                JComponent component = configurable.createComponent();
                if (component != null) {
                    configurablesPanel.add(component, BorderLayout.CENTER);
                }
            }
        }

        return panel;
    }

    public boolean isModified() {
        return  !Comparing.equal(projectStartingDirectory.getText(), StringUtil.notNullize(projectOptionsProvider.getStartingDirectory())) ||
                !Comparing.equal(projectShellPath.getText(), projectOptionsProvider.getShellPath()) ||
                !Comparing.equal(projectTabName.getText(), projectOptionsProvider.getTabName()) ||
                !Comparing.equal(applicationShellPath.getText(), applicationOptionsProvider.getShellPath()) ||
                !Comparing.equal(applicationTabName.getText(), applicationOptionsProvider.getTabName()) ||
                (applicationSoundBell.isSelected() != applicationOptionsProvider.getSoundBell()) ||
                (applicationCloseSessionOnLogout.isSelected() != applicationOptionsProvider.getCloseSessionOnLogout()) ||
                (applicationReportMouse.isSelected() != applicationOptionsProvider.getReportMouse()) ||
                (applicationCopyOnSelection.isSelected() != applicationOptionsProvider.getCopyOnSelection()) ||
                (applicationPasteOnMiddleMouseButton.isSelected() != applicationOptionsProvider.getPasteOnMiddleMouseButton()) ||
                (applicationOverrideIdeShortcuts.isSelected() != applicationOptionsProvider.getOverrideIdeShortcuts()) ||
                (applicationShellIntegration.isSelected() != applicationOptionsProvider.getShellIntegration()) ||
                (applicationHighlightHyperlinks.isSelected() != applicationOptionsProvider.getHighlightHyperlinks()) ||
                configurables.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    public void apply() {
        projectOptionsProvider.setStartingDirectory(projectStartingDirectory.getText());
        projectOptionsProvider.setShellPath(projectShellPath.getText());
        projectOptionsProvider.setTabName(projectTabName.getText());
        applicationOptionsProvider.setShellPath(applicationShellPath.getText());
        applicationOptionsProvider.setTabName(applicationTabName.getText());
        applicationOptionsProvider.setSoundBell(applicationSoundBell.isSelected());
        applicationOptionsProvider.setCloseSessionOnLogout(applicationCloseSessionOnLogout.isSelected());
        applicationOptionsProvider.setReportMouse(applicationReportMouse.isSelected());
        applicationOptionsProvider.setCopyOnSelection(applicationCopyOnSelection.isSelected());
        applicationOptionsProvider.setPasteOnMiddleMouseButton(applicationPasteOnMiddleMouseButton.isSelected());
        applicationOptionsProvider.setOverrideIdeShortcuts(applicationOverrideIdeShortcuts.isSelected());
        applicationOptionsProvider.setShellIntegration(applicationShellIntegration.isSelected());
        applicationOptionsProvider.setHighlightHyperlinks(applicationHighlightHyperlinks.isSelected());
        configurables.forEach(c -> {
            try {
                c.apply();
            } catch (ConfigurationException e) {
                //pass
            }
        });
    }

    public void reset() {
        projectStartingDirectory.setText(projectOptionsProvider.getStartingDirectory());
        projectShellPath.setText(projectOptionsProvider.getShellPath());
        projectTabName.setText(projectOptionsProvider.getTabName());
        applicationShellPath.setText(applicationOptionsProvider.getShellPath());
        applicationTabName.setText(applicationOptionsProvider.getTabName());
        applicationSoundBell.setSelected(applicationOptionsProvider.getSoundBell());
        applicationCloseSessionOnLogout.setSelected(applicationOptionsProvider.getCloseSessionOnLogout());
        applicationReportMouse.setSelected(applicationOptionsProvider.getReportMouse());
        applicationCopyOnSelection.setSelected(applicationOptionsProvider.getCopyOnSelection());
        applicationPasteOnMiddleMouseButton.setSelected(applicationOptionsProvider.getPasteOnMiddleMouseButton());
        applicationOverrideIdeShortcuts.setSelected(applicationOptionsProvider.getOverrideIdeShortcuts());
        applicationShellIntegration.setSelected(applicationOptionsProvider.getShellIntegration());
        applicationHighlightHyperlinks.setSelected(applicationOptionsProvider.getHighlightHyperlinks());
        configurables.forEach(UnnamedConfigurable::reset);
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
