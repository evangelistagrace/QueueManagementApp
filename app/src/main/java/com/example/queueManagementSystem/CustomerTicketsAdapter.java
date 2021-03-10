package com.example.queueManagementSystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Queue;

public class CustomerTicketsAdapter extends RecyclerView.Adapter<CustomerTicketsAdapter.ViewHolder> {

    private List<Ticket> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Intent intent;
    Context context;

    // data is passed into the constructor
    CustomerTicketsAdapter(Context context, List<Ticket> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.customer_tickets_row, parent, false);

        Button btnTicketView = view.findViewById(R.id.btnTicketView);
        Button btnTicketCancel = view.findViewById(R.id.btnTicketCancel);

//        btnTicketView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "btn view id: " + btnTicketView.getId(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ticketNumber = String.valueOf(mData.get(position).getTicketNumber());
        String ticketService = mData.get(position).getService().getServiceName();

        holder.tvTicketNumber.setText("Ticket #" + ticketNumber);
        holder.tvTicketService.setText(ticketService);
//        holder.btnTicketView.setId((int) mData.get(position).getTicketNumber());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    QueueManager queueManager;
    Queue<Ticket> tickets;
    Ticket ticket;
    Customer customer;
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTicketNumber, tvTicketService;
        Button btnTicketView, btnTicketCancel;

        ViewHolder(View itemView) {
            super(itemView);
            tvTicketNumber = itemView.findViewById(R.id.tvTicketNumber);
            tvTicketService = itemView.findViewById(R.id.tvTicketService);
            btnTicketView = itemView.findViewById(R.id.btnTicketView);
            btnTicketCancel = itemView.findViewById(R.id.btnTicketCancel);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, mData, getAdapterPosition());
//                mClickListener.onBtnClick(btnTicketView, mData, getAdapterPosition());
            }
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return String.valueOf(mData.get(id).getTicketNumber());
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, List<Ticket> tickets, int position);
//        void onBtnClick(Button btn, List<Ticket> tickets, int position);
    }
}