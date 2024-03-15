package edu.ucsd.cse110.successorator.app;

import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class TextViewStrikeThroughMatcher {

    public static Matcher<View> withStrikeThrough() {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView textView) {
                int flags = textView.getPaintFlags();
                return (flags & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with strike-through text");
            }
        };
    }
}
