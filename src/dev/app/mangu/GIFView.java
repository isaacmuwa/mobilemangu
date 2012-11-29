package dev.app.mangu;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GIFView extends View{        
    private Movie movie;    
    private long moviestart;  
    public GIFView(Context context) throws IOException {  
        super(context);
    movie=Movie.decodeStream(getResources().openRawResource(R.drawable.loading));
    }
    public GIFView(Context context, AttributeSet attrs) throws IOException{
        super(context, attrs);
    movie=Movie.decodeStream(getResources().openRawResource(R.drawable.loading));
    }
    public GIFView(Context context, AttributeSet attrs, int defStyle) throws IOException {
        super(context, attrs, defStyle);
        movie=Movie.decodeStream(getResources().openRawResource(R.drawable.loading));
    }
@Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    long now=android.os.SystemClock.uptimeMillis();
    Paint p = new Paint();
    p.setAntiAlias(true);
    if (moviestart == 0) 
            moviestart = now;
            int relTime;
            relTime = (int)((now - moviestart) % movie.duration());
            movie.setTime(relTime);
            movie.draw(canvas,0,0);
            this.invalidate();
       }                         
}   
/*
public class GIFView extends View{
	
	Movie movie;
	InputStream is = null;
	long moviestart;
	

	
	public GIFView(Context context) {
	    super(context);
	}

	public GIFView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public GIFView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	private int gifId;

	public void setGIFResource(int resId) {
	    this.gifId = resId;
	    initializeView();
	}

	public int getGIFResource() {
	    return this.gifId;
	}

	private void initializeView() {
	    if (gifId != 0) {
	        InputStream is = getContext().getResources().openRawResource(gifId);
	        movie = Movie.decodeStream(is);
	        moviestart = 0;
	        this.invalidate();
	    }
	}
	
	private void setAttrs(AttributeSet attrs) {
	    if (attrs != null) {
	        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GIFView, 0, 0);
	        String gifSource = a.getString(R.styleable.GIFView_src);
	        //little workaround here. Who knows better approach on how to easily get resource id - please share
	        String sourceName = Uri.parse(gifSource).getLastPathSegment().replace(".gif", "");
	        setGIFResource(getResources().getIdentifier(sourceName, "drawable", getContext().getPackageName()));
	        a.recycle();
	    }
	   
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	canvas.drawColor(Color.TRANSPARENT);
		
		Awesomeness.println("drawing");
		
	//	canvas.drawColor(0xFFCCCCCC);
	    super.onDraw(canvas);
	    long now = android.os.SystemClock.uptimeMillis();
	    if (moviestart == 0) {
	        moviestart = now;
	    }
	    if (movie != null) {
	        int relTime = (int) ((now - moviestart) % movie.duration());
	        movie.setTime(relTime);
	        movie.draw(canvas, getWidth() - movie.width(), getHeight() - movie.height());
	        this.invalidate();
	    }
		/*
		canvas.drawColor(0xFFCCCCCC);
		super.onDraw(canvas);
		long now=android.os.SystemClock.uptimeMillis();
		System.out.println("now="+now);
		 if (moviestart == 0) {   // first time
             moviestart = now;
             
         }
		
		 System.out.println("\tmoviestart="+moviestart);
		 try
		 {
		 int relTime = (int)((now - moviestart) % movie.duration()) ;
		// int relTime1=(int)((now - moviestart1)% movie1.duration());
		 System.out.println("time="+relTime+"\treltime="+movie.duration());
         movie.setTime(relTime);
         movie.draw(canvas,10,10);
         this.invalidate();
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
	}
}
*/