package com.example.bishnu.excalibur;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bishnu.Reddy on 9/21/2017.
 */

public class EmployeeListAdapter extends BaseFlipAdapter {

    private final int PAGES = 3;
    private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4, R.id.interest_5};
    private Context context;
    public EmployeeListAdapter(Context context, PaginatedScanList<Employee> items, FlipSettings settings) {
        super(context, items, settings);
        this.context = context;
    }

    @Override
    public View getPage(int position, View convertView, ViewGroup parent, Object employe1, Object employee2/*, SearchView.OnCloseListener closeListener*/) {
        final EmployeesHolder holder;
        if (convertView == null) {
            holder = new EmployeesHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.employee_merge_layout, parent, false);
            holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
            holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
            holder.infoPage = LayoutInflater.from(context).inflate(R.layout.employee_info_layout, parent, false);
            holder.nickName = (TextView) holder.infoPage.findViewById(R.id.nickname);
            holder.btn=(Button)holder.infoPage.findViewById(R.id.btnlstn);
            holder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
                }
            });
            for (int id : IDS_INTEREST)
                holder.interests.add((TextView) holder.infoPage.findViewById(id));

            convertView.setTag(holder);
        } else {
            holder = (EmployeesHolder) convertView.getTag();
        }


        switch (position) {
            // Merged page with 2 friends
            case 1:
                Glide.with(context).load(((Employee) employe1).getImage())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.leftAvatar);
                //holder.leftAvatar.setImageResource(((Employee) employe1).getImage());
                if (employee2 != null)
                    Glide.with(context).load(((Employee) employee2).getImage())
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.rightAvatar);
                  //  holder.rightAvatar.setImageResource(((Friend) employee2).getAvatar());
                break;
            default:
                fillHolder(holder, position == 0 ? (Employee) employe1 : (Employee) employee2);
                holder.btn= (Button) holder.infoPage.findViewById(R.id.btnlstn);
                holder.infoPage.setTag(holder);

                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
                    }
                });
                return holder.infoPage;
        }
        return convertView;
    }

    @Override
    public int getPagesCount() {
        return PAGES;
    }

    private void fillHolder(EmployeesHolder holder, Employee employee) {
        if (employee == null)
            return;
        Iterator<TextView> iViews = holder.interests.iterator();
        Iterator<String> iInterests = Arrays.asList(employee.getSkills().split(",")).iterator();
        while (iViews.hasNext() && iInterests.hasNext())
            iViews.next().setText(iInterests.next());
        holder.infoPage.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        holder.nickName.setText(employee.getName());
    }

    class EmployeesHolder {
        ImageView leftAvatar;
        ImageView rightAvatar;
        View infoPage;
        Button btn;
        List<TextView> interests = new ArrayList<>();
        TextView nickName;
    }
}
