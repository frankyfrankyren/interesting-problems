package com.cts.reny.interview.scenario.three;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 'Main' class for scenario 3
 */
public class IdBasedFolderToToPathConverter {
    public static final String ROOT_FOLDER_ID = "root";

    private final IdBasedFileSystemSummary idBasedFileSystemSummary;
    private final IdBasedFolderNameTruncator idBasedFolderNameTruncator;
    private final int charactersLimit;
    private Map<String, String> folderNameOverrides = new ConcurrentHashMap<>();

    public IdBasedFolderToToPathConverter(IdBasedFolder[] allFolders,
                                          IdBasedFolderNameTruncator idBasedFolderNameTruncator,
                                          int charactersLimit) {
        idBasedFileSystemSummary = createFoldersSummaryFrom(allFolders);
        this.idBasedFolderNameTruncator = idBasedFolderNameTruncator;
        this.charactersLimit = charactersLimit;
    }

    public String convertToAbsolutePath(String id) {
        List<String> absolutePathIds = new ArrayList<>(idBasedFileSystemSummary.getFolder(id).getParentIds());
        absolutePathIds.add(id);

        final List<String> folderNames = absolutePathIds.stream()
                .map(this::computeClashFreeFolderName)
                .collect(Collectors.toList());

        final String path = toPath(folderNames);

        if (path.length() <= charactersLimit) {
            return path;
        }

        return computeTruncatedAbsolutePath(absolutePathIds, folderNames);
    }

    private String computeTruncatedAbsolutePath(List<String> absolutePathIds, List<String> folderNames) {
        int totalLength = folderNames.stream().mapToInt(String::length).map(length -> length + 1).sum();
        List<String> truncatedFolderNames = new ArrayList<>(folderNames);

        for (int index = truncatedFolderNames.size() - 1; index >= 0; index--) {
            final String folderId = absolutePathIds.get(index);
            final String truncatedFolderName = getTruncatedNameFor(folderId);
            final String previousFolderName = folderNames.get(index);

            if (truncatedFolderName.length() < previousFolderName.length()) {
                saveOverride(folderId, truncatedFolderName);
                truncatedFolderNames.set(index, truncatedFolderName);
                totalLength = totalLength - previousFolderName.length() + truncatedFolderName.length();
            }

            if (totalLength <= charactersLimit) {
                return toPath(truncatedFolderNames);
            }
        }

        throw new RuntimeException(String.format("unable to truncate '%s' down to size %d", toPath(truncatedFolderNames), charactersLimit));
    }

    private void saveOverride(String folderId, String folderName) {
        folderNameOverrides.put(folderId, folderName);
    }

    private String getTruncatedNameFor(String folderId) {
        if (folderId.equals(ROOT_FOLDER_ID)) {
            return "";
        }

        return idBasedFolderNameTruncator.truncateNameFor(idBasedFileSystemSummary.getFolder(folderId));
    }

    private String toPath(List<String> folderNames) {
        if (folderNames.size() == 1) {
            return "/";
        }

        return String.join("/", folderNames);
    }

    private String computeClashFreeFolderName(String folderId) {
        if (folderId.equals(ROOT_FOLDER_ID)) {
            return "";
        }

        if (folderNameOverrides.containsKey(folderId)) {
            return folderNameOverrides.get(folderId);
        }

        String folderName = idBasedFileSystemSummary.getFolderName(folderId);

        if (idBasedFileSystemSummary.hasNameClash(folderId)) {
            folderName = getTruncatedNameFor(folderId);
            saveOverride(folderId, folderName);
        }

        return folderName;
    }

    private IdBasedFileSystemSummary createFoldersSummaryFrom(IdBasedFolder[] allFolders) {
        List<List<IdBasedFolder>> folderStructure = new ArrayList<>();
        Map<String, Integer> depthByFolderId = new HashMap<>();
        Map<String, IdBasedFolder> foldersById = new HashMap<>();

        for (IdBasedFolder folder : allFolders) {
            final int depth = folder.getParentIds().size();

            expandFolderStructureCapacity(folderStructure, depth);

            folderStructure.get(depth).add(folder);
            depthByFolderId.put(folder.getId(), depth);
            foldersById.put(folder.getId(), folder);
        }

        return new IdBasedFileSystemSummary(folderStructure, depthByFolderId, foldersById);
    }

    private void expandFolderStructureCapacity(List<List<IdBasedFolder>> folderStructure, int depth) {
        for (int structureSize = folderStructure.size(); structureSize <= depth; structureSize = folderStructure.size()) {
            folderStructure.add(new ArrayList<>());
        }
    }

    private class IdBasedFileSystemSummary {
        private final List<List<IdBasedFolder>> folderStructure;
        private final Map<String, Integer> depthByFolderId;
        private final Map<String, IdBasedFolder> foldersById;

        public IdBasedFileSystemSummary(
                List<List<IdBasedFolder>> folderStructure,
                Map<String, Integer> depthByFolderId,
                Map<String, IdBasedFolder> foldersById) {
            this.folderStructure = folderStructure;
            this.depthByFolderId = depthByFolderId;
            this.foldersById = foldersById;
        }

        private void verifyFolderIdExists(String folderId) {
            if (!foldersById.containsKey(folderId)) {
                throw new RuntimeException(String.format("Unable to find folder corresponding to '%s'", folderId));
            }
        }

        public boolean hasNameClash(String folderId) {
            verifyFolderIdExists(folderId);

            final String folderName = getFolderName(folderId);
            final int depthIndex = depthByFolderId.get(folderId);
            final List<IdBasedFolder> foldersByDepth = folderStructure.get(depthIndex);

            return foldersByDepth.stream()
                    .map(Folder::getName)
                    .filter(folderName::equals)
                    .count() > 1;
        }

        public String getFolderName(String folderId) {
            verifyFolderIdExists(folderId);

            return getFolder(folderId).getName();
        }

        public IdBasedFolder getFolder(String folderId) {
            verifyFolderIdExists(folderId);

            return foldersById.get(folderId);
        }
    }
}
