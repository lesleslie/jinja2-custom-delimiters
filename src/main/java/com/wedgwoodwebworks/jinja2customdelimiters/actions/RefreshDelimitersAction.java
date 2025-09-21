package com.wedgwoodwebworks.jinja2delimiters.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import org.jetbrains.annotations.NotNull;

public class RefreshDelimitersAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ApplicationManager.getApplication().runWriteAction(() -> {
            // Refresh all open Jinja2 files
            FileEditorManager editorManager = FileEditorManager.getInstance(project);
            VirtualFile[] openFiles = editorManager.getOpenFiles();

            for (VirtualFile file : openFiles) {
                if (file.getFileType() instanceof CustomJinja2FileType) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile != null) {
                        // Force re-parsing with new delimiters
                        psiFile.subtreeChanged();
                    }
                }
            }
        });
    }
}
