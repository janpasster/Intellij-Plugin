package com.thoughtworks.gauge.execution;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.SpecFile;

public class GaugeExecutionProducer extends RunConfigurationProducer {


    public GaugeExecutionProducer() {
        super(GaugeRunTaskConfigurationType.getInstance());
    }

    protected GaugeExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        if (context.getPsiLocation() == null) {
            return false;
        }

        PsiFile file = context.getPsiLocation().getContainingFile();
        if (!(file instanceof SpecFile)) {
            return false;
        }

        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            String name = file.getVirtualFile().getCanonicalPath();

            configuration.setName("Execute " + file.getName());
            ((GaugeRunConfiguration) configuration).setSpecsToExecute(name);
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;

    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if (configuration.getType() != getConfigurationType())
            return false;
        final Location location = context.getLocation();
        if (location == null || location.getVirtualFile() == null) {
            return false;
        }

        final String specsToExecute = ((GaugeRunConfiguration) configuration).getSpecsToExecute();
        return (specsToExecute.contains(location.getVirtualFile().getName()));
    }
}