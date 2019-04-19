package ru.cpc.smartflatview;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.idis.android.redx.ConnectionType;
import com.idis.android.redx.PlaySpeed;
import com.idis.android.redx.RAudioFormat;
import com.idis.android.redx.RDate;
import com.idis.android.redx.RDateTime;
import com.idis.android.redx.RDisconnectInfo;
import com.idis.android.redx.REvent;
import com.idis.android.redx.RPtzPreset;
import com.idis.android.redx.RSize;
import com.idis.android.redx.RStatus;
import com.idis.android.redx.RString;
import com.idis.android.redx.RTime;
import com.idis.android.redx.RUpgradeDevice;
import com.idis.android.redx.RUpgradeFile;
import com.idis.android.redx.WhyDisconnected;
import com.idis.android.redx.core.RCore;
import com.idis.android.redx.searcher.RSearcher;
import com.idis.android.redx.searcher.RSearcherListener;
import com.idis.android.redx.util.PeerMemory;
import com.idis.android.redx.watcher.PTZMoveCommand;
import com.idis.android.redx.watcher.PTZMoveMethod;
import com.idis.android.redx.watcher.PTZUseCommand;
import com.idis.android.redx.watcher.RWatcher;
import com.idis.android.redx.watcher.RWatcherListener;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import ru.IDIS.Ips;
import ru.IDIS.ScreenLayout;
import ru.IDIS.view.VideoBitmapPool;

import static android.content.Context.ACTIVITY_SERVICE;

