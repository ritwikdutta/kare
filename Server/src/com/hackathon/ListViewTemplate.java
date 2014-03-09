package com.hackathon;

import static com.hackathon.Tags.a;
import static com.hackathon.Tags.div;

/**
 * @author Adrian Chmielewski-Anders
 * @since 0.0.1
 */

public class ListViewTemplate extends Template {

    @Override
    public String define() {
        return div(
                a("$href", "$text")
        );
    }


}
