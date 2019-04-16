package com.example.damirsovic.solutionstester.incomecall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.damirsovic.solutionstester.R;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class IncomeCallLayout extends RelativeLayout {
    public static final int NO_ACTION = 0;
    public static final int CALL_START = 1;
    public static final int CALL_END = 2;
    private final PublishSubject<MotionEvent> mTouchSubject = PublishSubject.create();
    private final Flowable<MotionEvent> mTouches = mTouchSubject.hide().toFlowable(BackpressureStrategy.DROP);
    private final Flowable<MotionEvent> mDownObservable = mTouches.filter(ev -> ev.getActionMasked() == MotionEvent.ACTION_DOWN);
    private final Flowable<MotionEvent> mUpObservable = mTouches.filter(ev -> ev.getActionMasked() == MotionEvent.ACTION_UP);
    private final Flowable<MotionEvent> mMovesObservable = mTouches.filter(ev -> ev.getActionMasked() == MotionEvent.ACTION_MOVE);
    private RelativeLayout parentLayout;
    private ImageView callStartButton;
    private ImageView callEndButton;
    private RelativeLayout leftArrowsLayout;
    private ImageView leftArrow1;
    private ImageView leftArrow2;
    private ImageView leftArrow3;
    private ImageView activeButton;
    private RelativeLayout rightArrowsLayout;
    private ImageView rightArrow1;
    private ImageView rightArrow2;
    private ImageView rightArrow3;
    private View dividerView;
    final OnTouchListener leftListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            activeButton = callStartButton;
            callEndButton.setOnTouchListener(null);
            rightArrowsLayout.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
            mTouchSubject.onNext(event);
            return true;
        }
    };
    final OnTouchListener rightListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            activeButton = callEndButton;
            callStartButton.setOnTouchListener(null);
            leftArrowsLayout.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
            mTouchSubject.onNext(event);
            return true;
        }
    };
    private LayoutParams params = null;
    private IIncomeCallActionListener mlistener = null;
    private int radius;
    private boolean triggerAction;
    private boolean moving;
    private int margin;
    private float correction = 0;
    private boolean isScaled = false;

    private ObjectAnimator leftArrow1Animator;
    private ObjectAnimator leftArrow2Animator;
    private ObjectAnimator leftArrow3Animator;
    private ObjectAnimator rightArrow1Animator;
    private ObjectAnimator rightArrow2Animator;
    private ObjectAnimator rightArrow3Animator;

    final Flowable<MotionEvent> MotionEventsObservable = mDownObservable.flatMap(downEvent -> {
        triggerAction = false;
        if (params == null)
            params = (LayoutParams) activeButton.getLayoutParams();
        correction = downEvent.getX();
        moving = true;
        radius = activeButton.getWidth() / 2;
        Display display = ((WindowManager) super.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screen = new Point();
        display.getRealSize(screen);
        margin = (screen.x - parentLayout.getWidth()) / 2;

        return mMovesObservable
                .takeUntil(mUpObservable
                        .doOnNext(upEvent -> {
                            moving = false;
                            release();
                            takeAction(activeButton.getId());
                        }))
                .doOnNext(motionEvent -> moveButton(motionEvent.getRawX() - correction - margin));
    });

    public IncomeCallLayout(Context context) {
        super(context);
        initializeView();
    }

    public IncomeCallLayout(Context context, AttributeSet attr) {
        super(context, attr);
        initializeView();
    }

    public void initializeView() {
        super.onAttachedToWindow();
        inflate(super.getContext(), R.layout.income_call_layout, this);
        isScaled = false;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (!isScaled) {
            ViewParent parent = getParent();
            int measuredWidth = MeasureSpec.getSize(widthSpec);
            int measuredHeight = MeasureSpec.getSize(heightSpec);
            LayoutParams lParams = (LayoutParams) getLayoutParams();
            adjustButtons(lParams.height);
            isScaled = true;

        }
    }

    private void updateButton(ImageView view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        params.width = height;
        view.setLayoutParams(params);
        view.invalidate();
        view.requestLayout();
    }

    private void updateArrows(ImageView view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        params.width = height / 2;
        view.setLayoutParams(params);
        view.invalidate();
        view.requestLayout();
    }

    private void adjustButtons(int height) {
        updateButton(callStartButton, height);
        updateButton(callEndButton, height);

        height /= 3;

        updateArrows(leftArrow1, height);
        updateArrows(leftArrow2, height);
        updateArrows(leftArrow3, height);
        updateArrows(rightArrow1, height);
        updateArrows(rightArrow2, height);
        updateArrows(rightArrow3, height);
    }

    public void init() {

        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout).getParent();
        dividerView = findViewById(R.id.divider_view);
        leftArrowsLayout = findViewById(R.id.left_arrows);
        callStartButton = findViewById(R.id.call_start);
        leftArrow1 = findViewById(R.id.left_arrow_1);
        leftArrow2 = findViewById(R.id.left_arrow_2);
        leftArrow3 = findViewById(R.id.left_arrow_3);

        rightArrowsLayout = findViewById(R.id.right_arrows);
        callEndButton = findViewById(R.id.call_end);
        rightArrow1 = findViewById(R.id.right_arrow_1);
        rightArrow2 = findViewById(R.id.right_arrow_2);
        rightArrow3 = findViewById(R.id.right_arrow_3);


        reset();

        MotionEventsObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
        animateArrows();

    }

    private void release() {
        params = (LayoutParams) activeButton.getLayoutParams();
        final float endPoint = activeButton.getId() == R.id.call_start ? 0 : parentLayout.getWidth() - activeButton.getWidth();
        final float startPoint = activeButton.getId() == R.id.call_start ? params.getMarginStart() : parentLayout.getWidth() - params.getMarginEnd();

        final ValueAnimator positionAnimator = ValueAnimator.ofFloat(startPoint, endPoint);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            moveButton(x);
        });
        positionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset();
            }
        });
        positionAnimator.setDuration(200);
        positionAnimator.start();
    }

    public void moveButton(float x) {
        int centerX = radius + (int) (activeButton.getId() == R.id.call_start ? x : parentLayout.getWidth() - (x + activeButton.getWidth()));
        params = (RelativeLayout.LayoutParams) activeButton.getLayoutParams();
        if (x > 0 && x < (parentLayout.getWidth() - activeButton.getWidth())) {
            if (moving) {
                if (centerX > (parentLayout.getWidth() / 3)) {
                    triggerAction = true;
                    (activeButton.getId() == R.id.call_start ? leftArrow1 : rightArrow1).setVisibility(View.GONE);
                    (activeButton.getId() == R.id.call_start ? leftArrow2 : rightArrow2).setVisibility(View.GONE);
                    (activeButton.getId() == R.id.call_start ? leftArrow3 : rightArrow3).setVisibility(View.GONE);
                } else {
                    triggerAction = false;
                    (activeButton.getId() == R.id.call_start ? leftArrow1 : rightArrow1).setVisibility(View.VISIBLE);
                    (activeButton.getId() == R.id.call_start ? leftArrow2 : rightArrow2).setVisibility(View.VISIBLE);
                    (activeButton.getId() == R.id.call_start ? leftArrow3 : rightArrow3).setVisibility(View.VISIBLE);
                }
            }

        } else {
            x = (activeButton.getId() == R.id.call_start ? 0 : parentLayout.getWidth() - activeButton.getWidth());
        }
        if (centerX < parentLayout.getWidth() / 2) {
            if (activeButton.getId() == R.id.call_start) {
                params.setMarginStart((int) x);
            } else {
                params.setMarginEnd((int) (parentLayout.getWidth() - (x + 2 * radius)));
            }
            activeButton.setLayoutParams(params);
        }
    }

    private void reset() {
        callStartButton.setVisibility(View.VISIBLE);
        callStartButton.setOnTouchListener(leftListener);
        leftArrowsLayout.setVisibility(View.VISIBLE);
        leftArrow1.setVisibility(View.VISIBLE);
        leftArrow2.setVisibility(View.VISIBLE);
        leftArrow3.setVisibility(View.VISIBLE);

        callEndButton.setOnTouchListener(rightListener);
        rightArrowsLayout.setVisibility(View.VISIBLE);
        dividerView.setVisibility(View.VISIBLE);
        rightArrow1.setVisibility(View.VISIBLE);
        rightArrow2.setVisibility(View.VISIBLE);
        rightArrow3.setVisibility(View.VISIBLE);
    }

    private void takeAction(int id) {
        IncomeCallBundle bundle = new IncomeCallBundle();
        bundle.setTriggerAction(triggerAction);
        if (triggerAction) {
            if (id == R.id.call_start) {
                bundle.setCallAction(CALL_START);
            } else {
                bundle.setCallAction(CALL_END);
            }
            stopAnimation();
        } else {
            bundle.setCallAction(NO_ACTION);
        }
        if (mlistener != null) {
            mlistener.onIncomeCallAction(bundle);
        }
    }

    private ObjectAnimator buttonAnimator(ImageView view, int startDelay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        objectAnimator.setDuration(700);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        return objectAnimator;
    }

    private void animateArrows() {
        leftArrow1Animator = buttonAnimator(leftArrow1, 0);
        leftArrow1Animator.start();
        leftArrow2Animator = buttonAnimator(leftArrow2, 250);
        leftArrow2Animator.start();
        leftArrow3Animator = buttonAnimator(leftArrow3, 500);
        leftArrow3Animator.start();

        rightArrow1Animator = buttonAnimator(rightArrow1, 0);
        rightArrow1Animator.start();
        rightArrow2Animator = buttonAnimator(rightArrow2, 250);
        rightArrow2Animator.start();
        rightArrow3Animator = buttonAnimator(rightArrow3, 500);
        rightArrow3Animator.start();
    }

    private void stopAnimation() {
        leftArrow1Animator.end();
        leftArrow2Animator.end();
        leftArrow3Animator.end();
        rightArrow1Animator.end();
        rightArrow2Animator.end();
        rightArrow3Animator.end();
    }

    public void setOnIncomeCallActionListener(IIncomeCallActionListener listener) {
        this.mlistener = listener;
    }
}