public class IDISWatchDialog extends DialogFragment
        implements
        RWatcherListener,RSearcherListener,OnClickListener,IDISWatchDialogFullScreenExitListener
{
    public int mMode=0; //режим функционирования. 0= просмотр. 1-архив
    public int mPlay=-1;//идет просмотр архива(1), остановлен просмотр(0), не архив(-1)
    private boolean bZoomGo=false;// в данный момент осуществляется зум
    public RTime lastTime=null;//
    public static final String TAG = "Glindor IDISWatchDialog";
    public static final String TAG2 = "Glindo2 IDISWatchDialog";
    // Debug Log
    private static final boolean DEBUG_LOG = false;

    private ScreenLayout mScreenLayout;

    private RStatus _status = null;
    private REvent[] mEvents;
    private RSearcher mSearcher = null;
    private RWatcher mRWatcher = null;
    private CamVideo mCamVideo = null;
    private RDate[] mRecordedDates = null;
    private boolean[] mRecordedTimes = null;

    View viewBig=null;
    private Button butWitch=null;
    private Button butSearch=null;


    private static final boolean IPS_LOG = false;
    private Ips _ips = IPS_LOG ? new Ips() : null;

    private int mWatchSelectedCamera = -1;
    private int mSearchSelectedCamera = -1;
    private int mCountOfCamera = 64;

    public void Init(CamVideo camVideo)
    {
        mCamVideo = camVideo;
    }

    private VerticalTextView mDateTimeTextView;
    private VerticalTextView mNoCarrierTextView;
    private TextView tv1;

    public void RedrowPanel(boolean watch){
        Log.v(TAG,"Инициализация(+) панели IDIS - стандарт watch=" + watch);

//        tv1.setText("---");
        if(!watch)
        {
            Button bb1=null;
            bb1 = (Button)viewBig.findViewById(R.id.button11);
            bb1.setEnabled(true);
            bb1.setVisibility(Button.VISIBLE);
            bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button12);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button13);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button14);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button15);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button16);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button17);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button3);bb1.setEnabled(true);bb1.setVisibility(Button.VISIBLE);bb1.invalidate();

            bb1 = (Button)viewBig.findViewById(R.id.button4);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button5);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button6);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button7);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button8);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button18);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();
            bb1 = (Button)viewBig.findViewById(R.id.button19);bb1.setEnabled(false);bb1.setVisibility(Button.GONE);bb1.invalidate();

        }
        else{
            Button bb=null;

            bb = (Button)viewBig.findViewById(R.id.button11);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button12);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button13);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button14);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button15);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button16);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button17);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button3);bb.setEnabled(false);bb.setVisibility(Button.GONE);bb.invalidate();

            bb = (Button)viewBig.findViewById(R.id.button4);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button5);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button6);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button7);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button8);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button18);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();
            bb = (Button)viewBig.findViewById(R.id.button19);bb.setEnabled(true);bb.setVisibility(Button.VISIBLE);bb.invalidate();

        }

        Log.v(TAG,"Инициализация(-) панели IDIS - стандарт watch=" + watch);

    }

    public void onClick(View v)
    {
        Log.v(TAG2,"onClick(+)" + v.getId()+" ");

//        tv1.setText("--");
        switch (v.getId()) {
            case R.id.base_watch_container:
                Log.v(TAG, "onClick(+) base_watch_container");
            case R.id.noCarrierTextView:
                Log.v(TAG, "onClick(+) noCarrierTextView");

                IDISWatchDialogFS dlg = new IDISWatchDialogFS();
                if (mMode == 0)
                    dlg.Init(mRWatcher, this);
                else
                    dlg.Init(mSearcher, this);
                dlg.show(getFragmentManager(), "dlgFS");
                break;
            case R.id.button18:
                Log.v(TAG, "onClick(+) button18");

                if ((mRWatcher != null) && (mRWatcher.isConnected())) {
                    if (bZoomGo) {
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_STOP_ZOOM, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_FOCUS_ONEPUSH, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                    } else
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_ZOOM_OUT, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                    bZoomGo = !bZoomGo;

                } else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button8:
                Log.v(TAG, "onClick(+) button8");

                if ((mRWatcher != null) && (mRWatcher.isConnected())) {
                    if (bZoomGo) {
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_STOP_ZOOM, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_FOCUS_ONEPUSH, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                    } else
                        mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_ZOOM_IN, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                    bZoomGo = !bZoomGo;

                } else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button19:
                Log.v(TAG, "onClick(+) button19");

                if ((mRWatcher != null) && (mRWatcher.isConnected())) {
                    mRWatcher.movePTZ(mCamVideo.m_iCamID, PTZMoveCommand.PTZ_MOVE_STOP, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
//                    mRWatcher.usePTZ(mCamVideo.m_iCamID, PTZUseCommand.PTZ_FOCUS_ONEPUSH, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                    bZoomGo = false;
                } else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button4:
                Log.v(TAG, "onClick(+) button4");

                if ((mRWatcher != null) && (mRWatcher.isConnected())) {
                    mRWatcher.movePTZ(mCamVideo.m_iCamID, PTZMoveCommand.PTZ_MOVE_S, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                } else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button6:
                Log.v(TAG, "onClick(+) button6");

                if ((mRWatcher != null) && (mRWatcher.isConnected()))
                    mRWatcher.movePTZ(mCamVideo.m_iCamID, PTZMoveCommand.PTZ_MOVE_N, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button5:
                Log.v(TAG, "onClick(+) button5");

                if ((mRWatcher != null) && (mRWatcher.isConnected()))
                    mRWatcher.movePTZ(mCamVideo.m_iCamID, PTZMoveCommand.PTZ_MOVE_W, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button7:
                Log.v(TAG, "onClick(+) button7");

                if ((mRWatcher != null) && (mRWatcher.isConnected()))
                    mRWatcher.movePTZ(mCamVideo.m_iCamID, PTZMoveCommand.PTZ_MOVE_E, PTZMoveMethod.PTZ_MOVE_CONTINUOUS);
                else
                    Toast.makeText(getActivity(), "mRWatcher NOT enable", Toast.LENGTH_SHORT).show();
                break;
/*
            case R.id.button2:
                Log.v(TAG, "onClick(+) button2");

                final String[] speed_string = {
                        "HALF",
                        "x1",
                        "x2",
                        "x4",
                        "x8",
                        "x16",
                        "x32",
                };
                AlertDialog.Builder builder_set_speed = new AlertDialog.Builder(getActivity());
                builder_set_speed.setTitle("Play Speed");
                builder_set_speed.setSingleChoiceItems(speed_string, -1,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {

                                int speed = 0;

                                switch (item) {
                                    case 0:
                                        speed = PlaySpeed.PLAY_SPEED_HALF;
                                        break;
                                    case 1:
                                        speed = PlaySpeed.PLAY_SPEED_X1;
                                        break;
                                    case 2:
                                        speed = PlaySpeed.PLAY_SPEED_X2;
                                        break;
                                    case 3:
                                        speed = PlaySpeed.PLAY_SPEED_X4;
                                        break;
                                    case 4:
                                        speed = PlaySpeed.PLAY_SPEED_X8;
                                        break;
                                    case 5:
                                        speed = PlaySpeed.PLAY_SPEED_X16;
                                        break;
                                    case 6:
                                        speed = PlaySpeed.PLAY_SPEED_X32;
                                        break;
                                }

                                Log.v(TAG2, "onClick(button3) mSearcher.setSpeed(" + speed + ")");
                                mSearcher.setSpeed(speed);
                                dialog.dismiss();
                            }
                        });

                AlertDialog dlg_set_speed = builder_set_speed.create();
                dlg_set_speed.show();
                break;
                */
            case R.id.button3:
                Log.v(TAG, "onClick(+) button3");

                AlertDialog.Builder dItemBuilder = new AlertDialog.Builder(getActivity());
                dItemBuilder.setTitle("Выбор даты");
                final DatePicker dateP = new DatePicker(getActivity());

                dItemBuilder.setView(dateP);
//                dtItemBuilder.setView(dateP);

                dItemBuilder.setPositiveButton("Перейти к...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG2, "onClick(button3) mSearcher.requestTimeList(" + dateP.getYear() + "-" + dateP.getMonth() + "-" + dateP.getDayOfMonth() + ")");
                                mSearcher.requestTimeList(dateP.getYear(), dateP.getMonth(), dateP.getDayOfMonth());
//                                Toast.makeText(getActivity(), "requestTimeList("+dateP.getYear()+"-"+dateP.getMonth()+"-"+dateP.getDayOfMonth()+")", Toast.LENGTH_SHORT).show();
                                Log.v(TAG2, "onClick(button3) mSearcher.moveTo(12:00:00)");
                                RTime rt = new RTime();
                                rt.set(9, 0, 0);
                                mSearcher.moveTo(rt);
                                dialog.dismiss();
                            }
                        }

                );

                AlertDialog dtItem = dItemBuilder.create();
                dtItem.show();

                mNoCarrierTextView.setVisibility(View.VISIBLE);
                mNoCarrierTextView.setText(R.string.idisWaiting);

//                Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button12:
                Log.v(TAG, "onClick(+) button12");

                AlertDialog.Builder tItemBuilder = new AlertDialog.Builder(getActivity());
                tItemBuilder.setTitle(R.string.idisTimeSelect);
                final TimePicker timeP = new TimePicker(getActivity());
//                final DatePicker dateP = new DatePicker(getActivity());

                timeP.setCurrentHour(lastTime.hour());
                timeP.setCurrentMinute(lastTime.minute());

                tItemBuilder.setView(timeP);
//                dtItemBuilder.setView(dateP);

                tItemBuilder.setPositiveButton(R.string.idisGoTo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lastTime.set(timeP.getCurrentHour(), timeP.getCurrentMinute(), 0);
                                Log.v(TAG2, "onClick(button12) mSearcher.moveTo(" + lastTime.toString() + ")");
                                mSearcher.moveTo(lastTime);
//                                tv1.setText("!"+timeP.getCurrentHour()+"!"+timeP.getCurrentMinute()+"!");
                                dialog.dismiss();
                            }
                        }

                );

                AlertDialog tItem = tItemBuilder.create();
                tItem.show();

                mNoCarrierTextView.setVisibility(View.VISIBLE);
                mNoCarrierTextView.setText(R.string.idisWaiting);

//                Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button11:
                Log.v(TAG, "onClick(+) button11");

                Boolean isCon = mSearcher.isConnected();
                Log.v(TAG, "onClick(button11) isConnected=" + isCon);
                if ((mSearcher != null) && isCon) {
                    Log.v(TAG2, "onClick(button11) mSearcher.play()");
                    mSearcher.play();
                    mNoCarrierTextView.setVisibility(View.VISIBLE);
                    mNoCarrierTextView.setText(R.string.idisWaiting);
                    searchPress(1);
//                    Toast.makeText(getActivity(), "play", Toast.LENGTH_SHORT).show();
                } else {
                    searchPress(1);
                    Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button17:
                Log.v(TAG, "onClick(+) button17");

                Boolean isCon2 = mSearcher.isConnected();
                Log.v(TAG, "onClick(button17) isConnected=" + isCon2);
                if ((mSearcher != null) && (isCon2)) {
                    Log.v(TAG2, "onClick(button17) mSearcher.stop()");
                    mSearcher.stop();
                    searchPress(2);
//                    Toast.makeText(getActivity(), "stop", Toast.LENGTH_SHORT).show();
                } else {
                    searchPress(2);
                    Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button13:
                Log.v(TAG, "onClick(+) button13");

                if ((mSearcher != null) && (mSearcher.isConnected())) {
                    Log.v(TAG2, "onClick(button13) mSearcher.moveToFirst()");
                    mSearcher.moveToFirst();
                    mNoCarrierTextView.setVisibility(View.VISIBLE);
                    mNoCarrierTextView.setText(R.string.idisWaiting);

                    //                   Toast.makeText(getActivity(), "moveToFirst", Toast.LENGTH_SHORT).show();
//                    mSearcher.play();
//                    Toast.makeText(getActivity(), "play", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button14:
                Log.v(TAG, "onClick(+) button14");

                if ((mSearcher != null) && (mSearcher.isConnected())) {
                    Log.v(TAG2, "onClick(button14) mSearcher.rewind()");
                    mSearcher.rewind(0);
                    searchPress(1);
                } else {
                    searchPress(1);
                    Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button15:
                Log.v(TAG, "onClick(+) button15");

                if ((mSearcher != null) && (mSearcher.isConnected())) {
                    Log.v(TAG2, "onClick(button15) mSearcher.fastforward()");
                    searchPress(1);
                    mSearcher.fastforward(0);
                } else {
                    searchPress(1);

                Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button16:
                Log.v(TAG,"onClick(+) button16");

                if((mSearcher!=null)&&(mSearcher.isConnected()))
                {
                    Log.v(TAG2,"onClick(button15) mSearcher.moveToLast()");
                    mSearcher.moveToLast();
                    mNoCarrierTextView.setVisibility(View.VISIBLE);
                    mNoCarrierTextView.setText(R.string.idisWaiting);

//                    mSearcher.play();
//                    Toast.makeText(getActivity(), "play", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(), "Seaching NOT enable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button9://Witch
                Log.v(TAG,"onClick(+) button9");

                if((mSearcher!=null)&&(mSearcher.isConnected()))
                {
                    Log.v(TAG2,"onClick(+) mSearcher.disconnect()");
                    mSearcher.disconnect();
                }

                mMode = 0;
                butWitch.setEnabled(false);
//                butSearch.setEnabled(true);
                Connect();

                RedrowPanel(true);
                break;
            case R.id.button10://Search
                Log.v(TAG,"onClick(+) button10");

                ConstraintSet set = new ConstraintSet();
                ConstraintLayout cl = (ConstraintLayout)viewBig.findViewById(R.id.cl1);
                set.clone(cl);
                set.applyTo(cl);

                if((mRWatcher!=null)&&(mRWatcher.isConnected()))
                {
                    Log.v(TAG2,"onClick(+) mRWatcher.disconnect()");
                    mRWatcher.disconnect();
                }
                mMode = 1;
//                butWitch.setEnabled(true);
                butSearch.setEnabled(false);

                Connect();
//                searchPress(3);
                RedrowPanel(false);


                break;
        }
        Log.v(TAG,"onClick(-)" + v.getId());

    }
    private void searchPress(int type)
    {
        Log.v(TAG2,"searchPress " +type);
//        type = 1; //нажали на клавишу плей, все заложено кроме стоп
//        type = 2; //нажали на клавишу стоп, все разлочели
        //type = 3 //все залоченыж

        Button bb=null;
        boolean stopPress = type==2;
        int buttonID[] = new int[]{R.id.button11,R.id.button12,R.id.button13,R.id.button14,R.id.button15,R.id.button16,R.id.button3};

        int buttonDA100[] = new int[]{R.drawable.play,R.drawable.time,R.drawable.skip_back,R.drawable.rewind,R.drawable.forward,R.drawable.skip_frwd,R.drawable.calendar};
        int buttonDA25[] = new int[]{R.drawable.play_na,R.drawable.time_na,R.drawable.skip_back_na,R.drawable.rewind_na,R.drawable.forward_na,R.drawable.skip_frwd_na,R.drawable.calendar_na};

        int buttonID_stop = R.id.button17;

        for (int i=0;i<buttonID.length;i++) {
            int tid = buttonID[i];
            bb = (Button)viewBig.findViewById(tid);
            bb.setEnabled(stopPress);
//            bb.setVisibility(stopPress?View.VISIBLE:View.INVISIBLE);
            bb.setBackgroundResource( stopPress ? buttonDA100[i] : buttonDA25[i] );
            bb.invalidate();
        }
        bb = (Button)viewBig.findViewById(buttonID_stop);
        bb.setBackgroundResource(type==1 ? R.drawable.pause:R.drawable.pause_na);
        bb.setEnabled(type==1);
        bb.invalidate();

/*        bb = (Button)viewBig.findViewById(R.id.button11);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button12);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button13);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button14);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button15);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button16);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button2);bb.setEnabled(stopPress);bb.invalidate();
        bb = (Button)viewBig.findViewById(R.id.button3);bb.setEnabled(stopPress);bb.invalidate();
        */




    }

    public void Connect()
    {
        Log.v(TAG,"Connect(+)");
        mNoCarrierTextView.setVisibility(View.VISIBLE);
        mNoCarrierTextView.setText(R.string.idisWaiting);

        //RCore.getInstance().setResolution(new RSize(VideoBitmapPool.DEFAULT_ELEMENT_WIDTH, VideoBitmapPool.DEFAULT_ELEMENT_HEIGHT));
//        RCore.getInstance().setResolution(new RSize());
        if(mMode == 0)
        {
            if(mRWatcher==null)
                mRWatcher = new RWatcher();
            mRWatcher.setListener(this);
            int res = mRWatcher.connect(mCamVideo.mConnectionType, mCamVideo.m_sIP, mCamVideo.m_iPort, mCamVideo.m_sLogin, mCamVideo.m_sPass, mCamVideo.mUnityPort);
            Log.v(TAG2,"Connect mRWatcher.connect = "+res);
            this.RedrowPanel(true);

        }
        else
        {
            if(mSearcher==null)
                mSearcher = new RSearcher();
            mSearcher.setListener(this);
            int res = mSearcher.connect(mCamVideo.mConnectionType, mCamVideo.m_sIP, mCamVideo.m_iPort, mCamVideo.m_sLogin, mCamVideo.m_sPass, mCamVideo.mUnityPort);
            Log.v(TAG2,"Connect mSearcher.connect = "+res);
            this.RedrowPanel(false);

        }


        Log.v(TAG,"Connect(-)");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView(+)");

        getDialog().setTitle(R.string.idisVideo);

        VideoBitmapPool.initialize(null);

        mMode = 0;
        viewBig = inflater.inflate(R.layout.idis_watch, null);

        mNoCarrierTextView = (VerticalTextView) viewBig.findViewById(R.id.noCarrierTextView);

        mDateTimeTextView = (VerticalTextView) viewBig.findViewById(R.id.dateTimeTextView);
        setDateTime(0, 0, 0, 0, 0, 0);
 //       tv1 = (TextView) viewBig.findViewById(R.id.textViewLOG);
        lastTime = new RTime(0,0,0);

        butWitch = (Button) viewBig.findViewById(R.id.button9);
        butWitch.setOnClickListener(this);

        butSearch = (Button) viewBig.findViewById(R.id.button10);
        butSearch.setOnClickListener(this);
        Button bb;
        bb = (Button)viewBig.findViewById(R.id.button11);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button12);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button13);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button14);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button15);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button16);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button17);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button3);bb.setOnClickListener(this);

        bb = (Button)viewBig.findViewById(R.id.button4);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button5);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button6);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button7);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button8);bb.setOnClickListener(this);

        bb = (Button)viewBig.findViewById(R.id.button18);bb.setOnClickListener(this);
        bb = (Button)viewBig.findViewById(R.id.button19);bb.setOnClickListener(this);

        FrameLayout contentHolder = (FrameLayout) viewBig.findViewById(
                R.id.base_watch_container);
        contentHolder.setOnClickListener(this);
        mScreenLayout = new ScreenLayout(contentHolder.getContext());

        if (mCamVideo.mConnectionType == ConnectionType.VIA_DVRNS) {
            if (!RCore.getInstance().isFenClientManagerStarted()) {
                RCore.getInstance().startupFenClientManager();
            }
            RCore.getInstance().setDvrnsServer("dvrnames.net", 10088);
        }
        contentHolder.removeAllViews();
        contentHolder.addView(mScreenLayout);

        Connect();
