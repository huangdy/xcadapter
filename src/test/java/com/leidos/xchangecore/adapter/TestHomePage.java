package com.leidos.xchangecore.adapter;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.leidos.xchangecore.adapter.ui.UploadPage;

/**
 * Simple test using the WicketTester
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:contexts/applicationContext.xml"
})
public class TestHomePage {

    private WicketTester tester;

    @Test
    public void homepageRendersSuccessfully() {

        //start and render the test page
        tester.startPage(UploadPage.class);
        //assert rendered page class
        tester.assertRenderedPage(UploadPage.class);
    }

    @Before
    public void setUp() {

        tester = new WicketTester(new XchangeCoreAdapter());
    }
}
