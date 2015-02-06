// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameHandler;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.NotNull;

public class CustomRenameHandler implements RenameHandler {

    private PsiElement psiElement;
    private Editor editor;

    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        this.editor = editor;
        if (element == null) {
            if (editor == null) return false;
            int offset = editor.getCaretModel().getOffset();
            if (offset > 0 && offset == editor.getDocument().getTextLength()) offset--;
            PsiFile data = CommonDataKeys.PSI_FILE.getData(dataContext);
            if (data == null) return false;
            psiElement = getStepElement(data.findElementAt(offset));
            return psiElement != null && psiElement.getClass().equals(SpecStepImpl.class);
        }
        return CommonDataKeys.PROJECT.getData(dataContext) != null && (element.toString().equals("PsiAnnotation") ||
                element.getClass().equals(ConceptStepImpl.class) || element.getClass().equals(SpecStepImpl.class));
    }

    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (element == null) element = psiElement;
        psiElement = element;
        String text = element.toString();
        //Finding text from annotation
        if (element.toString().equals("PsiAnnotation")) {
            if (element.getChildren()[2].getChildren()[1].getChildren()[0].getChildren().length == 1)
                text = element.getChildren()[2].getChildren()[1].getText().substring(1, element.getChildren()[2].getChildren()[1].getText().length() - 1);
            else {
                Messages.showWarningDialog("Refactoring for steps having aliases are not supported", "Warning");
                return;
            }
        } else if (element.getClass().equals(SpecStepImpl.class)) {
            text = ((SpecStepImpl) element).getStepValue().getStepAnnotationText();
        }
        Messages.showInputDialog(project, String.format("Refactoring \"%s\" to : ", text), "Refactor", Messages.getInformationIcon(), text,
                new RenameInputValidator(project, this.editor, text, this.psiElement));

    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        invoke(project, null, null, dataContext);
    }

    private PsiElement getStepElement(PsiElement selectedElement) {
        if (selectedElement.getClass().equals(SpecStepImpl.class))
            return selectedElement;
        if (selectedElement.getParent() == null) return null;
        return getStepElement(selectedElement.getParent());
    }
}