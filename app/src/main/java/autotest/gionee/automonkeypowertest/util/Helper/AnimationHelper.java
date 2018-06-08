package autotest.gionee.automonkeypowertest.util.Helper;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayList;

public class AnimationHelper {

    public static TranslateAnimation up() {
        TranslateAnimation upAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        upAnimation.setDuration(500);
        return upAnimation;
    }

    public static TranslateAnimation shake() {
        TranslateAnimation animation = new TranslateAnimation(0, -15, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(50);
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    public static ScaleAnimation scale() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        return scaleAnimation;
    }

    public static AnimationSet setScrollText(final TextView view, final String text) {
        return setScrollText(view, text, 300);
    }

    public static AnimationSet setScrollText(final TextView view, final String text, final int duration) {
        Animation animation = view.getAnimation();
        if (animation != null && animation.hasStarted()) {
            view.setText(text);
//            Util.i("hasStarted");
            return null;
        }
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(duration);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(duration);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(mHiddenAction);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setText(text);
                TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                mHiddenAction.setDuration(duration);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(duration);
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(mHiddenAction);
                animationSet.addAnimation(alphaAnimation);
                view.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animationSet);
        return animationSet;
    }

    public static void setUpAnimation(ArrayList<View> views) {
        setUpAnimation(views.toArray(new View[]{}));
    }

    public static void setUpAnimation(View... views) {
        TranslateAnimation up = up();
        for (View view : views) {
            view.setAnimation(up);
        }
    }
}
