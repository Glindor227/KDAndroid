package ru.cpc.smartflatview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private List<ExpandedMenuModel> mListData; // header titles

    ExpandableListView expandList;

    public MyExpandableListAdapter(Context context, List<ExpandedMenuModel> listData, ExpandableListView mView) {
        this.mContext = context;
        this.mListData = listData;
        this.expandList = mView;
    }

    @Override
    public int getGroupCount() {
        if(mListData == null)
            return 0;

        int i = mListData.size();
        Log.d("GROUPCOUNT", String.valueOf(i));
        return this.mListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        if(mListData == null)
            return 0;

        int childCount = this.mListData.get(groupPosition).m_cNestedMenu.size();
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.mListData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Log.d("CHILD", groupPosition + ":" + childPosition);
        Log.d("CHILD", this.mListData.get(groupPosition).m_cNestedMenu.get(childPosition).toString());

        return this.mListData.get(groupPosition).m_cNestedMenu.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private static final int[] EMPTY_STATE_SET = {};
    private static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};
    private static final int[][] GROUP_STATE_SETS =
    {
        EMPTY_STATE_SET, // 0
        GROUP_EXPANDED_STATE_SET // 1
    };

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);

        //if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if( getChildrenCount( groupPosition ) == 0 )
                convertView = infalInflater.inflate(R.layout.listheader, null);
            else
                convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListHeader = (TextView)convertView.findViewById(R.id.submenu);

        txtListHeader.setText(headerTitle.getName());
        if( getChildrenCount( groupPosition ) == 0 )
        {
            ImageView childIcon = (ImageView)convertView.findViewById(R.id.iconimage);
            if(childIcon != null && headerTitle.getIcon() != -1)
                childIcon.setImageResource(headerTitle.getIcon());

            headerTitle.m_pView = convertView;
        }

        View ind = convertView.findViewById( R.id.explist_indicator);
        if( ind != null )
        {
            ImageView indicator = (ImageView)ind;
            if( getChildrenCount( groupPosition ) == 0 )
            {
                indicator.setVisibility( View.INVISIBLE );
            }
            else
            {
                indicator.setVisibility( View.VISIBLE );
                int stateSetIndex = ( isExpanded ? 1 : 0) ;
                Drawable drawable = indicator.getDrawable();
                drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        ExpandedMenuModel childItem = (ExpandedMenuModel) getChild(groupPosition, childPosition);

        //if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listheader, null);
        }

        TextView lblListChild = (TextView)convertView.findViewById(R.id.submenu);
        ImageView childIcon = (ImageView)convertView.findViewById(R.id.iconimage);

        //lblListChild.setTypeface(null, Typeface.BOLD);
        lblListChild.setText(childItem.getName());

        if(childIcon != null && childItem.getIcon() != -1)
            childIcon.setImageResource(childItem.getIcon());

        childItem.m_pView = convertView;

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}