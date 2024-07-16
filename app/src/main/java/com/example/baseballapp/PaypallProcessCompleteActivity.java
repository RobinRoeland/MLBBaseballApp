package com.example.baseballapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.baseballapp.classes.MLB.MLBTicket;
import com.example.baseballapp.data.MLBDataLayer;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import com.example.baseballapp.databinding.ActivityPaypallProcessCompleteBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaypallProcessCompleteActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPaypallProcessCompleteBinding binding;
    private String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MLBTicket ticket = MLBDataLayer.getInstance().m_tempTicketDuringPayment;
        super.onCreate(savedInstanceState);

        binding = ActivityPaypallProcessCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //get the orderID from the query parameter
        Uri redirectUri = getIntent().getData();
        List<String> segmentsInUrl = redirectUri.getPathSegments();
        //hier kan je succes of failure halen uit de segmenstInURL
        orderID = redirectUri.getQueryParameter("token");
        String payerID = redirectUri.getQueryParameter("PayerID");

        binding.relativeLayoutSuccess.setVisibility(View.GONE);
        binding.relativeLayoutFail.setVisibility(View.GONE);

        String confirmDetails = String.format("You're about to pay %.02f€", ticket.ticketPrice) + "\n" +
                "Your seat will be: " + ticket.venueSeat + " in box " + ticket.venueBox + "(" + ticket.venueZone + ")";
        binding.relativeLayoutConfirmDetails.setText(confirmDetails);

        //Stap 1: Annuleren of bevestigen order
        binding.relativeLayoutConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureOrder(orderID);
            }
        });

        binding.relativeLayoutCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToOrderZonderConfirm();
            }
        });

        //Stap 2: terugspringen naar activity na betaling of annuleren
        binding.relativeLayoutJumpToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaypallProcessCompleteActivity.this, MainActivity.class);
                intent.putExtra("JumpToFragment","TicketSuccess");
                intent.putExtra("AfgeslotenOrderId",orderID);
                //intent.putExtra("AfgeslotenPaymentId",mandje.mPayPalPaymentId);

                //progressbar.setVisibility(View.INVISIBLE);

                startActivity(intent);
            }
        });

        binding.relativeLayoutBackToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToOrderZonderConfirm();
            }
        });

    }

    void captureOrder(String orderID){
        //get the accessToken from MainActivity
        //progressbar.setVisibility(View.VISIBLE);

        String accessToken = MLBDataLayer.getInstance().m_PaymentSessionToken_Paypal;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + accessToken);

        client.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderID+"/capture", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // eerst het resultaat van call verwerken om paymentid op te halen
                String paymentId = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String orderId = jsonResponse.getString("id"); // This is the order ID
                    JSONArray purchaseUnits = jsonResponse.getJSONArray("purchase_units");
                    if (purchaseUnits.length() > 0) {
                        JSONObject purchaseUnit = purchaseUnits.getJSONObject(0);
                        JSONArray payments = purchaseUnit.getJSONObject("payments").getJSONArray("captures");
                        if (payments.length() > 0) {
                            JSONObject payment = payments.getJSONObject(0);
                            paymentId = payment.getString("id"); // dit is de payment id
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               /* Date currentDate = new Date();
                mandje.mPayedOnDate = new Timestamp(currentDate.getTime());
                repo.mWinkelMandje.setValue(mandje);

                // order opslaan in db
                Task_SendOrderToDB taak = new Task_SendOrderToDB();
                taak.execute(mandje);
*/
                // Success !!!
                binding.relativeLayoutSuccess.setVisibility(View.VISIBLE);
                binding.relativeLayoutConfirm.setVisibility(View.GONE);

                MLBDataLayer.getInstance().m_PaymentSessionToken_Paypal = "";
            }
        });
    }
    public void returnToOrderZonderConfirm() {
        Intent intent = new Intent(PaypallProcessCompleteActivity.this, MainActivity.class);
        intent.putExtra("JumpToFragment","AnnulerenTicket");
        startActivity(intent);
    }
}