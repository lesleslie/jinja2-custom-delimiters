package com.wedgwoodwebworks.jinja2delimiters.performance;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ConcurrentFactoryMap;
import com.wedgwoodwebworks.jinja2delimiters.lang.CustomJinja2FileType;
import com.wedgwoodwebworks.jinja2delimiters.psi.CustomJinja2File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for caching Jinja2 template analysis and improving performance
 */
@Service(Service.Level.PROJECT)
public final class Jinja2CachingService {

    private static final Logger LOG = Logger.getInstance(Jinja2CachingService.class);

    // Cache for template variables
    private final ConcurrentMap<VirtualFile, Set<String>> variableCache = new ConcurrentHashMap<>();

    // Cache for macro definitions
    private final ConcurrentMap<VirtualFile, Set<String>> macroCache = new ConcurrentHashMap<>();

    // Cache for template dependencies (extends/includes)
    private final ConcurrentMap<VirtualFile, Set<VirtualFile>> dependencyCache = new ConcurrentHashMap<>();

    // Cache for parsed AST metadata
    private final ConcurrentMap<VirtualFile, TemplateMetadata> metadataCache = 
        ConcurrentFactoryMap.createMap(this::analyzeTemplate);

    // Cache timestamp tracking for invalidation
    private final ConcurrentMap<VirtualFile, Long> cacheTimestamps = new ConcurrentHashMap<>();

    public static Jinja2CachingService getInstance(@NotNull Project project) {
        return project.getService(Jinja2CachingService.class);
    }

    /**
     * Get variables defined in a template (cached)
     */
    @NotNull
    public Set<String> getTemplateVariables(@NotNull VirtualFile file, @NotNull Project project) {
        if (!isJinja2File(file)) {
            return Set.of();
        }

        return variableCache.computeIfAbsent(file, f -> {
            LOG.debug("Analyzing variables for template: " + file.getName());
            return analyzeTemplateVariables(f, project);
        });
    }

    /**
     * Get macros defined in a template (cached)
     */
    @NotNull
    public Set<String> getTemplateMacros(@NotNull VirtualFile file, @NotNull Project project) {
        if (!isJinja2File(file)) {
            return Set.of();
        }

        return macroCache.computeIfAbsent(file, f -> {
            LOG.debug("Analyzing macros for template: " + file.getName());
            return analyzeTemplateMacros(f, project);
        });
    }

    /**
     * Get template dependencies (extends/includes) - cached
     */
    @NotNull
    public Set<VirtualFile> getTemplateDependencies(@NotNull VirtualFile file, @NotNull Project project) {
        if (!isJinja2File(file)) {
            return Set.of();
        }

        return dependencyCache.computeIfAbsent(file, f -> {
            LOG.debug("Analyzing dependencies for template: " + file.getName());
            return analyzeTemplateDependencies(f, project);
        });
    }

    /**
     * Get comprehensive template metadata (cached)
     */
    @NotNull
    public TemplateMetadata getTemplateMetadata(@NotNull VirtualFile file) {
        if (!isJinja2File(file)) {
            return TemplateMetadata.EMPTY;
        }

        // Check if cache is still valid
        long fileTimestamp = file.getTimeStamp();
        Long cachedTimestamp = cacheTimestamps.get(file);

        if (cachedTimestamp == null || cachedTimestamp < fileTimestamp) {
            // Cache is invalid, remove and re-analyze
            invalidateFile(file);
            cacheTimestamps.put(file, fileTimestamp);
        }

        return metadataCache.get(file);
    }

    /**
     * Invalidate cache for a specific file
     */
    public void invalidateFile(@NotNull VirtualFile file) {
        LOG.debug("Invalidating cache for file: " + file.getName());
        variableCache.remove(file);
        macroCache.remove(file);
        dependencyCache.remove(file);
        metadataCache.remove(file);
        cacheTimestamps.remove(file);
    }

    /**
     * Clear all caches (useful for settings changes)
     */
    public void clearAllCaches() {
        LOG.info("Clearing all Jinja2 template caches");
        variableCache.clear();
        macroCache.clear();
        dependencyCache.clear();
        metadataCache.clear();
        cacheTimestamps.clear();
    }

    /**
     * Get cache statistics for monitoring
     */
    @NotNull
    public CacheStatistics getCacheStatistics() {
        return new CacheStatistics(
            variableCache.size(),
            macroCache.size(),
            dependencyCache.size(),
            metadataCache.size()
        );
    }

    // Private analysis methods

