package njscky.psjc;

import android.os.Bundle;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.View.OnClickListener;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.widget.Button;


/**
 * Created by Administrator on 2015/10/8.
 */
public class speed extends AppCompatActivity implements OnClickListener, SoundPool.OnLoadCompleteListener  {

    SoundPool sndPool ;
    int sndid ;
    int[] StreamID =new int[10] ;
    int StreamNum = 0;
    int StreamNumPause = 0  ;
    int[] rid = new int[]{R.raw.iremembershort1,R.raw.in_call_alarm,R.raw.down,R.raw.down,R.raw.down,R.raw.down,
            R.raw.down,R.raw.down,R.raw.down,R.raw.down} ;

    private static final int SOUND_LOAD_OK = 1;
    private final Handler mHandler = new MyHandler() ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed);
        Button btnClose = (Button) findViewById(R.id.btnSpeedclose);
        btnClose.setOnClickListener(this);

        sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC,0 ) ;
        sndPool.setOnLoadCompleteListener(this);

        if( sndPool != null )
            sndid = sndPool.load( this , rid[StreamNum] , 1 ) ;
    }
    public void onDestroy()
    {
        sndPool.release() ;
        super.onDestroy();
    }
    private class MyHandler extends Handler {
        public void handleMessage(Message msg){
            switch( msg.what){
                case SOUND_LOAD_OK:
                    StreamID[StreamNum] = sndPool.play( msg.arg1, (float)0.8,(float)0.8, 16, 2, (float)1.0) ;
                    StreamNum ++ ;
                    break;
            }
        }
    }
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status)  {
        Message msg = mHandler.obtainMessage(SOUND_LOAD_OK);
        msg.arg1 = sampleId ;
        mHandler.sendMessage(msg);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSpeedclose :
                if( StreamNumPause< StreamNum )	{
                    sndPool.pause(StreamID[StreamNumPause]) ;
                    StreamNumPause ++ ;
                }
                this.finish();
                break;
        }
    }
}
