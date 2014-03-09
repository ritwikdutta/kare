package com.hackathon;

import static com.hackathon.Tags.a;
import static com.hackathon.Tags.divStyle;
import static com.hackathon.Tags.h2;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class ListViewTemplate extends Template {

    @Override
    public String define() {
        return divStyle("class=\"item\"",
                h2("$text"),
                
                a("$href", "$text")
        );
    }


}
