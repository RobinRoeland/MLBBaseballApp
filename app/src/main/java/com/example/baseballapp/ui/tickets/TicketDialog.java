package com.example.baseballapp.ui.tickets;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;

import com.example.baseballapp.classes.BitMapItem;
import com.example.baseballapp.classes.MLB.MLBGame;
import com.example.baseballapp.classes.MLB.MLBTeamInfo;
import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.classes.stadium.VenueBox;
import com.example.baseballapp.classes.stadium.VenueZone;
import com.example.baseballapp.classes.team.Team;
import com.example.baseballapp.data.MLBDataLayer;
import com.example.baseballapp.databinding.TicketDialogBinding;
import com.example.baseballapp.tasks.WebFetchImageTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TicketDialog extends DialogFragment {
    private MLBGame m_game;
    private BitMapItem stadiumimg;
    private TicketDialogBinding binding;
    private VenueZone currentZone;
    private VenueBox currentbox;
    private MLBDataLayer repo;

    public TicketDialog(MLBGame game){
        m_game = game;
        currentZone = null;
        repo = MLBDataLayer.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TicketDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stadiumimg = new BitMapItem();
        stadiumimg.m_imageName = "seat_MIL.jpg";
        stadiumimg.m_fullImageURL = "http://www.jursairplanefactory.com/baseballimg/seating/" + stadiumimg.m_imageName;
        stadiumimg.m_localFileSubFolder = "/images/venue";


        WebFetchImageTask imageTask = new WebFetchImageTask(getContext());
        imageTask.m_image = binding.dialogTicketFieldImage;
        imageTask.execute(stadiumimg);

        ArrayAdapter<VenueZone> zoneArray = new ArrayAdapter<VenueZone>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        zoneArray.addAll(m_game.venue.m_zones);
        binding.dialogTicketZoneNameSpinner.setAdapter(zoneArray);

        binding.dialogTicketZoneBoxesSpinner.setVisibility(View.GONE);
        binding.dialogTicketPreview.setVisibility(View.GONE);
        binding.dialogTicketPurchase.setVisibility(View.GONE);
        binding.dialogTicketBack.setVisibility(View.GONE);
        binding.dialogTicketPurchaseDetails.setVisibility(View.GONE);

        currentbox = null;

        binding.dialogTicketZoneNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VenueZone zone = (VenueZone)parent.getItemAtPosition(position);
                currentZone = zone;
                binding.dialogTicketZoneBoxesSpinner.setVisibility(View.VISIBLE);;
                ArrayAdapter<VenueBox> boxArray = new ArrayAdapter<VenueBox>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                boxArray.addAll(zone.m_BoxesList);
                binding.dialogTicketZoneBoxesSpinner.setAdapter(boxArray);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.dialogTicketZoneBoxesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentbox = (VenueBox)parent.getItemAtPosition(position);
                binding.dialogTicketPreview.setVisibility(View.VISIBLE);
                String preview = "Zone: " + currentZone.m_ZoneName + "\nBox: " + currentbox.m_BoxName + "\nPrice: " + currentbox.m_BoxPrice + "$";
                binding.dialogTicketPreview.setText(preview);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.dialogTicketOrderTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dialogTicketOrderTicket.setVisibility(View.GONE);
                binding.dialogTicketBack.setVisibility(View.VISIBLE);
                binding.dialogTicketPurchase.setVisibility(View.VISIBLE);
                binding.dialogTicketPurchaseDetails.setVisibility(View.VISIBLE);

                // Step 1: if no session token received or known, get one from paypal.
                if(repo.m_PaymentSessionToken_Paypal == "" && repo.m_VerifyIfOnline)
                    repo.Get_PaymentSessionToken();

            }
        });

        binding.dialogTicketBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dialogTicketOrderTicket.setVisibility(View.VISIBLE);
                binding.dialogTicketBack.setVisibility(View.GONE);
                binding.dialogTicketPurchase.setVisibility(View.GONE);
                binding.dialogTicketPurchaseDetails.setVisibility(View.GONE);
            }
        });

        binding.dialogTicketPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.dialogTicketPurchaseNameET.getText().toString() == ""){
                    Toast.makeText(getContext(), "Please fill in your name.", Toast.LENGTH_LONG).show();;
                    return;
                }
                if(binding.dialogTicketPurchaseEmailET.getText().toString() == ""){
                    Toast.makeText(getContext(), "Please fill in your email.", Toast.LENGTH_LONG).show();;
                    return;
                }
                startPurchaseProcess();
            }
        });
    }

    public void startPurchaseProcess(){
        // Step 2: create the purchase order for the ticket.
        if(repo.m_PaymentSessionToken_Paypal != ""){
            repo.m_tempTicketDuringPayment = new MLBTicket();
            repo.m_tempTicketDuringPayment.mlbGame = m_game;
            repo.m_tempTicketDuringPayment.ticketPrice = currentbox.m_BoxPrice;
            repo.m_tempTicketDuringPayment.venueBox = currentbox.m_BoxName;
            repo.m_tempTicketDuringPayment.venueZone = currentZone.m_ZoneName;
            repo.m_tempTicketDuringPayment.venueSeat = "1";
            createPaypalPurchaseOrder();
        }
        else{
            Toast.makeText(getContext(), "Retrieving paypal access token failed", Toast.LENGTH_LONG).show();;
        }

    }

    public void createPaypalPurchaseOrder(){
        //uitleg paypal transactie zie: https://lo-victoria.com/the-complete-guide-to-integrate-paypal-in-mobile-apps
        String order = "";

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + repo.m_PaymentSessionToken_Paypal);
        try {
            // build using https://developer.paypal.com/docs/api/orders/v2/#definition-purchase_unit    rest api doc
            JSONObject orderJSONObject = new JSONObject()
                    .put("intent", "CAPTURE") // this tells paypall we are submitting a new order in the transaction
                    .put("purchase_units", new JSONArray()
                            .put(createPurchasedItems()
                            )
                    )
                    .put("payee", new JSONObject()
                            .put("email_address","rroeland03@gmail.com")
                            .put("merchant_id","MLBbaseballTicketing")
                    )
                    .put("application_context", new JSONObject()
                            .put("brand_name","MLBbaseballTicketing")
                            .put("return_url","mlbbaseball://www.mlbbaseballrobinroeland.com/gelukt")
                            .put("cancel_url","mlbbaseball://www.mlbbaseballrobinroeland.com/mislukt")
                    );
            order = orderJSONObject.toString();
            Log.d("creatPurchaseOrder", order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity entity = new StringEntity(order, "utf-8");
        String uriToPaypal = "https://api-m.sandbox.paypal.com/";
        client.post(this.getActivity(), uriToPaypal+"/v2/checkout/orders", entity, "application/json",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
                Toast.makeText( getContext(), response,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // reading of the result from paypal checkout orders, this returns a list of url that can be called with a command
                // the command we need to call is "approve". The command we call is in the href link
                try {
                    JSONArray links = new JSONObject(response).getJSONArray("links");

                    //iterate the array to get the approval link and call href link in a browser
                    for (int i = 0; i < links.length(); ++i) {
                        JSONObject jsonobj = links.getJSONObject(i);
                        String rel = jsonobj.getString("rel");
                        String hreflink = jsonobj.getString("href"); //de href is de link die je moet oproepen om die order te bevestigen

                        if (rel.equals("approve")){
                            dismiss(); //close the dialog

                            //redirect this 'approve'  (from "href") in a customtabsintent web browser, see gradle dependencies
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(getActivity(), Uri.parse(hreflink));

                            //Ga naar de HREF link gegeven door Paypal in de response met rel = approve
                            //Zie voorbeeld stap 5 in stappenplan
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    private JSONObject createPurchasedItems() throws JSONException {
        String paymentAmount = String.format("%.02f",currentbox.m_BoxPrice);
        String item_total = String.format("%.02f",currentbox.m_BoxPrice);
        Log.d("Tag", paymentAmount);
        DecimalFormat df = new DecimalFormat("0.00");
        JSONObject json = new JSONObject();
        json.put("amount", new JSONObject()
                .put("currency_code", "EUR")
                .put("value", paymentAmount)
                .put("breakdown", new JSONObject()
                        .put("item_total", new JSONObject()
                                .put("currency_code", "EUR")
                                .put("value", item_total)
                        )
                )
        );
        // create order items
        JSONArray itemsArray = new JSONArray();
        MLBTeamInfo homeTeamInfo = m_game.getHomeTeam();
        Team homeTeam = repo.getTeamWithMLBID(homeTeamInfo.id);
        MLBTeamInfo awayTeamInfo = m_game.getOpponent(homeTeam);
        Team awayTeam = repo.getTeamWithMLBID(awayTeamInfo.id);

        String MLBGameTxt = homeTeam.name_display_short + " vs " + awayTeam.name_display_short + " on " + m_game.gameDate;
        JSONObject itemObject = new JSONObject()
                .put("name", MLBGameTxt)
                .put("unit_amount", new JSONObject()
                        .put("currency_code", "EUR")
                        .put("value", String.format("%.02f", currentbox.m_BoxPrice))
                )
                .put("quantity", "1");
        itemsArray.put(itemObject);
        json.put("items", itemsArray);
        return json;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
