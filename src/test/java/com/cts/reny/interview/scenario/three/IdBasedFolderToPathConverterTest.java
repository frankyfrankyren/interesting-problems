package com.cts.reny.interview.scenario.three;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cts.reny.interview.scenario.three.IdBasedFolderToToPathConverter.ROOT_FOLDER_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IdBasedFolderToPathConverterTest {
    private static final String REALLY_LONG_NAME = "Bacon ipsum dolor amet meatball strip steak bresaola picanha tri-tip, shankle ground round. Corned beef jowl tri-tip meatball burgdoggen andouille, shank porchetta sirloin bacon picanha pork spare ribs ham alcatra. Tail ground round rump capicola burgdoggen picanha pancetta pig turducken chicken flank turkey brisket pork chop andouille. Strip steak t-bone beef, meatball ham hock pork chop turkey brisket ribeye.\n" +
            "\n" +
            "Pork kielbasa sausage shankle buffalo picanha, andouille leberkas kevin. Flank leberkas pancetta, turkey tail jowl tongue frankfurter shoulder pig capicola kevin porchetta. Jerky turkey tongue, picanha bacon andouille pancetta porchetta pork leberkas. Pancetta tenderloin shank, ball tip meatball buffalo sausage bacon filet mignon jerky chicken andouille. Drumstick sirloin meatball leberkas tail swine pork belly beef ribeye meatloaf pig landjaeger shoulder.\n" +
            "\n" +
            "Cupim pork belly capicola ham hock doner strip steak sirloin pork chop burgdoggen buffalo biltong t-bone rump tri-tip pig. Alcatra chuck bacon landjaeger meatball, biltong corned beef capicola jowl kielbasa flank sausage short loin. Doner sirloin jerky boudin. Alcatra capicola pastrami doner fatback cupim jerky pork loin pig burgdoggen short ribs sirloin tri-tip chuck buffalo.\n" +
            "\n" +
            "Doner short loin tri-tip fatback, pig frankfurter burgdoggen beef ribs jerky. Corned beef pork shoulder rump. Tri-tip short loin shank spare ribs sirloin kevin. Jowl pork chop kielbasa pork flank swine. Burgdoggen hamburger pork pork belly turkey kielbasa rump short loin, jowl turducken prosciutto beef fatback tri-tip.\n" +
            "\n" +
            "Biltong flank spare ribs chuck doner, turducken andouille drumstick short loin tri-tip filet mignon turkey bacon burgdoggen porchetta. Landjaeger sausage cow buffalo fatback salami bacon tail. Ball tip tail picanha, bresaola jerky hamburger shank frankfurter ham turkey venison. Tail alcatra burgdoggen, beef bacon corned beef shoulder sirloin brisket. Shoulder strip steak prosciutto bresaola.";
    private static final String REALLY_SHORT_NAME = "Bacon";

    private static final String DEMO_FOLDER_ID = "Demo-Folder-Id";
    private static final String FOLDER_1_ID_1 = "Folder-1-Id-1";
    private static final String FOLDER_1_ID_2 = "Folder-1-Id-2";
    private static final String FOLDER_2_ID = "Folder-2-Id";
    private static final String FOLDER_LONG_NAME_ID = "Folder-Long-Name-Id";
    private static final String FOLDER_SHORT_NAME_ID = "Folder-Short-Name-Id";
    private static final int CHARACTERS_LIMIT_80 = 80;
    private static final int ORIGINAL_FOLDER_NAME_LIMIT_10 = 10;
    private static final String TRUNCATION_DELIMITER_DASH = "-";
    private static final String FOLDER_1_NAME = "Folder 1";
    private static final String DEMO_FOLDER_NAME = "Demo Folder";
    private static final String FOLDER_2_NAME = "Folder 2";
    private static final String TOO_LONG_TO_TRUNCATE_ID = "Too-Long-To-Truncate_Id";
    private IdBasedFolderToToPathConverter sut;

    @Before
    public void setUp() throws Exception {
        sut = new IdBasedFolderToToPathConverter(
                createFolders(),
                new IdBasedFolderNameTruncator(ORIGINAL_FOLDER_NAME_LIMIT_10, TRUNCATION_DELIMITER_DASH),
                CHARACTERS_LIMIT_80);
    }

    private IdBasedFolder[] createFolders() {
        return new IdBasedFolder[] {
                createRootFolder(),
                createFolder(DEMO_FOLDER_ID, Arrays.asList(ROOT_FOLDER_ID), DEMO_FOLDER_NAME),
                createFolder(FOLDER_1_ID_1, Arrays.asList(ROOT_FOLDER_ID, DEMO_FOLDER_ID), FOLDER_1_NAME),
                createFolder(FOLDER_1_ID_2, Arrays.asList(ROOT_FOLDER_ID, DEMO_FOLDER_ID), FOLDER_1_NAME),
                createFolder(FOLDER_2_ID, Arrays.asList(ROOT_FOLDER_ID, DEMO_FOLDER_ID, FOLDER_1_ID_1), FOLDER_2_NAME),
                createFolder(FOLDER_LONG_NAME_ID, Arrays.asList(ROOT_FOLDER_ID, DEMO_FOLDER_ID, FOLDER_1_ID_2), REALLY_LONG_NAME),
                createFolder(FOLDER_SHORT_NAME_ID, Arrays.asList(ROOT_FOLDER_ID, DEMO_FOLDER_ID, FOLDER_1_ID_2, FOLDER_LONG_NAME_ID), REALLY_SHORT_NAME),
        };
    }

    private IdBasedFolder createFolder(String folderId, List<String> parentIds, String folderName) {
        IdBasedFolder folder = new IdBasedFolder();
        folder.setId(folderId);
        folder.setParentIds(parentIds);
        folder.setName(folderName);
        return folder;
    }

    private IdBasedFolder createRootFolder() {
        IdBasedFolder folder = new IdBasedFolder();
        folder.setId("root");
        folder.setParentIds(new ArrayList<>());
        return folder;
    }

    @Test(expected = RuntimeException.class)
    public void convertToAbsolutePath_fails_on_unknown_id() {
        sut.convertToAbsolutePath("kdjsafsdvfjunverutugwernjgktrgtg");
    }

    @Test
    public void convertToAbsolutePath_appends_id_to_clashing_folder_name() {
        final String path = sut.convertToAbsolutePath(FOLDER_1_ID_1);
        assertThat(path, is(String.join("/", "",
                DEMO_FOLDER_NAME,
                FOLDER_1_NAME + TRUNCATION_DELIMITER_DASH + FOLDER_1_ID_1)));
    }

    @Test
    public void convertToAbsolutePath_appends_id_to_clashing_parent_folder_name() {
        final String path = sut.convertToAbsolutePath(FOLDER_2_ID);
        assertThat(path, is(String.join("/", "",
                DEMO_FOLDER_NAME,
                FOLDER_1_NAME + TRUNCATION_DELIMITER_DASH + FOLDER_1_ID_1,
                FOLDER_2_NAME)));
    }

    @Test
    public void convertToAbsolutePath_truncates_folder_name_and_appends_id_to_long_folder_name() {
        final String path = sut.convertToAbsolutePath(FOLDER_LONG_NAME_ID);
        assertThat(path, is(String.join("/", "",
                DEMO_FOLDER_NAME,
                FOLDER_1_NAME + TRUNCATION_DELIMITER_DASH + FOLDER_1_ID_2,
                REALLY_LONG_NAME.substring(0, ORIGINAL_FOLDER_NAME_LIMIT_10) + TRUNCATION_DELIMITER_DASH + FOLDER_LONG_NAME_ID)));
    }

    @Test
    public void convertToAbsolutePath_preserves_previous_truncation() {
        sut.convertToAbsolutePath(FOLDER_LONG_NAME_ID);
        final String path = sut.convertToAbsolutePath(FOLDER_SHORT_NAME_ID);
        assertThat(path, is(String.join("/", "",
                DEMO_FOLDER_NAME,
                FOLDER_1_NAME + TRUNCATION_DELIMITER_DASH + FOLDER_1_ID_2,
                REALLY_LONG_NAME.substring(0, ORIGINAL_FOLDER_NAME_LIMIT_10) + TRUNCATION_DELIMITER_DASH + FOLDER_LONG_NAME_ID,
                REALLY_SHORT_NAME)));
    }

    @Test(expected = RuntimeException.class)
    public void convertToAbsolutePath_fails_to_truncate() {
        sut.convertToAbsolutePath(TOO_LONG_TO_TRUNCATE_ID);
    }
}