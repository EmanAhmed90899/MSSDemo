package com.hemaya.mssdemo.view.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.DataHolder> {
    private List<User> items;
    private BottomSheetDialogFragment context;
    private OnUserClickListener onUserClickListener;

    public UsersAdapter(List<User> items, BottomSheetDialogFragment context) {
        this.items = items;
        this.context = context;
        this.onUserClickListener = (OnUserClickListener) context;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        holder.tv_user_name.setText(items.get(position).getName());
        holder.tv_user_serial.setText(items.get(position).getSerialNumber());
        holder.rb_user.setChecked(items.get(position).isUsed());
        holder.rb_user.setOnClickListener(v -> {
            for (User user : items) {
                user.setUsed(false);
            }
            items.get(position).setUsed(true);
            notifyDataSetChanged();

            onUserClickListener.onUserClick(items.get(position));
        });

        if (position == items.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DataHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name, tv_user_serial;
        RadioButton rb_user;
        View divider;

        public DataHolder(View itemView) {
            super(itemView);

            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_user_serial = itemView.findViewById(R.id.tv_user_serial);
            rb_user = itemView.findViewById(R.id.rb_user);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
