package ru.cpc.smartflatview;

import java.util.HashMap;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

public class IndicatorUI extends ViewGroup 
{
	private static final String TAG = "SMARTFLAT" ;
	
	protected View m_pOldView;
	//private View m_pNewView;
	protected TextView m_pText;
	protected TextView m_pText2;
	private View m_pCog = null;
	private View m_pShield = null;
	
	protected Animation m_pFadeInAnimation;
	protected Animation m_pFadeOutAnimation;
	
	static final int NONE = 0;
	static final int TAP  = 1;
	static final int DRAG = 2;
	int mode = NONE;
	
	private int m_iActivePointerID = -1;
	private PointF start = new PointF(0, 0);

	private Indicator m_pIndicator;
	
	Timer m_pTimer;
	GestureDetector gestureDetector;

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
        @Override
        public boolean onDown(MotionEvent e) {
            start.x = e.getX();
            start.y = e.getY();
            m_pIndicator.Pressed(e.getX(), e.getY());
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
			Log.d(TAG, m_pIndicator.m_sName + "(" + m_pIndicator.m_fXPercent + "," + m_pIndicator.m_fYPercent + ").onSingleTapUp");
            m_pIndicator.Released();
            return true;
        }

        // event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d(TAG, m_pIndicator.m_sName + "(" + m_pIndicator.m_fXPercent + "," + m_pIndicator.m_fYPercent + ").onDoubleTap");
			m_pIndicator.Released();

			m_pIndicator.GestureConfirmed();
			mode = NONE;

			String firewall = Prefs.getFirewall(getContext());

			if(m_pIndicator.m_iReaction == 2)
				return true;

			if(!m_pIndicator.m_bProtected || firewall.isEmpty())
				ControlPopup();
			else
			{
                LoginActivity.s_sTrueCode = firewall;
                LoginActivity.s_pUnlocker = new LoginActivity.SFUnlocker() {
                    @Override
                    public void Unlock(boolean bUnlock) {
                        if(bUnlock)
                            ControlPopup();
                    }
                };
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(intent);
//				Dialog vv = new Keypad(getContext(), this, firewall);
//				vv.show();
			}

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			Log.d(TAG, m_pIndicator.m_sName + "(" + m_pIndicator.m_fXPercent + "," + m_pIndicator.m_fYPercent + ").onSingleTapConfirmed");
			m_pIndicator.Released();
			m_pIndicator.GestureConfirmed();

			if(m_pIndicator.m_iReaction == 2)
				return true;

