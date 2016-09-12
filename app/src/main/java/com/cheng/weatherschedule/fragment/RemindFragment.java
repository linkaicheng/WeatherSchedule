package com.cheng.weatherschedule.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cheng.weatherschedule.R;
import com.cheng.weatherschedule.bean.AllPlanViewHolder;
import com.cheng.weatherschedule.bean.Plan;
import com.cheng.weatherschedule.calendar.EditPlanActivity;
import com.cheng.weatherschedule.dao.PlanDao;
import com.cheng.weatherschedule.daoImpl.PlanDaoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 2016/9/5.
 */
public class RemindFragment extends Fragment {

    private ListView lvPlans;
    private List<Map<String,Object>> data=null;
    private Adapter adapter=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remind,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }
    private void initView(){
        lvPlans = (ListView) getActivity().findViewById(R.id.lvPlans);
        lvPlans.setOnItemClickListener(new LvPlansOnItCkListener());

    }

    @Override
    public void onResume() {
        super.onResume();
        data=getData();
        adapter=new Adapter(data);
        lvPlans.setAdapter(adapter);
    }
    //点击某条计划，到计划修改界面
    private class LvPlansOnItCkListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(getActivity(),EditPlanActivity.class);
            intent.putExtra("id",(int)data.get(position).get("id"));
            intent.putExtra("date",(String)data.get(position).get("date"));
            startActivity(intent);
        }
    }

    /**
     * 从数据库获取所有的计划
     * @return
     */
    private List<Map<String,Object>> getData(){
    PlanDao planDao=new PlanDaoImpl(getActivity());
    List<Plan> plans=planDao.findAll();
    List<Map<String,Object>> mdata=new ArrayList<>();
    for(Plan plan:plans){
        Map<String,Object> row=new HashMap<>();
        row.put("date",plan.getDate());
        row.put("time",plan.getTime());
        row.put("title",plan.getTitle());
        row.put("id",plan.getId());
        mdata.add(row);
    }
    return mdata;
}

    /**
     * 自定义适配器
     */
    private class Adapter extends BaseAdapter{
        List<Map<String,Object>> data;
        public Adapter(List<Map<String,Object>> data){
            this.data=data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AllPlanViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new AllPlanViewHolder();
                convertView = View.inflate(getActivity(), R.layout.item_all_plan, null);
                viewHolder.tvPlanDate= (TextView) convertView.findViewById(R.id.tvPlanDate);
                viewHolder.tvPlanTime= (TextView) convertView.findViewById(R.id.tvPlanTime);
                viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.tvTitle);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (AllPlanViewHolder) convertView.getTag();
            }
            viewHolder.tvPlanDate.setText((String)data.get(position).get("date"));
            viewHolder.tvPlanTime.setText((String)data.get(position).get("time"));
            viewHolder.tvTitle.setText((String)data.get(position).get("title"));
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
