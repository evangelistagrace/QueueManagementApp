package com.example.queueManagementSystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerQueueOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerQueueOptionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerQueueOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceQueueOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerQueueOptionsFragment newInstance(String param1, String param2) {
        CustomerQueueOptionsFragment fragment = new CustomerQueueOptionsFragment();
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
    Customer customer, currentServingCustomer;
    Service service;
    Intent currentIntent;
    QueueManager queueManager = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.service_queue_options_fragment, container, false);

        // add code after this line
        TextView serviceTitle = (TextView) view.findViewById(R.id.tvServiceTitle);
        Button btnRequestTicket = (Button) view.findViewById(R.id.btnRequestTicket);
        Button btnViewQueue = (Button) view.findViewById(R.id.btnViewQueue);
        Button btnLeaveQueue = (Button) view.findViewById(R.id.btnLeaveQueue);

        currentIntent = getActivity().getIntent();
        customer = (Customer) currentIntent.getSerializableExtra("customerObject");
        service = (Service) currentIntent.getSerializableExtra("serviceObject");

        //set service title
        serviceTitle.setText(service.getServiceName());

        Toast.makeText(getActivity(), "Welcome again " + customer.getUsername() + ", you are in service " + service.getServiceName(), Toast.LENGTH_SHORT).show();

        // check if customer has an active ticket for the current service
//        for (Ticket tickets: customer.getTickets()) {
//            if (tickets.getService().getServiceId() == service.getServiceId()) {
//                // get queuemanager that is handling that is handling this customer's ticket
//               for (Counter counter: service.getCounters()) {
//                   for (Ticket ticket: counter.getQueueManager().getTickets()) { // there can only be one queuemanager handling a customer's ticket for a given service
//                       if (ticket.getCustomer() != null) {
//                           if (ticket.getCustomer().getUsername() == customer.getUsername()) {
//                               queueManager = counter.getQueueManager();
//                           }
//                       }
//                   }
//                }
//               // check on intervals if customer's ticket for this service is being served
//                //put this block
//               if (queueManager != null) { //means customer currently has a queuemanager managing their ticket, so we will need to call the block inside on intervals if this condition is satosfied
//                   currentServingCustomer = queueManager.getCurrentServingTicket().getCustomer();
//                   if (currentServingCustomer != null && currentServingCustomer.getUsername() == customer.getUsername()) {
//                       Toast.makeText(getActivity(), "IT IS YOUR TURN, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//                   } else {
//                       Toast.makeText(getActivity(), "YOU ARE QUEUED, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//                   }
//               }
//            }
//        }

//        for (Counter counter: service.getCounters()) {
//            queueManager = counter.getQueueManager();
//            currentServingCustomer = queueManager.getCurrentServingTicket().getCustomer();
//            if (currentServingCustomer != null && currentServingCustomer.getUsername() == customer.getUsername()) {
//                Toast.makeText(getActivity(), "IT IS YOUR TURN, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getActivity(), "YOU ARE QUEUED, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//            }
//        }

        btnRequestTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                Ticket requestedTicket = customer.sendTicketRequest(customer, service.getServiceId());
                QueueManager queueManager = null;

                if (requestedTicket != null) {
                    for (Counter counter : service.getCounters()) {
                        for (Ticket ticket : counter.getQueueManager().getTickets()) { // there can only be one queuemanager handling a customer's ticket for a given service
                            if (ticket.getTicketNumber() == requestedTicket.getTicketNumber()) {
                                queueManager = counter.getQueueManager();
                            }
                        }
                    }
                    currentIntent.putExtra("ticketObject", requestedTicket);
                    currentIntent.putExtra("customerObject", customer);
                    currentIntent.putExtra("queueManagerObject", queueManager);

                    fragment = new CustomerTicketFragment();
                    loadFragment(fragment);

                    Toast.makeText(getActivity(), "Successfully requested for a ticket", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getActivity(), "Error in requesting ticket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.customer_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}