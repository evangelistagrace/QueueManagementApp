package com.example.queueManagementSystem;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerTicketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerTicketsFragment extends Fragment implements CustomerTicketsAdapter.ItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerTicketsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerTicketsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerTicketsFragment newInstance(String param1, String param2) {
        CustomerTicketsFragment fragment = new CustomerTicketsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    CustomerTicketsAdapter adapter;
    Customer customer;
    Intent currentIntent;
    Button btnTicketView, btnTicketCancel;
    TextView tvNoActiveTicket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.customer_tickets_fragment, container, false);

        // init components
        currentIntent = getActivity().getIntent();
        customer = (Customer) currentIntent.getSerializableExtra("customerObject");
        tvNoActiveTicket = (TextView) view.findViewById(R.id.tvNoActiveTicket);


        // data to populate the RecyclerView with
        ArrayList<Ticket> tickets = customer.getTickets();

        if (tickets.size() == 0) {
            tvNoActiveTicket.setVisibility(View.VISIBLE);
        } else {
            tvNoActiveTicket.setVisibility(View.INVISIBLE);
        }


        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvCustomerTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CustomerTicketsAdapter(getActivity(), tickets);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, List<Ticket> tickets, int position) {
        //view has to be clicked first
        // load ticket fragment

//        Ticket ticket = tickets.get(position);
//        Customer customer = ticket.getCustomer();
//        QueueManager queueManager = ticket.getCounter().getQueueManager();
//
//        currentIntent.putExtra("ticketObject", ticket);
//        currentIntent.putExtra("customerObject", customer);
//        currentIntent.putExtra("queueManagerObject", queueManager);
//
//        Fragment fragment = new CustomerTicketFragment();
//        loadFragment(fragment);

        Ticket ticket = tickets.get(position);
        Customer customer = ticket.getCustomer();
        QueueManager queueManager = ticket.getCounter().getQueueManager();

        //view ticket
        view.findViewById(R.id.btnTicketView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent.putExtra("ticketObject", ticket);
                currentIntent.putExtra("customerObject", customer);
                currentIntent.putExtra("queueManagerObject", queueManager);

                Fragment fragment = new CustomerTicketFragment();
                loadFragment(fragment);
            }
        });


        // cancel ticket
        view.findViewById(R.id.btnTicketCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try catch to gracefully get back to main activity
                try {
                    androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        // clear customer ticker first
                        // clear only if the ticket is not currently being served
                        if (queueManager.getCurrentServingTicket().getTicketNumber() != ticket.getTicketNumber()) {
                            int index = 0;
                            int customerTicketIndex = -1;
                            for (Ticket customerTicket: customer.getTickets()) {
                                if (customerTicket.getTicketNumber() == ticket.getTicketNumber()) {
                                    customerTicketIndex = index;
                                }
                                index++;
                            }

                            if (customerTicketIndex > -1) {
                                customer.getTickets().remove(customerTicketIndex);
                            }

                            ticket.setExpired(true);
                        }

                        Toast.makeText(getActivity(), "Ticket cancelled", Toast.LENGTH_SHORT).show();
                        // close current fragment
                        fm.popBackStack(null, 0);

                    }
                } catch (Exception err) {
                    return;
                }

            }
        });


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.customer_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}