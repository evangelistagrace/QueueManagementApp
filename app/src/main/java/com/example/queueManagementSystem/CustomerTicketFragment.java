package com.example.queueManagementSystem;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Queue;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerTicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerTicketFragment<ServiceHandler> extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerTicketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment customer_ticket.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerTicketFragment newInstance(String param1, String param2) {
        CustomerTicketFragment fragment = new CustomerTicketFragment();
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
    Intent currentIntent;
    Ticket ticket;
    QueueManager queueManager;
    Timer timer;
    TextView tvQueuePosition, tvCurrentlyServing, tvCustomerTicketNumber;
    static int i = 0;
    String currentServingTicketNumber;

    //  background thread that displays queue details to UI
    private void runThread() {
        new Thread() {
            public void run() {
                while (!ticket.isExpired()) {
                    try {
                        long period = (long)(queueManager.getCurrentServingTicket().getTicketTimeInterval() * 1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentServingTicketNumber = String.valueOf(queueManager.getCurrentServingTicket().getTicketNumber());
                                long positionNumber = ticket.getTicketNumber() - Long.parseLong(currentServingTicketNumber);

                                // display customer position in queue
                                tvQueuePosition.setText(String.valueOf(positionNumber));

                                // display current serving ticket number
                                tvCurrentlyServing.setText(getResources().getString(R.string.currently_serving) + " " + currentServingTicketNumber);

                                // alert customer if it is their turn
                                currentServingCustomer = queueManager.getCurrentServingTicket().getCustomer();
                                if (currentServingCustomer != null && currentServingCustomer.getUsername() == customer.getUsername()) {
                                    Toast.makeText(getActivity(), "IT IS YOUR TURN, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Thread.sleep(period);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                //try catch to gracefully get back to main activity
                try {
                    androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        // close current fragment
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                } catch (Exception err) {
                    return;
                }

            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.customer_ticket_fragment, container, false);

        //init components
        Button btnLeaveQueue = (Button) view.findViewById(R.id.btnLeaveQueue);
        tvQueuePosition = (TextView) view.findViewById(R.id.tvQueuePosition);
        tvCurrentlyServing = (TextView) view.findViewById(R.id.tvCurrentlyServing);
        tvCustomerTicketNumber = (TextView) view.findViewById(R.id.tvCustomerTicketNumber);

        currentIntent = getActivity().getIntent();
        customer = (Customer) currentIntent.getSerializableExtra("customerObject");
        ticket = (Ticket) currentIntent.getSerializableExtra("ticketObject");
        queueManager = (QueueManager) currentIntent.getSerializableExtra("queueManagerObject");

        tvCustomerTicketNumber.setText(getResources().getString(R.string.ticket_number) + " "  + ticket.getTicketNumber());

//
//
//        Toast.makeText(getActivity(), "Welcome again " + customer.getUsername(), Toast.LENGTH_SHORT).show();

//        currentServingCustomer = queueManager.getCurrentServingTicket().getCustomer();
//       if (currentServingCustomer != null && currentServingCustomer.getUsername() == customer.getUsername()) {
//           Toast.makeText(getActivity(), "IT IS YOUR TURN, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//       } else {
//           Toast.makeText(getActivity(), "YOU ARE QUEUED, " + customer.getUsername(), Toast.LENGTH_SHORT).show();
//       }


       //TESTING RUNNING BACKGROUND THREAD
        runThread();

        btnLeaveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                try {
                    //

                    androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        // close current fragment
                        fm.popBackStack();
                    }

                    Toast.makeText(getActivity(), "Left queue for service " + ticket.getService().getServiceName(), Toast.LENGTH_SHORT).show();
                } catch(Exception err) {
                    return;
                }


            }
        });

        return view;
    }
}