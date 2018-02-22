package com.cts.reny.interview.scenario.three;

/**
 * Generic folder truncator interface
 * @param <F> Folder type to truncate
 */
public interface FolderNameTruncator<F extends Folder> {
    String truncateNameFor(F folder);
}
