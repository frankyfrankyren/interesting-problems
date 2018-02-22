package com.cts.reny.interview.scenario.three;

public class IdBasedFolderNameTruncator implements FolderNameTruncator<IdBasedFolder> {
    private final int originalNameLimit;
    private final String delimiter;

    /**
     *
     * @param originalNameLimit character limit for the original folder name
     * @param delimiter joining up truncated foldername with its id
     */
    public IdBasedFolderNameTruncator(int originalNameLimit, String delimiter) {
        this.originalNameLimit = originalNameLimit;
        this.delimiter = delimiter;
    }

    /**
     *
     * @param folder
     * @return truncated folder name with the folder id joined up using delimiter
     */
    @Override
    public String truncateNameFor(IdBasedFolder folder) {
        String folderName = folder.getName();

        if (folderName.length() > originalNameLimit) {
            folderName = folderName.substring(0, originalNameLimit);
        }

        return String.join(delimiter, folderName, folder.getId());
    }
}
