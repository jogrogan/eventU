package com.eventu;

import java.util.ArrayList;


/**
 * This class solely exists to provide other JUnit tests mock variables that are needed for some
 * tasks to work in the android framework
 */
public class MockTestVariables {

    static UserInfo mockUser = new UserInfo("test@mailinator.com",
            new ArrayList<String>(), "Test Club", "Test",
            "QzWke1Oq06Va7WFO8jWEBwuNkFI2", true);


}
