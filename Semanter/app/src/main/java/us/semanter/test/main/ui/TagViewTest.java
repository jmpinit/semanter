package us.semanter.test.main.ui;

import android.test.AndroidTestCase;

import java.util.Set;

import us.semanter.app.model.Tag;
import us.semanter.app.ui.TagView;

public class TagViewTest extends AndroidTestCase {
    public void testDuplicateTags() {
        TagView tagView = new TagView(getContext());

        tagView.onNewTag(new Tag("test"));
        tagView.onNewTag(new Tag("test"));

        Set<Tag> tags = tagView.getTags();
        for(Tag first: tags) {
            for(Tag second: tags) {
                if(first != second) {
                    assertFalse(first.getValue().equals(second.getValue()));
                }
            }
        }
    }
}