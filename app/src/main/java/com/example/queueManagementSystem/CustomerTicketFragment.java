package com.example.queueManagementSystem;

import android.app.Activity;
import android.app.FragmentManager;
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
    RunnableExample runnable;
    Thread thread;

    class RunnableExample implements Runnable {
        private volatile boolean running = true;

        public void terminate() {
            running = false;
        }

        public void run() {
            while (!ticket.isExpired() && running && !Thread.currentThread().isInterrupted()) {
                try {
                    long period = (long)(queueManager.getCurrentServingTicket().getTicketTimeInterval() * 1000);
                    Activity activity = getActivity();
                    if (activity!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long currentServingTicketNumber = queueManager.getCurrentServingTicket().getTicketNumber();
                                String positionNumber = String.valueOf(ticket.getTicketNumber() - currentServingTicketNumber);

                                // display customer position in queue
                                tvQueuePosition.setText(positionNumber);

                                // display current serving ticket number
                                tvCurrentlyServing.setText(getResources().getString(R.string.currently_serving) + " " + String.valueOf(currentServingTicketNumber));

                                // alert customer if it is their turn
                                currentServingCustomer = queueManager.getCurrentServingTicket().getCustomer();
                                if (currentServingCustomer != null && currentServingCustomer.getUsername() == customer.getUsername()) {
                                    Toast.makeText(getActivity(), "NOW SERVING CUSTOMER " + customer.getUsername(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Thread.sleep(period);
                    } else{
//                        Toast.makeText(getActivity(), "No activity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //try catch to gracefully get back to main activity
            // stop thread and pop off ticket fragment
            try {
                //load a new fragment?
                currentIntent.putExtra("customerObject", customer);
                currentIntent.putExtra("queueManagerObject", queueManager);
                Fragment fragment = new CustomerTicketsFragment(); // loads even for fragment on top
                loadFragment(fragment);

                if (runnable != null) {
                    runnable.terminate();
                    thread.join();
                }
            } catch (Exception err) {
                return;
            }
        }
    }

    //  background thread that displays queue details to UI
    private void runThread() throws InterruptedException {
        runnable = new RunnableExample();
        thread = new Thread(runnable);
        thread.start();
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

       //run background thread
        try {
            runThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btnLeaveQueue.setOnClickListener(new View.OnClickListener() {
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
                        // close current fragment
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        if (runnable != null) {
//                            runnable.terminate();
//                            thread.join();
//                        }
                    }
                } catch (Exception err) {
                    return;
                }
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.customer_fragment_container, fragment, Integer.toString(getFragmentCount()));
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected int getFragmentCount() {
        return getActivity().getSupportFragmentManager().getBackStackEntryCount();
    }

    private Fragment getFragmentAt(int index) {
        return getFragmentCount() > 0 ? getActivity().getSupportFragmentManager().findFragmentByTag(Integer.toString(index)) : null;
    }

    protected Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount() - 1);
    }
}