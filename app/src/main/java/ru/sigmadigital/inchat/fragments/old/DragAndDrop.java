package ru.sigmadigital.inchat.fragments.old;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.fragments.BaseFragment;

public class DragAndDrop extends BaseFragment implements View.OnTouchListener {


    private View selected_item = null;
    private int offset_x = 0;
    private int offset_y = 0;
    Boolean touchFlag = false;
    boolean dropFlag = false;
    ViewGroup.LayoutParams imageParams;


    ImageView /*imageDrop,*/ image1, image2;


    int eX, eY;
    //int topY, leftX, rightX, bottomY;


    public static DragAndDrop newInstance() {
        return new DragAndDrop();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_drag_and_drop, container, false);

        View root = v.findViewById(R.id.root);
        //imageDrop = (ImageView)  v.findViewById(R.id.imgDrop);
        image1 = (ImageView) v.findViewById(R.id.img);
        image2 = (ImageView) v.findViewById(R.id.img2);
        image1.setOnTouchListener(this);
        image2.setOnTouchListener(this);


        root.setOnTouchListener(new View.OnTouchListener() {
                                    public boolean onTouch(View v, MotionEvent event) {

                                        System.err.println("Display If  Part ::->" + touchFlag);
                                        switch (event.getActionMasked()) {

                                            case MotionEvent.ACTION_DOWN:
                                                savedMatrix.set(matrix);
                                                start.set(event.getX(), event.getY());
                                                mode = DRAG;
                                                lastEvent = null;
                                                break;
                                            case MotionEvent.ACTION_POINTER_DOWN:
                                                oldDist = spacing(event);
                                                if (oldDist > 10f) {
                                                    savedMatrix.set(matrix);
                                                    midPoint(mid, event);
                                                    mode = ZOOM;
                                                }
                                                lastEvent = new float[4];
                                                lastEvent[0] = event.getX(0);
                                                lastEvent[1] = event.getX(1);
                                                lastEvent[2] = event.getY(0);
                                                lastEvent[3] = event.getY(1);
                                                d = rotation(event);
                                                break;


                                            case MotionEvent.ACTION_MOVE:
                                                if (mode == DRAG) {
                                                    if (touchFlag) {
                                                        eX = (int) event.getX();
                                                        eY = (int) event.getY();
                                                        int x = (int) event.getX() - offset_x;
                                                        int y = (int) event.getY() - offset_y;
                                                        int w = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 50;
                                                        int h = getActivity().getWindowManager().getDefaultDisplay().getHeight() - 10;
                                                        if (x > w) x = w;
                                                        if (y > h) y = h;
                                                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                                                        lp.setMargins(x, y, 0, 0);


                                                        selected_item.bringToFront();
                                                        dropFlag = true;
                                                        selected_item.setLayoutParams(lp);


                                                        matrix.set(savedMatrix);
                                                        float dx = event.getX() - start.x;
                                                        float dy = event.getY() - start.y;
                                                        matrix.postTranslate(dx, dy);
                                                    }
                                                } else if (mode == ZOOM) {
                                                    if (touchFlag) {
                                                        Log.e("zoom", matrix.toShortString());
                                                        float newDist = spacing(event);

                                                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)(selected_item.getHeight() * newDist / oldDist), (int)(selected_item.getWidth() * newDist / oldDist));

                                                        selected_item.bringToFront();
                                                        dropFlag = true;
                                                        selected_item.setLayoutParams(lp);

                                                        if (newDist > 10f) {
                                                            matrix.set(savedMatrix);
                                                            float scale = (newDist / oldDist);
                                                            matrix.postScale(scale, scale, mid.x, mid.y);


                                                        }
                                                        if (lastEvent != null && event.getPointerCount() == 3) {
                                                            Log.e("zoom", matrix.toShortString());
                                                            newRot = rotation(event);
                                                            float r = newRot - d;
                                                            float[] values = new float[9];
                                                            matrix.getValues(values);
                                                            float tx = values[2];
                                                            float ty = values[5];
                                                            float sx = values[0];
                                                            float xc = (selected_item.getWidth() / 2) * sx;
                                                            float yc = (selected_item.getHeight() / 2) * sx;
                                                            matrix.postRotate(r, tx + xc, ty + yc);
                                                        }


                                                    }
                                                }


                                                break;

                                            case MotionEvent.ACTION_UP:
                                            case MotionEvent.ACTION_POINTER_UP:
                                                touchFlag = false;
                                                if (dropFlag) {
                                                    dropFlag = false;
                                                } else {
                                                    selected_item.setLayoutParams(imageParams);
                                                }


                                                mode = NONE;
                                                lastEvent = null;


                                                break;
                                            default:
                                                break;
                                        }

                                        return true;
                                    }
                                }
        );


        return v;
    }


    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = true;
                offset_x = (int) event.getX();
                offset_y = (int) event.getY();
                selected_item = v;
                imageParams = v.getLayoutParams();
                break;
            case MotionEvent.ACTION_UP:
                selected_item = null;
                touchFlag = false;
                break;
            default:
                break;
        }
        return false;
    }


    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;




/*
@Override
        public boolean onTouch(View v, MotionEvent event) {
            // handle touch events here
            ImageView view = (ImageView) v;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                    d = rotation(event);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    lastEvent = null;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        float dx = event.getX() - start.x;
                        float dy = event.getY() - start.y;
                        matrix.postTranslate(dx, dy);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = (newDist / oldDist);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                        if (lastEvent != null && event.getPointerCount() == 3) {
                            newRot = rotation(event);
                            float r = newRot - d;
                            float[] values = new float[9];
                            matrix.getValues(values);
                            float tx = values[2];
                            float ty = values[5];
                            float sx = values[0];
                            float xc = (view.getWidth() / 2) * sx;
                            float yc = (view.getHeight() / 2) * sx;
                            matrix.postRotate(r, tx + xc, ty + yc);
                        }
                    }
                    break;
            }

            view.setImageMatrix(matrix);
            return true;
        }
*/

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


}
