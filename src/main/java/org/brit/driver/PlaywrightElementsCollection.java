package org.brit.driver;

import java.util.List;

public interface PlaywrightElementsCollection {
    List<String> texts();

    PlaywrightElement first();
    PlaywrightElement last();

    PlaywrightElementsCollection first(int count);
    PlaywrightElementsCollection last(int count);

    long size();

    //TODO add conditions


    PlaywrightElementsCollection should(PWCollectionCondition condition);


}
