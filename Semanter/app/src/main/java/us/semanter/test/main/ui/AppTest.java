package test.ui.main;

import android.test.AndroidTestCase;

public class AppTest extends AndroidTestCase {
    public void permissionsTest() {
        assertActivityRequiresPermission("us.semanter.app", "SearchActivity", "READ_EXTERNAL_STORAGE");
        assertActivityRequiresPermission("us.semanter.app", "SearchActivity", "WRITE_EXTERNAL_STORAGE");
    }
}