/*        mRWatcher = new RWatcher();
        mRWatcher.setListener(this);
        int res = mRWatcher.connect(mCamVideo.mConnectionType, mCamVideo.m_sIP, mCamVideo.m_iPort, mCamVideo.m_sLogin, mCamVideo.m_sPass, mCamVideo.mUnityPort);
*/
//        if (res < 0) {
//            Toast.makeText(this, "Unable to connect to the site", Toast.LENGTH_SHORT).show();
//            //finish();
//        }
        Log.v(TAG,"onCreateView(-)");

        return viewBig;
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG,"onDestroyView(+)");

        if(mMode==0) {
            if (mRWatcher != null) {
                mRWatcher.setListener(null);
                mRWatcher.disconnect();
                mRWatcher.destroy();
                mRWatcher = null;
            }
        }
        if(mMode==1)
        {
            if (mSearcher != null) {
                mSearcher.setListener(null);
                mSearcher.disconnect();
                mSearcher.destroy();
                mSearcher = null;
            }
        }
//        if (misFenCleanupRequired) {
//            onAppBackground();
//        }
        super.onDestroyView();
        Log.v(TAG,"onDestroyView(-)");

    }

    @Override
    public void onConnected() {
        Log.v(TAG,"onConnected(+)");

        if (IPS_LOG) {
            _ips.reset();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mNoCarrierTextView.setText("НЕТ СВЯЗИ");
                mNoCarrierTextView.setVisibility(View.GONE);
            }
        });
        if(mMode==0)
        {
            mCountOfCamera = mRWatcher.cameraCount();
            boolean camList[] = new boolean[mCountOfCamera];
            Arrays.fill(camList, false);
            camList[mCamVideo.m_iCamID] = true;
            mRWatcher.setCameraList(camList, mCountOfCamera);
            mWatchSelectedCamera = mCamVideo.m_iCamID;
        }
        else
        {
            mCountOfCamera = mSearcher.cameraCount();
            boolean camList[] = new boolean[mCountOfCamera];
            Arrays.fill(camList, false);
            camList[mCamVideo.m_iCamID] = true;
            mSearcher.setCameraList(camList);
            mSearchSelectedCamera = mCamVideo.m_iCamID;
        }
        Log.v(TAG,"onConnected(-)");

    }

    @Override
    public void onDisconnected(final int whyDisconnect, int invalidLoginCount, RDisconnectInfo attachment) {
        Log.v(TAG, "onDisconnected(+)");
//        if (DEBUG_LOG) {
            Log.v(TAG, "onDisconnected+");
            Log.v(TAG, "Disconnected Reason = " + whyDisconnect);
            Log.v(TAG, "invalidLoginCount = " + invalidLoginCount);
            Log.v(TAG, "lastLoginTime = " + attachment.lastLoginTime().toString());

//        }


        if (IPS_LOG) {
            _ips.reset();
        }

        mWatchSelectedCamera = -1;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "onDisconnected+run()");
                if (whyDisconnect != WhyDisconnected.LOGOUT) {
                    Log.v(TAG, "onDisconnected+run()if");
                    mNoCarrierTextView.setVisibility(View.VISIBLE);
                    mNoCarrierTextView.setText(R.string.idisNoCarrier);
                    mNoCarrierTextView.invalidate();
//                    Toast.makeText(getActivity(), "Watch : connection failed " + whyDisconnect, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "onDisconnected-run()if");

                }
                Log.v(TAG, "onDisconnected-run()");

