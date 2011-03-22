package com.magnetstreet.swt.extra.window;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ColorSchemeTest
 *
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since Sep 8, 2010
 */
@RunWith(JMock.class)
public class ColorSchemeTest {
    private Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Test public void testImagePathResolution() {
        final Composite widget = context.mock(Composite.class);
        ColorScheme colorScheme = new ColorScheme("Test Scheme");
        colorScheme.setDefaultBackgroundImage("exclam.png");

        context.checking(new Expectations() {{
            one(widget).setBackgroundImage(with(Expectations.aNonNull(Image.class)));
            allowing(widget).getClass();
        }});

        colorScheme.applyScheme(widget);
    }

    @Test public void testWidgetSpecificTrumpsDefault() {
        final Composite widget = context.mock(Composite.class);
        ColorScheme colorScheme = new ColorScheme("Test Scheme");
        colorScheme.setDefaultBackgroundColor(0, 255, 0);
        colorScheme.mapWidgetBackgroundColor(widget.getClass(), 255, 1, 255);

        context.checking(new Expectations() {{
            one(widget).setBackground(with(new TypeSafeMatcher<Color>() {
                @Override public boolean matchesSafely(Color color) {
                    return color.getGreen() == 1;
                }
                public void describeTo(Description description) {
                    description = description.appendText("green value of 1");
                }
            }));
            allowing(widget).getClass();
        }});

        colorScheme.applyScheme(widget);
    }
}
