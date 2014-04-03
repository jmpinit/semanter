package graphics.epi.filesystemtree;

import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Folder implements Items, Serializable {
    Folder parent;
    String name;

    public List<Items> inside = new ArrayList<Items>();

    public Folder(Folder parent, String name, List<String> allFiles) {
        this.parent = parent;
        this.name = name;
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        for (String s : allFiles) {
            int secondSlash = -1;
            for (int i = 1; i < s.length(); i++) {
                if (s.charAt(i) == '/') {
                    secondSlash = i;
                    break;
                }
            }

            if (secondSlash == -1) {
                //s is just a file
                // FIXME inside.add(new MediaStore.Files(s.substring(1)));
            } else {
                String subfol = s.substring(1, secondSlash);
                List<String> list = map.get(subfol);
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(s.substring(secondSlash));

                map.put(subfol, list);
            }
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            inside.add(new Folder(this, key, value));
        }
    }

    public List<Items> getItems() {
        List<Items> ret = new ArrayList<Items>();
        if (parent != null) {
            ret.add(new Parent(parent));
        }
        ret.addAll(inside);
        return ret;
    }

    @Override
    public String toString() {
        return "Folder " + name;
    }

    public Items get(int position) {
        if (parent != null) {
            if (position == 0) {
                return parent;
            }
            return inside.get(position - 1);
        }
        return inside.get(position);
    }

    public boolean isFile() {
        return false;
    }
}