//                if (!isFinishing()) {
  //                  finish();
    //            }
            }
        });
        Log.v(TAG, "onDisconnected(-)");

    }

    @Override
    public void onStatusLoaded(RStatus status) {
        Log.v(TAG,"onStatusLoaded(+)");

        if (_status != null) {
            synchronized (_status) {
                _status = status;
            }
        }
        else {
            _status = status;
        }
        Log.v(TAG,"onStatusLoaded(-)");

    }

    public void setDateTime(int YYYY, int MM, int DD, int hh, int mm, int ss) {
        Log.v(TAG,"setDateTime(+)");

        String datetime = String.format(Locale.US,"%04d-%02d-%02d %02d:%02d:%02d", YYYY, MM, DD, hh, mm, ss);
        if(mMode==1)
            datetime = String.format(Locale.US,"%04d-%02d-%02d %02d:%02d:%02d", YYYY, MM, DD, hh, mm, ss);
        mDateTimeTextView.setText(datetime);
        mDateTimeTextView.invalidate();
        Log.v(TAG,"setDateTime(-)");

    }

    private boolean mFrameBusy = false;
    private static final Object lock1 = new Object();

    @Override
    public void onFrameLoaded(final int camera, final RString title, final RDateTime dateTime, final long peerBuffer, final RSize cvtSize, final RSize originSize)
    {
        final int rnd = (int)(Math.random()*1000);

        Log.v(TAG,"onFrameLoaded(+)"+rnd);
        Log.v(TAG2,"onFrameLoaded !1-7! "+rnd);

        if(mMode==1) {
            if (DEBUG_LOG) {
                Log.v(TAG, "onFrameLoaded ARCH");
            }
        }
        else
            if (DEBUG_LOG) {
                Log.v(TAG, "onFrameLoaded");
            }
//        Log.v(TAG,"onFrameLoaded !2! ");

        if (IPS_LOG) {
            if (!_ips.isStarted()) {
                _ips.start();
            }
            _ips.increment();
            float ips = _ips.getIps();
            Log.v(TAG, "onFrameLoaded " + cvtSize.toString() + "(" + ips + "ips)");
        }
//        Log.v(TAG,"onFrameLoaded !3! ");

        if(mMode==0){
            if (mWatchSelectedCamera != camera) {
                if (DEBUG_LOG) {
                    Log.v(TAG, "invalid frame loaded");
                }
                return;
            }
        }
        else {
            if (mSearchSelectedCamera != camera) {
                if (DEBUG_LOG) {
                    Log.v(TAG, "invalid frame loaded");
                }
                return;
            }
        }
//        Log.v(TAG,"onFrameLoaded !4! ");

        if (title != null) {
            if (DEBUG_LOG) {
                Log.v(TAG, "frame title - " + title.toString() + (title.isUTF8() ? "(UTF-8)" : "(Windows)"));
            }
        }
//        Log.v(TAG,"onFrameLoaded !5! ");

        // waiting for ScreenLayout instantiation.
        // this wait routine is applied only to the first frame.
        while (mScreenLayout == null) {
            try {
                if (DEBUG_LOG) {
                    Log.v(TAG, "waiting 500ms...");
                }

                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                if (DEBUG_LOG) {
                    Log.e(TAG, "screenLayout is missing");
                }

                e.printStackTrace();
            }
        }
//        Log.v(TAG,"onFrameLoaded !6! ");

        if (DEBUG_LOG) {
            Log.v(TAG, "Shoot to ScreenView.drawImage");
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "converted image");
        }
