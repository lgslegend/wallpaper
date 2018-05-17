package com.moe.LiveVisualizer.draw.circle;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.PointF;
import android.graphics.LinearGradient;

public class CircleDisperseDraw extends RingDraw
{
	private float[] points;
	
	public CircleDisperseDraw(ImageDraw draw, LiveWallpaper.WallpaperEngine engine)
	{
		super(draw, engine);
	}
	
	@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0:
				switch ( getEngine().getColorList().size() )
				{
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode,false);
						break;
					default:
						final int layer=canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
								drawGraph(getFft(), canvas, color_mode,false);
								if ( getShader() == null )
									setShader( new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null));
								if ( getShaderBuffer() == null )
								{
									setShaderBuffer( Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444));
									Canvas shaderCanvas=new Canvas(getShaderBuffer());
									paint.setShader(getShader());
									shaderCanvas.drawRect(0, 0, shaderCanvas.getWidth(), shaderCanvas.getHeight(), paint);
									paint.setShader(null);
								}
								paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
								//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
								canvas.drawBitmap(getShaderBuffer(), 0, 0, paint);
								//paint.setShader(null);
								paint.setXfermode(null);
								canvas.restoreToCount(layer);
								//canvas.drawBitmap(src, 0, 0, paint);
								//src.recycle();
								/*if ( shader == null )
								 shader = new SweepGradient(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, getEngine().getColorList().toArray(), null);
								 paint.setShader(shader);
								 drawLines(getFft(), canvas, false,color_mode);
								 paint.setShader(null);*/
								break;
							}
				break;
			case 1:
			case 2:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 3:
				int color=getColor();
				paint.setColor(getEngine().getSharedPreferences().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(), canvas, color_mode,false);
				paint.setShadowLayer(0, 0, 0, 0);
				break;
		}
		drawCircleImage(canvas);

	}

	@Override
	public void drawGraph(byte[] buffer, Canvas canvas, int color_mode, boolean useMode)
	{
		PointF point=getPointF();
		float radius=getRadius();
		final float radialHeight=getDirection()==OUTSIDE?getBorderHeight():getRadius();
		Paint paint=getPaint();
		if ( points == null || points.length != size() )
			points = new float[size()];
		int colorStep=0;
		float degress_step=360f / size();
		canvas.save();
		final PointF center=getPointF();
		canvas.rotate(degress_step / 2f, center.x, center.y);
		for ( int i=0;i < size();i ++ )
		{
			if ( useMode )
				if ( color_mode == 1 )
				{
					paint.setColor(getEngine().getColorList().get(colorStep));
					colorStep++;
					if ( colorStep >= getEngine().getColorList().size() )colorStep = 0;
				}
				else if ( color_mode == 2 )
				{
					paint.setColor(0xff000000 | (int)(Math.random() * 0xffffff));
				}
			float height=(float) (buffer[i] / 127d * radialHeight);
			if ( height > points[i] )
				points[i] = height;
			else
				height = points[i] - (points[i] - height) * getDownSpeed();
			if ( height < 0 )height = 0;
			points[i] = height;
			if(paint.getStrokeCap()==Paint.Cap.ROUND)
				canvas.drawLine(point.x,point.y-radius+(getDirection()==OUTSIDE?- height:height), point.x, point.y -radius+(getDirection()==OUTSIDE?- height:height), paint);
			else
				canvas.drawRect(point.x-paint.getStrokeWidth()/2,  point.y -radius+(getDirection()==OUTSIDE?- height:height), point.x + paint.getStrokeWidth()/2, point.y -radius+(getDirection()==OUTSIDE?- height-paint.getStrokeWidth():height+paint.getStrokeWidth()), paint);
			canvas.rotate(degress_step, center.x, center.y);
			//degress+=degress_step;
		}
		canvas.restore();
	}


	
}
