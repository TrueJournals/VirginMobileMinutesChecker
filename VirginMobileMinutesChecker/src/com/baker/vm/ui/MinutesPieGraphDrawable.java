/**
 *
 */
package com.baker.vm.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.baker.vm.PreferencesUtil;
import com.baker.vm.VMAccount;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class MinutesPieGraphDrawable extends MinutesGraphDrawable
{
	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;

    private static final int DRAWABLE_PADDING = 4;
    private static final float TIME_STROKE_WIDTH = 6;
    private static final int DRAWABLE_STROKE_WIDTH = 1;
    private static final int BACKGROUND_ALPHA = 150;

	private static final int DEGREES = 360;

	private final Context context;
	private int minDeg;
	private int dateDeg;
	private int dataDeg;
	private final float density;

	private int alignment = ALIGN_CENTER;

	String buf = "";

	public MinutesPieGraphDrawable(final Context c)
	{
		super(null);

		context = c;
		updateModel(null);
		
		// We use the density in multiple places. Instead of computing it multiple times, compute it once here.
		WindowManager winManage = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        
        DisplayMetrics metrics = new DisplayMetrics();
        
        winManage.getDefaultDisplay().getMetrics(metrics);
        
        density = metrics.density; 
	}

	public MinutesPieGraphDrawable(final Context c, final VMAccount account)
	{
		this(c);

		updateModel(account);
	}

	@Override
	protected void updateModel(final VMAccount account)
	{
		super.updateModel(account);

		if (hasMinutes())
		{
			minDeg = (int) (getMinutesPercent() * DEGREES);
		}
		else
		{
			minDeg = 0;
		}
		
		if(hasData())
		{
			dataDeg = (int) (getDataPercent() * DEGREES);
		}
		else
		{
			dataDeg = 0;
		}
		
		if (hasDates())
		{
			dateDeg = (int) (getDatePercent() * DEGREES);
		}
		else
		{
			dateDeg = 0;
		}
	}

	@Override
	public void draw(final Canvas c)
	{
        if (PreferencesUtil.getShowGraph(context))
        {
        	final Rect clip = squareIt(c.getClipBounds());
        	drawOnCanvas(c, clip);
        }
	}

	public void drawOnCanvas(final Canvas c, final Rect clip)
	{
		final RectF oval =
		    new RectF(clip.left + DRAWABLE_PADDING,
		              clip.top + DRAWABLE_PADDING,
		              clip.right - DRAWABLE_PADDING,
		              clip.bottom - DRAWABLE_PADDING);

		Log.e("drawOnCanvas", oval.toString());

		drawBackground(c, oval);
		drawMinutesChart(c, oval);
		
		drawDataChart(c, oval);

		drawTimeChart(c, oval);

		drawStroke(c, oval);

		drawText(c, clip);
	}

	public void setAlignment(final int iAlignment)
	{
		alignment = iAlignment;
	}

    private void drawText(final Canvas c, final Rect clip)
    {
        final Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setTextSize((int) (12.0*(density))); // Scale font size based on density
        black.setAntiAlias(true);

        String text = getAccount().getMinutesTotal() - getAccount().getMinutesUsed() + "";

        final int textHeight = (int) black.getFontMetrics().top / 2;

        c.drawText(text,
        		clip.left + (clip.right - clip.left - black.measureText(text)) / 2,
        		clip.top + (clip.bottom - clip.top - textHeight) / 2,
        		black);
    }

    private void drawBackground(final Canvas c, final RectF clip)
    {
        final Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(BACKGROUND_ALPHA);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        c.drawOval(clip, p);
    }

    private void drawStroke(final Canvas c, final RectF clip)
    {
        final Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setStrokeWidth(DRAWABLE_STROKE_WIDTH);

        c.drawOval(clip, p);
    }
    private void drawMinutesChart(final Canvas c, final RectF clip)
    {
        final Paint minPaint = new Paint();
        minPaint.setAlpha(getOpacity());
        minPaint.setStyle(Paint.Style.FILL);
        minPaint.setAntiAlias(true);
        
        if (dateDeg < (minDeg * 1.05F) && dateDeg > (minDeg * .95F))
        {
            minPaint.setColor(context.getResources().getColor(R.color.warning));
        }
        else if (dateDeg < minDeg)
        {
            minPaint.setColor(context.getResources().getColor(R.color.error));
        }
        else
        {
            minPaint.setColor(context.getResources().getColor(R.color.info));
        }
        
        final float rectChange = (float)10.0*density;
        
        RectF minClip = new RectF(clip.left + rectChange,
        							clip.top + rectChange, 
        							clip.right - rectChange, 
        							clip.bottom - rectChange);

        c.drawArc(minClip, 0, minDeg, true, minPaint);
    }
    
    private void drawDataChart(final Canvas c, final RectF clip)
    {
    	final Paint dataPaint = new Paint();
    	dataPaint.setAlpha(getOpacity());
    	dataPaint.setStyle(Paint.Style.STROKE);
    	dataPaint.setStrokeWidth((float)10.0*density);
    	dataPaint.setAntiAlias(true);
    	
    	if (dateDeg < (dataDeg * 1.05F) && dateDeg > (dataDeg * .95F))
        {
            dataPaint.setColor(context.getResources().getColor(R.color.warning));
        }
        else if (dateDeg < dataDeg)
        {
            dataPaint.setColor(context.getResources().getColor(R.color.error));
        }
        else
        {
            dataPaint.setColor(context.getResources().getColor(R.color.info));
        }
    	
    	final float rectChange = (float)5.0*density;
    	
    	RectF dataClip = new RectF(clip.left+rectChange, clip.top+rectChange, clip.right-rectChange, clip.bottom-rectChange);
    	
    	c.drawArc(dataClip, 0, dataDeg, false, dataPaint);
    	
    	dataClip.inset(rectChange, rectChange);
    	
    	// Draw a bounding circle for data
    	drawStroke(c, dataClip);
    }

    private void drawTimeChart(final Canvas c, final RectF clip)
    {
    	final float rectChange = (float)10.0*density;
        final RectF degOval = new RectF(clip.left + rectChange, clip.top + rectChange, clip.right - rectChange, clip.bottom - rectChange);
        final Paint degPaint = new Paint();
        degPaint.setColor(Color.BLACK);
        degPaint.setStyle(Paint.Style.STROKE);
        degPaint.setStrokeWidth(TIME_STROKE_WIDTH*density);
        degPaint.setAntiAlias(true);

        c.drawArc(degOval, 0, dateDeg, false, degPaint);

        // stroke the front of the time / minutes
        /*
        degPaint.setStrokeWidth(0);
        final int x = (int) ((clip.right - clip.left) / 2);
        final int y = (int) ((clip.bottom - clip.top) / 2);
        c.drawLine(clip.left + x, clip.top + y, clip.right, clip.top + y, degPaint);
        */
    }

    private Rect squareIt(final Rect clipBounds)
	{
		final Rect r = new Rect(clipBounds);
		final int w = r.right - r.left;
		final int h = r.bottom - r.top;
		final int size = Math.min(w, h);

		switch (alignment)
		{
			case ALIGN_LEFT:
				r.left = 0;
				r.right = size;
				break;
			case ALIGN_RIGHT:
				r.left = w - size;
				r.right = w;
				break;
			case ALIGN_CENTER:
			default:
				r.left += (w - size);
				r.right -= (w - size);
				break;
		}

		// Always vertically align
		r.top += (h - size);
		r.bottom -= (h - size);

		return r;
	}

}
