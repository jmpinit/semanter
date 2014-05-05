package us.semanter.app.vision.result;

import java.util.ArrayList;
import java.util.List;

import us.semanter.app.vision.util.Polygon;

public class GuessFactory {
    public static List<OutlineGuess> outlineGuess(List<Polygon> polygons) {
        List<OutlineGuess> outlines = new ArrayList<OutlineGuess>(polygons.size());

        for(Polygon polygon: polygons)
            outlines.add(new OutlineGuess(polygon, 0.0f));

        return outlines;
    }
}
