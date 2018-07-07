package maksimaleshko.intellij.plugin.rterminal;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RTerminalOptionsConfigurable implements SearchableConfigurable, Disposable {
    private RTerminalSettingsPanel panel;
    private final RTerminalProjectOptionsProvider projectOptionsProvider;
    private final RTerminalApplicationOptionsProvider applicationOptionsProvider;

    public RTerminalOptionsConfigurable(@NotNull Project project) {
        projectOptionsProvider = RTerminalProjectOptionsProvider.Companion.getInstance(project);
        applicationOptionsProvider = RTerminalApplicationOptionsProvider.Companion.getInstance();
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
        panel = new RTerminalSettingsPanel();
        return panel.createPanel(applicationOptionsProvider, projectOptionsProvider);
    }

    @Override
    public boolean isModified() {
        return panel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        panel.apply();
    }

    @Override
    public void reset() {
        panel.reset();
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        panel = null;
    }
}