    private Set<String> analyzeTemplateVariables(@NotNull VirtualFile file, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof CustomJinja2File)) {
            return Set.of();
        }

        // Use visitor pattern to collect variables
        Jinja2VariableCollector collector = new Jinja2VariableCollector();
        psiFile.accept(collector);
        return collector.getVariables();
    }

    private Set<String> analyzeTemplateMacros(@NotNull VirtualFile file, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof CustomJinja2File)) {
            return Set.of();
        }

        Jinja2MacroCollector collector = new Jinja2MacroCollector();
        psiFile.accept(collector);
        return collector.getMacros();
    }

    private Set<VirtualFile> analyzeTemplateDependencies(@NotNull VirtualFile file, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof CustomJinja2File)) {
            return Set.of();
        }

        Jinja2DependencyCollector collector = new Jinja2DependencyCollector(project);
        psiFile.accept(collector);
        return collector.getDependencies();
    }

    private TemplateMetadata analyzeTemplate(@NotNull VirtualFile file) {
        LOG.debug("Performing full analysis for template: " + file.getName());

        // This would be called by the ConcurrentFactoryMap when needed
        Project project = getProjectForFile(file);
        if (project == null) {
            return TemplateMetadata.EMPTY;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof CustomJinja2File)) {
            return TemplateMetadata.EMPTY;
        }

        // Perform comprehensive analysis
        Jinja2TemplateAnalyzer analyzer = new Jinja2TemplateAnalyzer();
        psiFile.accept(analyzer);

        return analyzer.getMetadata();
    }

    @Nullable
    private Project getProjectForFile(@NotNull VirtualFile file) {
        // Find project that contains this file
        Project[] projects = com.intellij.openapi.project.ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (project.getBaseDir() != null && 
                com.intellij.openapi.vfs.VfsUtilCore.isAncestor(project.getBaseDir(), file, true)) {
                return project;
            }
        }
        return null;
    }

    private boolean isJinja2File(@NotNull VirtualFile file) {
        return file.getFileType() instanceof CustomJinja2FileType;
    }

    // Data classes

    public static class TemplateMetadata {
        public static final TemplateMetadata EMPTY = new TemplateMetadata(Set.of(), Set.of(), Set.of(), Set.of(), 0);

        private final Set<String> variables;
        private final Set<String> macros;
        private final Set<String> blocks;
        private final Set<String> filters;
        private final int complexity;

        public TemplateMetadata(@NotNull Set<String> variables,
                              @NotNull Set<String> macros,
                              @NotNull Set<String> blocks,
                              @NotNull Set<String> filters,
                              int complexity) {
            this.variables = variables;
            this.macros = macros;
            this.blocks = blocks;
            this.filters = filters;
            this.complexity = complexity;
        }

        public Set<String> getVariables() { return variables; }
        public Set<String> getMacros() { return macros; }
        public Set<String> getBlocks() { return blocks; }
        public Set<String> getFilters() { return filters; }
        public int getComplexity() { return complexity; }
    }

    public static class CacheStatistics {
        private final int variableCacheSize;
        private final int macroCacheSize;
        private final int dependencyCacheSize;
        private final int metadataCacheSize;

        public CacheStatistics(int variableCacheSize, int macroCacheSize, 
                             int dependencyCacheSize, int metadataCacheSize) {
            this.variableCacheSize = variableCacheSize;
            this.macroCacheSize = macroCacheSize;
            this.dependencyCacheSize = dependencyCacheSize;
            this.metadataCacheSize = metadataCacheSize;
        }

        public int getVariableCacheSize() { return variableCacheSize; }
        public int getMacroCacheSize() { return macroCacheSize; }
        public int getDependencyCacheSize() { return dependencyCacheSize; }
        public int getMetadataCacheSize() { return metadataCacheSize; }

        public int getTotalCacheSize() {
            return variableCacheSize + macroCacheSize + dependencyCacheSize + metadataCacheSize;
        }

        @Override
        public String toString() {
            return String.format("CacheStatistics{variables=%d, macros=%d, dependencies=%d, metadata=%d, total=%d}",
                variableCacheSize, macroCacheSize, dependencyCacheSize, metadataCacheSize, getTotalCacheSize());
        }
    }

    // Helper visitor classes (simplified implementations)

    private static class Jinja2VariableCollector extends com.intellij.psi.PsiElementVisitor {
        private final Set<String> variables = ConcurrentHashMap.newKeySet();

        public Set<String> getVariables() {
            return variables;
        }

        // Implementation would visit PSI elements and collect variable names
    }

    private static class Jinja2MacroCollector extends com.intellij.psi.PsiElementVisitor {
        private final Set<String> macros = ConcurrentHashMap.newKeySet();

        public Set<String> getMacros() {
            return macros;
        }

        // Implementation would visit PSI elements and collect macro names
    }

    private static class Jinja2DependencyCollector extends com.intellij.psi.PsiElementVisitor {
        private final Set<VirtualFile> dependencies = ConcurrentHashMap.newKeySet();
        private final Project project;

        public Jinja2DependencyCollector(Project project) {
            this.project = project;
        }

        public Set<VirtualFile> getDependencies() {
            return dependencies;
        }

        // Implementation would visit PSI elements and collect template dependencies
    }

    private static class Jinja2TemplateAnalyzer extends com.intellij.psi.PsiElementVisitor {
        private final Set<String> variables = ConcurrentHashMap.newKeySet();
        private final Set<String> macros = ConcurrentHashMap.newKeySet();
        private final Set<String> blocks = ConcurrentHashMap.newKeySet();
        private final Set<String> filters = ConcurrentHashMap.newKeySet();
        private int complexity = 0;

        public TemplateMetadata getMetadata() {
            return new TemplateMetadata(variables, macros, blocks, filters, complexity);
        }

        // Implementation would perform comprehensive template analysis
    }
}