//        Log.v(TAG,"onFrameLoaded !7! ");

        if(mFrameBusy) {
            Log.v(TAG,"onFrameLoaded(-) mFrameBusy");
            return;
        }
        Log.v(TAG,"onFrameLoaded !8! "+rnd);

        mFrameBusy = true;

        if (mScreenLayout != null) {
            if (DEBUG_LOG) {
                Log.v(TAG, "Shoot to ScreenView.drawImage");
            }

            Log.v(TAG2, "onFrameLoaded " + cvtSize.toString()+" "+ originSize.toString()+" "+ title);
            Log.v(TAG,"onFrameLoaded !9! "+rnd);

            try
            {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
                if (activityManager != null)
                {
                    activityManager.getMemoryInfo(mi);
                }
                final Integer  needMem = cvtSize.width() * cvtSize.height() * 4;
                Log.v(TAG, "onFrameLoaded freemem = " + mi.availMem + "   нужно = " + needMem);
                Log.v(TAG,"onFrameLoaded !10! "+rnd);

                if(mi.availMem > cvtSize.width() * cvtSize.height() * 4)
                {
//                    final byte[] image_to_draw = new byte[cvtSize.width() * cvtSize.height() * 2];
//                    synchronized(lock1) {
                        Log.v(TAG,"onFrameLoaded !10! memory copy "+rnd);

//                        byte[] image_to_draw = new byte[cvtSize.width() * cvtSize.height() * 2];
                        Log.v(TAG,"onFrameLoaded !10! memory copy1 "+rnd);
                        final byte[] image_to_draw = PeerMemory.allocateByteArray(peerBuffer,cvtSize.width() * cvtSize.height() * 2);
  //                  }
                    Log.v(TAG,"onFrameLoaded !11! "+rnd);

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
//                            int rnd2 = (int)(Math.random()*1000);
                            Log.v(TAG,"onFrameLoaded !12! "+rnd);
                            if (DEBUG_LOG)
                            {
                                Log.v(TAG, "started posting...");
                            }
                            mNoCarrierTextView.setVisibility(View.GONE);
                            Log.v(TAG,"onFrameLoaded !13! "+rnd);

/*                    if(mMode==0)
                        tv1.setText("W FrameLoaded");
                    if(mMode==1)
                        tv1.setText("S FrameLoaded");
*/
//                    Toast.makeText(getActivity(), "frame on:" + cvtSize.height() + "X"+cvtSize.width()+" - "+ originSize.height() + "X"+originSize.width(), Toast.LENGTH_SHORT).show();
                            synchronized(lock1) {
                                Log.v(TAG,"onFrameLoaded !13! lock_in "+rnd);

                                mScreenLayout.drawImage(image_to_draw, cvtSize.width(), cvtSize.height());
                            }
                            Log.v(TAG,"onFrameLoaded !13.5! lock_out "+rnd);

                            setDateTime(dateTime.date().year(), dateTime.date().month(),
                                        dateTime.date().day(), dateTime.time().hour(),
                                        dateTime.time().minute(), dateTime.time().second());
                            Log.v(TAG,"onFrameLoaded !14! "+rnd);

                            if (DEBUG_LOG)
                            {
                                Log.v(TAG, "finished posting...");
                            }

                            Log.v(TAG2,"onFrameLoaded !15! "+rnd);

                            if(mMode==0){
                                butWitch.setEnabled(false);

                                if(needMem>5000000) {//это пришел архивный фрейм
                                    butSearch.setEnabled(true);
                                }
                                else
                                    Log.v(TAG2,"onFrameLoaded !15! ГОНКА. А живом режиме пришел архивный фрейм "+rnd);

                            }
                            else{
                                if(needMem<5000000) {//это пришел архивный фрейм
                                    butWitch.setEnabled(true);
//                                    searchPress(1);
                                }
                                else
                                    Log.v(TAG2,"onFrameLoaded !15! ГОНКА. А архивном режиме пришел живой фрейм "+rnd);

                                butSearch.setEnabled(false);
                            }

                            mFrameBusy = false;
                        }
                    });
                }
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage());
                mFrameBusy = false;
            }
            Log.v(TAG, "onFrameLoaded_EXIT "+rnd);
        }
        Log.v(TAG,"onFrameLoaded(-) "+rnd);

    }

    @Override public void onFrameStopped(int var1){
        Log.v(TAG,"onFrameStopped(+-)");
        searchPress(2);

    }
    @Override public void onReceiveDateList(RDate[] dates)
    {
        Log.v(TAG,"onReceiveDateList(+)");

        if (DEBUG_LOG) {
            Log.v(TAG, "onReceiveDateList");
        }
        mRecordedDates = dates;
        Log.v(TAG,"onReceiveDateList(-)");

    }
    @Override public void onReceiveEventList(REvent[] var1){}
    @Override public void onNoFrameLoaded(){}
    @Override public void onReceiveTimeList(boolean[] times)
    {
        Log.v(TAG,"onReceiveTimeList(+)");
        if (DEBUG_LOG) {
            Log.v(TAG, "onReceiveTimeList");
        }
        mRecordedTimes = times;
        Log.v(TAG,"onReceiveTimeList(-)");
    }
    @Override public void onReceiveMinuteList(RDateTime var1, byte[] var2){}

    @Override public void onEventLoaded(REvent event) {}
    @Override public void onReceivePtzPreset(int camera, RPtzPreset[] presets) {}
    @Override public void onReceivePtzAutoFocusBegin(int camera) {}
    @Override public void onReceivePtzAutoFocusEnd(int camera) {}
    @Override public void onReceivePushEnabledResult(int pushEnabledResult) {}
    @Override public void onReceivePushEnabledStatus(boolean enabled) {}
    @Override public void onReceivePasswordReissueStartResponse(int result, RString operationId, int authRemainTime) {}
    @Override public void onReceivePasswordReissueStatusResponse(int result, RString operationId, int authRemainTime) {}
    @Override public void onReceivePasswordReissueAuthConfirmResponse(int result, RString operationId, RString securityCode) {}
    @Override public void onReceivePasswordReissueResponse(int result, RString operationId, RString info) {}
    @Override public void onReceivePasswordReissueCancelResponse(int result, RString operationId) {}
    @Override public void onReceiveRemoteUpgradeDeviceCheck(int status, RUpgradeDevice[] devices) {}
    @Override public void onReceiveRemoteUpgradeDeviceFileInfos(RUpgradeFile[] fileInfos) {}
    @Override public void onReceiveRemoteUpgradeStatus(int deviceId, int status, boolean done, int percentage) {}
    @Override public void onReceiveRemoteUpgradeDeviceCancel(int deviceId, int result) {}
    @Override public void onReceiveRawProtocol(long buf, int size) {}
    @Override public void onReceiveResponseGpbSetup(long buf, int size) {}
    @Override public void onReceiveResponseModifyGpbSetupResult(long buf, int size) {}
    @Override public void onReceivePasswordResetStartResponse(RString publicKey) {}
    @Override public void onReceivePasswordResetResponse(int result, int[] failedReason) {}
    @Override public void onReceiveUwbCommandResponse(long buf, int size) {}
    @Override public void onReceiveFormatResult(int result) {}

    @Override
    public void onAudioConnected() {
        Log.v(TAG,"onAudioConnected(+)");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

//                Toast.makeText(WatchScreenActivity.this, "Audio connected", Toast.LENGTH_SHORT).show();

//                if (_audioDlg != null) {
//                    _audioDlg.dismiss();
//                    _audioDlg = null;
//                }
//
//                AlertDialog dlg = AudioControlDialog.createDialog(WatchScreenActivity.this, new AudioControlDialog.OnAudioControlListener() {
//
//                    @Override
//                    public void onSpeakerEnabled(boolean enable) {
//                        // TODO Auto-generated method stub
//                        if (mRWatcher == null) {
//                            return;
//                        }
//                        if (_status.audioInStatus(AUDIO_CAMERA) == 0) {
//                            Toast.makeText(WatchScreenActivity.this, "Audio-in is not available", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        if (enable) {
//                            mRWatcher.startReceivingAudio(AUDIO_CAMERA);
//                        }
//                        else {
//                            mRWatcher.stopReceivingAudio(AUDIO_CAMERA);
//                        }
//                    }
//
//                    @Override
//                    public void onMicEnabled(boolean enable) {
//                        // TODO Auto-generated method stub
//                        if (mRWatcher == null) {
//                            return;
//                        }
//                        if (_status.audioOutStatus(AUDIO_CAMERA) == 0) {
//                            Toast.makeText(WatchScreenActivity.this, "Audio-out is not available", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        if (enable) {
//                            mRWatcher.startSendingAudio(AUDIO_CAMERA);
//                        }
//                        else {
//                            AudioRecorder.getInstance().stop();
//                            mRWatcher.stopSendingAudio(AUDIO_CAMERA);
//                        }
//                    }
//
//                    @Override
//                    public void onClose() {
//                        // TODO Auto-generated method stub
//                        if (mRWatcher == null) {
//                            return;
//                        }
//                        mRWatcher.disconnectAudio();
//                    }
//                });
//                dlg.show();
//
//                _audioDlg = dlg;
            }

        });
        Log.v(TAG,"onAudioConnected(-)");

    }

    @Override
    public void onAudioDisconnected(int reason) {

//        AudioPlayer.getInstance().stop();
//        AudioRecorder.getInstance().stop();
//
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//
//                Toast.makeText(WatchScreenActivity.this, "Audio disconnected", Toast.LENGTH_SHORT).show();
//
//                if (_audioDlg != null) {
//                    _audioDlg.dismiss();
//                    _audioDlg = null;
//                }
//            }
//        });
    }

    @Override
    public void onReceiveReceivingAudioFormat(RAudioFormat recvInfo) {
        Log.v(TAG,"onReceiveReceivingAudioFormat(+)");

        if (!recvInfo.isValid()) {
            Log.v(TAG, "onReceiveReceivingAudioFormat invalid");
            return;
        }

        Log.v(TAG, "onReceiveReceivingAudioFormat");

//        AudioPlayer.getInstance().initialize(recvInfo);
        Log.v(TAG,"onReceiveReceivingAudioFormat(-)");

    }

    @Override
    public void onReceiveSendingAudioFormat(RAudioFormat sendInfo) {
        Log.v(TAG,"onReceiveSendingAudioFormat(+)");

        if (!sendInfo.isValid()) {
            Log.v(TAG, "onReceiveSendingAudioFormat invalid");
            return;
        }
        Log.v(TAG, "onReceiveSendingAudioFormat");

//        AudioRecorder.getInstance().initialize(sendInfo, new AudioRecorder.OnAudioCapturedListener() {
//
//            @Override
//            public boolean onAudioCaptured(byte[] data, int dataLen) {
//                // TODO Auto-generated method stub
//
//                Log.v(TAG, "sendAudio " + dataLen);
//
//                mRWatcher.sendAudio(data, dataLen, WatchScreenActivity.AUDIO_CAMERA);
//                return false;
//            }
//        });
//        AudioRecorder.getInstance().start();
        Log.v(TAG,"onReceiveSendingAudioFormat(+)");

    }

    @Override
    public void onReceiveAudioData(byte[] data) {
        Log.v(TAG,"onReceiveAudioData(+)");

        Log.v(TAG, "onReceiveAudioData " + data.length);

        Log.v(TAG,"onReceiveAudioData(-)");
//        AudioPlayer.getInstance().play(data);
    }

    @Override
    public void OnFullScreenExit()
    {
        Log.v(TAG,"OnFullScreenExit(+)");

        if(mMode==0)
        {
            mRWatcher.setListener(this);
        }
        else
        {
            mSearcher.setListener(this);
        }
        Log.v(TAG,"OnFullScreenExit(-)");


        //RCore.getInstance().setResolution(new RSize(VideoBitmapPool.DEFAULT_ELEMENT_WIDTH, VideoBitmapPool.DEFAULT_ELEMENT_HEIGHT));
//        RCore.getInstance().setResolution(new RSize());

    }
}