			String firewall = Prefs.getFirewall(getContext());
			if(!m_pIndicator.m_bProtected || firewall.isEmpty()) {
                if(m_pIndicator.m_bQuick)
                    SwitchOnOff();
                else
                    ControlPopup();
            }
			else
			{
                LoginActivity.s_sTrueCode = firewall;
                LoginActivity.s_pUnlocker = new LoginActivity.SFUnlocker() {
                    @Override
                    public void Unlock(boolean bUnlock) {
                        if(bUnlock) {
                            if(m_pIndicator.m_bQuick)
                                SwitchOnOff();
                            else
                                ControlPopup();
                        }
                    }
                };
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(intent);
//				Dialog vv = new Keypad(getContext(), this, firewall);
//				vv.show();
			}
			return true;
		}
	}

	private static HashMap<Integer, Float> s_KStore = new HashMap<Integer, Float>();

	protected static float GetK(Context context, int iResID, String indicator)
	{
		if(iResID == -1)
			return 1.0f;

		if(s_KStore.containsKey(iResID))
			return s_KStore.get(iResID);

		Log.d("11111111111", "Trying to read bitmap " + indicator);
		BitmapFactory.Options ops = new BitmapFactory.Options();
		//ops.inSampleSize = 16;
		ops.inJustDecodeBounds = true;

		Bitmap pBitmap = BitmapFactory.decodeResource(context.getResources(), iResID, ops);
		//float fK = (float)pBitmap.getWidth()/pBitmap.getHeight();
		float fK = (float)ops.outWidth/ops.outHeight;
		//pBitmap.recycle();
		pBitmap = null;

		s_KStore.put(iResID, fK);
		return fK;
	}

	Context initialContext;

	protected IndicatorUI(SFServer pServer, Context context, Indicator pIndicator)
	{
		super(context);

		initialContext = context;

		gestureDetector = new GestureDetector(context, new GestureListener());

		m_pIndicator = pIndicator;
		
		boolean bBlack = false;
		
		m_pOldView = pIndicator.GetViewComponent(context);
		if(m_pIndicator.m_iOldResID != -1)
			m_pOldView.setBackgroundResource(m_pIndicator.m_iOldResID);

		//m_pNewView = pIndicator.GetViewComponent(context);
		//m_pNewView.setBackgroundResource(m_pIndicator.m_iNewResID);
		
		addView(m_pOldView);
		//addView(m_pNewView);
		
		m_pOldView.setVisibility(VISIBLE);
		//m_pNewView.setVisibility(GONE);

//		Log.d("11111111111", "Trying to read bitmap " + m_pIndicator);
//		Bitmap pBitmap = BitmapFactory.decodeResource(context.getResources(), m_pIndicator.m_iOldResID);
//		m_fK = (float)pBitmap.getWidth()/pBitmap.getHeight();
//		pBitmap.recycle();
//		pBitmap = null;
		m_fK = GetK(context, m_pIndicator.m_iOldResID, m_pIndicator.toString());

		m_pText = new TextView(context);
		m_pText.setText(m_pIndicator.m_sName);
		m_pText.setTextSize(m_pIndicator.m_iFontSize/2);
		m_pText.setTextScaleX(3f/4f);
		m_pText.setGravity(Gravity.CENTER);
		m_pText.setVisibility(VISIBLE);
		if(!bBlack) {
//			m_pText.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));//, context.getTheme()));
			m_pText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));//, context.getTheme()));
		}
		else
			m_pText.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));//, context.getTheme()));
        addView(m_pText);
		
		if(m_pIndicator.m_bText2)
		{
			m_pText2 = new TextView(context);
			m_pText2.setText("");
			m_pText2.setTextSize(m_pIndicator.m_iFontSize);
			m_pText2.setTextScaleX(3f/4f);
			m_pText2.setGravity(Gravity.CENTER);
			m_pText2.setVisibility(VISIBLE);
			if(bBlack)
				m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));//, context.getTheme()));
			else
				m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));//, context.getTheme()));
			addView(m_pText2);
		}
		
		m_pFadeInAnimation = AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
		m_pFadeInAnimation.setRepeatMode(Animation.REVERSE);
		m_pFadeInAnimation.setDuration(1500);
		m_pFadeInAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		m_pFadeOutAnimation = AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
		m_pFadeOutAnimation.setRepeatMode(Animation.REVERSE);
		m_pFadeOutAnimation.setDuration(1500);
		m_pFadeOutAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		m_pFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() 
		{
        	public void onAnimationEnd(Animation animation) 
        	{
//				Log.d("111", "out end");
				m_pOldView.setBackgroundResource(m_pIndicator.m_iNewResID);
                m_pIndicator.m_iOldResID = m_pIndicator.m_iNewResID;
        	}

			public void onAnimationRepeat(Animation animation) 
			{
				// TODO Auto-generated method stub
			}

			public void onAnimationStart(Animation animation) 
			{
	        }
		});		

		m_pOldView.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				Log.d(TAG, m_pIndicator.m_sName + "(" + m_pIndicator.m_fXPercent + "," + m_pIndicator.m_fYPercent + ").onTouchEvent: " + event.toString());
				switch (event.getAction() & MotionEvent.ACTION_MASK)
				{
					case MotionEvent.ACTION_CANCEL:
						m_pIndicator.Released();
						break;
					case MotionEvent.ACTION_OUTSIDE:
						m_pIndicator.Released();
						break;
                    case MotionEvent.ACTION_UP:
                        m_pIndicator.Released();
                        break;
				}
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		if(m_pIndicator.m_bMetaIndicator)
		{
			m_pCog = new View(context);
			m_pCog.setBackgroundResource(R.drawable.shesterenka);
			addView(m_pCog);
		}
		
		if(m_pIndicator.m_bProtected)
		{
			m_pShield = new View(context);
			m_pShield.setBackgroundResource(R.drawable.shield);
			addView(m_pShield);
		}
		
		m_pIndicator.BindUI(pServer, context, this);
	}

	
	public void ControlPopup()
	{
		Log.d(TAG, "ShowPopup" );
		((Activity)initialContext).runOnUiThread(new Runnable() {
			public void run() {
				if(m_pIndicator.ShowPopup(initialContext))
					invalidate();
				mode = NONE;
			}
		});

		Log.d(TAG, "mode=NONE" );
	}
	
	public void ImitateOnOff()
	{
//		((RoomUI)m_pIndicator.m_pServer.m_pSwitcher.getChildAt(m_pIndicator.m_pServer.m_pSwitcher.getCurrentScreen())).m_pSubsystem.CheckDemoAlarm();
        for(Subsystem pSubsystem : Config.Instance.m_cSubsystems)
        {
            for(Indicator pIndicator: pSubsystem.m_cIndicators)
            {
                if(pIndicator == m_pIndicator)
                {
                    pSubsystem.CheckDemoAlarm();
                }
            }
        }

		if(m_pIndicator instanceof Macro)
		{
			for(Subsystem pSubsystem : Config.Instance.m_cSubsystems)
			{
				for(Indicator pIndicator: pSubsystem.m_cIndicators)
				{
					if(pIndicator != m_pIndicator && pIndicator instanceof Macro)
					{
						((Macro)pIndicator).m_bActive = false;
						pIndicator.Update();
					}
				}
			}
		}
		if(m_pIndicator.m_bMetaIndicator)
		{
            for(Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            {
				for(Indicator pIndicator: pSubsystem.m_cIndicators)
				{
					if(pIndicator != m_pIndicator)
					{
						if(m_pIndicator instanceof MotionSensor && pIndicator instanceof MotionSensor)
						{
							((MotionSensor)pIndicator).m_bGuard = ((MotionSensor)m_pIndicator).m_bGuard;
							if(!((MotionSensor)pIndicator).m_bGuard)
							{
								((MotionSensor)pIndicator).m_bAlarm = false;
								pSubsystem.CheckDemoAlarm();
							}
						}

						if(m_pIndicator instanceof FireSensor && pIndicator instanceof FireSensor)
						{
							((FireSensor)pIndicator).m_bGuard = ((FireSensor)m_pIndicator).m_bGuard;
							if(!((FireSensor)pIndicator).m_bGuard)
							{
								((FireSensor)pIndicator).m_bAlarm = false;
								pSubsystem.CheckDemoAlarm();
							}
						}

						if(m_pIndicator instanceof LeakageSensor && pIndicator instanceof LeakageSensor)
						{
							((LeakageSensor)pIndicator).m_bGuard = ((LeakageSensor)m_pIndicator).m_bGuard;
							if(!((LeakageSensor)pIndicator).m_bGuard)
							{
								((LeakageSensor)pIndicator).m_bAlarm = false;
								pSubsystem.CheckDemoAlarm();
							}
						}

						pIndicator.Update();
					}
				}
			}
		}
	}

	public void LonelyRelease()
	{
		((Activity)initialContext).runOnUiThread(new Runnable()
		{
			public void run()
			{
				if (m_pIndicator.m_iReaction == 2)
					return;

				if(m_pIndicator.SwitchOnOff(start.x, start.y))
					invalidate();
			}
		});
	}

	public void SwitchOnOff()
	{
		Log.d(TAG, "SwitchOnOff" );

		if (m_pIndicator.m_iReaction == 2)
			return;

		if(m_pIndicator.SwitchOnOff(start.x, start.y))
		{
			if(Config.DEMO)
				ImitateOnOff();

			invalidate();
		}
	}
	
	public int m_iX;
	public int m_iY;

	protected boolean m_bLoopAnimation = false;
    protected float m_fK = 1;
	
	protected boolean StartAnimation(int iResId)
	{
		if(iResId != -1)
		{
			m_pIndicator.m_iNewResID = iResId;
			m_pOldView.setBackgroundResource(m_pIndicator.m_iNewResID);
			m_pIndicator.m_iOldResID = m_pIndicator.m_iNewResID;
		}

//		if(m_pIndicator.m_iNewResID == iResId && m_pOldView.getAnimation() != null)
//			return false;
//
//		m_pOldView.setBackgroundResource(m_pIndicator.m_iNewResID);
//
//		m_pIndicator.m_iNewResID = iResId;
//
//		m_pNewView.setBackgroundResource(m_pIndicator.m_iNewResID);
//
//		if(m_bLoopAnimation)
//		{
//			m_pFadeInAnimation.setRepeatCount(Animation.INFINITE);
//			m_pFadeOutAnimation.setRepeatCount(Animation.INFINITE);
//		}
//		else
//		{
//			m_pFadeInAnimation.setRepeatCount(0);
//			m_pFadeOutAnimation.setRepeatCount(0);
//		}
//
//  		m_pOldView.startAnimation(m_pFadeOutAnimation);
//  		m_pNewView.startAnimation(m_pFadeInAnimation);
  		
  		return true;
	}
	
	
	protected int m_iWidth = 0;
	protected int m_iHeight = 0;

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) 
	{
		Log.d("Glindor4", m_pIndicator.m_sName + "(" + l + "," + t + ","+ r + ","+ b+")");
		m_iWidth = r-l;
		m_iHeight = b-t;

		IndicatorLayout pLayout = new IndicatorLayout(m_pIndicator, m_fK, l, t, r, b);

        m_iHeight -= pLayout.m_iTextSize*2.2;

		m_pOldView.layout(pLayout.m_iLeft, pLayout.m_iTop, pLayout.m_iLeft + pLayout.m_iWidth, pLayout.m_iTop + pLayout.m_iHeight);
		//m_pNewView.layout(iDX, iDY, iDX + iRealWidth, iDY + iRealHeight);

        Rect currentBounds = new Rect();
        final String s = m_pText.getText().toString();
		m_pText.setSingleLine(true);
		m_pText.setTextSize(TypedValue.COMPLEX_UNIT_PX, pLayout.m_iTextSize*1.25f);
		m_pText.getPaint().getTextBounds(s, 0, s.length(), currentBounds);
        float fFontSizeScale = 1.0f;
        float fHeightOffsetScale = 0.2f;
//        if (currentBounds.width() >= m_iWidth*2)
//        {
//            fFontSizeScale = 0.5f;
//        }

		int iTextStart = 0;
		int iTextWidth = m_iWidth;
		if(m_pIndicator.m_bDoubleWidth)
		{
			iTextWidth /= 2;
			iTextStart = (m_iWidth - iTextWidth)/2;
		}

        if (currentBounds.width() >= iTextWidth)
        {
			StringBuilder s1builder = new StringBuilder();
			String s2 = new String(s);
			int diffBefore = Math.abs(s2.length() - s1builder.length());
			boolean finished = false;
			while(!finished)
			{
				boolean spaceFound = false;
				for (int i = 0; i < s2.length(); i++)
				{
					if (s2.charAt(i) == ' ')
					{
						spaceFound = true;
						fHeightOffsetScale = 0.5f;

						int diffAfter = Math.abs(s2.length() - s1builder.length() - (i + 1) * 2);// (13-6) - (4+6) = 13-6 - 4-6 = 13-4 - 6*2
						if (diffAfter < diffBefore)
						{
							s1builder.append(s2.substring(0, i + 1));
							if (i + 1 < s2.length())
								s2 = s2.substring(i + 1);
							else
								s2 = "";

							diffBefore = diffAfter;

							break;
						}
						else
						{
							finished = true;
							break;
						}
					}
				}
				if(!spaceFound)
					break;
			}
			if(s1builder.length() == 0)
			{
				s1builder.append(s2);
				s2 = "";
			}
			String workStr = s1builder.toString();

			//m_pText.getPaint().getTextBounds(workStr, 0, workStr.length(), currentBounds);
			float widthMax = m_pText.getPaint().measureText(workStr);//currentBounds.width();
			//m_pText.getPaint().getTextBounds(s2, 0, s2.length(), currentBounds);
			if(m_pText.getPaint().measureText(s2) > widthMax) //currentBounds.width() > widthMax)
			{
				workStr = s2;
				widthMax = m_pText.getPaint().measureText(s2);//currentBounds.width();
			}

			if(widthMax > iTextWidth)
				fFontSizeScale = ((float)iTextWidth) / widthMax;
		}
		m_pText.setSingleLine(false);
		m_pText.setMaxLines(2);
		//m_pText.setEllipsize(TextUtils.TruncateAt.END);

        m_pText.getPaint().getTextBounds(s, 0, s.length(), currentBounds);
        //m_pText.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //int height = m_pText.getMeasuredHeight();
        //height += (m_pText.getLineCount()-1) * m_pText.getLineHeight();

		//Paint.FontMetrics fm = m_pText.getPaint().getFontMetrics();
        //int height2 = (int)(fm.descent - fm.ascent);
		//int height3 = (int)(fm.bottom - fm.top + fm.leading); // 265.4297

		//m_pText.layout(0, 0, m_iWidth, m_iHeight + iText);
/*		if(m_pIndicator.m_bDoubleScale)
		{
			m_pText.layout(-5, (int) (m_iHeight-iText*fFontSizeScale*0.2), m_iWidth, m_iHeight + iText*3);
			m_pText.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*fFontSizeScale);
		}
		else*/
		{
			//m_pText.layout(0, (int) (m_iHeight-currentBounds.height()), m_iWidth, m_iHeight+10*pLayout.m_iTextSize);
			m_pText.layout(iTextStart, (int) (m_iHeight-pLayout.m_iTextSize*fFontSizeScale*fHeightOffsetScale), iTextStart + iTextWidth, m_iHeight*2);// + pLayout.m_iTextSize*4);
			m_pText.setTextSize(TypedValue.COMPLEX_UNIT_PX, pLayout.m_iTextSize*fFontSizeScale*1.25f);
		}
		m_pText.setLineSpacing(0, 0.75f);
		//m_pText.setTextScaleX(0.8f);
		
		if(m_pCog != null)
			m_pCog.layout(pLayout.m_iLeft + pLayout.m_iWidth - pLayout.m_iWidth/3, pLayout.m_iTop, pLayout.m_iLeft + pLayout.m_iWidth, pLayout.m_iTop + pLayout.m_iHeight/3);
		
		if(m_pShield != null)
			m_pShield.layout(pLayout.m_iLeft, pLayout.m_iTop, pLayout.m_iLeft + pLayout.m_iWidth/3, pLayout.m_iTop + pLayout.m_iHeight/3);

		//Text2 выводится по центру индикаторв
//		if(m_pText2 != null)
//		{
//			if(m_pIndicator.m_bDoubleScale)
//			{
//                m_pText2.layout(-5, (int) (m_iHeight/2-iText), m_iWidth, m_iHeight/2 + iText);
//				m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*3.75f);
//			}
//			else
//			{
//                m_pText2.layout(-5, (int) (m_iHeight/2-iText), m_iWidth, m_iHeight/2 + iText);
//				m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.5f);
//			}
//		}
		
		m_pIndicator.FixLayout(l, t, r, b);
	}
}
